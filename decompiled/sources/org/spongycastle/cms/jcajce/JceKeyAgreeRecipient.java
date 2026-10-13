package org.spongycastle.cms.jcajce;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashSet;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.DERNull;
import org.spongycastle.asn1.cms.ecc.ECCCMSSharedInfo;
import org.spongycastle.asn1.cms.ecc.MQVuserKeyingMaterial;
import org.spongycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.spongycastle.asn1.cryptopro.Gost2814789EncryptedKey;
import org.spongycastle.asn1.cryptopro.Gost2814789KeyWrapParameters;
import org.spongycastle.asn1.pkcs.PrivateKeyInfo;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.asn1.x9.X9ObjectIdentifiers;
import org.spongycastle.cms.CMSException;
import org.spongycastle.cms.KeyAgreeRecipient;
import org.spongycastle.jcajce.spec.GOST28147WrapParameterSpec;
import org.spongycastle.jcajce.spec.MQVParameterSpec;
import org.spongycastle.jcajce.spec.UserKeyingMaterialSpec;
import org.spongycastle.operator.DefaultSecretKeySizeProvider;
import org.spongycastle.operator.SecretKeySizeProvider;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/jcajce/JceKeyAgreeRecipient.class */
public abstract class JceKeyAgreeRecipient implements KeyAgreeRecipient {
    private static final Set possibleOldMessages = new HashSet();
    private PrivateKey recipientKey;
    protected EnvelopedDataHelper helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
    protected EnvelopedDataHelper contentHelper = this.helper;
    private SecretKeySizeProvider keySizeProvider = new DefaultSecretKeySizeProvider();
    private static KeyMaterialGenerator old_ecc_cms_Generator;
    private static KeyMaterialGenerator ecc_cms_Generator;

    static {
        possibleOldMessages.add(X9ObjectIdentifiers.dhSinglePass_stdDH_sha1kdf_scheme);
        possibleOldMessages.add(X9ObjectIdentifiers.mqvSinglePass_sha1kdf_scheme);
        old_ecc_cms_Generator = new KeyMaterialGenerator() { // from class: org.spongycastle.cms.jcajce.JceKeyAgreeRecipient.1
            @Override // org.spongycastle.cms.jcajce.KeyMaterialGenerator
            public byte[] generateKDFMaterial(AlgorithmIdentifier keyAlgorithm, int keySize, byte[] userKeyMaterialParameters) {
                ECCCMSSharedInfo eccInfo = new ECCCMSSharedInfo(new AlgorithmIdentifier(keyAlgorithm.getAlgorithm(), DERNull.INSTANCE), userKeyMaterialParameters, Pack.intToBigEndian(keySize));
                try {
                    return eccInfo.getEncoded("DER");
                } catch (IOException e) {
                    throw new IllegalStateException("Unable to create KDF material: " + e);
                }
            }
        };
        ecc_cms_Generator = new RFC5753KeyMaterialGenerator();
    }

    public JceKeyAgreeRecipient(PrivateKey recipientKey) {
        this.recipientKey = recipientKey;
    }

