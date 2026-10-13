package com.aowagie.text.pdf;

import com.aowagie.text.Chunk;
import com.aowagie.text.Font;
import com.aowagie.text.Image;
import com.aowagie.text.SplitCharacter;
import com.aowagie.text.Utilities;
import com.sun.jna.platform.win32.COM.tlb.imp.TlbBase;
import java.awt.Color;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfChunk.class */
public class PdfChunk {
    private static final float ITALIC_ANGLE = 0.21256f;
    private String value;
    private String encoding;
    protected PdfFont font;
    private BaseFont baseFont;
    private SplitCharacter splitCharacter;
    private HashMap attributes;
    private HashMap noStroke;
    private boolean newlineSplit;
    private Image image;
    private float offsetX;
    private float offsetY;
    protected boolean changeLeading;
    private static final char[] singleSpace = {' '};
    private static final PdfChunk[] thisChunk = new PdfChunk[1];
    private static final HashMap keysAttributes = new LinkedHashMap();
    private static final HashMap keysNoStroke = new HashMap();

    static {
        keysAttributes.put(Chunk.ACTION, null);
        keysAttributes.put(Chunk.UNDERLINE, null);
        keysAttributes.put(Chunk.REMOTEGOTO, null);
        keysAttributes.put(Chunk.LOCALGOTO, null);
        keysAttributes.put(Chunk.LOCALDESTINATION, null);
        keysAttributes.put(Chunk.GENERICTAG, null);
        keysAttributes.put(Chunk.NEWPAGE, null);
        keysAttributes.put(Chunk.IMAGE, null);
        keysAttributes.put(Chunk.BACKGROUND, null);
        keysAttributes.put(Chunk.PDFANNOTATION, null);
        keysAttributes.put(Chunk.SKEW, null);
        keysAttributes.put(Chunk.HSCALE, null);
        keysAttributes.put(Chunk.SEPARATOR, null);
        keysAttributes.put(Chunk.TAB, null);
        keysNoStroke.put(Chunk.SUBSUPSCRIPT, null);
        keysNoStroke.put(Chunk.SPLITCHARACTER, null);
        keysNoStroke.put(Chunk.HYPHENATION, null);
        keysNoStroke.put(Chunk.TEXTRENDERMODE, null);
    }

    PdfChunk(String string, PdfChunk other) {
        this.value = "";
        this.encoding = "Cp1252";
        this.attributes = new LinkedHashMap();
        this.noStroke = new HashMap();
        this.changeLeading = false;
        thisChunk[0] = this;
        this.value = string;
        this.font = other.font;
        this.attributes = other.attributes;
        this.noStroke = other.noStroke;
        this.baseFont = other.baseFont;
        Object[] obj = (Object[]) this.attributes.get(Chunk.IMAGE);
        if (obj == null) {
            this.image = null;
        } else {
            this.image = (Image) obj[0];
            this.offsetX = ((Float) obj[1]).floatValue();
            this.offsetY = ((Float) obj[2]).floatValue();
            this.changeLeading = ((Boolean) obj[3]).booleanValue();
        }
        this.encoding = this.font.getFont().getEncoding();
        this.splitCharacter = (SplitCharacter) this.noStroke.get(Chunk.SPLITCHARACTER);
        if (this.splitCharacter == null) {
            this.splitCharacter = DefaultSplitCharacter.DEFAULT;
        }
    }

