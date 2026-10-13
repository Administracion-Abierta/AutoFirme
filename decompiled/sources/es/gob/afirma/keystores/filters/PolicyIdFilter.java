package es.gob.afirma.keystores.filters;

import es.gob.afirma.keystores.CertificateFilter;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.x509.CertificatePolicies;
import org.spongycastle.asn1.x509.PolicyInformation;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/filters/PolicyIdFilter.class */
public final class PolicyIdFilter extends CertificateFilter {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    public static final String OID_SEPARATOR = ";";
    final List<String> allowedOids;

    public PolicyIdFilter(String str) {
        if (str == null || str.isEmpty()) {
            throw new IllegalArgumentException("La lista de OID permitidos no puede ser nula ni vacia");
        }
        this.allowedOids = Arrays.asList(str.split(OID_SEPARATOR));
    }

    public PolicyIdFilter(List<String> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("La lista de OID permitidos no puede ser nula ni vacia");
        }
        this.allowedOids = list;
    }

    @Override // es.gob.afirma.keystores.CertificateFilter
    public boolean matches(X509Certificate x509Certificate) {
        if (x509Certificate == null) {
            LOGGER.warning("El certificado proporcionado es nulo, se considera que no cumple el filtro");
            return false;
        }
        List<String> certificatePolicyIds = getCertificatePolicyIds(x509Certificate);
        if (certificatePolicyIds == null || certificatePolicyIds.isEmpty()) {
            return false;
        }
        Iterator<String> it = certificatePolicyIds.iterator();
        while (it.hasNext()) {
            if (!this.allowedOids.contains(it.next())) {
                return false;
            }
        }
        return true;
    }

    private static List<String> getCertificatePolicyIds(X509Certificate x509Certificate) {
        byte[] extensionValue = x509Certificate.getExtensionValue("2.5.29.32");
        if (extensionValue == null || extensionValue.length < 1) {
            return new ArrayList(0);
        }
        PolicyInformation[] policyInformation = CertificatePolicies.getInstance(ASN1Sequence.getInstance(ASN1OctetString.getInstance(extensionValue).getOctets())).getPolicyInformation();
        ArrayList arrayList = new ArrayList(policyInformation.length);
        for (PolicyInformation policyInformation2 : policyInformation) {
            arrayList.add(policyInformation2.getPolicyIdentifier().toString());
        }
        return arrayList;
    }
}
