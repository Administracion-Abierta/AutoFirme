package org.apache.pdfbox.rendering;

import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.ttf.HeaderTable;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDCIDFontType2;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDVectorFont;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/rendering/TTFGlyph2D.class */
final class TTFGlyph2D implements Glyph2D {
    private static final Log LOG = LogFactory.getLog(TTFGlyph2D.class);
    private final PDFont font;
    private final TrueTypeFont ttf;
    private PDVectorFont vectorFont;
    private float scale;
    private boolean hasScaling;
    private final Map<Integer, GeneralPath> glyphs;
    private final boolean isCIDFont;

    TTFGlyph2D(PDTrueTypeFont ttfFont) throws IOException {
        this(ttfFont.getTrueTypeFont(), ttfFont, false);
        this.vectorFont = ttfFont;
    }

    TTFGlyph2D(PDType0Font type0Font) throws IOException {
        this(((PDCIDFontType2) type0Font.getDescendantFont()).getTrueTypeFont(), type0Font, true);
        this.vectorFont = type0Font;
    }

    private TTFGlyph2D(TrueTypeFont ttf, PDFont font, boolean isCIDFont) throws IOException {
        this.scale = 1.0f;
        this.glyphs = new HashMap();
        this.font = font;
        this.ttf = ttf;
        this.isCIDFont = isCIDFont;
        HeaderTable header = this.ttf.getHeader();
        if (header != null && header.getUnitsPerEm() != 1000) {
            this.scale = 1000.0f / header.getUnitsPerEm();
            this.hasScaling = true;
        }
    }

    @Override // org.apache.pdfbox.rendering.Glyph2D
    public GeneralPath getPathForCharacterCode(int code) throws IOException {
        int gid = getGIDForCharacterCode(code);
        return getPathForGID(gid, code);
    }

    private int getGIDForCharacterCode(int code) throws IOException {
        if (this.isCIDFont) {
            return ((PDType0Font) this.font).codeToGID(code);
        }
        return ((PDTrueTypeFont) this.font).codeToGID(code);
    }

    public GeneralPath getPathForGID(int gid, int code) throws IOException {
        if (gid == 0 && !this.isCIDFont && code == 10 && this.font.isStandard14()) {
            LOG.warn("No glyph for code " + code + " in font " + this.font.getName());
            return new GeneralPath();
        }
        GeneralPath glyphPath = this.glyphs.get(Integer.valueOf(gid));
        if (glyphPath == null) {
            if (gid == 0 || gid >= this.ttf.getMaximumProfile().getNumGlyphs()) {
                if (this.isCIDFont) {
                    int cid = ((PDType0Font) this.font).codeToCID(code);
                    String cidHex = String.format("%04x", Integer.valueOf(cid));
                    LOG.warn("No glyph for code " + code + " (CID " + cidHex + ") in font " + this.font.getName());
                } else {
                    LOG.warn("No glyph for " + code + " in font " + this.font.getName());
                }
            }
            GeneralPath glyph = this.vectorFont.getPath(code);
            if (gid == 0 && !this.font.isEmbedded() && !this.font.isStandard14()) {
                glyph = null;
            }
            if (glyph == null) {
                glyphPath = new GeneralPath();
                this.glyphs.put(Integer.valueOf(gid), glyphPath);
            } else {
                glyphPath = glyph;
                if (this.hasScaling) {
                    AffineTransform atScale = AffineTransform.getScaleInstance(this.scale, this.scale);
                    glyphPath.transform(atScale);
                }
                this.glyphs.put(Integer.valueOf(gid), glyphPath);
            }
        }
        return (GeneralPath) glyphPath.clone();
    }

    @Override // org.apache.pdfbox.rendering.Glyph2D
    public void dispose() {
        this.glyphs.clear();
    }
}
