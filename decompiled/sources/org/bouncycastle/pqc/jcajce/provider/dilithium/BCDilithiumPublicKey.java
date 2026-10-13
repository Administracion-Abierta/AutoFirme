package org.bouncycastle.pqc.jcajce.provider.dilithium;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.pqc.crypto.crystals.dilithium.DilithiumPublicKeyParameters;
import org.bouncycastle.pqc.crypto.util.PublicKeyFactory;
import org.bouncycastle.pqc.jcajce.interfaces.DilithiumPublicKey;
import org.bouncycastle.pqc.jcajce.provider.util.KeyUtil;
import org.bouncycastle.pqc.jcajce.spec.DilithiumParameterSpec;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/dilithium/BCDilithiumPublicKey.class */
public class BCDilithiumPublicKey implements DilithiumPublicKey {
    private static final long serialVersionUID = 1;
    private transient DilithiumPublicKeyParameters params;
    private transient String algorithm;
    private transient byte[] encoding;

    public BCDilithiumPublicKey(DilithiumPublicKeyParameters dilithiumPublicKeyParameters) {
        init(dilithiumPublicKeyParameters);
    }

    public BCDilithiumPublicKey(SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        init(subjectPublicKeyInfo);
    }

    private void init(SubjectPublicKeyInfo subjectPublicKeyInfo) throws IOException {
        init((DilithiumPublicKeyParameters) PublicKeyFactory.createKey(subjectPublicKeyInfo));
    }

    private void init(DilithiumPublicKeyParameters dilithiumPublicKeyParameters) {
        this.params = dilithiumPublicKeyParameters;
        this.algorithm = Strings.toUpperCase(dilithiumPublicKeyParameters.getParameters().getName());
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof BCDilithiumPublicKey) {
            return Arrays.areEqual(getEncoded(), ((BCDilithiumPublicKey) obj).getEncoded());
        }
        return false;
    }

    public int hashCode() {
        return Arrays.hashCode(getEncoded());
    }

    @Override // java.security.Key
    public final String getAlgorithm() {
        return this.algorithm;
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        if (this.encoding == null) {
            this.encoding = KeyUtil.getEncodedSubjectPublicKeyInfo(this.params);
        }
        return Arrays.clone(this.encoding);
    }

    @Override // java.security.Key
    public String getFormat() {
        return XMLX509Certificate.JCA_CERT_ID;
    }

    @Override // org.bouncycastle.pqc.jcajce.interfaces.DilithiumKey
    public DilithiumParameterSpec getParameterSpec() {
        return DilithiumParameterSpec.fromName(this.params.getParameters().getName());
    }

    DilithiumPublicKeyParameters getKeyParams() {
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
