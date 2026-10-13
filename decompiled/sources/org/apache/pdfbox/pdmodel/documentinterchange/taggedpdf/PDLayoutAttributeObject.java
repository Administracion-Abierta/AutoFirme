package org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf;

import com.aowagie.text.pdf.ColumnText;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.color.PDGamma;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/documentinterchange/taggedpdf/PDLayoutAttributeObject.class */
public class PDLayoutAttributeObject extends PDStandardAttributeObject {
    public static final String OWNER_LAYOUT = "Layout";
    private static final String PLACEMENT = "Placement";
    private static final String WRITING_MODE = "WritingMode";
    private static final String BACKGROUND_COLOR = "BackgroundColor";
    private static final String BORDER_COLOR = "BorderColor";
    private static final String BORDER_STYLE = "BorderStyle";
    private static final String BORDER_THICKNESS = "BorderThickness";
    private static final String PADDING = "Padding";
    private static final String COLOR = "Color";
    private static final String SPACE_BEFORE = "SpaceBefore";
    private static final String SPACE_AFTER = "SpaceAfter";
    private static final String START_INDENT = "StartIndent";
    private static final String END_INDENT = "EndIndent";
    private static final String TEXT_INDENT = "TextIndent";
    private static final String TEXT_ALIGN = "TextAlign";
    private static final String BBOX = "BBox";
    private static final String WIDTH = "Width";
    private static final String HEIGHT = "Height";
    private static final String BLOCK_ALIGN = "BlockAlign";
    private static final String INLINE_ALIGN = "InlineAlign";
    private static final String T_BORDER_STYLE = "TBorderStyle";
    private static final String T_PADDING = "TPadding";
    private static final String BASELINE_SHIFT = "BaselineShift";
    private static final String LINE_HEIGHT = "LineHeight";
    private static final String TEXT_DECORATION_COLOR = "TextDecorationColor";
    private static final String TEXT_DECORATION_THICKNESS = "TextDecorationThickness";
    private static final String TEXT_DECORATION_TYPE = "TextDecorationType";
    private static final String RUBY_ALIGN = "RubyAlign";
    private static final String RUBY_POSITION = "RubyPosition";
    private static final String GLYPH_ORIENTATION_VERTICAL = "GlyphOrientationVertical";
    private static final String COLUMN_COUNT = "ColumnCount";
    private static final String COLUMN_GAP = "ColumnGap";
    private static final String COLUMN_WIDTHS = "ColumnWidths";
    public static final String PLACEMENT_BLOCK = "Block";
    public static final String PLACEMENT_INLINE = "Inline";
    public static final String PLACEMENT_BEFORE = "Before";
    public static final String PLACEMENT_START = "Start";
    public static final String PLACEMENT_END = "End";
    public static final String WRITING_MODE_LRTB = "LrTb";
    public static final String WRITING_MODE_RLTB = "RlTb";
    public static final String WRITING_MODE_TBRL = "TbRl";
    public static final String BORDER_STYLE_NONE = "None";
    public static final String BORDER_STYLE_HIDDEN = "Hidden";
    public static final String BORDER_STYLE_DOTTED = "Dotted";
    public static final String BORDER_STYLE_DASHED = "Dashed";
    public static final String BORDER_STYLE_SOLID = "Solid";
    public static final String BORDER_STYLE_DOUBLE = "Double";
    public static final String BORDER_STYLE_GROOVE = "Groove";
    public static final String BORDER_STYLE_RIDGE = "Ridge";
    public static final String BORDER_STYLE_INSET = "Inset";
    public static final String BORDER_STYLE_OUTSET = "Outset";
    public static final String TEXT_ALIGN_START = "Start";
    public static final String TEXT_ALIGN_CENTER = "Center";
    public static final String TEXT_ALIGN_END = "End";
    public static final String TEXT_ALIGN_JUSTIFY = "Justify";
    public static final String WIDTH_AUTO = "Auto";
    public static final String HEIGHT_AUTO = "Auto";
    public static final String BLOCK_ALIGN_BEFORE = "Before";
    public static final String BLOCK_ALIGN_MIDDLE = "Middle";
    public static final String BLOCK_ALIGN_AFTER = "After";
    public static final String BLOCK_ALIGN_JUSTIFY = "Justify";
    public static final String INLINE_ALIGN_START = "Start";
    public static final String INLINE_ALIGN_CENTER = "Center";
    public static final String INLINE_ALIGN_END = "End";
    public static final String LINE_HEIGHT_NORMAL = "Normal";
    public static final String LINE_HEIGHT_AUTO = "Auto";
    public static final String TEXT_DECORATION_TYPE_NONE = "None";
    public static final String TEXT_DECORATION_TYPE_UNDERLINE = "Underline";
    public static final String TEXT_DECORATION_TYPE_OVERLINE = "Overline";
    public static final String TEXT_DECORATION_TYPE_LINE_THROUGH = "LineThrough";
    public static final String RUBY_ALIGN_START = "Start";
    public static final String RUBY_ALIGN_CENTER = "Center";
    public static final String RUBY_ALIGN_END = "End";
    public static final String RUBY_ALIGN_JUSTIFY = "Justify";
    public static final String RUBY_ALIGN_DISTRIBUTE = "Distribute";
    public static final String RUBY_POSITION_BEFORE = "Before";
    public static final String RUBY_POSITION_AFTER = "After";
    public static final String RUBY_POSITION_WARICHU = "Warichu";
    public static final String RUBY_POSITION_INLINE = "Inline";
    public static final String GLYPH_ORIENTATION_VERTICAL_AUTO = "Auto";
    public static final String GLYPH_ORIENTATION_VERTICAL_MINUS_180_DEGREES = "-180";
    public static final String GLYPH_ORIENTATION_VERTICAL_MINUS_90_DEGREES = "-90";
    public static final String GLYPH_ORIENTATION_VERTICAL_ZERO_DEGREES = "0";
    public static final String GLYPH_ORIENTATION_VERTICAL_90_DEGREES = "90";
    public static final String GLYPH_ORIENTATION_VERTICAL_180_DEGREES = "180";
    public static final String GLYPH_ORIENTATION_VERTICAL_270_DEGREES = "270";
    public static final String GLYPH_ORIENTATION_VERTICAL_360_DEGREES = "360";

