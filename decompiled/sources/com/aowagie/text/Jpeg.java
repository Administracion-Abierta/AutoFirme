package com.aowagie.text;

import com.sun.jna.platform.win32.WinError;
import java.awt.color.ICC_Profile;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/Jpeg.class */
public class Jpeg extends Image {
    private static final int NOT_A_MARKER = -1;
    private static final int VALID_MARKER = 0;
    private static final int UNSUPPORTED_MARKER = 1;
    private static final int NOPARAM_MARKER = 2;
    private static final int M_APP0 = 224;
    private static final int M_APP2 = 226;
    private static final int M_APPE = 238;
    private byte[][] icc;
    private static final int[] VALID_MARKERS = {192, 193, 194};
    private static final int[] UNSUPPORTED_MARKERS = {195, 197, 198, 199, 200, 201, 202, 203, 205, WinError.ERROR_FILENAME_EXCED_RANGE, WinError.ERROR_RING2_STACK_IN_USE};
    private static final int[] NOPARAM_MARKERS = {WinError.ERROR_META_EXPANSION_TOO_LONG, WinError.ERROR_INVALID_SIGNAL_NUMBER, WinError.ERROR_THREAD_1_INACTIVE, 211, WinError.ERROR_LOCKED, 213, WinError.ERROR_TOO_MANY_MODULES, WinError.ERROR_NESTING_NOT_ALLOWED, WinError.ERROR_EXE_MACHINE_TYPE_MISMATCH, 1};
    private static final byte[] JFIF_ID = {74, 70, 73, 70, 0};

    Jpeg(Image image) {
        super(image);
    }

    Jpeg(URL url) throws BadElementException, IOException {
        super(url);
        processParameters();
    }

    public Jpeg(byte[] img) throws BadElementException, IOException {
        super((URL) null);
        this.rawData = img;
        this.originalData = img;
        processParameters();
    }

    private static final int getShort(InputStream is) throws IOException {
        return (is.read() << 8) + is.read();
    }

    private static final int marker(int marker) {
        for (int element : VALID_MARKERS) {
            if (marker == element) {
                return 0;
            }
        }
        for (int element2 : NOPARAM_MARKERS) {
            if (marker == element2) {
                return 2;
            }
        }
        for (int element3 : UNSUPPORTED_MARKERS) {
            if (marker == element3) {
                return 1;
            }
        }
        return -1;
    }

    private void processParameters() throws BadElementException, IOException {
        InputStream is;
        String errorID;
        this.type = 32;
        this.originalType = 1;
        InputStream is2 = null;
        try {
            if (this.rawData == null) {
                is = this.url.openStream();
                errorID = this.url.toString();
            } else {
                is = new ByteArrayInputStream(this.rawData);
                errorID = "Byte array";
            }
            if (is.read() != 255 || is.read() != 216) {
                throw new BadElementException(errorID + " is not a valid JPEG-file.");
            }
            boolean firstPass = true;
            while (true) {
                int v = is.read();
                if (v < 0) {
                    throw new IOException("Premature EOF while reading JPG.");
                }
                if (v == 255) {
                    int marker = is.read();
                    if (firstPass && marker == 224) {
                        firstPass = false;
                        int len = getShort(is);
                        if (len < 16) {
                            Utilities.skip(is, len - 2);
                        } else {
                            byte[] bcomp = new byte[JFIF_ID.length];
                            int r = is.read(bcomp);
                            if (r != bcomp.length) {
                                throw new BadElementException(errorID + " corrupted JFIF marker.");
                            }
                            boolean found = true;
                            for (int k = 0; k < bcomp.length; k++) {
                                if (bcomp[k] != JFIF_ID[k]) {
                                    found = false;
                                    break;
                                }
                            }
                            if (!found) {
                                Utilities.skip(is, (len - 2) - bcomp.length);
                            } else {
                                Utilities.skip(is, 2);
                                int units = is.read();
                                int dx = getShort(is);
                                int dy = getShort(is);
                                if (units == 1) {
                                    this.dpiX = dx;
                                    this.dpiY = dy;
                                } else if (units == 2) {
                                    this.dpiX = (int) ((dx * 2.54f) + 0.5f);
                                    this.dpiY = (int) ((dy * 2.54f) + 0.5f);
                                }
                                Utilities.skip(is, ((len - 2) - bcomp.length) - 7);
                            }
                        }
                    } else if (marker == M_APPE) {
                        int len2 = getShort(is) - 2;
                        byte[] byteappe = new byte[len2];
                        for (int k2 = 0; k2 < len2; k2++) {
                            byteappe[k2] = (byte) is.read();
                        }
                        if (byteappe.length >= 12) {
                            String appe = new String(byteappe, 0, 5, "ISO-8859-1");
                            if (appe.equals("Adobe")) {
                                this.invert = true;
                            }
                        }
                    } else if (marker == 226) {
                        int len3 = getShort(is) - 2;
                        byte[] byteapp2 = new byte[len3];
                        for (int k3 = 0; k3 < len3; k3++) {
                            byteapp2[k3] = (byte) is.read();
                        }
                        if (byteapp2.length >= 14) {
                            String app2 = new String(byteapp2, 0, 11, "ISO-8859-1");
                            if (app2.equals("ICC_PROFILE")) {
                                int order = byteapp2[12] & 255;
                                int count = byteapp2[13] & 255;
                                if (this.icc == null) {
                                    this.icc = new byte[count][];
                                }
                                this.icc[order - 1] = byteapp2;
                            }
                        }
                    } else {
                        firstPass = false;
                        int markertype = marker(marker);
                        if (markertype == 0) {
                            Utilities.skip(is, 2);
                            if (is.read() != 8) {
                                throw new BadElementException(errorID + " must have 8 bits per component.");
                            }
                            this.scaledHeight = getShort(is);
                            setTop(this.scaledHeight);
                            this.scaledWidth = getShort(is);
                            setRight(this.scaledWidth);
                            this.colorspace = is.read();
                            this.bpc = 8;
                            if (is != null) {
                                is.close();
                            }
                            this.plainWidth = getWidth();
                            this.plainHeight = getHeight();
                            if (this.icc != null) {
                                int total = 0;
                                for (int k4 = 0; k4 < this.icc.length; k4++) {
                                    if (this.icc[k4] == null) {
                                        this.icc = (byte[][]) null;
                                        return;
                                    }
                                    total += this.icc[k4].length - 14;
                                }
                                byte[] ficc = new byte[total];
                                int total2 = 0;
                                for (byte[] element : this.icc) {
                                    System.arraycopy(element, 14, ficc, total2, element.length - 14);
                                    total2 += element.length - 14;
                                }
                                try {
                                    ICC_Profile icc_prof = ICC_Profile.getInstance(ficc);
                                    tagICC(icc_prof);
                                } catch (IllegalArgumentException e) {
                                }
                                this.icc = (byte[][]) null;
                                return;
                            }
                            return;
                        }
                        if (markertype == 1) {
                            throw new BadElementException(errorID + ": unsupported JPEG marker: " + marker);
                        }
                        if (markertype != 2) {
                            Utilities.skip(is, getShort(is) - 2);
                        }
                    }
                }
            }
        } catch (Throwable th) {
            if (0 != 0) {
                is2.close();
            }
            throw th;
        }
    }
}
