package org.spongycastle.operator.jcajce;

import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import javax.crypto.Cipher;
import org.spongycastle.asn1.cms.GenericHybridParameters;
import org.spongycastle.asn1.cms.RsaKemParameters;
import org.spongycastle.asn1.iso.ISOIECObjectIdentifiers;
import org.spongycastle.asn1.nist.NISTObjectIdentifiers;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x9.X9ObjectIdentifiers;
import org.spongycastle.crypto.util.DEROtherInfo;
import org.spongycastle.jcajce.spec.KTSParameterSpec;
import org.spongycastle.jcajce.util.DefaultJcaJceHelper;
import org.spongycastle.jcajce.util.NamedJcaJceHelper;
import org.spongycastle.jcajce.util.ProviderJcaJceHelper;
import org.spongycastle.operator.AsymmetricKeyWrapper;
import org.spongycastle.operator.GenericKey;
import org.spongycastle.operator.OperatorException;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/operator/jcajce/JceKTSKeyWrapper.class */
public class JceKTSKeyWrapper extends AsymmetricKeyWrapper {
    private final String symmetricWrappingAlg;
    private final int keySizeInBits;
    private final byte[] partyUInfo;
    private final byte[] partyVInfo;
    private OperatorHelper helper;
    private PublicKey publicKey;
    private SecureRandom random;

    public JceKTSKeyWrapper(PublicKey publicKey, String symmetricWrappingAlg, int keySizeInBits, byte[] partyUInfo, byte[] partyVInfo) {
        super(new AlgorithmIdentifier(PKCSObjectIdentifiers.id_rsa_KEM, new GenericHybridParameters(new AlgorithmIdentifier(ISOIECObjectIdentifiers.id_kem_rsa, new RsaKemParameters(new AlgorithmIdentifier(X9ObjectIdentifiers.id_kdf_kdf3, new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha256)), (keySizeInBits + 7) / 8)), JceSymmetricKeyWrapper.determineKeyEncAlg(symmetricWrappingAlg, keySizeInBits))));
        this.helper = new OperatorHelper(new DefaultJcaJceHelper());
        this.publicKey = publicKey;
        this.symmetricWrappingAlg = symmetricWrappingAlg;
        this.keySizeInBits = keySizeInBits;
        this.partyUInfo = Arrays.clone(partyUInfo);
        this.partyVInfo = Arrays.clone(partyVInfo);
    }

    public JceKTSKeyWrapper(X509Certificate certificate, String symmetricWrappingAlg, int keySizeInBits, byte[] partyUInfo, byte[] partyVInfo) {
        this(certificate.getPublicKey(), symmetricWrappingAlg, keySizeInBits, partyUInfo, partyVInfo);
    }

    public JceKTSKeyWrapper setProvider(Provider provider) {
        this.helper = new OperatorHelper(new ProviderJcaJceHelper(provider));
        return this;
    }

    public JceKTSKeyWrapper setProvider(String providerName) {
        this.helper = new OperatorHelper(new NamedJcaJceHelper(providerName));
        return this;
    }

    public JceKTSKeyWrapper setSecureRandom(SecureRandom random) {
        this.random = random;
        return this;
    }

    @Override // org.spongycastle.operator.KeyWrapper
    public byte[] generateWrappedKey(GenericKey encryptionKey) throws OperatorException {
        Cipher keyEncryptionCipher = this.helper.createAsymmetricWrapper(getAlgorithmIdentifier().getAlgorithm(), new HashMap());
        try {
            DEROtherInfo otherInfo = new DEROtherInfo.Builder(JceSymmetricKeyWrapper.determineKeyEncAlg(this.symmetricWrappingAlg, this.keySizeInBits), this.partyUInfo, this.partyVInfo).build();
            KTSParameterSpec ktsSpec = new KTSParameterSpec.Builder(this.symmetricWrappingAlg, this.keySizeInBits, otherInfo.getEncoded()).build();
            keyEncryptionCipher.init(3, this.publicKey, ktsSpec, this.random);
            return keyEncryptionCipher.wrap(OperatorUtils.getJceKey(encryptionKey));
        } catch (Exception e) {
            throw new OperatorException("Unable to wrap contents key: " + e.getMessage(), e);
        }
    }
}
