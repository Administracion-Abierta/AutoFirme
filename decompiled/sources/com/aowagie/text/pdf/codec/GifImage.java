package com.aowagie.text.pdf.codec;

import com.aowagie.text.ExceptionConverter;
import com.aowagie.text.Image;
import com.aowagie.text.ImgRaw;
import com.aowagie.text.pdf.PdfArray;
import com.aowagie.text.pdf.PdfDictionary;
import com.aowagie.text.pdf.PdfName;
import com.aowagie.text.pdf.PdfNumber;
import com.aowagie.text.pdf.PdfString;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/codec/GifImage.class */
public class GifImage {
    private DataInputStream in;
    private int width;
    private int height;
    private boolean gctFlag;
    private boolean lctFlag;
    private boolean interlace;
    private int ix;
    private int iy;
    private int iw;
    private int ih;
    private final byte[] block;
    private int blockSize;
    private int dispose;
    private boolean transparency;
    private int transIndex;
    private static final int MaxStackSize = 4096;
    private short[] prefix;
    private byte[] suffix;
    private byte[] pixelStack;
    private byte[] m_out;
    private int m_bpc;
    private int m_gbpc;
    private byte[] m_global_table;
    private byte[] m_curr_table;
    private int m_line_stride;
    private byte[] fromData;
    private URL fromUrl;
    private final ArrayList frames;

