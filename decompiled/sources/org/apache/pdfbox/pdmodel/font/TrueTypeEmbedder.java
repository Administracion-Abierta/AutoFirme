package org.apache.pdfbox.pdmodel.font;

import com.twelvemonkeys.imageio.metadata.tiff.TIFF;
import java.awt.geom.GeneralPath;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.fontbox.ttf.CmapLookup;
import org.apache.fontbox.ttf.CmapSubtable;
import org.apache.fontbox.ttf.GlyphTable;
import org.apache.fontbox.ttf.HeaderTable;
import org.apache.fontbox.ttf.HorizontalHeaderTable;
import org.apache.fontbox.ttf.HorizontalMetricsTable;
import org.apache.fontbox.ttf.IndexToLocationTable;
import org.apache.fontbox.ttf.MaximumProfileTable;
import org.apache.fontbox.ttf.OS2WindowsMetricsTable;
import org.apache.fontbox.ttf.PostScriptTable;
import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TTFSubsetter;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.StandardStructureTypes;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/font/TrueTypeEmbedder.class */
abstract class TrueTypeEmbedder implements Subsetter {
    private static final int ITALIC = 1;
    private static final int OBLIQUE = 512;
    private static final String BASE25 = "BCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final PDDocument document;
    protected TrueTypeFont ttf;
    protected PDFontDescriptor fontDescriptor;

    @Deprecated
    protected final CmapSubtable cmap;
    protected final CmapLookup cmapLookup;
    private final Set<Integer> subsetCodePoints = new HashSet();
    private final boolean embedSubset;

    protected abstract void buildSubset(InputStream inputStream, String str, Map<Integer, Integer> map) throws IOException;

    TrueTypeEmbedder(PDDocument document, COSDictionary dict, TrueTypeFont ttf, boolean embedSubset) throws IOException {
        this.document = document;
        this.embedSubset = embedSubset;
        this.ttf = ttf;
        this.fontDescriptor = createFontDescriptor(ttf);
        if (!isEmbeddingPermitted(ttf)) {
            throw new IOException("This font does not permit embedding");
        }
        if (!embedSubset) {
            InputStream is = ttf.getOriginalData();
            byte[] b = new byte[4];
            is.mark(b.length);
            if (is.read(b) == b.length && new String(b).equals("ttcf")) {
                is.close();
                throw new IOException("Full embedding of TrueType font collections not supported");
            }
            if (is.markSupported()) {
                is.reset();
            } else {
                is.close();
                is = ttf.getOriginalData();
            }
            PDStream stream = new PDStream(document, is, COSName.FLATE_DECODE);
            stream.getCOSObject().setLong(COSName.LENGTH1, ttf.getOriginalDataSize());
            this.fontDescriptor.setFontFile2(stream);
        }
        dict.setName(COSName.BASE_FONT, ttf.getName());
        this.cmap = ttf.getUnicodeCmap();
        this.cmapLookup = ttf.getUnicodeCmapLookup();
    }

    public void buildFontFile2(InputStream ttfStream) throws IOException {
        PDStream stream = new PDStream(this.document, ttfStream, COSName.FLATE_DECODE);
        try {
            InputStream input = stream.createInputStream();
            this.ttf = new TTFParser().parseEmbedded(input);
            if (!isEmbeddingPermitted(this.ttf)) {
                throw new IOException("This font does not permit embedding");
            }
            if (this.fontDescriptor == null) {
                this.fontDescriptor = createFontDescriptor(this.ttf);
            }
            IOUtils.closeQuietly(input);
            stream.getCOSObject().setLong(COSName.LENGTH1, this.ttf.getOriginalDataSize());
            this.fontDescriptor.setFontFile2(stream);
        } catch (Throwable th) {
            IOUtils.closeQuietly(null);
            throw th;
        }
    }

    boolean isEmbeddingPermitted(TrueTypeFont ttf) throws IOException {
        if (ttf.getOS2Windows() != null) {
            int fsType = ttf.getOS2Windows().getFsType();
            int maskedFsType = fsType & 15;
            if (maskedFsType == 2 || (fsType & 512) == 512) {
                return false;
            }
            return true;
        }
        return true;
    }

    private boolean isSubsettingPermitted(TrueTypeFont ttf) throws IOException {
        if (ttf.getOS2Windows() != null) {
            int fsType = ttf.getOS2Windows().getFsType();
            if ((fsType & 256) == 256) {
                return false;
            }
            return true;
        }
        return true;
    }

