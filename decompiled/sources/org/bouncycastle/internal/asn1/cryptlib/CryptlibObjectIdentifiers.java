package org.bouncycastle.internal.asn1.cryptlib;

import com.sun.jna.platform.win32.COM.tlb.imp.TlbConst;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/internal/asn1/cryptlib/CryptlibObjectIdentifiers.class */
public class CryptlibObjectIdentifiers {
    public static final ASN1ObjectIdentifier cryptlib = new ASN1ObjectIdentifier("1.3.6.1.4.1.3029");
    public static final ASN1ObjectIdentifier ecc = cryptlib.branch("1").branch(TlbConst.TYPELIB_MINOR_VERSION_OFFICE);
    public static final ASN1ObjectIdentifier curvey25519 = ecc.branch("1");
}
