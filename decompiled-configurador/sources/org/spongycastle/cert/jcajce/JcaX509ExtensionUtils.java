package org.spongycastle.cert.jcajce;

import es.gob.afirma.core.signers.AOSignConstants;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import javax.security.auth.x500.X500Principal;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.oiw.OIWObjectIdentifiers;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.asn1.x509.AuthorityKeyIdentifier;
import org.spongycastle.asn1.x509.GeneralName;
import org.spongycastle.asn1.x509.GeneralNames;
import org.spongycastle.asn1.x509.SubjectKeyIdentifier;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.cert.X509ExtensionUtils;
import org.spongycastle.operator.DigestCalculator;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cert/jcajce/JcaX509ExtensionUtils.class */
public class JcaX509ExtensionUtils extends X509ExtensionUtils {
    public JcaX509ExtensionUtils() throws NoSuchAlgorithmException {
        super(new SHA1DigestCalculator(MessageDigest.getInstance(AOSignConstants.DIGEST_ALGORITHM_SHA1)));
    }

    public JcaX509ExtensionUtils(DigestCalculator calculator) {
        super(calculator);
    }

    public AuthorityKeyIdentifier createAuthorityKeyIdentifier(X509Certificate cert) throws CertificateEncodingException {
        return super.createAuthorityKeyIdentifier(new JcaX509CertificateHolder(cert));
    }

    public AuthorityKeyIdentifier createAuthorityKeyIdentifier(PublicKey pubKey) {
        return super.createAuthorityKeyIdentifier(SubjectPublicKeyInfo.getInstance(pubKey.getEncoded()));
    }

    public AuthorityKeyIdentifier createAuthorityKeyIdentifier(PublicKey pubKey, X500Principal name, BigInteger serial) {
        return super.createAuthorityKeyIdentifier(SubjectPublicKeyInfo.getInstance(pubKey.getEncoded()), new GeneralNames(new GeneralName(X500Name.getInstance(name.getEncoded()))), serial);
    }

    public AuthorityKeyIdentifier createAuthorityKeyIdentifier(PublicKey pubKey, GeneralNames generalNames, BigInteger serial) {
        return super.createAuthorityKeyIdentifier(SubjectPublicKeyInfo.getInstance(pubKey.getEncoded()), generalNames, serial);
    }

    public SubjectKeyIdentifier createSubjectKeyIdentifier(PublicKey publicKey) {
        return super.createSubjectKeyIdentifier(SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()));
    }

    public SubjectKeyIdentifier createTruncatedSubjectKeyIdentifier(PublicKey publicKey) {
        return super.createTruncatedSubjectKeyIdentifier(SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()));
    }

    public static ASN1Primitive parseExtensionValue(byte[] encExtValue) throws IOException {
        return ASN1Primitive.fromByteArray(ASN1OctetString.getInstance(encExtValue).getOctets());
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cert/jcajce/JcaX509ExtensionUtils$SHA1DigestCalculator.class */
    private static class SHA1DigestCalculator implements DigestCalculator {
        private ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        private MessageDigest digest;

        public SHA1DigestCalculator(MessageDigest digest) {
            this.digest = digest;
        }

        @Override // org.spongycastle.operator.DigestCalculator
        public AlgorithmIdentifier getAlgorithmIdentifier() {
            return new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1);
        }

        @Override // org.spongycastle.operator.DigestCalculator
        public OutputStream getOutputStream() {
            return this.bOut;
        }

        @Override // org.spongycastle.operator.DigestCalculator
        public byte[] getDigest() {
            byte[] bytes = this.digest.digest(this.bOut.toByteArray());
            this.bOut.reset();
            return bytes;
        }
    }
}