    public GifImage(URL url) throws IOException {
        this.block = new byte[256];
        this.blockSize = 0;
        this.dispose = 0;
        this.transparency = false;
        this.frames = new ArrayList();
        this.fromUrl = url;
        InputStream is = null;
        try {
            InputStream is2 = url.openStream();
            process(is2);
            if (is2 != null) {
            }
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public GifImage(byte[] data) throws IOException {
        this.block = new byte[256];
        this.blockSize = 0;
        this.dispose = 0;
        this.transparency = false;
        this.frames = new ArrayList();
        this.fromData = data;
        InputStream is = null;
        try {
            InputStream is2 = new ByteArrayInputStream(data);
            process(is2);
            if (is2 != null) {
            }
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public int getFrameCount() {
        return this.frames.size();
    }

    public Image getImage(int frame) {
        GifFrame gf = (GifFrame) this.frames.get(frame - 1);
        return gf.image;
    }

    public int[] getLogicalScreen() {
        return new int[]{this.width, this.height};
    }

    private void process(InputStream is) throws IOException {
        this.in = new DataInputStream(new BufferedInputStream(is));
        readHeader();
        readContents();
        if (this.frames.isEmpty()) {
            throw new IOException("The file does not contain any valid image.");
        }
    }

    private void readHeader() throws IOException {
        String id = "";
        for (int i = 0; i < 6; i++) {
            id = id + ((char) this.in.read());
        }
        if (!id.startsWith("GIF8")) {
            throw new IOException("Gif signature nor found.");
        }
        readLSD();
        if (this.gctFlag) {
            this.m_global_table = readColorTable(this.m_gbpc);
        }
    }

    private void readLSD() throws IOException {
        this.width = readShort();
        this.height = readShort();
        int packed = this.in.read();
        this.gctFlag = (packed & 128) != 0;
        this.m_gbpc = (packed & 7) + 1;
    }

    private int readShort() throws IOException {
        return this.in.read() | (this.in.read() << 8);
    }

    private int readBlock() throws IOException {
        this.blockSize = this.in.read();
        if (this.blockSize <= 0) {
            this.blockSize = 0;
            return 0;
        }
        for (int k = 0; k < this.blockSize; k++) {
            int v = this.in.read();
            if (v < 0) {
                int i = k;
                this.blockSize = i;
                return i;
            }
            this.block[k] = (byte) v;
        }
        return this.blockSize;
    }

    private byte[] readColorTable(int bpc) throws IOException {
        int ncolors = 1 << bpc;
        int nbytes = 3 * ncolors;
        byte[] table = new byte[(1 << newBpc(bpc)) * 3];
        this.in.readFully(table, 0, nbytes);
        return table;
    }

    private static int newBpc(int bpc) {
        switch (bpc) {
            case 1:
            case 2:
            case 4:
                return bpc;
            case 3:
                return 4;
            default:
                return 8;
        }
    }

    private void readContents() throws IOException {
        boolean done = false;
        while (!done) {
            int code = this.in.read();
            switch (code) {
                case 33:
                    int code2 = this.in.read();
                    switch (code2) {
                        case 249:
                            readGraphicControlExt();
                            break;
                        case 255:
                            readBlock();
                            skip();
                            break;
                        default:
                            skip();
                            break;
                    }
                    break;
                case 44:
                    readImage();
                    break;
                default:
                    done = true;
                    break;
            }
        }
    }

    private void readImage() throws IOException {
        this.ix = readShort();
        this.iy = readShort();
        this.iw = readShort();
        this.ih = readShort();
        int packed = this.in.read();
        this.lctFlag = (packed & 128) != 0;
        this.interlace = (packed & 64) != 0;
        this.m_bpc = newBpc(this.m_gbpc);
        if (this.lctFlag) {
            this.m_curr_table = readColorTable((packed & 7) + 1);
            this.m_bpc = newBpc((packed & 7) + 1);
        } else {
            this.m_curr_table = this.m_global_table;
        }
        if (this.transparency && this.transIndex >= this.m_curr_table.length / 3) {
            this.transparency = false;
        }
        if (this.transparency && this.m_bpc == 1) {
            byte[] tp = new byte[12];
            System.arraycopy(this.m_curr_table, 0, tp, 0, 6);
            this.m_curr_table = tp;
            this.m_bpc = 2;
        }
        boolean skipZero = decodeImageData();
        if (!skipZero) {
            skip();
        }
        try {
            Image img = new ImgRaw(this.iw, this.ih, 1, this.m_bpc, this.m_out);
            PdfArray colorspace = new PdfArray();
            colorspace.add(PdfName.INDEXED);
            colorspace.add(PdfName.DEVICERGB);
            int len = this.m_curr_table.length;
            colorspace.add(new PdfNumber((len / 3) - 1));
            colorspace.add(new PdfString(this.m_curr_table));
            PdfDictionary ad = new PdfDictionary();
            ad.put(PdfName.COLORSPACE, colorspace);
            img.setAdditional(ad);
            if (this.transparency) {
                img.setTransparency(new int[]{this.transIndex, this.transIndex});
            }
            img.setOriginalType(3);
            img.setOriginalData(this.fromData);
            img.setUrl(this.fromUrl);
            GifFrame gf = new GifFrame();
            gf.image = img;
            this.frames.add(gf);
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    private boolean decodeImageData() throws IOException {
        int npix = this.iw * this.ih;
        boolean skipZero = false;
        if (this.prefix == null) {
            this.prefix = new short[4096];
        }
        if (this.suffix == null) {
            this.suffix = new byte[4096];
        }
        if (this.pixelStack == null) {
            this.pixelStack = new byte[4097];
        }
        this.m_line_stride = ((this.iw * this.m_bpc) + 7) / 8;
        this.m_out = new byte[this.m_line_stride * this.ih];
        int pass = 1;
        int inc = this.interlace ? 8 : 1;
        int line = 0;
        int xpos = 0;
        int data_size = this.in.read();
        int clear = 1 << data_size;
        int end_of_information = clear + 1;
        int available = clear + 2;
        int old_code = -1;
        int code_size = data_size + 1;
        int code_mask = (1 << code_size) - 1;
        for (int code = 0; code < clear; code++) {
            this.prefix[code] = 0;
            this.suffix[code] = (byte) code;
        }
        int bi = 0;
        int top = 0;
        int first = 0;
        int count = 0;
        int bits = 0;
        int datum = 0;
        int i = 0;
        while (i < npix) {
            if (top == 0) {
                if (bits < code_size) {
                    if (count == 0) {
                        count = readBlock();
                        if (count <= 0) {
                            skipZero = true;
                            return skipZero;
                        }
                        bi = 0;
                    }
                    datum += (this.block[bi] & 255) << bits;
                    bits += 8;
                    bi++;
                    count--;
                } else {
                    int code2 = datum & code_mask;
                    datum >>= code_size;
                    bits -= code_size;
                    if (code2 <= available && code2 != end_of_information) {
                        if (code2 == clear) {
                            code_size = data_size + 1;
                            code_mask = (1 << code_size) - 1;
                            available = clear + 2;
                            old_code = -1;
                        } else if (old_code == -1) {
                            int i2 = top;
                            top++;
                            this.pixelStack[i2] = this.suffix[code2];
                            old_code = code2;
                            first = code2;
                        } else {
                            if (code2 == available) {
                                int i3 = top;
                                top++;
                                this.pixelStack[i3] = (byte) first;
                                code2 = old_code;
                            }
                            while (code2 > clear) {
                                int i4 = top;
                                top++;
                                this.pixelStack[i4] = this.suffix[code2];
                                code2 = this.prefix[code2];
                            }
                            first = this.suffix[code2] & 255;
                            if (available < 4096) {
                                int i5 = top;
                                top++;
                                this.pixelStack[i5] = (byte) first;
                                this.prefix[available] = (short) old_code;
                                this.suffix[available] = (byte) first;
                                available++;
                                if ((available & code_mask) == 0 && available < 4096) {
                                    code_size++;
                                    code_mask += available;
                                }
                                old_code = code2;
                            }
                        }
                    }
                    return skipZero;
                }
            }
            top--;
            i++;
            setPixel(xpos, line, this.pixelStack[top]);
            xpos++;
            if (xpos >= this.iw) {
                xpos = 0;
                line += inc;
                if (line >= this.ih) {
                    if (this.interlace) {
                        do {
                            pass++;
                            switch (pass) {
                                case 2:
                                    line = 4;
                                    break;
                                case 3:
                                    line = 2;
                                    inc = 4;
                                    break;
                                case 4:
                                    line = 1;
                                    inc = 2;
                                    break;
                                default:
                                    line = this.ih - 1;
                                    inc = 0;
                                    break;
                            }
                        } while (line >= this.ih);
                    } else {
                        line = this.ih - 1;
                        inc = 0;
                    }
                }
            }
        }
        return skipZero;
    }

    private void setPixel(int x, int y, int v) {
        if (this.m_bpc == 8) {
            this.m_out[x + (this.iw * y)] = (byte) v;
        } else {
            int pos = (this.m_line_stride * y) + (x / (8 / this.m_bpc));
            int vout = v << ((8 - (this.m_bpc * (x % (8 / this.m_bpc)))) - this.m_bpc);
            byte[] bArr = this.m_out;
            bArr[pos] = (byte) (bArr[pos] | vout);
        }
    }

    private void readGraphicControlExt() throws IOException {
        this.in.read();
        int packed = this.in.read();
        this.dispose = (packed & 28) >> 2;
        if (this.dispose == 0) {
            this.dispose = 1;
        }
        this.transparency = (packed & 1) != 0;
        this.transIndex = this.in.read();
        this.in.read();
    }

    private void skip() throws IOException {
        do {
            readBlock();
        } while (this.blockSize > 0);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/codec/GifImage$GifFrame.class */
    private static class GifFrame {
        private Image image;

        private GifFrame() {
        }
    }
}
