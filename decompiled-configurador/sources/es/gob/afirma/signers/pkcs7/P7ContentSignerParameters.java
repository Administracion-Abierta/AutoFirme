package es.gob.afirma.signers.pkcs7;

import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/signers/pkcs7/P7ContentSignerParameters.class */
public final class P7ContentSignerParameters {
    private final byte[] data;
    private final String sigAlgo;
    private final byte[] signature;
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    public P7ContentSignerParameters(byte[] bArr, String str) {
        this.data = bArr != null ? (byte[]) bArr.clone() : null;
        if (str == null || str.length() < 1) {
            LOGGER.warning("No se especifico algoritmo para la firma CADES, se utilizara SHA512withRSA");
            this.sigAlgo = "SHA512withRSA";
        } else {
            this.sigAlgo = str;
        }
        this.signature = new byte[0];
    }

    public byte[] getContent() {
        if (this.data != null) {
            return (byte[]) this.data.clone();
        }
        return null;
    }

    public byte[] getSignature() {
        return (byte[]) this.signature.clone();
    }

    public String getSignatureAlgorithm() {
        return this.sigAlgo;
    }
}
