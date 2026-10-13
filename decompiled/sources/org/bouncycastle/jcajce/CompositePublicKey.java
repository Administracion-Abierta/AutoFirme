package org.bouncycastle.jcajce;

import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.internal.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.compositesignatures.CompositeIndex;
import org.bouncycastle.jcajce.provider.asymmetric.compositesignatures.KeyFactorySpi;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/CompositePublicKey.class */
public class CompositePublicKey implements PublicKey {
    private final List<PublicKey> keys;
    private final ASN1ObjectIdentifier algorithmIdentifier;

    public CompositePublicKey(PublicKey... publicKeyArr) {
        this(MiscObjectIdentifiers.id_composite_key, publicKeyArr);
    }

    public CompositePublicKey(ASN1ObjectIdentifier aSN1ObjectIdentifier, PublicKey... publicKeyArr) {
        this.algorithmIdentifier = aSN1ObjectIdentifier;
        if (publicKeyArr == null || publicKeyArr.length == 0) {
            throw new IllegalArgumentException("at least one public key must be provided for the composite public key");
        }
        ArrayList arrayList = new ArrayList(publicKeyArr.length);
        for (PublicKey publicKey : publicKeyArr) {
            arrayList.add(publicKey);
        }
        this.keys = Collections.unmodifiableList(arrayList);
    }

    public CompositePublicKey(SubjectPublicKeyInfo subjectPublicKeyInfo) {
        try {
            if (!CompositeIndex.isAlgorithmSupported(subjectPublicKeyInfo.getAlgorithm().getAlgorithm())) {
                throw new IllegalStateException("unable to create CompositePublicKey from SubjectPublicKeyInfo");
            }
            CompositePublicKey compositePublicKey = (CompositePublicKey) new KeyFactorySpi().generatePublic(subjectPublicKeyInfo);
            if (compositePublicKey == null) {
                throw new IllegalStateException("unable to create CompositePublicKey from SubjectPublicKeyInfo");
            }
            this.keys = compositePublicKey.getPublicKeys();
            this.algorithmIdentifier = compositePublicKey.getAlgorithmIdentifier();
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public List<PublicKey> getPublicKeys() {
        return this.keys;
    }

    @Override // java.security.Key
    public String getAlgorithm() {
        return CompositeIndex.getAlgorithmName(this.algorithmIdentifier);
    }

    public ASN1ObjectIdentifier getAlgorithmIdentifier() {
        return this.algorithmIdentifier;
    }

    @Override // java.security.Key
    public String getFormat() {
        return XMLX509Certificate.JCA_CERT_ID;
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        for (int i = 0; i < this.keys.size(); i++) {
            if (this.algorithmIdentifier.equals((ASN1Primitive) MiscObjectIdentifiers.id_composite_key)) {
                aSN1EncodableVector.add(SubjectPublicKeyInfo.getInstance(this.keys.get(i).getEncoded()));
            } else {
                aSN1EncodableVector.add(SubjectPublicKeyInfo.getInstance(this.keys.get(i).getEncoded()).getPublicKeyData());
            }
        }
        try {
            return new SubjectPublicKeyInfo(new AlgorithmIdentifier(this.algorithmIdentifier), new DERSequence(aSN1EncodableVector)).getEncoded("DER");
        } catch (IOException e) {
            throw new IllegalStateException("unable to encode composite public key: " + e.getMessage());
        }
    }

    public int hashCode() {
        return this.keys.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CompositePublicKey)) {
            return false;
        }
        boolean z = true;
        CompositePublicKey compositePublicKey = (CompositePublicKey) obj;
        if (!compositePublicKey.getAlgorithmIdentifier().equals((ASN1Primitive) this.algorithmIdentifier) || !this.keys.equals(compositePublicKey.keys)) {
            z = false;
        }
        return z;
    }
}
