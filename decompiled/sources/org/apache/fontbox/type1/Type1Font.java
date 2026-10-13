package org.apache.fontbox.type1;

import com.aowagie.text.pdf.BaseFont;
import java.awt.geom.GeneralPath;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.fontbox.EncodedFont;
import org.apache.fontbox.FontBoxFont;
import org.apache.fontbox.cff.Type1CharString;
import org.apache.fontbox.cff.Type1CharStringParser;
import org.apache.fontbox.encoding.Encoding;
import org.apache.fontbox.pfb.PfbParser;
import org.apache.fontbox.util.BoundingBox;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/type1/Type1Font.class */
public final class Type1Font implements Type1CharStringReader, EncodedFont, FontBoxFont {
    int paintType;
    int fontType;
    int uniqueID;
    float strokeWidth;
    float italicAngle;
    boolean isFixedPitch;
    float underlinePosition;
    float underlineThickness;
    float blueScale;
    int blueShift;
    int blueFuzz;
    boolean forceBold;
    int languageGroup;
    private final byte[] segment1;
    private final byte[] segment2;
    String fontName = "";
    Encoding encoding = null;
    List<Number> fontMatrix = new ArrayList();
    List<Number> fontBBox = new ArrayList();
    String fontID = "";
    String version = "";
    String notice = "";
    String fullName = "";
    String familyName = "";
    String weight = "";
    List<Number> blueValues = new ArrayList();
    List<Number> otherBlues = new ArrayList();
    List<Number> familyBlues = new ArrayList();
    List<Number> familyOtherBlues = new ArrayList();
    List<Number> stdHW = new ArrayList();
    List<Number> stdVW = new ArrayList();
    List<Number> stemSnapH = new ArrayList();
    List<Number> stemSnapV = new ArrayList();
    final List<byte[]> subrs = new ArrayList();
    final Map<String, byte[]> charstrings = new LinkedHashMap();
    private final Map<String, Type1CharString> charStringCache = new ConcurrentHashMap();

    public static Type1Font createWithPFB(InputStream pfbStream) throws IOException {
        PfbParser pfb = new PfbParser(pfbStream);
        Type1Parser parser = new Type1Parser();
        return parser.parse(pfb.getSegment1(), pfb.getSegment2());
    }

    public static Type1Font createWithPFB(byte[] pfbBytes) throws IOException {
        PfbParser pfb = new PfbParser(pfbBytes);
        Type1Parser parser = new Type1Parser();
        return parser.parse(pfb.getSegment1(), pfb.getSegment2());
    }

    public static Type1Font createWithSegments(byte[] segment1, byte[] segment2) throws IOException {
        Type1Parser parser = new Type1Parser();
        return parser.parse(segment1, segment2);
    }

    Type1Font(byte[] segment1, byte[] segment2) {
        this.segment1 = segment1;
        this.segment2 = segment2;
    }

    public List<byte[]> getSubrsArray() {
        return Collections.unmodifiableList(this.subrs);
    }

    public Map<String, byte[]> getCharStringsDict() {
        return Collections.unmodifiableMap(this.charstrings);
    }

    @Override // org.apache.fontbox.FontBoxFont
    public String getName() {
        return this.fontName;
    }

    @Override // org.apache.fontbox.FontBoxFont
    public GeneralPath getPath(String name) throws IOException {
        return getType1CharString(name).getPath();
    }

    @Override // org.apache.fontbox.FontBoxFont
    public float getWidth(String name) throws IOException {
        return getType1CharString(name).getWidth();
    }

    @Override // org.apache.fontbox.FontBoxFont
    public boolean hasGlyph(String name) {
        return this.charstrings.get(name) != null;
    }

    @Override // org.apache.fontbox.type1.Type1CharStringReader
    public Type1CharString getType1CharString(String name) throws IOException {
        Type1CharString type1 = this.charStringCache.get(name);
        if (type1 == null) {
            byte[] bytes = this.charstrings.get(name);
            if (bytes == null) {
                bytes = this.charstrings.get(BaseFont.notdef);
            }
            Type1CharStringParser parser = new Type1CharStringParser(this.fontName, name);
            List<Object> sequence = parser.parse(bytes, this.subrs);
            type1 = new Type1CharString(this, this.fontName, name, sequence);
            this.charStringCache.put(name, type1);
        }
        return type1;
    }

    public String getFontName() {
        return this.fontName;
    }

    @Override // org.apache.fontbox.EncodedFont
    public Encoding getEncoding() {
        return this.encoding;
    }

    public int getPaintType() {
        return this.paintType;
    }

    public int getFontType() {
        return this.fontType;
    }

    @Override // org.apache.fontbox.FontBoxFont
    public List<Number> getFontMatrix() {
        return Collections.unmodifiableList(this.fontMatrix);
    }

    @Override // org.apache.fontbox.FontBoxFont
    public BoundingBox getFontBBox() {
        return new BoundingBox(this.fontBBox);
    }

    public int getUniqueID() {
        return this.uniqueID;
    }

    public float getStrokeWidth() {
        return this.strokeWidth;
    }

    public String getFontID() {
        return this.fontID;
    }

    public String getVersion() {
        return this.version;
    }

    public String getNotice() {
        return this.notice;
    }

    public String getFullName() {
        return this.fullName;
    }

    public String getFamilyName() {
        return this.familyName;
    }

    public String getWeight() {
        return this.weight;
    }

    public float getItalicAngle() {
        return this.italicAngle;
    }

    public boolean isFixedPitch() {
        return this.isFixedPitch;
    }

    public float getUnderlinePosition() {
        return this.underlinePosition;
    }

    public float getUnderlineThickness() {
        return this.underlineThickness;
    }

    public List<Number> getBlueValues() {
        return Collections.unmodifiableList(this.blueValues);
    }

    public List<Number> getOtherBlues() {
        return Collections.unmodifiableList(this.otherBlues);
    }

    public List<Number> getFamilyBlues() {
        return Collections.unmodifiableList(this.familyBlues);
    }

    public List<Number> getFamilyOtherBlues() {
        return Collections.unmodifiableList(this.familyOtherBlues);
    }

    public float getBlueScale() {
        return this.blueScale;
    }

    public int getBlueShift() {
        return this.blueShift;
    }

    public int getBlueFuzz() {
        return this.blueFuzz;
    }

    public List<Number> getStdHW() {
        return Collections.unmodifiableList(this.stdHW);
    }

    public List<Number> getStdVW() {
        return Collections.unmodifiableList(this.stdVW);
    }

    public List<Number> getStemSnapH() {
        return Collections.unmodifiableList(this.stemSnapH);
    }

    public List<Number> getStemSnapV() {
        return Collections.unmodifiableList(this.stemSnapV);
    }

    public boolean isForceBold() {
        return this.forceBold;
    }

    public int getLanguageGroup() {
        return this.languageGroup;
    }

    public byte[] getASCIISegment() {
        return this.segment1;
    }

    public byte[] getBinarySegment() {
        return this.segment2;
    }

    public String toString() {
        return getClass().getName() + "[fontName=" + this.fontName + ", fullName=" + this.fullName + ", encoding=" + this.encoding + ", charStringsDict=" + this.charstrings + "]";
    }
}
