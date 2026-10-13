package org.apache.fontbox.cff;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.fontbox.FontBoxFont;
import org.apache.fontbox.afm.AFMParser;
import org.apache.fontbox.util.BoundingBox;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/cff/CFFFont.class */
public abstract class CFFFont implements FontBoxFont {
    protected String fontName;
    protected final Map<String, Object> topDict = new LinkedHashMap();
    protected CFFCharset charset;
    protected byte[][] charStrings;
    protected byte[][] globalSubrIndex;
    private CFFParser.ByteSource source;

    @Override // org.apache.fontbox.FontBoxFont
    public abstract List<Number> getFontMatrix();

    public abstract Type2CharString getType2CharString(int i) throws IOException;

    @Override // org.apache.fontbox.FontBoxFont
    public String getName() {
        return this.fontName;
    }

    void setName(String name) {
        this.fontName = name;
    }

    public void addValueToTopDict(String name, Object value) {
        if (value != null) {
            this.topDict.put(name, value);
        }
    }

    public Map<String, Object> getTopDict() {
        return this.topDict;
    }

    @Override // org.apache.fontbox.FontBoxFont
    public BoundingBox getFontBBox() {
        List<Number> numbers = (List) this.topDict.get(AFMParser.FONT_BBOX);
        return new BoundingBox(numbers);
    }

    public CFFCharset getCharset() {
        return this.charset;
    }

    void setCharset(CFFCharset charset) {
        this.charset = charset;
    }

    public final List<byte[]> getCharStringBytes() {
        return Arrays.asList(this.charStrings);
    }

    final void setData(CFFParser.ByteSource source) {
        this.source = source;
    }

    public byte[] getData() throws IOException {
        return this.source.getBytes();
    }

    public int getNumCharStrings() {
        return this.charStrings.length;
    }

    void setGlobalSubrIndex(byte[][] globalSubrIndexValue) {
        this.globalSubrIndex = globalSubrIndexValue;
    }

    public List<byte[]> getGlobalSubrIndex() {
        return Arrays.asList(this.globalSubrIndex);
    }

    public String toString() {
        return getClass().getSimpleName() + "[name=" + this.fontName + ", topDict=" + this.topDict + ", charset=" + this.charset + ", charStrings=" + Arrays.deepToString(this.charStrings) + "]";
    }
}
