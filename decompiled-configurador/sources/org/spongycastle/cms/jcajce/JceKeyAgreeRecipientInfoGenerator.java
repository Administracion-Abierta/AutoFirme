package org.spongycastle.cms.jcajce;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.spec.AlgorithmParameterSpec;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1Encoding;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.cms.KeyAgreeRecipientIdentifier;
import org.spongycastle.asn1.cms.OriginatorPublicKey;
import org.spongycastle.asn1.cms.RecipientEncryptedKey;
import org.spongycastle.asn1.cms.RecipientKeyIdentifier;
import org.spongycastle.asn1.cms.ecc.MQVuserKeyingMaterial;
import org.spongycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.spongycastle.asn1.cryptopro.Gost2814789EncryptedKey;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.cms.CMSException;
import org.spongycastle.cms.KeyAgreeRecipientInfoGenerator;
import org.spongycastle.jcajce.spec.GOST28147WrapParameterSpec;
import org.spongycastle.jcajce.spec.MQVParameterSpec;
import org.spongycastle.jcajce.spec.UserKeyingMaterialSpec;
import org.spongycastle.operator.DefaultSecretKeySizeProvider;
import org.spongycastle.operator.GenericKey;
import org.spongycastle.operator.SecretKeySizeProvider;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/jcajce/JceKeyAgreeRecipientInfoGenerator.class */
public class JceKeyAgreeRecipientInfoGenerator extends KeyAgreeRecipientInfoGenerator {
    private SecretKeySizeProvider keySizeProvider;
    private List recipientIDs;
    private List recipientKeys;
    private PublicKey senderPublicKey;
    private PrivateKey senderPrivateKey;
    private EnvelopedDataHelper helper;
    private SecureRandom random;
    private KeyPair ephemeralKP;
    private byte[] userKeyingMaterial;
    private static KeyMaterialGenerator ecc_cms_Generator = new RFC5753KeyMaterialGenerator();

    public JceKeyAgreeRecipientInfoGenerator(ASN1ObjectIdentifier keyAgreementOID, PrivateKey senderPrivateKey, PublicKey senderPublicKey, ASN1ObjectIdentifier keyEncryptionOID) {
        super(keyAgreementOID, SubjectPublicKeyInfo.getInstance(senderPublicKey.getEncoded()), keyEncryptionOID);
        this.keySizeProvider = new DefaultSecretKeySizeProvider();
        this.recipientIDs = new ArrayList();
        this.recipientKeys = new ArrayList();
        this.helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
        this.senderPublicKey = senderPublicKey;
        this.senderPrivateKey = senderPrivateKey;
    }

    public JceKeyAgreeRecipientInfoGenerator setUserKeyingMaterial(byte[] userKeyingMaterial) {
        this.userKeyingMaterial = Arrays.clone(userKeyingMaterial);
        return this;
    }

