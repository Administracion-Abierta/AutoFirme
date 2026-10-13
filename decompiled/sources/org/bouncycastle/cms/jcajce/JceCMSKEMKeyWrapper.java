package org.bouncycastle.cms.jcajce;

import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.cms.CMSORIforKEMOtherInfo;
import org.bouncycastle.asn1.iso.ISOIECObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x9.X9ObjectIdentifiers;
import org.bouncycastle.cms.KEMKeyWrapper;
import org.bouncycastle.jcajce.spec.KTSParameterSpec;
import org.bouncycastle.operator.DefaultKemEncapsulationLengthProvider;
import org.bouncycastle.operator.GenericKey;
import org.bouncycastle.operator.KemEncapsulationLengthProvider;
import org.bouncycastle.operator.OperatorException;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/jcajce/JceCMSKEMKeyWrapper.class */
class JceCMSKEMKeyWrapper extends KEMKeyWrapper {
    private final KemEncapsulationLengthProvider kemEncLenProvider;
    private final AlgorithmIdentifier symWrapAlgorithm;
    private final int kekLength;
    private JcaJceExtHelper helper;
    private Map extraMappings;
    private PublicKey publicKey;
    private SecureRandom random;
    private AlgorithmIdentifier kdfAlgorithm;
    private byte[] encapsulation;

    public JceCMSKEMKeyWrapper(PublicKey publicKey, ASN1ObjectIdentifier aSN1ObjectIdentifier) {
        super(publicKey instanceof RSAPublicKey ? new AlgorithmIdentifier(ISOIECObjectIdentifiers.id_kem_rsa) : SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()).getAlgorithm());
        this.kemEncLenProvider = new DefaultKemEncapsulationLengthProvider();
        this.helper = new DefaultJcaJceExtHelper();
        this.extraMappings = new HashMap();
        this.kdfAlgorithm = new AlgorithmIdentifier(X9ObjectIdentifiers.id_kdf_kdf3, new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256, DERNull.INSTANCE));
        this.publicKey = publicKey;
        this.symWrapAlgorithm = new AlgorithmIdentifier(aSN1ObjectIdentifier);
        this.kekLength = CMSUtils.getKekSize(aSN1ObjectIdentifier);
    }

    public JceCMSKEMKeyWrapper setProvider(Provider provider) {
        this.helper = new ProviderJcaJceExtHelper(provider);
        return this;
    }

    public JceCMSKEMKeyWrapper setProvider(String str) {
        this.helper = new NamedJcaJceExtHelper(str);
        return this;
    }

    public JceCMSKEMKeyWrapper setKDF(AlgorithmIdentifier algorithmIdentifier) {
        this.kdfAlgorithm = algorithmIdentifier;
        return this;
    }

    public JceCMSKEMKeyWrapper setSecureRandom(SecureRandom secureRandom) {
        this.random = secureRandom;
        return this;
    }

    public JceCMSKEMKeyWrapper setAlgorithmMapping(ASN1ObjectIdentifier aSN1ObjectIdentifier, String str) {
        this.extraMappings.put(aSN1ObjectIdentifier, str);
        return this;
    }

    @Override // org.bouncycastle.cms.KEMKeyWrapper
    public byte[] getEncapsulation() {
        return this.encapsulation;
    }

    @Override // org.bouncycastle.cms.KEMKeyWrapper
    public AlgorithmIdentifier getKdfAlgorithmIdentifier() {
        return this.kdfAlgorithm;
    }

    @Override // org.bouncycastle.cms.KEMKeyWrapper
    public int getKekLength() {
        return this.kekLength;
    }

    @Override // org.bouncycastle.cms.KEMKeyWrapper
    public AlgorithmIdentifier getWrapAlgorithmIdentifier() {
        return this.symWrapAlgorithm;
    }

    @Override // org.bouncycastle.operator.KeyWrapper
    public byte[] generateWrappedKey(GenericKey genericKey) throws OperatorException {
        try {
            byte[] encoded = new CMSORIforKEMOtherInfo(this.symWrapAlgorithm, this.kekLength).getEncoded();
            if (this.publicKey instanceof RSAPublicKey) {
                Cipher cipherCreateAsymmetricWrapper = CMSUtils.createAsymmetricWrapper(this.helper, getAlgorithmIdentifier().getAlgorithm(), new HashMap());
                try {
                    cipherCreateAsymmetricWrapper.init(3, this.publicKey, new KTSParameterSpec.Builder(CMSUtils.getWrapAlgorithmName(this.symWrapAlgorithm.getAlgorithm()), this.kekLength * 8, encoded).withKdfAlgorithm(this.kdfAlgorithm).build(), this.random);
                    byte[] bArrWrap = cipherCreateAsymmetricWrapper.wrap(CMSUtils.getJceKey(genericKey));
                    int iBitLength = (((RSAPublicKey) this.publicKey).getModulus().bitLength() + 7) / 8;
                    this.encapsulation = Arrays.copyOfRange(bArrWrap, 0, iBitLength);
                    return Arrays.copyOfRange(bArrWrap, iBitLength, bArrWrap.length);
                } catch (Exception e) {
                    throw new OperatorException("Unable to wrap contents key: " + e.getMessage(), e);
                }
            }
            Cipher cipherCreateAsymmetricWrapper2 = CMSUtils.createAsymmetricWrapper(this.helper, getAlgorithmIdentifier().getAlgorithm(), new HashMap());
            try {
                cipherCreateAsymmetricWrapper2.init(3, this.publicKey, new KTSParameterSpec.Builder(CMSUtils.getWrapAlgorithmName(this.symWrapAlgorithm.getAlgorithm()), this.kekLength * 8, encoded).withKdfAlgorithm(this.kdfAlgorithm).build(), this.random);
                byte[] bArrWrap2 = cipherCreateAsymmetricWrapper2.wrap(CMSUtils.getJceKey(genericKey));
                int kemEncLength = getKemEncLength(this.publicKey);
                this.encapsulation = Arrays.copyOfRange(bArrWrap2, 0, kemEncLength);
                return Arrays.copyOfRange(bArrWrap2, kemEncLength, bArrWrap2.length);
            } catch (Exception e2) {
                throw new OperatorException("Unable to wrap contents key: " + e2.getMessage(), e2);
            }
        } catch (Exception e3) {
            throw new OperatorException("unable to wrap contents key: " + e3.getMessage(), e3);
        }
        throw new OperatorException("unable to wrap contents key: " + e3.getMessage(), e3);
    }

    private int getKemEncLength(PublicKey publicKey) {
        return this.kemEncLenProvider.getEncapsulationLength(SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()).getAlgorithm());
    }
}
