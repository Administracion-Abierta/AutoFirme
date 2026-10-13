package org.spongycastle.jce.provider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.cert.CRL;
import java.security.cert.CRLSelector;
import java.security.cert.CertSelector;
import java.security.cert.CertStoreException;
import java.security.cert.CertStoreParameters;
import java.security.cert.CertStoreSpi;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRLSelector;
import java.security.cert.X509CertSelector;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.security.auth.x500.X500Principal;
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.asn1.x509.CertificatePair;
import org.spongycastle.jce.X509LDAPCertStoreParameters;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/jce/provider/X509LDAPCertStoreSpi.class */
public class X509LDAPCertStoreSpi extends CertStoreSpi {
    private X509LDAPCertStoreParameters params;
    private static String LDAP_PROVIDER = "com.sun.jndi.ldap.LdapCtxFactory";
    private static String REFERRALS_IGNORE = "ignore";
    private static final String SEARCH_SECURITY_LEVEL = "none";
    private static final String URL_CONTEXT_PREFIX = "com.sun.jndi.url";

    public X509LDAPCertStoreSpi(CertStoreParameters params) throws InvalidAlgorithmParameterException {
        super(params);
        if (!(params instanceof X509LDAPCertStoreParameters)) {
            throw new InvalidAlgorithmParameterException(X509LDAPCertStoreSpi.class.getName() + ": parameter must be a " + X509LDAPCertStoreParameters.class.getName() + " object\n" + params.toString());
        }
        this.params = (X509LDAPCertStoreParameters) params;
    }

    private DirContext connectLDAP() throws NamingException {
        Properties props = new Properties();
        props.setProperty("java.naming.factory.initial", LDAP_PROVIDER);
        props.setProperty("java.naming.batchsize", "0");
        props.setProperty("java.naming.provider.url", this.params.getLdapURL());
        props.setProperty("java.naming.factory.url.pkgs", URL_CONTEXT_PREFIX);
        props.setProperty("java.naming.referral", REFERRALS_IGNORE);
        props.setProperty("java.naming.security.authentication", SEARCH_SECURITY_LEVEL);
        return new InitialDirContext(props);
    }

    private String parseDN(String subject, String subjectAttributeName) {
        int begin = subject.toLowerCase().indexOf(subjectAttributeName.toLowerCase());
        String temp = subject.substring(begin + subjectAttributeName.length());
        int end = temp.indexOf(44);
        if (end == -1) {
            end = temp.length();
        }
        while (temp.charAt(end - 1) == '\\') {
            end = temp.indexOf(44, end + 1);
            if (end == -1) {
                end = temp.length();
            }
        }
        String temp2 = temp.substring(0, end);
        int begin2 = temp2.indexOf(61);
        String temp3 = temp2.substring(begin2 + 1);
        if (temp3.charAt(0) == ' ') {
            temp3 = temp3.substring(1);
        }
        if (temp3.startsWith("\"")) {
            temp3 = temp3.substring(1);
        }
        if (temp3.endsWith("\"")) {
            temp3 = temp3.substring(0, temp3.length() - 1);
        }
        return temp3;
    }

    @Override // java.security.cert.CertStoreSpi
    public Collection engineGetCertificates(CertSelector selector) throws CertStoreException {
        if (!(selector instanceof X509CertSelector)) {
            throw new CertStoreException("selector is not a X509CertSelector");
        }
        X509CertSelector xselector = (X509CertSelector) selector;
        Set certSet = new HashSet();
        Set<byte[]> set = getEndCertificates(xselector);
        set.addAll(getCACertificates(xselector));
        set.addAll(getCrossCertificates(xselector));
        try {
            CertificateFactory cf = CertificateFactory.getInstance(XMLX509Certificate.JCA_CERT_ID, BouncyCastleProvider.PROVIDER_NAME);
            for (byte[] bytes : set) {
                if (bytes != null && bytes.length != 0) {
                    List bytesList = new ArrayList();
                    bytesList.add(bytes);
                    try {
                        CertificatePair pair = CertificatePair.getInstance(new ASN1InputStream(bytes).readObject());
                        bytesList.clear();
                        if (pair.getForward() != null) {
                            bytesList.add(pair.getForward().getEncoded());
                        }
                        if (pair.getReverse() != null) {
                            bytesList.add(pair.getReverse().getEncoded());
                        }
                    } catch (IOException e) {
                    } catch (IllegalArgumentException e2) {
                    }
                    Iterator it2 = bytesList.iterator();
                    while (it2.hasNext()) {
                        ByteArrayInputStream bIn = new ByteArrayInputStream((byte[]) it2.next());
                        try {
                            Certificate cert = cf.generateCertificate(bIn);
                            if (xselector.match(cert)) {
                                certSet.add(cert);
                            }
                        } catch (Exception e3) {
                        }
                    }
                }
            }
            return certSet;
        } catch (Exception e4) {
            throw new CertStoreException("certificate cannot be constructed from LDAP result: " + e4);
        }
    }

