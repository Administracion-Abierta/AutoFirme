package org.bouncycastle.pkcs.jcajce;

import java.io.OutputStream;
import java.security.Provider;
import java.security.SecureRandom;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PBMAC1Params;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.jcajce.io.MacOutputStream;
import org.bouncycastle.jcajce.spec.PBKDF2KeySpec;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.DefaultMacAlgorithmIdentifierFinder;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.MacAlgorithmIdentifierFinder;
import org.bouncycastle.operator.MacCalculator;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.BigIntegers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pkcs/jcajce/JcePBMac1CalculatorBuilder.class */
public class JcePBMac1CalculatorBuilder {
    public static final AlgorithmIdentifier PRF_SHA224 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA224, DERNull.INSTANCE);
    public static final AlgorithmIdentifier PRF_SHA256 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA256, DERNull.INSTANCE);
    public static final AlgorithmIdentifier PRF_SHA384 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA384, DERNull.INSTANCE);
    public static final AlgorithmIdentifier PRF_SHA512 = new AlgorithmIdentifier(PKCSObjectIdentifiers.id_hmacWithSHA512, DERNull.INSTANCE);
    public static final AlgorithmIdentifier PRF_SHA3_224 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_hmacWithSHA3_224);
    public static final AlgorithmIdentifier PRF_SHA3_256 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_hmacWithSHA3_256);
    public static final AlgorithmIdentifier PRF_SHA3_384 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_hmacWithSHA3_384);
    public static final AlgorithmIdentifier PRF_SHA3_512 = new AlgorithmIdentifier(NISTObjectIdentifiers.id_hmacWithSHA3_512);
    private static final DefaultMacAlgorithmIdentifierFinder defaultFinder = new DefaultMacAlgorithmIdentifierFinder();
    private JcaJceHelper helper;
    private AlgorithmIdentifier macAlgorithm;
    private SecureRandom random;
    private int saltLength;
    private int iterationCount;
    private int keySize;
    private PBKDF2Params pbeParams;
    private AlgorithmIdentifier prf;
    private byte[] salt;

    public JcePBMac1CalculatorBuilder(String str, int i) {
        this(str, i, defaultFinder);
    }

    public JcePBMac1CalculatorBuilder(String str, int i, MacAlgorithmIdentifierFinder macAlgorithmIdentifierFinder) {
        this.helper = new DefaultJcaJceHelper();
        this.saltLength = -1;
        this.iterationCount = 8192;
        this.pbeParams = null;
        this.prf = PRF_SHA256;
        this.salt = null;
        this.macAlgorithm = macAlgorithmIdentifierFinder.find(str);
        this.keySize = i;
    }

    public JcePBMac1CalculatorBuilder(PBMAC1Params pBMAC1Params) {
        this.helper = new DefaultJcaJceHelper();
        this.saltLength = -1;
        this.iterationCount = 8192;
        this.pbeParams = null;
        this.prf = PRF_SHA256;
        this.salt = null;
        this.macAlgorithm = pBMAC1Params.getMessageAuthScheme();
        this.pbeParams = PBKDF2Params.getInstance(pBMAC1Params.getKeyDerivationFunc().getParameters());
    }

    public JcePBMac1CalculatorBuilder setProvider(Provider provider) {
        this.helper = new ProviderJcaJceHelper(provider);
        return this;
    }

    public JcePBMac1CalculatorBuilder setProvider(String str) {
        this.helper = new NamedJcaJceHelper(str);
        return this;
    }

    JcePBMac1CalculatorBuilder setHelper(JcaJceHelper jcaJceHelper) {
        this.helper = jcaJceHelper;
        return this;
    }

    public JcePBMac1CalculatorBuilder setIterationCount(int i) {
        this.iterationCount = i;
        return this;
    }

    public JcePBMac1CalculatorBuilder setSaltLength(int i) {
        this.saltLength = i;
        return this;
    }

    public JcePBMac1CalculatorBuilder setSalt(byte[] bArr) {
        this.salt = bArr;
        return this;
    }

    public JcePBMac1CalculatorBuilder setRandom(SecureRandom secureRandom) {
        this.random = secureRandom;
        return this;
    }

    public JcePBMac1CalculatorBuilder setPrf(AlgorithmIdentifier algorithmIdentifier) {
        this.prf = algorithmIdentifier;
        return this;
    }

    public MacCalculator build(char[] cArr) throws OperatorCreationException {
        if (this.random == null) {
            this.random = new SecureRandom();
        }
        try {
            final Mac macCreateMac = this.helper.createMac(this.macAlgorithm.getAlgorithm().getId());
            if (this.pbeParams != null) {
                this.salt = this.pbeParams.getSalt();
                this.iterationCount = BigIntegers.intValueExact(this.pbeParams.getIterationCount());
                this.keySize = BigIntegers.intValueExact(this.pbeParams.getKeyLength()) * 8;
            } else if (this.salt == null) {
                if (this.saltLength < 0) {
                    this.saltLength = macCreateMac.getMacLength();
                }
                this.salt = new byte[this.saltLength];
                this.random.nextBytes(this.salt);
            }
            final SecretKey secretKeyGenerateSecret = this.helper.createSecretKeyFactory("PBKDF2").generateSecret(new PBKDF2KeySpec(cArr, this.salt, this.iterationCount, this.keySize, this.prf));
            macCreateMac.init(secretKeyGenerateSecret);
            return new MacCalculator() { // from class: org.bouncycastle.pkcs.jcajce.JcePBMac1CalculatorBuilder.1
                @Override // org.bouncycastle.operator.MacCalculator
                public AlgorithmIdentifier getAlgorithmIdentifier() {
                    return new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBMAC1, new PBMAC1Params(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_PBES2, new PBKDF2Params(JcePBMac1CalculatorBuilder.this.salt, JcePBMac1CalculatorBuilder.this.iterationCount, (JcePBMac1CalculatorBuilder.this.keySize + 7) / 8, JcePBMac1CalculatorBuilder.this.prf)), JcePBMac1CalculatorBuilder.this.macAlgorithm));
                }

                @Override // org.bouncycastle.operator.MacCalculator
                public OutputStream getOutputStream() {
                    return new MacOutputStream(macCreateMac);
                }

                @Override // org.bouncycastle.operator.MacCalculator
                public byte[] getMac() {
                    return macCreateMac.doFinal();
                }

                @Override // org.bouncycastle.operator.MacCalculator
                public GenericKey getKey() {
                    return new GenericKey(getAlgorithmIdentifier(), secretKeyGenerateSecret.getEncoded());
                }
            };
        } catch (Exception e) {
            throw new OperatorCreationException("unable to create MAC calculator: " + e.getMessage(), e);
        }
    }
}
