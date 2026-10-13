package org.bouncycastle.jcajce.provider.symmetric.util;

import java.util.concurrent.atomic.AtomicBoolean;
import javax.crypto.interfaces.PBEKey;
import javax.crypto.spec.PBEKeySpec;
import javax.security.auth.Destroyable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/symmetric/util/BCPBEKey.class */
public class BCPBEKey implements PBEKey, Destroyable {
    private final AtomicBoolean hasBeenDestroyed;
    String algorithm;
    ASN1ObjectIdentifier oid;
    int type;
    int digest;
    int keySize;
    int ivSize;
    private final char[] password;
    private final byte[] salt;
    private final int iterationCount;
    private final CipherParameters param;
    boolean tryWrong;

    public BCPBEKey(String str, ASN1ObjectIdentifier aSN1ObjectIdentifier, int i, int i2, int i3, int i4, PBEKeySpec pBEKeySpec, CipherParameters cipherParameters) {
        this.hasBeenDestroyed = new AtomicBoolean(false);
        this.tryWrong = false;
        this.algorithm = str;
        this.oid = aSN1ObjectIdentifier;
        this.type = i;
        this.digest = i2;
        this.keySize = i3;
        this.ivSize = i4;
        this.password = pBEKeySpec.getPassword();
        this.iterationCount = pBEKeySpec.getIterationCount();
        this.salt = pBEKeySpec.getSalt();
        this.param = cipherParameters;
    }

    public BCPBEKey(String str, CipherParameters cipherParameters) {
        this.hasBeenDestroyed = new AtomicBoolean(false);
        this.tryWrong = false;
        this.algorithm = str;
        this.param = cipherParameters;
        this.password = null;
        this.iterationCount = -1;
        this.salt = null;
    }

    @Override // java.security.Key
    public String getAlgorithm() {
        String str = this.algorithm;
        checkDestroyed(this);
        return str;
    }

    @Override // java.security.Key
    public String getFormat() {
        checkDestroyed(this);
        return "RAW";
    }

    @Override // java.security.Key
    public byte[] getEncoded() {
        byte[] bArrPKCS5PasswordToUTF8Bytes;
        if (this.param != null) {
            bArrPKCS5PasswordToUTF8Bytes = (this.param instanceof ParametersWithIV ? (KeyParameter) ((ParametersWithIV) this.param).getParameters() : (KeyParameter) this.param).getKey();
        } else if (this.type == 2) {
            bArrPKCS5PasswordToUTF8Bytes = PBEParametersGenerator.PKCS12PasswordToBytes(this.password);
        } else {
            bArrPKCS5PasswordToUTF8Bytes = this.type == 5 ? PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(this.password) : PBEParametersGenerator.PKCS5PasswordToBytes(this.password);
        }
        checkDestroyed(this);
        return bArrPKCS5PasswordToUTF8Bytes;
    }

    int getType() {
        int i = this.type;
        checkDestroyed(this);
        return i;
    }

    int getDigest() {
        int i = this.digest;
        checkDestroyed(this);
        return i;
    }

    int getKeySize() {
        int i = this.keySize;
        checkDestroyed(this);
        return i;
    }

    public int getIvSize() {
        int i = this.ivSize;
        checkDestroyed(this);
        return i;
    }

    public CipherParameters getParam() {
        CipherParameters cipherParameters = this.param;
        checkDestroyed(this);
        return cipherParameters;
    }

    @Override // javax.crypto.interfaces.PBEKey
    public char[] getPassword() {
        char[] cArrClone = Arrays.clone(this.password);
        checkDestroyed(this);
        if (cArrClone == null) {
            throw new IllegalStateException("no password available");
        }
        return cArrClone;
    }

    @Override // javax.crypto.interfaces.PBEKey
    public byte[] getSalt() {
        byte[] bArrClone = Arrays.clone(this.salt);
        checkDestroyed(this);
        return bArrClone;
    }

    @Override // javax.crypto.interfaces.PBEKey
    public int getIterationCount() {
        int i = this.iterationCount;
        checkDestroyed(this);
        return i;
    }

    public ASN1ObjectIdentifier getOID() {
        ASN1ObjectIdentifier aSN1ObjectIdentifier = this.oid;
        checkDestroyed(this);
        return aSN1ObjectIdentifier;
    }

    public void setTryWrongPKCS12Zero(boolean z) {
        this.tryWrong = z;
    }

    boolean shouldTryWrongPKCS12() {
        return this.tryWrong;
    }

    @Override // javax.security.auth.Destroyable
    public void destroy() {
        if (this.hasBeenDestroyed.getAndSet(true)) {
            return;
        }
        if (this.password != null) {
            Arrays.fill(this.password, (char) 0);
        }
        if (this.salt != null) {
            Arrays.fill(this.salt, (byte) 0);
        }
    }

    @Override // javax.security.auth.Destroyable
    public boolean isDestroyed() {
        return this.hasBeenDestroyed.get();
    }

    static void checkDestroyed(Destroyable destroyable) {
        if (destroyable.isDestroyed()) {
            throw new IllegalStateException("key has been destroyed");
        }
    }
}
