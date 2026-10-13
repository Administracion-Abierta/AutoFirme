package org.bouncycastle.jcajce.provider.asymmetric.edec;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.params.Ed448PublicKeyParameters;
import org.bouncycastle.internal.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.jcajce.interfaces.EdDSAPublicKey;
import org.bouncycastle.jcajce.spec.EdDSAParameterSpec;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/edec/BCEdDSAPublicKey.class */
public class BCEdDSAPublicKey implements EdDSAPublicKey {
    static final long serialVersionUID = 1;
    transient AsymmetricKeyParameter eddsaPublicKey;

    BCEdDSAPublicKey(AsymmetricKeyParameter asymmetricKeyParameter) {
        this.eddsaPublicKey = asymmetricKeyParameter;
    }

    BCEdDSAPublicKey(SubjectPublicKeyInfo subjectPublicKeyInfo) {
        populateFromPubKeyInfo(subjectPublicKeyInfo);
    }

    BCEdDSAPublicKey(byte[] bArr, byte[] bArr2) throws InvalidKeySpecException {
        int length = bArr.length;
        if (!Utils.isValidPrefix(bArr, bArr2)) {
            throw new InvalidKeySpecException("raw key data not recognised");
        }
        if (bArr2.length - length == 57) {
            this.eddsaPublicKey = new Ed448PublicKeyParameters(bArr2, length);
        } else {
            if (bArr2.length - length != 32) {
                throw new InvalidKeySpecException("raw key data not recognised");
            }
            this.eddsaPublicKey = new Ed25519PublicKeyParameters(bArr2, length);
        }
    }

    @Override // org.bouncycastle.jcajce.interfaces.EdDSAPublicKey
    public byte[] getPointEncoding() {
        return this.eddsaPublicKey instanceof Ed448PublicKeyParameters ? ((Ed448PublicKeyParameters) this.eddsaPublicKey).getEncoded() : ((Ed25519PublicKeyParameters) this.eddsaPublicKey).getEncoded();
    }

    private void populateFromPubKeyInfo(SubjectPublicKeyInfo subjectPublicKeyInfo) {
        byte[] octets = subjectPublicKeyInfo.getPublicKeyData().getOctets();
        if (EdECObjectIdentifiers.id_Ed448.equals((ASN1Primitive) subjectPublicKeyInfo.getAlgorithm().getAlgorithm())) {
            this.eddsaPublicKey = new Ed448PublicKeyParameters(octets);
        } else {
            this.eddsaPublicKey = new Ed25519PublicKeyParameters(octets);
        }
    }

    @Override // java.security.Key
    public String getAlgorithm() {
        if (Properties.isOverrideSet(Properties.EMULATE_ORACLE)) {
            return "EdDSA";
        }
        return this.eddsaPublicKey instanceof Ed448PublicKeyParameters ? EdDSAParameterSpec.Ed448 : EdDSAParameterSpec.Ed25519;
    }

    @Override // java.security.Key
    public String getFormat() {
        return XMLX509Certificate.JCA_CERT_ID;
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        if (this.eddsaPublicKey instanceof Ed448PublicKeyParameters) {
            byte[] bArr = new byte[KeyFactorySpi.Ed448Prefix.length + 57];
            System.arraycopy(KeyFactorySpi.Ed448Prefix, 0, bArr, 0, KeyFactorySpi.Ed448Prefix.length);
            ((Ed448PublicKeyParameters) this.eddsaPublicKey).encode(bArr, KeyFactorySpi.Ed448Prefix.length);
            return bArr;
        }
        byte[] bArr2 = new byte[KeyFactorySpi.Ed25519Prefix.length + 32];
        System.arraycopy(KeyFactorySpi.Ed25519Prefix, 0, bArr2, 0, KeyFactorySpi.Ed25519Prefix.length);
        ((Ed25519PublicKeyParameters) this.eddsaPublicKey).encode(bArr2, KeyFactorySpi.Ed25519Prefix.length);
        return bArr2;
    }

    AsymmetricKeyParameter engineGetKeyParameters() {
        return this.eddsaPublicKey;
    }

    public String toString() {
        return Utils.keyToString("Public Key", getAlgorithm(), this.eddsaPublicKey);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof PublicKey) {
            return Arrays.areEqual(((PublicKey) obj).getEncoded(), getEncoded());
        }
        return false;
    }

    public int hashCode() {
        return Arrays.hashCode(getEncoded());
    }

    private void readObject(ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        objectInputStream.defaultReadObject();
        populateFromPubKeyInfo(SubjectPublicKeyInfo.getInstance((byte[]) objectInputStream.readObject()));
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(getEncoded());
    }
}
