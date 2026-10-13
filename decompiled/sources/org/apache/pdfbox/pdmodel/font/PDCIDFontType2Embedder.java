package org.apache.pdfbox.pdmodel.font;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.ttf.GlyphData;
import org.apache.fontbox.ttf.GlyphTable;
import org.apache.fontbox.ttf.HorizontalMetricsTable;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.fontbox.ttf.VerticalHeaderTable;
import org.apache.fontbox.ttf.VerticalMetricsTable;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDStream;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/font/PDCIDFontType2Embedder.class */
final class PDCIDFontType2Embedder extends TrueTypeEmbedder {
    private static final Log LOG = LogFactory.getLog(PDCIDFontType2Embedder.class);
    private final PDDocument document;
    private final PDType0Font parent;
    private final COSDictionary dict;
    private final COSDictionary cidFont;
    private final boolean vertical;

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/font/PDCIDFontType2Embedder$State.class */
    enum State {
        FIRST,
        BRACKET,
        SERIAL
    }

    PDCIDFontType2Embedder(PDDocument document, COSDictionary dict, TrueTypeFont ttf, boolean embedSubset, PDType0Font parent, boolean vertical) throws IOException {
        super(document, dict, ttf, embedSubset);
        this.document = document;
        this.dict = dict;
        this.parent = parent;
        this.vertical = vertical;
        dict.setItem(COSName.SUBTYPE, COSName.TYPE0);
        dict.setName(COSName.BASE_FONT, this.fontDescriptor.getFontName());
        dict.setItem(COSName.ENCODING, vertical ? COSName.IDENTITY_V : COSName.IDENTITY_H);
        this.cidFont = createCIDFont();
        COSArray descendantFonts = new COSArray();
        descendantFonts.add((COSBase) this.cidFont);
        dict.setItem(COSName.DESCENDANT_FONTS, (COSBase) descendantFonts);
        if (!embedSubset) {
            buildToUnicodeCMap(null);
        }
    }

    @Override // org.apache.pdfbox.pdmodel.font.TrueTypeEmbedder
    protected void buildSubset(InputStream ttfSubset, String tag, Map<Integer, Integer> gidToCid) throws IOException {
        Map<Integer, Integer> cidToGid = new HashMap<>(gidToCid.size());
        for (Map.Entry<Integer, Integer> entry : gidToCid.entrySet()) {
            int newGID = entry.getKey().intValue();
            int oldGID = entry.getValue().intValue();
            cidToGid.put(Integer.valueOf(oldGID), Integer.valueOf(newGID));
        }
        buildToUnicodeCMap(gidToCid);
        if (this.vertical) {
            buildVerticalMetrics(cidToGid);
        }
        buildFontFile2(ttfSubset);
        addNameTag(tag);
        buildWidths(cidToGid);
        buildCIDToGIDMap(cidToGid);
        buildCIDSet(cidToGid);
    }

