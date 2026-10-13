package org.spongycastle.cms.jcajce;

import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.spongycastle.asn1.cryptopro.Gost2814789EncryptedKey;
import org.spongycastle.asn1.cryptopro.GostR3410KeyTransport;
import org.spongycastle.asn1.cryptopro.GostR3410TransportParameters;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.cms.CMSException;
import org.spongycastle.cms.KeyTransRecipient;
import org.spongycastle.jcajce.spec.GOST28147WrapParameterSpec;
import org.spongycastle.jcajce.spec.UserKeyingMaterialSpec;
import org.spongycastle.operator.OperatorException;
import org.spongycastle.operator.jcajce.JceAsymmetricKeyUnwrapper;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/jcajce/JceKeyTransRecipient.class */
public abstract class JceKeyTransRecipient implements KeyTransRecipient {
    private PrivateKey recipientKey;
    protected EnvelopedDataHelper helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
    protected EnvelopedDataHelper contentHelper = this.helper;
    protected Map extraMappings = new HashMap();
    protected boolean validateKeySize = false;
    protected boolean unwrappedKeyMustBeEncodable;

    public JceKeyTransRecipient(PrivateKey recipientKey) {
        this.recipientKey = recipientKey;
    }

    public JceKeyTransRecipient setProvider(Provider provider) {
        this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(provider));
        this.contentHelper = this.helper;
        return this;
    }

    public JceKeyTransRecipient setProvider(String providerName) {
        this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(providerName));
        this.contentHelper = this.helper;
        return this;
    }

    public JceKeyTransRecipient setAlgorithmMapping(ASN1ObjectIdentifier algorithm, String algorithmName) {
        this.extraMappings.put(algorithm, algorithmName);
        return this;
    }

    public JceKeyTransRecipient setContentProvider(Provider provider) {
        this.contentHelper = CMSUtils.createContentHelper(provider);
        return this;
    }

    public JceKeyTransRecipient setMustProduceEncodableUnwrappedKey(boolean unwrappedKeyMustBeEncodable) {
        this.unwrappedKeyMustBeEncodable = unwrappedKeyMustBeEncodable;
        return this;
    }

    public JceKeyTransRecipient setContentProvider(String providerName) {
        this.contentHelper = CMSUtils.createContentHelper(providerName);
        return this;
    }

    public JceKeyTransRecipient setKeySizeValidation(boolean doValidate) {
        this.validateKeySize = doValidate;
        return this;
    }

    protected Key extractSecretKey(AlgorithmIdentifier keyEncryptionAlgorithm, AlgorithmIdentifier encryptedKeyAlgorithm, byte[] encryptedEncryptionKey) throws CMSException {
        if (CMSUtils.isGOST(keyEncryptionAlgorithm.getAlgorithm())) {
            try {
                GostR3410KeyTransport transport = GostR3410KeyTransport.getInstance(encryptedEncryptionKey);
                GostR3410TransportParameters transParams = transport.getTransportParameters();
                KeyFactory keyFactory = this.helper.createKeyFactory(keyEncryptionAlgorithm.getAlgorithm());
                PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(transParams.getEphemeralPublicKey().getEncoded()));
                KeyAgreement agreement = this.helper.createKeyAgreement(keyEncryptionAlgorithm.getAlgorithm());
                agreement.init(this.recipientKey, new UserKeyingMaterialSpec(transParams.getUkm()));
                agreement.doPhase(pubKey, true);
                SecretKey key = agreement.generateSecret("GOST28147");
                Cipher keyCipher = this.helper.createCipher(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_KeyWrap);
                keyCipher.init(4, key, new GOST28147WrapParameterSpec(transParams.getEncryptionParamSet(), transParams.getUkm()));
                Gost2814789EncryptedKey encKey = transport.getSessionEncryptedKey();
                return keyCipher.unwrap(Arrays.concatenate(encKey.getEncryptedKey(), encKey.getMacKey()), this.helper.getBaseCipherName(encryptedKeyAlgorithm.getAlgorithm()), 3);
            } catch (Exception e) {
                throw new CMSException("exception unwrapping key: " + e.getMessage(), e);
            }
        }
        JceAsymmetricKeyUnwrapper unwrapper = this.helper.createAsymmetricUnwrapper(keyEncryptionAlgorithm, this.recipientKey).setMustProduceEncodableUnwrappedKey(this.unwrappedKeyMustBeEncodable);
        if (!this.extraMappings.isEmpty()) {
            for (ASN1ObjectIdentifier algorithm : this.extraMappings.keySet()) {
                unwrapper.setAlgorithmMapping(algorithm, (String) this.extraMappings.get(algorithm));
            }
        }
        try {
            Key key2 = this.helper.getJceKey(encryptedKeyAlgorithm.getAlgorithm(), unwrapper.generateUnwrappedKey(encryptedKeyAlgorithm, encryptedEncryptionKey));
            if (this.validateKeySize) {
                this.helper.keySizeCheck(encryptedKeyAlgorithm, key2);
            }
            return key2;
        } catch (OperatorException e2) {
            throw new CMSException("exception unwrapping key: " + e2.getMessage(), e2);
        }
    }
}
