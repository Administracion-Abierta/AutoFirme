package org.apache.fontbox.ttf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/OTFParser.class */
public final class OTFParser extends TTFParser {
    public OTFParser() {
    }

    public OTFParser(boolean isEmbedded) {
        this(isEmbedded, false);
    }

    public OTFParser(boolean isEmbedded, boolean parseOnDemand) {
        super(isEmbedded, parseOnDemand);
    }

    @Override // org.apache.fontbox.ttf.TTFParser
    public OpenTypeFont parse(String file) throws IOException {
        return (OpenTypeFont) super.parse(file);
    }

    @Override // org.apache.fontbox.ttf.TTFParser
    public OpenTypeFont parse(File file) throws IOException {
        return (OpenTypeFont) super.parse(file);
    }

    @Override // org.apache.fontbox.ttf.TTFParser
    public OpenTypeFont parse(InputStream data) throws IOException {
        return (OpenTypeFont) super.parse(data);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.apache.fontbox.ttf.TTFParser
    public OpenTypeFont parse(TTFDataStream raf) throws IOException {
        return (OpenTypeFont) super.parse(raf);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // org.apache.fontbox.ttf.TTFParser
    public OpenTypeFont newFont(TTFDataStream raf) {
        return new OpenTypeFont(raf);
    }

    @Override // org.apache.fontbox.ttf.TTFParser
    protected TTFTable readTable(TrueTypeFont font, String tag) {
        if (tag.equals("BASE") || tag.equals("GDEF") || tag.equals("GPOS") || tag.equals(GlyphSubstitutionTable.TAG) || tag.equals("JSTF")) {
            return new OTLTable(font);
        }
        if (tag.equals(CFFTable.TAG)) {
            return new CFFTable(font);
        }
        return super.readTable(font, tag);
    }

    @Override // org.apache.fontbox.ttf.TTFParser
    protected boolean allowCFF() {
        return true;
    }
}
