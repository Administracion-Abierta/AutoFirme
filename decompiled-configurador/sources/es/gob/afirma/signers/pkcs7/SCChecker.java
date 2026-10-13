package es.gob.afirma.signers.pkcs7;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/signers/pkcs7/SCChecker.class */
public final class SCChecker {
    private static final String SC_VERSION = "1.47";

    public void checkSpongyCastle() {
        try {
            Class.forName("org.spongycastle.asn1.ASN1Primitive");
        } catch (ClassNotFoundException e) {
            throw new InvalidSpongyCastleException(SC_VERSION, "1.46 o anterior", e);
        }
    }
}
