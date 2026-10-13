package es.gob.afirma.signers.tsp.pkcs7;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/signers/tsp/pkcs7/TsaRequestExtension.class */
public final class TsaRequestExtension {
    private final String oid;
    private final boolean critical;
    private final byte[] value;

    public String toString() {
        return "Extension [OID: " + this.oid + ", citical: " + this.critical + ", value: " + new String(this.value) + "]";
    }

    public TsaRequestExtension(String str, boolean z, byte[] bArr) {
        if (str == null || str.isEmpty()) {
            throw new IllegalArgumentException("Las extensiones TSA necesitan obligatoriamente un OID");
        }
        if (bArr == null || bArr.length < 1) {
            throw new IllegalArgumentException("Las extensiones TSA necesitan obligatoriamente un valor");
        }
        this.oid = str;
        this.critical = z;
        this.value = (byte[]) bArr.clone();
    }

    boolean isCritical() {
        return this.critical;
    }

    String getOid() {
        return this.oid;
    }

    byte[] getValue() {
        return (byte[]) this.value.clone();
    }

    private TsaRequestExtension() {
        this.oid = null;
        this.critical = false;
        this.value = null;
    }
}