    PdfChunk(Chunk chunk, PdfAction action) {
        this.value = "";
        this.encoding = "Cp1252";
        this.attributes = new LinkedHashMap();
        this.noStroke = new HashMap();
        this.changeLeading = false;
        thisChunk[0] = this;
        this.value = chunk.getContent();
        Font f = chunk.getFont();
        float size = f.getSize();
        size = size == -1.0f ? 12.0f : size;
        this.baseFont = f.getBaseFont();
        int style = f.getStyle();
        style = style == -1 ? 0 : style;
        if (this.baseFont == null) {
            this.baseFont = f.getCalculatedBaseFont(false);
        } else {
            if ((style & 1) != 0) {
                this.attributes.put(Chunk.TEXTRENDERMODE, new Object[]{2, Float.valueOf(size / 30.0f), null});
            }
            if ((style & 2) != 0) {
                this.attributes.put(Chunk.SKEW, new float[]{ColumnText.GLOBAL_SPACE_CHAR_RATIO, ITALIC_ANGLE});
            }
        }
        this.font = new PdfFont(this.baseFont, size);
        HashMap attr = chunk.getAttributes();
        if (attr != null) {
            for (Map.Entry entry : attr.entrySet()) {
                Object name = entry.getKey();
                if (keysAttributes.containsKey(name)) {
                    this.attributes.put(name, entry.getValue());
                } else if (keysNoStroke.containsKey(name)) {
                    this.noStroke.put(name, entry.getValue());
                }
            }
            if ("".equals(attr.get(Chunk.GENERICTAG))) {
                this.attributes.put(Chunk.GENERICTAG, chunk.getContent());
            }
        }
        if (f.isUnderlined()) {
            Object[][] unders = Utilities.addToArray((Object[][]) this.attributes.get(Chunk.UNDERLINE), new Object[]{null, new float[]{ColumnText.GLOBAL_SPACE_CHAR_RATIO, 0.06666667f, ColumnText.GLOBAL_SPACE_CHAR_RATIO, -0.33333334f, ColumnText.GLOBAL_SPACE_CHAR_RATIO}});
            this.attributes.put(Chunk.UNDERLINE, unders);
        }
        if (f.isStrikethru()) {
            Object[][] unders2 = Utilities.addToArray((Object[][]) this.attributes.get(Chunk.UNDERLINE), new Object[]{null, new float[]{ColumnText.GLOBAL_SPACE_CHAR_RATIO, 0.06666667f, ColumnText.GLOBAL_SPACE_CHAR_RATIO, 0.33333334f, ColumnText.GLOBAL_SPACE_CHAR_RATIO}});
            this.attributes.put(Chunk.UNDERLINE, unders2);
        }
        if (action != null) {
            this.attributes.put(Chunk.ACTION, action);
        }
        this.noStroke.put(Chunk.COLOR, f.getColor());
        this.noStroke.put(Chunk.ENCODING, this.font.getFont().getEncoding());
        Object[] obj = (Object[]) this.attributes.get(Chunk.IMAGE);
        if (obj == null) {
            this.image = null;
        } else {
            this.attributes.remove(Chunk.HSCALE);
            this.image = (Image) obj[0];
            this.offsetX = ((Float) obj[1]).floatValue();
            this.offsetY = ((Float) obj[2]).floatValue();
            this.changeLeading = ((Boolean) obj[3]).booleanValue();
        }
        this.font.setImage(this.image);
        Float hs = (Float) this.attributes.get(Chunk.HSCALE);
        if (hs != null) {
            this.font.setHorizontalScaling(hs.floatValue());
        }
        this.encoding = this.font.getFont().getEncoding();
        this.splitCharacter = (SplitCharacter) this.noStroke.get(Chunk.SPLITCHARACTER);
        if (this.splitCharacter == null) {
            this.splitCharacter = DefaultSplitCharacter.DEFAULT;
        }
    }

    int getUnicodeEquivalent(int c) {
        return this.baseFont.getUnicodeEquivalent(c);
    }

    private int getWord(String text, int start) {
        int len = text.length();
        while (start < len && Character.isLetter(text.charAt(start))) {
            start++;
        }
        return start;
    }

