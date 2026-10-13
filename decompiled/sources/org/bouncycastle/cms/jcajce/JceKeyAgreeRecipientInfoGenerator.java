package org.bouncycastle.cms.jcajce;

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
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.cms.KeyAgreeRecipientIdentifier;
import org.bouncycastle.asn1.cms.OriginatorPublicKey;
import org.bouncycastle.asn1.cms.RecipientEncryptedKey;
import org.bouncycastle.asn1.cms.RecipientKeyIdentifier;
import org.bouncycastle.asn1.cms.ecc.MQVuserKeyingMaterial;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.Gost2814789EncryptedKey;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.KeyAgreeRecipientInfoGenerator;
import org.bouncycastle.jcajce.spec.GOST28147WrapParameterSpec;
import org.bouncycastle.jcajce.spec.MQVParameterSpec;
import org.bouncycastle.jcajce.spec.UserKeyingMaterialSpec;
import org.bouncycastle.operator.DefaultSecretKeySizeProvider;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.SecretKeySizeProvider;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/jcajce/JceKeyAgreeRecipientInfoGenerator.class */
public class JceKeyAgreeRecipientInfoGenerator extends KeyAgreeRecipientInfoGenerator {
    private SecretKeySizeProvider keySizeProvider;
    private List recipientIDs;
    private List recipientKeys;
    private PublicKey senderPublicKey;
    private PrivateKey senderPrivateKey;
    private EnvelopedDataHelper helper;
    private EnvelopedDataHelper wrappingHelper;
    private SecureRandom random;
    private KeyPair ephemeralKP;
    private byte[] userKeyingMaterial;
    private static KeyMaterialGenerator ecc_cms_Generator = new RFC5753KeyMaterialGenerator();

