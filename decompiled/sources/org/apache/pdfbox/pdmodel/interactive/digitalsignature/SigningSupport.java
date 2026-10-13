package org.apache.pdfbox.pdmodel.interactive.digitalsignature;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import org.apache.pdfbox.pdfwriter.COSWriter;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/digitalsignature/SigningSupport.class */
public class SigningSupport implements ExternalSigningSupport, Closeable {
    private COSWriter cosWriter;

    public SigningSupport(COSWriter cosWriter) {
        this.cosWriter = cosWriter;
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.digitalsignature.ExternalSigningSupport
    public InputStream getContent() throws IOException {
        return this.cosWriter.getDataToSign();
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.digitalsignature.ExternalSigningSupport
    public void setSignature(byte[] signature) throws IOException {
        this.cosWriter.writeExternalSignature(signature);
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.cosWriter != null) {
            try {
                this.cosWriter.close();
            } finally {
                this.cosWriter = null;
            }
        }
    }
}
