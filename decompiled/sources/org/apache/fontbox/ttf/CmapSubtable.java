package org.apache.fontbox.ttf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/CmapSubtable.class */
public class CmapSubtable implements CmapLookup {
    private static final Log LOG = LogFactory.getLog(CmapSubtable.class);
    private static final long LEAD_OFFSET = 55232;
    private static final long SURROGATE_OFFSET = -56613888;
    private int platformId;
    private int platformEncodingId;
    private long subTableOffset;
    private int[] glyphIdToCharacterCode;
    private final Map<Integer, List<Integer>> glyphIdToCharacterCodeMultiple = new HashMap();
    private Map<Integer, Integer> characterCodeToGlyphId = new HashMap();

    void initData(TTFDataStream data) throws IOException {
        this.platformId = data.readUnsignedShort();
        this.platformEncodingId = data.readUnsignedShort();
        this.subTableOffset = data.readUnsignedInt();
    }

    void initSubtable(CmapTable cmap, int numGlyphs, TTFDataStream data) throws IOException {
        data.seek(cmap.getOffset() + this.subTableOffset);
        int subtableFormat = data.readUnsignedShort();
        if (subtableFormat < 8) {
            data.readUnsignedShort();
            data.readUnsignedShort();
        } else {
            data.readUnsignedShort();
            data.readUnsignedInt();
            data.readUnsignedInt();
        }
        switch (subtableFormat) {
            case 0:
                processSubtype0(data);
                return;
            case 1:
            case 3:
            case 5:
            case 7:
            case 9:
            case 11:
            default:
                throw new IOException("Unknown cmap format:" + subtableFormat);
            case 2:
                processSubtype2(data, numGlyphs);
                return;
            case 4:
                processSubtype4(data, numGlyphs);
                return;
            case 6:
                processSubtype6(data, numGlyphs);
                return;
            case 8:
                processSubtype8(data, numGlyphs);
                return;
            case 10:
                processSubtype10(data, numGlyphs);
                return;
            case 12:
                processSubtype12(data, numGlyphs);
                return;
            case 13:
                processSubtype13(data, numGlyphs);
                return;
            case 14:
                processSubtype14(data, numGlyphs);
                return;
        }
    }

    void processSubtype8(TTFDataStream data, int numGlyphs) throws IOException {
        int currentCharCode;
        int[] is32 = data.readUnsignedByteArray(8192);
        long nbGroups = data.readUnsignedInt();
        if (nbGroups > 65536) {
            throw new IOException("CMap ( Subtype8 ) is invalid");
        }
        this.glyphIdToCharacterCode = newGlyphIdToCharacterCode(numGlyphs);
        this.characterCodeToGlyphId = new HashMap(numGlyphs);
        if (numGlyphs == 0) {
            LOG.warn("subtable has no glyphs");
            return;
        }
        long j = 0;
        while (true) {
            long i = j;
            if (i < nbGroups) {
                long firstCode = data.readUnsignedInt();
                long endCode = data.readUnsignedInt();
                long startGlyph = data.readUnsignedInt();
                if (firstCode > endCode || 0 > firstCode) {
                    throw new IOException("Range invalid");
                }
                long j2 = firstCode;
                while (true) {
                    long j3 = j2;
                    if (j3 <= endCode) {
                        if (j3 > 2147483647L) {
                            throw new IOException("[Sub Format 8] Invalid character code " + j3);
                        }
                        if (((int) j3) / 8 >= is32.length) {
                            throw new IOException("[Sub Format 8] Invalid character code " + j3);
                        }
                        if ((is32[((int) j3) / 8] & (1 << (((int) j3) % 8))) == 0) {
                            currentCharCode = (int) j3;
                        } else {
                            long lead = LEAD_OFFSET + (j3 >> 10);
                            long trail = 56320 + (j3 & 1023);
                            long codepoint = (lead << 10) + trail + SURROGATE_OFFSET;
                            if (codepoint > 2147483647L) {
                                throw new IOException("[Sub Format 8] Invalid character code " + codepoint);
                            }
                            currentCharCode = (int) codepoint;
                        }
                        long glyphIndex = startGlyph + (j3 - firstCode);
                        if (glyphIndex > numGlyphs || glyphIndex > 2147483647L) {
                            throw new IOException("CMap contains an invalid glyph index");
                        }
                        this.glyphIdToCharacterCode[(int) glyphIndex] = currentCharCode;
                        this.characterCodeToGlyphId.put(Integer.valueOf(currentCharCode), Integer.valueOf((int) glyphIndex));
                        j2 = j3 + 1;
                    }
                }
                j = i + 1;
            } else {
                return;
            }
        }
    }

