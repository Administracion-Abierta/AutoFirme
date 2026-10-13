package com.aowagie.text.pdf;

import java.awt.Color;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfSpotColor.class */
public class PdfSpotColor {
    private float tint;
    private PdfName name;
    private Color altcs;

    public float getTint() {
        return this.tint;
    }

    public Color getAlternativeCS() {
        return this.altcs;
    }

    protected PdfObject getSpotObject(PdfWriter writer) {
        PdfFunction func;
        PdfArray array = new PdfArray(PdfName.SEPARATION);
        array.add(this.name);
        if (this.altcs instanceof ExtendedColor) {
            int type = ((ExtendedColor) this.altcs).type;
            switch (type) {
                case 1:
                    array.add(PdfName.DEVICEGRAY);
                    func = PdfFunction.type2(writer, new float[]{ColumnText.GLOBAL_SPACE_CHAR_RATIO, 1.0f}, null, new float[]{ColumnText.GLOBAL_SPACE_CHAR_RATIO}, new float[]{((GrayColor) this.altcs).getGray()}, 1.0f);
                    break;
                case 2:
                    array.add(PdfName.DEVICECMYK);
                    CMYKColor cmyk = (CMYKColor) this.altcs;
                    func = PdfFunction.type2(writer, new float[]{ColumnText.GLOBAL_SPACE_CHAR_RATIO, 1.0f}, null, new float[]{ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO, ColumnText.GLOBAL_SPACE_CHAR_RATIO}, new float[]{cmyk.getCyan(), cmyk.getMagenta(), cmyk.getYellow(), cmyk.getBlack()}, 1.0f);
                    break;
                default:
                    throw new RuntimeException("Only RGB, Gray and CMYK are supported as alternative color spaces.");
            }
        } else {
            array.add(PdfName.DEVICERGB);
            func = PdfFunction.type2(writer, new float[]{ColumnText.GLOBAL_SPACE_CHAR_RATIO, 1.0f}, null, new float[]{1.0f, 1.0f, 1.0f}, new float[]{this.altcs.getRed() / 255.0f, this.altcs.getGreen() / 255.0f, this.altcs.getBlue() / 255.0f}, 1.0f);
        }
        array.add(func.getReference());
        return array;
    }
}
