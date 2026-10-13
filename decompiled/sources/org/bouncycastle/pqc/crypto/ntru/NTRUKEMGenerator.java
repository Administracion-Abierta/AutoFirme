package org.bouncycastle.pqc.crypto.ntru;

import java.security.SecureRandom;
import org.bouncycastle.crypto.EncapsulatedSecretGenerator;
import org.bouncycastle.crypto.SecretWithEncapsulation;
import org.bouncycastle.crypto.digests.SHA3Digest;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.pqc.crypto.util.SecretWithEncapsulationImpl;
import org.bouncycastle.pqc.math.ntru.Polynomial;
import org.bouncycastle.pqc.math.ntru.parameters.NTRUParameterSet;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/ntru/NTRUKEMGenerator.class */
public class NTRUKEMGenerator implements EncapsulatedSecretGenerator {
    private final SecureRandom random;

    public NTRUKEMGenerator(SecureRandom secureRandom) {
        if (secureRandom == null) {
            throw new NullPointerException("'random' cannot be null");
        }
        this.random = secureRandom;
    }

    @Override // org.bouncycastle.crypto.EncapsulatedSecretGenerator
    public SecretWithEncapsulation generateEncapsulated(AsymmetricKeyParameter asymmetricKeyParameter) {
        if (asymmetricKeyParameter == null) {
            throw new NullPointerException("'recipientKey' cannot be null");
        }
        NTRUPublicKeyParameters nTRUPublicKeyParameters = (NTRUPublicKeyParameters) asymmetricKeyParameter;
        NTRUParameterSet parameterSet = nTRUPublicKeyParameters.getParameters().getParameterSet();
        NTRUSampling nTRUSampling = new NTRUSampling(parameterSet);
        NTRUOWCPA ntruowcpa = new NTRUOWCPA(parameterSet);
        byte[] bArr = new byte[parameterSet.owcpaMsgBytes()];
        byte[] bArr2 = new byte[parameterSet.sampleRmBytes()];
        this.random.nextBytes(bArr2);
        PolynomialPair polynomialPairSampleRm = nTRUSampling.sampleRm(bArr2);
        Polynomial polynomialR = polynomialPairSampleRm.r();
        Polynomial polynomialM = polynomialPairSampleRm.m();
        polynomialR.s3ToBytes(bArr, 0);
        polynomialM.s3ToBytes(bArr, parameterSet.packTrinaryBytes());
        SHA3Digest sHA3Digest = new SHA3Digest(256);
        byte[] bArr3 = new byte[sHA3Digest.getDigestSize()];
        sHA3Digest.update(bArr, 0, bArr.length);
        sHA3Digest.doFinal(bArr3, 0);
        polynomialR.z3ToZq();
        byte[] bArrEncrypt = ntruowcpa.encrypt(polynomialR, polynomialM, nTRUPublicKeyParameters.publicKey);
        byte[] bArrCopyOfRange = Arrays.copyOfRange(bArr3, 0, parameterSet.sharedKeyBytes());
        Arrays.clear(bArr3);
        return new SecretWithEncapsulationImpl(bArrCopyOfRange, bArrEncrypt);
    }
}
