package org.spongycastle.cms.jcajce;

import java.io.IOException;
import java.security.Provider;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import org.spongycastle.asn1.ASN1Encoding;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.cms.IssuerAndSerialNumber;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.cert.jcajce.JcaX509CertificateHolder;
import org.spongycastle.cms.KeyTransRecipientInfoGenerator;
import org.spongycastle.operator.jcajce.JceAsymmetricKeyWrapper;
import org.spongycastle.operator.jcajce.JceKTSKeyWrapper;
import org.spongycastle.util.encoders.Hex;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cms/jcajce/JceKTSKeyTransRecipientInfoGenerator.class */
public class JceKTSKeyTransRecipientInfoGenerator extends KeyTransRecipientInfoGenerator {
    private static final byte[] ANONYMOUS_SENDER = Hex.decode("0c14416e6f6e796d6f75732053656e64657220202020");

    private JceKTSKeyTransRecipientInfoGenerator(X509Certificate recipientCert, IssuerAndSerialNumber recipientID, String symmetricWrappingAlg, int keySizeInBits) throws CertificateEncodingException {
        super(recipientID, new JceKTSKeyWrapper(recipientCert, symmetricWrappingAlg, keySizeInBits, ANONYMOUS_SENDER, getEncodedRecipID(recipientID)));
    }

    public JceKTSKeyTransRecipientInfoGenerator(X509Certificate recipientCert, String symmetricWrappingAlg, int keySizeInBits) throws CertificateEncodingException {
        this(recipientCert, new IssuerAndSerialNumber(new JcaX509CertificateHolder(recipientCert).toASN1Structure()), symmetricWrappingAlg, keySizeInBits);
    }

    public JceKTSKeyTransRecipientInfoGenerator(byte[] subjectKeyIdentifier, PublicKey publicKey, String symmetricWrappingAlg, int keySizeInBits) {
        super(subjectKeyIdentifier, new JceKTSKeyWrapper(publicKey, symmetricWrappingAlg, keySizeInBits, ANONYMOUS_SENDER, getEncodedSubKeyId(subjectKeyIdentifier)));
    }

    private static byte[] getEncodedRecipID(IssuerAndSerialNumber recipientID) throws CertificateEncodingException {
        try {
            return recipientID.getEncoded(ASN1Encoding.DER);
        } catch (IOException e) {
            throw new CertificateEncodingException("Cannot process extracted IssuerAndSerialNumber: " + e.getMessage()) { // from class: org.spongycastle.cms.jcajce.JceKTSKeyTransRecipientInfoGenerator.1
                @Override // java.lang.Throwable
                public Throwable getCause() {
                    return e;
                }
            };
        }
    }

    private static byte[] getEncodedSubKeyId(byte[] subjectKeyIdentifier) {
        try {
            return new DEROctetString(subjectKeyIdentifier).getEncoded();
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot process subject key identifier: " + e.getMessage()) { // from class: org.spongycastle.cms.jcajce.JceKTSKeyTransRecipientInfoGenerator.2
                @Override // java.lang.Throwable
                public Throwable getCause() {
                    return e;
                }
            };
        }
    }

    public JceKTSKeyTransRecipientInfoGenerator(X509Certificate recipientCert, AlgorithmIdentifier algorithmIdentifier) throws CertificateEncodingException {
        super(new IssuerAndSerialNumber(new JcaX509CertificateHolder(recipientCert).toASN1Structure()), new JceAsymmetricKeyWrapper(algorithmIdentifier, recipientCert.getPublicKey()));
    }

    public JceKTSKeyTransRecipientInfoGenerator(byte[] subjectKeyIdentifier, AlgorithmIdentifier algorithmIdentifier, PublicKey publicKey) {
        super(subjectKeyIdentifier, new JceAsymmetricKeyWrapper(algorithmIdentifier, publicKey));
    }

    public JceKTSKeyTransRecipientInfoGenerator setProvider(String providerName) {
        ((JceKTSKeyWrapper) this.wrapper).setProvider(providerName);
        return this;
    }

    public JceKTSKeyTransRecipientInfoGenerator setProvider(Provider provider) {
        ((JceKTSKeyWrapper) this.wrapper).setProvider(provider);
        return this;
    }
}
