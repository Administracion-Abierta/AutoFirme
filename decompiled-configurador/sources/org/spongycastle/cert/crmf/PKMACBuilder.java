package org.spongycastle.cert.crmf;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import org.spongycastle.asn1.DERNull;
import org.spongycastle.asn1.cmp.CMPObjectIdentifiers;
import org.spongycastle.asn1.cmp.PBMParameter;
import org.spongycastle.asn1.iana.IANAObjectIdentifiers;
import org.spongycastle.asn1.oiw.OIWObjectIdentifiers;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.operator.GenericKey;
import org.spongycastle.operator.MacCalculator;
import org.spongycastle.operator.RuntimeOperatorException;
import org.spongycastle.util.Strings;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cert/crmf/PKMACBuilder.class */
public class PKMACBuilder {
    private AlgorithmIdentifier owf;
    private int iterationCount;
    private AlgorithmIdentifier mac;
    private int saltLength;
    private SecureRandom random;
    private PKMACValuesCalculator calculator;
    private PBMParameter parameters;
    private int maxIterations;

    public PKMACBuilder(PKMACValuesCalculator calculator) {
        this(new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1), 1000, new AlgorithmIdentifier(IANAObjectIdentifiers.hmacSHA1, DERNull.INSTANCE), calculator);
    }

    public PKMACBuilder(PKMACValuesCalculator calculator, int maxIterations) {
        this.saltLength = 20;
        this.maxIterations = maxIterations;
        this.calculator = calculator;
    }

    private PKMACBuilder(AlgorithmIdentifier hashAlgorithm, int iterationCount, AlgorithmIdentifier macAlgorithm, PKMACValuesCalculator calculator) {
        this.saltLength = 20;
        this.owf = hashAlgorithm;
        this.iterationCount = iterationCount;
        this.mac = macAlgorithm;
        this.calculator = calculator;
    }

    public PKMACBuilder setSaltLength(int saltLength) {
        if (saltLength < 8) {
            throw new IllegalArgumentException("salt length must be at least 8 bytes");
        }
        this.saltLength = saltLength;
        return this;
    }

    public PKMACBuilder setIterationCount(int iterationCount) {
        if (iterationCount < 100) {
            throw new IllegalArgumentException("iteration count must be at least 100");
        }
        checkIterationCountCeiling(iterationCount);
        this.iterationCount = iterationCount;
        return this;
    }

    public PKMACBuilder setSecureRandom(SecureRandom random) {
        this.random = random;
        return this;
    }

    public PKMACBuilder setParameters(PBMParameter parameters) {
        checkIterationCountCeiling(parameters.getIterationCount().getValue().intValue());
        this.parameters = parameters;
        return this;
    }

    public MacCalculator build(char[] password) throws CRMFException {
        if (this.parameters != null) {
            return genCalculator(this.parameters, password);
        }
        byte[] salt = new byte[this.saltLength];
        if (this.random == null) {
            this.random = new SecureRandom();
        }
        this.random.nextBytes(salt);
        return genCalculator(new PBMParameter(salt, this.owf, this.iterationCount, this.mac), password);
    }

    private void checkIterationCountCeiling(int iterationCount) {
        if (this.maxIterations > 0 && iterationCount > this.maxIterations) {
            throw new IllegalArgumentException("iteration count exceeds limit (" + iterationCount + " > " + this.maxIterations + ")");
        }
    }

    private MacCalculator genCalculator(final PBMParameter params, char[] password) throws CRMFException {
        byte[] pw = Strings.toUTF8ByteArray(password);
        byte[] salt = params.getSalt().getOctets();
        final byte[] K = new byte[pw.length + salt.length];
        System.arraycopy(pw, 0, K, 0, pw.length);
        System.arraycopy(salt, 0, K, pw.length, salt.length);
        this.calculator.setup(params.getOwf(), params.getMac());
        int iter = params.getIterationCount().getValue().intValue();
        do {
            K = this.calculator.calculateDigest(K);
            iter--;
        } while (iter > 0);
        return new MacCalculator() { // from class: org.spongycastle.cert.crmf.PKMACBuilder.1
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();

            @Override // org.spongycastle.operator.MacCalculator
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return new AlgorithmIdentifier(CMPObjectIdentifiers.passwordBasedMac, params);
            }

            @Override // org.spongycastle.operator.MacCalculator
            public GenericKey getKey() {
                return new GenericKey(getAlgorithmIdentifier(), K);
            }

            @Override // org.spongycastle.operator.MacCalculator
            public OutputStream getOutputStream() {
                return this.bOut;
            }

            @Override // org.spongycastle.operator.MacCalculator
            public byte[] getMac() {
                try {
                    return PKMACBuilder.this.calculator.calculateMac(K, this.bOut.toByteArray());
                } catch (CRMFException e) {
                    throw new RuntimeOperatorException("exception calculating mac: " + e.getMessage(), e);
                }
            }
        };
    }
}
