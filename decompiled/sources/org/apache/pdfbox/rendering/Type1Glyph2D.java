package org.apache.pdfbox.rendering;

import com.aowagie.text.pdf.BaseFont;
import java.awt.geom.GeneralPath;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.font.PDSimpleFont;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/rendering/Type1Glyph2D.class */
final class Type1Glyph2D implements Glyph2D {
    private static final Log LOG = LogFactory.getLog(Type1Glyph2D.class);
    private final Map<Integer, GeneralPath> cache = new HashMap();
    private final PDSimpleFont font;

    Type1Glyph2D(PDSimpleFont font) {
        this.font = font;
    }

    @Override // org.apache.pdfbox.rendering.Glyph2D
    public GeneralPath getPathForCharacterCode(int code) {
        GeneralPath path = this.cache.get(Integer.valueOf(code));
        if (path == null) {
            try {
                String name = this.font.getEncoding().getName(code);
                if (!this.font.hasGlyph(name)) {
                    LOG.warn("No glyph for code " + code + " (" + name + ") in font " + this.font.getName());
                    if (code == 10 && this.font.isStandard14()) {
                        GeneralPath path2 = new GeneralPath();
                        this.cache.put(Integer.valueOf(code), path2);
                        return path2;
                    }
                    String unicodes = this.font.getGlyphList().toUnicode(name);
                    if (unicodes != null && unicodes.length() == 1) {
                        String uniName = getUniNameOfCodePoint(unicodes.codePointAt(0));
                        if (this.font.hasGlyph(uniName)) {
                            name = uniName;
                        }
                    }
                }
                GeneralPath path3 = this.font.getPath(name);
                if (path3 == null) {
                    path3 = this.font.getPath(BaseFont.notdef);
                }
                this.cache.put(Integer.valueOf(code), path3);
                return path3;
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

    private static String getUniNameOfCodePoint(int codePoint) {
        String hex = Integer.toString(codePoint, 16).toUpperCase(Locale.US);
        switch (hex.length()) {
            case 1:
                return "uni000" + hex;
            case 2:
                return "uni00" + hex;
            case 3:
                return "uni0" + hex;
            default:
                return "uni" + hex;
        }
    }
}
