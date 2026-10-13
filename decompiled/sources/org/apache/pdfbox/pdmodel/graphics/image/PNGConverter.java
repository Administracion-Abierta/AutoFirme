package org.apache.pdfbox.pdmodel.graphics.image;

import java.awt.color.ICC_Profile;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.imageio.stream.MemoryCacheImageInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.filter.Filter;
import org.apache.pdfbox.filter.FilterFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceGray;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.graphics.color.PDICCBased;
import org.apache.pdfbox.pdmodel.graphics.color.PDIndexed;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/image/PNGConverter.class */
final class PNGConverter {
    private static final int CHUNK_IHDR = 1229472850;
    private static final int CHUNK_IDAT = 1229209940;
    private static final int CHUNK_PLTE = 1347179589;
    private static final int CHUNK_IEND = 1229278788;
    private static final int CHUNK_TRNS = 1951551059;
    private static final int CHUNK_CHRM = 1665684045;
    private static final int CHUNK_GAMA = 1732332865;
    private static final int CHUNK_ICCP = 1766015824;
    private static final int CHUNK_SBIT = 1933723988;
    private static final int CHUNK_SRGB = 1934772034;
    private static final int CHUNK_TEXT = 1950701684;
    private static final int CHUNK_ZTXT = 2052348020;
    private static final int CHUNK_ITXT = 1767135348;
    private static final int CHUNK_KBKG = 1799506759;
    private static final int CHUNK_HIST = 1749635924;
    private static final int CHUNK_PHYS = 1883789683;
    private static final int CHUNK_SPLT = 1934642260;
    private static final int CHUNK_TIME = 1950960965;
    private static final Log LOG = LogFactory.getLog(PNGConverter.class);
    private static final int[] CRC_TABLE = new int[256];

    static {
        makeCrcTable();
    }

    private PNGConverter() {
    }

    static PDImageXObject convertPNGImage(PDDocument doc, byte[] imageData) throws IOException {
        PNGConverterState state = parsePNGChunks(imageData);
        if (!checkConverterState(state)) {
            return null;
        }
        return convertPng(doc, state);
    }

    private static PDImageXObject convertPng(PDDocument doc, PNGConverterState state) throws IOException {
        Chunk ihdr = state.IHDR;
        int ihdrStart = ihdr.start;
        int width = readInt(ihdr.bytes, ihdrStart);
        int height = readInt(ihdr.bytes, ihdrStart + 4);
        int bitDepth = ihdr.bytes[ihdrStart + 8] & 255;
        int colorType = ihdr.bytes[ihdrStart + 9] & 255;
        int compressionMethod = ihdr.bytes[ihdrStart + 10] & 255;
        int filterMethod = ihdr.bytes[ihdrStart + 11] & 255;
        int interlaceMethod = ihdr.bytes[ihdrStart + 12] & 255;
        if (bitDepth != 1 && bitDepth != 2 && bitDepth != 4 && bitDepth != 8 && bitDepth != 16) {
            LOG.error(String.format("Invalid bit depth %d.", Integer.valueOf(bitDepth)));
            return null;
        }
        if (width <= 0 || height <= 0) {
            LOG.error(String.format("Invalid image size %d x %d", Integer.valueOf(width), Integer.valueOf(height)));
            return null;
        }
        if (compressionMethod != 0) {
            LOG.error(String.format("Unknown PNG compression method %d.", Integer.valueOf(compressionMethod)));
            return null;
        }
        if (filterMethod != 0) {
            LOG.error(String.format("Unknown PNG filtering method %d.", Integer.valueOf(compressionMethod)));
            return null;
        }
        if (interlaceMethod != 0) {
            LOG.debug(String.format("Can't handle interlace method %d.", Integer.valueOf(interlaceMethod)));
            return null;
        }
        state.width = width;
        state.height = height;
        state.bitsPerComponent = bitDepth;
        switch (colorType) {
            case 0:
                LOG.debug("Can't handle grayscale yet.");
                return null;
            case 1:
            case 5:
            default:
                LOG.error("Unknown PNG color type " + colorType);
                return null;
            case 2:
                if (state.tRNS != null) {
                    LOG.debug("Can't handle images with transparent colors.");
                    return null;
                }
                return buildImageObject(doc, state);
            case 3:
                return buildIndexImage(doc, state);
            case 4:
                LOG.debug("Can't handle grayscale with alpha, would need to separate alpha from image data");
                return null;
            case 6:
                LOG.debug("Can't handle truecolor with alpha, would need to separate alpha from image data");
                return null;
        }
    }

