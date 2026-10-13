package org.bouncycastle.jcajce.provider.asymmetric.mldsa;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jcajce.interfaces.MLDSAPublicKey;
import org.bouncycastle.jcajce.spec.MLDSAParameterSpec;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAPublicKeyParameters;
import org.bouncycastle.pqc.crypto.util.PublicKeyFactory;
import org.bouncycastle.pqc.crypto.util.SubjectPublicKeyInfoFactory;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Fingerprint;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.encoders.Hex;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/mldsa/BCMLDSAPublicKey.class */
public class BCMLDSAPublicKey implements MLDSAPublicKey {
    private static final long serialVersionUID = 1;
    private transient MLDSAPublicKeyParameters params;
    private transient String algorithm;

    public BCMLDSAPublicKey(MLDSAPublicKeyParameters mLDSAPublicKeyParameters) {
        this.params = mLDSAPublicKeyParameters;
        this.algorithm = Strings.toUpperCase(MLDSAParameterSpec.fromName(mLDSAPublicKeyParameters.getParameters().getName()).getName());
    }

    public BCMLDSAPublicKey(SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        init(subjectPublicKeyInfo);
    }

    private void init(SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        this.params = (MLDSAPublicKeyParameters) PublicKeyFactory.createKey(subjectPublicKeyInfo);
        this.algorithm = Strings.toUpperCase(MLDSAParameterSpec.fromName(this.params.getParameters().getName()).getName());
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof BCMLDSAPublicKey) {
            return Arrays.areEqual(this.params.getEncoded(), ((BCMLDSAPublicKey) obj).params.getEncoded());
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

    @Override // org.bouncycastle.jcajce.interfaces.MLDSAPublicKey
    public byte[] getPublicData() {
        return this.params.getEncoded();
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        try {
            return SubjectPublicKeyInfoFactory.createSubjectPublicKeyInfo(this.params).getEncoded();
        } catch (IOException e) {
            return null;
        }
    }

    @Override // java.security.Key
    public String getFormat() {
        return XMLX509Certificate.JCA_CERT_ID;
    }

    @Override // org.bouncycastle.jcajce.interfaces.MLDSAKey
    public MLDSAParameterSpec getParameterSpec() {
        return MLDSAParameterSpec.fromName(this.params.getParameters().getName());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        String strLineSeparator = Strings.lineSeparator();
        byte[] encoded = this.params.getEncoded();
        sb.append(getAlgorithm()).append(" ").append("Public Key").append(" [").append(new Fingerprint(encoded).toString()).append("]").append(strLineSeparator).append("    public data: ").append(Hex.toHexString(encoded)).append(strLineSeparator);
        return sb.toString();
    }

    MLDSAPublicKeyParameters getKeyParams() {
        return this.params;
    }

    private void readObject(ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
        objectInputStream.defaultReadObject();
        init(SubjectPublicKeyInfo.getInstance((byte[]) objectInputStream.readObject()));
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.defaultWriteObject();
        objectOutputStream.writeObject(getEncoded());
    }
}