    private void buildToUnicodeCMap(Map<Integer, Integer> newGIDToOldCID) throws IOException {
        int cid;
        ToUnicodeWriter toUniWriter = new ToUnicodeWriter();
        boolean hasSurrogates = false;
        int max = this.ttf.getMaximumProfile().getNumGlyphs();
        for (int gid = 1; gid <= max; gid++) {
            if (newGIDToOldCID != null) {
                if (newGIDToOldCID.containsKey(Integer.valueOf(gid))) {
                    cid = newGIDToOldCID.get(Integer.valueOf(gid)).intValue();
                }
            } else {
                cid = gid;
            }
            List<Integer> codes = this.cmapLookup.getCharCodes(cid);
            if (codes != null) {
                int codePoint = codes.get(0).intValue();
                if (codePoint > 65535) {
                    hasSurrogates = true;
                }
                toUniWriter.add(cid, new String(new int[]{codePoint}, 0, 1));
            }
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        toUniWriter.writeTo(out);
        InputStream cMapStream = new ByteArrayInputStream(out.toByteArray());
        PDStream stream = new PDStream(this.document, cMapStream, COSName.FLATE_DECODE);
        if (hasSurrogates) {
            float version = this.document.getVersion();
            if (version < 1.5d) {
                this.document.setVersion(1.5f);
            }
        }
        this.dict.setItem(COSName.TO_UNICODE, stream);
    }

    private COSDictionary toCIDSystemInfo(String registry, String ordering, int supplement) {
        COSDictionary info = new COSDictionary();
        info.setString(COSName.REGISTRY, registry);
        info.setString(COSName.ORDERING, ordering);
        info.setInt(COSName.SUPPLEMENT, supplement);
        return info;
    }

    private COSDictionary createCIDFont() throws IOException {
        COSDictionary cidFont = new COSDictionary();
        cidFont.setItem(COSName.TYPE, (COSBase) COSName.FONT);
        cidFont.setItem(COSName.SUBTYPE, (COSBase) COSName.CID_FONT_TYPE2);
        cidFont.setName(COSName.BASE_FONT, this.fontDescriptor.getFontName());
        COSDictionary info = toCIDSystemInfo("Adobe", "Identity", 0);
        cidFont.setItem(COSName.CIDSYSTEMINFO, (COSBase) info);
        cidFont.setItem(COSName.FONT_DESC, (COSBase) this.fontDescriptor.getCOSObject());
        buildWidths(cidFont);
        if (this.vertical) {
            buildVerticalMetrics(cidFont);
        }
        cidFont.setItem(COSName.CID_TO_GID_MAP, (COSBase) COSName.IDENTITY);
        return cidFont;
    }

    private void addNameTag(String tag) {
        String name = this.fontDescriptor.getFontName();
        String newName = tag + name;
        this.dict.setName(COSName.BASE_FONT, newName);
        this.fontDescriptor.setFontName(newName);
        this.cidFont.setName(COSName.BASE_FONT, newName);
    }

    private void buildCIDToGIDMap(Map<Integer, Integer> cidToGid) throws IOException {
        int iIntValue;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int cidMax = ((Integer) Collections.max(cidToGid.keySet())).intValue();
        for (int i = 0; i <= cidMax; i++) {
            if (cidToGid.containsKey(Integer.valueOf(i))) {
                iIntValue = cidToGid.get(Integer.valueOf(i)).intValue();
            } else {
                iIntValue = 0;
            }
            int gid = iIntValue;
            out.write(new byte[]{(byte) ((gid >> 8) & 255), (byte) (gid & 255)});
        }
        InputStream input = new ByteArrayInputStream(out.toByteArray());
        PDStream stream = new PDStream(this.document, input, COSName.FLATE_DECODE);
        this.cidFont.setItem(COSName.CID_TO_GID_MAP, stream);
    }

    private void buildCIDSet(Map<Integer, Integer> cidToGid) throws IOException {
        int cidMax = ((Integer) Collections.max(cidToGid.keySet())).intValue();
        byte[] bytes = new byte[(cidMax / 8) + 1];
        for (int cid = 0; cid <= cidMax; cid++) {
            int mask = 1 << (7 - (cid % 8));
            int i = cid / 8;
            bytes[i] = (byte) (bytes[i] | mask);
        }
        InputStream input = new ByteArrayInputStream(bytes);
        PDStream stream = new PDStream(this.document, input, COSName.FLATE_DECODE);
        this.fontDescriptor.setCIDSet(stream);
    }

    private void buildWidths(Map<Integer, Integer> cidToGid) throws IOException {
        float scaling = 1000.0f / this.ttf.getHeader().getUnitsPerEm();
        COSArray widths = new COSArray();
        COSArray ws = new COSArray();
        int prev = Integer.MIN_VALUE;
        Set<Integer> keys = new TreeSet<>(cidToGid.keySet());
        Iterator<Integer> it = keys.iterator();
        while (it.hasNext()) {
            int cid = it.next().intValue();
            int gid = cidToGid.get(Integer.valueOf(cid)).intValue();
            long width = Math.round(this.ttf.getHorizontalMetrics().getAdvanceWidth(gid) * scaling);
            if (width != 1000) {
                if (prev != cid - 1) {
                    ws = new COSArray();
                    widths.add((COSBase) COSInteger.get(cid));
                    widths.add((COSBase) ws);
                }
                ws.add((COSBase) COSInteger.get(width));
                prev = cid;
            }
        }
        this.cidFont.setItem(COSName.W, (COSBase) widths);
    }

    private boolean buildVerticalHeader(COSDictionary cidFont) throws IOException {
        VerticalHeaderTable vhea = this.ttf.getVerticalHeader();
        if (vhea == null) {
            LOG.warn("Font to be subset is set to vertical, but has no 'vhea' table");
            return false;
        }
        float scaling = 1000.0f / this.ttf.getHeader().getUnitsPerEm();
        long v = Math.round(vhea.getAscender() * scaling);
        long w1 = Math.round((-vhea.getAdvanceHeightMax()) * scaling);
        if (v != 880 || w1 != -1000) {
            COSArray cosDw2 = new COSArray();
            cosDw2.add((COSBase) COSInteger.get(v));
            cosDw2.add((COSBase) COSInteger.get(w1));
            cidFont.setItem(COSName.DW2, (COSBase) cosDw2);
            return true;
        }
        return true;
    }

    private void buildVerticalMetrics(Map<Integer, Integer> cidToGid) throws IOException {
        if (!buildVerticalHeader(this.cidFont)) {
            return;
        }
        float scaling = 1000.0f / this.ttf.getHeader().getUnitsPerEm();
        VerticalHeaderTable vhea = this.ttf.getVerticalHeader();
        VerticalMetricsTable vmtx = this.ttf.getVerticalMetrics();
        GlyphTable glyf = this.ttf.getGlyph();
        HorizontalMetricsTable hmtx = this.ttf.getHorizontalMetrics();
        long v_y = Math.round(vhea.getAscender() * scaling);
        long w1 = Math.round((-vhea.getAdvanceHeightMax()) * scaling);
        COSArray heights = new COSArray();
        COSArray w2 = new COSArray();
        int prev = Integer.MIN_VALUE;
        Set<Integer> keys = new TreeSet<>(cidToGid.keySet());
        Iterator<Integer> it = keys.iterator();
        while (it.hasNext()) {
            int cid = it.next().intValue();
            GlyphData glyph = glyf.getGlyph(cid);
            if (glyph != null) {
                long height = Math.round((glyph.getYMaximum() + vmtx.getTopSideBearing(cid)) * scaling);
                long advance = Math.round((-vmtx.getAdvanceHeight(cid)) * scaling);
                if (height != v_y || advance != w1) {
                    if (prev != cid - 1) {
                        w2 = new COSArray();
                        heights.add((COSBase) COSInteger.get(cid));
                        heights.add((COSBase) w2);
                    }
                    w2.add((COSBase) COSInteger.get(advance));
                    long width = Math.round(hmtx.getAdvanceWidth(cid) * scaling);
                    w2.add((COSBase) COSInteger.get(width / 2));
                    w2.add((COSBase) COSInteger.get(height));
                    prev = cid;
                }
            }
        }
        this.cidFont.setItem(COSName.W2, (COSBase) heights);
    }

    private void buildWidths(COSDictionary cidFont) throws IOException {
        int cidMax = this.ttf.getNumberOfGlyphs();
        int[] gidwidths = new int[cidMax * 2];
        for (int cid = 0; cid < cidMax; cid++) {
            gidwidths[cid * 2] = cid;
            gidwidths[(cid * 2) + 1] = this.ttf.getHorizontalMetrics().getAdvanceWidth(cid);
        }
        cidFont.setItem(COSName.W, (COSBase) getWidths(gidwidths));
    }

    private COSArray getWidths(int[] widths) throws IOException {
        if (widths.length == 0) {
            throw new IllegalArgumentException("length of widths must be > 0");
        }
        float scaling = 1000.0f / this.ttf.getHeader().getUnitsPerEm();
        long lastCid = widths[0];
        long lastValue = Math.round(widths[1] * scaling);
        COSArray inner = new COSArray();
        COSArray outer = new COSArray();
        outer.add(COSInteger.get(lastCid));
        State state = State.FIRST;
        for (int i = 2; i < widths.length; i += 2) {
            long cid = widths[i];
            long value = Math.round(widths[i + 1] * scaling);
            switch (state) {
                case FIRST:
                    if (cid == lastCid + 1 && value == lastValue) {
                        state = State.SERIAL;
                    } else if (cid == lastCid + 1) {
                        state = State.BRACKET;
                        inner = new COSArray();
                        inner.add((COSBase) COSInteger.get(lastValue));
                    } else {
                        inner = new COSArray();
                        inner.add((COSBase) COSInteger.get(lastValue));
                        outer.add((COSBase) inner);
                        outer.add(COSInteger.get(cid));
                    }
                    break;
                case BRACKET:
                    if (cid == lastCid + 1 && value == lastValue) {
                        state = State.SERIAL;
                        outer.add((COSBase) inner);
                        outer.add(COSInteger.get(lastCid));
                    } else if (cid == lastCid + 1) {
                        inner.add((COSBase) COSInteger.get(lastValue));
                    } else {
                        state = State.FIRST;
                        inner.add((COSBase) COSInteger.get(lastValue));
                        outer.add((COSBase) inner);
                        outer.add(COSInteger.get(cid));
                    }
                    break;
                case SERIAL:
                    if (cid != lastCid + 1 || value != lastValue) {
                        outer.add(COSInteger.get(lastCid));
                        outer.add(COSInteger.get(lastValue));
                        outer.add(COSInteger.get(cid));
                        state = State.FIRST;
                    }
                    break;
            }
            lastValue = value;
            lastCid = cid;
        }
        switch (state) {
            case FIRST:
                COSArray inner2 = new COSArray();
                inner2.add((COSBase) COSInteger.get(lastValue));
                outer.add((COSBase) inner2);
                break;
            case BRACKET:
                inner.add((COSBase) COSInteger.get(lastValue));
                outer.add((COSBase) inner);
                break;
            case SERIAL:
                outer.add(COSInteger.get(lastCid));
                outer.add(COSInteger.get(lastValue));
                break;
        }
        return outer;
    }

    private void buildVerticalMetrics(COSDictionary cidFont) throws IOException {
        if (!buildVerticalHeader(cidFont)) {
            return;
        }
        int cidMax = this.ttf.getNumberOfGlyphs();
        int[] gidMetrics = new int[cidMax * 4];
        for (int cid = 0; cid < cidMax; cid++) {
            GlyphData glyph = this.ttf.getGlyph().getGlyph(cid);
            if (glyph == null) {
                gidMetrics[cid * 4] = Integer.MIN_VALUE;
            } else {
                gidMetrics[cid * 4] = cid;
                gidMetrics[(cid * 4) + 1] = this.ttf.getVerticalMetrics().getAdvanceHeight(cid);
                gidMetrics[(cid * 4) + 2] = this.ttf.getHorizontalMetrics().getAdvanceWidth(cid);
                gidMetrics[(cid * 4) + 3] = glyph.getYMaximum() + this.ttf.getVerticalMetrics().getTopSideBearing(cid);
            }
        }
        cidFont.setItem(COSName.W2, (COSBase) getVerticalMetrics(gidMetrics));
    }

    private COSArray getVerticalMetrics(int[] values) throws IOException {
        if (values.length == 0) {
            throw new IllegalArgumentException("length of values must be > 0");
        }
        float scaling = 1000.0f / this.ttf.getHeader().getUnitsPerEm();
        long lastCid = values[0];
        long lastW1Value = Math.round((-values[1]) * scaling);
        long lastVxValue = Math.round((values[2] * scaling) / 2.0f);
        long lastVyValue = Math.round(values[3] * scaling);
        COSArray inner = new COSArray();
        COSArray outer = new COSArray();
        outer.add(COSInteger.get(lastCid));
        State state = State.FIRST;
        for (int i = 4; i < values.length; i += 4) {
            long cid = values[i];
            if (cid != -2147483648L) {
                long w1Value = Math.round((-values[i + 1]) * scaling);
                long vxValue = Math.round((values[i + 2] * scaling) / 2.0f);
                long vyValue = Math.round(values[i + 3] * scaling);
                switch (state) {
                    case FIRST:
                        if (cid == lastCid + 1 && w1Value == lastW1Value && vxValue == lastVxValue && vyValue == lastVyValue) {
                            state = State.SERIAL;
                        } else if (cid == lastCid + 1) {
                            state = State.BRACKET;
                            inner = new COSArray();
                            inner.add((COSBase) COSInteger.get(lastW1Value));
                            inner.add((COSBase) COSInteger.get(lastVxValue));
                            inner.add((COSBase) COSInteger.get(lastVyValue));
                        } else {
                            inner = new COSArray();
                            inner.add((COSBase) COSInteger.get(lastW1Value));
                            inner.add((COSBase) COSInteger.get(lastVxValue));
                            inner.add((COSBase) COSInteger.get(lastVyValue));
                            outer.add((COSBase) inner);
                            outer.add(COSInteger.get(cid));
                        }
                        break;
                    case BRACKET:
                        if (cid == lastCid + 1 && w1Value == lastW1Value && vxValue == lastVxValue && vyValue == lastVyValue) {
                            state = State.SERIAL;
                            outer.add((COSBase) inner);
                            outer.add(COSInteger.get(lastCid));
                        } else if (cid == lastCid + 1) {
                            inner.add((COSBase) COSInteger.get(lastW1Value));
                            inner.add((COSBase) COSInteger.get(lastVxValue));
                            inner.add((COSBase) COSInteger.get(lastVyValue));
                        } else {
                            state = State.FIRST;
                            inner.add((COSBase) COSInteger.get(lastW1Value));
                            inner.add((COSBase) COSInteger.get(lastVxValue));
                            inner.add((COSBase) COSInteger.get(lastVyValue));
                            outer.add((COSBase) inner);
                            outer.add(COSInteger.get(cid));
                        }
                        break;
                    case SERIAL:
                        if (cid != lastCid + 1 || w1Value != lastW1Value || vxValue != lastVxValue || vyValue != lastVyValue) {
                            outer.add(COSInteger.get(lastCid));
                            outer.add(COSInteger.get(lastW1Value));
                            outer.add(COSInteger.get(lastVxValue));
                            outer.add(COSInteger.get(lastVyValue));
                            outer.add(COSInteger.get(cid));
                            state = State.FIRST;
                        }
                        break;
                }
                lastW1Value = w1Value;
                lastVxValue = vxValue;
                lastVyValue = vyValue;
                lastCid = cid;
            }
        }
        switch (state) {
            case FIRST:
                COSArray inner2 = new COSArray();
                inner2.add((COSBase) COSInteger.get(lastW1Value));
                inner2.add((COSBase) COSInteger.get(lastVxValue));
                inner2.add((COSBase) COSInteger.get(lastVyValue));
                outer.add((COSBase) inner2);
                break;
            case BRACKET:
                inner.add((COSBase) COSInteger.get(lastW1Value));
                inner.add((COSBase) COSInteger.get(lastVxValue));
                inner.add((COSBase) COSInteger.get(lastVyValue));
                outer.add((COSBase) inner);
                break;
            case SERIAL:
                outer.add(COSInteger.get(lastCid));
                outer.add(COSInteger.get(lastW1Value));
                outer.add(COSInteger.get(lastVxValue));
                outer.add(COSInteger.get(lastVyValue));
                break;
        }
        return outer;
    }

    public PDCIDFont getCIDFont() throws IOException {
        return new PDCIDFontType2(this.cidFont, this.parent, this.ttf);
    }
}
