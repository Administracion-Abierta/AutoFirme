package org.apache.fontbox.cff;

import java.awt.geom.GeneralPath;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.fontbox.type1.Type1CharStringReader;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/cff/CFFCIDFont.class */
public class CFFCIDFont extends CFFFont {
    private String registry;
    private String ordering;
    private int supplement;
    private FDSelect fdSelect;
    private List<Map<String, Object>> fontDictionaries = new LinkedList();
    private List<Map<String, Object>> privateDictionaries = new LinkedList();
    private final Map<Integer, CIDKeyedType2CharString> charStringCache = new ConcurrentHashMap();
    private final PrivateType1CharStringReader reader = new PrivateType1CharStringReader();

    public String getRegistry() {
        return this.registry;
    }

    void setRegistry(String registry) {
        this.registry = registry;
    }

    public String getOrdering() {
        return this.ordering;
    }

    void setOrdering(String ordering) {
        this.ordering = ordering;
    }

    public int getSupplement() {
        return this.supplement;
    }

    void setSupplement(int supplement) {
        this.supplement = supplement;
    }

    public List<Map<String, Object>> getFontDicts() {
        return this.fontDictionaries;
    }

    void setFontDict(List<Map<String, Object>> fontDict) {
        this.fontDictionaries = fontDict;
    }

    public List<Map<String, Object>> getPrivDicts() {
        return this.privateDictionaries;
    }

    void setPrivDict(List<Map<String, Object>> privDict) {
        this.privateDictionaries = privDict;
    }

    public FDSelect getFdSelect() {
        return this.fdSelect;
    }

    void setFdSelect(FDSelect fdSelect) {
        this.fdSelect = fdSelect;
    }

    private int getDefaultWidthX(int gid) {
        int fdArrayIndex = this.fdSelect.getFDIndex(gid);
        if (fdArrayIndex == -1) {
            return 1000;
        }
        Map<String, Object> privDict = this.privateDictionaries.get(fdArrayIndex);
        if (privDict.containsKey("defaultWidthX")) {
            return ((Number) privDict.get("defaultWidthX")).intValue();
        }
        return 1000;
    }

    private int getNominalWidthX(int gid) {
        int fdArrayIndex = this.fdSelect.getFDIndex(gid);
        if (fdArrayIndex == -1) {
            return 0;
        }
        Map<String, Object> privDict = this.privateDictionaries.get(fdArrayIndex);
        if (privDict.containsKey("nominalWidthX")) {
            return ((Number) privDict.get("nominalWidthX")).intValue();
        }
        return 0;
    }

    private byte[][] getLocalSubrIndex(int gid) {
        int fdArrayIndex = this.fdSelect.getFDIndex(gid);
        if (fdArrayIndex == -1) {
            return (byte[][]) null;
        }
        Map<String, Object> privDict = this.privateDictionaries.get(fdArrayIndex);
        return (byte[][]) privDict.get("Subrs");
    }

    @Override // org.apache.fontbox.cff.CFFFont
    public CIDKeyedType2CharString getType2CharString(int cid) throws IOException {
        CIDKeyedType2CharString type2 = this.charStringCache.get(Integer.valueOf(cid));
        if (type2 == null) {
            int gid = this.charset.getGIDForCID(cid);
            byte[] bytes = this.charStrings[gid];
            if (bytes == null) {
                bytes = this.charStrings[0];
            }
            Type2CharStringParser parser = new Type2CharStringParser(this.fontName, cid);
            List<Object> type2seq = parser.parse(bytes, this.globalSubrIndex, getLocalSubrIndex(gid));
            type2 = new CIDKeyedType2CharString(this.reader, this.fontName, cid, gid, type2seq, getDefaultWidthX(gid), getNominalWidthX(gid));
            this.charStringCache.put(Integer.valueOf(cid), type2);
        }
        return type2;
    }

    @Override // org.apache.fontbox.cff.CFFFont, org.apache.fontbox.FontBoxFont
    public List<Number> getFontMatrix() {
        return (List) this.topDict.get("FontMatrix");
    }

    @Override // org.apache.fontbox.FontBoxFont
    public GeneralPath getPath(String selector) throws IOException {
        int cid = selectorToCID(selector);
        return getType2CharString(cid).getPath();
    }

    @Override // org.apache.fontbox.FontBoxFont
    public float getWidth(String selector) throws IOException {
        int cid = selectorToCID(selector);
        return getType2CharString(cid).getWidth();
    }

    @Override // org.apache.fontbox.FontBoxFont
    public boolean hasGlyph(String selector) throws IOException {
        int cid = selectorToCID(selector);
        return cid != 0;
    }

    private int selectorToCID(String selector) {
        if (!selector.startsWith("\\")) {
            throw new IllegalArgumentException("Invalid selector");
        }
        return Integer.parseInt(selector.substring(1));
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/cff/CFFCIDFont$PrivateType1CharStringReader.class */
    private class PrivateType1CharStringReader implements Type1CharStringReader {
        private PrivateType1CharStringReader() {
        }

        @Override // org.apache.fontbox.type1.Type1CharStringReader
        public Type1CharString getType1CharString(String name) throws IOException {
            return CFFCIDFont.this.getType2CharString(0);
        }
    }
}
