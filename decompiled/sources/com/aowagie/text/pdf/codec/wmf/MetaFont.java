package com.aowagie.text.pdf.codec.wmf;

import com.aowagie.text.Document;
import com.aowagie.text.ExceptionConverter;
import com.aowagie.text.Font;
import com.aowagie.text.FontFactory;
import com.aowagie.text.pdf.BaseFont;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.mozilla.javascript.NativeSymbol;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/codec/wmf/MetaFont.class */
class MetaFont extends MetaObject {
    private static final String[] fontNames = {"Courier", "Courier-Bold", "Courier-Oblique", "Courier-BoldOblique", "Helvetica", "Helvetica-Bold", "Helvetica-Oblique", "Helvetica-BoldOblique", "Times-Roman", "Times-Bold", "Times-Italic", "Times-BoldItalic", "Symbol", "ZapfDingbats"};
    private static final int MARKER_BOLD = 1;
    private static final int MARKER_ITALIC = 2;
    private static final int MARKER_COURIER = 0;
    private static final int MARKER_HELVETICA = 4;
    private static final int MARKER_TIMES = 8;
    private static final int MARKER_SYMBOL = 12;
    private static final int FIXED_PITCH = 1;
    private static final int FF_ROMAN = 1;
    private static final int FF_SWISS = 2;
    private static final int FF_MODERN = 3;
    private static final int FF_SCRIPT = 4;
    private static final int FF_DECORATIVE = 5;
    private static final int BOLDTHRESHOLD = 600;
    private static final int nameSize = 32;
    static final int ETO_OPAQUE = 2;
    static final int ETO_CLIPPED = 4;
    private int height;
    private float angle;
    private int bold;
    private int italic;
    private boolean underline;
    private boolean strikeout;
    private int charset;
    private int pitchAndFamily;
    private String faceName = "arial";
    private BaseFont font = null;

    public MetaFont() {
        this.type = 3;
    }

    public void init(InputMeta in) throws IOException {
        int c;
        this.height = Math.abs(in.readShort());
        in.skip(2);
        this.angle = (float) ((((double) in.readShort()) / 1800.0d) * 3.141592653589793d);
        in.skip(2);
        this.bold = in.readShort() >= 600 ? 1 : 0;
        this.italic = in.readByte() != 0 ? 2 : 0;
        this.underline = in.readByte() != 0;
        this.strikeout = in.readByte() != 0;
        this.charset = in.readByte();
        in.skip(3);
        this.pitchAndFamily = in.readByte();
        byte[] name = new byte[32];
        int k = 0;
        while (k < 32 && (c = in.readByte()) != 0) {
            name[k] = (byte) c;
            k++;
        }
        try {
            this.faceName = new String(name, 0, k, "Cp1252");
        } catch (UnsupportedEncodingException e) {
            this.faceName = new String(name, 0, k);
        }
        this.faceName = this.faceName.toLowerCase();
    }

    public BaseFont getFont() {
        String fontName;
        if (this.font != null) {
            return this.font;
        }
        Font ff2 = FontFactory.getFont(this.faceName, "Cp1252", true, 10.0f, (this.italic != 0 ? 2 : 0) | (this.bold != 0 ? 1 : 0));
        this.font = ff2.getBaseFont();
        if (this.font != null) {
            return this.font;
        }
        if (this.faceName.indexOf("courier") != -1 || this.faceName.indexOf("terminal") != -1 || this.faceName.indexOf("fixedsys") != -1) {
            fontName = fontNames[0 + this.italic + this.bold];
        } else if (this.faceName.indexOf("ms sans serif") != -1 || this.faceName.indexOf("arial") != -1 || this.faceName.indexOf("system") != -1) {
            fontName = fontNames[4 + this.italic + this.bold];
        } else if (this.faceName.indexOf("arial black") != -1) {
            fontName = fontNames[4 + this.italic + 1];
        } else if (this.faceName.indexOf("times") != -1 || this.faceName.indexOf("ms serif") != -1 || this.faceName.indexOf("roman") != -1) {
            fontName = fontNames[8 + this.italic + this.bold];
        } else if (this.faceName.indexOf(NativeSymbol.TYPE_NAME) != -1) {
            fontName = fontNames[12];
        } else {
            int pitch = this.pitchAndFamily & 3;
            int family = (this.pitchAndFamily >> 4) & 7;
            switch (family) {
                case 1:
                    fontName = fontNames[8 + this.italic + this.bold];
                    break;
                case 2:
                case 4:
                case 5:
                    fontName = fontNames[4 + this.italic + this.bold];
                    break;
                case 3:
                    fontName = fontNames[0 + this.italic + this.bold];
                    break;
                default:
                    switch (pitch) {
                        case 1:
                            fontName = fontNames[0 + this.italic + this.bold];
                            break;
                        default:
                            fontName = fontNames[4 + this.italic + this.bold];
                            break;
                    }
                    break;
            }
        }
        try {
            this.font = BaseFont.createFont(fontName, "Cp1252", false);
            return this.font;
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    public float getAngle() {
        return this.angle;
    }

    public boolean isUnderline() {
        return this.underline;
    }

    public boolean isStrikeout() {
        return this.strikeout;
    }

    public float getFontSize(MetaState state) {
        return Math.abs(state.transformY(this.height) - state.transformY(0)) * Document.wmfFontCorrection;
    }
}
