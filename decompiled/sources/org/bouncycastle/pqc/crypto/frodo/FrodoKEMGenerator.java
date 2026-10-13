package org.bouncycastle.pqc.crypto.frodo;

import java.security.SecureRandom;
import org.bouncycastle.crypto.EncapsulatedSecretGenerator;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.util.SecretWithEncapsulationImpl;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/frodo/FrodoKEMGenerator.class */
public class FrodoKEMGenerator implements EncapsulatedSecretGenerator {
    private final SecureRandom sr;

    public FrodoKEMGenerator(SecureRandom secureRandom) {
        this.sr = secureRandom;
    }

    @Override // org.bouncycastle.crypto.EncapsulatedSecretGenerator
    public SecretWithEncapsulation generateEncapsulated(AsymmetricKeyParameter asymmetricKeyParameter) {
        FrodoPublicKeyParameters frodoPublicKeyParameters = (FrodoPublicKeyParameters) asymmetricKeyParameter;
        FrodoEngine engine = frodoPublicKeyParameters.getParameters().getEngine();
        byte[] bArr = new byte[engine.getCipherTextSize()];
        byte[] bArr2 = new byte[engine.getSessionKeySize()];
        engine.kem_enc(bArr, bArr2, frodoPublicKeyParameters.getPublicKey(), this.sr);
        return new SecretWithEncapsulationImpl(bArr2, bArr);
    }
}
