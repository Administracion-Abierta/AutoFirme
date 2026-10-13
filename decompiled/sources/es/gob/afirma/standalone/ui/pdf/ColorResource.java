package es.gob.afirma.standalone.ui.pdf;

import com.aowagie.text.ElementTags;
import es.gob.afirma.core.misc.Base64;
import java.awt.Color;
import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/pdf/ColorResource.class */
enum ColorResource {
    BLACK(SignPdfUiMessages.getString("ColorResource.0"), new Color(0, 0, 0), "black", "iVBORw0KGgoAAAANSUhEUgAAAA8AAAAKAQMAAABR1Za6AAAAB3RJTUUH4AQCEDkN6QqNHgAAAAlwSFlzAAALEwAACxMBAJqcGAAAAARnQU1BAACxjwv8YQUAAAAGUExURQAAAP///6XZn90AAAALSURBVHjaY2DABwAAHgABZVmecQAAAABJRU5ErkJggg=="),
    WHITE(SignPdfUiMessages.getString("ColorResource.1"), new Color(255, 255, 255), "white", "iVBORw0KGgoAAAANSUhEUgAAAA8AAAAKCAYAAABrGwT5AAAAE0lEQVR42mP4TwFgGNU8qpkQAAA5QlXHEslhzQAAAABJRU5ErkJggg=="),
    LIGHTGRAY(SignPdfUiMessages.getString("ColorResource.2"), new Color(128, 128, 128), "lightGray", "iVBORw0KGgoAAAANSUhEUgAAAA8AAAAKCAYAAABrGwT5AAAAFklEQVR42mM4cODAf3Ixw6jmUc2EMAAJyeb6dfPeQwAAAABJRU5ErkJggg=="),
    GRAY(SignPdfUiMessages.getString("ColorResource.3"), new Color(192, 192, 192), "gray", "iVBORw0KGgoAAAANSUhEUgAAAA8AAAAKCAYAAABrGwT5AAAAFklEQVR42mNoaGj4Ty5mGNU8qpkQBgDBIHZ6rI/MaQAAAABJRU5ErkJggg=="),
    DARKGRAY(SignPdfUiMessages.getString("ColorResource.4"), new Color(64, 64, 64), "darkGray", "iVBORw0KGgoAAAANSUhEUgAAAA8AAAAKCAYAAABrGwT5AAAAFklEQVR42mNwcHD4Ty5mGNU8qpkQBgB4hgX6KvpHxgAAAABJRU5ErkJggg=="),
    RED(SignPdfUiMessages.getString("ColorResource.5"), new Color(255, 0, 0), ElementTags.RED, "iVBORw0KGgoAAAANSUhEUgAAAA8AAAAKCAYAAABrGwT5AAAAFklEQVR42mP4z8Dwn1zMMKp5VDMhDAAd0irkzp6dHwAAAABJRU5ErkJggg=="),
    PINK(SignPdfUiMessages.getString("ColorResource.6"), new Color(255, 175, 175), "pink", "iVBORw0KGgoAAAANSUhEUgAAAA8AAAAKCAYAAABrGwT5AAAAFklEQVR42mP4v379f3Ixw6jmUc2EMACA8vf4UWDE9wAAAABJRU5ErkJggg==");

    private final String colorName;
    private Color color;
    private final String pdfKey;
    private Image image;

    ColorResource(String str, Color color, String str2, String str3) {
        this.color = null;
        this.colorName = str;
        this.color = color;
        this.pdfKey = str2;
        try {
            this.image = ImageIO.read(new ByteArrayInputStream(Base64.decode(str3)));
        } catch (IOException e) {
            Logger.getLogger("es.gob.afirma").warning("Error cargando la imagen del color " + str + ": " + e);
            this.image = null;
        }
    }

    @Override // java.lang.Enum
    public String toString() {
        return this.colorName;
    }

    Image getImage() {
        return this.image;
    }

    Color getColor() {
        return this.color;
    }

    static ColorResource[] getAllColorResources() {
        return new ColorResource[]{BLACK, WHITE, LIGHTGRAY, GRAY, DARKGRAY, RED, PINK};
    }

    String getPdfColorKey() {
        return this.pdfKey;
    }
}
