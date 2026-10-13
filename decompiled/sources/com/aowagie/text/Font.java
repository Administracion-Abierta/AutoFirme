package com.aowagie.text;

import com.aowagie.text.html.Markup;
import com.aowagie.text.pdf.BaseFont;
import java.awt.Color;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/Font.class */
public class Font implements Comparable {
    public static final int COURIER = 0;
    public static final int HELVETICA = 1;
    public static final int TIMES_ROMAN = 2;
    public static final int SYMBOL = 3;
    public static final int ZAPFDINGBATS = 4;
    public static final int NORMAL = 0;
    public static final int BOLD = 1;
    public static final int ITALIC = 2;
    public static final int UNDERLINE = 4;
    public static final int STRIKETHRU = 8;
    static final int BOLDITALIC = 3;
    public static final int UNDEFINED = -1;
    static final int DEFAULTSIZE = 12;
    private int family;
    private float size;
    private int style;
    private Color color;
    private BaseFont baseFont;

    public Font(Font other) {
        this.family = -1;
        this.size = -1.0f;
        this.style = -1;
        this.color = null;
        this.baseFont = null;
        this.family = other.family;
        this.size = other.size;
        this.style = other.style;
        this.color = other.color;
        this.baseFont = other.baseFont;
    }

    public Font(int family, float size, int style, Color color) {
        this.family = -1;
        this.size = -1.0f;
        this.style = -1;
        this.color = null;
        this.baseFont = null;
        this.family = family;
        this.size = size;
        this.style = style;
        this.color = color;
    }

    public Font(BaseFont bf, float size, int style, Color color) {
        this.family = -1;
        this.size = -1.0f;
        this.style = -1;
        this.color = null;
        this.baseFont = null;
        this.baseFont = bf;
        this.size = size;
        this.style = style;
        this.color = color;
    }

    public Font(BaseFont bf) {
        this(bf, -1.0f, -1, (Color) null);
    }

    public Font() {
        this(-1, -1.0f, -1, (Color) null);
    }

    @Override // java.lang.Comparable
    public int compareTo(Object object) {
        if (object == null) {
            return -1;
        }
        try {
            Font font = (Font) object;
            if (this.baseFont != null && !this.baseFont.equals(font.getBaseFont())) {
                return -2;
            }
            if (this.family != font.getFamily()) {
                return 1;
            }
            if (this.size != font.getSize()) {
                return 2;
            }
            if (this.style != font.getStyle()) {
                return 3;
            }
            if (this.color == null) {
                if (font.color == null) {
                    return 0;
                }
                return 4;
            }
            if (font.color != null && this.color.equals(font.getColor())) {
                return 0;
            }
            return 4;
        } catch (ClassCastException e) {
            return -3;
        }
    }

    public int getFamily() {
        return this.family;
    }

    public String getFamilyname() {
        String tmp = "unknown";
        switch (getFamily()) {
            case 0:
                return "Courier";
            case 1:
                return "Helvetica";
            case 2:
                return "Times-Roman";
            case 3:
                return "Symbol";
            case 4:
                return "ZapfDingbats";
            default:
                if (this.baseFont != null) {
                    String[][] names = this.baseFont.getFamilyFontName();
                    for (String[] name : names) {
                        if ("0".equals(name[2])) {
                            return name[3];
                        }
                        if ("1033".equals(name[2])) {
                            tmp = name[3];
                        }
                        if ("".equals(name[2])) {
                            tmp = name[3];
                        }
                    }
                }
                return tmp;
        }
    }

    public void setFamily(String family) {
        this.family = getFamilyIndex(family);
    }

    private static int getFamilyIndex(String family) {
        if (family.equalsIgnoreCase("Courier")) {
            return 0;
        }
        if (family.equalsIgnoreCase("Helvetica")) {
            return 1;
        }
        if (family.equalsIgnoreCase("Times-Roman")) {
            return 2;
        }
        if (family.equalsIgnoreCase("Symbol")) {
            return 3;
        }
        if (family.equalsIgnoreCase("ZapfDingbats")) {
            return 4;
        }
        return -1;
    }

    public float getSize() {
        return this.size;
    }

    public float getCalculatedSize() {
        float s = this.size;
        if (s == -1.0f) {
            s = 12.0f;
        }
        return s;
    }

    float getCalculatedLeading(float linespacing) {
        return linespacing * getCalculatedSize();
    }

    public void setSize(float size) {
        this.size = size;
    }

    public int getStyle() {
        return this.style;
    }

