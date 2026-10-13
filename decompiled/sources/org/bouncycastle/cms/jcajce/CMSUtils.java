package org.bouncycastle.cms.jcajce;

import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.iso.ISOIECObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.sec.SECObjectIdentifiers;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.jcajce.util.AlgorithmParametersUtils;
import org.bouncycastle.jcajce.util.AnnotatedPrivateKey;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorCreationException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/jcajce/CMSUtils.class */
class CMSUtils {
    private static final Set mqvAlgs = new HashSet();
    private static final Set ecAlgs = new HashSet();
    private static final Set gostAlgs = new HashSet();
    private static final Map asymmetricWrapperAlgNames = new HashMap();
    private static Map<ASN1ObjectIdentifier, String> wrapAlgNames = new HashMap();

    CMSUtils() {
    }

    static boolean isMQV(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return mqvAlgs.contains(aSN1ObjectIdentifier);
    }

    static boolean isEC(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return ecAlgs.contains(aSN1ObjectIdentifier);
    }

    static boolean isGOST(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return gostAlgs.contains(aSN1ObjectIdentifier);
    }

    static boolean isRFC2631(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return aSN1ObjectIdentifier.equals((ASN1Primitive) PKCSObjectIdentifiers.id_alg_ESDH) || aSN1ObjectIdentifier.equals((ASN1Primitive) PKCSObjectIdentifiers.id_alg_SSDH);
    }

    static String getWrapAlgorithmName(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return wrapAlgNames.get(aSN1ObjectIdentifier);
    }

    static PrivateKey cleanPrivateKey(PrivateKey privateKey) {
        return privateKey instanceof AnnotatedPrivateKey ? cleanPrivateKey(((AnnotatedPrivateKey) privateKey).getKey()) : privateKey;
    }

    static IssuerAndSerialNumber getIssuerAndSerialNumber(X509Certificate x509Certificate) throws CertificateEncodingException {
        return new IssuerAndSerialNumber(Certificate.getInstance(x509Certificate.getEncoded()).getIssuer(), x509Certificate.getSerialNumber());
    }

    static byte[] getSubjectKeyId(X509Certificate x509Certificate) {
        byte[] extensionValue = x509Certificate.getExtensionValue(Extension.subjectKeyIdentifier.getId());
        if (extensionValue != null) {
            return ASN1OctetString.getInstance(ASN1OctetString.getInstance(extensionValue).getOctets()).getOctets();
        }
        return null;
    }

    static EnvelopedDataHelper createContentHelper(Provider provider) {
        return provider != null ? new EnvelopedDataHelper(new ProviderJcaJceExtHelper(provider)) : new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
    }

    static EnvelopedDataHelper createContentHelper(String str) {
        return str != null ? new EnvelopedDataHelper(new NamedJcaJceExtHelper(str)) : new EnvelopedDataHelper(new DefaultJcaJceExtHelper());
    }

    static ASN1Encodable extractParameters(AlgorithmParameters algorithmParameters) throws CMSException {
        try {
            return AlgorithmParametersUtils.extractParameters(algorithmParameters);
        } catch (IOException e) {
            throw new CMSException("cannot extract parameters: " + e.getMessage(), e);
        }
    }

    static void loadParameters(AlgorithmParameters algorithmParameters, ASN1Encodable aSN1Encodable) throws CMSException {
        try {
            AlgorithmParametersUtils.loadParameters(algorithmParameters, aSN1Encodable);
        } catch (IOException e) {
            throw new CMSException("error encoding algorithm parameters.", e);
        }
    }

    static Key getJceKey(GenericKey genericKey) {
        if (genericKey.getRepresentation() instanceof Key) {
            return (Key) genericKey.getRepresentation();
        }
        if (genericKey.getRepresentation() instanceof byte[]) {
            return new SecretKeySpec((byte[]) genericKey.getRepresentation(), "ENC");
        }
        throw new IllegalArgumentException("unknown generic key type");
    }

    static Cipher createAsymmetricWrapper(JcaJceHelper jcaJceHelper, ASN1ObjectIdentifier aSN1ObjectIdentifier, Map map) throws OperatorCreationException {
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
                    return jcaJceHelper.createCipher(str);
                } catch (NoSuchAlgorithmException e) {
                    if (str.equals("RSA/ECB/PKCS1Padding")) {
                        try {
                            return jcaJceHelper.createCipher("RSA/NONE/PKCS1Padding");
                        } catch (NoSuchAlgorithmException e2) {
                        }
                    }
                }
            }
            return jcaJceHelper.createCipher(aSN1ObjectIdentifier.getId());
        } catch (GeneralSecurityException e3) {
            throw new OperatorCreationException("cannot create cipher: " + e3.getMessage(), e3);
        }
    }

    public static int getKekSize(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        if (aSN1ObjectIdentifier.equals((ASN1Primitive) CMSAlgorithm.AES256_WRAP)) {
            return 32;
        }
        if (aSN1ObjectIdentifier.equals((ASN1Primitive) CMSAlgorithm.AES128_WRAP)) {
            return 16;
        }
        if (aSN1ObjectIdentifier.equals((ASN1Primitive) CMSAlgorithm.AES192_WRAP)) {
            return 24;
        }
        throw new IllegalArgumentException("unknown wrap algorithm");
    }

    static {
        wrapAlgNames.put(CMSAlgorithm.AES128_WRAP, "AESWRAP");
        wrapAlgNames.put(CMSAlgorithm.AES192_WRAP, "AESWRAP");
        wrapAlgNames.put(CMSAlgorithm.AES256_WRAP, "AESWRAP");
        mqvAlgs.add(X9ObjectIdentifiers.mqvSinglePass_sha1kdf_scheme);
        mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha224kdf_scheme);
        mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha256kdf_scheme);
        mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha384kdf_scheme);
        mqvAlgs.add(SECObjectIdentifiers.mqvSinglePass_sha512kdf_scheme);
        ecAlgs.add(X9ObjectIdentifiers.dhSinglePass_cofactorDH_sha1kdf_scheme);
        ecAlgs.add(X9ObjectIdentifiers.dhSinglePass_stdDH_sha1kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha224kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha224kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha256kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha256kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha384kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha384kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_cofactorDH_sha512kdf_scheme);
        ecAlgs.add(SECObjectIdentifiers.dhSinglePass_stdDH_sha512kdf_scheme);
        gostAlgs.add(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_ESDH);
        gostAlgs.add(CryptoProObjectIdentifiers.gostR3410_2001);
        gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_256);
        gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_512);
        gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256);
        gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512);
        asymmetricWrapperAlgNames.put(PKCSObjectIdentifiers.rsaEncryption, "RSA/ECB/PKCS1Padding");
        asymmetricWrapperAlgNames.put(OIWObjectIdentifiers.elGamalAlgorithm, "Elgamal/ECB/PKCS1Padding");
        asymmetricWrapperAlgNames.put(PKCSObjectIdentifiers.id_RSAES_OAEP, "RSA/ECB/OAEPPadding");
        asymmetricWrapperAlgNames.put(CryptoProObjectIdentifiers.gostR3410_2001, "ECGOST3410");
        asymmetricWrapperAlgNames.put(ISOIECObjectIdentifiers.id_kem_rsa, "RSA-KTS-KEM-KWS");
    }
}