    public JceKeyAgreeRecipientInfoGenerator(ASN1ObjectIdentifier aSN1ObjectIdentifier, PrivateKey privateKey, PublicKey publicKey, ASN1ObjectIdentifier aSN1ObjectIdentifier2) {
        super(aSN1ObjectIdentifier, SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()), aSN1ObjectIdentifier2);
        this.keySizeProvider = new DefaultSecretKeySizeProvider();
        this.recipientIDs = new ArrayList();
        this.recipientKeys = new ArrayList();
        this.helper = new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
        this.wrappingHelper = null;
        this.senderPublicKey = publicKey;
        this.senderPrivateKey = CMSUtils.cleanPrivateKey(privateKey);
    }

    public JceKeyAgreeRecipientInfoGenerator setUserKeyingMaterial(byte[] bArr) {
        this.userKeyingMaterial = Arrays.clone(bArr);
        return this;
    }

    public JceKeyAgreeRecipientInfoGenerator setProvider(Provider provider) {
        this.helper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(provider));
        return this;
    }

    public JceKeyAgreeRecipientInfoGenerator setProvider(String str) {
        this.helper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(str));
        return this;
    }

    public JceKeyAgreeRecipientInfoGenerator setKeyWrappingProvider(Provider provider) {
        this.wrappingHelper = new EnvelopedDataHelper(new ProviderJcaJceExtHelper(provider));
        return this;
    }

    public JceKeyAgreeRecipientInfoGenerator setKeyWrappingProvider(String str) {
        this.wrappingHelper = new EnvelopedDataHelper(new NamedJcaJceExtHelper(str));
        return this;
    }

    public JceKeyAgreeRecipientInfoGenerator setSecureRandom(SecureRandom secureRandom) {
        this.random = secureRandom;
        return this;
    }

    public JceKeyAgreeRecipientInfoGenerator addRecipient(X509Certificate x509Certificate) throws CertificateEncodingException {
        this.recipientIDs.add(new KeyAgreeRecipientIdentifier(CMSUtils.getIssuerAndSerialNumber(x509Certificate)));
        this.recipientKeys.add(x509Certificate.getPublicKey());
        return this;
    }

    public JceKeyAgreeRecipientInfoGenerator addRecipient(byte[] bArr, PublicKey publicKey) throws CertificateEncodingException {
        this.recipientIDs.add(new KeyAgreeRecipientIdentifier(new RecipientKeyIdentifier(bArr)));
        this.recipientKeys.add(publicKey);
        return this;
    }

    @Override // org.bouncycastle.cms.KeyAgreeRecipientInfoGenerator
    public ASN1Sequence generateRecipientEncryptedKeys(AlgorithmIdentifier algorithmIdentifier, AlgorithmIdentifier algorithmIdentifier2, GenericKey genericKey) throws CMSException {
        AlgorithmParameterSpec userKeyingMaterialSpec;
        DEROctetString dEROctetString;
        if (this.recipientIDs.isEmpty()) {
            throw new CMSException("No recipients associated with generator - use addRecipient()");
        }
        init(algorithmIdentifier.getAlgorithm());
        PrivateKey privateKey = this.senderPrivateKey;
        ASN1ObjectIdentifier algorithm = algorithmIdentifier.getAlgorithm();
        ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
        for (int i = 0; i != this.recipientIDs.size(); i++) {
            PublicKey publicKey = (PublicKey) this.recipientKeys.get(i);
            KeyAgreeRecipientIdentifier keyAgreeRecipientIdentifier = (KeyAgreeRecipientIdentifier) this.recipientIDs.get(i);
            try {
                ASN1ObjectIdentifier algorithm2 = algorithmIdentifier2.getAlgorithm();
                if (CMSUtils.isMQV(algorithm)) {
                    userKeyingMaterialSpec = new MQVParameterSpec(this.ephemeralKP, publicKey, this.userKeyingMaterial);
                } else if (CMSUtils.isEC(algorithm)) {
                    userKeyingMaterialSpec = new UserKeyingMaterialSpec(ecc_cms_Generator.generateKDFMaterial(algorithmIdentifier2, this.keySizeProvider.getKeySize(algorithm2), this.userKeyingMaterial));
                } else if (!CMSUtils.isRFC2631(algorithm)) {
                    if (!CMSUtils.isGOST(algorithm)) {
                        throw new CMSException("Unknown key agreement algorithm: " + algorithm);
                    }
                    if (this.userKeyingMaterial == null) {
                        throw new CMSException("User keying material must be set for static keys.");
                    }
                    userKeyingMaterialSpec = new UserKeyingMaterialSpec(this.userKeyingMaterial);
                } else if (this.userKeyingMaterial != null) {
                    userKeyingMaterialSpec = new UserKeyingMaterialSpec(this.userKeyingMaterial);
                } else {
                    if (algorithm.equals((ASN1Primitive) PKCSObjectIdentifiers.id_alg_SSDH)) {
                        throw new CMSException("User keying material must be set for static keys.");
                    }
                    userKeyingMaterialSpec = null;
                }
                KeyAgreement keyAgreementCreateKeyAgreement = this.helper.createKeyAgreement(algorithm);
                keyAgreementCreateKeyAgreement.init(privateKey, userKeyingMaterialSpec, this.random);
                keyAgreementCreateKeyAgreement.doPhase(publicKey, true);
                SecretKey secretKeyGenerateSecret = keyAgreementCreateKeyAgreement.generateSecret(algorithm2.getId());
                EnvelopedDataHelper envelopedDataHelper = this.wrappingHelper != null ? this.wrappingHelper : this.helper;
                Cipher cipherCreateCipher = envelopedDataHelper.createCipher(algorithm2);
                if (algorithm2.equals((ASN1Primitive) CryptoProObjectIdentifiers.id_Gost28147_89_None_KeyWrap) || algorithm2.equals((ASN1Primitive) CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_KeyWrap)) {
                    cipherCreateCipher.init(3, secretKeyGenerateSecret, new GOST28147WrapParameterSpec(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_A_ParamSet, this.userKeyingMaterial));
                    byte[] bArrWrap = cipherCreateCipher.wrap(envelopedDataHelper.getJceKey(genericKey));
                    dEROctetString = new DEROctetString(new Gost2814789EncryptedKey(Arrays.copyOfRange(bArrWrap, 0, bArrWrap.length - 4), Arrays.copyOfRange(bArrWrap, bArrWrap.length - 4, bArrWrap.length)).getEncoded("DER"));
                } else {
                    cipherCreateCipher.init(3, secretKeyGenerateSecret, this.random);
                    dEROctetString = new DEROctetString(cipherCreateCipher.wrap(envelopedDataHelper.getJceKey(genericKey)));
                }
                aSN1EncodableVector.add(new RecipientEncryptedKey(keyAgreeRecipientIdentifier, dEROctetString));
            } catch (IOException e) {
                throw new CMSException("unable to encode wrapped key: " + e.getMessage(), e);
            } catch (GeneralSecurityException e2) {
                throw new CMSException("cannot perform agreement step: " + e2.getMessage(), e2);
            }
        }
        return new DERSequence(aSN1EncodableVector);
    }

    @Override // org.bouncycastle.cms.KeyAgreeRecipientInfoGenerator
    protected byte[] getUserKeyingMaterial(AlgorithmIdentifier algorithmIdentifier) throws CMSException {
        init(algorithmIdentifier.getAlgorithm());
        if (this.ephemeralKP == null) {
            return this.userKeyingMaterial;
        }
        OriginatorPublicKey originatorPublicKeyCreateOriginatorPublicKey = createOriginatorPublicKey(SubjectPublicKeyInfo.getInstance(this.ephemeralKP.getPublic().getEncoded()));
        try {
            return this.userKeyingMaterial != null ? new MQVuserKeyingMaterial(originatorPublicKeyCreateOriginatorPublicKey, new DEROctetString(this.userKeyingMaterial)).getEncoded() : new MQVuserKeyingMaterial(originatorPublicKeyCreateOriginatorPublicKey, null).getEncoded();
        } catch (IOException e) {
            throw new CMSException("unable to encode user keying material: " + e.getMessage(), e);
        }
    }

    private void init(ASN1ObjectIdentifier aSN1ObjectIdentifier) throws CMSException {
        if (this.random == null) {
            this.random = new SecureRandom();
        }
        if (CMSUtils.isMQV(aSN1ObjectIdentifier) && this.ephemeralKP == null) {
            try {
                SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(this.senderPublicKey.getEncoded());
                AlgorithmParameters algorithmParametersCreateAlgorithmParameters = this.helper.createAlgorithmParameters(aSN1ObjectIdentifier);
                algorithmParametersCreateAlgorithmParameters.init(subjectPublicKeyInfo.getAlgorithm().getParameters().toASN1Primitive().getEncoded());
                KeyPairGenerator keyPairGeneratorCreateKeyPairGenerator = this.helper.createKeyPairGenerator(aSN1ObjectIdentifier);
                keyPairGeneratorCreateKeyPairGenerator.initialize(algorithmParametersCreateAlgorithmParameters.getParameterSpec(AlgorithmParameterSpec.class), this.random);
                this.ephemeralKP = keyPairGeneratorCreateKeyPairGenerator.generateKeyPair();
            } catch (Exception e) {
                throw new CMSException("cannot determine MQV ephemeral key pair parameters from public key: " + e, e);
            }
        }
    }
}
