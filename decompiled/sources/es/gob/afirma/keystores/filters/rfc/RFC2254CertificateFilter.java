package es.gob.afirma.keystores.filters.rfc;

import es.gob.afirma.core.keystores.KeyStoreManager;
import es.gob.afirma.keystores.CertificateFilter;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/filters/rfc/RFC2254CertificateFilter.class */
public final class RFC2254CertificateFilter extends CertificateFilter {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private final String rfc2254IssuerFilter;
    private final String rfc2254SubjectFilter;
    private final boolean recurseIssuers;

    public RFC2254CertificateFilter(String str, String str2, boolean z) {
        if ((str == null || str.isEmpty()) && (str2 == null || str2.isEmpty())) {
            throw new IllegalArgumentException("Al menos uno de los criterios de filtrado debe no ser nulo");
        }
        this.rfc2254IssuerFilter = (str2 == null || str2.isEmpty()) ? null : str2;
        this.rfc2254SubjectFilter = (str == null || str.isEmpty()) ? null : str;
        this.recurseIssuers = z;
    }

    public RFC2254CertificateFilter(String str, String str2) {
        this(str, str2, false);
    }

    @Override // es.gob.afirma.keystores.CertificateFilter
    public String[] matches(String[] strArr, KeyStoreManager keyStoreManager) {
        if (!this.recurseIssuers) {
            return super.matches(strArr, keyStoreManager);
        }
        ArrayList arrayList = new ArrayList();
        for (String str : strArr) {
            if (filterSubjectByRFC2254(this.rfc2254SubjectFilter, keyStoreManager.getCertificate(str)) && matchesIssuersRecursivelly(keyStoreManager.getCertificateChain(str))) {
                arrayList.add(str);
            }
        }
        return (String[]) arrayList.toArray(new String[arrayList.size()]);
    }

    @Override // es.gob.afirma.keystores.CertificateFilter
    public boolean matches(X509Certificate x509Certificate) {
        if (this.recurseIssuers) {
            LOGGER.warning("No se dispone de la cadena de certificacion completa, el filtro solo se aplicara al emisor inmediato");
        }
        return filterSubjectByRFC2254(this.rfc2254SubjectFilter, x509Certificate) && filterIssuerByRFC2254(this.rfc2254IssuerFilter, x509Certificate);
    }

    private boolean matchesIssuersRecursivelly(X509Certificate[] x509CertificateArr) {
        if (x509CertificateArr == null) {
            return false;
        }
        for (X509Certificate x509Certificate : x509CertificateArr) {
            if (filterSubjectByRFC2254(this.rfc2254IssuerFilter, x509Certificate)) {
                return true;
            }
        }
        return false;
    }

    private static boolean filterSubjectByRFC2254(String str, X509Certificate x509Certificate) {
        if (x509Certificate == null || str == null) {
            return true;
        }
        return filterRFC2254(str, x509Certificate.getSubjectDN().toString());
    }

    private static boolean filterIssuerByRFC2254(String str, X509Certificate x509Certificate) {
        if (x509Certificate == null || str == null) {
            return true;
        }
        return filterRFC2254(str, x509Certificate.getIssuerDN().toString());
    }

    private static boolean filterRFC2254(String str, String str2) {
        try {
            return filterRFC2254(str, new LdapName(str2));
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "No ha sido posible filtrar el certificado (filtro: '" + str + "', nombre: '" + str2 + "'), no se eliminara del listado: " + e, (Throwable) e);
            return true;
        }
    }

    private static boolean filterRFC2254(String str, LdapName ldapName) {
        if (str == null || ldapName == null) {
            return true;
        }
        try {
            List<Rdn> rdns = ldapName.getRdns();
            if (rdns == null || rdns.isEmpty()) {
                LOGGER.warning("El nombre proporcionado para filtrar no contiene atributos, no se mostrara el certificado en el listado");
                return false;
            }
            Attributes basicAttributes = new BasicAttributes(true);
            for (Rdn rdn : rdns) {
                basicAttributes.put(rdn.getType(), rdn.getValue());
            }
            return new SearchFilter(str).check(basicAttributes);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "No ha sido posible filtrar el certificado (filtro: '" + str + "', nombre: '" + ldapName + "'), no se eliminara del listado: " + e, (Throwable) e);
            return true;
        }
    }
}
