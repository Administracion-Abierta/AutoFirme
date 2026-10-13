package org.apache.commons.codec.language;

import java.util.Locale;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.pdfbox.pdmodel.common.PDPageLabelRange;
import org.apache.pdfbox.pdmodel.interactive.measurement.PDNumberFormatDictionary;

/* JADX INFO: loaded from: autofirma.jar:org/apache/commons/codec/language/Caverphone1.class */
public class Caverphone1 extends AbstractCaverphone {
    private static final String SIX_1 = "111111";

    @Override // org.apache.commons.codec.StringEncoder
    public String encode(String source) {
        if (source == null || source.isEmpty()) {
            return SIX_1;
        }
        String txt = source.toLowerCase(Locale.ENGLISH);
        return (txt.replaceAll("[^a-z]", "").replaceAll("^cough", "cou2f").replaceAll("^rough", "rou2f").replaceAll("^tough", "tou2f").replaceAll("^enough", "enou2f").replaceAll("^gn", "2n").replaceAll("mb$", "m2").replace("cq", "2q").replace("ci", "si").replace("ce", "se").replace("cy", "sy").replace("tch", "2ch").replace("c", OperatorName.NON_STROKING_CMYK).replace(OperatorName.SAVE, OperatorName.NON_STROKING_CMYK).replace("x", OperatorName.NON_STROKING_CMYK).replace(OperatorName.CURVE_TO_REPLICATE_INITIAL_POINT, OperatorName.FILL_NON_ZERO).replace("dg", "2g").replace("tio", "sio").replace("tia", "sia").replace(OperatorName.SET_LINE_DASHPATTERN, "t").replace("ph", "fh").replace(OperatorName.CLOSE_FILL_NON_ZERO_AND_STROKE, "p").replace(OperatorName.SHADING_FILL, "s2").replace("z", OperatorName.CLOSE_AND_STROKE).replaceAll("^[aeiou]", "A").replaceAll("[aeiou]", "3").replace("3gh3", "3kh3").replace("gh", "22").replace(OperatorName.NON_STROKING_GRAY, OperatorName.NON_STROKING_CMYK).replaceAll("s+", "S").replaceAll("t+", PDNumberFormatDictionary.FRACTIONAL_DISPLAY_TRUNCATE).replaceAll("p+", "P").replaceAll("k+", OperatorName.STROKING_COLOR_CMYK).replaceAll("f+", "F").replaceAll("m+", OperatorName.SET_LINE_MITERLIMIT).replaceAll("n+", "N").replace("w3", "W3").replace("wy", "Wy").replace("wh3", "Wh3").replace("why", "Why").replace(OperatorName.SET_LINE_WIDTH, "2").replaceAll("^h", "A").replace(OperatorName.CLOSE_PATH, "2").replace("r3", "R3").replace("ry", "Ry").replace(PDPageLabelRange.STYLE_ROMAN_LOWER, "2").replace("l3", "L3").replace("ly", "Ly").replace(OperatorName.LINE_TO, "2").replace(OperatorName.SET_LINE_JOINSTYLE, OperatorName.CURVE_TO_REPLICATE_FINAL_POINT).replace("y3", "Y3").replace(OperatorName.CURVE_TO_REPLICATE_FINAL_POINT, "2").replace("2", "").replace("3", "") + SIX_1).substring(0, SIX_1.length());
    }
}
