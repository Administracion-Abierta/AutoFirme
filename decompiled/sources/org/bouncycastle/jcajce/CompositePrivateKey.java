package org.bouncycastle.jcajce;

import java.io.IOException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.internal.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.compositesignatures.CompositeIndex;
import org.bouncycastle.jcajce.provider.asymmetric.compositesignatures.KeyFactorySpi;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Exceptions;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/CompositePrivateKey.class */
public class CompositePrivateKey implements PrivateKey {
    private final List<PrivateKey> keys;
    private ASN1ObjectIdentifier algorithmIdentifier;

    public CompositePrivateKey(PrivateKey... privateKeyArr) {
        this(MiscObjectIdentifiers.id_composite_key, privateKeyArr);
    }

    public CompositePrivateKey(ASN1ObjectIdentifier aSN1ObjectIdentifier, PrivateKey... privateKeyArr) {
        this.algorithmIdentifier = aSN1ObjectIdentifier;
        if (privateKeyArr == null || privateKeyArr.length == 0) {
            throw new IllegalArgumentException("at least one private key must be provided for the composite private key");
        }
        ArrayList arrayList = new ArrayList(privateKeyArr.length);
        for (PrivateKey privateKey : privateKeyArr) {
            arrayList.add(privateKey);
        }
        this.keys = Collections.unmodifiableList(arrayList);
    }

    public CompositePrivateKey(PrivateKeyInfo privateKeyInfo) {
        try {
            if (!CompositeIndex.isAlgorithmSupported(privateKeyInfo.getPrivateKeyAlgorithm().getAlgorithm())) {
                throw new IllegalStateException("Unable to create CompositePrivateKey from PrivateKeyInfo");
            }
            CompositePrivateKey compositePrivateKey = (CompositePrivateKey) new KeyFactorySpi().generatePrivate(privateKeyInfo);
            if (compositePrivateKey == null) {
                throw new IllegalStateException("Unable to create CompositePrivateKey from PrivateKeyInfo");
            }
            this.keys = compositePrivateKey.getPrivateKeys();
            this.algorithmIdentifier = compositePrivateKey.getAlgorithmIdentifier();
        } catch (IOException e) {
            throw Exceptions.illegalStateException(e.getMessage(), e);
        }
    }

    public List<PrivateKey> getPrivateKeys() {
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
        return "PKCS#8";
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        if (this.algorithmIdentifier.equals((ASN1Primitive) MiscObjectIdentifiers.id_composite_key)) {
            for (int i = 0; i < this.keys.size(); i++) {
                aSN1EncodableVector.add(PrivateKeyInfo.getInstance(this.keys.get(i).getEncoded()));
            }
            try {
                return new PrivateKeyInfo(new AlgorithmIdentifier(this.algorithmIdentifier), new DERSequence(aSN1EncodableVector)).getEncoded("DER");
            } catch (IOException e) {
                throw new IllegalStateException("unable to encode composite private key: " + e.getMessage());
            }
        }
        byte[] bArrConcatenate = null;
        for (int i2 = 0; i2 < this.keys.size(); i2++) {
            bArrConcatenate = Arrays.concatenate(bArrConcatenate, PrivateKeyInfo.getInstance(this.keys.get(i2).getEncoded()).getPrivateKey().getOctets());
        }
        try {
            return new PrivateKeyInfo(new AlgorithmIdentifier(this.algorithmIdentifier), bArrConcatenate).getEncoded("DER");
        } catch (IOException e2) {
            throw new IllegalStateException("unable to encode composite private key: " + e2.getMessage());
        }
    }

    public int hashCode() {
        return this.keys.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CompositePrivateKey)) {
            return false;
        }
        boolean z = true;
        CompositePrivateKey compositePrivateKey = (CompositePrivateKey) obj;
        if (!compositePrivateKey.getAlgorithmIdentifier().equals((ASN1Primitive) this.algorithmIdentifier) || !this.keys.equals(compositePrivateKey.keys)) {
            z = false;
        }
        return z;
    }
}
