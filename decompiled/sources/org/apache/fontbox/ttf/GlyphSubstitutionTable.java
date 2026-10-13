package org.apache.fontbox.ttf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/GlyphSubstitutionTable.class */
public class GlyphSubstitutionTable extends TTFTable {
    private static final Log LOG = LogFactory.getLog(GlyphSubstitutionTable.class);
    public static final String TAG = "GSUB";
    private LinkedHashMap<String, ScriptTable> scriptList;
    private FeatureRecord[] featureList;
    private LookupTable[] lookupList;
    private final Map<Integer, Integer> lookupCache;
    private final Map<Integer, Integer> reverseLookup;
    private String lastUsedSupportedScript;

    GlyphSubstitutionTable(TrueTypeFont font) {
        super(font);
        this.lookupCache = new HashMap();
        this.reverseLookup = new HashMap();
    }

    @Override // org.apache.fontbox.ttf.TTFTable
    void read(TrueTypeFont ttf, TTFDataStream data) throws IOException {
        long start = data.getCurrentPosition();
        data.readUnsignedShort();
        int minorVersion = data.readUnsignedShort();
        int scriptListOffset = data.readUnsignedShort();
        int featureListOffset = data.readUnsignedShort();
        int lookupListOffset = data.readUnsignedShort();
        if (minorVersion == 1) {
            data.readUnsignedInt();
        }
        this.scriptList = readScriptList(data, start + ((long) scriptListOffset));
        this.featureList = readFeatureList(data, start + ((long) featureListOffset));
        this.lookupList = readLookupList(data, start + ((long) lookupListOffset));
    }

    LinkedHashMap<String, ScriptTable> readScriptList(TTFDataStream data, long offset) throws IOException {
        data.seek(offset);
        int scriptCount = data.readUnsignedShort();
        ScriptRecord[] scriptRecords = new ScriptRecord[scriptCount];
        int[] scriptOffsets = new int[scriptCount];
        for (int i = 0; i < scriptCount; i++) {
            ScriptRecord scriptRecord = new ScriptRecord();
            scriptRecord.scriptTag = data.readString(4);
            scriptOffsets[i] = data.readUnsignedShort();
            scriptRecords[i] = scriptRecord;
        }
        for (int i2 = 0; i2 < scriptCount; i2++) {
            scriptRecords[i2].scriptTable = readScriptTable(data, offset + ((long) scriptOffsets[i2]));
        }
        LinkedHashMap<String, ScriptTable> resultScriptList = new LinkedHashMap<>(scriptCount);
        for (ScriptRecord scriptRecord2 : scriptRecords) {
            resultScriptList.put(scriptRecord2.scriptTag, scriptRecord2.scriptTable);
        }
        return resultScriptList;
    }

    ScriptTable readScriptTable(TTFDataStream data, long offset) throws IOException {
        data.seek(offset);
        ScriptTable scriptTable = new ScriptTable();
        int defaultLangSys = data.readUnsignedShort();
        int langSysCount = data.readUnsignedShort();
        LangSysRecord[] langSysRecords = new LangSysRecord[langSysCount];
        int[] langSysOffsets = new int[langSysCount];
        String prevLangSysTag = "";
        for (int i = 0; i < langSysCount; i++) {
            LangSysRecord langSysRecord = new LangSysRecord();
            langSysRecord.langSysTag = data.readString(4);
            if (i > 0 && langSysRecord.langSysTag.compareTo(prevLangSysTag) <= 0) {
                throw new IOException("LangSysRecords not alphabetically sorted by LangSys tag: " + langSysRecord.langSysTag + " <= " + prevLangSysTag);
            }
            langSysOffsets[i] = data.readUnsignedShort();
            langSysRecords[i] = langSysRecord;
            prevLangSysTag = langSysRecord.langSysTag;
        }
        if (defaultLangSys != 0) {
            scriptTable.defaultLangSysTable = readLangSysTable(data, offset + ((long) defaultLangSys));
        }
        for (int i2 = 0; i2 < langSysCount; i2++) {
            langSysRecords[i2].langSysTable = readLangSysTable(data, offset + ((long) langSysOffsets[i2]));
        }
        scriptTable.langSysTables = new LinkedHashMap<>(langSysCount);
        for (LangSysRecord langSysRecord2 : langSysRecords) {
            scriptTable.langSysTables.put(langSysRecord2.langSysTag, langSysRecord2.langSysTable);
        }
        return scriptTable;
    }

