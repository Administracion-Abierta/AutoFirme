package org.bouncycastle.pqc.jcajce.provider.ntruprime;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PublicKey;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimePublicKeyParameters;
import org.bouncycastle.pqc.crypto.util.PublicKeyFactory;
import org.bouncycastle.pqc.crypto.util.SubjectPublicKeyInfoFactory;
import org.bouncycastle.pqc.jcajce.interfaces.NTRULPRimeKey;
import org.bouncycastle.pqc.jcajce.spec.NTRULPRimeParameterSpec;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/ntruprime/BCNTRULPRimePublicKey.class */
public class BCNTRULPRimePublicKey implements PublicKey, NTRULPRimeKey {
    private static final long serialVersionUID = 1;
    private transient NTRULPRimePublicKeyParameters params;

    public BCNTRULPRimePublicKey(NTRULPRimePublicKeyParameters nTRULPRimePublicKeyParameters) {
        this.params = nTRULPRimePublicKeyParameters;
    }

    public BCNTRULPRimePublicKey(SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        init(subjectPublicKeyInfo);
    }

    private void init(SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        this.params = (NTRULPRimePublicKeyParameters) PublicKeyFactory.createKey(subjectPublicKeyInfo);
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof BCNTRULPRimePublicKey) {
            return Arrays.areEqual(this.params.getEncoded(), ((BCNTRULPRimePublicKey) obj).params.getEncoded());
        }
        return false;
    }

    public int hashCode() {
        return Arrays.hashCode(this.params.getEncoded());
    }

    @Override // java.security.Key
    public final String getAlgorithm() {
        return "NTRULPRime";
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

    @Override // org.bouncycastle.pqc.jcajce.interfaces.NTRULPRimeKey
    public NTRULPRimeParameterSpec getParameterSpec() {
        return NTRULPRimeParameterSpec.fromName(this.params.getParameters().getName());
    }

    NTRULPRimePublicKeyParameters getKeyParams() {
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