    private static PDImageXObject buildIndexImage(PDDocument doc, PNGConverterState state) throws IOException {
        Chunk plte = state.PLTE;
        if (plte == null) {
            LOG.error("Indexed image without PLTE chunk.");
            return null;
        }
        if (plte.length % 3 != 0) {
            LOG.error("PLTE table corrupted, last (r,g,b) tuple is not complete.");
            return null;
        }
        if (state.bitsPerComponent > 8) {
            LOG.debug(String.format("Can only convert indexed images with bit depth <= 8, not %d.", Integer.valueOf(state.bitsPerComponent)));
            return null;
        }
        PDImageXObject image = buildImageObject(doc, state);
        if (image == null) {
            return null;
        }
        int highVal = (plte.length / 3) - 1;
        if (highVal > 255) {
            LOG.error(String.format("Too much colors in PLTE, only 256 allowed, found %d colors.", Integer.valueOf(highVal + 1)));
            return null;
        }
        setupIndexedColorSpace(doc, plte, image, highVal);
        if (state.tRNS != null) {
            image.getCOSObject().setItem(COSName.SMASK, buildTransparencyMaskFromIndexedData(doc, image, state));
        }
        return image;
    }

    private static PDImageXObject buildTransparencyMaskFromIndexedData(PDDocument doc, PDImageXObject image, PNGConverterState state) throws IOException {
        Filter flateDecode = FilterFactory.INSTANCE.getFilter(COSName.FLATE_DECODE);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        COSDictionary decodeParams = buildDecodeParams(state, PDDeviceGray.INSTANCE);
        COSDictionary imageDict = new COSDictionary();
        imageDict.setItem(COSName.FILTER, (COSBase) COSName.FLATE_DECODE);
        imageDict.setItem(COSName.DECODE_PARMS, (COSBase) decodeParams);
        flateDecode.decode(getIDATInputStream(state), outputStream, imageDict, 0);
        int length = image.getWidth() * image.getHeight();
        byte[] bytes = new byte[length];
        byte[] transparencyTable = state.tRNS.getData();
        byte[] decodedIDAT = outputStream.toByteArray();
        MemoryCacheImageInputStream memoryCacheImageInputStream = new MemoryCacheImageInputStream(new ByteArrayInputStream(decodedIDAT));
        try {
            int bitsPerComponent = state.bitsPerComponent;
            int w = 0;
            int neededBits = bitsPerComponent * state.width;
            int bitPadding = neededBits % 8;
            for (int i = 0; i < bytes.length; i++) {
                int idx = (int) memoryCacheImageInputStream.readBits(bitsPerComponent);
                if (idx < transparencyTable.length) {
                    bytes[i] = transparencyTable[idx];
                } else {
                    bytes[i] = -1;
                }
                w++;
                if (w == state.width) {
                    w = 0;
                    memoryCacheImageInputStream.readBits(bitPadding);
                }
            }
            return LosslessFactory.prepareImageXObject(doc, bytes, image.getWidth(), image.getHeight(), 8, PDDeviceGray.INSTANCE);
        } finally {
            memoryCacheImageInputStream.close();
        }
    }