    LangSysTable readLangSysTable(TTFDataStream data, long offset) throws IOException {
        data.seek(offset);
        LangSysTable langSysTable = new LangSysTable();
        data.readUnsignedShort();
        langSysTable.requiredFeatureIndex = data.readUnsignedShort();
        int featureIndexCount = data.readUnsignedShort();
        langSysTable.featureIndices = new int[featureIndexCount];
        for (int i = 0; i < featureIndexCount; i++) {
            langSysTable.featureIndices[i] = data.readUnsignedShort();
        }
        return langSysTable;
    }

    FeatureRecord[] readFeatureList(TTFDataStream data, long offset) throws IOException {
        data.seek(offset);
        int featureCount = data.readUnsignedShort();
        FeatureRecord[] featureRecords = new FeatureRecord[featureCount];
        int[] featureOffsets = new int[featureCount];
        String prevFeatureTag = "";
        for (int i = 0; i < featureCount; i++) {
            FeatureRecord featureRecord = new FeatureRecord();
            featureRecord.featureTag = data.readString(4);
            if (i > 0 && featureRecord.featureTag.compareTo(prevFeatureTag) < 0) {
                if (featureRecord.featureTag.matches("\\w{4}") && prevFeatureTag.matches("\\w{4}")) {
                    LOG.debug("FeatureRecord array not alphabetically sorted by FeatureTag: " + featureRecord.featureTag + " < " + prevFeatureTag);
                } else {
                    LOG.warn("FeatureRecord array not alphabetically sorted by FeatureTag: " + featureRecord.featureTag + " < " + prevFeatureTag);
                    return new FeatureRecord[0];
                }
            }
            featureOffsets[i] = data.readUnsignedShort();
            featureRecords[i] = featureRecord;
            prevFeatureTag = featureRecord.featureTag;
        }
        for (int i2 = 0; i2 < featureCount; i2++) {
            featureRecords[i2].featureTable = readFeatureTable(data, offset + ((long) featureOffsets[i2]));
        }
        return featureRecords;
    }

    FeatureTable readFeatureTable(TTFDataStream data, long offset) throws IOException {
        data.seek(offset);
        FeatureTable featureTable = new FeatureTable();
        data.readUnsignedShort();
        int lookupIndexCount = data.readUnsignedShort();
        featureTable.lookupListIndices = new int[lookupIndexCount];
        for (int i = 0; i < lookupIndexCount; i++) {
            featureTable.lookupListIndices[i] = data.readUnsignedShort();
        }
        return featureTable;
    }

    LookupTable[] readLookupList(TTFDataStream data, long offset) throws IOException {
        data.seek(offset);
        int lookupCount = data.readUnsignedShort();
        int[] lookups = new int[lookupCount];
        for (int i = 0; i < lookupCount; i++) {
            lookups[i] = data.readUnsignedShort();
        }
        LookupTable[] lookupTables = new LookupTable[lookupCount];
        for (int i2 = 0; i2 < lookupCount; i2++) {
            lookupTables[i2] = readLookupTable(data, offset + ((long) lookups[i2]));
        }
        return lookupTables;
    }

