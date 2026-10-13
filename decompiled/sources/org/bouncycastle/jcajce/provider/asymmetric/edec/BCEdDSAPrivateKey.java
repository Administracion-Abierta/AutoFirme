package org.bouncycastle.jcajce.provider.asymmetric.edec;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PrivateKey;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.params.Ed448PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed448PublicKeyParameters;
import org.bouncycastle.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.internal.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.jcajce.interfaces.EdDSAPrivateKey;
import org.bouncycastle.jcajce.interfaces.EdDSAPublicKey;
import org.bouncycastle.jcajce.spec.EdDSAParameterSpec;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/edec/BCEdDSAPrivateKey.class */
public class BCEdDSAPrivateKey implements EdDSAPrivateKey {
    static final long serialVersionUID = 1;
    transient AsymmetricKeyParameter eddsaPrivateKey;
    transient AsymmetricKeyParameter eddsaPublicKey;
    transient int hashCode;
    private final boolean hasPublicKey;
    private final byte[] attributes;

    BCEdDSAPrivateKey(AsymmetricKeyParameter asymmetricKeyParameter) {
        this.hasPublicKey = true;
        this.attributes = null;
        this.eddsaPrivateKey = asymmetricKeyParameter;
        if (this.eddsaPrivateKey instanceof Ed448PrivateKeyParameters) {
            this.eddsaPublicKey = ((Ed448PrivateKeyParameters) this.eddsaPrivateKey).generatePublicKey();
        } else {
            this.eddsaPublicKey = ((Ed25519PrivateKeyParameters) this.eddsaPrivateKey).generatePublicKey();
        }
        this.hashCode = calculateHashCode();
    }

    BCEdDSAPrivateKey(PrivateKeyInfo privateKeyInfo) throws IOException {
        this.hasPublicKey = privateKeyInfo.hasPublicKey();
        this.attributes = privateKeyInfo.getAttributes() != null ? privateKeyInfo.getAttributes().getEncoded() : null;
        populateFromPrivateKeyInfo(privateKeyInfo);
    }

    private void populateFromPrivateKeyInfo(PrivateKeyInfo privateKeyInfo) throws IOException {
        byte[] octets = ASN1OctetString.getInstance(privateKeyInfo.parsePrivateKey()).getOctets();
        if (EdECObjectIdentifiers.id_Ed448.equals((ASN1Primitive) privateKeyInfo.getPrivateKeyAlgorithm().getAlgorithm())) {
            this.eddsaPrivateKey = new Ed448PrivateKeyParameters(octets);
            this.eddsaPublicKey = ((Ed448PrivateKeyParameters) this.eddsaPrivateKey).generatePublicKey();
        } else {
            this.eddsaPrivateKey = new Ed25519PrivateKeyParameters(octets);
            this.eddsaPublicKey = ((Ed25519PrivateKeyParameters) this.eddsaPrivateKey).generatePublicKey();
        }
        this.hashCode = calculateHashCode();
    }

    @Override // java.security.Key
    public String getAlgorithm() {
        if (Properties.isOverrideSet(Properties.EMULATE_ORACLE)) {
            return "EdDSA";
        }
        return this.eddsaPrivateKey instanceof Ed448PrivateKeyParameters ? EdDSAParameterSpec.Ed448 : EdDSAParameterSpec.Ed25519;
    }

    @Override // java.security.Key
    public String getFormat() {
        return "PKCS#8";
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        try {
            PrivateKeyInfo privateKeyInfo = getPrivateKeyInfo();
            if (privateKeyInfo == null) {
                return null;
            }
            return privateKeyInfo.getEncoded();
        } catch (IOException e) {
            return null;
        }
    }

    private PrivateKeyInfo getPrivateKeyInfo() {
        try {
            ASN1Set aSN1Set = ASN1Set.getInstance(this.attributes);
            PrivateKeyInfo privateKeyInfoCreatePrivateKeyInfo = PrivateKeyInfoFactory.createPrivateKeyInfo(this.eddsaPrivateKey, aSN1Set);
            return (!this.hasPublicKey || Properties.isOverrideSet("org.bouncycastle.pkcs8.v1_info_only")) ? new PrivateKeyInfo(privateKeyInfoCreatePrivateKeyInfo.getPrivateKeyAlgorithm(), privateKeyInfoCreatePrivateKeyInfo.parsePrivateKey(), aSN1Set) : privateKeyInfoCreatePrivateKeyInfo;
        } catch (IOException e) {
            return null;
        }
    }

    @Override // org.bouncycastle.jcajce.interfaces.EdDSAPrivateKey
    public EdDSAPublicKey getPublicKey() {
        return new BCEdDSAPublicKey(this.eddsaPublicKey);
    }

    AsymmetricKeyParameter engineGetKeyParameters() {
        return this.eddsaPrivateKey;
    }

    public String toString() {
        return Utils.keyToString("Private Key", getAlgorithm(), this.eddsaPublicKey);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PrivateKey)) {
            return false;
        }
        PrivateKey privateKey = (PrivateKey) obj;
        PrivateKeyInfo privateKeyInfo = getPrivateKeyInfo();
        PrivateKeyInfo privateKeyInfo2 = privateKey instanceof BCEdDSAPrivateKey ? ((BCEdDSAPrivateKey) privateKey).getPrivateKeyInfo() : PrivateKeyInfo.getInstance(privateKey.getEncoded());
        if (privateKeyInfo == null || privateKeyInfo2 == null) {
            return false;
        }
        try {
            return Arrays.constantTimeAreEqual(privateKeyInfo.getPrivateKeyAlgorithm().getEncoded(), privateKeyInfo2.getPrivateKeyAlgorithm().getEncoded()) & Arrays.constantTimeAreEqual(privateKeyInfo.getPrivateKey().getEncoded(), privateKeyInfo2.getPrivateKey().getEncoded());
        } catch (IOException e) {
            return false;
        }
    }

    public int hashCode() {
        return this.hashCode;
    }

    private int calculateHashCode() {
        return (31 * getAlgorithm().hashCode()) + Arrays.hashCode(this.eddsaPublicKey instanceof Ed448PublicKeyParameters ? ((Ed448PublicKeyParameters) this.eddsaPublicKey).getEncoded() : ((Ed25519PublicKeyParameters) this.eddsaPublicKey).getEncoded());
    }

    private void readObject(ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        objectInputStream.defaultReadObject();
        populateFromPrivateKeyInfo(PrivateKeyInfo.getInstance((byte[]) objectInputStream.readObject()));
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(getEncoded());
    }
}
