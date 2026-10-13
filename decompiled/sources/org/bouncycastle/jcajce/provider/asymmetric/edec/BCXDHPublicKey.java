package org.bouncycastle.jcajce.provider.asymmetric.edec;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;
import org.bouncycastle.crypto.params.X448PublicKeyParameters;
import org.bouncycastle.internal.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.jcajce.interfaces.XDHPublicKey;
import org.bouncycastle.jcajce.spec.XDHParameterSpec;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/edec/BCXDHPublicKey.class */
public class BCXDHPublicKey implements XDHPublicKey {
    static final long serialVersionUID = 1;
    transient AsymmetricKeyParameter xdhPublicKey;

    BCXDHPublicKey(AsymmetricKeyParameter asymmetricKeyParameter) {
        this.xdhPublicKey = asymmetricKeyParameter;
    }

    BCXDHPublicKey(SubjectPublicKeyInfo subjectPublicKeyInfo) {
        populateFromPubKeyInfo(subjectPublicKeyInfo);
    }

    BCXDHPublicKey(byte[] bArr, byte[] bArr2) throws InvalidKeySpecException {
        int length = bArr.length;
        if (!Utils.isValidPrefix(bArr, bArr2)) {
            throw new InvalidKeySpecException("raw key data not recognised");
        }
        if (bArr2.length - length == 56) {
            this.xdhPublicKey = new X448PublicKeyParameters(bArr2, length);
        } else {
            if (bArr2.length - length != 32) {
                throw new InvalidKeySpecException("raw key data not recognised");
            }
            this.xdhPublicKey = new X25519PublicKeyParameters(bArr2, length);
        }
    }

    private void populateFromPubKeyInfo(SubjectPublicKeyInfo subjectPublicKeyInfo) {
        byte[] octets = subjectPublicKeyInfo.getPublicKeyData().getOctets();
        if (EdECObjectIdentifiers.id_X448.equals((ASN1Primitive) subjectPublicKeyInfo.getAlgorithm().getAlgorithm())) {
            this.xdhPublicKey = new X448PublicKeyParameters(octets);
        } else {
            this.xdhPublicKey = new X25519PublicKeyParameters(octets);
        }
    }

    @Override // java.security.Key
    public String getAlgorithm() {
        if (Properties.isOverrideSet(Properties.EMULATE_ORACLE)) {
            return "XDH";
        }
        return this.xdhPublicKey instanceof X448PublicKeyParameters ? XDHParameterSpec.X448 : XDHParameterSpec.X25519;
    }

    @Override // java.security.Key
    public String getFormat() {
        return XMLX509Certificate.JCA_CERT_ID;
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        if (this.xdhPublicKey instanceof X448PublicKeyParameters) {
            byte[] bArr = new byte[KeyFactorySpi.x448Prefix.length + 56];
            System.arraycopy(KeyFactorySpi.x448Prefix, 0, bArr, 0, KeyFactorySpi.x448Prefix.length);
            ((X448PublicKeyParameters) this.xdhPublicKey).encode(bArr, KeyFactorySpi.x448Prefix.length);
            return bArr;
        }
        byte[] bArr2 = new byte[KeyFactorySpi.x25519Prefix.length + 32];
        System.arraycopy(KeyFactorySpi.x25519Prefix, 0, bArr2, 0, KeyFactorySpi.x25519Prefix.length);
        ((X25519PublicKeyParameters) this.xdhPublicKey).encode(bArr2, KeyFactorySpi.x25519Prefix.length);
        return bArr2;
    }

    AsymmetricKeyParameter engineGetKeyParameters() {
        return this.xdhPublicKey;
    }

    @Override // org.bouncycastle.jcajce.interfaces.XDHPublicKey
    public BigInteger getU() {
        byte[] uEncoding = getUEncoding();
        Arrays.reverseInPlace(uEncoding);
        return new BigInteger(1, uEncoding);
    }

    @Override // org.bouncycastle.jcajce.interfaces.XDHPublicKey
    public byte[] getUEncoding() {
        return this.xdhPublicKey instanceof X448PublicKeyParameters ? ((X448PublicKeyParameters) this.xdhPublicKey).getEncoded() : ((X25519PublicKeyParameters) this.xdhPublicKey).getEncoded();
    }

    public String toString() {
        return Utils.keyToString("Public Key", getAlgorithm(), this.xdhPublicKey);
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
