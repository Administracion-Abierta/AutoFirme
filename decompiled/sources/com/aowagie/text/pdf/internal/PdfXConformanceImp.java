package com.aowagie.text.pdf.internal;

import com.aowagie.text.pdf.BaseFont;
import com.aowagie.text.pdf.ExtendedColor;
import com.aowagie.text.pdf.PatternColor;
import com.aowagie.text.pdf.PdfArray;
import com.aowagie.text.pdf.PdfDictionary;
import com.aowagie.text.pdf.PdfGState;
import com.aowagie.text.pdf.PdfImage;
import com.aowagie.text.pdf.PdfName;
import com.aowagie.text.pdf.PdfNumber;
import com.aowagie.text.pdf.PdfObject;
import com.aowagie.text.pdf.PdfString;
import com.aowagie.text.pdf.PdfWriter;
import com.aowagie.text.pdf.PdfXConformanceException;
import com.aowagie.text.pdf.ShadingColor;
import com.aowagie.text.pdf.SpotColor;
import com.aowagie.text.pdf.interfaces.PdfXConformance;
import java.awt.Color;
import org.apache.fontbox.ttf.OpenTypeScript;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/internal/PdfXConformanceImp.class */
public class PdfXConformanceImp implements PdfXConformance {
    public static final int PDFXKEY_COLOR = 1;
    private static final int PDFXKEY_CMYK = 2;
    public static final int PDFXKEY_RGB = 3;
    public static final int PDFXKEY_FONT = 4;
    public static final int PDFXKEY_IMAGE = 5;
    public static final int PDFXKEY_GSTATE = 6;
    public static final int PDFXKEY_LAYER = 7;
    private int pdfxConformance = 0;

    @Override // com.aowagie.text.pdf.interfaces.PdfXConformance
    public void setPDFXConformance(int pdfxConformance) {
        this.pdfxConformance = pdfxConformance;
    }

    @Override // com.aowagie.text.pdf.interfaces.PdfXConformance
    public int getPDFXConformance() {
        return this.pdfxConformance;
    }

    @Override // com.aowagie.text.pdf.interfaces.PdfXConformance
    public boolean isPdfX() {
        return this.pdfxConformance != 0;
    }

    public boolean isPdfX1A2001() {
        return this.pdfxConformance == 1;
    }

    public boolean isPdfX32002() {
        return this.pdfxConformance == 2;
    }

    public boolean isPdfA1() {
        return this.pdfxConformance == 3 || this.pdfxConformance == 4 || this.pdfxConformance == 5 || this.pdfxConformance == 6;
    }

    public boolean isPdfA1A() {
        return this.pdfxConformance == 3;
    }

    public void completeInfoDictionary(PdfDictionary info) {
        if (isPdfX() && !isPdfA1()) {
            if (info.get(PdfName.GTS_PDFXVERSION) == null) {
                if (isPdfX1A2001()) {
                    info.put(PdfName.GTS_PDFXVERSION, new PdfString("PDF/X-1:2001"));
                    info.put(new PdfName("GTS_PDFXConformance"), new PdfString("PDF/X-1a:2001"));
                } else if (isPdfX32002()) {
                    info.put(PdfName.GTS_PDFXVERSION, new PdfString("PDF/X-3:2002"));
                }
            }
            if (info.get(PdfName.TITLE) == null) {
                info.put(PdfName.TITLE, new PdfString("Pdf document"));
            }
            if (info.get(PdfName.CREATOR) == null) {
                info.put(PdfName.CREATOR, new PdfString(OpenTypeScript.UNKNOWN));
            }
            if (info.get(PdfName.TRAPPED) == null) {
                info.put(PdfName.TRAPPED, new PdfName("False"));
            }
        }
    }

    public void completeExtraCatalog(PdfDictionary extraCatalog) {
        if (isPdfX() && !isPdfA1() && extraCatalog.get(PdfName.OUTPUTINTENTS) == null) {
            PdfDictionary out = new PdfDictionary(PdfName.OUTPUTINTENT);
            out.put(PdfName.OUTPUTCONDITION, new PdfString("SWOP CGATS TR 001-1995"));
            out.put(PdfName.OUTPUTCONDITIONIDENTIFIER, new PdfString("CGATS TR 001"));
            out.put(PdfName.REGISTRYNAME, new PdfString("http://www.color.org"));
            out.put(PdfName.INFO, new PdfString(""));
            out.put(PdfName.S, PdfName.GTS_PDFX);
            extraCatalog.put(PdfName.OUTPUTINTENTS, new PdfArray(out));
        }
    }

