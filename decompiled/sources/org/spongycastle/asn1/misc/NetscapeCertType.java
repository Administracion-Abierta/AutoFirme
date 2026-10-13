package org.spongycastle.asn1.misc;

import org.spongycastle.asn1.DERBitString;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/misc/NetscapeCertType.class */
public class NetscapeCertType extends DERBitString {
    public static final int sslClient = 128;
    public static final int sslServer = 64;
    public static final int smime = 32;
    public static final int objectSigning = 16;
    public static final int reserved = 8;
    public static final int sslCA = 4;
    public static final int smimeCA = 2;
    public static final int objectSigningCA = 1;

    public NetscapeCertType(int usage) {
        super(getBytes(usage), getPadBits(usage));
    }

    public NetscapeCertType(DERBitString usage) {
        super(usage.getBytes(), usage.getPadBits());
    }

    @Override // org.spongycastle.asn1.ASN1BitString
    public String toString() {
        return "NetscapeCertType: 0x" + Integer.toHexString(this.data[0] & 255);
    }
}