    private static void setupIndexedColorSpace(PDDocument doc, Chunk lookupTable, PDImageXObject image, int highVal) throws IOException {
        COSArray indexedArray = new COSArray();
        indexedArray.add(COSName.INDEXED);
        indexedArray.add(image.getColorSpace());
        ((COSDictionary) image.getCOSObject().getItem(COSName.DECODE_PARMS)).setItem(COSName.COLORS, (COSBase) COSInteger.ONE);
        indexedArray.add(COSInteger.get(highVal));
        PDStream colorTable = new PDStream(doc);
        OutputStream colorTableStream = colorTable.createOutputStream(COSName.FLATE_DECODE);
        try {
            colorTableStream.write(lookupTable.bytes, lookupTable.start, lookupTable.length);
            colorTableStream.close();
            indexedArray.add(colorTable);
            PDIndexed indexed = new PDIndexed(indexedArray);
            image.setColorSpace(indexed);
        } catch (Throwable th) {
            colorTableStream.close();
            throw th;
        }
    }

    private static PDImageXObject buildImageObject(PDDocument document, PNGConverterState state) throws IOException {
        InputStream encodedByteStream = getIDATInputStream(state);
        PDColorSpace colorSpace = PDDeviceRGB.INSTANCE;
        PDImageXObject imageXObject = new PDImageXObject(document, encodedByteStream, COSName.FLATE_DECODE, state.width, state.height, state.bitsPerComponent, colorSpace);
        COSDictionary decodeParams = buildDecodeParams(state, colorSpace);
        imageXObject.getCOSObject().setItem(COSName.DECODE_PARMS, (COSBase) decodeParams);
        boolean hasICCColorProfile = (state.sRGB == null && state.iCCP == null) ? false : true;
        if (state.gAMA != null && !hasICCColorProfile) {
            if (state.gAMA.length != 4) {
                LOG.error("Invalid gAMA chunk length " + state.gAMA.length);
                return null;
            }
            float gamma = readPNGFloat(state.gAMA.bytes, state.gAMA.start);
            if (Math.abs(gamma - 0.45454544f) > 1.0E-5d) {
                LOG.debug(String.format("We can't handle gamma of %f yet.", Float.valueOf(gamma)));
                return null;
            }
        }
        if (state.sRGB != null) {
            if (state.sRGB.length != 1) {
                LOG.error(String.format("sRGB chunk has an invalid length of %d", Integer.valueOf(state.sRGB.length)));
                return null;
            }
            COSName value = mapPNGRenderIntent(state.sRGB.bytes[state.sRGB.start]);
            imageXObject.getCOSObject().setItem(COSName.INTENT, (COSBase) value);
        }
        if (state.cHRM != null && !hasICCColorProfile) {
            if (state.cHRM.length != 32) {
                LOG.error("Invalid cHRM chunk length " + state.cHRM.length);
                return null;
            }
            LOG.debug("We can not handle cHRM chunks yet.");
            return null;
        }
        if (state.iCCP != null || state.sRGB != null) {
            COSStream cosStream = createCOSStreamwithIccProfile(document, colorSpace, state);
            if (cosStream == null) {
                return null;
            }
            COSArray array = new COSArray();
            array.add((COSBase) COSName.ICCBASED);
            array.add((COSBase) cosStream);
            PDICCBased profile = PDICCBased.create(array, (PDResources) null);
            imageXObject.setColorSpace(profile);
        }
        return imageXObject;
    }

