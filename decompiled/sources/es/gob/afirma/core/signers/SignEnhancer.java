package es.gob.afirma.core.signers;

import java.io.IOException;
import java.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/signers/SignEnhancer.class */
public interface SignEnhancer {
    byte[] enhance(byte[] bArr, Properties properties) throws IOException;
}
