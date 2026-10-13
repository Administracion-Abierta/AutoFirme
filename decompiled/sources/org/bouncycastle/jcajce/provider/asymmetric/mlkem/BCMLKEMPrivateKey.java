package org.bouncycastle.jcajce.provider.asymmetric.mlkem;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.jcajce.interfaces.MLKEMPrivateKey;
import org.bouncycastle.jcajce.interfaces.MLKEMPublicKey;
import org.bouncycastle.jcajce.spec.MLKEMParameterSpec;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.util.PrivateKeyFactory;
import org.bouncycastle.pqc.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Fingerprint;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/mlkem/BCMLKEMPrivateKey.class */
public class BCMLKEMPrivateKey implements MLKEMPrivateKey {
    private static final long serialVersionUID = 1;
    private transient MLKEMPrivateKeyParameters params;
    private transient String algorithm;
    private transient ASN1Set attributes;

    public BCMLKEMPrivateKey(MLKEMPrivateKeyParameters mLKEMPrivateKeyParameters) {
        this.params = mLKEMPrivateKeyParameters;
        this.algorithm = Strings.toUpperCase(mLKEMPrivateKeyParameters.getParameters().getName());
    }

    public BCMLKEMPrivateKey(PrivateKeyInfo privateKeyInfo) throws IOException {
        init(privateKeyInfo);
    }

    private void init(PrivateKeyInfo privateKeyInfo) throws IOException {
        this.attributes = privateKeyInfo.getAttributes();
        this.params = (MLKEMPrivateKeyParameters) PrivateKeyFactory.createKey(privateKeyInfo);
        this.algorithm = Strings.toUpperCase(MLKEMParameterSpec.fromName(this.params.getParameters().getName()).getName());
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof BCMLKEMPrivateKey) {
            return Arrays.areEqual(this.params.getEncoded(), ((BCMLKEMPrivateKey) obj).params.getEncoded());
        }
        return false;
    }

    public int hashCode() {
        return Arrays.hashCode(this.params.getEncoded());
    }

    @Override // java.security.Key
    public final String getAlgorithm() {
        return this.algorithm;
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        try {
            return PrivateKeyInfoFactory.createPrivateKeyInfo(this.params, this.attributes).getEncoded();
        } catch (IOException e) {
            return null;
        }
    }

    @Override // org.bouncycastle.jcajce.interfaces.MLKEMPrivateKey
    public MLKEMPublicKey getPublicKey() {
        return new BCMLKEMPublicKey(this.params.getPublicKeyParameters());
    }

    @Override // org.bouncycastle.jcajce.interfaces.MLKEMPrivateKey
    public byte[] getPrivateData() {
        return this.params.getEncoded();
    }

    @Override // org.bouncycastle.jcajce.interfaces.MLKEMPrivateKey
    public byte[] getSeed() {
        return this.params.getSeed();
    }

    @Override // org.bouncycastle.jcajce.interfaces.MLKEMKey
    public MLKEMParameterSpec getParameterSpec() {
        return MLKEMParameterSpec.fromName(this.params.getParameters().getName());
    }

    @Override // java.security.Key
    public String getFormat() {
        return "PKCS#8";
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        String strLineSeparator = Strings.lineSeparator();
        byte[] publicKey = this.params.getPublicKey();
        sb.append(getAlgorithm()).append(" ").append("Private Key").append(" [").append(new Fingerprint(publicKey).toString()).append("]").append(strLineSeparator).append("    public data: ").append(Hex.toHexString(publicKey)).append(strLineSeparator);
        return sb.toString();
    }

    MLKEMPrivateKeyParameters getKeyParams() {
        return this.params;
    }

    private void readObject(ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        objectInputStream.defaultReadObject();
        init(PrivateKeyInfo.getInstance((byte[]) objectInputStream.readObject()));
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(getEncoded());
    }
}