    void processSubtype10(TTFDataStream data, int numGlyphs) throws IOException {
        long startCode = data.readUnsignedInt();
        long numChars = data.readUnsignedInt();
        if (numChars > 2147483647L) {
            throw new IOException("Invalid number of Characters");
        }
        if (startCode < 0 || startCode > 1114111 || startCode + numChars > 1114111 || (startCode + numChars >= 55296 && startCode + numChars <= 57343)) {
            throw new IOException("Invalid character codes, " + String.format("startCode: 0x%X, numChars: %d", Long.valueOf(startCode), Long.valueOf(numChars)));
        }
    }

    void processSubtype12(TTFDataStream data, int numGlyphs) throws IOException {
        int maxGlyphId = 0;
        long nbGroups = data.readUnsignedInt();
        this.glyphIdToCharacterCode = newGlyphIdToCharacterCode(numGlyphs);
        this.characterCodeToGlyphId = new HashMap(numGlyphs);
        if (numGlyphs == 0) {
            LOG.warn("subtable has no glyphs");
            return;
        }
        long j = 0;
        while (true) {
            long i = j;
            if (i < nbGroups) {
                long firstCode = data.readUnsignedInt();
                long endCode = data.readUnsignedInt();
                long startGlyph = data.readUnsignedInt();
                if (firstCode < 0 || firstCode > 1114111 || (firstCode >= 55296 && firstCode <= 57343)) {
                    throw new IOException("Invalid character code " + String.format("0x%X", Long.valueOf(firstCode)));
                }
                if ((endCode > 0 && endCode < firstCode) || endCode > 1114111 || (endCode >= 55296 && endCode <= 57343)) {
                    throw new IOException("Invalid character code " + String.format("0x%X", Long.valueOf(endCode)));
                }
                long j2 = 0;
                while (true) {
                    long j3 = j2;
                    if (j3 > endCode - firstCode) {
                        break;
                    }
                    long glyphIndex = startGlyph + j3;
                    if (glyphIndex >= numGlyphs) {
                        LOG.warn("Format 12 cmap contains an invalid glyph index");
                        break;
                    }
                    if (firstCode + j3 > 1114111) {
                        LOG.warn("Format 12 cmap contains character beyond UCS-4");
                    }
                    maxGlyphId = Math.max(maxGlyphId, (int) glyphIndex);
                    this.characterCodeToGlyphId.put(Integer.valueOf((int) (firstCode + j3)), Integer.valueOf((int) glyphIndex));
                    j2 = j3 + 1;
                }
                j = i + 1;
            } else {
                buildGlyphIdToCharacterCodeLookup(maxGlyphId);
                return;
            }
        }
    }