    public JceKeyAgreeRecipientInfoGenerator setProvider(Provider provider) {
        this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(provider));
        return this;
    }

    public JceKeyAgreeRecipientInfoGenerator setProvider(String providerName) {
        this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(providerName));
        return this;
    }

    public JceKeyAgreeRecipientInfoGenerator setSecureRandom(SecureRandom random) {
        this.random = random;
        return this;
    }

    public JceKeyAgreeRecipientInfoGenerator addRecipient(X509Certificate recipientCert) throws CertificateEncodingException {
        this.recipientIDs.add(new KeyAgreeRecipientIdentifier(CMSUtils.getIssuerAndSerialNumber(recipientCert)));
        this.recipientKeys.add(recipientCert.getPublicKey());
        return this;
    }

    public JceKeyAgreeRecipientInfoGenerator addRecipient(byte[] subjectKeyID, PublicKey publicKey) throws CertificateEncodingException {
        this.recipientIDs.add(new KeyAgreeRecipientIdentifier(new RecipientKeyIdentifier(subjectKeyID)));
        this.recipientKeys.add(publicKey);
        return this;
    }

    @Override // org.spongycastle.cms.KeyAgreeRecipientInfoGenerator
    public ASN1Sequence generateRecipientEncryptedKeys(AlgorithmIdentifier keyAgreeAlgorithm, AlgorithmIdentifier keyEncryptionAlgorithm, GenericKey contentEncryptionKey) throws CMSException {
        AlgorithmParameterSpec agreementParamSpec;
        ASN1OctetString encryptedKey;
        if (this.recipientIDs.isEmpty()) {
            throw new CMSException("No recipients associated with generator - use addRecipient()");
        }
        init(keyAgreeAlgorithm.getAlgorithm());
        PrivateKey senderPrivateKey = this.senderPrivateKey;
        ASN1ObjectIdentifier keyAgreementOID = keyAgreeAlgorithm.getAlgorithm();
        ASN1EncodableVector recipientEncryptedKeys = new ASN1EncodableVector();
        for (int i = 0; i != this.recipientIDs.size(); i++) {
            PublicKey recipientPublicKey = (PublicKey) this.recipientKeys.get(i);
            KeyAgreeRecipientIdentifier karId = (KeyAgreeRecipientIdentifier) this.recipientIDs.get(i);
            try {
                ASN1ObjectIdentifier keyEncAlg = keyEncryptionAlgorithm.getAlgorithm();
                if (CMSUtils.isMQV(keyAgreementOID)) {
                    agreementParamSpec = new MQVParameterSpec(this.ephemeralKP, recipientPublicKey, this.userKeyingMaterial);
                } else if (CMSUtils.isEC(keyAgreementOID)) {
                    byte[] ukmKeyingMaterial = ecc_cms_Generator.generateKDFMaterial(keyEncryptionAlgorithm, this.keySizeProvider.getKeySize(keyEncAlg), this.userKeyingMaterial);
                    agreementParamSpec = new UserKeyingMaterialSpec(ukmKeyingMaterial);
                } else if (CMSUtils.isRFC2631(keyAgreementOID)) {
                    if (this.userKeyingMaterial != null) {
                        agreementParamSpec = new UserKeyingMaterialSpec(this.userKeyingMaterial);
                    } else {
                        if (keyAgreementOID.equals(PKCSObjectIdentifiers.id_alg_SSDH)) {
                            throw new CMSException("User keying material must be set for static keys.");
                        }
                        agreementParamSpec = null;
                    }
                } else if (CMSUtils.isGOST(keyAgreementOID)) {
                    if (this.userKeyingMaterial != null) {
                        agreementParamSpec = new UserKeyingMaterialSpec(this.userKeyingMaterial);
                    } else {
                        throw new CMSException("User keying material must be set for static keys.");
                    }
                } else {
                    throw new CMSException("Unknown key agreement algorithm: " + keyAgreementOID);
                }
                KeyAgreement keyAgreement = this.helper.createKeyAgreement(keyAgreementOID);
                keyAgreement.init(senderPrivateKey, agreementParamSpec, this.random);
                keyAgreement.doPhase(recipientPublicKey, true);
                SecretKey keyEncryptionKey = keyAgreement.generateSecret(keyEncAlg.getId());
                Cipher keyEncryptionCipher = this.helper.createCipher(keyEncAlg);
                if (keyEncAlg.equals(CryptoProObjectIdentifiers.id_Gost28147_89_None_KeyWrap) || keyEncAlg.equals(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_KeyWrap)) {
                    keyEncryptionCipher.init(3, keyEncryptionKey, new GOST28147WrapParameterSpec(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_A_ParamSet, this.userKeyingMaterial));
                    byte[] encKeyBytes = keyEncryptionCipher.wrap(this.helper.getJceKey(contentEncryptionKey));
                    Gost2814789EncryptedKey encKey = new Gost2814789EncryptedKey(Arrays.copyOfRange(encKeyBytes, 0, encKeyBytes.length - 4), Arrays.copyOfRange(encKeyBytes, encKeyBytes.length - 4, encKeyBytes.length));
                    encryptedKey = new DEROctetString(encKey.getEncoded(ASN1Encoding.DER));
                } else {
                    keyEncryptionCipher.init(3, keyEncryptionKey, this.random);
                    byte[] encryptedKeyBytes = keyEncryptionCipher.wrap(this.helper.getJceKey(contentEncryptionKey));
                    encryptedKey = new DEROctetString(encryptedKeyBytes);
                }
                recipientEncryptedKeys.add(new RecipientEncryptedKey(karId, encryptedKey));
            } catch (IOException e) {
                throw new CMSException("unable to encode wrapped key: " + e.getMessage(), e);
            } catch (GeneralSecurityException e2) {
                throw new CMSException("cannot perform agreement step: " + e2.getMessage(), e2);
            }
        }
        return new DERSequence(recipientEncryptedKeys);
    }

    @Override // org.spongycastle.cms.KeyAgreeRecipientInfoGenerator
    protected byte[] getUserKeyingMaterial(AlgorithmIdentifier keyAgreeAlg) throws CMSException {
        init(keyAgreeAlg.getAlgorithm());
        if (this.ephemeralKP != null) {
            OriginatorPublicKey originatorPublicKey = createOriginatorPublicKey(SubjectPublicKeyInfo.getInstance(this.ephemeralKP.getPublic().getEncoded()));
            try {
                if (this.userKeyingMaterial != null) {
                    return new MQVuserKeyingMaterial(originatorPublicKey, new DEROctetString(this.userKeyingMaterial)).getEncoded();
                }
                return new MQVuserKeyingMaterial(originatorPublicKey, null).getEncoded();
            } catch (IOException e) {
                throw new CMSException("unable to encode user keying material: " + e.getMessage(), e);
            }
        }
        return this.userKeyingMaterial;
    }

    private void init(ASN1ObjectIdentifier keyAgreementOID) throws CMSException {
        if (this.random == null) {
            this.random = new SecureRandom();
        }
        if (CMSUtils.isMQV(keyAgreementOID) && this.ephemeralKP == null) {
            try {
                SubjectPublicKeyInfo pubInfo = SubjectPublicKeyInfo.getInstance(this.senderPublicKey.getEncoded());
                AlgorithmParameters ecAlgParams = this.helper.createAlgorithmParameters(keyAgreementOID);
                ecAlgParams.init(pubInfo.getAlgorithm().getParameters().toASN1Primitive().getEncoded());
                KeyPairGenerator ephemKPG = this.helper.createKeyPairGenerator(keyAgreementOID);
                ephemKPG.initialize(ecAlgParams.getParameterSpec(AlgorithmParameterSpec.class), this.random);
                this.ephemeralKP = ephemKPG.generateKeyPair();
            } catch (Exception e) {
                throw new CMSException("cannot determine MQV ephemeral key pair parameters from public key: " + e, e);
            }
        }
    }
}
