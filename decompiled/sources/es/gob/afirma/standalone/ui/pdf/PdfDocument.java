package es.gob.afirma.standalone.ui.pdf;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/pdf/PdfDocument.class */
final class PdfDocument {
    private byte[] bytesPdf;

    PdfDocument() {
    }

    void loadNewPages(List<BufferedImage> list, int i) throws IOException {
        if (this.bytesPdf != null) {
            try {
                Pdf2ImagesConverter.updateUsefulSections(this.bytesPdf, i, list);
            } catch (IOException e) {
                throw new IOException("Error al crear previsualizaciones de pagina", e);
            }
        }
    }

    byte[] getBytesPdf() {
        return this.bytesPdf;
    }

    void setBytesPdf(byte[] bArr) {
        this.bytesPdf = bArr;
    }
}