    LookupTable readLookupTable(TTFDataStream data, long offset) throws IOException {
        data.seek(offset);
        LookupTable lookupTable = new LookupTable();
        lookupTable.lookupType = data.readUnsignedShort();
        lookupTable.lookupFlag = data.readUnsignedShort();
        int subTableCount = data.readUnsignedShort();
        int[] subTableOffets = new int[subTableCount];
        for (int i = 0; i < subTableCount; i++) {
            subTableOffets[i] = data.readUnsignedShort();
        }
        if ((lookupTable.lookupFlag & 16) != 0) {
            lookupTable.markFilteringSet = data.readUnsignedShort();
        }
        lookupTable.subTables = new LookupSubTable[subTableCount];
        switch (lookupTable.lookupType) {
            case 1:
                for (int i2 = 0; i2 < subTableCount; i2++) {
                    lookupTable.subTables[i2] = readLookupSubTable(data, offset + ((long) subTableOffets[i2]));
                }
                break;
            default:
                LOG.debug("Type " + lookupTable.lookupType + " GSUB lookup table is not supported and will be ignored");
                break;
        }
        return lookupTable;
    }

    LookupSubTable readLookupSubTable(TTFDataStream data, long offset) throws IOException {
        data.seek(offset);
        int substFormat = data.readUnsignedShort();
        switch (substFormat) {
            case 1:
                LookupTypeSingleSubstFormat1 lookupSubTable = new LookupTypeSingleSubstFormat1();
                lookupSubTable.substFormat = substFormat;
                int coverageOffset = data.readUnsignedShort();
                lookupSubTable.deltaGlyphID = data.readSignedShort();
                lookupSubTable.coverageTable = readCoverageTable(data, offset + ((long) coverageOffset));
                return lookupSubTable;
            case 2:
                LookupTypeSingleSubstFormat2 lookupSubTable2 = new LookupTypeSingleSubstFormat2();
                lookupSubTable2.substFormat = substFormat;
                int coverageOffset2 = data.readUnsignedShort();
                int glyphCount = data.readUnsignedShort();
                lookupSubTable2.substituteGlyphIDs = new int[glyphCount];
                for (int i = 0; i < glyphCount; i++) {
                    lookupSubTable2.substituteGlyphIDs[i] = data.readUnsignedShort();
                }
                lookupSubTable2.coverageTable = readCoverageTable(data, offset + ((long) coverageOffset2));
                return lookupSubTable2;
            default:
                throw new IOException("Unknown substFormat: " + substFormat);
        }
    }

    CoverageTable readCoverageTable(TTFDataStream data, long offset) throws IOException {
        data.seek(offset);
        int coverageFormat = data.readUnsignedShort();
        switch (coverageFormat) {
            case 1:
                CoverageTableFormat1 coverageTable = new CoverageTableFormat1();
                coverageTable.coverageFormat = coverageFormat;
                int glyphCount = data.readUnsignedShort();
                coverageTable.glyphArray = new int[glyphCount];
                for (int i = 0; i < glyphCount; i++) {
                    coverageTable.glyphArray[i] = data.readUnsignedShort();
                }
                return coverageTable;
            case 2:
                CoverageTableFormat2 coverageTable2 = new CoverageTableFormat2();
                coverageTable2.coverageFormat = coverageFormat;
                int rangeCount = data.readUnsignedShort();
                coverageTable2.rangeRecords = new RangeRecord[rangeCount];
                for (int i2 = 0; i2 < rangeCount; i2++) {
                    coverageTable2.rangeRecords[i2] = readRangeRecord(data);
                }
                return coverageTable2;
            default:
                throw new IOException("Unknown coverage format: " + coverageFormat);
        }
    }

    private String selectScriptTag(String[] tags) {
        if (tags.length == 1) {
            String tag = tags[0];
            if (OpenTypeScript.INHERITED.equals(tag) || (OpenTypeScript.TAG_DEFAULT.equals(tag) && !this.scriptList.containsKey(tag))) {
                if (this.lastUsedSupportedScript == null) {
                    this.lastUsedSupportedScript = this.scriptList.keySet().iterator().next();
                }
                return this.lastUsedSupportedScript;
            }
        }
        for (String tag2 : tags) {
            if (this.scriptList.containsKey(tag2)) {
                this.lastUsedSupportedScript = tag2;
                return this.lastUsedSupportedScript;
            }
        }
        return tags[0];
    }

