package org.bouncycastle.openssl.jcajce;

import java.io.IOException;
import java.io.InputStream;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.Provider;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.pkcs.EncryptionScheme;
import org.bouncycastle.asn1.pkcs.KeyDerivationFunc;
import org.bouncycastle.asn1.pkcs.PBEParameter;
import org.bouncycastle.asn1.pkcs.PBES2Parameters;
import org.bouncycastle.asn1.pkcs.PBKDF2Params;
import org.bouncycastle.asn1.pkcs.PKCS12PBEParams;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.crypto.CharToByteConverter;
import org.bouncycastle.jcajce.PBKDF1KeyWithParameters;
import org.bouncycastle.jcajce.PKCS12KeyWithParameters;
import org.bouncycastle.jcajce.io.CipherInputStream;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.operator.InputDecryptor;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/openssl/jcajce/JceOpenSSLPKCS8DecryptorProviderBuilder.class */
public class JceOpenSSLPKCS8DecryptorProviderBuilder {
    private JcaJceHelper helper = new DefaultJcaJceHelper();

    public JceOpenSSLPKCS8DecryptorProviderBuilder setProvider(String str) {
        this.helper = new NamedJcaJceHelper(str);
        return this;
    }

    public JceOpenSSLPKCS8DecryptorProviderBuilder setProvider(Provider provider) {
        this.helper = new ProviderJcaJceHelper(provider);
        return this;
    }

    public InputDecryptorProvider build(final char[] cArr) throws OperatorCreationException {
        return new InputDecryptorProvider() { // from class: org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder.1
            @Override // org.bouncycastle.operator.InputDecryptorProvider
            public InputDecryptor get(final AlgorithmIdentifier algorithmIdentifier) throws OperatorCreationException {
                Cipher cipherCreateCipher;
                try {
                    if (PEMUtilities.isPKCS5Scheme2(algorithmIdentifier.getAlgorithm())) {
                        PBES2Parameters pBES2Parameters = PBES2Parameters.getInstance(algorithmIdentifier.getParameters());
                        KeyDerivationFunc keyDerivationFunc = pBES2Parameters.getKeyDerivationFunc();
                        EncryptionScheme encryptionScheme = pBES2Parameters.getEncryptionScheme();
                        PBKDF2Params pBKDF2Params = (PBKDF2Params) keyDerivationFunc.getParameters();
                        int iIntValue = pBKDF2Params.getIterationCount().intValue();
                        byte[] salt = pBKDF2Params.getSalt();
                        String id = encryptionScheme.getAlgorithm().getId();
                        SecretKey secretKeyGenerateSecretKeyForPKCS5Scheme2 = PEMUtilities.isHmacSHA1(pBKDF2Params.getPrf()) ? PEMUtilities.generateSecretKeyForPKCS5Scheme2(JceOpenSSLPKCS8DecryptorProviderBuilder.this.helper, id, cArr, salt, iIntValue) : PEMUtilities.generateSecretKeyForPKCS5Scheme2(JceOpenSSLPKCS8DecryptorProviderBuilder.this.helper, id, cArr, salt, iIntValue, pBKDF2Params.getPrf());
                        cipherCreateCipher = JceOpenSSLPKCS8DecryptorProviderBuilder.this.helper.createCipher(PEMUtilities.getCipherName(encryptionScheme.getAlgorithm()));
                        AlgorithmParameters algorithmParametersCreateAlgorithmParameters = JceOpenSSLPKCS8DecryptorProviderBuilder.this.helper.createAlgorithmParameters(id);
                        algorithmParametersCreateAlgorithmParameters.init(encryptionScheme.getParameters().toASN1Primitive().getEncoded());
                        cipherCreateCipher.init(2, secretKeyGenerateSecretKeyForPKCS5Scheme2, algorithmParametersCreateAlgorithmParameters);
                    } else if (PEMUtilities.isPKCS12(algorithmIdentifier.getAlgorithm())) {
                        PKCS12PBEParams pKCS12PBEParams = PKCS12PBEParams.getInstance(algorithmIdentifier.getParameters());
                        cipherCreateCipher = JceOpenSSLPKCS8DecryptorProviderBuilder.this.helper.createCipher(PEMUtilities.getCipherName(algorithmIdentifier.getAlgorithm()));
                        cipherCreateCipher.init(2, new PKCS12KeyWithParameters(cArr, pKCS12PBEParams.getIV(), pKCS12PBEParams.getIterations().intValue()));
                    } else {
                        if (!PEMUtilities.isPKCS5Scheme1(algorithmIdentifier.getAlgorithm())) {
                            throw new PEMException("Unknown algorithm: " + algorithmIdentifier.getAlgorithm());
                        }
                        PBEParameter pBEParameter = PBEParameter.getInstance(algorithmIdentifier.getParameters());
                        cipherCreateCipher = JceOpenSSLPKCS8DecryptorProviderBuilder.this.helper.createCipher(PEMUtilities.getCipherName(algorithmIdentifier.getAlgorithm()));
                        cipherCreateCipher.init(2, new PBKDF1KeyWithParameters(cArr, new CharToByteConverter() { // from class: org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder.1.1
                            @Override // org.bouncycastle.crypto.CharToByteConverter
                            public String getType() {
                                return "ASCII";
                            }

                            @Override // org.bouncycastle.crypto.CharToByteConverter
                            public byte[] convert(char[] cArr2) {
                                return Strings.toByteArray(cArr2);
                            }
                        }, pBEParameter.getSalt(), pBEParameter.getIterationCount().intValue()));
                    }
                    final Cipher cipher = cipherCreateCipher;
                    return new InputDecryptor() { // from class: org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder.1.2
                        @Override // org.bouncycastle.operator.InputDecryptor
                        public AlgorithmIdentifier getAlgorithmIdentifier() {
                            return algorithmIdentifier;
                        }

                        @Override // org.bouncycastle.operator.InputDecryptor
                        public InputStream getInputStream(InputStream inputStream) {
                            return new CipherInputStream(inputStream, cipher);
                        }
                    };
                } catch (IOException e) {
                    throw new OperatorCreationException(algorithmIdentifier.getAlgorithm() + " not available: " + e.getMessage(), e);
                } catch (GeneralSecurityException e2) {
                    throw new OperatorCreationException(algorithmIdentifier.getAlgorithm() + " not available: " + e2.getMessage(), e2);
                }
            }
        };
    }
}