    PdfChunk split(float width) {
        int wordIdx;
        this.newlineSplit = false;
        if (this.image != null) {
            if (this.image.getScaledWidth() > width) {
                PdfChunk pc = new PdfChunk(Chunk.OBJECT_REPLACEMENT_CHARACTER, this);
                this.value = "";
                this.attributes = new HashMap();
                this.image = null;
                this.font = PdfFont.getDefaultFont();
                return pc;
            }
            return null;
        }
        HyphenationEvent hyphenationEvent = (HyphenationEvent) this.noStroke.get(Chunk.HYPHENATION);
        int currentPosition = 0;
        int splitPosition = -1;
        float currentWidth = 0.0f;
        int lastSpace = -1;
        float lastSpaceWidth = 0.0f;
        int length = this.value.length();
        char[] valueArray = this.value.toCharArray();
        BaseFont ft = this.font.getFont();
        if (ft.getFontType() == 2 && ft.getUnicodeEquivalent(32) != 32) {
            while (currentPosition < length) {
                char cidChar = valueArray[currentPosition];
                char character = (char) ft.getUnicodeEquivalent(cidChar);
                if (character == '\n') {
                    this.newlineSplit = true;
                    String returnValue = this.value.substring(currentPosition + 1);
                    this.value = this.value.substring(0, currentPosition);
                    if (this.value.length() < 1) {
                        this.value = "\u0001";
                    }
                    PdfChunk pc2 = new PdfChunk(returnValue, this);
                    return pc2;
                }
                currentWidth += this.font.width(cidChar);
                if (character == ' ') {
                    lastSpace = currentPosition + 1;
                    lastSpaceWidth = currentWidth;
                }
                if (currentWidth > width) {
                    break;
                }
                if (this.splitCharacter.isSplitCharacter(0, currentPosition, length, valueArray, thisChunk)) {
                    splitPosition = currentPosition + 1;
                }
                currentPosition++;
            }
        } else {
            while (currentPosition < length) {
                char character2 = valueArray[currentPosition];
                if (character2 == '\r' || character2 == '\n') {
                    this.newlineSplit = true;
                    int inc = 1;
                    if (character2 == '\r' && currentPosition + 1 < length && valueArray[currentPosition + 1] == '\n') {
                        inc = 2;
                    }
                    String returnValue2 = this.value.substring(currentPosition + inc);
                    this.value = this.value.substring(0, currentPosition);
                    if (this.value.length() < 1) {
                        this.value = " ";
                    }
                    PdfChunk pc3 = new PdfChunk(returnValue2, this);
                    return pc3;
                }
                boolean surrogate = Utilities.isSurrogatePair(valueArray, currentPosition);
                if (surrogate) {
                    currentWidth += this.font.width(Utilities.convertToUtf32(valueArray[currentPosition], valueArray[currentPosition + 1]));
                } else {
                    currentWidth += this.font.width(character2);
                }
                if (character2 == ' ') {
                    lastSpace = currentPosition + 1;
                    lastSpaceWidth = currentWidth;
                }
                if (surrogate) {
                    currentPosition++;
                }
                if (currentWidth > width) {
                    break;
                }
                if (this.splitCharacter.isSplitCharacter(0, currentPosition, length, valueArray, null)) {
                    splitPosition = currentPosition + 1;
                }
                currentPosition++;
            }
        }
        if (currentPosition == length) {
            return null;
        }
        if (splitPosition < 0) {
            String returnValue3 = this.value;
            this.value = "";
            PdfChunk pc4 = new PdfChunk(returnValue3, this);
            return pc4;
        }
        if (lastSpace > splitPosition && this.splitCharacter.isSplitCharacter(0, 0, 1, singleSpace, null)) {
            splitPosition = lastSpace;
        }
        if (hyphenationEvent != null && lastSpace >= 0 && lastSpace < currentPosition && (wordIdx = getWord(this.value, lastSpace)) > lastSpace) {
            String pre = hyphenationEvent.getHyphenatedWordPre(this.value.substring(lastSpace, wordIdx), this.font.getFont(), this.font.size(), width - lastSpaceWidth);
            String post = hyphenationEvent.getHyphenatedWordPost();
            if (pre.length() > 0) {
                String returnValue4 = post + this.value.substring(wordIdx);
                this.value = trim(this.value.substring(0, lastSpace) + pre);
                PdfChunk pc5 = new PdfChunk(returnValue4, this);
                return pc5;
            }
        }
        String returnValue5 = this.value.substring(splitPosition);
        this.value = trim(this.value.substring(0, splitPosition));
        PdfChunk pc6 = new PdfChunk(returnValue5, this);
        return pc6;
    }

    PdfChunk truncate(float width) {
        if (this.image != null) {
            if (this.image.getScaledWidth() > width) {
                PdfChunk pc = new PdfChunk("", this);
                this.value = "";
                this.attributes.remove(Chunk.IMAGE);
                this.image = null;
                this.font = PdfFont.getDefaultFont();
                return pc;
            }
            return null;
        }
        int currentPosition = 0;
        float currentWidth = 0.0f;
        if (width < this.font.width()) {
            String returnValue = this.value.substring(1);
            this.value = this.value.substring(0, 1);
            PdfChunk pc2 = new PdfChunk(returnValue, this);
            return pc2;
        }
        int length = this.value.length();
        boolean surrogate = false;
        while (currentPosition < length) {
            surrogate = Utilities.isSurrogatePair(this.value, currentPosition);
            if (surrogate) {
                currentWidth += this.font.width(Utilities.convertToUtf32(this.value, currentPosition));
            } else {
                currentWidth += this.font.width(this.value.charAt(currentPosition));
            }
            if (currentWidth > width) {
                break;
            }
            if (surrogate) {
                currentPosition++;
            }
            currentPosition++;
        }
        if (currentPosition == length) {
            return null;
        }
        if (currentPosition == 0) {
            currentPosition = 1;
            if (surrogate) {
                currentPosition = 1 + 1;
            }
        }
        String returnValue2 = this.value.substring(currentPosition);
        this.value = this.value.substring(0, currentPosition);
        PdfChunk pc3 = new PdfChunk(returnValue2, this);
        return pc3;
    }

    PdfFont font() {
        return this.font;
    }

    Color color() {
        return (Color) this.noStroke.get(Chunk.COLOR);
    }

    float width() {
        return this.font.width(this.value);
    }

    public boolean isNewlineSplit() {
        return this.newlineSplit;
    }

