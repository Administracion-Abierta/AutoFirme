package org.spongycastle.cms;

import org.spongycastle.asn1.ASN1ObjectIdentifier;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/CMSConfig.class */
public class CMSConfig {
    public static void setSigningEncryptionAlgorithmMapping(String oid, String algorithmName) {
        ASN1ObjectIdentifier id = new ASN1ObjectIdentifier(oid);
        CMSSignedHelper.INSTANCE.setSigningEncryptionAlgorithmMapping(id, algorithmName);
    }

    public static void setSigningDigestAlgorithmMapping(String oid, String algorithmName) {
        ASN1ObjectIdentifier id = new ASN1ObjectIdentifier(oid);
        CMSSignedHelper.INSTANCE.setSigningDigestAlgorithmMapping(id, algorithmName);
    }
}