    public static void checkPDFXConformance(PdfWriter writer, int key, Object obj1) {
        if (writer == null || !writer.isPdfX()) {
            return;
        }
        int conf = writer.getPDFXConformance();
        switch (key) {
            case 1:
                switch (conf) {
                    case 1:
                        if (obj1 instanceof ExtendedColor) {
                            ExtendedColor ec = (ExtendedColor) obj1;
                            switch (ec.getType()) {
                                case 0:
                                    throw new PdfXConformanceException("Colorspace RGB is not allowed.");
                                case 1:
                                case 2:
                                    return;
                                case 3:
                                    SpotColor sc = (SpotColor) ec;
                                    checkPDFXConformance(writer, 1, sc.getPdfSpotColor().getAlternativeCS());
                                    return;
                                case 4:
                                    PatternColor pc = (PatternColor) ec;
                                    checkPDFXConformance(writer, 1, pc.getPainter().getDefaultColor());
                                    return;
                                case 5:
                                    ShadingColor xc = (ShadingColor) ec;
                                    checkPDFXConformance(writer, 1, xc.getPdfShadingPattern().getShading().getColorSpace());
                                    return;
                                default:
                                    return;
                            }
                        }
                        if (obj1 instanceof Color) {
                            throw new PdfXConformanceException("Colorspace RGB is not allowed.");
                        }
                        return;
                    default:
                        return;
                }
            case 2:
            default:
                return;
            case 3:
                if (conf == 1) {
                    throw new PdfXConformanceException("Colorspace RGB is not allowed.");
                }
                return;
            case 4:
                if (!((BaseFont) obj1).isEmbedded()) {
                    throw new PdfXConformanceException("All the fonts must be embedded. This one isn't: " + ((BaseFont) obj1).getPostscriptFontName());
                }
                return;
            case 5:
                PdfImage image = (PdfImage) obj1;
                if (image.get(PdfName.SMASK) != null) {
                    throw new PdfXConformanceException("The /SMask key is not allowed in images.");
                }
                switch (conf) {
                    case 1:
                        PdfObject cs = image.get(PdfName.COLORSPACE);
                        if (cs == null) {
                            return;
                        }
                        if (cs.isName()) {
                            if (PdfName.DEVICERGB.equals(cs)) {
                                throw new PdfXConformanceException("Colorspace RGB is not allowed.");
                            }
                            return;
                        } else {
                            if (cs.isArray() && PdfName.CALRGB.equals(((PdfArray) cs).getPdfObject(0))) {
                                throw new PdfXConformanceException("Colorspace CalRGB is not allowed.");
                            }
                            return;
                        }
                    default:
                        return;
                }
            case 6:
                PdfDictionary gs = (PdfDictionary) obj1;
                PdfObject obj = gs.get(PdfName.BM);
                if (obj != null && !PdfGState.BM_NORMAL.equals(obj) && !PdfGState.BM_COMPATIBLE.equals(obj)) {
                    throw new PdfXConformanceException("Blend mode " + obj.toString() + " not allowed.");
                }
                PdfObject obj2 = gs.get(PdfName.CA);
                if (obj2 != null) {
                    double v = ((PdfNumber) obj2).doubleValue();
                    if (v != 1.0d) {
                        throw new PdfXConformanceException("Transparency is not allowed: /CA = " + v);
                    }
                }
                PdfObject obj3 = gs.get(PdfName.ca);
                if (obj3 != null) {
                    double v2 = ((PdfNumber) obj3).doubleValue();
                    if (v2 != 1.0d) {
                        throw new PdfXConformanceException("Transparency is not allowed: /ca = " + v2);
                    }
                    return;
                }
                return;
            case 7:
                throw new PdfXConformanceException("Layers are not allowed.");
        }
    }
}
