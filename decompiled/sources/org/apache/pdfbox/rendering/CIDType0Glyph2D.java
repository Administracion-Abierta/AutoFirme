package org.apache.pdfbox.rendering;

import java.awt.geom.GeneralPath;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.font.PDCIDFontType0;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/rendering/CIDType0Glyph2D.class */
final class CIDType0Glyph2D implements Glyph2D {
    private static final Log LOG = LogFactory.getLog(CIDType0Glyph2D.class);
    private final Map<Integer, GeneralPath> cache = new HashMap();
    private final PDCIDFontType0 font;
    private final String fontName;

    CIDType0Glyph2D(PDCIDFontType0 font) {
        this.font = font;
        this.fontName = font.getBaseFont();
    }

    @Override // org.apache.pdfbox.rendering.Glyph2D
    public GeneralPath getPathForCharacterCode(int code) {
        GeneralPath path = this.cache.get(Integer.valueOf(code));
        if (path == null) {
            try {
                if (!this.font.hasGlyph(code)) {
                    int cid = this.font.getParent().codeToCID(code);
                    String cidHex = String.format("%04x", Integer.valueOf(cid));
                    LOG.warn("No glyph for " + code + " (CID " + cidHex + ") in font " + this.fontName);
                }
                GeneralPath path2 = this.font.getPath(code);
                this.cache.put(Integer.valueOf(code), path2);
                return path2;
            } catch (IOException e) {
                LOG.error("Glyph rendering failed", e);
                path = new GeneralPath();
            }
        }
        return path;
    }

    @Override // org.apache.pdfbox.rendering.Glyph2D
    public void dispose() {
        this.cache.clear();
    }
}
