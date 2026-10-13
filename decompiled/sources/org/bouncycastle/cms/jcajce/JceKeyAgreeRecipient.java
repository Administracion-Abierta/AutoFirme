package org.bouncycastle.cms.jcajce;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
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
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.cms.ecc.ECCCMSSharedInfo;
import org.bouncycastle.asn1.cms.ecc.MQVuserKeyingMaterial;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.Gost2814789EncryptedKey;
import org.bouncycastle.asn1.cryptopro.Gost2814789KeyWrapParameters;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.KeyAgreeRecipient;
import org.bouncycastle.jcajce.spec.GOST28147WrapParameterSpec;
import org.bouncycastle.jcajce.spec.MQVParameterSpec;
import org.bouncycastle.jcajce.spec.UserKeyingMaterialSpec;
import org.bouncycastle.operator.DefaultSecretKeySizeProvider;
import org.bouncycastle.operator.SecretKeySizeProvider;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/jcajce/JceKeyAgreeRecipient.class */
public abstract class JceKeyAgreeRecipient implements KeyAgreeRecipient {
    private static final Set possibleOldMessages = new HashSet();
    private PrivateKey recipientKey;
    protected EnvelopedDataHelper helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
    protected EnvelopedDataHelper contentHelper = this.helper;
    protected EnvelopedDataHelper unwrappingHelper = this.helper;
    private SecretKeySizeProvider keySizeProvider = new DefaultSecretKeySizeProvider();
    private AlgorithmIdentifier privKeyAlgID = null;
    private static KeyMaterialGenerator old_ecc_cms_Generator;
    private static KeyMaterialGenerator simple_ecc_cmsGenerator;
    private static KeyMaterialGenerator ecc_cms_Generator;

    public JceKeyAgreeRecipient(PrivateKey privateKey) {
        this.recipientKey = CMSUtils.cleanPrivateKey(privateKey);
    }

