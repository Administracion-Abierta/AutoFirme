package org.spongycastle.pkcs.jcajce;

import java.io.InputStream;
import java.security.Provider;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.cryptopro.GOST28147Parameters;
import org.spongycastle.asn1.pkcs.PBEParameter;
import org.spongycastle.asn1.pkcs.PBES2Parameters;
import org.spongycastle.asn1.pkcs.PBKDF2Params;
import org.spongycastle.asn1.pkcs.PKCS12PBEParams;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.crypto.PasswordConverter;
import org.spongycastle.jcajce.PBKDF1Key;
import org.spongycastle.jcajce.PKCS12KeyWithParameters;
import org.spongycastle.jcajce.spec.GOST28147ParameterSpec;
import org.spongycastle.jcajce.spec.PBKDF2KeySpec;
import org.spongycastle.jcajce.util.DefaultJcaJceHelper;
import org.spongycastle.jcajce.util.JcaJceHelper;
import org.spongycastle.jcajce.util.NamedJcaJceHelper;
import org.spongycastle.jcajce.util.ProviderJcaJceHelper;
import org.spongycastle.operator.DefaultSecretKeySizeProvider;
import org.spongycastle.operator.InputDecryptor;
import org.spongycastle.operator.InputDecryptorProvider;
import org.spongycastle.operator.OperatorCreationException;
import org.spongycastle.operator.SecretKeySizeProvider;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pkcs/jcajce/JcePKCSPBEInputDecryptorProviderBuilder.class */
public class JcePKCSPBEInputDecryptorProviderBuilder {
    private JcaJceHelper helper = new DefaultJcaJceHelper();
    private boolean wrongPKCS12Zero = false;
    private SecretKeySizeProvider keySizeProvider = DefaultSecretKeySizeProvider.INSTANCE;

    public JcePKCSPBEInputDecryptorProviderBuilder setProvider(Provider provider) {
        this.helper = new ProviderJcaJceHelper(provider);
        return this;
    }

    public JcePKCSPBEInputDecryptorProviderBuilder setProvider(String providerName) {
        this.helper = new NamedJcaJceHelper(providerName);
        return this;
    }

    public JcePKCSPBEInputDecryptorProviderBuilder setTryWrongPKCS12Zero(boolean tryWrong) {
        this.wrongPKCS12Zero = tryWrong;
        return this;
    }

    public JcePKCSPBEInputDecryptorProviderBuilder setKeySizeProvider(SecretKeySizeProvider keySizeProvider) {
        this.keySizeProvider = keySizeProvider;
        return this;
    }

    public InputDecryptorProvider build(final char[] password) {
        return new InputDecryptorProvider() { // from class: org.spongycastle.pkcs.jcajce.JcePKCSPBEInputDecryptorProviderBuilder.1
            private Cipher cipher;
            private AlgorithmIdentifier encryptionAlg;

            @Override // org.spongycastle.operator.InputDecryptorProvider
            public InputDecryptor get(AlgorithmIdentifier algorithmIdentifier) throws OperatorCreationException {
                SecretKey key;
                ASN1ObjectIdentifier algorithm = algorithmIdentifier.getAlgorithm();
                try {
                    if (algorithm.on(PKCSObjectIdentifiers.pkcs_12PbeIds)) {
                        PKCS12PBEParams pbeParams = PKCS12PBEParams.getInstance(algorithmIdentifier.getParameters());
                        this.cipher = JcePKCSPBEInputDecryptorProviderBuilder.this.helper.createCipher(algorithm.getId());
                        this.cipher.init(2, new PKCS12KeyWithParameters(password, JcePKCSPBEInputDecryptorProviderBuilder.this.wrongPKCS12Zero, pbeParams.getIV(), pbeParams.getIterations().intValue()));
                        this.encryptionAlg = algorithmIdentifier;
                    } else if (algorithm.equals(PKCSObjectIdentifiers.id_PBES2)) {
                        PBES2Parameters alg = PBES2Parameters.getInstance(algorithmIdentifier.getParameters());
                        PBKDF2Params func = PBKDF2Params.getInstance(alg.getKeyDerivationFunc().getParameters());
                        AlgorithmIdentifier encScheme = AlgorithmIdentifier.getInstance(alg.getEncryptionScheme());
                        SecretKeyFactory keyFact = JcePKCSPBEInputDecryptorProviderBuilder.this.helper.createSecretKeyFactory(alg.getKeyDerivationFunc().getAlgorithm().getId());
                        if (func.isDefaultPrf()) {
                            key = keyFact.generateSecret(new PBEKeySpec(password, func.getSalt(), func.getIterationCount().intValue(), JcePKCSPBEInputDecryptorProviderBuilder.this.keySizeProvider.getKeySize(encScheme)));
                        } else {
                            key = keyFact.generateSecret(new PBKDF2KeySpec(password, func.getSalt(), func.getIterationCount().intValue(), JcePKCSPBEInputDecryptorProviderBuilder.this.keySizeProvider.getKeySize(encScheme), func.getPrf()));
                        }
                        this.cipher = JcePKCSPBEInputDecryptorProviderBuilder.this.helper.createCipher(alg.getEncryptionScheme().getAlgorithm().getId());
                        this.encryptionAlg = AlgorithmIdentifier.getInstance(alg.getEncryptionScheme());
                        ASN1Encodable encParams = alg.getEncryptionScheme().getParameters();
                        if (encParams instanceof ASN1OctetString) {
                            this.cipher.init(2, key, new IvParameterSpec(ASN1OctetString.getInstance(encParams).getOctets()));
                        } else {
                            GOST28147Parameters gParams = GOST28147Parameters.getInstance(encParams);
                            this.cipher.init(2, key, new GOST28147ParameterSpec(gParams.getEncryptionParamSet(), gParams.getIV()));
                        }
                    } else if (algorithm.equals(PKCSObjectIdentifiers.pbeWithMD5AndDES_CBC) || algorithm.equals(PKCSObjectIdentifiers.pbeWithSHA1AndDES_CBC)) {
                        PBEParameter pbeParams2 = PBEParameter.getInstance(algorithmIdentifier.getParameters());
                        this.cipher = JcePKCSPBEInputDecryptorProviderBuilder.this.helper.createCipher(algorithm.getId());
                        this.cipher.init(2, new PBKDF1Key(password, PasswordConverter.ASCII), new PBEParameterSpec(pbeParams2.getSalt(), pbeParams2.getIterationCount().intValue()));
                    } else {
                        throw new OperatorCreationException("unable to create InputDecryptor: algorithm " + algorithm + " unknown.");
                    }
                    return new InputDecryptor() { // from class: org.spongycastle.pkcs.jcajce.JcePKCSPBEInputDecryptorProviderBuilder.1.1
                        @Override // org.spongycastle.operator.InputDecryptor
                        public AlgorithmIdentifier getAlgorithmIdentifier() {
                            return AnonymousClass1.this.encryptionAlg;
                        }

                        @Override // org.spongycastle.operator.InputDecryptor
                        public InputStream getInputStream(InputStream input) {
                            return new CipherInputStream(input, AnonymousClass1.this.cipher);
                        }
                    };
                } catch (Exception e) {
                    throw new OperatorCreationException("unable to create InputDecryptor: " + e.getMessage(), e);
                }
            }
        };
    }
}
