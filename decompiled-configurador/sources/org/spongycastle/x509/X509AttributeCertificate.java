package org.spongycastle.x509;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Extension;
import java.util.Date;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/x509/X509AttributeCertificate.class */
public interface X509AttributeCertificate extends X509Extension {
    int getVersion();

    BigInteger getSerialNumber();

    Date getNotBefore();

    Date getNotAfter();

    AttributeCertificateHolder getHolder();

    AttributeCertificateIssuer getIssuer();

    X509Attribute[] getAttributes();

    X509Attribute[] getAttributes(String str);

    boolean[] getIssuerUniqueID();

    void checkValidity() throws CertificateNotYetValidException, CertificateExpiredException;

    void checkValidity(Date date) throws CertificateNotYetValidException, CertificateExpiredException;

    byte[] getSignature();

    void verify(PublicKey publicKey, String str) throws NoSuchAlgorithmException, SignatureException, InvalidKeyException, CertificateException, NoSuchProviderException;

    byte[] getEncoded() throws IOException;
}
