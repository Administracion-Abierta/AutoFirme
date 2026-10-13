package es.gob.afirma.standalone.ui.pdf;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/pdf/FontResource.class */
enum FontResource {
    TIMES_ROMAN("Times Roman", "/fonts/times.ttf", 2),
    COURIER("Courier", "/fonts/courier.ttf", 0),
    HELVETICA("Helvetica", "/fonts/helvetica.ttf", 1);

    private final String fontName;
    private Font font;
    private final int pdfFontIndex;
    private final GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    private final Font[] fonts = this.ge.getAllFonts();

    FontResource(String str, String str2, int i) {
        this.font = null;
        this.fontName = str;
        this.pdfFontIndex = i;
        for (Font font : this.fonts) {
            if (font.getFontName().equals(this.fontName)) {
                this.font = font;
            }
        }
        if (this.font == null) {
            try {
                InputStream resourceAsStream = FontResource.class.getResourceAsStream(str2);
                Throwable th = null;
                try {
                    try {
                        this.font = Font.createFont(0, resourceAsStream);
                        this.ge.registerFont(this.font);
                        if (resourceAsStream != null) {
                            if (0 != 0) {
                                try {
                                    resourceAsStream.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            } else {
                                resourceAsStream.close();
                            }
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        throw th3;
                    }
                } catch (Throwable th4) {
                    if (resourceAsStream != null) {
                        if (th != null) {
                            try {
                                resourceAsStream.close();
                            } catch (Throwable th5) {
                                th.addSuppressed(th5);
                            }
                        } else {
                            resourceAsStream.close();
                        }
                    }
                    throw th4;
                }
            } catch (Exception e) {
                throw new IllegalStateException("Error creando el tipo de letra " + this.fontName + ": " + e);
            }
        }
    }

    @Override // java.lang.Enum
    public String toString() {
        return this.fontName;
    }

    Font getFont() {
        return this.font;
    }

    static FontResource[] getAllFontresources() {
        return new FontResource[]{COURIER, HELVETICA, TIMES_ROMAN};
    }

    String getPdfFontIndex() {
        return Integer.toString(this.pdfFontIndex);
    }
}
