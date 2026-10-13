package org.spongycastle.crypto.tls;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/tls/SupplementalDataEntry.class */
public class SupplementalDataEntry {
    protected int dataType;
    protected byte[] data;

    public SupplementalDataEntry(int dataType, byte[] data) {
        this.dataType = dataType;
        this.data = data;
    }

    public int getDataType() {
        return this.dataType;
    }

    public byte[] getData() {
        return this.data;
    }
}
