package org.bouncycastle.crypto.kems;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DerivationFunction;
import org.bouncycastle.crypto.KeyEncapsulation;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.crypto.constraints.ConstraintUtils;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.ECKeyParameters;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.params.ECPublicKeyParameters;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/kems/ECIESKeyEncapsulation.class */
public class ECIESKeyEncapsulation implements KeyEncapsulation {
    private DerivationFunction kdf;
    private SecureRandom rnd;
    private ECKeyParameters key;
    private boolean CofactorMode;
    private boolean OldCofactorMode;
    private boolean SingleHashMode;

    public ECIESKeyEncapsulation(DerivationFunction derivationFunction, SecureRandom secureRandom) {
        this.kdf = derivationFunction;
        this.rnd = secureRandom;
        this.CofactorMode = false;
        this.OldCofactorMode = false;
        this.SingleHashMode = false;
    }

    public ECIESKeyEncapsulation(DerivationFunction derivationFunction, SecureRandom secureRandom, boolean z, boolean z2, boolean z3) {
        this.kdf = derivationFunction;
        this.rnd = secureRandom;
        this.CofactorMode = z;
        if (z) {
            this.OldCofactorMode = false;
        } else {
            this.OldCofactorMode = z2;
        }
        this.SingleHashMode = z3;
    }

    @Override // org.bouncycastle.crypto.KeyEncapsulation
    public void init(CipherParameters cipherParameters) throws IllegalArgumentException {
        if (!(cipherParameters instanceof ECKeyParameters)) {
            throw new IllegalArgumentException("EC key required");
        }
        this.key = (ECKeyParameters) cipherParameters;
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties("ECIESKem", ConstraintUtils.bitsOfSecurityFor(this.key.getParameters().getCurve()), cipherParameters, CryptoServicePurpose.ANY));
    }

    @Override // org.bouncycastle.crypto.KeyEncapsulation
    public CipherParameters encrypt(byte[] bArr, int i, int i2) throws IllegalArgumentException {
        if (!(this.key instanceof ECPublicKeyParameters)) {
            throw new IllegalArgumentException("Public key required for encryption");
        }
        SecretWithEncapsulation secretWithEncapsulationGenerateEncapsulated = new ECIESKEMGenerator(i2, this.kdf, this.rnd, this.CofactorMode, this.OldCofactorMode, this.SingleHashMode).generateEncapsulated(this.key);
        byte[] encapsulation = secretWithEncapsulationGenerateEncapsulated.getEncapsulation();
        System.arraycopy(encapsulation, 0, bArr, i, encapsulation.length);
        return new KeyParameter(secretWithEncapsulationGenerateEncapsulated.getSecret());
    }

    public CipherParameters encrypt(byte[] bArr, int i) {
        return encrypt(bArr, 0, i);
    }

    @Override // org.bouncycastle.crypto.KeyEncapsulation
    public CipherParameters decrypt(byte[] bArr, int i, int i2, int i3) throws IllegalArgumentException {
        if (this.key instanceof ECPrivateKeyParameters) {
            return new KeyParameter(new ECIESKEMExtractor((ECPrivateKeyParameters) this.key, i3, this.kdf, this.CofactorMode, this.OldCofactorMode, this.SingleHashMode).extractSecret(Arrays.copyOfRange(bArr, i, i + i2)));
        }
        throw new IllegalArgumentException("Private key required for encryption");
    }

    public CipherParameters decrypt(byte[] bArr, int i) {
        return decrypt(bArr, 0, bArr.length, i);
    }
}
