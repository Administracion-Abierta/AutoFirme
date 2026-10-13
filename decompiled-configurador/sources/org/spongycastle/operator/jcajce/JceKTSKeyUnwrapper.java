package org.spongycastle.operator.jcajce;

import java.security.Key;
import java.security.PrivateKey;
import java.security.Provider;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import org.spongycastle.asn1.cms.GenericHybridParameters;
import org.spongycastle.asn1.cms.RsaKemParameters;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.crypto.util.DEROtherInfo;
import org.spongycastle.jcajce.spec.KTSParameterSpec;
import org.spongycastle.jcajce.util.DefaultJcaJceHelper;
import org.spongycastle.jcajce.util.NamedJcaJceHelper;
import org.spongycastle.jcajce.util.ProviderJcaJceHelper;
import org.spongycastle.operator.AsymmetricKeyUnwrapper;
import org.spongycastle.operator.GenericKey;
import org.spongycastle.operator.OperatorException;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/operator/jcajce/JceKTSKeyUnwrapper.class */
public class JceKTSKeyUnwrapper extends AsymmetricKeyUnwrapper {
    private OperatorHelper helper;
    private Map extraMappings;
    private PrivateKey privKey;
    private byte[] partyUInfo;
    private byte[] partyVInfo;

    public JceKTSKeyUnwrapper(AlgorithmIdentifier algorithmIdentifier, PrivateKey privKey, byte[] partyUInfo, byte[] partyVInfo) {
        super(algorithmIdentifier);
        this.helper = new OperatorHelper(new DefaultJcaJceHelper());
        this.extraMappings = new HashMap();
        this.privKey = privKey;
        this.partyUInfo = Arrays.clone(partyUInfo);
        this.partyVInfo = Arrays.clone(partyVInfo);
    }

    public JceKTSKeyUnwrapper setProvider(Provider provider) {
        this.helper = new OperatorHelper(new ProviderJcaJceHelper(provider));
        return this;
    }

    public JceKTSKeyUnwrapper setProvider(String providerName) {
        this.helper = new OperatorHelper(new NamedJcaJceHelper(providerName));
        return this;
    }

    @Override // org.spongycastle.operator.KeyUnwrapper
    public GenericKey generateUnwrappedKey(AlgorithmIdentifier encryptedKeyAlgorithm, byte[] encryptedKey) throws OperatorException {
        GenericHybridParameters params = GenericHybridParameters.getInstance(getAlgorithmIdentifier().getParameters());
        Cipher keyCipher = this.helper.createAsymmetricWrapper(getAlgorithmIdentifier().getAlgorithm(), this.extraMappings);
        String symmetricWrappingAlg = this.helper.getWrappingAlgorithmName(params.getDem().getAlgorithm());
        RsaKemParameters kemParameters = RsaKemParameters.getInstance(params.getKem().getParameters());
        int keySizeInBits = kemParameters.getKeyLength().intValue() * 8;
        try {
            DEROtherInfo otherInfo = new DEROtherInfo.Builder(params.getDem(), this.partyUInfo, this.partyVInfo).build();
            KTSParameterSpec ktsSpec = new KTSParameterSpec.Builder(symmetricWrappingAlg, keySizeInBits, otherInfo.getEncoded()).withKdfAlgorithm(kemParameters.getKeyDerivationFunction()).build();
            keyCipher.init(4, this.privKey, ktsSpec);
            Key sKey = keyCipher.unwrap(encryptedKey, this.helper.getKeyAlgorithmName(encryptedKeyAlgorithm.getAlgorithm()), 3);
            return new JceGenericKey(encryptedKeyAlgorithm, sKey);
        } catch (Exception e) {
            throw new OperatorException("Unable to unwrap contents key: " + e.getMessage(), e);
        }
    }
}
