package es.gob.afirma.core.signers;

import es.gob.afirma.core.AOException;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Properties;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/signers/AOSimpleSigner.class */
public interface AOSimpleSigner {
    byte[] sign(byte[] bArr, String str, PrivateKey privateKey, Certificate[] certificateArr, Properties properties) throws IOException, AOException;
}