    private Collection<LangSysTable> getLangSysTables(String scriptTag) {
        Collection<LangSysTable> result = Collections.emptyList();
        ScriptTable scriptTable = this.scriptList.get(scriptTag);
        if (scriptTable != null) {
            if (scriptTable.defaultLangSysTable == null) {
                result = scriptTable.langSysTables.values();
            } else {
                result = new ArrayList(scriptTable.langSysTables.values());
                result.add(scriptTable.defaultLangSysTable);
            }
        }
        return result;
    }

    private List<FeatureRecord> getFeatureRecords(Collection<LangSysTable> langSysTables, final List<String> enabledFeatures) {
        if (langSysTables.isEmpty()) {
            return Collections.emptyList();
        }
        List<FeatureRecord> result = new ArrayList<>();
        for (LangSysTable langSysTable : langSysTables) {
            int required = langSysTable.requiredFeatureIndex;
            if (required != 65535 && required < this.featureList.length) {
                result.add(this.featureList[required]);
            }
            for (int featureIndex : langSysTable.featureIndices) {
                if (featureIndex < this.featureList.length && (enabledFeatures == null || enabledFeatures.contains(this.featureList[featureIndex].featureTag))) {
                    result.add(this.featureList[featureIndex]);
                }
            }
        }
        if (containsFeature(result, "vrt2")) {
            removeFeature(result, "vert");
        }
        if (enabledFeatures != null && result.size() > 1) {
            Collections.sort(result, new Comparator<FeatureRecord>() { // from class: org.apache.fontbox.ttf.GlyphSubstitutionTable.1
                @Override // java.util.Comparator
                public int compare(FeatureRecord o1, FeatureRecord o2) {
                    int i1 = enabledFeatures.indexOf(o1.featureTag);
                    int i2 = enabledFeatures.indexOf(o2.featureTag);
                    if (i1 < i2) {
                        return -1;
                    }
                    return i1 == i2 ? 0 : 1;
                }
            });
        }
        return result;
    }

    private boolean containsFeature(List<FeatureRecord> featureRecords, String featureTag) {
        for (FeatureRecord featureRecord : featureRecords) {
            if (featureRecord.featureTag.equals(featureTag)) {
                return true;
            }
        }
        return false;
    }

    private void removeFeature(List<FeatureRecord> featureRecords, String featureTag) {
        Iterator<FeatureRecord> iter = featureRecords.iterator();
        while (iter.hasNext()) {
            if (iter.next().featureTag.equals(featureTag)) {
                iter.remove();
            }
        }
    }

    private int applyFeature(FeatureRecord featureRecord, int gid) {
        for (int lookupListIndex : featureRecord.featureTable.lookupListIndices) {
            LookupTable lookupTable = this.lookupList[lookupListIndex];
            if (lookupTable.lookupType != 1) {
                LOG.debug("Skipping GSUB feature '" + featureRecord.featureTag + "' because it requires unsupported lookup table type " + lookupTable.lookupType);
            } else {
                gid = doLookup(lookupTable, gid);
            }
        }
        return gid;
    }

    private int doLookup(LookupTable lookupTable, int gid) {
        for (LookupSubTable lookupSubtable : lookupTable.subTables) {
            int coverageIndex = lookupSubtable.coverageTable.getCoverageIndex(gid);
            if (coverageIndex >= 0) {
                return lookupSubtable.doSubstitution(gid, coverageIndex);
            }
        }
        return gid;
    }

