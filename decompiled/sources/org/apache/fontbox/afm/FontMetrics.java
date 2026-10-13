package org.apache.fontbox.afm;

import com.aowagie.text.pdf.ColumnText;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.fontbox.util.BoundingBox;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/afm/FontMetrics.class */
public class FontMetrics {
    private float afmVersion;
    private String fontName;
    private String fullName;
    private String familyName;
    private String weight;
    private BoundingBox fontBBox;
    private String fontVersion;
    private String notice;
    private String encodingScheme;
    private int mappingScheme;
    private int escChar;
    private String characterSet;
    private int characters;
    private boolean isBaseFont;
    private float[] vVector;
    private boolean isFixedV;
    private float capHeight;
    private float xHeight;
    private float ascender;
    private float descender;
    private float underlinePosition;
    private float underlineThickness;
    private float italicAngle;
    private float[] charWidth;
    private boolean isFixedPitch;
    private float standardHorizontalWidth;
    private float standardVerticalWidth;
    private int metricSets = 0;
    private final List<String> comments = new ArrayList();
    private List<CharMetric> charMetrics = new ArrayList();
    private Map<String, CharMetric> charMetricsMap = new HashMap();
    private List<TrackKern> trackKern = new ArrayList();
    private List<Composite> composites = new ArrayList();
    private List<KernPair> kernPairs = new ArrayList();
    private List<KernPair> kernPairs0 = new ArrayList();
    private List<KernPair> kernPairs1 = new ArrayList();

    public float getCharacterWidth(String name) {
        float result = 0.0f;
        CharMetric metric = this.charMetricsMap.get(name);
        if (metric != null) {
            result = metric.getWx();
        }
        return result;
    }

    public float getCharacterHeight(String name) {
        float result = 0.0f;
        CharMetric metric = this.charMetricsMap.get(name);
        if (metric != null) {
            result = metric.getWy();
            if (result == ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                result = metric.getBoundingBox().getHeight();
            }
        }
        return result;
    }

    public float getAverageCharacterWidth() {
        float average = 0.0f;
        float totalWidths = 0.0f;
        float characterCount = 0.0f;
        for (CharMetric metric : this.charMetrics) {
            if (metric.getWx() > ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
                totalWidths += metric.getWx();
                characterCount += 1.0f;
            }
        }
        if (totalWidths > ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            average = totalWidths / characterCount;
        }
        return average;
    }

    public void addComment(String comment) {
        this.comments.add(comment);
    }

    public List<String> getComments() {
        return Collections.unmodifiableList(this.comments);
    }

    public float getAFMVersion() {
        return this.afmVersion;
    }

    public int getMetricSets() {
        return this.metricSets;
    }

    public void setAFMVersion(float afmVersionValue) {
        this.afmVersion = afmVersionValue;
    }

    public void setMetricSets(int metricSetsValue) {
        if (metricSetsValue < 0 || metricSetsValue > 2) {
            throw new IllegalArgumentException("The metricSets attribute must be in the set {0,1,2} and not '" + metricSetsValue + OperatorName.SHOW_TEXT_LINE);
        }
        this.metricSets = metricSetsValue;
    }

    public String getFontName() {
        return this.fontName;
    }

    public void setFontName(String name) {
        this.fontName = name;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullNameValue) {
        this.fullName = fullNameValue;
    }

    public String getFamilyName() {
        return this.familyName;
    }

    public void setFamilyName(String familyNameValue) {
        this.familyName = familyNameValue;
    }

    public String getWeight() {
        return this.weight;
    }

    public void setWeight(String weightValue) {
        this.weight = weightValue;
    }

    public BoundingBox getFontBBox() {
        return this.fontBBox;
    }

    public void setFontBBox(BoundingBox bBox) {
        this.fontBBox = bBox;
    }

    public String getNotice() {
        return this.notice;
    }

    public void setNotice(String noticeValue) {
        this.notice = noticeValue;
    }

    public String getEncodingScheme() {
        return this.encodingScheme;
    }

    public void setEncodingScheme(String encodingSchemeValue) {
        this.encodingScheme = encodingSchemeValue;
    }

    public int getMappingScheme() {
        return this.mappingScheme;
    }

    public void setMappingScheme(int mappingSchemeValue) {
        this.mappingScheme = mappingSchemeValue;
    }

    public int getEscChar() {
        return this.escChar;
    }

    public void setEscChar(int escCharValue) {
        this.escChar = escCharValue;
    }

    public String getCharacterSet() {
        return this.characterSet;
    }

    public void setCharacterSet(String characterSetValue) {
        this.characterSet = characterSetValue;
    }

    public int getCharacters() {
        return this.characters;
    }

    public void setCharacters(int charactersValue) {
        this.characters = charactersValue;
    }

    public boolean isBaseFont() {
        return this.isBaseFont;
    }

