package es.gob.afirma.core.signers.der;

import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/signers/der/DerEncoder.class */
public interface DerEncoder {
    void derEncode(OutputStream outputStream) throws IOException;
}
