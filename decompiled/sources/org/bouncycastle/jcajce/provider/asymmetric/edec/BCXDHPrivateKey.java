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
import org.bouncycastle.crypto.params.X25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;
import org.bouncycastle.crypto.params.X448PrivateKeyParameters;
import org.bouncycastle.crypto.params.X448PublicKeyParameters;
import org.bouncycastle.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.internal.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.jcajce.interfaces.XDHPrivateKey;
import org.bouncycastle.jcajce.interfaces.XDHPublicKey;
import org.bouncycastle.jcajce.spec.XDHParameterSpec;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Properties;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/edec/BCXDHPrivateKey.class */
public class BCXDHPrivateKey implements XDHPrivateKey {
    static final long serialVersionUID = 1;
    transient AsymmetricKeyParameter xdhPrivateKey;
    transient AsymmetricKeyParameter xdhPublicKey;
    transient int hashCode;
    private final boolean hasPublicKey;
    private final byte[] attributes;

    BCXDHPrivateKey(AsymmetricKeyParameter asymmetricKeyParameter) {
        this.hasPublicKey = true;
        this.attributes = null;
        this.xdhPrivateKey = asymmetricKeyParameter;
        if (this.xdhPrivateKey instanceof X448PrivateKeyParameters) {
            this.xdhPublicKey = ((X448PrivateKeyParameters) this.xdhPrivateKey).generatePublicKey();
        } else {
            this.xdhPublicKey = ((X25519PrivateKeyParameters) this.xdhPrivateKey).generatePublicKey();
        }
        this.hashCode = calculateHashCode();
    }

    BCXDHPrivateKey(PrivateKeyInfo privateKeyInfo) throws IOException {
        this.hasPublicKey = privateKeyInfo.hasPublicKey();
        this.attributes = privateKeyInfo.getAttributes() != null ? privateKeyInfo.getAttributes().getEncoded() : null;
        populateFromPrivateKeyInfo(privateKeyInfo);
    }

    private void populateFromPrivateKeyInfo(PrivateKeyInfo privateKeyInfo) throws IOException {
        int privateKeyLength = privateKeyInfo.getPrivateKeyLength();
        byte[] octets = (privateKeyLength == 32 || privateKeyLength == 56) ? privateKeyInfo.getPrivateKey().getOctets() : ASN1OctetString.getInstance(privateKeyInfo.parsePrivateKey()).getOctets();
        if (EdECObjectIdentifiers.id_X448.equals((ASN1Primitive) privateKeyInfo.getPrivateKeyAlgorithm().getAlgorithm())) {
            this.xdhPrivateKey = new X448PrivateKeyParameters(octets);
            this.xdhPublicKey = ((X448PrivateKeyParameters) this.xdhPrivateKey).generatePublicKey();
        } else {
            this.xdhPrivateKey = new X25519PrivateKeyParameters(octets);
            this.xdhPublicKey = ((X25519PrivateKeyParameters) this.xdhPrivateKey).generatePublicKey();
        }
        this.hashCode = calculateHashCode();
    }

    @Override // java.security.Key
    public String getAlgorithm() {
        if (Properties.isOverrideSet(Properties.EMULATE_ORACLE)) {
            return "XDH";
        }
        return this.xdhPrivateKey instanceof X448PrivateKeyParameters ? XDHParameterSpec.X448 : XDHParameterSpec.X25519;
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
            PrivateKeyInfo privateKeyInfoCreatePrivateKeyInfo = PrivateKeyInfoFactory.createPrivateKeyInfo(this.xdhPrivateKey, aSN1Set);
            return (!this.hasPublicKey || Properties.isOverrideSet("org.bouncycastle.pkcs8.v1_info_only")) ? new PrivateKeyInfo(privateKeyInfoCreatePrivateKeyInfo.getPrivateKeyAlgorithm(), privateKeyInfoCreatePrivateKeyInfo.parsePrivateKey(), aSN1Set) : privateKeyInfoCreatePrivateKeyInfo;
        } catch (IOException e) {
            return null;
        }
    }

    @Override // org.bouncycastle.jcajce.interfaces.XDHPrivateKey
    public XDHPublicKey getPublicKey() {
        return new BCXDHPublicKey(this.xdhPublicKey);
    }

    AsymmetricKeyParameter engineGetKeyParameters() {
        return this.xdhPrivateKey;
    }

    public String toString() {
        return Utils.keyToString("Private Key", getAlgorithm(), this.xdhPublicKey);
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
        PrivateKeyInfo privateKeyInfo2 = privateKey instanceof BCXDHPrivateKey ? ((BCXDHPrivateKey) privateKey).getPrivateKeyInfo() : PrivateKeyInfo.getInstance(privateKey.getEncoded());
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
        return (31 * getAlgorithm().hashCode()) + Arrays.hashCode(this.xdhPublicKey instanceof X448PublicKeyParameters ? ((X448PublicKeyParameters) this.xdhPublicKey).getEncoded() : ((X25519PublicKeyParameters) this.xdhPublicKey).getEncoded());
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