    public void setIsBaseFont(boolean isBaseFontValue) {
        this.isBaseFont = isBaseFontValue;
    }

    public float[] getVVector() {
        return this.vVector;
    }

    public void setVVector(float[] vVectorValue) {
        this.vVector = vVectorValue;
    }

    public boolean isFixedV() {
        return this.isFixedV;
    }

    public void setIsFixedV(boolean isFixedVValue) {
        this.isFixedV = isFixedVValue;
    }

    public float getCapHeight() {
        return this.capHeight;
    }

    public void setCapHeight(float capHeightValue) {
        this.capHeight = capHeightValue;
    }

    public float getXHeight() {
        return this.xHeight;
    }

    public void setXHeight(float xHeightValue) {
        this.xHeight = xHeightValue;
    }

    public float getAscender() {
        return this.ascender;
    }

    public void setAscender(float ascenderValue) {
        this.ascender = ascenderValue;
    }

    public float getDescender() {
        return this.descender;
    }

    public void setDescender(float descenderValue) {
        this.descender = descenderValue;
    }

    public String getFontVersion() {
        return this.fontVersion;
    }

    public void setFontVersion(String fontVersionValue) {
        this.fontVersion = fontVersionValue;
    }

    public float getUnderlinePosition() {
        return this.underlinePosition;
    }

    public void setUnderlinePosition(float underlinePositionValue) {
        this.underlinePosition = underlinePositionValue;
    }

    public float getUnderlineThickness() {
        return this.underlineThickness;
    }

    public void setUnderlineThickness(float underlineThicknessValue) {
        this.underlineThickness = underlineThicknessValue;
    }

    public float getItalicAngle() {
        return this.italicAngle;
    }

    public void setItalicAngle(float italicAngleValue) {
        this.italicAngle = italicAngleValue;
    }

    public float[] getCharWidth() {
        return this.charWidth;
    }

    public void setCharWidth(float[] charWidthValue) {
        this.charWidth = charWidthValue;
    }

    public boolean isFixedPitch() {
        return this.isFixedPitch;
    }

    public void setFixedPitch(boolean isFixedPitchValue) {
        this.isFixedPitch = isFixedPitchValue;
    }

    public List<CharMetric> getCharMetrics() {
        return Collections.unmodifiableList(this.charMetrics);
    }

    public void setCharMetrics(List<CharMetric> charMetricsValue) {
        this.charMetrics = charMetricsValue;
        this.charMetricsMap = new HashMap(this.charMetrics.size());
        for (CharMetric metric : charMetricsValue) {
            this.charMetricsMap.put(metric.getName(), metric);
        }
    }

    public void addCharMetric(CharMetric metric) {
        this.charMetrics.add(metric);
        this.charMetricsMap.put(metric.getName(), metric);
    }

    public List<TrackKern> getTrackKern() {
        return Collections.unmodifiableList(this.trackKern);
    }

    public void setTrackKern(List<TrackKern> trackKernValue) {
        this.trackKern = trackKernValue;
    }

    public void addTrackKern(TrackKern kern) {
        this.trackKern.add(kern);
    }

    public List<Composite> getComposites() {
        return Collections.unmodifiableList(this.composites);
    }

    public void setComposites(List<Composite> compositesList) {
        this.composites = compositesList;
    }

    public void addComposite(Composite composite) {
        this.composites.add(composite);
    }

    public List<KernPair> getKernPairs() {
        return Collections.unmodifiableList(this.kernPairs);
    }

    public void addKernPair(KernPair kernPair) {
        this.kernPairs.add(kernPair);
    }

    public void setKernPairs(List<KernPair> kernPairsList) {
        this.kernPairs = kernPairsList;
    }

    public List<KernPair> getKernPairs0() {
        return Collections.unmodifiableList(this.kernPairs0);
    }

    public void addKernPair0(KernPair kernPair) {
        this.kernPairs0.add(kernPair);
    }

    public void setKernPairs0(List<KernPair> kernPairs0List) {
        this.kernPairs0 = kernPairs0List;
    }

    public List<KernPair> getKernPairs1() {
        return Collections.unmodifiableList(this.kernPairs1);
    }

    public void addKernPair1(KernPair kernPair) {
        this.kernPairs1.add(kernPair);
    }

    public void setKernPairs1(List<KernPair> kernPairs1List) {
        this.kernPairs1 = kernPairs1List;
    }

    public float getStandardHorizontalWidth() {
        return this.standardHorizontalWidth;
    }

    public void setStandardHorizontalWidth(float standardHorizontalWidthValue) {
        this.standardHorizontalWidth = standardHorizontalWidthValue;
    }

    public float getStandardVerticalWidth() {
        return this.standardVerticalWidth;
    }

    public void setStandardVerticalWidth(float standardVerticalWidthValue) {
        this.standardVerticalWidth = standardVerticalWidthValue;
    }
}