    public PDLayoutAttributeObject() {
        setOwner(OWNER_LAYOUT);
    }

    public PDLayoutAttributeObject(COSDictionary dictionary) {
        super(dictionary);
    }

    public String getPlacement() {
        return getName(PLACEMENT, "Inline");
    }

    public void setPlacement(String placement) {
        setName(PLACEMENT, placement);
    }

    public String getWritingMode() {
        return getName(WRITING_MODE, WRITING_MODE_LRTB);
    }

    public void setWritingMode(String writingMode) {
        setName(WRITING_MODE, writingMode);
    }

    public PDGamma getBackgroundColor() {
        return getColor(BACKGROUND_COLOR);
    }

    public void setBackgroundColor(PDGamma backgroundColor) {
        setColor(BACKGROUND_COLOR, backgroundColor);
    }

    public Object getBorderColors() {
        return getColorOrFourColors(BORDER_COLOR);
    }

    public void setAllBorderColors(PDGamma borderColor) {
        setColor(BORDER_COLOR, borderColor);
    }

    public void setBorderColors(PDFourColours borderColors) {
        setFourColors(BORDER_COLOR, borderColors);
    }

    public Object getBorderStyle() {
        return getNameOrArrayOfName(BORDER_STYLE, "None");
    }

    public void setAllBorderStyles(String borderStyle) {
        setName(BORDER_STYLE, borderStyle);
    }

    public void setBorderStyles(String[] borderStyles) {
        setArrayOfName(BORDER_STYLE, borderStyles);
    }

    public Object getBorderThickness() {
        return getNumberOrArrayOfNumber(BORDER_THICKNESS, -1.0f);
    }

    public void setAllBorderThicknesses(float borderThickness) {
        setNumber(BORDER_THICKNESS, borderThickness);
    }

    public void setAllBorderThicknesses(int borderThickness) {
        setNumber(BORDER_THICKNESS, borderThickness);
    }

    public void setBorderThicknesses(float[] borderThicknesses) {
        setArrayOfNumber(BORDER_THICKNESS, borderThicknesses);
    }

