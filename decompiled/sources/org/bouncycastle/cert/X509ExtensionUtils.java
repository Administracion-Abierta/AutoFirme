package org.bouncycastle.cert;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.operator.DigestCalculator;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/cert/X509ExtensionUtils.class */
public class X509ExtensionUtils {
    private DigestCalculator calculator;

    public X509ExtensionUtils(DigestCalculator digestCalculator) {
        this.calculator = digestCalculator;
    }

    public AuthorityKeyIdentifier createAuthorityKeyIdentifier(X509CertificateHolder x509CertificateHolder) {
        return new AuthorityKeyIdentifier(getSubjectKeyIdentifier(x509CertificateHolder), new GeneralNames(new GeneralName(x509CertificateHolder.getIssuer())), x509CertificateHolder.getSerialNumber());
    }

    public AuthorityKeyIdentifier createAuthorityKeyIdentifier(SubjectPublicKeyInfo subjectPublicKeyInfo) {
        return new AuthorityKeyIdentifier(calculateIdentifier(subjectPublicKeyInfo));
    }

    public AuthorityKeyIdentifier createAuthorityKeyIdentifier(SubjectPublicKeyInfo subjectPublicKeyInfo, GeneralNames generalNames, BigInteger bigInteger) {
        return new AuthorityKeyIdentifier(calculateIdentifier(subjectPublicKeyInfo), generalNames, bigInteger);
    }

    public SubjectKeyIdentifier createSubjectKeyIdentifier(SubjectPublicKeyInfo subjectPublicKeyInfo) {
        return new SubjectKeyIdentifier(calculateIdentifier(subjectPublicKeyInfo));
    }

    public SubjectKeyIdentifier createTruncatedSubjectKeyIdentifier(SubjectPublicKeyInfo subjectPublicKeyInfo) {
        byte[] bArrCalculateIdentifier = calculateIdentifier(subjectPublicKeyInfo);
        byte[] bArr = new byte[8];
        System.arraycopy(bArrCalculateIdentifier, bArrCalculateIdentifier.length - 8, bArr, 0, bArr.length);
        bArr[0] = (byte) (bArr[0] & 15);
        bArr[0] = (byte) (bArr[0] | 64);
        return new SubjectKeyIdentifier(bArr);
    }

    private byte[] getSubjectKeyIdentifier(X509CertificateHolder x509CertificateHolder) {
        Extension extension;
        if (x509CertificateHolder.getVersionNumber() == 3 && (extension = x509CertificateHolder.getExtension(Extension.subjectKeyIdentifier)) != null) {
            return ASN1OctetString.getInstance(extension.getParsedValue()).getOctets();
        }
        return calculateIdentifier(x509CertificateHolder.getSubjectPublicKeyInfo());
    }

    private byte[] calculateIdentifier(SubjectPublicKeyInfo subjectPublicKeyInfo) {
        byte[] bytes = subjectPublicKeyInfo.getPublicKeyData().getBytes();
        OutputStream outputStream = this.calculator.getOutputStream();
        try {
            outputStream.write(bytes);
            outputStream.close();
            return this.calculator.getDigest();
        } catch (IOException e) {
            throw new CertRuntimeException("unable to calculate identifier: " + e.getMessage(), e);
        }
    }
}