    void processSubtype13(TTFDataStream data, int numGlyphs) throws IOException {
        long nbGroups = data.readUnsignedInt();
        this.glyphIdToCharacterCode = newGlyphIdToCharacterCode(numGlyphs);
        this.characterCodeToGlyphId = new HashMap(numGlyphs);
        if (numGlyphs == 0) {
            LOG.warn("subtable has no glyphs");
            return;
        }
        long j = 0;
        while (true) {
            long i = j;
            if (i < nbGroups) {
                long firstCode = data.readUnsignedInt();
                long endCode = data.readUnsignedInt();
                long glyphId = data.readUnsignedInt();
                if (glyphId > numGlyphs) {
                    LOG.warn("Format 13 cmap contains an invalid glyph index");
                    return;
                }
                if (firstCode < 0 || firstCode > 1114111 || (firstCode >= 55296 && firstCode <= 57343)) {
                    throw new IOException("Invalid character code " + String.format("0x%X", Long.valueOf(firstCode)));
                }
                if ((endCode > 0 && endCode < firstCode) || endCode > 1114111 || (endCode >= 55296 && endCode <= 57343)) {
                    throw new IOException("Invalid character code " + String.format("0x%X", Long.valueOf(endCode)));
                }
                long j2 = 0;
                while (true) {
                    long j3 = j2;
                    if (j3 <= endCode - firstCode) {
                        if (firstCode + j3 > 2147483647L) {
                            throw new IOException("Character Code greater than Integer.MAX_VALUE");
                        }
                        if (firstCode + j3 > 1114111) {
                            LOG.warn("Format 13 cmap contains character beyond UCS-4");
                        }
                        this.glyphIdToCharacterCode[(int) glyphId] = (int) (firstCode + j3);
                        this.characterCodeToGlyphId.put(Integer.valueOf((int) (firstCode + j3)), Integer.valueOf((int) glyphId));
                        j2 = j3 + 1;
                    }
                }
                j = i + 1;
            } else {
                return;
            }
        }
    }

    void processSubtype14(TTFDataStream data, int numGlyphs) throws IOException {
        LOG.warn("Format 14 cmap table is not supported and will be ignored");
    }

    void processSubtype6(TTFDataStream data, int numGlyphs) throws IOException {
        int firstCode = data.readUnsignedShort();
        int entryCount = data.readUnsignedShort();
        if (entryCount == 0) {
            return;
        }
        this.characterCodeToGlyphId = new HashMap(numGlyphs);
        int[] glyphIdArray = data.readUnsignedShortArray(entryCount);
        int maxGlyphId = 0;
        for (int i = 0; i < entryCount; i++) {
            maxGlyphId = Math.max(maxGlyphId, glyphIdArray[i]);
            this.characterCodeToGlyphId.put(Integer.valueOf(firstCode + i), Integer.valueOf(glyphIdArray[i]));
        }
        buildGlyphIdToCharacterCodeLookup(maxGlyphId);
    }

    void processSubtype4(TTFDataStream data, int numGlyphs) throws IOException {
        int segCountX2 = data.readUnsignedShort();
        int segCount = segCountX2 / 2;
        data.readUnsignedShort();
        data.readUnsignedShort();
        data.readUnsignedShort();
        int[] endCount = data.readUnsignedShortArray(segCount);
        data.readUnsignedShort();
        int[] startCount = data.readUnsignedShortArray(segCount);
        int[] idDelta = data.readUnsignedShortArray(segCount);
        long idRangeOffsetPosition = data.getCurrentPosition();
        int[] idRangeOffset = data.readUnsignedShortArray(segCount);
        this.characterCodeToGlyphId = new HashMap(numGlyphs);
        int maxGlyphId = 0;
        for (int i = 0; i < segCount; i++) {
            int start = startCount[i];
            int end = endCount[i];
            int delta = idDelta[i];
            int rangeOffset = idRangeOffset[i];
            long segmentRangeOffset = idRangeOffsetPosition + ((long) (i * 2)) + ((long) rangeOffset);
            if (start != 65535 && end != 65535) {
                for (int j = start; j <= end; j++) {
                    if (rangeOffset == 0) {
                        int glyphid = (j + delta) & 65535;
                        maxGlyphId = Math.max(glyphid, maxGlyphId);
                        this.characterCodeToGlyphId.put(Integer.valueOf(j), Integer.valueOf(glyphid));
                    } else {
                        long glyphOffset = segmentRangeOffset + ((long) ((j - start) * 2));
                        data.seek(glyphOffset);
                        int glyphIndex = data.readUnsignedShort();
                        if (glyphIndex != 0) {
                            int glyphIndex2 = (glyphIndex + delta) & 65535;
                            maxGlyphId = Math.max(glyphIndex2, maxGlyphId);
                            this.characterCodeToGlyphId.put(Integer.valueOf(j), Integer.valueOf(glyphIndex2));
                        }
                    }
                }
            }
        }
        if (this.characterCodeToGlyphId.isEmpty()) {
            LOG.warn("cmap format 4 subtable is empty");
        } else {
            buildGlyphIdToCharacterCodeLookup(maxGlyphId);
        }
    }

