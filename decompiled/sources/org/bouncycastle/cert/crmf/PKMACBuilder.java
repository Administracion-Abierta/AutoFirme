package org.bouncycastle.cert.crmf;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.security.SecureRandom;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.cmp.CMPObjectIdentifiers;
import org.bouncycastle.asn1.cmp.PBMParameter;
import org.bouncycastle.asn1.iana.IANAObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.PBEMacCalculatorProvider;
import org.bouncycastle.operator.RuntimeOperatorException;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/crmf/PKMACBuilder.class */
public class PKMACBuilder implements PBEMacCalculatorProvider {
    private AlgorithmIdentifier owf;
    private int iterationCount;
    private AlgorithmIdentifier mac;
    private int saltLength;
    private SecureRandom random;
    private PKMACValuesCalculator calculator;
    private PBMParameter parameters;
    private int maxIterations;

    public PKMACBuilder(PKMACValuesCalculator pKMACValuesCalculator) {
        this(new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1), 1000, new AlgorithmIdentifier(IANAObjectIdentifiers.hmacSHA1, DERNull.INSTANCE), pKMACValuesCalculator);
    }

    public PKMACBuilder(PKMACValuesCalculator pKMACValuesCalculator, int i) {
        this.saltLength = 20;
        this.maxIterations = i;
        this.calculator = pKMACValuesCalculator;
    }

    private PKMACBuilder(AlgorithmIdentifier algorithmIdentifier, int i, AlgorithmIdentifier algorithmIdentifier2, PKMACValuesCalculator pKMACValuesCalculator) {
        this.saltLength = 20;
        this.owf = algorithmIdentifier;
        this.iterationCount = i;
        this.mac = algorithmIdentifier2;
        this.calculator = pKMACValuesCalculator;
    }

    public PKMACBuilder setSaltLength(int i) {
        if (i < 8) {
            throw new IllegalArgumentException("salt length must be at least 8 bytes");
        }
        this.saltLength = i;
        return this;
    }

    public PKMACBuilder setIterationCount(int i) {
        if (i < 100) {
            throw new IllegalArgumentException("iteration count must be at least 100");
        }
        checkIterationCountCeiling(i);
        this.iterationCount = i;
        return this;
    }

    public PKMACBuilder setSecureRandom(SecureRandom secureRandom) {
        this.random = secureRandom;
        return this;
    }

    public PKMACBuilder setParameters(PBMParameter pBMParameter) {
        checkIterationCountCeiling(pBMParameter.getIterationCount().intValueExact());
        this.parameters = pBMParameter;
        return this;
    }

    @Override // org.bouncycastle.operator.PBEMacCalculatorProvider
    public MacCalculator get(AlgorithmIdentifier algorithmIdentifier, char[] cArr) throws OperatorCreationException {
        if (!CMPObjectIdentifiers.passwordBasedMac.equals((ASN1Primitive) algorithmIdentifier.getAlgorithm())) {
            throw new OperatorCreationException("protection algorithm not mac based");
        }
        setParameters(PBMParameter.getInstance(algorithmIdentifier.getParameters()));
        try {
            return build(cArr);
        } catch (CRMFException e) {
            throw new OperatorCreationException(e.getMessage(), e.getCause());
        }
    }

    public MacCalculator build(char[] cArr) throws CRMFException {
        PBMParameter pBMParameterGenParameters = this.parameters;
        if (pBMParameterGenParameters == null) {
            pBMParameterGenParameters = genParameters();
        }
        return genCalculator(pBMParameterGenParameters, cArr);
    }

    private void checkIterationCountCeiling(int i) {
        if (this.maxIterations > 0 && i > this.maxIterations) {
            throw new IllegalArgumentException("iteration count exceeds limit (" + i + " > " + this.maxIterations + ")");
        }
    }

    private MacCalculator genCalculator(final PBMParameter pBMParameter, char[] cArr) throws CRMFException {
        byte[] uTF8ByteArray = Strings.toUTF8ByteArray(cArr);
        byte[] octets = pBMParameter.getSalt().getOctets();
        final byte[] bArrCalculateDigest = new byte[uTF8ByteArray.length + octets.length];
        System.arraycopy(uTF8ByteArray, 0, bArrCalculateDigest, 0, uTF8ByteArray.length);
        System.arraycopy(octets, 0, bArrCalculateDigest, uTF8ByteArray.length, octets.length);
        this.calculator.setup(pBMParameter.getOwf(), pBMParameter.getMac());
        int iIntValueExact = pBMParameter.getIterationCount().intValueExact();
        do {
            bArrCalculateDigest = this.calculator.calculateDigest(bArrCalculateDigest);
            iIntValueExact--;
        } while (iIntValueExact > 0);
        return new MacCalculator() { // from class: org.bouncycastle.cert.crmf.PKMACBuilder.1
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();

            @Override // org.bouncycastle.operator.MacCalculator
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return new AlgorithmIdentifier(CMPObjectIdentifiers.passwordBasedMac, pBMParameter);
            }

            @Override // org.bouncycastle.operator.MacCalculator
            public GenericKey getKey() {
                return new GenericKey(getAlgorithmIdentifier(), bArrCalculateDigest);
            }

            @Override // org.bouncycastle.operator.MacCalculator
            public OutputStream getOutputStream() {
                return this.bOut;
            }

            @Override // org.bouncycastle.operator.MacCalculator
            public byte[] getMac() {
                try {
                    return PKMACBuilder.this.calculator.calculateMac(bArrCalculateDigest, this.bOut.toByteArray());
                } catch (CRMFException e) {
                    throw new RuntimeOperatorException("exception calculating mac: " + e.getMessage(), e);
                }
            }
        };
    }

    private PBMParameter genParameters() {
        byte[] bArr = new byte[this.saltLength];
        if (this.random == null) {
            this.random = new SecureRandom();
        }
        this.random.nextBytes(bArr);
        return new PBMParameter(bArr, this.owf, this.iterationCount, this.mac);
    }
}
