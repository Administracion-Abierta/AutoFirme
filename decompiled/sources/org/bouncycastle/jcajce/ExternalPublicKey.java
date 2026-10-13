package org.bouncycastle.jcajce;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.PublicKey;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.asn1.bc.ExternalValue;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.util.MessageDigestUtils;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/ExternalPublicKey.class */
public class ExternalPublicKey implements PublicKey {
    private final GeneralName location;
    private final AlgorithmIdentifier digestAlg;
    private final byte[] digest;

    public ExternalPublicKey(GeneralName generalName, AlgorithmIdentifier algorithmIdentifier, byte[] bArr) {
        this.location = generalName;
        this.digestAlg = algorithmIdentifier;
        this.digest = Arrays.clone(bArr);
    }

    public ExternalPublicKey(PublicKey publicKey, GeneralName generalName, MessageDigest messageDigest) {
        this(generalName, MessageDigestUtils.getDigestAlgID(messageDigest.getAlgorithm()), messageDigest.digest(publicKey.getEncoded()));
    }

    public ExternalPublicKey(ExternalValue externalValue) {
        this(externalValue.getLocation(), externalValue.getHashAlg(), externalValue.getHashValue());
    }

    @Override // java.security.Key
    public String getAlgorithm() {
        return "ExternalKey";
    }

    @Override // java.security.Key
    public String getFormat() {
        return XMLX509Certificate.JCA_CERT_ID;
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        try {
            return new SubjectPublicKeyInfo(new AlgorithmIdentifier(BCObjectIdentifiers.external_value), new ExternalValue(this.location, this.digestAlg, this.digest)).getEncoded("DER");
        } catch (IOException e) {
            throw new IllegalStateException("unable to encode composite key: " + e.getMessage());
        }
    }
}