    public JceKeyAgreeRecipient setProvider(Provider provider) {
        this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(provider));
        this.contentHelper = this.helper;
        return this;
    }

    public JceKeyAgreeRecipient setProvider(String providerName) {
        this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(providerName));
        this.contentHelper = this.helper;
        return this;
    }

    public JceKeyAgreeRecipient setContentProvider(Provider provider) {
        this.contentHelper = CMSUtils.createContentHelper(provider);
        return this;
    }

    public JceKeyAgreeRecipient setContentProvider(String providerName) {
        this.contentHelper = CMSUtils.createContentHelper(providerName);
        return this;
    }

    private SecretKey calculateAgreedWrapKey(AlgorithmIdentifier keyEncAlg, AlgorithmIdentifier wrapAlg, PublicKey senderPublicKey, ASN1OctetString userKeyingMaterial, PrivateKey receiverPrivateKey, KeyMaterialGenerator kmGen) throws GeneralSecurityException, IOException, CMSException {
        if (CMSUtils.isMQV(keyEncAlg.getAlgorithm())) {
            MQVuserKeyingMaterial ukm = MQVuserKeyingMaterial.getInstance(userKeyingMaterial.getOctets());
            SubjectPublicKeyInfo pubInfo = new SubjectPublicKeyInfo(getPrivateKeyAlgorithmIdentifier(), ukm.getEphemeralPublicKey().getPublicKey().getBytes());
            X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(pubInfo.getEncoded());
            KeyFactory fact = this.helper.createKeyFactory(keyEncAlg.getAlgorithm());
            PublicKey ephemeralKey = fact.generatePublic(pubSpec);
            KeyAgreement agreement = this.helper.createKeyAgreement(keyEncAlg.getAlgorithm());
            byte[] ukmKeyingMaterial = ukm.getAddedukm() != null ? ukm.getAddedukm().getOctets() : null;
            if (kmGen == old_ecc_cms_Generator) {
                ukmKeyingMaterial = old_ecc_cms_Generator.generateKDFMaterial(wrapAlg, this.keySizeProvider.getKeySize(wrapAlg), ukmKeyingMaterial);
            }
            agreement.init(receiverPrivateKey, new MQVParameterSpec(receiverPrivateKey, ephemeralKey, ukmKeyingMaterial));
            agreement.doPhase(senderPublicKey, true);
            return agreement.generateSecret(wrapAlg.getAlgorithm().getId());
        }
        KeyAgreement agreement2 = this.helper.createKeyAgreement(keyEncAlg.getAlgorithm());
        UserKeyingMaterialSpec userKeyingMaterialSpec = null;
        if (CMSUtils.isEC(keyEncAlg.getAlgorithm())) {
            if (userKeyingMaterial != null) {
                byte[] ukmKeyingMaterial2 = kmGen.generateKDFMaterial(wrapAlg, this.keySizeProvider.getKeySize(wrapAlg), userKeyingMaterial.getOctets());
                userKeyingMaterialSpec = new UserKeyingMaterialSpec(ukmKeyingMaterial2);
            } else {
                byte[] ukmKeyingMaterial3 = kmGen.generateKDFMaterial(wrapAlg, this.keySizeProvider.getKeySize(wrapAlg), null);
                userKeyingMaterialSpec = new UserKeyingMaterialSpec(ukmKeyingMaterial3);
            }
        } else if (CMSUtils.isRFC2631(keyEncAlg.getAlgorithm())) {
            if (userKeyingMaterial != null) {
                userKeyingMaterialSpec = new UserKeyingMaterialSpec(userKeyingMaterial.getOctets());
            }
        } else if (CMSUtils.isGOST(keyEncAlg.getAlgorithm())) {
            if (userKeyingMaterial != null) {
                userKeyingMaterialSpec = new UserKeyingMaterialSpec(userKeyingMaterial.getOctets());
            }
        } else {
            throw new CMSException("Unknown key agreement algorithm: " + keyEncAlg.getAlgorithm());
        }
        agreement2.init(receiverPrivateKey, userKeyingMaterialSpec);
        agreement2.doPhase(senderPublicKey, true);
        return agreement2.generateSecret(wrapAlg.getAlgorithm().getId());
    }

    private Key unwrapSessionKey(ASN1ObjectIdentifier wrapAlg, SecretKey agreedKey, ASN1ObjectIdentifier contentEncryptionAlgorithm, byte[] encryptedContentEncryptionKey) throws NoSuchAlgorithmException, InvalidKeyException, CMSException {
        Cipher keyCipher = this.helper.createCipher(wrapAlg);
        keyCipher.init(4, agreedKey);
        return keyCipher.unwrap(encryptedContentEncryptionKey, this.helper.getBaseCipherName(contentEncryptionAlgorithm), 3);
    }

    protected Key extractSecretKey(AlgorithmIdentifier keyEncryptionAlgorithm, AlgorithmIdentifier contentEncryptionAlgorithm, SubjectPublicKeyInfo senderKey, ASN1OctetString userKeyingMaterial, byte[] encryptedContentEncryptionKey) throws CMSException {
        try {
            AlgorithmIdentifier wrapAlg = AlgorithmIdentifier.getInstance(keyEncryptionAlgorithm.getParameters());
            X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(senderKey.getEncoded());
            KeyFactory fact = this.helper.createKeyFactory(senderKey.getAlgorithm().getAlgorithm());
            PublicKey senderPublicKey = fact.generatePublic(pubSpec);
            try {
                SecretKey agreedWrapKey = calculateAgreedWrapKey(keyEncryptionAlgorithm, wrapAlg, senderPublicKey, userKeyingMaterial, this.recipientKey, ecc_cms_Generator);
                if (wrapAlg.getAlgorithm().equals(CryptoProObjectIdentifiers.id_Gost28147_89_None_KeyWrap) || wrapAlg.getAlgorithm().equals(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_KeyWrap)) {
                    Gost2814789EncryptedKey encKey = Gost2814789EncryptedKey.getInstance(encryptedContentEncryptionKey);
                    Gost2814789KeyWrapParameters wrapParams = Gost2814789KeyWrapParameters.getInstance(wrapAlg.getParameters());
                    Cipher keyCipher = this.helper.createCipher(wrapAlg.getAlgorithm());
                    keyCipher.init(4, agreedWrapKey, new GOST28147WrapParameterSpec(wrapParams.getEncryptionParamSet(), userKeyingMaterial.getOctets()));
                    return keyCipher.unwrap(Arrays.concatenate(encKey.getEncryptedKey(), encKey.getMacKey()), this.helper.getBaseCipherName(contentEncryptionAlgorithm.getAlgorithm()), 3);
                }
                return unwrapSessionKey(wrapAlg.getAlgorithm(), agreedWrapKey, contentEncryptionAlgorithm.getAlgorithm(), encryptedContentEncryptionKey);
            } catch (InvalidKeyException e) {
                if (possibleOldMessages.contains(keyEncryptionAlgorithm.getAlgorithm())) {
                    return unwrapSessionKey(wrapAlg.getAlgorithm(), calculateAgreedWrapKey(keyEncryptionAlgorithm, wrapAlg, senderPublicKey, userKeyingMaterial, this.recipientKey, old_ecc_cms_Generator), contentEncryptionAlgorithm.getAlgorithm(), encryptedContentEncryptionKey);
                }
                throw e;
            }
        } catch (InvalidKeyException e2) {
            throw new CMSException("key invalid in message.", e2);
        } catch (NoSuchAlgorithmException e3) {
            throw new CMSException("can't find algorithm.", e3);
        } catch (InvalidKeySpecException e4) {
            throw new CMSException("originator key spec invalid.", e4);
        } catch (NoSuchPaddingException e5) {
            throw new CMSException("required padding not supported.", e5);
        } catch (Exception e6) {
            throw new CMSException("originator key invalid.", e6);
        }
    }

    @Override // org.spongycastle.cms.KeyAgreeRecipient
    public AlgorithmIdentifier getPrivateKeyAlgorithmIdentifier() {
        return PrivateKeyInfo.getInstance(this.recipientKey.getEncoded()).getPrivateKeyAlgorithm();
    }
}
