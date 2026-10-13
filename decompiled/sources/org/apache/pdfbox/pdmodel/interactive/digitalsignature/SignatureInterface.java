package org.apache.pdfbox.pdmodel.interactive.digitalsignature;

import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/digitalsignature/SignatureInterface.class */
public interface SignatureInterface {
    byte[] sign(InputStream inputStream) throws IOException;
}
