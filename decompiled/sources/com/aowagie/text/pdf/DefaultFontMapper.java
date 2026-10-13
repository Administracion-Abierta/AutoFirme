package com.aowagie.text.pdf;

import com.aowagie.text.ExceptionConverter;
import java.awt.Font;
import java.util.HashMap;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/DefaultFontMapper.class */
public class DefaultFontMapper implements FontMapper {
    private final HashMap aliases = new HashMap();
    private final HashMap mapper = new HashMap();

    /* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/DefaultFontMapper$BaseFontParameters.class */
    static class BaseFontParameters {
        String fontName;
        String encoding = "Cp1252";
        boolean embedded = true;
        boolean cached = true;
        byte[] ttfAfm;
        byte[] pfb;

        private BaseFontParameters(String fontName) {
            this.fontName = fontName;
        }
    }

    @Override // com.aowagie.text.pdf.FontMapper
    public BaseFont awtToPdf(Font font) {
        String fontKey;
        try {
            BaseFontParameters p = getBaseFontParameters(font.getFontName());
            if (p != null) {
                return BaseFont.createFont(p.fontName, p.encoding, p.embedded, p.cached, p.ttfAfm, p.pfb);
            }
            String logicalName = font.getName();
            if (logicalName.equalsIgnoreCase("DialogInput") || logicalName.equalsIgnoreCase("Monospaced") || logicalName.equalsIgnoreCase("Courier")) {
                if (font.isItalic()) {
                    if (font.isBold()) {
                        fontKey = "Courier-BoldOblique";
                    } else {
                        fontKey = "Courier-Oblique";
                    }
                } else if (font.isBold()) {
                    fontKey = "Courier-Bold";
                } else {
                    fontKey = "Courier";
                }
            } else if (logicalName.equalsIgnoreCase("Serif") || logicalName.equalsIgnoreCase("TimesRoman")) {
                if (font.isItalic()) {
                    if (font.isBold()) {
                        fontKey = "Times-BoldItalic";
                    } else {
                        fontKey = "Times-Italic";
                    }
                } else if (font.isBold()) {
                    fontKey = "Times-Bold";
                } else {
                    fontKey = "Times-Roman";
                }
            } else if (font.isItalic()) {
                if (font.isBold()) {
                    fontKey = "Helvetica-BoldOblique";
                } else {
                    fontKey = "Helvetica-Oblique";
                }
            } else if (font.isBold()) {
                fontKey = "Helvetica-Bold";
            } else {
                fontKey = "Helvetica";
            }
            return BaseFont.createFont(fontKey, "Cp1252", false);
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
    }

    BaseFontParameters getBaseFontParameters(String name) {
        String alias = (String) this.aliases.get(name);
        if (alias == null) {
            return (BaseFontParameters) this.mapper.get(name);
        }
        BaseFontParameters p = (BaseFontParameters) this.mapper.get(alias);
        if (p == null) {
            return (BaseFontParameters) this.mapper.get(name);
        }
        return p;
    }

    public HashMap getMapper() {
        return this.mapper;
    }

    public HashMap getAliases() {
        return this.aliases;
    }
}
