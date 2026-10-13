package org.apache.fontbox.ttf;

import java.awt.geom.GeneralPath;
import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/ttf/OpenTypeFont.class */
public class OpenTypeFont extends TrueTypeFont {
    private boolean isPostScript;

    OpenTypeFont(TTFDataStream fontData) {
        super(fontData);
    }

    @Override // org.apache.fontbox.ttf.TrueTypeFont
    void setVersion(float versionValue) {
        this.isPostScript = Float.floatToIntBits(versionValue) == 1184802985;
        super.setVersion(versionValue);
    }

    public CFFTable getCFF() throws IOException {
        if (!this.isPostScript) {
            throw new UnsupportedOperationException("TTF fonts do not have a CFF table");
        }
        return (CFFTable) getTable(CFFTable.TAG);
    }

    @Override // org.apache.fontbox.ttf.TrueTypeFont
    public GlyphTable getGlyph() throws IOException {
        if (this.isPostScript) {
            throw new UnsupportedOperationException("OTF fonts do not have a glyf table");
        }
        return super.getGlyph();
    }

    @Override // org.apache.fontbox.ttf.TrueTypeFont, org.apache.fontbox.FontBoxFont
    public GeneralPath getPath(String name) throws IOException {
        int gid = nameToGID(name);
        return getCFF().getFont().getType2CharString(gid).getPath();
    }

    public boolean isPostScript() {
        return this.tables.containsKey(CFFTable.TAG);
    }

    public boolean hasLayoutTables() {
        return this.tables.containsKey("BASE") || this.tables.containsKey("GDEF") || this.tables.containsKey("GPOS") || this.tables.containsKey(GlyphSubstitutionTable.TAG) || this.tables.containsKey("JSTF");
    }
}