    public int getCalculatedStyle() {
        int style = this.style;
        if (style == -1) {
            style = 0;
        }
        if (this.baseFont != null) {
            return style;
        }
        if (this.family == 3 || this.family == 4) {
            return style;
        }
        return style & (-4);
    }

    public boolean isBold() {
        return this.style != -1 && (this.style & 1) == 1;
    }

    public boolean isItalic() {
        return this.style != -1 && (this.style & 2) == 2;
    }

    public boolean isUnderlined() {
        return this.style != -1 && (this.style & 4) == 4;
    }

    public boolean isStrikethru() {
        return this.style != -1 && (this.style & 8) == 8;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public void setStyle(String style) {
        if (this.style == -1) {
            this.style = 0;
        }
        this.style |= getStyleValue(style);
    }

    static int getStyleValue(String style) {
        int s = 0;
        if (style.indexOf(Markup.CSS_VALUE_NORMAL) != -1) {
            s = 0 | 0;
        }
        if (style.indexOf(Markup.CSS_VALUE_BOLD) != -1) {
            s |= 1;
        }
        if (style.indexOf(Markup.CSS_VALUE_ITALIC) != -1) {
            s |= 2;
        }
        if (style.indexOf(Markup.CSS_VALUE_OBLIQUE) != -1) {
            s |= 2;
        }
        if (style.indexOf(Markup.CSS_VALUE_UNDERLINE) != -1) {
            s |= 4;
        }
        if (style.indexOf(Markup.CSS_VALUE_LINETHROUGH) != -1) {
            s |= 8;
        }
        return s;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public BaseFont getBaseFont() {
        return this.baseFont;
    }

    public BaseFont getCalculatedBaseFont(boolean specialEncoding) {
        String fontName;
        if (this.baseFont != null) {
            return this.baseFont;
        }
        int style = this.style;
        if (style == -1) {
            style = 0;
        }
        String encoding = "Cp1252";
        switch (this.family) {
            case 0:
                switch (style & 3) {
                    case 1:
                        fontName = "Courier-Bold";
                        break;
                    case 2:
                        fontName = "Courier-Oblique";
                        break;
                    case 3:
                        fontName = "Courier-BoldOblique";
                        break;
                    default:
                        fontName = "Courier";
                        break;
                }
                break;
            case 1:
            default:
                switch (style & 3) {
                    case 0:
                    default:
                        fontName = "Helvetica";
                        break;
                    case 1:
                        fontName = "Helvetica-Bold";
                        break;
                    case 2:
                        fontName = "Helvetica-Oblique";
                        break;
                    case 3:
                        fontName = "Helvetica-BoldOblique";
                        break;
                }
                break;
            case 2:
                switch (style & 3) {
                    case 0:
                    default:
                        fontName = "Times-Roman";
                        break;
                    case 1:
                        fontName = "Times-Bold";
                        break;
                    case 2:
                        fontName = "Times-Italic";
                        break;
                    case 3:
                        fontName = "Times-BoldItalic";
                        break;
                }
                break;
            case 3:
                fontName = "Symbol";
                if (specialEncoding) {
                    encoding = "Symbol";
                }
                break;
            case 4:
                fontName = "ZapfDingbats";
                if (specialEncoding) {
                    encoding = "ZapfDingbats";
                }
                break;
        }
        try {
            BaseFont cfont = BaseFont.createFont(fontName, encoding, false);
            return cfont;
        } catch (Exception ee) {
            throw new ExceptionConverter(ee);
        }
    }

    public boolean isStandardFont() {
        return this.family == -1 && this.size == -1.0f && this.style == -1 && this.color == null && this.baseFont == null;
    }

    Font difference(Font font) {
        if (font == null) {
            return this;
        }
        float dSize = font.size;
        if (dSize == -1.0f) {
            dSize = this.size;
        }
        int dStyle = -1;
        int style1 = this.style;
        int style2 = font.getStyle();
        if (style1 != -1 || style2 != -1) {
            if (style1 == -1) {
                style1 = 0;
            }
            if (style2 == -1) {
                style2 = 0;
            }
            dStyle = style1 | style2;
        }
        Color dColor = font.color;
        if (dColor == null) {
            dColor = this.color;
        }
        if (font.baseFont != null) {
            return new Font(font.baseFont, dSize, dStyle, dColor);
        }
        if (font.getFamily() != -1) {
            return new Font(font.family, dSize, dStyle, dColor);
        }
        if (this.baseFont != null) {
            if (dStyle == style1) {
                return new Font(this.baseFont, dSize, dStyle, dColor);
            }
            return FontFactory.getFont(getFamilyname(), dSize, dStyle, dColor);
        }
        return new Font(this.family, dSize, dStyle, dColor);
    }
}
