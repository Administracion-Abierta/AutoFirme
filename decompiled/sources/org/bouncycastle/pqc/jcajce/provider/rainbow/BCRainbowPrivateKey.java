package org.bouncycastle.pqc.jcajce.provider.rainbow;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.pqc.crypto.rainbow.RainbowPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.rainbow.RainbowPublicKeyParameters;
import org.bouncycastle.pqc.crypto.util.PrivateKeyFactory;
import org.bouncycastle.pqc.jcajce.interfaces.RainbowPrivateKey;
import org.bouncycastle.pqc.jcajce.interfaces.RainbowPublicKey;
import org.bouncycastle.pqc.jcajce.provider.util.KeyUtil;
import org.bouncycastle.pqc.jcajce.spec.RainbowParameterSpec;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/provider/rainbow/BCRainbowPrivateKey.class */
public class BCRainbowPrivateKey implements RainbowPrivateKey {
    private static final long serialVersionUID = 1;
    private transient RainbowPrivateKeyParameters params;
    private transient String algorithm;
    private transient byte[] encoding;
    private transient ASN1Set attributes;

    public BCRainbowPrivateKey(RainbowPrivateKeyParameters rainbowPrivateKeyParameters) {
        init(rainbowPrivateKeyParameters, null);
    }

    public BCRainbowPrivateKey(PrivateKeyInfo privateKeyInfo) throws IOException {
        init(privateKeyInfo);
    }

    private void init(PrivateKeyInfo privateKeyInfo) throws IOException {
        init((RainbowPrivateKeyParameters) PrivateKeyFactory.createKey(privateKeyInfo), privateKeyInfo.getAttributes());
    }

    private void init(RainbowPrivateKeyParameters rainbowPrivateKeyParameters, ASN1Set aSN1Set) {
        this.attributes = aSN1Set;
        this.params = rainbowPrivateKeyParameters;
        this.algorithm = Strings.toUpperCase(rainbowPrivateKeyParameters.getParameters().getName());
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof BCRainbowPrivateKey) {
            return Arrays.areEqual(getEncoded(), ((BCRainbowPrivateKey) obj).getEncoded());
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
            this.encoding = KeyUtil.getEncodedPrivateKeyInfo(this.params, this.attributes);
        }
        return Arrays.clone(this.encoding);
    }

    @Override // org.bouncycastle.pqc.jcajce.interfaces.RainbowKey
    public RainbowParameterSpec getParameterSpec() {
        return RainbowParameterSpec.fromName(this.params.getParameters().getName());
    }

    @Override // java.security.Key
    public String getFormat() {
        return "PKCS#8";
    }

    @Override // org.bouncycastle.pqc.jcajce.interfaces.RainbowPrivateKey
    public RainbowPublicKey getPublicKey() {
        return new BCRainbowPublicKey(new RainbowPublicKeyParameters(this.params.getParameters(), this.params.getPublicKey()));
    }

    RainbowPrivateKeyParameters getKeyParams() {
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
