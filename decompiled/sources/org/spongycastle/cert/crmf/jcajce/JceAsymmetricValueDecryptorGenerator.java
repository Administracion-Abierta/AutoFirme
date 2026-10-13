package org.spongycastle.cert.crmf.jcajce;

import java.io.InputStream;
import java.security.Key;
import java.security.PrivateKey;
import java.security.Provider;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.cert.crmf.CRMFException;
import org.spongycastle.cert.crmf.ValueDecryptorGenerator;
import org.spongycastle.jcajce.util.DefaultJcaJceHelper;
import org.spongycastle.jcajce.util.NamedJcaJceHelper;
import org.spongycastle.jcajce.util.ProviderJcaJceHelper;
import org.spongycastle.operator.InputDecryptor;
import org.spongycastle.operator.OperatorException;
import org.spongycastle.operator.jcajce.JceAsymmetricKeyUnwrapper;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cert/crmf/jcajce/JceAsymmetricValueDecryptorGenerator.class */
public class JceAsymmetricValueDecryptorGenerator implements ValueDecryptorGenerator {
    private PrivateKey recipientKey;
    private CRMFHelper helper = new CRMFHelper(new DefaultJcaJceHelper());
    private Provider provider = null;
    private String providerName = null;

    public JceAsymmetricValueDecryptorGenerator(PrivateKey recipientKey) {
        this.recipientKey = recipientKey;
    }

    public JceAsymmetricValueDecryptorGenerator setProvider(Provider provider) {
        this.helper = new CRMFHelper(new ProviderJcaJceHelper(provider));
        this.provider = provider;
        this.providerName = null;
        return this;
    }

    public JceAsymmetricValueDecryptorGenerator setProvider(String providerName) {
        this.helper = new CRMFHelper(new NamedJcaJceHelper(providerName));
        this.provider = null;
        this.providerName = providerName;
        return this;
    }

    private Key extractSecretKey(AlgorithmIdentifier keyEncryptionAlgorithm, AlgorithmIdentifier contentEncryptionAlgorithm, byte[] encryptedContentEncryptionKey) throws CRMFException {
        try {
            JceAsymmetricKeyUnwrapper unwrapper = new JceAsymmetricKeyUnwrapper(keyEncryptionAlgorithm, this.recipientKey);
            if (this.provider != null) {
                unwrapper.setProvider(this.provider);
            }
            if (this.providerName != null) {
                unwrapper.setProvider(this.providerName);
            }
            return new SecretKeySpec((byte[]) unwrapper.generateUnwrappedKey(contentEncryptionAlgorithm, encryptedContentEncryptionKey).getRepresentation(), contentEncryptionAlgorithm.getAlgorithm().getId());
        } catch (OperatorException e) {
            throw new CRMFException("key invalid in message: " + e.getMessage(), e);
        }
    }

    @Override // org.spongycastle.cert.crmf.ValueDecryptorGenerator
    public InputDecryptor getValueDecryptor(AlgorithmIdentifier keyEncryptionAlgorithm, final AlgorithmIdentifier contentEncryptionAlgorithm, byte[] encryptedContentEncryptionKey) throws CRMFException {
        Key secretKey = extractSecretKey(keyEncryptionAlgorithm, contentEncryptionAlgorithm, encryptedContentEncryptionKey);
        final Cipher dataCipher = this.helper.createContentCipher(secretKey, contentEncryptionAlgorithm);
        return new InputDecryptor() { // from class: org.spongycastle.cert.crmf.jcajce.JceAsymmetricValueDecryptorGenerator.1
            @Override // org.spongycastle.operator.InputDecryptor
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return contentEncryptionAlgorithm;
            }

            @Override // org.spongycastle.operator.InputDecryptor
            public InputStream getInputStream(InputStream dataIn) {
                return new CipherInputStream(dataIn, dataCipher);
            }
        };
    }
}