    public Object getPadding() {
        return getNumberOrArrayOfNumber(PADDING, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
    }

    public void setAllPaddings(float padding) {
        setNumber(PADDING, padding);
    }

    public void setAllPaddings(int padding) {
        setNumber(PADDING, padding);
    }

    public void setPaddings(float[] paddings) {
        setArrayOfNumber(PADDING, paddings);
    }

    public PDGamma getColor() {
        return getColor(COLOR);
    }

    public void setColor(PDGamma color) {
        setColor(COLOR, color);
    }

    public float getSpaceBefore() {
        return getNumber(SPACE_BEFORE, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
    }

    public void setSpaceBefore(float spaceBefore) {
        setNumber(SPACE_BEFORE, spaceBefore);
    }

    public void setSpaceBefore(int spaceBefore) {
        setNumber(SPACE_BEFORE, spaceBefore);
    }

    public float getSpaceAfter() {
        return getNumber(SPACE_AFTER, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
    }

    public void setSpaceAfter(float spaceAfter) {
        setNumber(SPACE_AFTER, spaceAfter);
    }

    public void setSpaceAfter(int spaceAfter) {
        setNumber(SPACE_AFTER, spaceAfter);
    }

    public float getStartIndent() {
        return getNumber(START_INDENT, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
    }

    public void setStartIndent(float startIndent) {
        setNumber(START_INDENT, startIndent);
    }

    public void setStartIndent(int startIndent) {
        setNumber(START_INDENT, startIndent);
    }

    public float getEndIndent() {
        return getNumber(END_INDENT, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
    }

    public void setEndIndent(float endIndent) {
        setNumber(END_INDENT, endIndent);
    }

    public void setEndIndent(int endIndent) {
        setNumber(END_INDENT, endIndent);
    }

    public float getTextIndent() {
        return getNumber(TEXT_INDENT, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
    }

    public void setTextIndent(float textIndent) {
        setNumber(TEXT_INDENT, textIndent);
    }

    public void setTextIndent(int textIndent) {
        setNumber(TEXT_INDENT, textIndent);
    }

    public String getTextAlign() {
        return getName(TEXT_ALIGN, "Start");
    }

    public void setTextAlign(String textIndent) {
        setName(TEXT_ALIGN, textIndent);
    }

    public PDRectangle getBBox() {
        COSArray array = (COSArray) getCOSObject().getDictionaryObject(BBOX);
        if (array != null) {
            return new PDRectangle(array);
        }
        return null;
    }

    public void setBBox(PDRectangle bbox) {
        COSBase oldValue = getCOSObject().getDictionaryObject(BBOX);
        getCOSObject().setItem(BBOX, bbox);
        COSBase newValue = bbox == null ? null : bbox.getCOSObject();
        potentiallyNotifyChanged(oldValue, newValue);
    }

    public Object getWidth() {
        return getNumberOrName(WIDTH, "Auto");
    }

    public void setWidthAuto() {
        setName(WIDTH, "Auto");
    }

    public void setWidth(float width) {
        setNumber(WIDTH, width);
    }

    public void setWidth(int width) {
        setNumber(WIDTH, width);
    }

    public Object getHeight() {
        return getNumberOrName(HEIGHT, "Auto");
    }

    public void setHeightAuto() {
        setName(HEIGHT, "Auto");
    }

    public void setHeight(float height) {
        setNumber(HEIGHT, height);
    }

    public void setHeight(int height) {
        setNumber(HEIGHT, height);
    }

    public String getBlockAlign() {
        return getName(BLOCK_ALIGN, "Before");
    }

    public void setBlockAlign(String blockAlign) {
        setName(BLOCK_ALIGN, blockAlign);
    }

    public String getInlineAlign() {
        return getName(INLINE_ALIGN, "Start");
    }

    public void setInlineAlign(String inlineAlign) {
        setName(INLINE_ALIGN, inlineAlign);
    }

    public Object getTBorderStyle() {
        return getNameOrArrayOfName(T_BORDER_STYLE, "None");
    }

    public void setAllTBorderStyles(String tBorderStyle) {
        setName(T_BORDER_STYLE, tBorderStyle);
    }

    public void setTBorderStyles(String[] tBorderStyles) {
        setArrayOfName(T_BORDER_STYLE, tBorderStyles);
    }

    public Object getTPadding() {
        return getNumberOrArrayOfNumber(T_PADDING, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
    }

    public void setAllTPaddings(float tPadding) {
        setNumber(T_PADDING, tPadding);
    }

    public void setAllTPaddings(int tPadding) {
        setNumber(T_PADDING, tPadding);
    }

    public void setTPaddings(float[] tPaddings) {
        setArrayOfNumber(T_PADDING, tPaddings);
    }

    public float getBaselineShift() {
        return getNumber(BASELINE_SHIFT, ColumnText.GLOBAL_SPACE_CHAR_RATIO);
    }

    public void setBaselineShift(float baselineShift) {
        setNumber(BASELINE_SHIFT, baselineShift);
    }

    public void setBaselineShift(int baselineShift) {
        setNumber(BASELINE_SHIFT, baselineShift);
    }

    public Object getLineHeight() {
        return getNumberOrName(LINE_HEIGHT, LINE_HEIGHT_NORMAL);
    }

    public void setLineHeightNormal() {
        setName(LINE_HEIGHT, LINE_HEIGHT_NORMAL);
    }

    public void setLineHeightAuto() {
        setName(LINE_HEIGHT, "Auto");
    }

    public void setLineHeight(float lineHeight) {
        setNumber(LINE_HEIGHT, lineHeight);
    }

    public void setLineHeight(int lineHeight) {
        setNumber(LINE_HEIGHT, lineHeight);
    }

    public PDGamma getTextDecorationColor() {
        return getColor(TEXT_DECORATION_COLOR);
    }

    public void setTextDecorationColor(PDGamma textDecorationColor) {
        setColor(TEXT_DECORATION_COLOR, textDecorationColor);
    }

    public float getTextDecorationThickness() {
        return getNumber(TEXT_DECORATION_THICKNESS);
    }

    public void setTextDecorationThickness(float textDecorationThickness) {
        setNumber(TEXT_DECORATION_THICKNESS, textDecorationThickness);
    }

    public void setTextDecorationThickness(int textDecorationThickness) {
        setNumber(TEXT_DECORATION_THICKNESS, textDecorationThickness);
    }

    public String getTextDecorationType() {
        return getName(TEXT_DECORATION_TYPE, "None");
    }

    public void setTextDecorationType(String textDecorationType) {
        setName(TEXT_DECORATION_TYPE, textDecorationType);
    }

    public String getRubyAlign() {
        return getName(RUBY_ALIGN, RUBY_ALIGN_DISTRIBUTE);
    }

    public void setRubyAlign(String rubyAlign) {
        setName(RUBY_ALIGN, rubyAlign);
    }

    public String getRubyPosition() {
        return getName(RUBY_POSITION, "Before");
    }

    public void setRubyPosition(String rubyPosition) {
        setName(RUBY_POSITION, rubyPosition);
    }

    public String getGlyphOrientationVertical() {
        return getName(GLYPH_ORIENTATION_VERTICAL, "Auto");
    }

    public void setGlyphOrientationVertical(String glyphOrientationVertical) {
        setName(GLYPH_ORIENTATION_VERTICAL, glyphOrientationVertical);
    }

    public int getColumnCount() {
        return getInteger(COLUMN_COUNT, 1);
    }

    public void setColumnCount(int columnCount) {
        setInteger(COLUMN_COUNT, columnCount);
    }

    public Object getColumnGap() {
        return getNumberOrArrayOfNumber(COLUMN_GAP, -1.0f);
    }

    public void setColumnGap(float columnGap) {
        setNumber(COLUMN_GAP, columnGap);
    }

    public void setColumnGap(int columnGap) {
        setNumber(COLUMN_GAP, columnGap);
    }

    public void setColumnGaps(float[] columnGaps) {
        setArrayOfNumber(COLUMN_GAP, columnGaps);
    }

    public Object getColumnWidths() {
        return getNumberOrArrayOfNumber(COLUMN_WIDTHS, -1.0f);
    }

    public void setAllColumnWidths(float columnWidth) {
        setNumber(COLUMN_WIDTHS, columnWidth);
    }

    public void setAllColumnWidths(int columnWidth) {
        setNumber(COLUMN_WIDTHS, columnWidth);
    }

    public void setColumnWidths(float[] columnWidths) {
        setArrayOfNumber(COLUMN_WIDTHS, columnWidths);
    }

    @Override // org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDAttributeObject
    public String toString() {
        StringBuilder sb = new StringBuilder().append(super.toString());
        if (isSpecified(PLACEMENT)) {
            sb.append(", Placement=").append(getPlacement());
        }
        if (isSpecified(WRITING_MODE)) {
            sb.append(", WritingMode=").append(getWritingMode());
        }
        if (isSpecified(BACKGROUND_COLOR)) {
            sb.append(", BackgroundColor=").append(getBackgroundColor());
        }
        if (isSpecified(BORDER_COLOR)) {
            sb.append(", BorderColor=").append(getBorderColors());
        }
        if (isSpecified(BORDER_STYLE)) {
            Object borderStyle = getBorderStyle();
            sb.append(", BorderStyle=");
            if (borderStyle instanceof String[]) {
                sb.append(arrayToString((String[]) borderStyle));
            } else {
                sb.append(borderStyle);
            }
        }
        if (isSpecified(BORDER_THICKNESS)) {
            Object borderThickness = getBorderThickness();
            sb.append(", BorderThickness=");
            if (borderThickness instanceof float[]) {
                sb.append(arrayToString((float[]) borderThickness));
            } else {
                sb.append(borderThickness);
            }
        }
        if (isSpecified(PADDING)) {
            Object padding = getPadding();
            sb.append(", Padding=");
            if (padding instanceof float[]) {
                sb.append(arrayToString((float[]) padding));
            } else {
                sb.append(padding);
            }
        }
        if (isSpecified(COLOR)) {
            sb.append(", Color=").append(getColor());
        }
        if (isSpecified(SPACE_BEFORE)) {
            sb.append(", SpaceBefore=").append(getSpaceBefore());
        }
        if (isSpecified(SPACE_AFTER)) {
            sb.append(", SpaceAfter=").append(getSpaceAfter());
        }
        if (isSpecified(START_INDENT)) {
            sb.append(", StartIndent=").append(getStartIndent());
        }
        if (isSpecified(END_INDENT)) {
            sb.append(", EndIndent=").append(getEndIndent());
        }
        if (isSpecified(TEXT_INDENT)) {
            sb.append(", TextIndent=").append(getTextIndent());
        }
        if (isSpecified(TEXT_ALIGN)) {
            sb.append(", TextAlign=").append(getTextAlign());
        }
        if (isSpecified(BBOX)) {
            sb.append(", BBox=").append(getBBox());
        }
        if (isSpecified(WIDTH)) {
            sb.append(", Width=").append(getWidth());
        }
        if (isSpecified(HEIGHT)) {
            sb.append(", Height=").append(getHeight());
        }
        if (isSpecified(BLOCK_ALIGN)) {
            sb.append(", BlockAlign=").append(getBlockAlign());
        }
        if (isSpecified(INLINE_ALIGN)) {
            sb.append(", InlineAlign=").append(getInlineAlign());
        }
        if (isSpecified(T_BORDER_STYLE)) {
            Object tBorderStyle = getTBorderStyle();
            sb.append(", TBorderStyle=");
            if (tBorderStyle instanceof String[]) {
                sb.append(arrayToString((String[]) tBorderStyle));
            } else {
                sb.append(tBorderStyle);
            }
        }
        if (isSpecified(T_PADDING)) {
            Object tPadding = getTPadding();
            sb.append(", TPadding=");
            if (tPadding instanceof float[]) {
                sb.append(arrayToString((float[]) tPadding));
            } else {
                sb.append(tPadding);
            }
        }
        if (isSpecified(BASELINE_SHIFT)) {
            sb.append(", BaselineShift=").append(getBaselineShift());
        }
        if (isSpecified(LINE_HEIGHT)) {
            sb.append(", LineHeight=").append(getLineHeight());
        }
        if (isSpecified(TEXT_DECORATION_COLOR)) {
            sb.append(", TextDecorationColor=").append(getTextDecorationColor());
        }
        if (isSpecified(TEXT_DECORATION_THICKNESS)) {
            sb.append(", TextDecorationThickness=").append(getTextDecorationThickness());
        }
        if (isSpecified(TEXT_DECORATION_TYPE)) {
            sb.append(", TextDecorationType=").append(getTextDecorationType());
        }
        if (isSpecified(RUBY_ALIGN)) {
            sb.append(", RubyAlign=").append(getRubyAlign());
        }
        if (isSpecified(RUBY_POSITION)) {
            sb.append(", RubyPosition=").append(getRubyPosition());
        }
        if (isSpecified(GLYPH_ORIENTATION_VERTICAL)) {
            sb.append(", GlyphOrientationVertical=").append(getGlyphOrientationVertical());
        }
        if (isSpecified(COLUMN_COUNT)) {
            sb.append(", ColumnCount=").append(getColumnCount());
        }
        if (isSpecified(COLUMN_GAP)) {
            Object columnGap = getColumnGap();
            sb.append(", ColumnGap=");
            if (columnGap instanceof float[]) {
                sb.append(arrayToString((float[]) columnGap));
            } else {
                sb.append(columnGap);
            }
        }
        if (isSpecified(COLUMN_WIDTHS)) {
            Object columnWidth = getColumnWidths();
            sb.append(", ColumnWidths=");
            if (columnWidth instanceof float[]) {
                sb.append(arrayToString((float[]) columnWidth));
            } else {
                sb.append(columnWidth);
            }
        }
        return sb.toString();
    }
}
