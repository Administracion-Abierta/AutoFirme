package org.apache.pdfbox.pdmodel.interactive.measurement;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.StandardStructureTypes;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDBorderStyleDictionary;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/measurement/PDNumberFormatDictionary.class */
public class PDNumberFormatDictionary implements COSObjectable {
    public static final String TYPE = "NumberFormat";
    public static final String LABEL_SUFFIX_TO_VALUE = "S";
    public static final String LABEL_PREFIX_TO_VALUE = "P";
    public static final String FRACTIONAL_DISPLAY_DECIMAL = "D";
    public static final String FRACTIONAL_DISPLAY_FRACTION = "F";
    public static final String FRACTIONAL_DISPLAY_ROUND = "R";
    public static final String FRACTIONAL_DISPLAY_TRUNCATE = "T";
    private COSDictionary numberFormatDictionary;

    public PDNumberFormatDictionary() {
        this.numberFormatDictionary = new COSDictionary();
        this.numberFormatDictionary.setName(COSName.TYPE, TYPE);
    }

    public PDNumberFormatDictionary(COSDictionary dictionary) {
        this.numberFormatDictionary = dictionary;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.numberFormatDictionary;
    }

    public String getType() {
        return TYPE;
    }

    public String getUnits() {
        return getCOSObject().getString(PDBorderStyleDictionary.STYLE_UNDERLINE);
    }

    public void setUnits(String units) {
        getCOSObject().setString(PDBorderStyleDictionary.STYLE_UNDERLINE, units);
    }

    public float getConversionFactor() {
        return getCOSObject().getFloat("C");
    }

    public void setConversionFactor(float conversionFactor) {
        getCOSObject().setFloat("C", conversionFactor);
    }

    public String getFractionalDisplay() {
        return getCOSObject().getString("F", "D");
    }

    public void setFractionalDisplay(String fractionalDisplay) {
        if (fractionalDisplay == null || "D".equals(fractionalDisplay) || "F".equals(fractionalDisplay) || "R".equals(fractionalDisplay) || FRACTIONAL_DISPLAY_TRUNCATE.equals(fractionalDisplay)) {
            getCOSObject().setString("F", fractionalDisplay);
            return;
        }
        throw new IllegalArgumentException("Value must be \"D\", \"F\", \"R\", or \"T\", (or null).");
    }

    public int getDenominator() {
        return getCOSObject().getInt("D");
    }

    public void setDenominator(int denominator) {
        getCOSObject().setInt("D", denominator);
    }

    public boolean isFD() {
        return getCOSObject().getBoolean("FD", false);
    }

    public void setFD(boolean fd) {
        getCOSObject().setBoolean("FD", fd);
    }

    public String getThousandsSeparator() {
        return getCOSObject().getString(StandardStructureTypes.RT, ",");
    }

    public void setThousandsSeparator(String thousandsSeparator) {
        getCOSObject().setString(StandardStructureTypes.RT, thousandsSeparator);
    }

    public String getDecimalSeparator() {
        return getCOSObject().getString("RD", ".");
    }

    public void setDecimalSeparator(String decimalSeparator) {
        getCOSObject().setString("RD", decimalSeparator);
    }

    public String getLabelPrefixString() {
        return getCOSObject().getString("PS", " ");
    }

    public void setLabelPrefixString(String labelPrefixString) {
        getCOSObject().setString("PS", labelPrefixString);
    }

    public String getLabelSuffixString() {
        return getCOSObject().getString("SS", " ");
    }

    public void setLabelSuffixString(String labelSuffixString) {
        getCOSObject().setString("SS", labelSuffixString);
    }

    public String getLabelPositionToValue() {
        return getCOSObject().getString(PDAnnotationLink.HIGHLIGHT_MODE_OUTLINE, "S");
    }

    public void setLabelPositionToValue(String labelPositionToValue) {
        if (labelPositionToValue == null || "P".equals(labelPositionToValue) || "S".equals(labelPositionToValue)) {
            getCOSObject().setString(PDAnnotationLink.HIGHLIGHT_MODE_OUTLINE, labelPositionToValue);
            return;
        }
        throw new IllegalArgumentException("Value must be \"S\", or \"P\" (or null).");
    }
}
