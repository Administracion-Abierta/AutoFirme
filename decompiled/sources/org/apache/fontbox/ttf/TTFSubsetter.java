package org.apache.fontbox.ttf;

import com.ctc.wstx.io.CharsetNames;
import com.sun.jna.platform.win32.WinError;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/TTFSubsetter.class */
public final class TTFSubsetter {
    private static final Log LOG = LogFactory.getLog(TTFSubsetter.class);
    private static final byte[] PAD_BUF = {0, 0, 0};
    private final TrueTypeFont ttf;
    private final CmapLookup unicodeCmap;
    private final SortedMap<Integer, Integer> uniToGID;
    private final List<String> keepTables;
    private final SortedSet<Integer> glyphIds;
    private String prefix;
    private boolean hasAddedCompoundReferences;

    public TTFSubsetter(TrueTypeFont ttf) throws IOException {
        this(ttf, null);
    }

    public TTFSubsetter(TrueTypeFont ttf, List<String> tables) throws IOException {
        this.ttf = ttf;
        this.keepTables = tables;
        this.uniToGID = new TreeMap();
        this.glyphIds = new TreeSet();
        this.unicodeCmap = ttf.getUnicodeCmapLookup();
        this.glyphIds.add(0);
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void add(int unicode) {
        int gid = this.unicodeCmap.getGlyphId(unicode);
        if (gid != 0) {
            this.uniToGID.put(Integer.valueOf(unicode), Integer.valueOf(gid));
            this.glyphIds.add(Integer.valueOf(gid));
        }
    }

    public void addAll(Set<Integer> unicodeSet) {
        Iterator<Integer> it = unicodeSet.iterator();
        while (it.hasNext()) {
            int unicode = it.next().intValue();
            add(unicode);
        }
    }

    public Map<Integer, Integer> getGIDMap() throws IOException {
        addCompoundReferences();
        Map<Integer, Integer> newToOld = new HashMap<>();
        int newGID = 0;
        Iterator<Integer> it = this.glyphIds.iterator();
        while (it.hasNext()) {
            int oldGID = it.next().intValue();
            newToOld.put(Integer.valueOf(newGID), Integer.valueOf(oldGID));
            newGID++;
        }
        return newToOld;
    }

    private long writeFileHeader(DataOutputStream out, int nTables) throws IOException {
        out.writeInt(65536);
        out.writeShort(nTables);
        int mask = Integer.highestOneBit(nTables);
        int searchRange = mask * 16;
        out.writeShort(searchRange);
        int entrySelector = log2(mask);
        out.writeShort(entrySelector);
        int last = (16 * nTables) - searchRange;
        out.writeShort(last);
        return 65536 + toUInt32(nTables, searchRange) + toUInt32(entrySelector, last);
    }

    private long writeTableHeader(DataOutputStream out, String tag, long offset, byte[] bytes) throws IOException {
        long checksum = 0;
        int n = bytes.length;
        for (int nup = 0; nup < n; nup++) {
            checksum += (((long) bytes[nup]) & 255) << (24 - ((nup % 4) * 8));
        }
        long checksum2 = checksum & 4294967295L;
        byte[] tagbytes = tag.getBytes(CharsetNames.CS_US_ASCII);
        out.write(tagbytes, 0, 4);
        out.writeInt((int) checksum2);
        out.writeInt((int) offset);
        out.writeInt(bytes.length);
        return toUInt32(tagbytes) + checksum2 + checksum2 + offset + ((long) bytes.length);
    }

    private void writeTableBody(OutputStream os, byte[] bytes) throws IOException {
        int n = bytes.length;
        os.write(bytes);
        if (n % 4 != 0) {
            os.write(PAD_BUF, 0, 4 - (n % 4));
        }
    }

    private byte[] buildHeadTable() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bos);
        HeaderTable h = this.ttf.getHeader();
        writeFixed(out, h.getVersion());
        writeFixed(out, h.getFontRevision());
        writeUint32(out, 0L);
        writeUint32(out, h.getMagicNumber());
        writeUint16(out, h.getFlags());
        writeUint16(out, h.getUnitsPerEm());
        writeLongDateTime(out, h.getCreated());
        writeLongDateTime(out, h.getModified());
        writeSInt16(out, h.getXMin());
        writeSInt16(out, h.getYMin());
        writeSInt16(out, h.getXMax());
        writeSInt16(out, h.getYMax());
        writeUint16(out, h.getMacStyle());
        writeUint16(out, h.getLowestRecPPEM());
        writeSInt16(out, h.getFontDirectionHint());
        writeSInt16(out, (short) 1);
        writeSInt16(out, h.getGlyphDataFormat());
        out.flush();
        return bos.toByteArray();
    }

    private byte[] buildHheaTable() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bos);
        HorizontalHeaderTable h = this.ttf.getHorizontalHeader();
        writeFixed(out, h.getVersion());
        writeSInt16(out, h.getAscender());
        writeSInt16(out, h.getDescender());
        writeSInt16(out, h.getLineGap());
        writeUint16(out, h.getAdvanceWidthMax());
        writeSInt16(out, h.getMinLeftSideBearing());
        writeSInt16(out, h.getMinRightSideBearing());
        writeSInt16(out, h.getXMaxExtent());
        writeSInt16(out, h.getCaretSlopeRise());
        writeSInt16(out, h.getCaretSlopeRun());
        writeSInt16(out, h.getReserved1());
        writeSInt16(out, h.getReserved2());
        writeSInt16(out, h.getReserved3());
        writeSInt16(out, h.getReserved4());
        writeSInt16(out, h.getReserved5());
        writeSInt16(out, h.getMetricDataFormat());
        int hmetrics = this.glyphIds.subSet(0, Integer.valueOf(h.getNumberOfHMetrics())).size();
        if (this.glyphIds.last().intValue() >= h.getNumberOfHMetrics() && !this.glyphIds.contains(Integer.valueOf(h.getNumberOfHMetrics() - 1))) {
            hmetrics++;
        }
        writeUint16(out, hmetrics);
        out.flush();
        return bos.toByteArray();
    }

    private boolean shouldCopyNameRecord(NameRecord nr) {
        return nr.getPlatformId() == 3 && nr.getPlatformEncodingId() == 1 && nr.getLanguageId() == 1033 && nr.getNameId() >= 0 && nr.getNameId() < 7;
    }

    private byte[] buildNameTable() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bos);
        NamingTable name = this.ttf.getNaming();
        if (name == null) {
            return null;
        }
        if (this.keepTables != null && !this.keepTables.contains("name")) {
            return null;
        }
        List<NameRecord> nameRecords = name.getNameRecords();
        int numRecords = 0;
        Iterator<NameRecord> it = nameRecords.iterator();
        while (it.hasNext()) {
            if (shouldCopyNameRecord(it.next())) {
                numRecords++;
            }
        }
        writeUint16(out, 0);
        writeUint16(out, numRecords);
        writeUint16(out, 6 + (12 * numRecords));
        if (numRecords == 0) {
            return null;
        }
        byte[][] names = new byte[numRecords][];
        int j = 0;
        for (NameRecord record : nameRecords) {
            if (shouldCopyNameRecord(record)) {
                int platform = record.getPlatformId();
                int encoding = record.getPlatformEncodingId();
                String charset = "ISO-8859-1";
                if (platform == 3 && encoding == 1) {
                    charset = CharsetNames.CS_UTF16BE;
                } else if (platform == 2) {
                    if (encoding == 0) {
                        charset = CharsetNames.CS_US_ASCII;
                    } else if (encoding == 1) {
                        charset = "UTF16-BE";
                    } else if (encoding == 2) {
                        charset = "ISO-8859-1";
                    }
                }
                String value = record.getString();
                if (record.getNameId() == 6 && this.prefix != null) {
                    value = this.prefix + value;
                }
                names[j] = value.getBytes(charset);
                j++;
            }
        }
        int offset = 0;
        int j2 = 0;
        for (NameRecord nr : nameRecords) {
            if (shouldCopyNameRecord(nr)) {
                writeUint16(out, nr.getPlatformId());
                writeUint16(out, nr.getPlatformEncodingId());
                writeUint16(out, nr.getLanguageId());
                writeUint16(out, nr.getNameId());
                writeUint16(out, names[j2].length);
                writeUint16(out, offset);
                offset += names[j2].length;
                j2++;
            }
        }
        for (int i = 0; i < numRecords; i++) {
            out.write(names[i]);
        }
        out.flush();
        return bos.toByteArray();
    }

    private byte[] buildMaxpTable() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bos);
        MaximumProfileTable p = this.ttf.getMaximumProfile();
        writeFixed(out, 1.0d);
        writeUint16(out, this.glyphIds.size());
        writeUint16(out, p.getMaxPoints());
        writeUint16(out, p.getMaxContours());
        writeUint16(out, p.getMaxCompositePoints());
        writeUint16(out, p.getMaxCompositeContours());
        writeUint16(out, p.getMaxZones());
        writeUint16(out, p.getMaxTwilightPoints());
        writeUint16(out, p.getMaxStorage());
        writeUint16(out, p.getMaxFunctionDefs());
        writeUint16(out, p.getMaxInstructionDefs());
        writeUint16(out, p.getMaxStackElements());
        writeUint16(out, p.getMaxSizeOfInstructions());
        writeUint16(out, p.getMaxComponentElements());
        writeUint16(out, p.getMaxComponentDepth());
        out.flush();
        return bos.toByteArray();
    }

    private byte[] buildOS2Table() throws IOException {
        OS2WindowsMetricsTable os2 = this.ttf.getOS2Windows();
        if (os2 == null || this.uniToGID.isEmpty()) {
            return null;
        }
        if (this.keepTables != null && !this.keepTables.contains(OS2WindowsMetricsTable.TAG)) {
            return null;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bos);
        writeUint16(out, os2.getVersion());
        writeSInt16(out, os2.getAverageCharWidth());
        writeUint16(out, os2.getWeightClass());
        writeUint16(out, os2.getWidthClass());
        writeSInt16(out, os2.getFsType());
        writeSInt16(out, os2.getSubscriptXSize());
        writeSInt16(out, os2.getSubscriptYSize());
        writeSInt16(out, os2.getSubscriptXOffset());
        writeSInt16(out, os2.getSubscriptYOffset());
        writeSInt16(out, os2.getSuperscriptXSize());
        writeSInt16(out, os2.getSuperscriptYSize());
        writeSInt16(out, os2.getSuperscriptXOffset());
        writeSInt16(out, os2.getSuperscriptYOffset());
        writeSInt16(out, os2.getStrikeoutSize());
        writeSInt16(out, os2.getStrikeoutPosition());
        writeSInt16(out, (short) os2.getFamilyClass());
        out.write(os2.getPanose());
        writeUint32(out, 0L);
        writeUint32(out, 0L);
        writeUint32(out, 0L);
        writeUint32(out, 0L);
        out.write(os2.getAchVendId().getBytes(CharsetNames.CS_US_ASCII));
        writeUint16(out, os2.getFsSelection());
        writeUint16(out, this.uniToGID.firstKey().intValue());
        writeUint16(out, this.uniToGID.lastKey().intValue());
        writeUint16(out, os2.getTypoAscender());
        writeUint16(out, os2.getTypoDescender());
        writeUint16(out, os2.getTypoLineGap());
        writeUint16(out, os2.getWinAscent());
        writeUint16(out, os2.getWinDescent());
        out.flush();
        return bos.toByteArray();
    }

    private byte[] buildLocaTable(long[] newOffsets) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bos);
        for (long offset : newOffsets) {
            writeUint32(out, offset);
        }
        out.flush();
        return bos.toByteArray();
    }

    private void addCompoundReferences() throws IOException {
        boolean hasNested;
        int flags;
        if (this.hasAddedCompoundReferences) {
            return;
        }
        this.hasAddedCompoundReferences = true;
        GlyphTable g = this.ttf.getGlyph();
        long[] offsets = this.ttf.getIndexToLocation().getOffsets();
        do {
            InputStream is = this.ttf.getOriginalData();
            Set<Integer> glyphIdsToAdd = null;
            try {
                is.skip(g.getOffset());
                long lastOff = 0;
                for (Integer glyphId : this.glyphIds) {
                    long offset = offsets[glyphId.intValue()];
                    long len = offsets[glyphId.intValue() + 1] - offset;
                    is.skip(offset - lastOff);
                    byte[] buf = new byte[(int) len];
                    is.read(buf);
                    if (buf.length >= 2 && buf[0] == -1 && buf[1] == -1) {
                        int off = 10;
                        do {
                            flags = ((buf[off] & 255) << 8) | (buf[off + 1] & 255);
                            int off2 = off + 2;
                            int ogid = ((buf[off2] & 255) << 8) | (buf[off2 + 1] & 255);
                            if (!this.glyphIds.contains(Integer.valueOf(ogid))) {
                                if (glyphIdsToAdd == null) {
                                    glyphIdsToAdd = new TreeSet<>();
                                }
                                glyphIdsToAdd.add(Integer.valueOf(ogid));
                            }
                            int off3 = off2 + 2;
                            if ((flags & 1) != 0) {
                                off = off3 + 4;
                            } else {
                                off = off3 + 2;
                            }
                            if ((flags & 128) != 0) {
                                off += 8;
                            } else if ((flags & 64) != 0) {
                                off += 4;
                            } else if ((flags & 8) != 0) {
                                off += 2;
                            }
                        } while ((flags & 32) != 0);
                    }
                    lastOff = offsets[glyphId.intValue() + 1];
                }
                is.close();
                if (glyphIdsToAdd != null) {
                    this.glyphIds.addAll(glyphIdsToAdd);
                }
                hasNested = glyphIdsToAdd != null;
            } catch (Throwable th) {
                is.close();
                throw th;
            }
        } while (hasNested);
    }

    private byte[] buildGlyfTable(long[] newOffsets) throws IOException {
        int flags;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        GlyphTable g = this.ttf.getGlyph();
        long[] offsets = this.ttf.getIndexToLocation().getOffsets();
        InputStream is = this.ttf.getOriginalData();
        try {
            is.skip(g.getOffset());
            long prevEnd = 0;
            long newOffset = 0;
            int newGid = 0;
            for (Integer gid : this.glyphIds) {
                long offset = offsets[gid.intValue()];
                long length = offsets[gid.intValue() + 1] - offset;
                int i = newGid;
                newGid++;
                newOffsets[i] = newOffset;
                is.skip(offset - prevEnd);
                byte[] buf = new byte[(int) length];
                is.read(buf);
                if (buf.length >= 2 && buf[0] == -1 && buf[1] == -1) {
                    int off = 10;
                    do {
                        flags = ((buf[off] & 255) << 8) | (buf[off + 1] & 255);
                        int off2 = off + 2;
                        int componentGid = ((buf[off2] & 255) << 8) | (buf[off2 + 1] & 255);
                        this.glyphIds.add(Integer.valueOf(componentGid));
                        int newComponentGid = getNewGlyphId(Integer.valueOf(componentGid));
                        buf[off2] = (byte) (newComponentGid >>> 8);
                        buf[off2 + 1] = (byte) newComponentGid;
                        int off3 = off2 + 2;
                        if ((flags & 1) != 0) {
                            off = off3 + 4;
                        } else {
                            off = off3 + 2;
                        }
                        if ((flags & 128) != 0) {
                            off += 8;
                        } else if ((flags & 64) != 0) {
                            off += 4;
                        } else if ((flags & 8) != 0) {
                            off += 2;
                        }
                    } while ((flags & 32) != 0);
                    if ((flags & 256) == 256) {
                        int numInstr = ((buf[off] & 255) << 8) | (buf[off + 1] & 255);
                        off = off + 2 + numInstr;
                    }
                    bos.write(buf, 0, off);
                    newOffset += (long) off;
                } else if (buf.length > 0) {
                    bos.write(buf, 0, buf.length);
                    newOffset += (long) buf.length;
                }
                if (newOffset % 4 != 0) {
                    int len = 4 - ((int) (newOffset % 4));
                    bos.write(PAD_BUF, 0, len);
                    newOffset += (long) len;
                }
                prevEnd = offset + length;
            }
            int i2 = newGid;
            int i3 = newGid + 1;
            newOffsets[i2] = newOffset;
            return bos.toByteArray();
        } finally {
            is.close();
        }
    }

    private int getNewGlyphId(Integer oldGid) {
        return this.glyphIds.headSet(oldGid).size();
    }

    private byte[] buildCmapTable() throws IOException {
        if (this.ttf.getCmap() == null || this.uniToGID.isEmpty()) {
            return null;
        }
        if (this.keepTables != null && !this.keepTables.contains(CmapTable.TAG)) {
            return null;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bos);
        writeUint16(out, 0);
        writeUint16(out, 1);
        writeUint16(out, 3);
        writeUint16(out, 1);
        writeUint32(out, 12L);
        Iterator<Map.Entry<Integer, Integer>> it = this.uniToGID.entrySet().iterator();
        Map.Entry<Integer, Integer> lastChar = it.next();
        Map.Entry<Integer, Integer> prevChar = lastChar;
        int lastGid = getNewGlyphId(lastChar.getValue());
        int[] startCode = new int[this.uniToGID.size() + 1];
        int[] endCode = new int[startCode.length];
        int[] idDelta = new int[startCode.length];
        int segCount = 0;
        while (it.hasNext()) {
            Map.Entry<Integer, Integer> curChar2Gid = it.next();
            int curGid = getNewGlyphId(curChar2Gid.getValue());
            if (curChar2Gid.getKey().intValue() > 65535) {
                throw new UnsupportedOperationException("non-BMP Unicode character");
            }
            if (curChar2Gid.getKey().intValue() != prevChar.getKey().intValue() + 1 || curGid - lastGid != curChar2Gid.getKey().intValue() - lastChar.getKey().intValue()) {
                if (lastGid != 0) {
                    startCode[segCount] = lastChar.getKey().intValue();
                    endCode[segCount] = prevChar.getKey().intValue();
                    idDelta[segCount] = lastGid - lastChar.getKey().intValue();
                    segCount++;
                } else if (!lastChar.getKey().equals(prevChar.getKey())) {
                    startCode[segCount] = lastChar.getKey().intValue() + 1;
                    endCode[segCount] = prevChar.getKey().intValue();
                    idDelta[segCount] = lastGid - lastChar.getKey().intValue();
                    segCount++;
                }
                lastGid = curGid;
                lastChar = curChar2Gid;
            }
            prevChar = curChar2Gid;
        }
        startCode[segCount] = lastChar.getKey().intValue();
        endCode[segCount] = prevChar.getKey().intValue();
        idDelta[segCount] = lastGid - lastChar.getKey().intValue();
        int segCount2 = segCount + 1;
        startCode[segCount2] = 65535;
        endCode[segCount2] = 65535;
        idDelta[segCount2] = 1;
        int segCount3 = segCount2 + 1;
        int searchRange = 2 * ((int) Math.pow(2.0d, log2(segCount3)));
        writeUint16(out, 4);
        writeUint16(out, 16 + (segCount3 * 4 * 2));
        writeUint16(out, 0);
        writeUint16(out, segCount3 * 2);
        writeUint16(out, searchRange);
        writeUint16(out, log2(searchRange / 2));
        writeUint16(out, (2 * segCount3) - searchRange);
        for (int i = 0; i < segCount3; i++) {
            writeUint16(out, endCode[i]);
        }
        writeUint16(out, 0);
        for (int i2 = 0; i2 < segCount3; i2++) {
            writeUint16(out, startCode[i2]);
        }
        for (int i3 = 0; i3 < segCount3; i3++) {
            writeUint16(out, idDelta[i3]);
        }
        for (int i4 = 0; i4 < segCount3; i4++) {
            writeUint16(out, 0);
        }
        return bos.toByteArray();
    }

    private byte[] buildPostTable() throws IOException {
        PostScriptTable post = this.ttf.getPostScript();
        if (post == null) {
            return null;
        }
        if (this.keepTables != null && !this.keepTables.contains(PostScriptTable.TAG)) {
            return null;
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bos);
        writeFixed(out, 2.0d);
        writeFixed(out, post.getItalicAngle());
        writeSInt16(out, post.getUnderlinePosition());
        writeSInt16(out, post.getUnderlineThickness());
        writeUint32(out, post.getIsFixedPitch());
        writeUint32(out, post.getMinMemType42());
        writeUint32(out, post.getMaxMemType42());
        writeUint32(out, post.getMinMemType1());
        writeUint32(out, post.getMaxMemType1());
        writeUint16(out, this.glyphIds.size());
        Map<String, Integer> names = new LinkedHashMap<>();
        Iterator<Integer> it = this.glyphIds.iterator();
        while (it.hasNext()) {
            int gid = it.next().intValue();
            String name = post.getName(gid);
            Integer macId = WGL4Names.MAC_GLYPH_NAMES_INDICES.get(name);
            if (macId != null) {
                writeUint16(out, macId.intValue());
            } else {
                Integer ordinal = names.get(name);
                if (ordinal == null) {
                    ordinal = Integer.valueOf(names.size());
                    names.put(name, ordinal);
                }
                writeUint16(out, 258 + ordinal.intValue());
            }
        }
        Iterator<String> it2 = names.keySet().iterator();
        while (it2.hasNext()) {
            byte[] buf = it2.next().getBytes(Charset.forName(CharsetNames.CS_US_ASCII));
            writeUint8(out, buf.length);
            out.write(buf);
        }
        out.flush();
        return bos.toByteArray();
    }

    private byte[] buildHmtxTable() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        HorizontalHeaderTable h = this.ttf.getHorizontalHeader();
        HorizontalMetricsTable hm = this.ttf.getHorizontalMetrics();
        InputStream is = this.ttf.getOriginalData();
        int lastgid = h.getNumberOfHMetrics() - 1;
        boolean needLastGidWidth = this.glyphIds.last().intValue() > lastgid && !this.glyphIds.contains(Integer.valueOf(lastgid));
        try {
            is.skip(hm.getOffset());
            long lastOffset = 0;
            for (Integer glyphId : this.glyphIds) {
                if (glyphId.intValue() <= lastgid) {
                    long offset = ((long) glyphId.intValue()) * 4;
                    lastOffset = copyBytes(is, bos, offset, lastOffset, 4);
                } else {
                    if (needLastGidWidth) {
                        needLastGidWidth = false;
                        long offset2 = ((long) lastgid) * 4;
                        lastOffset = copyBytes(is, bos, offset2, lastOffset, 2);
                    }
                    long offset3 = (((long) h.getNumberOfHMetrics()) * 4) + (((long) (glyphId.intValue() - h.getNumberOfHMetrics())) * 2);
                    lastOffset = copyBytes(is, bos, offset3, lastOffset, 2);
                }
            }
            return bos.toByteArray();
        } finally {
            is.close();
        }
    }

    private long copyBytes(InputStream is, OutputStream os, long newOffset, long lastOffset, int count) throws IOException {
        long nskip = newOffset - lastOffset;
        if (nskip != is.skip(nskip)) {
            throw new EOFException("Unexpected EOF exception parsing glyphId of hmtx table.");
        }
        byte[] buf = new byte[count];
        if (count != is.read(buf, 0, count)) {
            throw new EOFException("Unexpected EOF exception parsing glyphId of hmtx table.");
        }
        os.write(buf, 0, count);
        return newOffset + ((long) count);
    }

    public void writeToStream(OutputStream os) throws IOException {
        if (this.glyphIds.isEmpty() || this.uniToGID.isEmpty()) {
            LOG.info("font subset is empty");
        }
        addCompoundReferences();
        DataOutputStream out = new DataOutputStream(os);
        try {
            long[] newLoca = new long[this.glyphIds.size() + 1];
            byte[] head = buildHeadTable();
            byte[] hhea = buildHheaTable();
            byte[] maxp = buildMaxpTable();
            byte[] name = buildNameTable();
            byte[] os2 = buildOS2Table();
            byte[] glyf = buildGlyfTable(newLoca);
            byte[] loca = buildLocaTable(newLoca);
            byte[] cmap = buildCmapTable();
            byte[] hmtx = buildHmtxTable();
            byte[] post = buildPostTable();
            Map<String, byte[]> tables = new TreeMap<>();
            if (os2 != null) {
                tables.put(OS2WindowsMetricsTable.TAG, os2);
            }
            if (cmap != null) {
                tables.put(CmapTable.TAG, cmap);
            }
            tables.put(GlyphTable.TAG, glyf);
            tables.put(HeaderTable.TAG, head);
            tables.put(HorizontalHeaderTable.TAG, hhea);
            tables.put(HorizontalMetricsTable.TAG, hmtx);
            tables.put(IndexToLocationTable.TAG, loca);
            tables.put(MaximumProfileTable.TAG, maxp);
            if (name != null) {
                tables.put("name", name);
            }
            if (post != null) {
                tables.put(PostScriptTable.TAG, post);
            }
            for (Map.Entry<String, TTFTable> entry : this.ttf.getTableMap().entrySet()) {
                String tag = entry.getKey();
                TTFTable table = entry.getValue();
                if (!tables.containsKey(tag) && (this.keepTables == null || this.keepTables.contains(tag))) {
                    tables.put(tag, this.ttf.getTableBytes(table));
                }
            }
            long checksum = writeFileHeader(out, tables.size());
            long offset = 12 + (16 * ((long) tables.size()));
            for (Map.Entry<String, byte[]> entry2 : tables.entrySet()) {
                checksum += writeTableHeader(out, entry2.getKey(), offset, entry2.getValue());
                offset += (long) (((entry2.getValue().length + 3) / 4) * 4);
            }
            long checksum2 = 2981146554L - (checksum & 4294967295L);
            head[8] = (byte) (checksum2 >>> 24);
            head[9] = (byte) (checksum2 >>> 16);
            head[10] = (byte) (checksum2 >>> 8);
            head[11] = (byte) checksum2;
            for (byte[] bytes : tables.values()) {
                writeTableBody(out, bytes);
            }
        } finally {
            out.close();
        }
    }

    private void writeFixed(DataOutputStream out, double f) throws IOException {
        double ip = Math.floor(f);
        double fp = (f - ip) * 65536.0d;
        out.writeShort((int) ip);
        out.writeShort((int) fp);
    }

    private void writeUint32(DataOutputStream out, long l) throws IOException {
        out.writeInt((int) l);
    }

    private void writeUint16(DataOutputStream out, int i) throws IOException {
        out.writeShort(i);
    }

    private void writeSInt16(DataOutputStream out, short i) throws IOException {
        out.writeShort(i);
    }

    private void writeUint8(DataOutputStream out, int i) throws IOException {
        out.writeByte(i);
    }

    private void writeLongDateTime(DataOutputStream out, Calendar calendar) throws IOException {
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.set(WinError.ERROR_ALREADY_WAITING, 0, 1, 0, 0, 0);
        cal.set(14, 0);
        long millisFor1904 = cal.getTimeInMillis();
        long secondsSince1904 = (calendar.getTimeInMillis() - millisFor1904) / 1000;
        out.writeLong(secondsSince1904);
    }

    private long toUInt32(int high, int low) {
        return ((((long) high) & 65535) << 16) | (((long) low) & 65535);
    }

    private long toUInt32(byte[] bytes) {
        return ((((long) bytes[0]) & 255) << 24) | ((((long) bytes[1]) & 255) << 16) | ((((long) bytes[2]) & 255) << 8) | (((long) bytes[3]) & 255);
    }

    private int log2(int num) {
        return (int) Math.floor(Math.log(num) / Math.log(2.0d));
    }
}
