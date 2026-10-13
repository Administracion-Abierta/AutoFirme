package org.bouncycastle.pqc.jcajce.interfaces;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.cert.Certificate;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/jcajce/interfaces/StateAwareSignature.class */
public interface StateAwareSignature {
    void initVerify(PublicKey publicKey) throws InvalidKeyException;

    void initVerify(Certificate certificate) throws InvalidKeyException;

    void initSign(PrivateKey privateKey) throws InvalidKeyException;

    void initSign(PrivateKey privateKey, SecureRandom secureRandom) throws InvalidKeyException;

    byte[] sign() throws SignatureException;

    int sign(byte[] bArr, int i, int i2) throws SignatureException;

    boolean verify(byte[] bArr) throws SignatureException;

    boolean verify(byte[] bArr, int i, int i2) throws SignatureException;

    void update(byte b) throws SignatureException;

    void update(byte[] bArr) throws SignatureException;

    void update(byte[] bArr, int i, int i2) throws SignatureException;

    void update(ByteBuffer byteBuffer) throws SignatureException;

    String getAlgorithm();

    boolean isSigningCapable();

    PrivateKey getUpdatedPrivateKey();
}
