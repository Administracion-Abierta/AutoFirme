package org.spongycastle.cert;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.x509.AuthorityKeyIdentifier;
import org.spongycastle.asn1.x509.Extension;
import org.spongycastle.asn1.x509.GeneralName;
import org.spongycastle.asn1.x509.GeneralNames;
import org.spongycastle.asn1.x509.SubjectKeyIdentifier;
import org.spongycastle.asn1.x509.SubjectPublicKeyInfo;
import org.spongycastle.operator.DigestCalculator;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/cert/X509ExtensionUtils.class */
public class X509ExtensionUtils {
    private DigestCalculator calculator;

    public X509ExtensionUtils(DigestCalculator calculator) {
        this.calculator = calculator;
    }

    public AuthorityKeyIdentifier createAuthorityKeyIdentifier(X509CertificateHolder certHolder) {
        GeneralName genName = new GeneralName(certHolder.getIssuer());
        return new AuthorityKeyIdentifier(getSubjectKeyIdentifier(certHolder), new GeneralNames(genName), certHolder.getSerialNumber());
    }

    public AuthorityKeyIdentifier createAuthorityKeyIdentifier(SubjectPublicKeyInfo publicKeyInfo) {
        return new AuthorityKeyIdentifier(calculateIdentifier(publicKeyInfo));
    }

    public AuthorityKeyIdentifier createAuthorityKeyIdentifier(SubjectPublicKeyInfo publicKeyInfo, GeneralNames generalNames, BigInteger serial) {
        return new AuthorityKeyIdentifier(calculateIdentifier(publicKeyInfo), generalNames, serial);
    }

    public SubjectKeyIdentifier createSubjectKeyIdentifier(SubjectPublicKeyInfo publicKeyInfo) {
        return new SubjectKeyIdentifier(calculateIdentifier(publicKeyInfo));
    }

    public SubjectKeyIdentifier createTruncatedSubjectKeyIdentifier(SubjectPublicKeyInfo publicKeyInfo) {
        byte[] digest = calculateIdentifier(publicKeyInfo);
        byte[] id = new byte[8];
        System.arraycopy(digest, digest.length - 8, id, 0, id.length);
        id[0] = (byte) (id[0] & 15);
        id[0] = (byte) (id[0] | 64);
        return new SubjectKeyIdentifier(id);
    }

    private byte[] getSubjectKeyIdentifier(X509CertificateHolder certHolder) {
        if (certHolder.getVersionNumber() != 3) {
            return calculateIdentifier(certHolder.getSubjectPublicKeyInfo());
        }
        Extension ext = certHolder.getExtension(Extension.subjectKeyIdentifier);
        if (ext != null) {
            return ASN1OctetString.getInstance(ext.getParsedValue()).getOctets();
        }
        return calculateIdentifier(certHolder.getSubjectPublicKeyInfo());
    }

    private byte[] calculateIdentifier(SubjectPublicKeyInfo publicKeyInfo) {
        byte[] bytes = publicKeyInfo.getPublicKeyData().getBytes();
        OutputStream cOut = this.calculator.getOutputStream();
        try {
            cOut.write(bytes);
            cOut.close();
            return this.calculator.getDigest();
        } catch (IOException e) {
            throw new CertRuntimeException("unable to calculate identifier: " + e.getMessage(), e);
        }
    }
}
