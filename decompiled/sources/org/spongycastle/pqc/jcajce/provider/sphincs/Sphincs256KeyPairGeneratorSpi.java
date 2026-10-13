package org.spongycastle.pqc.jcajce.provider.sphincs;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.nist.NISTObjectIdentifiers;
import org.spongycastle.crypto.AsymmetricCipherKeyPair;
import org.spongycastle.crypto.digests.SHA3Digest;
import org.spongycastle.crypto.digests.SHA512tDigest;
import org.spongycastle.pqc.crypto.sphincs.SPHINCS256KeyGenerationParameters;
import org.spongycastle.pqc.crypto.sphincs.SPHINCS256KeyPairGenerator;
import org.spongycastle.pqc.crypto.sphincs.SPHINCSPrivateKeyParameters;
import org.spongycastle.pqc.crypto.sphincs.SPHINCSPublicKeyParameters;
import org.spongycastle.pqc.jcajce.spec.SPHINCS256KeyGenParameterSpec;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/pqc/jcajce/provider/sphincs/Sphincs256KeyPairGeneratorSpi.class */
public class Sphincs256KeyPairGeneratorSpi extends KeyPairGenerator {
    ASN1ObjectIdentifier treeDigest;
    SPHINCS256KeyGenerationParameters param;
    SPHINCS256KeyPairGenerator engine;
    SecureRandom random;
    boolean initialised;

    public Sphincs256KeyPairGeneratorSpi() {
        super("SPHINCS256");
        this.treeDigest = NISTObjectIdentifiers.id_sha512_256;
        this.engine = new SPHINCS256KeyPairGenerator();
        this.random = new SecureRandom();
        this.initialised = false;
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public void initialize(int strength, SecureRandom random) {
        throw new IllegalArgumentException("use AlgorithmParameterSpec");
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public void initialize(AlgorithmParameterSpec params, SecureRandom random) throws InvalidAlgorithmParameterException {
        if (!(params instanceof SPHINCS256KeyGenParameterSpec)) {
            throw new InvalidAlgorithmParameterException("parameter object not a SPHINCS256KeyGenParameterSpec");
        }
        SPHINCS256KeyGenParameterSpec sphincsParams = (SPHINCS256KeyGenParameterSpec) params;
        if (sphincsParams.getTreeDigest().equals("SHA512-256")) {
            this.treeDigest = NISTObjectIdentifiers.id_sha512_256;
            this.param = new SPHINCS256KeyGenerationParameters(random, new SHA512tDigest(256));
        } else if (sphincsParams.getTreeDigest().equals("SHA3-256")) {
            this.treeDigest = NISTObjectIdentifiers.id_sha3_256;
            this.param = new SPHINCS256KeyGenerationParameters(random, new SHA3Digest(256));
        }
        this.engine.init(this.param);
        this.initialised = true;
    }

    @Override // java.security.KeyPairGenerator, java.security.KeyPairGeneratorSpi
    public KeyPair generateKeyPair() {
        if (!this.initialised) {
            this.param = new SPHINCS256KeyGenerationParameters(this.random, new SHA512tDigest(256));
            this.engine.init(this.param);
            this.initialised = true;
        }
        AsymmetricCipherKeyPair pair = this.engine.generateKeyPair();
        SPHINCSPublicKeyParameters pub = (SPHINCSPublicKeyParameters) pair.getPublic();
        SPHINCSPrivateKeyParameters priv = (SPHINCSPrivateKeyParameters) pair.getPrivate();
        return new KeyPair(new BCSphincs256PublicKey(this.treeDigest, pub), new BCSphincs256PrivateKey(this.treeDigest, priv));
    }
}
