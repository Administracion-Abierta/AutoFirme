package org.bouncycastle.cms.jcajce;

import java.security.Key;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.asn1.cms.KEMRecipientInfo;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.Gost2814789EncryptedKey;
import org.bouncycastle.asn1.cryptopro.GostR3410KeyTransport;
import org.bouncycastle.asn1.cryptopro.GostR3410TransportParameters;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.KeyTransRecipient;
import org.bouncycastle.jcajce.spec.GOST28147WrapParameterSpec;
import org.bouncycastle.jcajce.spec.UserKeyingMaterialSpec;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.operator.jcajce.JceAsymmetricKeyUnwrapper;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/jcajce/JceKeyTransRecipient.class */
public abstract class JceKeyTransRecipient implements KeyTransRecipient {
    private PrivateKey recipientKey;
    protected EnvelopedDataHelper helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
    protected EnvelopedDataHelper contentHelper = this.helper;
    protected Map extraMappings = new HashMap();
    protected boolean validateKeySize = false;
    protected boolean unwrappedKeyMustBeEncodable;

    public JceKeyTransRecipient(PrivateKey privateKey) {
        this.recipientKey = CMSUtils.cleanPrivateKey(privateKey);
    }

    public JceKeyTransRecipient setProvider(Provider provider) {
        this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(provider));
        this.contentHelper = this.helper;
        return this;
    }

    public JceKeyTransRecipient setProvider(String str) {
        this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(str));
        this.contentHelper = this.helper;
        return this;
    }

    public JceKeyTransRecipient setAlgorithmMapping(ASN1ObjectIdentifier aSN1ObjectIdentifier, String str) {
        this.extraMappings.put(aSN1ObjectIdentifier, str);
        return this;
    }

    public JceKeyTransRecipient setContentProvider(Provider provider) {
        this.contentHelper = CMSUtils.createContentHelper(provider);
        return this;
    }

    public JceKeyTransRecipient setMustProduceEncodableUnwrappedKey(boolean z) {
        this.unwrappedKeyMustBeEncodable = z;
        return this;
    }

    public JceKeyTransRecipient setContentProvider(String str) {
        this.contentHelper = CMSUtils.createContentHelper(str);
        return this;
    }

    public JceKeyTransRecipient setKeySizeValidation(boolean z) {
        this.validateKeySize = z;
        return this;
    }

    protected Key extractSecretKey(AlgorithmIdentifier algorithmIdentifier, AlgorithmIdentifier algorithmIdentifier2, byte[] bArr) throws CMSException {
        if (CMSUtils.isGOST(algorithmIdentifier.getAlgorithm())) {
            try {
                GostR3410KeyTransport gostR3410KeyTransport = GostR3410KeyTransport.getInstance(bArr);
                GostR3410TransportParameters transportParameters = gostR3410KeyTransport.getTransportParameters();
                PublicKey publicKeyGeneratePublic = this.helper.createKeyFactory(algorithmIdentifier.getAlgorithm()).generatePublic(new X509EncodedKeySpec(transportParameters.getEphemeralPublicKey().getEncoded()));
                KeyAgreement keyAgreementCreateKeyAgreement = this.helper.createKeyAgreement(algorithmIdentifier.getAlgorithm());
                keyAgreementCreateKeyAgreement.init(this.recipientKey, new UserKeyingMaterialSpec(transportParameters.getUkm()));
                keyAgreementCreateKeyAgreement.doPhase(publicKeyGeneratePublic, true);
                SecretKey secretKeyGenerateSecret = keyAgreementCreateKeyAgreement.generateSecret(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_KeyWrap.getId());
                Cipher cipherCreateCipher = this.helper.createCipher(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_KeyWrap);
                cipherCreateCipher.init(4, secretKeyGenerateSecret, new GOST28147WrapParameterSpec(transportParameters.getEncryptionParamSet(), transportParameters.getUkm()));
                Gost2814789EncryptedKey sessionEncryptedKey = gostR3410KeyTransport.getSessionEncryptedKey();
                return cipherCreateCipher.unwrap(Arrays.concatenate(sessionEncryptedKey.getEncryptedKey(), sessionEncryptedKey.getMacKey()), this.helper.getBaseCipherName(algorithmIdentifier2.getAlgorithm()), 3);
            } catch (Exception e) {
                throw new CMSException("exception unwrapping key: " + e.getMessage(), e);
            }
        }
        if (CMSObjectIdentifiers.id_ori_kem.equals((ASN1Primitive) algorithmIdentifier.getAlgorithm())) {
            JceAsymmetricKeyUnwrapper mustProduceEncodableUnwrappedKey = this.helper.createAsymmetricUnwrapper(KEMRecipientInfo.getInstance(algorithmIdentifier.getParameters()).getKem(), this.recipientKey).setMustProduceEncodableUnwrappedKey(this.unwrappedKeyMustBeEncodable);
            if (!this.extraMappings.isEmpty()) {
                for (ASN1ObjectIdentifier aSN1ObjectIdentifier : this.extraMappings.keySet()) {
                    mustProduceEncodableUnwrappedKey.setAlgorithmMapping(aSN1ObjectIdentifier, (String) this.extraMappings.get(aSN1ObjectIdentifier));
                }
            }
            try {
                Key jceKey = this.helper.getJceKey(algorithmIdentifier2, mustProduceEncodableUnwrappedKey.generateUnwrappedKey(algorithmIdentifier2, bArr));
                if (this.validateKeySize) {
                    this.helper.keySizeCheck(algorithmIdentifier2, jceKey);
                }
                return jceKey;
            } catch (OperatorException e2) {
                throw new CMSException("exception unwrapping key: " + e2.getMessage(), e2);
            }
        }
        JceAsymmetricKeyUnwrapper mustProduceEncodableUnwrappedKey2 = this.helper.createAsymmetricUnwrapper(algorithmIdentifier, this.recipientKey).setMustProduceEncodableUnwrappedKey(this.unwrappedKeyMustBeEncodable);
        if (!this.extraMappings.isEmpty()) {
            for (ASN1ObjectIdentifier aSN1ObjectIdentifier2 : this.extraMappings.keySet()) {
                mustProduceEncodableUnwrappedKey2.setAlgorithmMapping(aSN1ObjectIdentifier2, (String) this.extraMappings.get(aSN1ObjectIdentifier2));
            }
        }
        try {
            Key jceKey2 = this.helper.getJceKey(algorithmIdentifier2, mustProduceEncodableUnwrappedKey2.generateUnwrappedKey(algorithmIdentifier2, bArr));
            if (this.validateKeySize) {
                if (bArr.equals(CMSObjectIdentifiers.id_alg_cek_hkdf_sha256)) {
                    this.helper.keySizeCheck(AlgorithmIdentifier.getInstance(algorithmIdentifier2.getParameters()), jceKey2);
                } else {
                    this.helper.keySizeCheck(algorithmIdentifier2, jceKey2);
                }
            }
            return jceKey2;
        } catch (OperatorException e3) {
            throw new CMSException("exception unwrapping key: " + e3.getMessage(), e3);
        }
    }
}