    private static COSStream createCOSStreamwithIccProfile(PDDocument document, PDColorSpace colorSpace, PNGConverterState state) throws IOException {
        COSStream cosStream = document.getDocument().createCOSStream();
        cosStream.setInt(COSName.N, colorSpace.getNumberOfComponents());
        cosStream.setItem(COSName.ALTERNATE, (COSBase) (colorSpace.getNumberOfComponents() == 1 ? COSName.DEVICEGRAY : COSName.DEVICERGB));
        cosStream.setItem(COSName.FILTER, (COSBase) COSName.FLATE_DECODE);
        if (state.iCCP != null) {
            int iccProfileDataStart = 0;
            while (iccProfileDataStart < 80 && iccProfileDataStart < state.iCCP.length && state.iCCP.bytes[state.iCCP.start + iccProfileDataStart] != 0) {
                iccProfileDataStart++;
            }
            int iccProfileDataStart2 = iccProfileDataStart + 1;
            if (iccProfileDataStart2 >= state.iCCP.length) {
                LOG.error("Invalid iCCP chunk, to few bytes");
                return null;
            }
            byte compressionMethod = state.iCCP.bytes[state.iCCP.start + iccProfileDataStart2];
            if (compressionMethod != 0) {
                LOG.error(String.format("iCCP chunk: invalid compression method %d", Byte.valueOf(compressionMethod)));
                return null;
            }
            int iccProfileDataStart3 = iccProfileDataStart2 + 1;
            OutputStream rawOutputStream = cosStream.createRawOutputStream();
            try {
                rawOutputStream.write(state.iCCP.bytes, state.iCCP.start + iccProfileDataStart3, state.iCCP.length - iccProfileDataStart3);
            } finally {
                rawOutputStream.close();
            }
        } else {
            ICC_Profile rgbProfile = ICC_Profile.getInstance(1000);
            OutputStream outputStream = cosStream.createOutputStream();
            try {
                outputStream.write(rgbProfile.getData());
            } finally {
                outputStream.close();
            }
        }
        return cosStream;
    }

    private static COSDictionary buildDecodeParams(PNGConverterState state, PDColorSpace colorSpace) {
        COSDictionary decodeParms = new COSDictionary();
        decodeParms.setItem(COSName.BITS_PER_COMPONENT, (COSBase) COSInteger.get(state.bitsPerComponent));
        decodeParms.setItem(COSName.PREDICTOR, (COSBase) COSInteger.get(15L));
        decodeParms.setItem(COSName.COLUMNS, (COSBase) COSInteger.get(state.width));
        decodeParms.setItem(COSName.COLORS, (COSBase) COSInteger.get(colorSpace.getNumberOfComponents()));
        return decodeParms;
    }

