package org.apache.fontbox.ttf;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/KerningSubtable.class */
public class KerningSubtable {
    private static final Log LOG = LogFactory.getLog(KerningSubtable.class);
    private static final int COVERAGE_HORIZONTAL = 1;
    private static final int COVERAGE_MINIMUMS = 2;
    private static final int COVERAGE_CROSS_STREAM = 4;
    private static final int COVERAGE_FORMAT = 65280;
    private static final int COVERAGE_HORIZONTAL_SHIFT = 0;
    private static final int COVERAGE_MINIMUMS_SHIFT = 1;
    private static final int COVERAGE_CROSS_STREAM_SHIFT = 2;
    private static final int COVERAGE_FORMAT_SHIFT = 8;
    private boolean horizontal;
    private boolean minimums;
    private boolean crossStream;
    private PairData pairs;

    /* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/KerningSubtable$PairData.class */
    private interface PairData {
        void read(TTFDataStream tTFDataStream) throws IOException;

        int getKerning(int i, int i2);
    }

    KerningSubtable() {
    }

    void read(TTFDataStream data, int version) throws IOException {
        if (version == 0) {
            readSubtable0(data);
        } else {
            if (version == 1) {
                readSubtable1(data);
                return;
            }
            throw new IllegalStateException();
        }
    }

    public boolean isHorizontalKerning() {
        return isHorizontalKerning(false);
    }

    public boolean isHorizontalKerning(boolean cross) {
        if (!this.horizontal || this.minimums) {
            return false;
        }
        if (cross) {
            return this.crossStream;
        }
        return !this.crossStream;
    }

    public int[] getKerning(int[] glyphs) {
        int[] kerning = null;
        if (this.pairs != null) {
            int ng = glyphs.length;
            kerning = new int[ng];
            for (int i = 0; i < ng; i++) {
                int l = glyphs[i];
                int r = -1;
                for (int k = i + 1; k < ng; k++) {
                    int g = glyphs[k];
                    if (g >= 0) {
                        r = g;
                        break;
                    }
                }
                kerning[i] = getKerning(l, r);
            }
        } else {
            LOG.warn("No kerning subtable data available due to an unsupported kerning subtable version");
        }
        return kerning;
    }

    public int getKerning(int l, int r) {
        if (this.pairs == null) {
            LOG.warn("No kerning subtable data available due to an unsupported kerning subtable version");
            return 0;
        }
        return this.pairs.getKerning(l, r);
    }

    private void readSubtable0(TTFDataStream data) throws IOException {
        int version = data.readUnsignedShort();
        if (version != 0) {
            LOG.info("Unsupported kerning sub-table version: " + version);
            return;
        }
        int length = data.readUnsignedShort();
        if (length < 6) {
            throw new IOException("Kerning sub-table too short, got " + length + " bytes, expect 6 or more.");
        }
        int coverage = data.readUnsignedShort();
        if (isBitsSet(coverage, 1, 0)) {
            this.horizontal = true;
        }
        if (isBitsSet(coverage, 2, 1)) {
            this.minimums = true;
        }
        if (isBitsSet(coverage, 4, 2)) {
            this.crossStream = true;
        }
        int format = getBits(coverage, 65280, 8);
        if (format == 0) {
            readSubtable0Format0(data);
        } else if (format == 2) {
            readSubtable0Format2(data);
        } else {
            LOG.debug("Skipped kerning subtable due to an unsupported kerning subtable version: " + format);
        }
    }

    private void readSubtable0Format0(TTFDataStream data) throws IOException {
        this.pairs = new PairData0Format0();
        this.pairs.read(data);
    }

    private void readSubtable0Format2(TTFDataStream data) {
        LOG.info("Kerning subtable format 2 not yet supported.");
    }

    private void readSubtable1(TTFDataStream data) {
        LOG.info("Kerning subtable format 1 not yet supported.");
    }

    private static boolean isBitsSet(int bits, int mask, int shift) {
        return getBits(bits, mask, shift) != 0;
    }

    private static int getBits(int bits, int mask, int shift) {
        return (bits & mask) >> shift;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/KerningSubtable$PairData0Format0.class */
    private static class PairData0Format0 implements Comparator<int[]>, PairData {
        private int searchRange;
        private int[][] pairs;
        static final /* synthetic */ boolean $assertionsDisabled;

        private PairData0Format0() {
        }

        static {
            $assertionsDisabled = !KerningSubtable.class.desiredAssertionStatus();
        }

        @Override // org.apache.fontbox.ttf.KerningSubtable.PairData
        public void read(TTFDataStream data) throws IOException {
            int numPairs = data.readUnsignedShort();
            this.searchRange = data.readUnsignedShort() / 6;
            data.readUnsignedShort();
            data.readUnsignedShort();
            this.pairs = new int[numPairs][3];
            for (int i = 0; i < numPairs; i++) {
                int left = data.readUnsignedShort();
                int right = data.readUnsignedShort();
                int value = data.readSignedShort();
                this.pairs[i][0] = left;
                this.pairs[i][1] = right;
                this.pairs[i][2] = value;
            }
        }

        @Override // org.apache.fontbox.ttf.KerningSubtable.PairData
        public int getKerning(int l, int r) {
            int[] key = {l, r, 0};
            int index = Arrays.binarySearch(this.pairs, key, this);
            if (index >= 0) {
                return this.pairs[index][2];
            }
            return 0;
        }

        @Override // java.util.Comparator
        public int compare(int[] p1, int[] p2) {
            if (!$assertionsDisabled && p1 == null) {
                throw new AssertionError();
            }
            if (!$assertionsDisabled && p1.length < 2) {
                throw new AssertionError();
            }
            if (!$assertionsDisabled && p2 == null) {
                throw new AssertionError();
            }
            if (!$assertionsDisabled && p2.length < 2) {
                throw new AssertionError();
            }
            int l1 = p1[0];
            int l2 = p2[0];
            if (l1 < l2) {
                return -1;
            }
            if (l1 > l2) {
                return 1;
            }
            int r1 = p1[1];
            int r2 = p2[1];
            if (r1 < r2) {
                return -1;
            }
            if (r1 > r2) {
                return 1;
            }
            return 0;
        }
    }
}
