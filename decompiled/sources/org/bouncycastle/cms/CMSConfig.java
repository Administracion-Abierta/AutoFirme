package org.bouncycastle.cms;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/CMSConfig.class */
public class CMSConfig {
    public static void setSigningEncryptionAlgorithmMapping(String str, String str2) {
        CMSSignedHelper.INSTANCE.setSigningEncryptionAlgorithmMapping(new ASN1ObjectIdentifier(str), str2);
    }

    public static void setSigningDigestAlgorithmMapping(String str, String str2) {
        new ASN1ObjectIdentifier(str);
    }
}
