package org.spongycastle.jcajce.provider.asymmetric.ecgost;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.spongycastle.asn1.x9.X9IntegerConverter;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.DerivationFunction;
import org.spongycastle.crypto.agreement.ECVKOAgreement;
import org.spongycastle.crypto.digests.GOST3411Digest;
import org.spongycastle.crypto.params.AsymmetricKeyParameter;
import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.crypto.params.ECPrivateKeyParameters;
import org.spongycastle.crypto.params.ParametersWithUKM;
import org.spongycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.spongycastle.jcajce.provider.asymmetric.util.BaseAgreementSpi;
import org.spongycastle.jcajce.provider.asymmetric.util.ECUtil;
import org.spongycastle.jcajce.spec.UserKeyingMaterialSpec;
import org.spongycastle.jce.interfaces.ECPrivateKey;
import org.spongycastle.jce.interfaces.ECPublicKey;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/asymmetric/ecgost/KeyAgreementSpi.class */
public class KeyAgreementSpi extends BaseAgreementSpi {
    private static final X9IntegerConverter converter = new X9IntegerConverter();
    private String kaAlgorithm;
    private ECDomainParameters parameters;
    private ECVKOAgreement agreement;
    private byte[] result;

    protected KeyAgreementSpi(String kaAlgorithm, ECVKOAgreement agreement, DerivationFunction kdf) {
        super(kaAlgorithm, kdf);
        this.kaAlgorithm = kaAlgorithm;
        this.agreement = agreement;
    }

    @Override // javax.crypto.KeyAgreementSpi
    protected Key engineDoPhase(Key key, boolean lastPhase) throws IllegalStateException, InvalidKeyException {
        if (this.parameters == null) {
            throw new IllegalStateException(this.kaAlgorithm + " not initialised.");
        }
        if (!lastPhase) {
            throw new IllegalStateException(this.kaAlgorithm + " can only be between two parties.");
        }
        if (!(key instanceof PublicKey)) {
            throw new InvalidKeyException(this.kaAlgorithm + " key agreement requires " + getSimpleName(ECPublicKey.class) + " for doPhase");
        }
        CipherParameters pubKey = generatePublicKeyParameter((PublicKey) key);
        try {
            this.result = this.agreement.calculateAgreement(pubKey);
            return null;
        } catch (Exception e) {
            throw new InvalidKeyException("calculation failed: " + e.getMessage()) { // from class: org.spongycastle.jcajce.provider.asymmetric.ecgost.KeyAgreementSpi.1
                @Override // java.lang.Throwable
                public Throwable getCause() {
                    return e;
                }
            };
        }
    }

    @Override // javax.crypto.KeyAgreementSpi
    protected void engineInit(Key key, AlgorithmParameterSpec params, SecureRandom random) throws InvalidKeyException, InvalidAlgorithmParameterException {
        if (params != null && !(params instanceof UserKeyingMaterialSpec)) {
            throw new InvalidAlgorithmParameterException("No algorithm parameters supported");
        }
        initFromKey(key, params);
    }

    @Override // javax.crypto.KeyAgreementSpi
    protected void engineInit(Key key, SecureRandom random) throws InvalidKeyException {
        initFromKey(key, null);
    }

    private void initFromKey(Key key, AlgorithmParameterSpec parameterSpec) throws InvalidKeyException {
        if (!(key instanceof PrivateKey)) {
            throw new InvalidKeyException(this.kaAlgorithm + " key agreement requires " + getSimpleName(ECPrivateKey.class) + " for initialisation");
        }
        ECPrivateKeyParameters privKey = (ECPrivateKeyParameters) ECUtil.generatePrivateKeyParameter((PrivateKey) key);
        this.parameters = privKey.getParameters();
        this.ukmParameters = parameterSpec instanceof UserKeyingMaterialSpec ? ((UserKeyingMaterialSpec) parameterSpec).getUserKeyingMaterial() : null;
        this.agreement.init(new ParametersWithUKM(privKey, this.ukmParameters));
    }

    private static String getSimpleName(Class clazz) {
        String fullName = clazz.getName();
        return fullName.substring(fullName.lastIndexOf(46) + 1);
    }

    @Override // org.spongycastle.jcajce.provider.asymmetric.util.BaseAgreementSpi
    protected byte[] calcSecret() {
        return this.result;
    }

    static AsymmetricKeyParameter generatePublicKeyParameter(PublicKey key) throws InvalidKeyException {
        return key instanceof BCECPublicKey ? ((BCECGOST3410PublicKey) key).engineGetKeyParameters() : ECUtil.generatePublicKeyParameter(key);
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jcajce/provider/asymmetric/ecgost/KeyAgreementSpi$ECVKO.class */
    public static class ECVKO extends KeyAgreementSpi {
        public ECVKO() {
            super("ECGOST3410", new ECVKOAgreement(new GOST3411Digest()), null);
        }
    }
}
