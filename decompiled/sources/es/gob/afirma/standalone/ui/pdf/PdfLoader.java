package es.gob.afirma.standalone.ui.pdf;

import java.io.IOException;
import java.util.EventListener;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/pdf/PdfLoader.class */
final class PdfLoader {

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/pdf/PdfLoader$PdfLoaderListener.class */
    interface PdfLoaderListener extends EventListener {
        void pdfLoaded(boolean z, boolean z2, byte[] bArr) throws IOException;

        void pdfLoadedFailed(Throwable th);
    }

    private PdfLoader() {
    }

    static void loadPdf(boolean z, boolean z2, byte[] bArr, PdfLoaderListener pdfLoaderListener) {
        new Thread(() -> {
            try {
                pdfLoaderListener.pdfLoaded(z, z2, bArr);
            } catch (IOException | OutOfMemoryError e) {
                pdfLoaderListener.pdfLoadedFailed(e);
            }
        }).start();
    }
}