    public int getSubstitution(int gid, String[] scriptTags, List<String> enabledFeatures) {
        if (gid == -1) {
            return -1;
        }
        Integer cached = this.lookupCache.get(Integer.valueOf(gid));
        if (cached != null) {
            return cached.intValue();
        }
        String scriptTag = selectScriptTag(scriptTags);
        Collection<LangSysTable> langSysTables = getLangSysTables(scriptTag);
        List<FeatureRecord> featureRecords = getFeatureRecords(langSysTables, enabledFeatures);
        int sgid = gid;
        for (FeatureRecord featureRecord : featureRecords) {
            sgid = applyFeature(featureRecord, sgid);
        }
        this.lookupCache.put(Integer.valueOf(gid), Integer.valueOf(sgid));
        this.reverseLookup.put(Integer.valueOf(sgid), Integer.valueOf(gid));
        return sgid;
    }

    public int getUnsubstitution(int sgid) {
        Integer gid = this.reverseLookup.get(Integer.valueOf(sgid));
        if (gid == null) {
            LOG.warn("Trying to un-substitute a never-before-seen gid: " + sgid);
            return sgid;
        }
        return gid.intValue();
    }

    RangeRecord readRangeRecord(TTFDataStream data) throws IOException {
        RangeRecord rangeRecord = new RangeRecord();
        rangeRecord.startGlyphID = data.readUnsignedShort();
        rangeRecord.endGlyphID = data.readUnsignedShort();
        rangeRecord.startCoverageIndex = data.readUnsignedShort();
        return rangeRecord;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/GlyphSubstitutionTable$ScriptRecord.class */
    static class ScriptRecord {
        String scriptTag;
        ScriptTable scriptTable;

        ScriptRecord() {
        }

        public String toString() {
            return String.format("ScriptRecord[scriptTag=%s]", this.scriptTag);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/GlyphSubstitutionTable$ScriptTable.class */
    static class ScriptTable {
        LangSysTable defaultLangSysTable;
        LinkedHashMap<String, LangSysTable> langSysTables;

        ScriptTable() {
        }

        public String toString() {
            Object[] objArr = new Object[2];
            objArr[0] = Boolean.valueOf(this.defaultLangSysTable != null);
            objArr[1] = Integer.valueOf(this.langSysTables.size());
            return String.format("ScriptTable[hasDefault=%s,langSysRecordsCount=%d]", objArr);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/GlyphSubstitutionTable$LangSysRecord.class */
    static class LangSysRecord {
        String langSysTag;
        LangSysTable langSysTable;

        LangSysRecord() {
        }

        public String toString() {
            return String.format("LangSysRecord[langSysTag=%s]", this.langSysTag);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/GlyphSubstitutionTable$LangSysTable.class */
    static class LangSysTable {
        int requiredFeatureIndex;
        int[] featureIndices;

        LangSysTable() {
        }

        public String toString() {
            return String.format("LangSysTable[requiredFeatureIndex=%d]", Integer.valueOf(this.requiredFeatureIndex));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/GlyphSubstitutionTable$FeatureRecord.class */
    static class FeatureRecord {
        String featureTag;
        FeatureTable featureTable;

        FeatureRecord() {
        }

        public String toString() {
            return String.format("FeatureRecord[featureTag=%s]", this.featureTag);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/GlyphSubstitutionTable$FeatureTable.class */
    static class FeatureTable {
        int[] lookupListIndices;

        FeatureTable() {
        }

        public String toString() {
            return String.format("FeatureTable[lookupListIndiciesCount=%d]", Integer.valueOf(this.lookupListIndices.length));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/GlyphSubstitutionTable$LookupTable.class */
    static class LookupTable {
        int lookupType;
        int lookupFlag;
        int markFilteringSet;
        LookupSubTable[] subTables;

        LookupTable() {
        }

        public String toString() {
            return String.format("LookupTable[lookupType=%d,lookupFlag=%d,markFilteringSet=%d]", Integer.valueOf(this.lookupType), Integer.valueOf(this.lookupFlag), Integer.valueOf(this.markFilteringSet));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/GlyphSubstitutionTable$LookupSubTable.class */
    static abstract class LookupSubTable {
        int substFormat;
        CoverageTable coverageTable;

        abstract int doSubstitution(int i, int i2);

        LookupSubTable() {
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/GlyphSubstitutionTable$LookupTypeSingleSubstFormat1.class */
    static class LookupTypeSingleSubstFormat1 extends LookupSubTable {
        short deltaGlyphID;

        LookupTypeSingleSubstFormat1() {
        }

        @Override // org.apache.fontbox.ttf.GlyphSubstitutionTable.LookupSubTable
        int doSubstitution(int gid, int coverageIndex) {
            return coverageIndex < 0 ? gid : gid + this.deltaGlyphID;
        }

        public String toString() {
            return String.format("LookupTypeSingleSubstFormat1[substFormat=%d,deltaGlyphID=%d]", Integer.valueOf(this.substFormat), Short.valueOf(this.deltaGlyphID));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/GlyphSubstitutionTable$LookupTypeSingleSubstFormat2.class */
    static class LookupTypeSingleSubstFormat2 extends LookupSubTable {
        int[] substituteGlyphIDs;

        LookupTypeSingleSubstFormat2() {
        }

        @Override // org.apache.fontbox.ttf.GlyphSubstitutionTable.LookupSubTable
        int doSubstitution(int gid, int coverageIndex) {
            return coverageIndex < 0 ? gid : this.substituteGlyphIDs[coverageIndex];
        }

        public String toString() {
            return String.format("LookupTypeSingleSubstFormat2[substFormat=%d,substituteGlyphIDs=%s]", Integer.valueOf(this.substFormat), Arrays.toString(this.substituteGlyphIDs));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/GlyphSubstitutionTable$CoverageTable.class */
    static abstract class CoverageTable {
        int coverageFormat;

        abstract int getCoverageIndex(int i);

        CoverageTable() {
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/GlyphSubstitutionTable$CoverageTableFormat1.class */
    static class CoverageTableFormat1 extends CoverageTable {
        int[] glyphArray;

        CoverageTableFormat1() {
        }

        @Override // org.apache.fontbox.ttf.GlyphSubstitutionTable.CoverageTable
        int getCoverageIndex(int gid) {
            return Arrays.binarySearch(this.glyphArray, gid);
        }

        public String toString() {
            return String.format("CoverageTableFormat1[coverageFormat=%d,glyphArray=%s]", Integer.valueOf(this.coverageFormat), Arrays.toString(this.glyphArray));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/GlyphSubstitutionTable$CoverageTableFormat2.class */
    static class CoverageTableFormat2 extends CoverageTable {
        RangeRecord[] rangeRecords;

        CoverageTableFormat2() {
        }

        @Override // org.apache.fontbox.ttf.GlyphSubstitutionTable.CoverageTable
        int getCoverageIndex(int gid) {
            for (RangeRecord rangeRecord : this.rangeRecords) {
                if (rangeRecord.startGlyphID <= gid && gid <= rangeRecord.endGlyphID) {
                    return (rangeRecord.startCoverageIndex + gid) - rangeRecord.startGlyphID;
                }
            }
            return -1;
        }

        public String toString() {
            return String.format("CoverageTableFormat2[coverageFormat=%d]", Integer.valueOf(this.coverageFormat));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/GlyphSubstitutionTable$RangeRecord.class */
    static class RangeRecord {
        int startGlyphID;
        int endGlyphID;
        int startCoverageIndex;

        RangeRecord() {
        }

        public String toString() {
            return String.format("RangeRecord[startGlyphID=%d,endGlyphID=%d,startCoverageIndex=%d]", Integer.valueOf(this.startGlyphID), Integer.valueOf(this.endGlyphID), Integer.valueOf(this.startCoverageIndex));
        }
    }
}
