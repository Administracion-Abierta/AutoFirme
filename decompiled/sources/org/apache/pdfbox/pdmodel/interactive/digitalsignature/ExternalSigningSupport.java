package org.apache.pdfbox.pdmodel.interactive.digitalsignature;

import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/digitalsignature/ExternalSigningSupport.class */
public interface ExternalSigningSupport {
    InputStream getContent() throws IOException;

    void setSignature(byte[] bArr) throws IOException;
}
