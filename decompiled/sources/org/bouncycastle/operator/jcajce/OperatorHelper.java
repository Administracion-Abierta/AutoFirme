package org.bouncycastle.operator.jcajce;

import es.gob.afirma.core.signers.AOSignConstants;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PSSParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.kisa.KISAObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.ntt.NTTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.jcajce.util.AlgorithmParametersUtils;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.jcajce.util.MessageDigestUtils;
import org.bouncycastle.operator.DefaultSignatureNameFinder;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.util.Integers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/operator/jcajce/OperatorHelper.class */
class OperatorHelper {
    private static final Map oids = new HashMap();
    private static final Map asymmetricWrapperAlgNames = new HashMap();
    private static final Map symmetricWrapperAlgNames = new HashMap();
    private static final Map symmetricKeyAlgNames = new HashMap();
    private static final Map symmetricWrapperKeySizes = new HashMap();
    private static DefaultSignatureNameFinder sigFinder = new DefaultSignatureNameFinder();
    private JcaJceHelper helper;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/operator/jcajce/OperatorHelper$OpCertificateException.class */
    private static class OpCertificateException extends CertificateException {
        private Throwable cause;

        public OpCertificateException(String str, Throwable th) {
            super(str);
            this.cause = th;
        }

        @Override // java.lang.Throwable
        public Throwable getCause() {
            return this.cause;
        }
    }

    OperatorHelper(JcaJceHelper jcaJceHelper) {
        this.helper = jcaJceHelper;
    }

    String getWrappingAlgorithmName(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return (String) symmetricWrapperAlgNames.get(aSN1ObjectIdentifier);
    }

    int getKeySizeInBits(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return ((Integer) symmetricWrapperKeySizes.get(aSN1ObjectIdentifier)).intValue();
    }

    KeyPairGenerator createKeyPairGenerator(ASN1ObjectIdentifier aSN1ObjectIdentifier) throws CMSException {
        if (0 != 0) {
            try {
                try {
                    return this.helper.createKeyPairGenerator(null);
                } catch (NoSuchAlgorithmException e) {
                    return this.helper.createKeyPairGenerator(aSN1ObjectIdentifier.getId());
                }
            } catch (GeneralSecurityException e2) {
                throw new CMSException("cannot create key agreement: " + e2.getMessage(), e2);
            }
        }
        return this.helper.createKeyPairGenerator(aSN1ObjectIdentifier.getId());
    }

    Cipher createCipher(ASN1ObjectIdentifier aSN1ObjectIdentifier) throws OperatorCreationException {
        try {
            return this.helper.createCipher(aSN1ObjectIdentifier.getId());
        } catch (GeneralSecurityException e) {
            throw new OperatorCreationException("cannot create cipher: " + e.getMessage(), e);
        }
    }

    KeyAgreement createKeyAgreement(ASN1ObjectIdentifier aSN1ObjectIdentifier) throws OperatorCreationException {
        if (0 != 0) {
            try {
                try {
                    return this.helper.createKeyAgreement(null);
                } catch (NoSuchAlgorithmException e) {
                    return this.helper.createKeyAgreement(aSN1ObjectIdentifier.getId());
                }
            } catch (GeneralSecurityException e2) {
                throw new OperatorCreationException("cannot create key agreement: " + e2.getMessage(), e2);
            }
        }
        return this.helper.createKeyAgreement(aSN1ObjectIdentifier.getId());
    }

    Cipher createAsymmetricWrapper(ASN1ObjectIdentifier aSN1ObjectIdentifier, Map map) throws OperatorCreationException {
        try {
            String str = null;
            if (!map.isEmpty()) {
                str = (String) map.get(aSN1ObjectIdentifier);
            }
            if (str == null) {
                str = (String) asymmetricWrapperAlgNames.get(aSN1ObjectIdentifier);
            }
            if (str != null) {
                try {
                    return this.helper.createCipher(str);
                } catch (NoSuchAlgorithmException e) {
                    if (str.equals("RSA/ECB/PKCS1Padding")) {
                        try {
                            return this.helper.createCipher("RSA/NONE/PKCS1Padding");
                        } catch (NoSuchAlgorithmException e2) {
                        }
                    }
                }
            }
            return this.helper.createCipher(aSN1ObjectIdentifier.getId());
        } catch (GeneralSecurityException e3) {
            throw new OperatorCreationException("cannot create cipher: " + e3.getMessage(), e3);
        }
    }