    private PDFontDescriptor createFontDescriptor(TrueTypeFont ttf) throws IOException {
        PDFontDescriptor fd = new PDFontDescriptor();
        fd.setFontName(ttf.getName());
        OS2WindowsMetricsTable os2 = ttf.getOS2Windows();
        if (os2 == null) {
            throw new IOException("os2 table is missing in font " + ttf.getName());
        }
        PostScriptTable post = ttf.getPostScript();
        if (post == null) {
            throw new IOException("post table is missing in font " + ttf.getName());
        }
        fd.setFixedPitch(post.getIsFixedPitch() > 0 || ttf.getHorizontalHeader().getNumberOfHMetrics() == 1);
        int fsSelection = os2.getFsSelection();
        fd.setItalic((fsSelection & TIFF.TAG_JPEG_INTERCHANGE_FORMAT) != 0);
        switch (os2.getFamilyClass()) {
            case 1:
            case 3:
            case 4:
            case 5:
            case 7:
                fd.setSerif(true);
                break;
            case 10:
                fd.setScript(true);
                break;
        }
        fd.setFontWeight(os2.getWeightClass());
        fd.setSymbolic(true);
        fd.setNonSymbolic(false);
        fd.setItalicAngle(post.getItalicAngle());
        HeaderTable header = ttf.getHeader();
        PDRectangle rect = new PDRectangle();
        float scaling = 1000.0f / header.getUnitsPerEm();
        rect.setLowerLeftX(header.getXMin() * scaling);
        rect.setLowerLeftY(header.getYMin() * scaling);
        rect.setUpperRightX(header.getXMax() * scaling);
        rect.setUpperRightY(header.getYMax() * scaling);
        fd.setFontBoundingBox(rect);
        HorizontalHeaderTable hHeader = ttf.getHorizontalHeader();
        fd.setAscent(hHeader.getAscender() * scaling);
        fd.setDescent(hHeader.getDescender() * scaling);
        if (os2.getVersion() >= 1.2d) {
            fd.setCapHeight(os2.getCapHeight() * scaling);
            fd.setXHeight(os2.getHeight() * scaling);
        } else {
            GeneralPath capHPath = ttf.getPath(StandardStructureTypes.H);
            if (capHPath != null) {
                fd.setCapHeight(Math.round(capHPath.getBounds2D().getMaxY()) * scaling);
            } else {
                fd.setCapHeight((os2.getTypoAscender() + os2.getTypoDescender()) * scaling);
            }
            GeneralPath xPath = ttf.getPath("x");
            if (xPath != null) {
                fd.setXHeight(Math.round(xPath.getBounds2D().getMaxY()) * scaling);
            } else {
                fd.setXHeight((os2.getTypoAscender() / 2.0f) * scaling);
            }
        }
        fd.setStemV(fd.getFontBoundingBox().getWidth() * 0.13f);
        return fd;
    }

    @Deprecated
    public TrueTypeFont getTrueTypeFont() {
        return this.ttf;
    }

    public PDFontDescriptor getFontDescriptor() {
        return this.fontDescriptor;
    }

    @Override // org.apache.pdfbox.pdmodel.font.Subsetter
    public void addToSubset(int codePoint) {
        this.subsetCodePoints.add(Integer.valueOf(codePoint));
    }

    @Override // org.apache.pdfbox.pdmodel.font.Subsetter
    public void subset() throws IOException {
        if (!isSubsettingPermitted(this.ttf)) {
            throw new IOException("This font does not permit subsetting");
        }
        if (!this.embedSubset) {
            throw new IllegalStateException("Subsetting is disabled");
        }
        List<String> tables = new ArrayList<>();
        tables.add(HeaderTable.TAG);
        tables.add(HorizontalHeaderTable.TAG);
        tables.add(IndexToLocationTable.TAG);
        tables.add(MaximumProfileTable.TAG);
        tables.add("cvt ");
        tables.add("prep");
        tables.add(GlyphTable.TAG);
        tables.add(HorizontalMetricsTable.TAG);
        tables.add("fpgm");
        tables.add("gasp");
        TTFSubsetter subsetter = new TTFSubsetter(this.ttf, tables);
        subsetter.addAll(this.subsetCodePoints);
        Map<Integer, Integer> gidToCid = subsetter.getGIDMap();
        String tag = getTag(gidToCid);
        subsetter.setPrefix(tag);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        subsetter.writeToStream(out);
        buildSubset(new ByteArrayInputStream(out.toByteArray()), tag, gidToCid);
        this.ttf.close();
    }

    public boolean needsSubset() {
        return this.embedSubset;
    }

    public String getTag(Map<Integer, Integer> gidToCid) {
        long num = gidToCid.hashCode();
        StringBuilder sb = new StringBuilder();
        do {
            long div = num / 25;
            int mod = (int) (num % 25);
            sb.append(BASE25.charAt(mod));
            num = div;
            if (num == 0) {
                break;
            }
        } while (sb.length() < 6);
        while (sb.length() < 6) {
            sb.insert(0, 'A');
        }
        sb.append('+');
        return sb.toString();
    }
}
