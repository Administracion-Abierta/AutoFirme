package org.bouncycastle.cms.jcajce;

import java.io.IOException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.cms.IssuerAndSerialNumber;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cms.KeyTransRecipientInfoGenerator;
import org.bouncycastle.operator.jcajce.JceAsymmetricKeyWrapper;
import org.bouncycastle.operator.jcajce.JceKTSKeyWrapper;
import org.bouncycastle.util.encoders.Hex;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cms/jcajce/JceKTSKeyTransRecipientInfoGenerator.class */
public class JceKTSKeyTransRecipientInfoGenerator extends KeyTransRecipientInfoGenerator {
    private static final byte[] ANONYMOUS_SENDER = Hex.decode("0c14416e6f6e796d6f75732053656e64657220202020");

    private JceKTSKeyTransRecipientInfoGenerator(X509Certificate x509Certificate, IssuerAndSerialNumber issuerAndSerialNumber, String str, int i) throws CertificateEncodingException {
        super(issuerAndSerialNumber, new JceKTSKeyWrapper(x509Certificate, str, i, ANONYMOUS_SENDER, getEncodedRecipID(issuerAndSerialNumber)));
    }

    public JceKTSKeyTransRecipientInfoGenerator(X509Certificate x509Certificate, String str, int i) throws CertificateEncodingException {
        this(x509Certificate, new IssuerAndSerialNumber(new JcaX509CertificateHolder(x509Certificate).toASN1Structure()), str, i);
    }

    public JceKTSKeyTransRecipientInfoGenerator(byte[] bArr, PublicKey publicKey, String str, int i) {
        super(bArr, new JceKTSKeyWrapper(publicKey, str, i, ANONYMOUS_SENDER, getEncodedSubKeyId(bArr)));
    }

    private static byte[] getEncodedRecipID(IssuerAndSerialNumber issuerAndSerialNumber) throws CertificateEncodingException {
        try {
            return issuerAndSerialNumber.getEncoded("DER");
        } catch (IOException e) {
            throw new CertificateEncodingException("Cannot process extracted IssuerAndSerialNumber: " + e.getMessage()) { // from class: org.bouncycastle.cms.jcajce.JceKTSKeyTransRecipientInfoGenerator.1
                @Override // java.lang.Throwable
                public Throwable getCause() {
                    return e;
                }
            };
        }
    }

    private static byte[] getEncodedSubKeyId(byte[] bArr) {
        try {
            return new DEROctetString(bArr).getEncoded();
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot process subject key identifier: " + e.getMessage()) { // from class: org.bouncycastle.cms.jcajce.JceKTSKeyTransRecipientInfoGenerator.2
                @Override // java.lang.Throwable
                public Throwable getCause() {
                    return e;
                }
            };
        }
    }

    public JceKTSKeyTransRecipientInfoGenerator(X509Certificate x509Certificate, AlgorithmIdentifier algorithmIdentifier) throws CertificateEncodingException {
        super(new IssuerAndSerialNumber(new JcaX509CertificateHolder(x509Certificate).toASN1Structure()), new JceAsymmetricKeyWrapper(algorithmIdentifier, x509Certificate.getPublicKey()));
    }

    public JceKTSKeyTransRecipientInfoGenerator(byte[] bArr, AlgorithmIdentifier algorithmIdentifier, PublicKey publicKey) {
        super(bArr, new JceAsymmetricKeyWrapper(algorithmIdentifier, publicKey));
    }

    public JceKTSKeyTransRecipientInfoGenerator setProvider(String str) {
        ((JceKTSKeyWrapper) this.wrapper).setProvider(str);
        return this;
    }

    public JceKTSKeyTransRecipientInfoGenerator setProvider(Provider provider) {
        ((JceKTSKeyWrapper) this.wrapper).setProvider(provider);
        return this;
    }
}
