package es.gob.afirma.standalone.ui.pdf;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/pdf/Pdf2ImagesConverter.class */
final class Pdf2ImagesConverter {
    Pdf2ImagesConverter() {
    }

    static List<BufferedImage> pdf2Images(byte[] bArr) throws IOException {
        PDDocument pDDocumentLoad = PDDocument.load(new ByteArrayInputStream(bArr));
        Throwable th = null;
        try {
            try {
                PDFRenderer pDFRenderer = new PDFRenderer(pDDocumentLoad);
                ArrayList arrayList = new ArrayList(pDDocumentLoad.getNumberOfPages());
                for (int i = 0; i < pDDocumentLoad.getNumberOfPages(); i++) {
                    arrayList.add(pDFRenderer.renderImageWithDPI(i, 75.0f, ImageType.RGB));
                }
                if (pDDocumentLoad != null) {
                    if (0 != 0) {
                        try {
                            pDDocumentLoad.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        pDDocumentLoad.close();
                    }
                }
                return arrayList;
            } catch (Throwable th3) {
                th = th3;
                throw th3;
            }
        } catch (Throwable th4) {
            if (pDDocumentLoad != null) {
                if (th != null) {
                    try {
                        pDDocumentLoad.close();
                    } catch (Throwable th5) {
                        th.addSuppressed(th5);
                    }
                } else {
                    pDDocumentLoad.close();
                }
            }
            throw th4;
        }
    }

    static List<BufferedImage> pdf2ImagesUsefulSections(byte[] bArr, char[] cArr, int i) throws IOException {
        PDDocument pDDocumentLoad = PDDocument.load(new ByteArrayInputStream(bArr), cArr != null ? new String(cArr) : null);
        Throwable th = null;
        try {
            try {
                PDFRenderer pDFRenderer = new PDFRenderer(pDDocumentLoad);
                ArrayList arrayList = new ArrayList(pDDocumentLoad.getNumberOfPages());
                for (int i2 = 0; i2 < pDDocumentLoad.getNumberOfPages(); i2++) {
                    if (isOfTheFirstPages(i2) || isOfTheLastPages(i2, pDDocumentLoad.getNumberOfPages()) || isNearTheCurrentPage(i2, i)) {
                        arrayList.add(pDFRenderer.renderImageWithDPI(i2, 75.0f, ImageType.RGB));
                    } else {
                        arrayList.add(null);
                    }
                }
                if (pDDocumentLoad != null) {
                    if (0 != 0) {
                        try {
                            pDDocumentLoad.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        pDDocumentLoad.close();
                    }
                }
                return arrayList;
            } catch (Throwable th3) {
                th = th3;
                throw th3;
            }
        } catch (Throwable th4) {
            if (pDDocumentLoad != null) {
                if (th != null) {
                    try {
                        pDDocumentLoad.close();
                    } catch (Throwable th5) {
                        th.addSuppressed(th5);
                    }
                } else {
                    pDDocumentLoad.close();
                }
            }
            throw th4;
        }
    }

    /* JADX WARN: Bottom block not found for handler: all -> 0x00a3 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    static void updateUsefulSections(byte[] r7, int r8, java.util.List<java.awt.image.BufferedImage> r9) throws java.io.IOException {
        /*
            Method dump skipped, instruction units count: 201
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: es.gob.afirma.standalone.ui.pdf.Pdf2ImagesConverter.updateUsefulSections(byte[], int, java.util.List):void");
    }

    private static boolean isOfTheFirstPages(int i) {
        return i < 4;
    }

    private static boolean isOfTheLastPages(int i, int i2) {
        return i >= i2 - 4 && i < i2;
    }

    private static boolean isNearTheCurrentPage(int i, int i2) {
        return Math.abs(i - i2) <= 4;
    }
}
