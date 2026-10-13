package es.gob.afirma.keystores.filters.rfc;

import es.gob.afirma.keystores.CertificateFilter;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DEROctetString;
import org.spongycastle.asn1.x509.qualified.QCStatement;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/filters/rfc/SscdFilter.class */
public final class SscdFilter extends CertificateFilter {
    private static final String QC_STATEMENTS_OID = "1.3.6.1.5.5.7.1.3";
    private static final ASN1ObjectIdentifier QC_SSCD_OID = new ASN1ObjectIdentifier("0.4.0.1862.1.4");

    @Override // es.gob.afirma.keystores.CertificateFilter
    public boolean matches(X509Certificate x509Certificate) {
        byte[] extensionValue;
        if (x509Certificate == null || (extensionValue = x509Certificate.getExtensionValue(QC_STATEMENTS_OID)) == null) {
            return false;
        }
        try {
            Iterator<QCStatement> it = QCStatements.getInstance((ASN1Sequence) ASN1Primitive.fromByteArray(((DEROctetString) ASN1Primitive.fromByteArray(extensionValue)).getOctets())).getQCStatement().iterator();
            while (it.hasNext()) {
                if (QC_SSCD_OID.equals(it.next().getStatementId())) {
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }
}
