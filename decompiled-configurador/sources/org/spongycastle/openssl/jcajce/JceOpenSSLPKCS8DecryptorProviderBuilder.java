package org.spongycastle.openssl.jcajce;

import java.io.IOException;
import java.io.InputStream;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.Provider;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import org.spongycastle.asn1.pkcs.EncryptionScheme;
import org.spongycastle.asn1.pkcs.KeyDerivationFunc;
import org.spongycastle.asn1.pkcs.PBEParameter;
import org.spongycastle.asn1.pkcs.PBES2Parameters;
import org.spongycastle.asn1.pkcs.PBKDF2Params;
import org.spongycastle.asn1.pkcs.PKCS12PBEParams;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.crypto.CharToByteConverter;
import org.spongycastle.jcajce.PBKDF1KeyWithParameters;
import org.spongycastle.jcajce.PKCS12KeyWithParameters;
import org.spongycastle.jcajce.util.DefaultJcaJceHelper;
import org.spongycastle.jcajce.util.JcaJceHelper;
import org.spongycastle.jcajce.util.NamedJcaJceHelper;
import org.spongycastle.jcajce.util.ProviderJcaJceHelper;
import org.spongycastle.openssl.PEMException;
import org.spongycastle.operator.InputDecryptor;
import org.spongycastle.operator.InputDecryptorProvider;
import org.spongycastle.operator.OperatorCreationException;
import org.spongycastle.util.Strings;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/openssl/jcajce/JceOpenSSLPKCS8DecryptorProviderBuilder.class */
public class JceOpenSSLPKCS8DecryptorProviderBuilder {
    private JcaJceHelper helper;

    public JceOpenSSLPKCS8DecryptorProviderBuilder() {
        this.helper = new DefaultJcaJceHelper();
        this.helper = new DefaultJcaJceHelper();
    }

    public JceOpenSSLPKCS8DecryptorProviderBuilder setProvider(String providerName) {
        this.helper = new NamedJcaJceHelper(providerName);
        return this;
    }

    public JceOpenSSLPKCS8DecryptorProviderBuilder setProvider(Provider provider) {
        this.helper = new ProviderJcaJceHelper(provider);
        return this;
    }

    public InputDecryptorProvider build(final char[] password) throws OperatorCreationException {
        return new InputDecryptorProvider() { // from class: org.spongycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder.1
            @Override // org.spongycastle.operator.InputDecryptorProvider
            public InputDecryptor get(final AlgorithmIdentifier algorithm) throws OperatorCreationException {
                Cipher cipher;
                try {
                    if (PEMUtilities.isPKCS5Scheme2(algorithm.getAlgorithm())) {
                        PBES2Parameters params = PBES2Parameters.getInstance(algorithm.getParameters());
                        KeyDerivationFunc func = params.getKeyDerivationFunc();
                        EncryptionScheme scheme = params.getEncryptionScheme();
                        PBKDF2Params defParams = (PBKDF2Params) func.getParameters();
                        int iterationCount = defParams.getIterationCount().intValue();
                        byte[] salt = defParams.getSalt();
                        String oid = scheme.getAlgorithm().getId();
                        SecretKey key = PEMUtilities.isHmacSHA1(defParams.getPrf()) ? PEMUtilities.generateSecretKeyForPKCS5Scheme2(JceOpenSSLPKCS8DecryptorProviderBuilder.this.helper, oid, password, salt, iterationCount) : PEMUtilities.generateSecretKeyForPKCS5Scheme2(JceOpenSSLPKCS8DecryptorProviderBuilder.this.helper, oid, password, salt, iterationCount, defParams.getPrf());
                        cipher = JceOpenSSLPKCS8DecryptorProviderBuilder.this.helper.createCipher(oid);
                        AlgorithmParameters algParams = JceOpenSSLPKCS8DecryptorProviderBuilder.this.helper.createAlgorithmParameters(oid);
                        algParams.init(scheme.getParameters().toASN1Primitive().getEncoded());
                        cipher.init(2, key, algParams);
                    } else if (PEMUtilities.isPKCS12(algorithm.getAlgorithm())) {
                        PKCS12PBEParams params2 = PKCS12PBEParams.getInstance(algorithm.getParameters());
                        cipher = JceOpenSSLPKCS8DecryptorProviderBuilder.this.helper.createCipher(algorithm.getAlgorithm().getId());
                        cipher.init(2, new PKCS12KeyWithParameters(password, params2.getIV(), params2.getIterations().intValue()));
                    } else if (PEMUtilities.isPKCS5Scheme1(algorithm.getAlgorithm())) {
                        PBEParameter params3 = PBEParameter.getInstance(algorithm.getParameters());
                        cipher = JceOpenSSLPKCS8DecryptorProviderBuilder.this.helper.createCipher(algorithm.getAlgorithm().getId());
                        cipher.init(2, new PBKDF1KeyWithParameters(password, new CharToByteConverter() { // from class: org.spongycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder.1.1
                            @Override // org.spongycastle.crypto.CharToByteConverter
                            public String getType() {
                                return "ASCII";
                            }

                            @Override // org.spongycastle.crypto.CharToByteConverter
                            public byte[] convert(char[] password2) {
                                return Strings.toByteArray(password2);
                            }
                        }, params3.getSalt(), params3.getIterationCount().intValue()));
                    } else {
                        throw new PEMException("Unknown algorithm: " + algorithm.getAlgorithm());
                    }
                    final Cipher cipher2 = cipher;
                    return new InputDecryptor() { // from class: org.spongycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder.1.2
                        @Override // org.spongycastle.operator.InputDecryptor
                        public AlgorithmIdentifier getAlgorithmIdentifier() {
                            return algorithm;
                        }

                        @Override // org.spongycastle.operator.InputDecryptor
                        public InputStream getInputStream(InputStream encIn) {
                            return new CipherInputStream(encIn, cipher2);
                        }
                    };
                } catch (IOException e) {
                    throw new OperatorCreationException(algorithm.getAlgorithm() + " not available: " + e.getMessage(), e);
                } catch (GeneralSecurityException e2) {
                    throw new OperatorCreationException(algorithm.getAlgorithm() + " not available: " + e2.getMessage(), e2);
                }
            }
        };
    }
}
