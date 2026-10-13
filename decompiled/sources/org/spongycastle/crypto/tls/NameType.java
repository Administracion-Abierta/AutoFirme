package org.spongycastle.crypto.tls;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/NameType.class */
public class NameType {
    public static final short host_name = 0;

    public static boolean isValid(short nameType) {
        return nameType == 0;
    }
}