    private Set certSubjectSerialSearch(X509CertSelector xselector, String[] attrs, String attrName, String subjectAttributeName) throws CertStoreException {
        String subject;
        Set set = new HashSet();
        try {
            if (xselector.getSubjectAsBytes() != null || xselector.getSubjectAsString() != null || xselector.getCertificate() != null) {
                String serial = null;
                if (xselector.getCertificate() != null) {
                    subject = xselector.getCertificate().getSubjectX500Principal().getName("RFC1779");
                    serial = xselector.getCertificate().getSerialNumber().toString();
                } else if (xselector.getSubjectAsBytes() != null) {
                    subject = new X500Principal(xselector.getSubjectAsBytes()).getName("RFC1779");
                } else {
                    subject = xselector.getSubjectAsString();
                }
                String attrValue = parseDN(subject, subjectAttributeName);
                set.addAll(search(attrName, "*" + attrValue + "*", attrs));
                if (serial != null && this.params.getSearchForSerialNumberIn() != null) {
                    String attrValue2 = serial;
                    set.addAll(search(this.params.getSearchForSerialNumberIn(), "*" + attrValue2 + "*", attrs));
                }
            } else {
                set.addAll(search(attrName, "*", attrs));
            }
            return set;
        } catch (IOException e) {
            throw new CertStoreException("exception processing selector: " + e);
        }
    }

    private Set getEndCertificates(X509CertSelector xselector) throws CertStoreException {
        String[] attrs = {this.params.getUserCertificateAttribute()};
        String attrName = this.params.getLdapUserCertificateAttributeName();
        String subjectAttributeName = this.params.getUserCertificateSubjectAttributeName();
        Set set = certSubjectSerialSearch(xselector, attrs, attrName, subjectAttributeName);
        return set;
    }

    private Set getCACertificates(X509CertSelector xselector) throws CertStoreException {
        String[] attrs = {this.params.getCACertificateAttribute()};
        String attrName = this.params.getLdapCACertificateAttributeName();
        String subjectAttributeName = this.params.getCACertificateSubjectAttributeName();
        Set set = certSubjectSerialSearch(xselector, attrs, attrName, subjectAttributeName);
        if (set.isEmpty()) {
            set.addAll(search(null, "*", attrs));
        }
        return set;
    }

    private Set getCrossCertificates(X509CertSelector xselector) throws CertStoreException {
        String[] attrs = {this.params.getCrossCertificateAttribute()};
        String attrName = this.params.getLdapCrossCertificateAttributeName();
        String subjectAttributeName = this.params.getCrossCertificateSubjectAttributeName();
        Set set = certSubjectSerialSearch(xselector, attrs, attrName, subjectAttributeName);
        if (set.isEmpty()) {
            set.addAll(search(null, "*", attrs));
        }
        return set;
    }

    @Override // java.security.cert.CertStoreSpi
    public Collection engineGetCRLs(CRLSelector selector) throws CertStoreException {
        String dn;
        String[] attrs = {this.params.getCertificateRevocationListAttribute()};
        if (!(selector instanceof X509CRLSelector)) {
            throw new CertStoreException("selector is not a X509CRLSelector");
        }
        X509CRLSelector xselector = (X509CRLSelector) selector;
        Set crlSet = new HashSet();
        String attrName = this.params.getLdapCertificateRevocationListAttributeName();
        Set set = new HashSet();
        if (xselector.getIssuerNames() != null) {
            for (Object o : xselector.getIssuerNames()) {
                if (o instanceof String) {
                    String issuerAttributeName = this.params.getCertificateRevocationListIssuerAttributeName();
                    dn = parseDN((String) o, issuerAttributeName);
                } else {
                    String issuerAttributeName2 = this.params.getCertificateRevocationListIssuerAttributeName();
                    dn = parseDN(new X500Principal((byte[]) o).getName("RFC1779"), issuerAttributeName2);
                }
                String attrValue = dn;
                set.addAll(search(attrName, "*" + attrValue + "*", attrs));
            }
        } else {
            set.addAll(search(attrName, "*", attrs));
        }
        set.addAll(search(null, "*", attrs));
        Iterator it = set.iterator();
        try {
            CertificateFactory cf = CertificateFactory.getInstance(XMLX509Certificate.JCA_CERT_ID, BouncyCastleProvider.PROVIDER_NAME);
            while (it.hasNext()) {
                CRL crl = cf.generateCRL(new ByteArrayInputStream((byte[]) it.next()));
                if (xselector.match(crl)) {
                    crlSet.add(crl);
                }
            }
            return crlSet;
        } catch (Exception e) {
            throw new CertStoreException("CRL cannot be constructed from LDAP result " + e);
        }
    }

    private Set search(String attributeName, String attributeValue, String[] attrs) throws CertStoreException {
        String filter = attributeName + "=" + attributeValue;
        if (attributeName == null) {
            filter = null;
        }
        DirContext ctx = null;
        Set set = new HashSet();
        try {
            try {
                ctx = connectLDAP();
                SearchControls constraints = new SearchControls();
                constraints.setSearchScope(2);
                constraints.setCountLimit(0L);
                for (String str : attrs) {
                    String[] temp = {str};
                    constraints.setReturningAttributes(temp);
                    String filter2 = "(&(" + filter + ")(" + temp[0] + "=*))";
                    if (filter == null) {
                        filter2 = "(" + temp[0] + "=*)";
                    }
                    NamingEnumeration results = ctx.search(this.params.getBaseDN(), filter2, constraints);
                    while (results.hasMoreElements()) {
                        SearchResult sr = (SearchResult) results.next();
                        NamingEnumeration enumeration = ((Attribute) sr.getAttributes().getAll().next()).getAll();
                        while (enumeration.hasMore()) {
                            Object o = enumeration.next();
                            set.add(o);
                        }
                    }
                }
                if (null != ctx) {
                    try {
                        ctx.close();
                    } catch (Exception e) {
                    }
                }
                return set;
            } catch (Exception e2) {
                throw new CertStoreException("Error getting results from LDAP directory " + e2);
            }
        } catch (Throwable th) {
            if (null != ctx) {
                try {
                    ctx.close();
                } catch (Exception e3) {
                    throw th;
                }
            }
            throw th;
        }
    }
}
