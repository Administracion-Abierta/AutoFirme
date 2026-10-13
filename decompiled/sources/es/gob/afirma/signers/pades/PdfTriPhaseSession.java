package es.gob.afirma.signers.pades;

import com.aowagie.text.pdf.PdfSignatureAppearance;
import java.io.ByteArrayOutputStream;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/pades/PdfTriPhaseSession.class */
public final class PdfTriPhaseSession {
    private final PdfSignatureAppearance sap;
    private final ByteArrayOutputStream baos;
    private final String fileID;

    public String toString() {
        return "Sesion trifasica del PDF con identificador: " + this.fileID;
    }

    PdfTriPhaseSession(PdfSignatureAppearance pdfSignatureAppearance, ByteArrayOutputStream byteArrayOutputStream, String str) {
        this.sap = pdfSignatureAppearance;
        this.baos = byteArrayOutputStream;
        this.fileID = str;
    }

    public ByteArrayOutputStream getBAOS() {
        return this.baos;
    }

    public PdfSignatureAppearance getSAP() {
        return this.sap;
    }

    public String getFileID() {
        return this.fileID;
    }
}
