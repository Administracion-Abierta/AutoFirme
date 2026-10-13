package org.spongycastle.pqc.jcajce.provider.xmss;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.nist.NISTObjectIdentifiers;
import org.spongycastle.crypto.AsymmetricCipherKeyPair;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.digests.SHA512Digest;
import org.spongycastle.crypto.digests.SHAKEDigest;
import org.spongycastle.pqc.crypto.xmss.XMSSKeyGenerationParameters;
import org.spongycastle.pqc.crypto.xmss.XMSSKeyPairGenerator;
import org.spongycastle.pqc.crypto.xmss.XMSSParameters;
import org.spongycastle.pqc.crypto.xmss.XMSSPrivateKeyParameters;
import org.spongycastle.pqc.crypto.xmss.XMSSPublicKeyParameters;
import org.spongycastle.pqc.jcajce.spec.XMSSParameterSpec;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/jcajce/provider/xmss/XMSSKeyPairGeneratorSpi.class */
public class XMSSKeyPairGeneratorSpi extends KeyPairGenerator {
    private XMSSKeyGenerationParameters param;
    private ASN1ObjectIdentifier treeDigest;
    private XMSSKeyPairGenerator engine;
    private SecureRandom random;
    private boolean initialised;

    public XMSSKeyPairGeneratorSpi() {
        super("XMSS");
        this.engine = new XMSSKeyPairGenerator();
        this.random = new SecureRandom();
        this.initialised = false;
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public void initialize(int strength, SecureRandom random) {
        throw new IllegalArgumentException("use AlgorithmParameterSpec");
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public void initialize(AlgorithmParameterSpec params, SecureRandom random) throws InvalidAlgorithmParameterException {
        if (!(params instanceof XMSSParameterSpec)) {
            throw new InvalidAlgorithmParameterException("parameter object not a XMSSParameterSpec");
        }
        XMSSParameterSpec xmssParams = (XMSSParameterSpec) params;
        if (xmssParams.getTreeDigest().equals("SHA256")) {
            this.treeDigest = NISTObjectIdentifiers.id_sha256;
            this.param = new XMSSKeyGenerationParameters(new XMSSParameters(xmssParams.getHeight(), new SHA256Digest()), random);
        } else if (xmssParams.getTreeDigest().equals("SHA512")) {
            this.treeDigest = NISTObjectIdentifiers.id_sha512;
            this.param = new XMSSKeyGenerationParameters(new XMSSParameters(xmssParams.getHeight(), new SHA512Digest()), random);
        } else if (xmssParams.getTreeDigest().equals("SHAKE128")) {
            this.treeDigest = NISTObjectIdentifiers.id_shake128;
            this.param = new XMSSKeyGenerationParameters(new XMSSParameters(xmssParams.getHeight(), new SHAKEDigest(128)), random);
        } else if (xmssParams.getTreeDigest().equals("SHAKE256")) {
            this.treeDigest = NISTObjectIdentifiers.id_shake256;
            this.param = new XMSSKeyGenerationParameters(new XMSSParameters(xmssParams.getHeight(), new SHAKEDigest(256)), random);
        }
        this.engine.init(this.param);
        this.initialised = true;
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public KeyPair generateKeyPair() {
        if (!this.initialised) {
            this.param = new XMSSKeyGenerationParameters(new XMSSParameters(10, new SHA512Digest()), this.random);
            this.engine.init(this.param);
            this.initialised = true;
        }
        AsymmetricCipherKeyPair pair = this.engine.generateKeyPair();
        XMSSPublicKeyParameters pub = (XMSSPublicKeyParameters) pair.getPublic();
        XMSSPrivateKeyParameters priv = (XMSSPrivateKeyParameters) pair.getPrivate();
        return new KeyPair(new BCXMSSPublicKey(this.treeDigest, pub), new BCXMSSPrivateKey(this.treeDigest, priv));
    }
}