    private void buildGlyphIdToCharacterCodeLookup(int maxGlyphId) {
        this.glyphIdToCharacterCode = newGlyphIdToCharacterCode(maxGlyphId + 1);
        for (Map.Entry<Integer, Integer> entry : this.characterCodeToGlyphId.entrySet()) {
            if (this.glyphIdToCharacterCode[entry.getValue().intValue()] == -1) {
                this.glyphIdToCharacterCode[entry.getValue().intValue()] = entry.getKey().intValue();
            } else {
                List<Integer> mappedValues = this.glyphIdToCharacterCodeMultiple.get(entry.getValue());
                if (mappedValues == null) {
                    mappedValues = new ArrayList();
                    this.glyphIdToCharacterCodeMultiple.put(entry.getValue(), mappedValues);
                    mappedValues.add(Integer.valueOf(this.glyphIdToCharacterCode[entry.getValue().intValue()]));
                    this.glyphIdToCharacterCode[entry.getValue().intValue()] = Integer.MIN_VALUE;
                }
                mappedValues.add(entry.getKey());
            }
        }
    }

    void processSubtype2(TTFDataStream data, int numGlyphs) throws IOException {
        int[] subHeaderKeys = new int[256];
        int maxSubHeaderIndex = 0;
        for (int i = 0; i < 256; i++) {
            subHeaderKeys[i] = data.readUnsignedShort();
            maxSubHeaderIndex = Math.max(maxSubHeaderIndex, subHeaderKeys[i] / 8);
        }
        SubHeader[] subHeaders = new SubHeader[maxSubHeaderIndex + 1];
        for (int i2 = 0; i2 <= maxSubHeaderIndex; i2++) {
            int firstCode = data.readUnsignedShort();
            int entryCount = data.readUnsignedShort();
            short idDelta = data.readSignedShort();
            int idRangeOffset = (data.readUnsignedShort() - ((((maxSubHeaderIndex + 1) - i2) - 1) * 8)) - 2;
            subHeaders[i2] = new SubHeader(firstCode, entryCount, idDelta, idRangeOffset);
        }
        long startGlyphIndexOffset = data.getCurrentPosition();
        this.glyphIdToCharacterCode = newGlyphIdToCharacterCode(numGlyphs);
        this.characterCodeToGlyphId = new HashMap(numGlyphs);
        if (numGlyphs == 0) {
            LOG.warn("subtable has no glyphs");
            return;
        }
        for (int i3 = 0; i3 <= maxSubHeaderIndex; i3++) {
            SubHeader sh = subHeaders[i3];
            int firstCode2 = sh.getFirstCode();
            int idRangeOffset2 = sh.getIdRangeOffset();
            int idDelta2 = sh.getIdDelta();
            int entryCount2 = sh.getEntryCount();
            data.seek(startGlyphIndexOffset + ((long) idRangeOffset2));
            for (int j = 0; j < entryCount2; j++) {
                int charCode = (i3 << 8) + firstCode2 + j;
                int p = data.readUnsignedShort();
                if (p > 0) {
                    p = (p + idDelta2) % 65536;
                    if (p < 0) {
                        p += 65536;
                    }
                }
                if (p >= numGlyphs) {
                    LOG.warn("glyphId " + p + " for charcode " + charCode + " ignored, numGlyphs is " + numGlyphs);
                } else {
                    this.glyphIdToCharacterCode[p] = charCode;
                    this.characterCodeToGlyphId.put(Integer.valueOf(charCode), Integer.valueOf(p));
                }
            }
        }
    }

