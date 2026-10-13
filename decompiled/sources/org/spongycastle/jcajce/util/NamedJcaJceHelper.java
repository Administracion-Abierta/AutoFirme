package org.spongycastle.jcajce.util;

import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKeyFactory;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jcajce/util/NamedJcaJceHelper.class */
public class NamedJcaJceHelper implements JcaJceHelper {
    protected final String providerName;

    public NamedJcaJceHelper(String providerName) {
        this.providerName = providerName;
    }

    @Override // org.spongycastle.jcajce.util.JcaJceHelper
    public Cipher createCipher(String algorithm) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException {
        return Cipher.getInstance(algorithm, this.providerName);
    }

    @Override // org.spongycastle.jcajce.util.JcaJceHelper
    public Mac createMac(String algorithm) throws NoSuchAlgorithmException, NoSuchProviderException {
        return Mac.getInstance(algorithm, this.providerName);
    }

    @Override // org.spongycastle.jcajce.util.JcaJceHelper
    public KeyAgreement createKeyAgreement(String algorithm) throws NoSuchAlgorithmException, NoSuchProviderException {
        return KeyAgreement.getInstance(algorithm, this.providerName);
    }

    @Override // org.spongycastle.jcajce.util.JcaJceHelper
    public AlgorithmParameterGenerator createAlgorithmParameterGenerator(String algorithm) throws NoSuchAlgorithmException, NoSuchProviderException {
        return AlgorithmParameterGenerator.getInstance(algorithm, this.providerName);
    }

    @Override // org.spongycastle.jcajce.util.JcaJceHelper
    public AlgorithmParameters createAlgorithmParameters(String algorithm) throws NoSuchAlgorithmException, NoSuchProviderException {
        return AlgorithmParameters.getInstance(algorithm, this.providerName);
    }

    @Override // org.spongycastle.jcajce.util.JcaJceHelper
    public KeyGenerator createKeyGenerator(String algorithm) throws NoSuchAlgorithmException, NoSuchProviderException {
        return KeyGenerator.getInstance(algorithm, this.providerName);
    }

    @Override // org.spongycastle.jcajce.util.JcaJceHelper
    public KeyFactory createKeyFactory(String algorithm) throws NoSuchAlgorithmException, NoSuchProviderException {
        return KeyFactory.getInstance(algorithm, this.providerName);
    }

    @Override // org.spongycastle.jcajce.util.JcaJceHelper
    public SecretKeyFactory createSecretKeyFactory(String algorithm) throws NoSuchAlgorithmException, NoSuchProviderException {
        return SecretKeyFactory.getInstance(algorithm, this.providerName);
    }

    @Override // org.spongycastle.jcajce.util.JcaJceHelper
    public KeyPairGenerator createKeyPairGenerator(String algorithm) throws NoSuchAlgorithmException, NoSuchProviderException {
        return KeyPairGenerator.getInstance(algorithm, this.providerName);
    }

    @Override // org.spongycastle.jcajce.util.JcaJceHelper
    public MessageDigest createDigest(String algorithm) throws NoSuchAlgorithmException, NoSuchProviderException {
        return MessageDigest.getInstance(algorithm, this.providerName);
    }

    @Override // org.spongycastle.jcajce.util.JcaJceHelper
    public Signature createSignature(String algorithm) throws NoSuchAlgorithmException, NoSuchProviderException {
        return Signature.getInstance(algorithm, this.providerName);
    }

    @Override // org.spongycastle.jcajce.util.JcaJceHelper
    public CertificateFactory createCertificateFactory(String algorithm) throws CertificateException, NoSuchProviderException {
        return CertificateFactory.getInstance(algorithm, this.providerName);
    }

    @Override // org.spongycastle.jcajce.util.JcaJceHelper
    public SecureRandom createSecureRandom(String algorithm) throws NoSuchAlgorithmException, NoSuchProviderException {
        return SecureRandom.getInstance(algorithm, this.providerName);
    }
}
