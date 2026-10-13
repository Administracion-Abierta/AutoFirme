package es.gob.afirma.standalone.ui.pdf;

import com.aowagie.text.pdf.PdfReader;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/pdf/SignPdfUiUtil.class */
final class SignPdfUiUtil {
    private SignPdfUiUtil() {
    }

    public static Rectangle getScreenBounds(Component component) {
        Rectangle bounds;
        if (component == null || component.getGraphicsConfiguration() == null) {
            bounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getBounds();
        } else {
            bounds = component.getGraphicsConfiguration().getBounds();
        }
        Insets screenInsets = getScreenInsets(component);
        bounds.x += screenInsets.left;
        bounds.y += screenInsets.top;
        bounds.width -= screenInsets.left + screenInsets.right;
        bounds.height -= screenInsets.top + screenInsets.bottom;
        return bounds;
    }

    private static Insets getScreenInsets(Component component) {
        if (component == null || component.getToolkit() == null || component.getGraphicsConfiguration() == null) {
            return Toolkit.getDefaultToolkit().getScreenInsets(GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());
        }
        return component.getToolkit().getScreenInsets(component.getGraphicsConfiguration());
    }

    static List<Dimension> getPageSizes(byte[] bArr, char[] cArr) throws IOException {
        PdfReader pdfReader = new PdfReader(bArr, cArr != null ? new String(cArr).getBytes(StandardCharsets.UTF_8) : null);
        int numberOfPages = pdfReader.getNumberOfPages();
        ArrayList arrayList = new ArrayList(numberOfPages);
        for (int i = 1; i <= numberOfPages; i++) {
            com.aowagie.text.Rectangle pageSizeWithRotation = pdfReader.getPageSizeWithRotation(i);
            arrayList.add(new Dimension(Math.round(pageSizeWithRotation.getWidth()), Math.round(pageSizeWithRotation.getHeight())));
        }
        return arrayList;
    }
}