    public JceKeyAgreeRecipient setProvider(Provider provider) {
        this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(provider));
        this.contentHelper = this.helper;
        this.unwrappingHelper = this.helper;
        return this;
    }

    public JceKeyAgreeRecipient setProvider(String str) {
        this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(str));
        this.contentHelper = this.helper;
        this.unwrappingHelper = this.helper;
        return this;
    }

    public JceKeyAgreeRecipient setUnwrappingProvider(Provider provider) {
        this.unwrappingHelper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(provider));
        return this;
    }

    public JceKeyAgreeRecipient setUnwrappingProvider(String str) {
        this.unwrappingHelper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(str));
        return this;
    }

    public JceKeyAgreeRecipient setContentProvider(Provider provider) {
        this.contentHelper = CMSUtils.createContentHelper(provider);
        return this;
    }

    public JceKeyAgreeRecipient setContentProvider(String str) {
        this.contentHelper = CMSUtils.createContentHelper(str);
        return this;
    }

    public JceKeyAgreeRecipient setPrivateKeyAlgorithmIdentifier(AlgorithmIdentifier algorithmIdentifier) {
        this.privKeyAlgID = algorithmIdentifier;
        return this;
    }

    private SecretKey calculateAgreedWrapKey(AlgorithmIdentifier algorithmIdentifier, AlgorithmIdentifier algorithmIdentifier2, PublicKey publicKey, ASN1OctetString aSN1OctetString, PrivateKey privateKey, KeyMaterialGenerator keyMaterialGenerator) throws GeneralSecurityException, CMSException, IOException {
        PrivateKey privateKeyCleanPrivateKey = CMSUtils.cleanPrivateKey(privateKey);
        if (CMSUtils.isMQV(algorithmIdentifier.getAlgorithm())) {
            MQVuserKeyingMaterial mQVuserKeyingMaterial = MQVuserKeyingMaterial.getInstance(aSN1OctetString.getOctets());
            PublicKey publicKeyGeneratePublic = this.helper.createKeyFactory(algorithmIdentifier.getAlgorithm()).generatePublic(new X509EncodedKeySpec(new SubjectPublicKeyInfo(getPrivateKeyAlgorithmIdentifier(), mQVuserKeyingMaterial.getEphemeralPublicKey().getPublicKeyData()).getEncoded()));
            KeyAgreement keyAgreementCreateKeyAgreement = this.helper.createKeyAgreement(algorithmIdentifier.getAlgorithm());
            byte[] octets = mQVuserKeyingMaterial.getAddedukm() != null ? mQVuserKeyingMaterial.getAddedukm().getOctets() : null;
            if (keyMaterialGenerator == old_ecc_cms_Generator) {
                octets = old_ecc_cms_Generator.generateKDFMaterial(algorithmIdentifier2, this.keySizeProvider.getKeySize(algorithmIdentifier2), octets);
            }
            keyAgreementCreateKeyAgreement.init(privateKeyCleanPrivateKey, new MQVParameterSpec(privateKeyCleanPrivateKey, publicKeyGeneratePublic, octets));
            keyAgreementCreateKeyAgreement.doPhase(publicKey, true);
            return keyAgreementCreateKeyAgreement.generateSecret(algorithmIdentifier2.getAlgorithm().getId());
        }
        KeyAgreement keyAgreementCreateKeyAgreement2 = this.helper.createKeyAgreement(algorithmIdentifier.getAlgorithm());
        UserKeyingMaterialSpec userKeyingMaterialSpec = null;
        if (CMSUtils.isEC(algorithmIdentifier.getAlgorithm())) {
            userKeyingMaterialSpec = new UserKeyingMaterialSpec(aSN1OctetString != null ? keyMaterialGenerator.generateKDFMaterial(algorithmIdentifier2, this.keySizeProvider.getKeySize(algorithmIdentifier2), aSN1OctetString.getOctets()) : keyMaterialGenerator.generateKDFMaterial(algorithmIdentifier2, this.keySizeProvider.getKeySize(algorithmIdentifier2), null));
        } else if (CMSUtils.isRFC2631(algorithmIdentifier.getAlgorithm())) {
            if (aSN1OctetString != null) {
                userKeyingMaterialSpec = new UserKeyingMaterialSpec(aSN1OctetString.getOctets());
            }
        } else {
            if (!CMSUtils.isGOST(algorithmIdentifier.getAlgorithm())) {
                throw new CMSException("Unknown key agreement algorithm: " + algorithmIdentifier.getAlgorithm());
            }
            if (aSN1OctetString != null) {
                userKeyingMaterialSpec = new UserKeyingMaterialSpec(aSN1OctetString.getOctets());
            }
        }
        keyAgreementCreateKeyAgreement2.init(privateKeyCleanPrivateKey, userKeyingMaterialSpec);
        keyAgreementCreateKeyAgreement2.doPhase(publicKey, true);
        return keyAgreementCreateKeyAgreement2.generateSecret(algorithmIdentifier2.getAlgorithm().getId());
    }

    protected Key unwrapSessionKey(ASN1ObjectIdentifier aSN1ObjectIdentifier, SecretKey secretKey, ASN1ObjectIdentifier aSN1ObjectIdentifier2, byte[] bArr) throws CMSException, NoSuchAlgorithmException, InvalidKeyException {
        Cipher cipherCreateCipher = this.unwrappingHelper.createCipher(aSN1ObjectIdentifier);
        cipherCreateCipher.init(4, secretKey);
        return cipherCreateCipher.unwrap(bArr, this.helper.getBaseCipherName(aSN1ObjectIdentifier2), 3);
    }

    protected Key extractSecretKey(AlgorithmIdentifier algorithmIdentifier, AlgorithmIdentifier algorithmIdentifier2, SubjectPublicKeyInfo subjectPublicKeyInfo, ASN1OctetString aSN1OctetString, byte[] bArr) throws CMSException {
        try {
            AlgorithmIdentifier algorithmIdentifier3 = AlgorithmIdentifier.getInstance(algorithmIdentifier.getParameters());
            PublicKey publicKeyGeneratePublic = this.helper.createKeyFactory(subjectPublicKeyInfo.getAlgorithm().getAlgorithm()).generatePublic(new X509EncodedKeySpec(subjectPublicKeyInfo.getEncoded()));
            try {
                SecretKey secretKeyCalculateAgreedWrapKey = calculateAgreedWrapKey(algorithmIdentifier, algorithmIdentifier3, publicKeyGeneratePublic, aSN1OctetString, this.recipientKey, ecc_cms_Generator);
                if (!algorithmIdentifier3.getAlgorithm().equals((ASN1Primitive) CryptoProObjectIdentifiers.id_Gost28147_89_None_KeyWrap) && !algorithmIdentifier3.getAlgorithm().equals((ASN1Primitive) CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_KeyWrap)) {
                    return unwrapSessionKey(algorithmIdentifier3.getAlgorithm(), secretKeyCalculateAgreedWrapKey, algorithmIdentifier2.getAlgorithm(), bArr);
                }
                Gost2814789EncryptedKey gost2814789EncryptedKey = Gost2814789EncryptedKey.getInstance(bArr);
                Gost2814789KeyWrapParameters gost2814789KeyWrapParameters = Gost2814789KeyWrapParameters.getInstance(algorithmIdentifier3.getParameters());
                Cipher cipherCreateCipher = this.helper.createCipher(algorithmIdentifier3.getAlgorithm());
                cipherCreateCipher.init(4, secretKeyCalculateAgreedWrapKey, new GOST28147WrapParameterSpec(gost2814789KeyWrapParameters.getEncryptionParamSet(), aSN1OctetString.getOctets()));
                return cipherCreateCipher.unwrap(Arrays.concatenate(gost2814789EncryptedKey.getEncryptedKey(), gost2814789EncryptedKey.getMacKey()), this.helper.getBaseCipherName(algorithmIdentifier2.getAlgorithm()), 3);
            } catch (InvalidKeyException e) {
                if (possibleOldMessages.contains(algorithmIdentifier.getAlgorithm())) {
                    return unwrapSessionKey(algorithmIdentifier3.getAlgorithm(), calculateAgreedWrapKey(algorithmIdentifier, algorithmIdentifier3, publicKeyGeneratePublic, aSN1OctetString, this.recipientKey, old_ecc_cms_Generator), algorithmIdentifier2.getAlgorithm(), bArr);
                }
                if (aSN1OctetString == null) {
                    throw e;
                }
                try {
                    return unwrapSessionKey(algorithmIdentifier3.getAlgorithm(), calculateAgreedWrapKey(algorithmIdentifier, algorithmIdentifier3, publicKeyGeneratePublic, aSN1OctetString, this.recipientKey, simple_ecc_cmsGenerator), algorithmIdentifier2.getAlgorithm(), bArr);
                } catch (InvalidKeyException e2) {
                    throw e;
                }
            }
        } catch (InvalidKeyException e3) {
            throw new CMSException("key invalid in message.", e3);
        } catch (NoSuchAlgorithmException e4) {
            throw new CMSException("can't find algorithm.", e4);
        } catch (InvalidKeySpecException e5) {
            throw new CMSException("originator key spec invalid.", e5);
        } catch (NoSuchPaddingException e6) {
            throw new CMSException("required padding not supported.", e6);
        } catch (Exception e7) {
            throw new CMSException("originator key invalid.", e7);
        }
    }

    @Override // org.bouncycastle.cms.KeyAgreeRecipient
    public AlgorithmIdentifier getPrivateKeyAlgorithmIdentifier() {
        if (this.privKeyAlgID == null) {
            this.privKeyAlgID = PrivateKeyInfo.getInstance(this.recipientKey.getEncoded()).getPrivateKeyAlgorithm();
        }
        return this.privKeyAlgID;
    }

    static {
        possibleOldMessages.add(X9ObjectIdentifiers.dhSinglePass_stdDH_sha1kdf_scheme);
        possibleOldMessages.add(X9ObjectIdentifiers.mqvSinglePass_sha1kdf_scheme);
        old_ecc_cms_Generator = new KeyMaterialGenerator() { // from class: org.bouncycastle.cms.jcajce.JceKeyAgreeRecipient.1
            @Override // org.bouncycastle.cms.jcajce.KeyMaterialGenerator
            public byte[] generateKDFMaterial(AlgorithmIdentifier algorithmIdentifier, int i, byte[] bArr) {
                try {
                    return new ECCCMSSharedInfo(new AlgorithmIdentifier(algorithmIdentifier.getAlgorithm(), DERNull.INSTANCE), bArr, Pack.intToBigEndian(i)).getEncoded("DER");
                } catch (IOException e) {
                    throw new IllegalStateException("Unable to create KDF material: " + e);
                }
            }
        };
        simple_ecc_cmsGenerator = new KeyMaterialGenerator() { // from class: org.bouncycastle.cms.jcajce.JceKeyAgreeRecipient.2
            @Override // org.bouncycastle.cms.jcajce.KeyMaterialGenerator
            public byte[] generateKDFMaterial(AlgorithmIdentifier algorithmIdentifier, int i, byte[] bArr) {
                return bArr;
            }
        };
        ecc_cms_Generator = new RFC5753KeyMaterialGenerator();
    }
}
