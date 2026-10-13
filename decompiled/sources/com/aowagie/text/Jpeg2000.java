package com.aowagie.text;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/Jpeg2000.class */
public class Jpeg2000 extends Image {
    private static final int JP2_JP = 1783636000;
    private static final int JP2_IHDR = 1768449138;
    private static final int JP2_FTYP = 1718909296;
    private static final int JP2_JP2H = 1785737832;
    private static final int JP2_JP2C = 1785737827;
    private InputStream inp;
    private int boxLength;
    private int boxType;

    Jpeg2000(Image image) {
        super(image);
    }

    Jpeg2000(URL url) throws BadElementException, IOException {
        super(url);
        processParameters();
    }

    Jpeg2000(byte[] img) throws BadElementException, IOException {
        super((URL) null);
        this.rawData = img;
        this.originalData = img;
        processParameters();
    }

    private int cio_read(int n) throws IOException {
        int v = 0;
        for (int i = n - 1; i >= 0; i--) {
            v += this.inp.read() << (i << 3);
        }
        return v;
    }

    private void jp2_read_boxhdr() throws IOException {
        this.boxLength = cio_read(4);
        this.boxType = cio_read(4);
        if (this.boxLength == 1) {
            if (cio_read(4) != 0) {
                throw new IOException("Cannot handle box sizes higher than 2^32");
            }
            this.boxLength = cio_read(4);
            if (this.boxLength == 0) {
                throw new IOException("Unsupported box size == 0");
            }
            return;
        }
        if (this.boxLength == 0) {
            throw new IOException("Unsupported box size == 0");
        }
    }

    private void processParameters() throws IOException {
        this.type = 33;
        this.originalType = 8;
        this.inp = null;
        try {
            if (this.rawData == null) {
                this.inp = this.url.openStream();
                this.url.toString();
            } else {
                this.inp = new ByteArrayInputStream(this.rawData);
            }
            this.boxLength = cio_read(4);
            if (this.boxLength == 12) {
                this.boxType = cio_read(4);
                if (JP2_JP != this.boxType) {
                    throw new IOException("Expected JP Marker");
                }
                if (218793738 != cio_read(4)) {
                    throw new IOException("Error with JP Marker");
                }
                jp2_read_boxhdr();
                if (JP2_FTYP != this.boxType) {
                    throw new IOException("Expected FTYP Marker");
                }
                Utilities.skip(this.inp, this.boxLength - 8);
                jp2_read_boxhdr();
                do {
                    if (JP2_JP2H != this.boxType) {
                        if (this.boxType == JP2_JP2C) {
                            throw new IOException("Expected JP2H Marker");
                        }
                        Utilities.skip(this.inp, this.boxLength - 8);
                        jp2_read_boxhdr();
                    }
                } while (JP2_JP2H != this.boxType);
                jp2_read_boxhdr();
                if (JP2_IHDR != this.boxType) {
                    throw new IOException("Expected IHDR Marker");
                }
                this.scaledHeight = cio_read(4);
                setTop(this.scaledHeight);
                this.scaledWidth = cio_read(4);
                setRight(this.scaledWidth);
                this.bpc = -1;
            } else if (this.boxLength == -11534511) {
                Utilities.skip(this.inp, 4);
                int x1 = cio_read(4);
                int y1 = cio_read(4);
                int x0 = cio_read(4);
                int y0 = cio_read(4);
                Utilities.skip(this.inp, 16);
                this.colorspace = cio_read(2);
                this.bpc = 8;
                this.scaledHeight = y1 - y0;
                setTop(this.scaledHeight);
                this.scaledWidth = x1 - x0;
                setRight(this.scaledWidth);
            } else {
                throw new IOException("Not a valid Jpeg2000 file");
            }
            if (this.inp != null) {
                try {
                    this.inp.close();
                } catch (Exception e) {
                }
                this.inp = null;
            }
            this.plainWidth = getWidth();
            this.plainHeight = getHeight();
        } catch (Throwable th) {
            if (this.inp != null) {
                try {
                    this.inp.close();
                } catch (Exception e2) {
                }
                this.inp = null;
            }
            throw th;
        }
    }
}
