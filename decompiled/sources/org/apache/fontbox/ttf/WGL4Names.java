package org.apache.fontbox.ttf;

import com.aowagie.text.ElementTags;
import com.aowagie.text.pdf.BaseFont;
import java.util.HashMap;
import java.util.Map;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.pdmodel.common.PDPageLabelRange;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.StandardStructureTypes;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.apache.pdfbox.pdmodel.interactive.measurement.PDNumberFormatDictionary;
import org.apache.xml.security.utils.Constants;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/WGL4Names.class */
public final class WGL4Names {
    public static final int NUMBER_OF_MAC_GLYPHS = 258;
    public static final String[] MAC_GLYPH_NAMES = {BaseFont.notdef, ".null", "nonmarkingreturn", "space", "exclam", "quotedbl", "numbersign", "dollar", "percent", "ampersand", "quotesingle", "parenleft", "parenright", "asterisk", "plus", "comma", "hyphen", "period", "slash", "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "colon", "semicolon", "less", "equal", "greater", "question", "at", "A", "B", "C", "D", "E", "F", "G", StandardStructureTypes.H, "I", "J", OperatorName.STROKING_COLOR_CMYK, "L", OperatorName.SET_LINE_MITERLIMIT, "N", PDAnnotationLink.HIGHLIGHT_MODE_OUTLINE, "P", "Q", "R", "S", PDNumberFormatDictionary.FRACTIONAL_DISPLAY_TRUNCATE, PDBorderStyleDictionary.STYLE_UNDERLINE, "V", "W", "X", Constants._TAG_Y, "Z", "bracketleft", "backslash", "bracketright", "asciicircum", "underscore", "grave", PDPageLabelRange.STYLE_LETTERS_LOWER, OperatorName.CLOSE_FILL_NON_ZERO_AND_STROKE, "c", OperatorName.SET_LINE_DASHPATTERN, "e", OperatorName.FILL_NON_ZERO, OperatorName.NON_STROKING_GRAY, OperatorName.CLOSE_PATH, OperatorName.SET_FLATNESS, OperatorName.SET_LINE_JOINSTYLE, OperatorName.NON_STROKING_CMYK, OperatorName.LINE_TO, OperatorName.MOVE_TO, OperatorName.ENDPATH, "o", "p", OperatorName.SAVE, PDPageLabelRange.STYLE_ROMAN_LOWER, OperatorName.CLOSE_AND_STROKE, "t", "u", OperatorName.CURVE_TO_REPLICATE_INITIAL_POINT, OperatorName.SET_LINE_WIDTH, "x", OperatorName.CURVE_TO_REPLICATE_FINAL_POINT, "z", "braceleft", "bar", "braceright", "asciitilde", "Adieresis", "Aring", "Ccedilla", "Eacute", "Ntilde", "Odieresis", "Udieresis", "aacute", "agrave", "acircumflex", "adieresis", "atilde", "aring", "ccedilla", "eacute", "egrave", "ecircumflex", "edieresis", "iacute", "igrave", "icircumflex", "idieresis", "ntilde", "oacute", "ograve", "ocircumflex", "odieresis", "otilde", "uacute", "ugrave", "ucircumflex", "udieresis", "dagger", "degree", "cent", "sterling", ElementTags.SECTION, "bullet", ElementTags.PARAGRAPH, "germandbls", "registered", "copyright", "trademark", "acute", "dieresis", "notequal", "AE", "Oslash", "infinity", "plusminus", "lessequal", "greaterequal", "yen", "mu", "partialdiff", "summation", "product", "pi", "integral", "ordfeminine", "ordmasculine", "Omega", "ae", "oslash", "questiondown", "exclamdown", "logicalnot", "radical", "florin", "approxequal", "Delta", "guillemotleft", "guillemotright", "ellipsis", "nonbreakingspace", "Agrave", "Atilde", "Otilde", "OE", "oe", "endash", "emdash", "quotedblleft", "quotedblright", "quoteleft", "quoteright", "divide", "lozenge", "ydieresis", "Ydieresis", "fraction", "currency", "guilsinglleft", "guilsinglright", "fi", "fl", "daggerdbl", "periodcentered", "quotesinglbase", "quotedblbase", "perthousand", "Acircumflex", "Ecircumflex", "Aacute", "Edieresis", "Egrave", "Iacute", "Icircumflex", "Idieresis", "Igrave", "Oacute", "Ocircumflex", "apple", "Ograve", "Uacute", "Ucircumflex", "Ugrave", "dotlessi", "circumflex", "tilde", "macron", "breve", "dotaccent", "ring", "cedilla", "hungarumlaut", "ogonek", "caron", "Lslash", "lslash", "Scaron", "scaron", "Zcaron", "zcaron", "brokenbar", "Eth", "eth", "Yacute", "yacute", "Thorn", "thorn", "minus", "multiply", "onesuperior", "twosuperior", "threesuperior", "onehalf", "onequarter", "threequarters", "franc", "Gbreve", "gbreve", "Idotaccent", "Scedilla", "scedilla", "Cacute", "cacute", "Ccaron", "ccaron", "dcroat"};
    public static final Map<String, Integer> MAC_GLYPH_NAMES_INDICES = new HashMap(258);

    static {
        for (int i = 0; i < 258; i++) {
            MAC_GLYPH_NAMES_INDICES.put(MAC_GLYPH_NAMES[i], Integer.valueOf(i));
        }
    }

    private WGL4Names() {
    }
}
