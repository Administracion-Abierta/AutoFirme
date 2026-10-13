package com.aowagie.text.pdf;

import java.awt.Color;
import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfShading.class */
public class PdfShading {
    private PdfDictionary shading;
    private final PdfWriter writer;
    private int shadingType;
    private ColorDetails colorDetails;
    private PdfName shadingName;
    private PdfIndirectReference shadingReference;
    private Color cspace;
    private float[] bBox;
    private boolean antiAlias = false;

    private PdfShading(PdfWriter writer) {
        this.writer = writer;
    }

    private void setColorSpace(Color color) {
        PdfObject colorSpace;
        this.cspace = color;
        int type = ExtendedColor.getType(color);
        switch (type) {
            case 1:
                colorSpace = PdfName.DEVICEGRAY;
                break;
            case 2:
                colorSpace = PdfName.DEVICECMYK;
                break;
            case 3:
                SpotColor spot = (SpotColor) color;
                this.colorDetails = this.writer.addSimple(spot.getPdfSpotColor());
                colorSpace = this.colorDetails.getIndirectReference();
                break;
            case 4:
            case 5:
                throwColorSpaceError();
            default:
                colorSpace = PdfName.DEVICERGB;
                break;
        }
        this.shading.put(PdfName.COLORSPACE, colorSpace);
    }

    public Color getColorSpace() {
        return this.cspace;
    }

    private static void throwColorSpaceError() {
        throw new IllegalArgumentException("A tiling or shading pattern cannot be used as a color space in a shading pattern");
    }

    private static void checkCompatibleColors(Color c1, Color c2) {
        int type1 = ExtendedColor.getType(c1);
        int type2 = ExtendedColor.getType(c2);
        if (type1 != type2) {
            throw new IllegalArgumentException("Both colors must be of the same type.");
        }
        if (type1 == 3 && ((SpotColor) c1).getPdfSpotColor() != ((SpotColor) c2).getPdfSpotColor()) {
            throw new IllegalArgumentException("The spot color must be the same, only the tint can vary.");
        }
        if (type1 == 4 || type1 == 5) {
            throwColorSpaceError();
        }
    }

    private static float[] getColorArray(Color color) {
        int type = ExtendedColor.getType(color);
        switch (type) {
            case 0:
                return new float[]{color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f};
            case 1:
                return new float[]{((GrayColor) color).getGray()};
            case 2:
                CMYKColor cmyk = (CMYKColor) color;
                return new float[]{cmyk.getCyan(), cmyk.getMagenta(), cmyk.getYellow(), cmyk.getBlack()};
            case 3:
                return new float[]{((SpotColor) color).getTint()};
            default:
                throwColorSpaceError();
                return null;
        }
    }

    private static PdfShading type2(PdfWriter writer, Color colorSpace, float[] coords, float[] domain, PdfFunction function, boolean[] extend) {
        PdfShading sp = new PdfShading(writer);
        sp.shading = new PdfDictionary();
        sp.shadingType = 2;
        sp.shading.put(PdfName.SHADINGTYPE, new PdfNumber(sp.shadingType));
        sp.setColorSpace(colorSpace);
        sp.shading.put(PdfName.COORDS, new PdfArray(coords));
        if (domain != null) {
            sp.shading.put(PdfName.DOMAIN, new PdfArray(domain));
        }
        sp.shading.put(PdfName.FUNCTION, function.getReference());
        if (extend != null && (extend[0] || extend[1])) {
            PdfArray array = new PdfArray(extend[0] ? PdfBoolean.PDFTRUE : PdfBoolean.PDFFALSE);
            array.add(extend[1] ? PdfBoolean.PDFTRUE : PdfBoolean.PDFFALSE);
            sp.shading.put(PdfName.EXTEND, array);
        }
        return sp;
    }

    private static PdfShading type3(PdfWriter writer, Color colorSpace, float[] coords, float[] domain, PdfFunction function, boolean[] extend) {
        PdfShading sp = type2(writer, colorSpace, coords, domain, function, extend);
        sp.shadingType = 3;
        sp.shading.put(PdfName.SHADINGTYPE, new PdfNumber(sp.shadingType));
        return sp;
    }

    private static PdfShading simpleAxial(PdfWriter writer, float x0, float y0, float x1, float y1, Color startColor, Color endColor, boolean extendStart, boolean extendEnd) {
        checkCompatibleColors(startColor, endColor);
        PdfFunction function = PdfFunction.type2(writer, new float[]{ColumnText.GLOBAL_SPACE_CHAR_RATIO, 1.0f}, null, getColorArray(startColor), getColorArray(endColor), 1.0f);
        return type2(writer, startColor, new float[]{x0, y0, x1, y1}, null, function, new boolean[]{extendStart, extendEnd});
    }

    static PdfShading simpleAxial(PdfWriter writer, float x0, float y0, float x1, float y1, Color startColor, Color endColor) {
        return simpleAxial(writer, x0, y0, x1, y1, startColor, endColor, true, true);
    }

    private static PdfShading simpleRadial(PdfWriter writer, float x0, float y0, float r0, float x1, float y1, float r1, Color startColor, Color endColor, boolean extendStart, boolean extendEnd) {
        checkCompatibleColors(startColor, endColor);
        PdfFunction function = PdfFunction.type2(writer, new float[]{ColumnText.GLOBAL_SPACE_CHAR_RATIO, 1.0f}, null, getColorArray(startColor), getColorArray(endColor), 1.0f);
        return type3(writer, startColor, new float[]{x0, y0, r0, x1, y1, r1}, null, function, new boolean[]{extendStart, extendEnd});
    }

    PdfName getShadingName() {
        return this.shadingName;
    }

    PdfIndirectReference getShadingReference() {
        if (this.shadingReference == null) {
            this.shadingReference = this.writer.getPdfIndirectReference();
        }
        return this.shadingReference;
    }

    void setName(int number) {
        this.shadingName = new PdfName("Sh" + number);
    }

    void addToBody() throws IOException {
        if (this.bBox != null) {
            this.shading.put(PdfName.BBOX, new PdfArray(this.bBox));
        }
        if (this.antiAlias) {
            this.shading.put(PdfName.ANTIALIAS, PdfBoolean.PDFTRUE);
        }
        this.writer.addToBody(this.shading, getShadingReference());
    }

    PdfWriter getWriter() {
        return this.writer;
    }

    ColorDetails getColorDetails() {
        return this.colorDetails;
    }

    public float[] getBBox() {
        return this.bBox;
    }

    public void setBBox(float[] bBox) {
        if (bBox.length != 4) {
            throw new IllegalArgumentException("BBox must be a 4 element array.");
        }
        this.bBox = bBox;
    }

    public boolean isAntiAlias() {
        return this.antiAlias;
    }

    public void setAntiAlias(boolean antiAlias) {
        this.antiAlias = antiAlias;
    }
}
