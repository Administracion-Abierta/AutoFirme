package org.bouncycastle.operator.jcajce;

import es.gob.afirma.core.signers.AOSignConstants;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Provider;
import java.security.ProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.security.interfaces.ECPublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.MGF1ParameterSpec;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.asn1.cryptopro.Gost2814789EncryptedKey;
import org.bouncycastle.asn1.cryptopro.GostR3410KeyTransport;
import org.bouncycastle.asn1.cryptopro.GostR3410TransportParameters;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.RSAESOAEPparams;
import org.bouncycastle.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.jcajce.spec.GOST28147WrapParameterSpec;
import org.bouncycastle.jcajce.spec.UserKeyingMaterialSpec;
import org.bouncycastle.jcajce.util.DefaultJcaJceHelper;
import org.bouncycastle.jcajce.util.NamedJcaJceHelper;
import org.bouncycastle.jcajce.util.ProviderJcaJceHelper;
import org.bouncycastle.operator.AsymmetricKeyWrapper;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/operator/jcajce/JceAsymmetricKeyWrapper.class */
public class JceAsymmetricKeyWrapper extends AsymmetricKeyWrapper {
    private static final Set gostAlgs = new HashSet();
    private OperatorHelper helper;
    private Map extraMappings;
    private PublicKey publicKey;
    private SecureRandom random;
    private static final Map digests;

    static boolean isGOST(ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        return gostAlgs.contains(aSN1ObjectIdentifier);
    }