    Cipher createSymmetricWrapper(ASN1ObjectIdentifier aSN1ObjectIdentifier) throws OperatorCreationException {
        try {
            String str = (String) symmetricWrapperAlgNames.get(aSN1ObjectIdentifier);
            if (str != null) {
                try {
                    return this.helper.createCipher(str);
                } catch (NoSuchAlgorithmException e) {
                }
            }
            return this.helper.createCipher(aSN1ObjectIdentifier.getId());
        } catch (GeneralSecurityException e2) {
            throw new OperatorCreationException("cannot create cipher: " + e2.getMessage(), e2);
        }
    }

    AlgorithmParameters createAlgorithmParameters(AlgorithmIdentifier algorithmIdentifier) throws OperatorCreationException {
        AlgorithmParameters algorithmParametersCreateAlgorithmParameters = null;
        if (algorithmIdentifier.getAlgorithm().equals((ASN1Primitive) PKCSObjectIdentifiers.rsaEncryption)) {
            return null;
        }
        if (algorithmIdentifier.getAlgorithm().equals((ASN1Primitive) PKCSObjectIdentifiers.id_RSAES_OAEP)) {
            try {
                algorithmParametersCreateAlgorithmParameters = this.helper.createAlgorithmParameters("OAEP");
            } catch (NoSuchAlgorithmException e) {
            } catch (NoSuchProviderException e2) {
                throw new OperatorCreationException("cannot create algorithm parameters: " + e2.getMessage(), e2);
            }
        }
        if (algorithmParametersCreateAlgorithmParameters == null) {
            try {
                algorithmParametersCreateAlgorithmParameters = this.helper.createAlgorithmParameters(algorithmIdentifier.getAlgorithm().getId());
            } catch (NoSuchAlgorithmException e3) {
                return null;
            } catch (NoSuchProviderException e4) {
                throw new OperatorCreationException("cannot create algorithm parameters: " + e4.getMessage(), e4);
            }
        }
        try {
            algorithmParametersCreateAlgorithmParameters.init(algorithmIdentifier.getParameters().toASN1Primitive().getEncoded());
            return algorithmParametersCreateAlgorithmParameters;
        } catch (IOException e5) {
            throw new OperatorCreationException("cannot initialise algorithm parameters: " + e5.getMessage(), e5);
        }
    }

    MessageDigest createDigest(AlgorithmIdentifier algorithmIdentifier) throws GeneralSecurityException {
        MessageDigest messageDigestCreateMessageDigest;
        try {
            if (algorithmIdentifier.getAlgorithm().equals((ASN1Primitive) NISTObjectIdentifiers.id_shake256_len)) {
                messageDigestCreateMessageDigest = this.helper.createMessageDigest("SHAKE256-" + ASN1Integer.getInstance(algorithmIdentifier.getParameters()).getValue());
            } else {
                messageDigestCreateMessageDigest = algorithmIdentifier.getAlgorithm().equals((ASN1Primitive) NISTObjectIdentifiers.id_shake128_len) ? this.helper.createMessageDigest("SHAKE128-" + ASN1Integer.getInstance(algorithmIdentifier.getParameters()).getValue()) : this.helper.createMessageDigest(MessageDigestUtils.getDigestName(algorithmIdentifier.getAlgorithm()));
            }
        } catch (NoSuchAlgorithmException e) {
            if (oids.get(algorithmIdentifier.getAlgorithm()) == null) {
                throw e;
            }
            messageDigestCreateMessageDigest = this.helper.createMessageDigest((String) oids.get(algorithmIdentifier.getAlgorithm()));
        }
        return messageDigestCreateMessageDigest;
    }

    Signature createSignature(AlgorithmIdentifier algorithmIdentifier) throws GeneralSecurityException {
        Signature signatureCreateSignature;
        String signatureName = getSignatureName(algorithmIdentifier);
        try {
            signatureCreateSignature = this.helper.createSignature(signatureName);
        } catch (NoSuchAlgorithmException e) {
            if (!signatureName.endsWith("WITHRSAANDMGF1")) {
                throw e;
            }
            signatureCreateSignature = this.helper.createSignature(signatureName.substring(0, signatureName.indexOf(87)) + "WITHRSASSA-PSS");
        }
        if (algorithmIdentifier.getAlgorithm().equals((ASN1Primitive) PKCSObjectIdentifiers.id_RSASSA_PSS)) {
            ASN1Sequence aSN1Sequence = ASN1Sequence.getInstance(algorithmIdentifier.getParameters());
            if (notDefaultPSSParams(aSN1Sequence)) {
                try {
                    AlgorithmParameters algorithmParametersCreateAlgorithmParameters = this.helper.createAlgorithmParameters("PSS");
                    algorithmParametersCreateAlgorithmParameters.init(aSN1Sequence.getEncoded());
                    signatureCreateSignature.setParameter(algorithmParametersCreateAlgorithmParameters.getParameterSpec(PSSParameterSpec.class));
                } catch (IOException e2) {
                    throw new GeneralSecurityException("unable to process PSS parameters: " + e2.getMessage());
                }
            }
        }
        return signatureCreateSignature;
    }

