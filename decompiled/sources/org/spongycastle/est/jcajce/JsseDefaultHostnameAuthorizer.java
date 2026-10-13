package org.spongycastle.est.jcajce;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.net.ssl.SSLSession;
import org.spongycastle.asn1.x500.AttributeTypeAndValue;
import org.spongycastle.asn1.x500.RDN;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x500.style.BCStyle;
import org.spongycastle.est.ESTException;
import org.spongycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/est/jcajce/JsseDefaultHostnameAuthorizer.class */
public class JsseDefaultHostnameAuthorizer implements JsseHostnameAuthorizer {
    private final Set<String> knownSuffixes;

    public JsseDefaultHostnameAuthorizer(Set<String> knownSuffixes) {
        this.knownSuffixes = knownSuffixes;
    }

    @Override // org.spongycastle.est.jcajce.JsseHostnameAuthorizer
    public boolean verified(String name, SSLSession context) throws IOException {
        try {
            CertificateFactory fac = CertificateFactory.getInstance("X509");
            X509Certificate cert = (X509Certificate) fac.generateCertificate(new ByteArrayInputStream(context.getPeerCertificates()[0].getEncoded()));
            return verify(name, cert);
        } catch (Exception ex) {
            if (ex instanceof ESTException) {
                throw ((ESTException) ex);
            }
            throw new ESTException(ex.getMessage(), ex);
        }
    }

    public boolean verify(String name, X509Certificate cert) throws IOException {
        try {
            Collection<List<?>> subjectAlternativeNames = cert.getSubjectAlternativeNames();
            if (subjectAlternativeNames != null) {
                for (List<?> list : subjectAlternativeNames) {
                    switch (((Number) list.get(0)).intValue()) {
                        case 2:
                            if (isValidNameMatch(name, list.get(1).toString(), this.knownSuffixes)) {
                                return true;
                            }
                            continue;
                            break;
                        case 7:
                            if (InetAddress.getByName(name).equals(InetAddress.getByName(list.get(1).toString()))) {
                                return true;
                            }
                            continue;
                            break;
                        default:
                            throw new RuntimeException("Unable to handle ");
                    }
                    throw new ESTException(ex.getMessage(), ex);
                }
                return false;
            }
            if (cert.getSubjectX500Principal() == null) {
                return false;
            }
            RDN[] rdNs = X500Name.getInstance(cert.getSubjectX500Principal().getEncoded()).getRDNs();
            for (int i = 0; i != rdNs.length; i++) {
                RDN rdn = rdNs[i];
                AttributeTypeAndValue[] typesAndValues = rdn.getTypesAndValues();
                for (int j = 0; j != typesAndValues.length; j++) {
                    AttributeTypeAndValue atv = typesAndValues[j];
                    if (atv.getType().equals(BCStyle.CN)) {
                        return isValidNameMatch(name, rdn.getFirst().getValue().toString(), this.knownSuffixes);
                    }
                }
            }
            return false;
        } catch (Exception ex) {
            throw new ESTException(ex.getMessage(), ex);
        }
    }

    public static boolean isValidNameMatch(String name, String dnsName, Set<String> suffixes) throws IOException {
        if (dnsName.contains("*")) {
            int wildIndex = dnsName.indexOf(42);
            if (wildIndex != dnsName.lastIndexOf("*") || dnsName.contains("..") || dnsName.charAt(dnsName.length() - 1) == '*') {
                return false;
            }
            int dnsDotIndex = dnsName.indexOf(46, wildIndex);
            if (suffixes != null && suffixes.contains(Strings.toLowerCase(dnsName.substring(dnsDotIndex)))) {
                throw new IOException("Wildcard `" + dnsName + "` matches known public suffix.");
            }
            String end = Strings.toLowerCase(dnsName.substring(wildIndex + 1));
            String loweredName = Strings.toLowerCase(name);
            if (loweredName.equals(end) || end.length() > loweredName.length()) {
                return false;
            }
            if (wildIndex > 0) {
                return loweredName.startsWith(dnsName.substring(0, wildIndex - 1)) && loweredName.endsWith(end) && loweredName.substring(wildIndex, loweredName.length() - end.length()).indexOf(46) < 0;
            }
            String prefix = loweredName.substring(0, loweredName.length() - end.length());
            if (prefix.indexOf(46) > 0) {
                return false;
            }
            return loweredName.endsWith(end);
        }
        return name.equalsIgnoreCase(dnsName);
    }
}
