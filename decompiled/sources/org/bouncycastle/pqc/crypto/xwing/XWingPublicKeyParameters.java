package org.bouncycastle.pqc.crypto.xwing;

import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.X25519PublicKeyParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPublicKeyParameters;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/xwing/XWingPublicKeyParameters.class */
public class XWingPublicKeyParameters extends XWingKeyParameters {
    private final MLKEMPublicKeyParameters kybPub;
    private final X25519PublicKeyParameters xdhPub;

    XWingPublicKeyParameters(AsymmetricKeyParameter asymmetricKeyParameter, AsymmetricKeyParameter asymmetricKeyParameter2) {
        super(false);
        this.kybPub = (MLKEMPublicKeyParameters) asymmetricKeyParameter;
        this.xdhPub = (X25519PublicKeyParameters) asymmetricKeyParameter2;
    }

    public XWingPublicKeyParameters(byte[] bArr) {
        super(false);
        this.kybPub = new MLKEMPublicKeyParameters(MLKEMParameters.ml_kem_768, Arrays.copyOfRange(bArr, 0, bArr.length - 32));
        this.xdhPub = new X25519PublicKeyParameters(bArr, bArr.length - 32);
    }

    MLKEMPublicKeyParameters getKyberPublicKey() {
        return this.kybPub;
    }

    X25519PublicKeyParameters getXDHPublicKey() {
        return this.xdhPub;
    }

    public byte[] getEncoded() {
        return Arrays.concatenate(this.kybPub.getEncoded(), this.xdhPub.getEncoded());
    }
}