    Signature createRawSignature(AlgorithmIdentifier algorithmIdentifier) {
        try {
            String signatureName = getSignatureName(algorithmIdentifier);
            String str = AOSignConstants.SIGN_FORMAT_PKCS1 + signatureName.substring(signatureName.indexOf("WITH"));
            Signature signatureCreateSignature = this.helper.createSignature(str);
            if (algorithmIdentifier.getAlgorithm().equals((ASN1Primitive) PKCSObjectIdentifiers.id_RSASSA_PSS)) {
                AlgorithmParameters algorithmParametersCreateAlgorithmParameters = this.helper.createAlgorithmParameters(str);
                AlgorithmParametersUtils.loadParameters(algorithmParametersCreateAlgorithmParameters, algorithmIdentifier.getParameters());
                signatureCreateSignature.setParameter((PSSParameterSpec) algorithmParametersCreateAlgorithmParameters.getParameterSpec(PSSParameterSpec.class));
            }
            return signatureCreateSignature;
        } catch (Exception e) {
            return null;
        }
    }

    private static String getSignatureName(AlgorithmIdentifier algorithmIdentifier) {
        return sigFinder.getAlgorithmName(algorithmIdentifier);
    }

    static String getDigestName(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        String digestName = MessageDigestUtils.getDigestName(aSN1ObjectIdentifier);
        int iIndexOf = digestName.indexOf(45);
        return (iIndexOf <= 0 || digestName.startsWith("SHA3")) ? digestName : digestName.substring(0, iIndexOf) + digestName.substring(iIndexOf + 1);
    }

    public X509Certificate convertCertificate(X509CertificateHolder x509CertificateHolder) throws CertificateException {
        try {
            return (X509Certificate) this.helper.createCertificateFactory(XMLX509Certificate.JCA_CERT_ID).generateCertificate(new ByteArrayInputStream(x509CertificateHolder.getEncoded()));
        } catch (IOException e) {
            throw new OpCertificateException("cannot get encoded form of certificate: " + e.getMessage(), e);
        } catch (NoSuchProviderException e2) {
            throw new OpCertificateException("cannot find factory provider: " + e2.getMessage(), e2);
        }
    }

    public PublicKey convertPublicKey(SubjectPublicKeyInfo subjectPublicKeyInfo) throws OperatorCreationException {
        try {
            return this.helper.createKeyFactory(subjectPublicKeyInfo.getAlgorithm().getAlgorithm().getId()).generatePublic(new X509EncodedKeySpec(subjectPublicKeyInfo.getEncoded()));
        } catch (IOException e) {
            throw new OperatorCreationException("cannot get encoded form of key: " + e.getMessage(), e);
        } catch (NoSuchAlgorithmException e2) {
            throw new OperatorCreationException("cannot create key factory: " + e2.getMessage(), e2);
        } catch (NoSuchProviderException e3) {
            throw new OperatorCreationException("cannot find factory provider: " + e3.getMessage(), e3);
        } catch (InvalidKeySpecException e4) {
            throw new OperatorCreationException("cannot create key factory: " + e4.getMessage(), e4);
        }
    }

    String getKeyAlgorithmName(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        String str = (String) symmetricKeyAlgNames.get(aSN1ObjectIdentifier);
        return str != null ? str : aSN1ObjectIdentifier.getId();
    }

    private boolean notDefaultPSSParams(ASN1Sequence aSN1Sequence) throws GeneralSecurityException {
        if (aSN1Sequence == null || aSN1Sequence.size() == 0) {
            return false;
        }
        RSASSAPSSparams rSASSAPSSparams = RSASSAPSSparams.getInstance(aSN1Sequence);
        if (rSASSAPSSparams.getMaskGenAlgorithm().getAlgorithm().equals((ASN1Primitive) PKCSObjectIdentifiers.id_mgf1) && rSASSAPSSparams.getHashAlgorithm().equals(AlgorithmIdentifier.getInstance(rSASSAPSSparams.getMaskGenAlgorithm().getParameters()))) {
            return rSASSAPSSparams.getSaltLength().intValue() != createDigest(rSASSAPSSparams.getHashAlgorithm()).getDigestLength();
        }
        return true;
    }

