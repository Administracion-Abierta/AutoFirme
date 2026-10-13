package org.bouncycastle.jcajce.provider.asymmetric.x509;

import es.gob.afirma.core.signers.AOSignConstants;
import java.io.IOException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.PSSParameterSpec;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSASSAPSSparams;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.internal.asn1.edec.EdECObjectIdentifiers;
import org.bouncycastle.internal.asn1.misc.MiscObjectIdentifiers;
import org.bouncycastle.internal.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.jcajce.spec.EdDSAParameterSpec;
import org.bouncycastle.jcajce.util.MessageDigestUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Objects;
import org.bouncycastle.util.Properties;
import org.bouncycastle.util.encoders.Hex;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/asymmetric/x509/X509SignatureUtil.class */
class X509SignatureUtil {
    private static final Map<ASN1ObjectIdentifier, String> algNames = new HashMap();

    X509SignatureUtil() {
    }

    static boolean areEquivalentAlgorithms(AlgorithmIdentifier algorithmIdentifier, AlgorithmIdentifier algorithmIdentifier2) {
        if (!algorithmIdentifier.getAlgorithm().equals((ASN1Primitive) algorithmIdentifier2.getAlgorithm())) {
            return false;
        }
        if (Properties.isOverrideSet("org.bouncycastle.x509.allow_absent_equiv_NULL") && isAbsentOrEmptyParameters(algorithmIdentifier.getParameters()) && isAbsentOrEmptyParameters(algorithmIdentifier2.getParameters())) {
            return true;
        }
        return Objects.areEqual(algorithmIdentifier.getParameters(), algorithmIdentifier2.getParameters());
    }

    private static boolean isAbsentOrEmptyParameters(ASN1Encodable aSN1Encodable) {
        return aSN1Encodable == null || DERNull.INSTANCE.equals(aSN1Encodable);
    }

    static boolean isCompositeAlgorithm(AlgorithmIdentifier algorithmIdentifier) {
        return MiscObjectIdentifiers.id_alg_composite.equals((ASN1Primitive) algorithmIdentifier.getAlgorithm());
    }

    static void setSignatureParameters(Signature signature, ASN1Encodable aSN1Encodable) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException {
        if (isAbsentOrEmptyParameters(aSN1Encodable)) {
            return;
        }
        String algorithm = signature.getAlgorithm();
        AlgorithmParameters algorithmParameters = AlgorithmParameters.getInstance(algorithm, signature.getProvider());
        try {
            algorithmParameters.init(aSN1Encodable.toASN1Primitive().getEncoded());
            if (algorithm.endsWith("MGF1")) {
                try {
                    signature.setParameter(algorithmParameters.getParameterSpec(PSSParameterSpec.class));
                } catch (GeneralSecurityException e) {
                    throw new SignatureException("Exception extracting parameters: " + e.getMessage());
                }
            }
        } catch (IOException e2) {
            throw new SignatureException("IOException decoding parameters: " + e2.getMessage());
        }
    }

    static String getSignatureName(AlgorithmIdentifier algorithmIdentifier) {
        ASN1ObjectIdentifier algorithm = algorithmIdentifier.getAlgorithm();
        ASN1Encodable parameters = algorithmIdentifier.getParameters();
        if (!isAbsentOrEmptyParameters(parameters)) {
            if (PKCSObjectIdentifiers.id_RSASSA_PSS.equals((ASN1Primitive) algorithm)) {
                return getDigestAlgName(RSASSAPSSparams.getInstance(parameters).getHashAlgorithm().getAlgorithm()) + "withRSAandMGF1";
            }
            if (X9ObjectIdentifiers.ecdsa_with_SHA2.equals((ASN1Primitive) algorithm)) {
                return getDigestAlgName(AlgorithmIdentifier.getInstance(parameters).getAlgorithm()) + "withECDSA";
            }
        }
        String str = algNames.get(algorithm);
        return str != null ? str : findAlgName(algorithm);
    }

    private static String getDigestAlgName(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        String digestName = MessageDigestUtils.getDigestName(aSN1ObjectIdentifier);
        int iIndexOf = digestName.indexOf(45);
        return (iIndexOf <= 0 || digestName.startsWith("SHA3")) ? digestName : digestName.substring(0, iIndexOf) + digestName.substring(iIndexOf + 1);
    }

    private static String findAlgName(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        String strLookupAlg;
        String strLookupAlg2;
        Provider provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
        if (provider != null && (strLookupAlg2 = lookupAlg(provider, aSN1ObjectIdentifier)) != null) {
            return strLookupAlg2;
        }
        Provider[] providers = Security.getProviders();
        for (int i = 0; i != providers.length; i++) {
            if (provider != providers[i] && (strLookupAlg = lookupAlg(providers[i], aSN1ObjectIdentifier)) != null) {
                return strLookupAlg;
            }
        }
        return aSN1ObjectIdentifier.getId();
    }

    private static String lookupAlg(Provider provider, ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        String property = provider.getProperty("Alg.Alias.Signature." + aSN1ObjectIdentifier);
        if (property != null) {
            return property;
        }
        String property2 = provider.getProperty("Alg.Alias.Signature.OID." + aSN1ObjectIdentifier);
        if (property2 != null) {
            return property2;
        }
        return null;
    }

    static void prettyPrintSignature(byte[] bArr, StringBuffer stringBuffer, String str) {
        if (bArr.length <= 20) {
            stringBuffer.append("            Signature: ").append(Hex.toHexString(bArr)).append(str);
            return;
        }
        stringBuffer.append("            Signature: ").append(Hex.toHexString(bArr, 0, 20)).append(str);
        for (int i = 20; i < bArr.length; i += 20) {
            if (i < bArr.length - 20) {
                stringBuffer.append("                       ").append(Hex.toHexString(bArr, i, 20)).append(str);
            } else {
                stringBuffer.append("                       ").append(Hex.toHexString(bArr, i, bArr.length - i)).append(str);
            }
        }
    }

    static {
        algNames.put(EdECObjectIdentifiers.id_Ed25519, EdDSAParameterSpec.Ed25519);
        algNames.put(EdECObjectIdentifiers.id_Ed448, EdDSAParameterSpec.Ed448);
        algNames.put(OIWObjectIdentifiers.dsaWithSHA1, AOSignConstants.SIGN_ALGORITHM_SHA1WITHDSA);
        algNames.put(X9ObjectIdentifiers.id_dsa_with_sha1, AOSignConstants.SIGN_ALGORITHM_SHA1WITHDSA);
    }
}
