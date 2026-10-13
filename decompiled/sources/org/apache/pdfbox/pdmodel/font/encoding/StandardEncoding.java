package org.apache.pdfbox.pdmodel.font.encoding;

import com.aowagie.text.ElementTags;
import com.sun.jna.platform.win32.WinError;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.PDPageLabelRange;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.StandardStructureTypes;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;
import org.apache.pdfbox.pdmodel.interactive.measurement.PDNumberFormatDictionary;
import org.apache.xml.security.utils.Constants;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/font/encoding/StandardEncoding.class */
public class StandardEncoding extends Encoding {
    private static final Object[][] STANDARD_ENCODING_TABLE = {new Object[]{65, "A"}, new Object[]{Integer.valueOf(WinError.ERROR_VIRUS_INFECTED), "AE"}, new Object[]{66, "B"}, new Object[]{67, "C"}, new Object[]{68, "D"}, new Object[]{69, "E"}, new Object[]{70, "F"}, new Object[]{71, "G"}, new Object[]{72, StandardStructureTypes.H}, new Object[]{73, "I"}, new Object[]{74, "J"}, new Object[]{75, OperatorName.STROKING_COLOR_CMYK}, new Object[]{76, "L"}, new Object[]{Integer.valueOf(WinError.ERROR_NO_DATA), "Lslash"}, new Object[]{77, OperatorName.SET_LINE_MITERLIMIT}, new Object[]{78, "N"}, new Object[]{79, PDAnnotationLink.HIGHLIGHT_MODE_OUTLINE}, new Object[]{Integer.valueOf(WinError.ERROR_MORE_DATA), "OE"}, new Object[]{Integer.valueOf(WinError.ERROR_PIPE_NOT_CONNECTED), "Oslash"}, new Object[]{80, "P"}, new Object[]{81, "Q"}, new Object[]{82, "R"}, new Object[]{83, "S"}, new Object[]{84, PDNumberFormatDictionary.FRACTIONAL_DISPLAY_TRUNCATE}, new Object[]{85, PDBorderStyleDictionary.STYLE_UNDERLINE}, new Object[]{86, "V"}, new Object[]{87, "W"}, new Object[]{88, "X"}, new Object[]{89, Constants._TAG_Y}, new Object[]{90, "Z"}, new Object[]{97, PDPageLabelRange.STYLE_LETTERS_LOWER}, new Object[]{194, "acute"}, new Object[]{241, "ae"}, new Object[]{38, "ampersand"}, new Object[]{94, "asciicircum"}, new Object[]{126, "asciitilde"}, new Object[]{42, "asterisk"}, new Object[]{64, "at"}, new Object[]{98, OperatorName.CLOSE_FILL_NON_ZERO_AND_STROKE}, new Object[]{92, "backslash"}, new Object[]{124, "bar"}, new Object[]{123, "braceleft"}, new Object[]{125, "braceright"}, new Object[]{91, "bracketleft"}, new Object[]{93, "bracketright"}, new Object[]{198, "breve"}, new Object[]{183, "bullet"}, new Object[]{99, "c"}, new Object[]{Integer.valueOf(WinError.ERROR_RING2_STACK_IN_USE), "caron"}, new Object[]{203, "cedilla"}, new Object[]{162, "cent"}, new Object[]{195, "circumflex"}, new Object[]{58, "colon"}, new Object[]{44, "comma"}, new Object[]{168, "currency"}, new Object[]{100, OperatorName.SET_LINE_DASHPATTERN}, new Object[]{178, "dagger"}, new Object[]{179, "daggerdbl"}, new Object[]{200, "dieresis"}, new Object[]{36, "dollar"}, new Object[]{199, "dotaccent"}, new Object[]{245, "dotlessi"}, new Object[]{101, "e"}, new Object[]{56, "eight"}, new Object[]{188, "ellipsis"}, new Object[]{Integer.valueOf(WinError.ERROR_META_EXPANSION_TOO_LONG), "emdash"}, new Object[]{177, "endash"}, new Object[]{61, "equal"}, new Object[]{33, "exclam"}, new Object[]{161, "exclamdown"}, new Object[]{102, OperatorName.FILL_NON_ZERO}, new Object[]{174, "fi"}, new Object[]{53, "five"}, new Object[]{175, "fl"}, new Object[]{166, "florin"}, new Object[]{52, "four"}, new Object[]{164, "fraction"}, new Object[]{103, OperatorName.NON_STROKING_GRAY}, new Object[]{251, "germandbls"}, new Object[]{193, "grave"}, new Object[]{62, "greater"}, new Object[]{171, "guillemotleft"}, new Object[]{187, "guillemotright"}, new Object[]{172, "guilsinglleft"}, new Object[]{173, "guilsinglright"}, new Object[]{104, OperatorName.CLOSE_PATH}, new Object[]{205, "hungarumlaut"}, new Object[]{45, "hyphen"}, new Object[]{105, OperatorName.SET_FLATNESS}, new Object[]{106, OperatorName.SET_LINE_JOINSTYLE}, new Object[]{107, OperatorName.NON_STROKING_CMYK}, new Object[]{108, OperatorName.LINE_TO}, new Object[]{60, "less"}, new Object[]{248, "lslash"}, new Object[]{109, OperatorName.MOVE_TO}, new Object[]{197, "macron"}, new Object[]{110, OperatorName.ENDPATH}, new Object[]{57, "nine"}, new Object[]{35, "numbersign"}, new Object[]{111, "o"}, new Object[]{250, "oe"}, new Object[]{Integer.valueOf(WinError.ERROR_FILENAME_EXCED_RANGE), "ogonek"}, new Object[]{49, "one"}, new Object[]{227, "ordfeminine"}, new Object[]{235, "ordmasculine"}, new Object[]{249, "oslash"}, new Object[]{112, "p"}, new Object[]{182, ElementTags.PARAGRAPH}, new Object[]{40, "parenleft"}, new Object[]{41, "parenright"}, new Object[]{37, "percent"}, new Object[]{46, "period"}, new Object[]{180, "periodcentered"}, new Object[]{189, "perthousand"}, new Object[]{43, "plus"}, new Object[]{113, OperatorName.SAVE}, new Object[]{63, "question"}, new Object[]{191, "questiondown"}, new Object[]{34, "quotedbl"}, new Object[]{185, "quotedblbase"}, new Object[]{170, "quotedblleft"}, new Object[]{186, "quotedblright"}, new Object[]{96, "quoteleft"}, new Object[]{39, "quoteright"}, new Object[]{184, "quotesinglbase"}, new Object[]{169, "quotesingle"}, new Object[]{114, PDPageLabelRange.STYLE_ROMAN_LOWER}, new Object[]{202, "ring"}, new Object[]{115, OperatorName.CLOSE_AND_STROKE}, new Object[]{167, ElementTags.SECTION}, new Object[]{59, "semicolon"}, new Object[]{55, "seven"}, new Object[]{54, "six"}, new Object[]{47, "slash"}, new Object[]{32, "space"}, new Object[]{163, "sterling"}, new Object[]{116, "t"}, new Object[]{51, "three"}, new Object[]{196, "tilde"}, new Object[]{50, "two"}, new Object[]{117, "u"}, new Object[]{95, "underscore"}, new Object[]{118, OperatorName.CURVE_TO_REPLICATE_INITIAL_POINT}, new Object[]{119, OperatorName.SET_LINE_WIDTH}, new Object[]{120, "x"}, new Object[]{121, OperatorName.CURVE_TO_REPLICATE_FINAL_POINT}, new Object[]{165, "yen"}, new Object[]{122, "z"}, new Object[]{48, "zero"}};
    public static final StandardEncoding INSTANCE = new StandardEncoding();

    public StandardEncoding() {
        for (Object[] encodingEntry : STANDARD_ENCODING_TABLE) {
            add(((Integer) encodingEntry[0]).intValue(), encodingEntry[1].toString());
        }
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSBase getCOSObject() {
        return COSName.STANDARD_ENCODING;
    }

    @Override // org.apache.pdfbox.pdmodel.font.encoding.Encoding
    public String getEncodingName() {
        return "StandardEncoding";
    }
}