    float getWidthCorrected(float charSpacing, float wordSpacing) {
        if (this.image != null) {
            return this.image.getScaledWidth() + charSpacing;
        }
        int numberOfSpaces = 0;
        int idx = -1;
        while (true) {
            int iIndexOf = this.value.indexOf(32, idx + 1);
            idx = iIndexOf;
            if (iIndexOf >= 0) {
                numberOfSpaces++;
            } else {
                return width() + (this.value.length() * charSpacing) + (numberOfSpaces * wordSpacing);
            }
        }
    }

    public float getTextRise() {
        Float f = (Float) getAttribute(Chunk.SUBSUPSCRIPT);
        if (f != null) {
            return f.floatValue();
        }
        return ColumnText.GLOBAL_SPACE_CHAR_RATIO;
    }

    float trimLastSpace() {
        BaseFont ft = this.font.getFont();
        if (ft.getFontType() == 2 && ft.getUnicodeEquivalent(32) != 32) {
            if (this.value.length() > 1 && this.value.endsWith("\u0001")) {
                this.value = this.value.substring(0, this.value.length() - 1);
                return this.font.width(1);
            }
            return ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        }
        if (this.value.length() > 1 && this.value.endsWith(" ")) {
            this.value = this.value.substring(0, this.value.length() - 1);
            return this.font.width(32);
        }
        return ColumnText.GLOBAL_SPACE_CHAR_RATIO;
    }

    public float trimFirstSpace() {
        BaseFont ft = this.font.getFont();
        if (ft.getFontType() == 2 && ft.getUnicodeEquivalent(32) != 32) {
            if (this.value.length() > 1 && this.value.startsWith("\u0001")) {
                this.value = this.value.substring(1);
                return this.font.width(1);
            }
            return ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        }
        if (this.value.length() > 1 && this.value.startsWith(" ")) {
            this.value = this.value.substring(1);
            return this.font.width(32);
        }
        return ColumnText.GLOBAL_SPACE_CHAR_RATIO;
    }

    Object getAttribute(String name) {
        if (this.attributes.containsKey(name)) {
            return this.attributes.get(name);
        }
        return this.noStroke.get(name);
    }

    boolean isAttribute(String name) {
        if (this.attributes.containsKey(name)) {
            return true;
        }
        return this.noStroke.containsKey(name);
    }

    boolean isStroked() {
        return !this.attributes.isEmpty();
    }

    boolean isSeparator() {
        return isAttribute(Chunk.SEPARATOR);
    }

    boolean isHorizontalSeparator() {
        if (isAttribute(Chunk.SEPARATOR)) {
            Object[] o = (Object[]) getAttribute(Chunk.SEPARATOR);
            return !((Boolean) o[1]).booleanValue();
        }
        return false;
    }

    boolean isTab() {
        return isAttribute(Chunk.TAB);
    }

    void adjustLeft(float newValue) {
        Object[] o = (Object[]) this.attributes.get(Chunk.TAB);
        if (o != null) {
            this.attributes.put(Chunk.TAB, new Object[]{o[0], o[1], o[2], Float.valueOf(newValue)});
        }
    }

    boolean isImage() {
        return this.image != null;
    }

    Image getImage() {
        return this.image;
    }

    float getImageOffsetX() {
        return this.offsetX;
    }

    float getImageOffsetY() {
        return this.offsetY;
    }

    void setValue(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }

    boolean isSpecialEncoding() {
        return this.encoding.equals("UnicodeBigUnmarked") || this.encoding.equals(BaseFont.IDENTITY_H);
    }

    int length() {
        return this.value.length();
    }

    int lengthUtf32() {
        if (!BaseFont.IDENTITY_H.equals(this.encoding)) {
            return this.value.length();
        }
        int total = 0;
        int len = this.value.length();
        int k = 0;
        while (k < len) {
            if (Utilities.isSurrogateHigh(this.value.charAt(k))) {
                k++;
            }
            total++;
            k++;
        }
        return total;
    }

    boolean isExtSplitCharacter(int start, int current, int end, char[] cc, PdfChunk[] ck) {
        return this.splitCharacter.isSplitCharacter(start, current, end, cc, ck);
    }

    private String trim(String string) {
        BaseFont ft = this.font.getFont();
        if (ft.getFontType() != 2 || ft.getUnicodeEquivalent(32) == 32) {
            while (true) {
                if (!string.endsWith(" ") && !string.endsWith(TlbBase.TAB)) {
                    break;
                }
                string = string.substring(0, string.length() - 1);
            }
        } else {
            while (string.endsWith("\u0001")) {
                string = string.substring(0, string.length() - 1);
            }
        }
        return string;
    }

    float getCharWidth(int c) {
        if (noPrint(c)) {
            return ColumnText.GLOBAL_SPACE_CHAR_RATIO;
        }
        return this.font.width(c);
    }

    static boolean noPrint(int c) {
        return (c >= 8203 && c <= 8207) || (c >= 8234 && c <= 8238);
    }
}