    private static InputStream getIDATInputStream(PNGConverterState state) {
        MultipleInputStream inputStream = new MultipleInputStream();
        for (Chunk idat : state.IDATs) {
            inputStream.inputStreams.add(new ByteArrayInputStream(idat.bytes, idat.start, idat.length));
        }
        return inputStream;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/image/PNGConverter$MultipleInputStream.class */
    private static class MultipleInputStream extends InputStream {
        List<InputStream> inputStreams;
        int currentStreamIdx;
        InputStream currentStream;

        private MultipleInputStream() {
            this.inputStreams = new ArrayList();
        }

        private boolean ensureStream() {
            if (this.currentStream == null) {
                if (this.currentStreamIdx >= this.inputStreams.size()) {
                    return false;
                }
                List<InputStream> list = this.inputStreams;
                int i = this.currentStreamIdx;
                this.currentStreamIdx = i + 1;
                this.currentStream = list.get(i);
                return true;
            }
            return true;
        }

        @Override // java.io.InputStream
        public int read() throws IOException {
            if (!ensureStream()) {
                return -1;
            }
            int ret = this.currentStream.read();
            if (ret == -1) {
                this.currentStream = null;
                return read();
            }
            return ret;
        }

        @Override // java.io.InputStream
        public int available() throws IOException {
            if (!ensureStream()) {
                return 0;
            }
            return 1;
        }

        @Override // java.io.InputStream
        public int read(byte[] b, int off, int len) throws IOException {
            if (!ensureStream()) {
                return -1;
            }
            int ret = this.currentStream.read(b, off, len);
            if (ret == -1) {
                this.currentStream = null;
                return read(b, off, len);
            }
            return ret;
        }
    }

    static COSName mapPNGRenderIntent(int renderIntent) {
        COSName value;
        switch (renderIntent) {
            case 0:
                value = COSName.PERCEPTUAL;
                break;
            case 1:
                value = COSName.RELATIVE_COLORIMETRIC;
                break;
            case 2:
                value = COSName.SATURATION;
                break;
            case 3:
                value = COSName.ABSOLUTE_COLORIMETRIC;
                break;
            default:
                value = null;
                break;
        }
        return value;
    }

    static boolean checkConverterState(PNGConverterState state) {
        if (state == null) {
            return false;
        }
        if (state.IHDR == null || !checkChunkSane(state.IHDR)) {
            LOG.error("Invalid IHDR chunk.");
            return false;
        }
        if (!checkChunkSane(state.PLTE)) {
            LOG.error("Invalid PLTE chunk.");
            return false;
        }
        if (!checkChunkSane(state.iCCP)) {
            LOG.error("Invalid iCCP chunk.");
            return false;
        }
        if (!checkChunkSane(state.tRNS)) {
            LOG.error("Invalid tRNS chunk.");
            return false;
        }
        if (!checkChunkSane(state.sRGB)) {
            LOG.error("Invalid sRGB chunk.");
            return false;
        }
        if (!checkChunkSane(state.cHRM)) {
            LOG.error("Invalid cHRM chunk.");
            return false;
        }
        if (!checkChunkSane(state.gAMA)) {
            LOG.error("Invalid gAMA chunk.");
            return false;
        }
        if (state.IDATs.isEmpty()) {
            LOG.error("No IDAT chunks.");
            return false;
        }
        for (Chunk idat : state.IDATs) {
            if (!checkChunkSane(idat)) {
                LOG.error("Invalid IDAT chunk.");
                return false;
            }
        }
        return true;
    }

    static boolean checkChunkSane(Chunk chunk) {
        if (chunk == null) {
            return true;
        }
        if (chunk.start + chunk.length > chunk.bytes.length || chunk.start < 4) {
            return false;
        }
        int ourCRC = crc(chunk.bytes, chunk.start - 4, chunk.length + 4);
        if (ourCRC != chunk.crc) {
            LOG.error(String.format("Invalid CRC %08X on chunk %08X, expected %08X.", Integer.valueOf(ourCRC), Integer.valueOf(chunk.chunkType), Integer.valueOf(chunk.crc)));
            return false;
        }
        return true;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/image/PNGConverter$Chunk.class */
    static final class Chunk {
        byte[] bytes;
        int chunkType;
        int crc;
        int start;
        int length;

        Chunk() {
        }

        byte[] getData() {
            return Arrays.copyOfRange(this.bytes, this.start, this.start + this.length);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/image/PNGConverter$PNGConverterState.class */
    static final class PNGConverterState {
        List<Chunk> IDATs = new ArrayList();
        Chunk IHDR;
        Chunk PLTE;
        Chunk iCCP;
        Chunk tRNS;
        Chunk sRGB;
        Chunk gAMA;
        Chunk cHRM;
        int width;
        int height;
        int bitsPerComponent;

        PNGConverterState() {
        }
    }

    private static int readInt(byte[] data, int offset) {
        int b1 = (data[offset] & 255) << 24;
        int b2 = (data[offset + 1] & 255) << 16;
        int b3 = (data[offset + 2] & 255) << 8;
        int b4 = data[offset + 3] & 255;
        return b1 | b2 | b3 | b4;
    }

    private static float readPNGFloat(byte[] bytes, int offset) {
        int v = readInt(bytes, offset);
        return v / 100000.0f;
    }

    private static PNGConverterState parsePNGChunks(byte[] imageData) {
        if (imageData.length < 20) {
            LOG.error("ByteArray way to small: " + imageData.length);
            return null;
        }
        PNGConverterState state = new PNGConverterState();
        int ptr = 8;
        int firstChunkType = readInt(imageData, 8 + 4);
        if (firstChunkType != CHUNK_IHDR) {
            LOG.error(String.format("First Chunktype was %08X, not IHDR", Integer.valueOf(firstChunkType)));
            return null;
        }
        while (ptr + 12 <= imageData.length) {
            int chunkLength = readInt(imageData, ptr);
            int chunkType = readInt(imageData, ptr + 4);
            int ptr2 = ptr + 8;
            if (ptr2 + chunkLength + 4 > imageData.length) {
                LOG.error("Not enough bytes. At offset " + ptr2 + " are " + chunkLength + " bytes expected. Overall length is " + imageData.length);
                return null;
            }
            Chunk chunk = new Chunk();
            chunk.chunkType = chunkType;
            chunk.bytes = imageData;
            chunk.start = ptr2;
            chunk.length = chunkLength;
            switch (chunkType) {
                case CHUNK_IDAT /* 1229209940 */:
                    state.IDATs.add(chunk);
                    break;
                case CHUNK_IEND /* 1229278788 */:
                    return state;
                case CHUNK_IHDR /* 1229472850 */:
                    if (state.IHDR != null) {
                        LOG.error("Two IHDR chunks? There is something wrong.");
                        return null;
                    }
                    state.IHDR = chunk;
                    break;
                    break;
                case CHUNK_PLTE /* 1347179589 */:
                    if (state.PLTE != null) {
                        LOG.error("Two PLTE chunks? There is something wrong.");
                        return null;
                    }
                    state.PLTE = chunk;
                    break;
                    break;
                case CHUNK_CHRM /* 1665684045 */:
                    state.cHRM = chunk;
                    break;
                case CHUNK_GAMA /* 1732332865 */:
                    state.gAMA = chunk;
                    break;
                case CHUNK_HIST /* 1749635924 */:
                case CHUNK_ITXT /* 1767135348 */:
                case CHUNK_KBKG /* 1799506759 */:
                case CHUNK_PHYS /* 1883789683 */:
                case CHUNK_SPLT /* 1934642260 */:
                case CHUNK_TEXT /* 1950701684 */:
                case CHUNK_TIME /* 1950960965 */:
                case CHUNK_ZTXT /* 2052348020 */:
                    break;
                case CHUNK_ICCP /* 1766015824 */:
                    state.iCCP = chunk;
                    break;
                case CHUNK_SBIT /* 1933723988 */:
                    LOG.debug("Can't convert PNGs with sBIT chunk.");
                    break;
                case CHUNK_SRGB /* 1934772034 */:
                    state.sRGB = chunk;
                    break;
                case CHUNK_TRNS /* 1951551059 */:
                    if (state.tRNS != null) {
                        LOG.error("Two tRNS chunks? There is something wrong.");
                        return null;
                    }
                    state.tRNS = chunk;
                    break;
                    break;
                default:
                    LOG.debug(String.format("Unknown chunk type %08X, skipping.", Integer.valueOf(chunkType)));
                    break;
            }
            int ptr3 = ptr2 + chunkLength;
            chunk.crc = readInt(imageData, ptr3);
            ptr = ptr3 + 4;
        }
        LOG.error("No IEND chunk found.");
        return null;
    }

    private static void makeCrcTable() {
        int i;
        for (int n = 0; n < 256; n++) {
            int c = n;
            for (int k = 0; k < 8; k++) {
                if ((c & 1) != 0) {
                    i = (-306674912) ^ (c >>> 1);
                } else {
                    i = c >>> 1;
                }
                c = i;
            }
            CRC_TABLE[n] = c;
        }
    }

    private static int updateCrc(byte[] buf, int offset, int len) {
        int c = -1;
        int end = offset + len;
        for (int n = offset; n < end; n++) {
            c = CRC_TABLE[(c ^ buf[n]) & 255] ^ (c >>> 8);
        }
        return c;
    }

    static int crc(byte[] buf, int offset, int len) {
        return updateCrc(buf, offset, len) ^ (-1);
    }
}
