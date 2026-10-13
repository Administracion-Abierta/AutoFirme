package org.apache.fontbox.cff;

import java.awt.geom.GeneralPath;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.fontbox.EncodedFont;
import org.apache.fontbox.type1.Type1CharStringReader;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/cff/CFFType1Font.class */
public class CFFType1Font extends CFFFont implements EncodedFont {
    private CFFEncoding encoding;
    private final Map<String, Object> privateDict = new LinkedHashMap();
    private final Map<Integer, Type2CharString> charStringCache = new ConcurrentHashMap();
    private final PrivateType1CharStringReader reader = new PrivateType1CharStringReader();

    /* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/cff/CFFType1Font$PrivateType1CharStringReader.class */
    private class PrivateType1CharStringReader implements Type1CharStringReader {
        private PrivateType1CharStringReader() {
        }

        @Override // org.apache.fontbox.type1.Type1CharStringReader
        public Type1CharString getType1CharString(String name) throws IOException {
            return CFFType1Font.this.getType1CharString(name);
        }
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
        int sid = this.charset.getSID(name);
        int gid = this.charset.getGIDForSID(sid);
        return gid != 0;
    }

    @Override // org.apache.fontbox.cff.CFFFont, org.apache.fontbox.FontBoxFont
    public List<Number> getFontMatrix() {
        return (List) this.topDict.get("FontMatrix");
    }

    public Type1CharString getType1CharString(String name) throws IOException {
        int gid = nameToGID(name);
        return getType2CharString(gid, name);
    }

    public int nameToGID(String name) {
        int sid = this.charset.getSID(name);
        return this.charset.getGIDForSID(sid);
    }

    @Override // org.apache.fontbox.cff.CFFFont
    public Type2CharString getType2CharString(int gid) throws IOException {
        String name = "GID+" + gid;
        return getType2CharString(gid, name);
    }

    private Type2CharString getType2CharString(int gid, String name) throws IOException {
        Type2CharString type2 = this.charStringCache.get(Integer.valueOf(gid));
        if (type2 == null) {
            byte[] bytes = null;
            if (gid < this.charStrings.length) {
                bytes = this.charStrings[gid];
            }
            if (bytes == null) {
                bytes = this.charStrings[0];
            }
            Type2CharStringParser parser = new Type2CharStringParser(this.fontName, name);
            List<Object> type2seq = parser.parse(bytes, this.globalSubrIndex, getLocalSubrIndex());
            type2 = new Type2CharString(this.reader, this.fontName, name, gid, type2seq, getDefaultWidthX(), getNominalWidthX());
            this.charStringCache.put(Integer.valueOf(gid), type2);
        }
        return type2;
    }

    public Map<String, Object> getPrivateDict() {
        return this.privateDict;
    }

    void addToPrivateDict(String name, Object value) {
        if (value != null) {
            this.privateDict.put(name, value);
        }
    }

    @Override // org.apache.fontbox.EncodedFont
    public CFFEncoding getEncoding() {
        return this.encoding;
    }

    void setEncoding(CFFEncoding encoding) {
        this.encoding = encoding;
    }

    private byte[][] getLocalSubrIndex() {
        return (byte[][]) this.privateDict.get("Subrs");
    }

    private Object getProperty(String name) {
        Object topDictValue = this.topDict.get(name);
        if (topDictValue != null) {
            return topDictValue;
        }
        return this.privateDict.get(name);
    }

    private int getDefaultWidthX() {
        Number num = (Number) getProperty("defaultWidthX");
        if (num == null) {
            return 1000;
        }
        return num.intValue();
    }

    private int getNominalWidthX() {
        Number num = (Number) getProperty("nominalWidthX");
        if (num == null) {
            return 0;
        }
        return num.intValue();
    }
}