    void processSubtype0(TTFDataStream data) throws IOException {
        byte[] glyphMapping = data.read(256);
        this.glyphIdToCharacterCode = newGlyphIdToCharacterCode(256);
        this.characterCodeToGlyphId = new HashMap(glyphMapping.length);
        for (int i = 0; i < glyphMapping.length; i++) {
            int glyphIndex = glyphMapping[i] & 255;
            this.glyphIdToCharacterCode[glyphIndex] = i;
            this.characterCodeToGlyphId.put(Integer.valueOf(i), Integer.valueOf(glyphIndex));
        }
    }

    private int[] newGlyphIdToCharacterCode(int size) {
        int[] gidToCode = new int[size];
        Arrays.fill(gidToCode, -1);
        return gidToCode;
    }

    public int getPlatformEncodingId() {
        return this.platformEncodingId;
    }

    public void setPlatformEncodingId(int platformEncodingIdValue) {
        this.platformEncodingId = platformEncodingIdValue;
    }

    public int getPlatformId() {
        return this.platformId;
    }

    public void setPlatformId(int platformIdValue) {
        this.platformId = platformIdValue;
    }

    @Override // org.apache.fontbox.ttf.CmapLookup
    public int getGlyphId(int characterCode) {
        Integer glyphId = this.characterCodeToGlyphId.get(Integer.valueOf(characterCode));
        if (glyphId == null) {
            return 0;
        }
        return glyphId.intValue();
    }

    @Deprecated
    public Integer getCharacterCode(int gid) {
        List<Integer> mappedValues;
        int code = getCharCode(gid);
        if (code == -1) {
            return null;
        }
        if (code == Integer.MIN_VALUE && (mappedValues = this.glyphIdToCharacterCodeMultiple.get(Integer.valueOf(gid))) != null) {
            return mappedValues.get(0);
        }
        return Integer.valueOf(code);
    }

    private int getCharCode(int gid) {
        if (gid < 0 || gid >= this.glyphIdToCharacterCode.length) {
            return -1;
        }
        return this.glyphIdToCharacterCode[gid];
    }

    @Override // org.apache.fontbox.ttf.CmapLookup
    public List<Integer> getCharCodes(int gid) {
        int code = getCharCode(gid);
        if (code == -1) {
            return null;
        }
        List<Integer> codes = null;
        if (code == Integer.MIN_VALUE) {
            List<Integer> mappedValues = this.glyphIdToCharacterCodeMultiple.get(Integer.valueOf(gid));
            if (mappedValues != null) {
                codes = new ArrayList<>(mappedValues);
                Collections.sort(codes);
            }
        } else {
            codes = new ArrayList<>(1);
            codes.add(Integer.valueOf(code));
        }
        return codes;
    }

    public String toString() {
        return "{" + getPlatformId() + " " + getPlatformEncodingId() + "}";
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/CmapSubtable$SubHeader.class */
    private static class SubHeader {
        private final int firstCode;
        private final int entryCount;
        private final short idDelta;
        private final int idRangeOffset;

        private SubHeader(int firstCodeValue, int entryCountValue, short idDeltaValue, int idRangeOffsetValue) {
            this.firstCode = firstCodeValue;
            this.entryCount = entryCountValue;
            this.idDelta = idDeltaValue;
            this.idRangeOffset = idRangeOffsetValue;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int getFirstCode() {
            return this.firstCode;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int getEntryCount() {
            return this.entryCount;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public short getIdDelta() {
            return this.idDelta;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int getIdRangeOffset() {
            return this.idRangeOffset;
        }
    }
}