    public JceAsymmetricKeyWrapper(PublicKey publicKey) {
        super(SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()).getAlgorithm());
        this.helper = new OperatorHelper(new DefaultJcaJceHelper());
        this.extraMappings = new HashMap();
        this.publicKey = publicKey;
    }

    public JceAsymmetricKeyWrapper(X509Certificate x509Certificate) {
        this(x509Certificate.getPublicKey());
    }

    public JceAsymmetricKeyWrapper(AlgorithmIdentifier algorithmIdentifier, PublicKey publicKey) {
        super(algorithmIdentifier);
        this.helper = new OperatorHelper(new DefaultJcaJceHelper());
        this.extraMappings = new HashMap();
        this.publicKey = publicKey;
    }

    public JceAsymmetricKeyWrapper(AlgorithmParameters algorithmParameters, PublicKey publicKey) throws InvalidParameterSpecException {
        super(extractAlgID(publicKey, algorithmParameters.getParameterSpec(AlgorithmParameterSpec.class)));
        this.helper = new OperatorHelper(new DefaultJcaJceHelper());
        this.extraMappings = new HashMap();
        this.publicKey = publicKey;
    }

    public JceAsymmetricKeyWrapper(AlgorithmParameterSpec algorithmParameterSpec, PublicKey publicKey) {
        super(extractAlgID(publicKey, algorithmParameterSpec));
        this.helper = new OperatorHelper(new DefaultJcaJceHelper());
        this.extraMappings = new HashMap();
        this.publicKey = publicKey;
    }

    public JceAsymmetricKeyWrapper setProvider(Provider provider) {
        this.helper = new OperatorHelper(new ProviderJcaJceHelper(provider));
        return this;
    }

    public JceAsymmetricKeyWrapper setProvider(String str) {
        this.helper = new OperatorHelper(new NamedJcaJceHelper(str));
        return this;
    }

    public JceAsymmetricKeyWrapper setSecureRandom(SecureRandom secureRandom) {
        this.random = secureRandom;
        return this;
    }

    public JceAsymmetricKeyWrapper setAlgorithmMapping(ASN1ObjectIdentifier aSN1ObjectIdentifier, String str) {
        this.extraMappings.put(aSN1ObjectIdentifier, str);
        return this;
    }

    @Override // org.bouncycastle.operator.KeyWrapper
    public byte[] generateWrappedKey(GenericKey genericKey) throws OperatorException {
        byte[] bArrDoFinal = null;
        if (!isGOST(getAlgorithmIdentifier().getAlgorithm())) {
            Cipher cipherCreateAsymmetricWrapper = this.helper.createAsymmetricWrapper(getAlgorithmIdentifier().getAlgorithm(), this.extraMappings);
            AlgorithmParameters algorithmParametersCreateAlgorithmParameters = null;
            try {
                if (!getAlgorithmIdentifier().getAlgorithm().equals((ASN1Primitive) OIWObjectIdentifiers.elGamalAlgorithm)) {
                    algorithmParametersCreateAlgorithmParameters = this.helper.createAlgorithmParameters(getAlgorithmIdentifier());
                }
                if (algorithmParametersCreateAlgorithmParameters != null) {
                    cipherCreateAsymmetricWrapper.init(3, this.publicKey, algorithmParametersCreateAlgorithmParameters, this.random);
                } else {
                    cipherCreateAsymmetricWrapper.init(3, this.publicKey, this.random);
                }
                bArrDoFinal = cipherCreateAsymmetricWrapper.wrap(OperatorUtils.getJceKey(genericKey));
            } catch (IllegalStateException e) {
            } catch (UnsupportedOperationException e2) {
            } catch (InvalidKeyException e3) {
            } catch (GeneralSecurityException e4) {
            } catch (ProviderException e5) {
            }
            if (bArrDoFinal == null) {
                try {
                    if (algorithmParametersCreateAlgorithmParameters != null) {
                        cipherCreateAsymmetricWrapper.init(1, this.publicKey, algorithmParametersCreateAlgorithmParameters, this.random);
                    } else {
                        cipherCreateAsymmetricWrapper.init(1, this.publicKey, this.random);
                    }
                    bArrDoFinal = cipherCreateAsymmetricWrapper.doFinal(OperatorUtils.getJceKey(genericKey).getEncoded());
                } catch (InvalidKeyException e6) {
                    throw new OperatorException("unable to encrypt contents key", e6);
                } catch (GeneralSecurityException e7) {
                    throw new OperatorException("unable to encrypt contents key", e7);
                }
            }
            return bArrDoFinal;
        }
        try {
            this.random = CryptoServicesRegistrar.getSecureRandom(this.random);
            KeyPairGenerator keyPairGeneratorCreateKeyPairGenerator = this.helper.createKeyPairGenerator(getAlgorithmIdentifier().getAlgorithm());
            keyPairGeneratorCreateKeyPairGenerator.initialize(((ECPublicKey) this.publicKey).getParams(), this.random);
            KeyPair keyPairGenerateKeyPair = keyPairGeneratorCreateKeyPairGenerator.generateKeyPair();
            byte[] bArr = new byte[8];
            this.random.nextBytes(bArr);
            SubjectPublicKeyInfo subjectPublicKeyInfo = SubjectPublicKeyInfo.getInstance(keyPairGenerateKeyPair.getPublic().getEncoded());
            GostR3410TransportParameters gostR3410TransportParameters = subjectPublicKeyInfo.getAlgorithm().getAlgorithm().on(RosstandartObjectIdentifiers.id_tc26) ? new GostR3410TransportParameters(RosstandartObjectIdentifiers.id_tc26_gost_28147_param_Z, subjectPublicKeyInfo, bArr) : new GostR3410TransportParameters(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_A_ParamSet, subjectPublicKeyInfo, bArr);
            KeyAgreement keyAgreementCreateKeyAgreement = this.helper.createKeyAgreement(getAlgorithmIdentifier().getAlgorithm());
            keyAgreementCreateKeyAgreement.init(keyPairGenerateKeyPair.getPrivate(), new UserKeyingMaterialSpec(gostR3410TransportParameters.getUkm()));
            keyAgreementCreateKeyAgreement.doPhase(this.publicKey, true);
            SecretKey secretKeyGenerateSecret = keyAgreementCreateKeyAgreement.generateSecret(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_KeyWrap.getId());
            byte[] encoded = OperatorUtils.getJceKey(genericKey).getEncoded();
            Cipher cipherCreateCipher = this.helper.createCipher(CryptoProObjectIdentifiers.id_Gost28147_89_CryptoPro_KeyWrap);
            cipherCreateCipher.init(3, secretKeyGenerateSecret, new GOST28147WrapParameterSpec(gostR3410TransportParameters.getEncryptionParamSet(), gostR3410TransportParameters.getUkm()));
            byte[] bArrWrap = cipherCreateCipher.wrap(new SecretKeySpec(encoded, "GOST"));
            return new GostR3410KeyTransport(new Gost2814789EncryptedKey(Arrays.copyOfRange(bArrWrap, 0, 32), Arrays.copyOfRange(bArrWrap, 32, 36)), gostR3410TransportParameters).getEncoded();
        } catch (Exception e8) {
            throw new OperatorException("exception wrapping key: " + e8.getMessage(), e8);
        }
    }

    private static AlgorithmIdentifier extractAlgID(PublicKey publicKey, AlgorithmParameterSpec algorithmParameterSpec) {
        if (!(algorithmParameterSpec instanceof OAEPParameterSpec)) {
            throw new IllegalArgumentException("unknown spec: " + algorithmParameterSpec.getClass().getName());
        }
        OAEPParameterSpec oAEPParameterSpec = (OAEPParameterSpec) algorithmParameterSpec;
        if (!oAEPParameterSpec.getMGFAlgorithm().equals(OAEPParameterSpec.DEFAULT.getMGFAlgorithm())) {
            throw new IllegalArgumentException("unknown MGF: " + oAEPParameterSpec.getMGFAlgorithm());
        }
        if (oAEPParameterSpec.getPSource() instanceof PSource.PSpecified) {
            return new AlgorithmIdentifier(PKCSObjectIdentifiers.id_RSAES_OAEP, new RSAESOAEPparams(getDigest(oAEPParameterSpec.getDigestAlgorithm()), new AlgorithmIdentifier(PKCSObjectIdentifiers.id_mgf1, getDigest(((MGF1ParameterSpec) oAEPParameterSpec.getMGFParameters()).getDigestAlgorithm())), new AlgorithmIdentifier(PKCSObjectIdentifiers.id_pSpecified, new DEROctetString(((PSource.PSpecified) oAEPParameterSpec.getPSource()).getValue()))));
        }
        throw new IllegalArgumentException("unknown PSource: " + oAEPParameterSpec.getPSource().getAlgorithm());
    }

    private static AlgorithmIdentifier getDigest(String str) {
        AlgorithmIdentifier algorithmIdentifier = (AlgorithmIdentifier) digests.get(str);
        if (algorithmIdentifier != null) {
            return algorithmIdentifier;
        }
        throw new IllegalArgumentException("unknown digest name: " + str);
    }

    static {
        gostAlgs.add(CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_ESDH);
        gostAlgs.add(CryptoProObjectIdentifiers.gostR3410_2001);
        gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_256);
        gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_512);
        gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256);
        gostAlgs.add(RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512);
        digests = new HashMap();
        digests.put(AOSignConstants.DIGEST_ALGORITHM_SHA1, new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, DERNull.INSTANCE));
        digests.put("SHA-1", new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1, DERNull.INSTANCE));
        digests.put("SHA224", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha224, DERNull.INSTANCE));
        digests.put("SHA-224", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha224, DERNull.INSTANCE));
        digests.put("SHA256", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256, DERNull.INSTANCE));
        digests.put("SHA-256", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256, DERNull.INSTANCE));
        digests.put(AOSignConstants.DIGEST_ALGORITHM_SHA384, new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha384, DERNull.INSTANCE));
        digests.put("SHA-384", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha384, DERNull.INSTANCE));
        digests.put("SHA512", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512, DERNull.INSTANCE));
        digests.put("SHA-512", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512, DERNull.INSTANCE));
        digests.put("SHA512/224", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512_224, DERNull.INSTANCE));
        digests.put(MessageDigestAlgorithms.SHA_512_224, new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512_224, DERNull.INSTANCE));
        digests.put("SHA-512(224)", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512_224, DERNull.INSTANCE));
        digests.put("SHA512/256", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512_256, DERNull.INSTANCE));
        digests.put("SHA-512/256", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512_256, DERNull.INSTANCE));
        digests.put("SHA-512(256)", new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512_256, DERNull.INSTANCE));
    }
}
