package com.aowagie.text.html;

import com.aowagie.text.pdf.ColumnText;
import es.gob.afirma.keystores.filters.PolicyIdFilter;
import java.awt.Color;
import java.util.Properties;
import java.util.StringTokenizer;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/html/Markup.class */
public class Markup {
    public static final String HTML_ATTR_STYLE = "style";
    public static final String CSS_KEY_COLOR = "color";
    public static final String CSS_KEY_FONTFAMILY = "font-family";
    public static final String CSS_KEY_FONTSIZE = "font-size";
    public static final String CSS_KEY_FONTSTYLE = "font-style";
    public static final String CSS_KEY_FONTWEIGHT = "font-weight";
    public static final String CSS_KEY_LINEHEIGHT = "line-height";
    public static final String CSS_KEY_VERTICALALIGN = "vertical-align";
    public static final String CSS_VALUE_BOLD = "bold";
    public static final String CSS_VALUE_ITALIC = "italic";
    public static final String CSS_VALUE_LINETHROUGH = "line-through";
    public static final String CSS_VALUE_NORMAL = "normal";
    public static final String CSS_VALUE_OBLIQUE = "oblique";
    public static final String CSS_VALUE_UNDERLINE = "underline";
    public static final float DEFAULT_FONT_SIZE = 12.0f;

    public static float parseLength(String string) {
        int pos = 0;
        int length = string.length();
        boolean ok = true;
        while (ok && pos < length) {
            switch (string.charAt(pos)) {
                case '+':
                case '-':
                case '.':
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    pos++;
                    break;
                case ',':
                case '/':
                default:
                    ok = false;
                    break;
            }
        }
        if (pos == 0) {
            return ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        }
        if (pos == length) {
            return Float.parseFloat(string + OperatorName.FILL_NON_ZERO);
        }
        float f = Float.parseFloat(string.substring(0, pos) + OperatorName.FILL_NON_ZERO);
        String string2 = string.substring(pos);
        if (string2.startsWith("in")) {
            return f * 72.0f;
        }
        if (string2.startsWith(OperatorName.CONCAT)) {
            return (f / 2.54f) * 72.0f;
        }
        if (string2.startsWith("mm")) {
            return (f / 25.4f) * 72.0f;
        }
        if (string2.startsWith("pc")) {
            return f * 12.0f;
        }
        return f;
    }

    public static float parseLength(String string, float actualFontSize) {
        if (string == null) {
            return ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        }
        int pos = 0;
        int length = string.length();
        boolean ok = true;
        while (ok && pos < length) {
            switch (string.charAt(pos)) {
                case '+':
                case '-':
                case '.':
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    pos++;
                    break;
                case ',':
                case '/':
                default:
                    ok = false;
                    break;
            }
        }
        if (pos == 0) {
            return ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        }
        if (pos == length) {
            return Float.parseFloat(string + OperatorName.FILL_NON_ZERO);
        }
        float f = Float.parseFloat(string.substring(0, pos) + OperatorName.FILL_NON_ZERO);
        String string2 = string.substring(pos);
        if (string2.startsWith("in")) {
            return f * 72.0f;
        }
        if (string2.startsWith(OperatorName.CONCAT)) {
            return (f / 2.54f) * 72.0f;
        }
        if (string2.startsWith("mm")) {
            return (f / 25.4f) * 72.0f;
        }
        if (string2.startsWith("pc")) {
            return f * 12.0f;
        }
        if (string2.startsWith("em")) {
            return f * actualFontSize;
        }
        if (string2.startsWith("ex")) {
            return (f * actualFontSize) / 2.0f;
        }
        return f;
    }

    public static Color decodeColor(String s) {
        if (s == null) {
            return null;
        }
        try {
            return WebColors.getRGBColor(s.toLowerCase().trim());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static Properties parseAttributes(String string) {
        Properties result = new Properties();
        if (string == null) {
            return result;
        }
        StringTokenizer keyValuePairs = new StringTokenizer(string, PolicyIdFilter.OID_SEPARATOR);
        while (keyValuePairs.hasMoreTokens()) {
            StringTokenizer keyValuePair = new StringTokenizer(keyValuePairs.nextToken(), ":");
            if (keyValuePair.hasMoreTokens()) {
                String key = keyValuePair.nextToken().trim();
                if (keyValuePair.hasMoreTokens()) {
                    String value = keyValuePair.nextToken().trim();
                    if (value.startsWith(OperatorName.SHOW_TEXT_LINE_AND_SPACE)) {
                        value = value.substring(1);
                    }
                    if (value.endsWith(OperatorName.SHOW_TEXT_LINE_AND_SPACE)) {
                        value = value.substring(0, value.length() - 1);
                    }
                    result.setProperty(key.toLowerCase(), value);
                }
            }
        }
        return result;
    }
}
