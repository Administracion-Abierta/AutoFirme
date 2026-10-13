package org.spongycastle.pkcs.bc;

import java.io.OutputStream;
import java.security.SecureRandom;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.pkcs.PKCS12PBEParams;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.BufferedBlockCipher;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.ExtendedDigest;
import org.spongycastle.crypto.digests.SHA1Digest;
import org.spongycastle.crypto.generators.PKCS12ParametersGenerator;
import org.spongycastle.crypto.io.CipherOutputStream;
import org.spongycastle.crypto.paddings.PKCS7Padding;
import org.spongycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.spongycastle.operator.GenericKey;
import org.spongycastle.operator.OutputEncryptor;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/pkcs/bc/BcPKCS12PBEOutputEncryptorBuilder.class */
public class BcPKCS12PBEOutputEncryptorBuilder {
    private ExtendedDigest digest;
    private BufferedBlockCipher engine;
    private ASN1ObjectIdentifier algorithm;
    private SecureRandom random;
    private int iterationCount;

    public BcPKCS12PBEOutputEncryptorBuilder(ASN1ObjectIdentifier algorithm, BlockCipher engine) {
        this(algorithm, engine, new SHA1Digest());
    }

    public BcPKCS12PBEOutputEncryptorBuilder(ASN1ObjectIdentifier algorithm, BlockCipher engine, ExtendedDigest pbeDigest) {
        this.iterationCount = 1024;
        this.algorithm = algorithm;
        this.engine = new PaddedBufferedBlockCipher(engine, new PKCS7Padding());
        this.digest = pbeDigest;
    }

    public BcPKCS12PBEOutputEncryptorBuilder setIterationCount(int iterationCount) {
        this.iterationCount = iterationCount;
        return this;
    }

    public OutputEncryptor build(final char[] password) {
        if (this.random == null) {
            this.random = new SecureRandom();
        }
        byte[] salt = new byte[20];
        this.random.nextBytes(salt);
        final PKCS12PBEParams pbeParams = new PKCS12PBEParams(salt, this.iterationCount);
        CipherParameters params = PKCS12PBEUtils.createCipherParameters(this.algorithm, this.digest, this.engine.getBlockSize(), pbeParams, password);
        this.engine.init(true, params);
        return new OutputEncryptor() { // from class: org.spongycastle.pkcs.bc.BcPKCS12PBEOutputEncryptorBuilder.1
            @Override // org.spongycastle.operator.OutputEncryptor
            public AlgorithmIdentifier getAlgorithmIdentifier() {
                return new AlgorithmIdentifier(BcPKCS12PBEOutputEncryptorBuilder.this.algorithm, pbeParams);
            }

            @Override // org.spongycastle.operator.OutputEncryptor
            public OutputStream getOutputStream(OutputStream out) {
                return new CipherOutputStream(out, BcPKCS12PBEOutputEncryptorBuilder.this.engine);
            }

            @Override // org.spongycastle.operator.OutputEncryptor
            public GenericKey getKey() {
                return new GenericKey(new AlgorithmIdentifier(BcPKCS12PBEOutputEncryptorBuilder.this.algorithm, pbeParams), PKCS12ParametersGenerator.PKCS12PasswordToBytes(password));
            }
        };
    }
}
