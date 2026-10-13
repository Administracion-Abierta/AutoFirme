package org.bouncycastle.pqc.jcajce.provider.lms;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.PrivateKey;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.pqc.crypto.lms.HSSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.lms.LMSKeyParameters;
import org.bouncycastle.pqc.crypto.lms.LMSPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.util.PrivateKeyFactory;
import org.bouncycastle.pqc.crypto.util.PrivateKeyInfoFactory;
import org.bouncycastle.pqc.jcajce.interfaces.LMSPrivateKey;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/lms/BCLMSPrivateKey.class */
public class BCLMSPrivateKey implements PrivateKey, LMSPrivateKey {
    private static final long serialVersionUID = 8568701712864512338L;
    private transient LMSKeyParameters keyParams;
    private transient ASN1Set attributes;

    public BCLMSPrivateKey(LMSKeyParameters lMSKeyParameters) {
        this.keyParams = lMSKeyParameters instanceof HSSPrivateKeyParameters ? (HSSPrivateKeyParameters) lMSKeyParameters : new HSSPrivateKeyParameters((LMSPrivateKeyParameters) lMSKeyParameters, ((LMSPrivateKeyParameters) lMSKeyParameters).getIndex(), ((long) ((LMSPrivateKeyParameters) lMSKeyParameters).getIndex()) + ((LMSPrivateKeyParameters) lMSKeyParameters).getUsagesRemaining());
    }

    public BCLMSPrivateKey(PrivateKeyInfo privateKeyInfo) throws IOException {
        init(privateKeyInfo);
    }

    private void init(PrivateKeyInfo privateKeyInfo) throws IOException {
        this.attributes = privateKeyInfo.getAttributes();
        this.keyParams = (LMSKeyParameters) PrivateKeyFactory.createKey(privateKeyInfo);
    }

    @Override // org.bouncycastle.pqc.jcajce.interfaces.LMSPrivateKey
    public long getIndex() {
        if (getUsagesRemaining() == 0) {
            throw new IllegalStateException("key exhausted");
        }
        return this.keyParams instanceof LMSPrivateKeyParameters ? ((LMSPrivateKeyParameters) this.keyParams).getIndex() : ((HSSPrivateKeyParameters) this.keyParams).getIndex();
    }

    @Override // org.bouncycastle.pqc.jcajce.interfaces.LMSPrivateKey
    public long getUsagesRemaining() {
        return this.keyParams instanceof LMSPrivateKeyParameters ? ((LMSPrivateKeyParameters) this.keyParams).getUsagesRemaining() : ((HSSPrivateKeyParameters) this.keyParams).getUsagesRemaining();
    }

    @Override // org.bouncycastle.pqc.jcajce.interfaces.LMSPrivateKey
    public LMSPrivateKey extractKeyShard(int i) {
        return this.keyParams instanceof LMSPrivateKeyParameters ? new BCLMSPrivateKey(((LMSPrivateKeyParameters) this.keyParams).extractKeyShard(i)) : new BCLMSPrivateKey(((HSSPrivateKeyParameters) this.keyParams).extractKeyShard(i));
    }

    @Override // java.security.Key
    public String getAlgorithm() {
        return "LMS";
    }

    @Override // java.security.Key
    public String getFormat() {
        return "PKCS#8";
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        try {
            return PrivateKeyInfoFactory.createPrivateKeyInfo(this.keyParams, this.attributes).getEncoded();
        } catch (IOException e) {
            return null;
        }
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof BCLMSPrivateKey)) {
            return false;
        }
        try {
            return Arrays.areEqual(this.keyParams.getEncoded(), ((BCLMSPrivateKey) obj).keyParams.getEncoded());
        } catch (IOException e) {
            throw new IllegalStateException("unable to perform equals");
        }
    }

    public int hashCode() {
        try {
            return Arrays.hashCode(this.keyParams.getEncoded());
        } catch (IOException e) {
            throw new IllegalStateException("unable to calculate hashCode");
        }
    }

    CipherParameters getKeyParams() {
        return this.keyParams;
    }

    @Override // org.bouncycastle.pqc.jcajce.interfaces.LMSKey
    public int getLevels() {
        if (this.keyParams instanceof LMSPrivateKeyParameters) {
            return 1;
        }
        return ((HSSPrivateKeyParameters) this.keyParams).getL();
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
