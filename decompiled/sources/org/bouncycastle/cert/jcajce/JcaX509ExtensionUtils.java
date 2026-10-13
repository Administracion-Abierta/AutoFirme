package org.bouncycastle.cert.jcajce;

import es.gob.afirma.core.signers.AOSignConstants;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import javax.security.auth.x500.X500Principal;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1String;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.RFC4519Style;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509ExtensionUtils;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.util.Integers;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/jcajce/JcaX509ExtensionUtils.class */
public class JcaX509ExtensionUtils extends X509ExtensionUtils {

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/jcajce/JcaX509ExtensionUtils$SHA1DigestCalculator.class */
    private static class SHA1DigestCalculator implements DigestCalculator {
        private ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        private MessageDigest digest;

        public SHA1DigestCalculator(MessageDigest messageDigest) {
            this.digest = messageDigest;
        }

        @Override // org.bouncycastle.operator.DigestCalculator
        public AlgorithmIdentifier getAlgorithmIdentifier() {
            return new AlgorithmIdentifier(OIWObjectIdentifiers.idSHA1);
        }

        @Override // org.bouncycastle.operator.DigestCalculator
        public OutputStream getOutputStream() {
            return this.bOut;
        }

        @Override // org.bouncycastle.operator.DigestCalculator
        public byte[] getDigest() {
            byte[] bArrDigest = this.digest.digest(this.bOut.toByteArray());
            this.bOut.reset();
            return bArrDigest;
        }
    }

    public JcaX509ExtensionUtils() throws NoSuchAlgorithmException {
        super(new SHA1DigestCalculator(MessageDigest.getInstance(AOSignConstants.DIGEST_ALGORITHM_SHA1)));
    }

    public JcaX509ExtensionUtils(DigestCalculator digestCalculator) {
        super(digestCalculator);
    }

    public AuthorityKeyIdentifier createAuthorityKeyIdentifier(X509Certificate x509Certificate) throws CertificateEncodingException {
        return super.createAuthorityKeyIdentifier(new JcaX509CertificateHolder(x509Certificate));
    }

    public AuthorityKeyIdentifier createAuthorityKeyIdentifier(PublicKey publicKey) {
        return super.createAuthorityKeyIdentifier(SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()));
    }

    public AuthorityKeyIdentifier createAuthorityKeyIdentifier(PublicKey publicKey, X500Principal x500Principal, BigInteger bigInteger) {
        return super.createAuthorityKeyIdentifier(SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()), new GeneralNames(new GeneralName(X500Name.getInstance(x500Principal.getEncoded()))), bigInteger);
    }

    public AuthorityKeyIdentifier createAuthorityKeyIdentifier(PublicKey publicKey, GeneralNames generalNames, BigInteger bigInteger) {
        return super.createAuthorityKeyIdentifier(SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()), generalNames, bigInteger);
    }

    public SubjectKeyIdentifier createSubjectKeyIdentifier(PublicKey publicKey) {
        return super.createSubjectKeyIdentifier(SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()));
    }

    public SubjectKeyIdentifier createTruncatedSubjectKeyIdentifier(PublicKey publicKey) {
        return super.createTruncatedSubjectKeyIdentifier(SubjectPublicKeyInfo.getInstance(publicKey.getEncoded()));
    }

    public static ASN1Primitive parseExtensionValue(byte[] bArr) throws IOException {
        return ASN1Primitive.fromByteArray(ASN1OctetString.getInstance(bArr).getOctets());
    }

    public static Collection getIssuerAlternativeNames(X509Certificate x509Certificate) throws CertificateParsingException {
        return getAlternativeNames(x509Certificate.getExtensionValue(Extension.issuerAlternativeName.getId()));
    }

    public static Collection getSubjectAlternativeNames(X509Certificate x509Certificate) throws CertificateParsingException {
        return getAlternativeNames(x509Certificate.getExtensionValue(Extension.subjectAlternativeName.getId()));
    }

    private static Collection getAlternativeNames(byte[] bArr) throws CertificateParsingException {
        if (bArr == null) {
            return Collections.EMPTY_LIST;
        }
        try {
            ArrayList arrayList = new ArrayList();
            Enumeration objects = DERSequence.getInstance(parseExtensionValue(bArr)).getObjects();
            while (objects.hasMoreElements()) {
                GeneralName generalName = GeneralName.getInstance(objects.nextElement());
                ArrayList arrayList2 = new ArrayList();
                arrayList2.add(Integers.valueOf(generalName.getTagNo()));
                switch (generalName.getTagNo()) {
                    case 0:
                    case 3:
                    case 5:
                        arrayList2.add(generalName.getEncoded());
                        arrayList.add(arrayList2);
                        break;
                    case 1:
                    case 2:
                    case 6:
                        arrayList2.add(((ASN1String) generalName.getName()).getString());
                        arrayList.add(arrayList2);
                        break;
                    case 4:
                        arrayList2.add(X500Name.getInstance(RFC4519Style.INSTANCE, generalName.getName()).toString());
                        arrayList.add(arrayList2);
                        break;
                    case 7:
                        try {
                            arrayList2.add(InetAddress.getByAddress(DEROctetString.getInstance(generalName.getName()).getOctets()).getHostAddress());
                            arrayList.add(arrayList2);
                        } catch (UnknownHostException e) {
                        }
                        break;
                    case 8:
                        arrayList2.add(ASN1ObjectIdentifier.getInstance(generalName.getName()).getId());
                        arrayList.add(arrayList2);
                        break;
                    default:
                        throw new IOException("Bad tag number: " + generalName.getTagNo());
                }
            }
            return Collections.unmodifiableCollection(arrayList);
        } catch (Exception e2) {
            throw new CertificateParsingException(e2.getMessage());
        }
    }
}