    static {
        oids.put(OIWObjectIdentifiers.idSHA1, AOSignConstants.DIGEST_ALGORITHM_SHA1);
        oids.put(NISTObjectIdentifiers.id_sha224, "SHA224");
        oids.put(NISTObjectIdentifiers.id_sha256, "SHA256");
        oids.put(NISTObjectIdentifiers.id_sha384, AOSignConstants.DIGEST_ALGORITHM_SHA384);
        oids.put(NISTObjectIdentifiers.id_sha512, "SHA512");
        oids.put(TeleTrusTObjectIdentifiers.ripemd128, "RIPEMD128");
        oids.put(TeleTrusTObjectIdentifiers.ripemd160, "RIPEMD160");
        oids.put(TeleTrusTObjectIdentifiers.ripemd256, "RIPEMD256");
        asymmetricWrapperAlgNames.put(PKCSObjectIdentifiers.rsaEncryption, "RSA/ECB/PKCS1Padding");
        asymmetricWrapperAlgNames.put(OIWObjectIdentifiers.elGamalAlgorithm, "Elgamal/ECB/PKCS1Padding");
        asymmetricWrapperAlgNames.put(PKCSObjectIdentifiers.id_RSAES_OAEP, "RSA/ECB/OAEPPadding");
        asymmetricWrapperAlgNames.put(CryptoProObjectIdentifiers.gostR3410_2001, "ECGOST3410");
        symmetricWrapperAlgNames.put(PKCSObjectIdentifiers.id_alg_CMS3DESwrap, "DESEDEWrap");
        symmetricWrapperAlgNames.put(PKCSObjectIdentifiers.id_alg_CMSRC2wrap, "RC2Wrap");
        symmetricWrapperAlgNames.put(NISTObjectIdentifiers.id_aes128_wrap, "AESWrap");
        symmetricWrapperAlgNames.put(NISTObjectIdentifiers.id_aes192_wrap, "AESWrap");
        symmetricWrapperAlgNames.put(NISTObjectIdentifiers.id_aes256_wrap, "AESWrap");
        symmetricWrapperAlgNames.put(NTTObjectIdentifiers.id_camellia128_wrap, "CamelliaWrap");
        symmetricWrapperAlgNames.put(NTTObjectIdentifiers.id_camellia192_wrap, "CamelliaWrap");
        symmetricWrapperAlgNames.put(NTTObjectIdentifiers.id_camellia256_wrap, "CamelliaWrap");
        symmetricWrapperAlgNames.put(KISAObjectIdentifiers.id_npki_app_cmsSeed_wrap, "SEEDWrap");
        symmetricWrapperAlgNames.put(PKCSObjectIdentifiers.des_EDE3_CBC, "DESede");
        symmetricWrapperKeySizes.put(PKCSObjectIdentifiers.id_alg_CMS3DESwrap, Integers.valueOf(192));
        symmetricWrapperKeySizes.put(NISTObjectIdentifiers.id_aes128_wrap, Integers.valueOf(128));
        symmetricWrapperKeySizes.put(NISTObjectIdentifiers.id_aes192_wrap, Integers.valueOf(192));
        symmetricWrapperKeySizes.put(NISTObjectIdentifiers.id_aes256_wrap, Integers.valueOf(256));
        symmetricWrapperKeySizes.put(NTTObjectIdentifiers.id_camellia128_wrap, Integers.valueOf(128));
        symmetricWrapperKeySizes.put(NTTObjectIdentifiers.id_camellia192_wrap, Integers.valueOf(192));
        symmetricWrapperKeySizes.put(NTTObjectIdentifiers.id_camellia256_wrap, Integers.valueOf(256));
        symmetricWrapperKeySizes.put(KISAObjectIdentifiers.id_npki_app_cmsSeed_wrap, Integers.valueOf(128));
        symmetricWrapperKeySizes.put(PKCSObjectIdentifiers.des_EDE3_CBC, Integers.valueOf(192));
        symmetricKeyAlgNames.put(NISTObjectIdentifiers.aes, "AES");
        symmetricKeyAlgNames.put(NISTObjectIdentifiers.id_aes128_CBC, "AES");
        symmetricKeyAlgNames.put(NISTObjectIdentifiers.id_aes192_CBC, "AES");
        symmetricKeyAlgNames.put(NISTObjectIdentifiers.id_aes256_CBC, "AES");
        symmetricKeyAlgNames.put(PKCSObjectIdentifiers.des_EDE3_CBC, "DESede");
        symmetricKeyAlgNames.put(PKCSObjectIdentifiers.RC2_CBC, "RC2");
    }
}
