package org.spongycastle.x509.util;

import com.aowagie.text.ElementTags;
import com.twelvemonkeys.lang.DateUtil;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.Principal;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.asn1.x509.Certificate;
import org.spongycastle.asn1.x509.CertificatePair;
import org.spongycastle.jce.X509LDAPCertStoreParameters;
import org.spongycastle.jce.provider.X509AttrCertParser;
import org.spongycastle.jce.provider.X509CRLParser;
import org.spongycastle.jce.provider.X509CertPairParser;
import org.spongycastle.jce.provider.X509CertParser;
import org.spongycastle.util.StoreException;
import org.spongycastle.x509.X509AttributeCertStoreSelector;
import org.spongycastle.x509.X509AttributeCertificate;
import org.spongycastle.x509.X509CRLStoreSelector;
import org.spongycastle.x509.X509CertPairStoreSelector;
import org.spongycastle.x509.X509CertStoreSelector;
import org.spongycastle.x509.X509CertificatePair;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/x509/util/LDAPStoreHelper.class */
public class LDAPStoreHelper {
    private X509LDAPCertStoreParameters params;
    private static final String SEARCH_SECURITY_LEVEL = "none";
    private static final String URL_CONTEXT_PREFIX = "com.sun.jndi.url";
    private Map cacheMap = new HashMap(cacheSize);
    private static String LDAP_PROVIDER = "com.sun.jndi.ldap.LdapCtxFactory";
    private static String REFERRALS_IGNORE = ElementTags.IGNORE;
    private static int cacheSize = 32;
    private static long lifeTime = DateUtil.MINUTE;

    public LDAPStoreHelper(X509LDAPCertStoreParameters params) {
        this.params = params;
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

    private String parseDN(String subject, String dNAttributeName) {
        int begin = subject.toLowerCase().indexOf(dNAttributeName.toLowerCase() + "=");
        if (begin == -1) {
            return "";
        }
        String temp = subject.substring(begin + dNAttributeName.length());
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
        String temp3 = temp2.substring(temp2.indexOf(61) + 1);
        if (temp3.charAt(0) == ' ') {
            temp3 = temp3.substring(1);
        }
        if (temp3.startsWith(OperatorName.SHOW_TEXT_LINE_AND_SPACE)) {
            temp3 = temp3.substring(1);
        }
        if (temp3.endsWith(OperatorName.SHOW_TEXT_LINE_AND_SPACE)) {
            temp3 = temp3.substring(0, temp3.length() - 1);
        }
        return temp3;
    }

    private Set createCerts(List list, X509CertStoreSelector xselector) throws StoreException {
        Set certSet = new HashSet();
        Iterator it = list.iterator();
        X509CertParser parser = new X509CertParser();
        while (it.hasNext()) {
            try {
                parser.engineInit(new ByteArrayInputStream((byte[]) it.next()));
                X509Certificate cert = (X509Certificate) parser.engineRead();
                if (xselector.match((Object) cert)) {
                    certSet.add(cert);
                }
            } catch (Exception e) {
            }
        }
        return certSet;
    }

    private List certSubjectSerialSearch(X509CertStoreSelector xselector, String[] attrs, String[] attrNames, String[] subjectAttributeNames) throws StoreException {
        List list = new ArrayList();
        String serial = null;
        String subject = getSubjectAsString(xselector);
        if (xselector.getSerialNumber() != null) {
            serial = xselector.getSerialNumber().toString();
        }
        if (xselector.getCertificate() != null) {
            subject = xselector.getCertificate().getSubjectX500Principal().getName("RFC1779");
            serial = xselector.getCertificate().getSerialNumber().toString();
        }
        if (subject != null) {
            for (String str : subjectAttributeNames) {
                String attrValue = parseDN(subject, str);
                list.addAll(search(attrNames, "*" + attrValue + "*", attrs));
            }
        }
        if (serial != null && this.params.getSearchForSerialNumberIn() != null) {
            String attrValue2 = serial;
            list.addAll(search(splitString(this.params.getSearchForSerialNumberIn()), attrValue2, attrs));
        }
        if (serial == null && subject == null) {
            list.addAll(search(attrNames, "*", attrs));
        }
        return list;
    }

    private List crossCertificatePairSubjectSearch(X509CertPairStoreSelector xselector, String[] attrs, String[] attrNames, String[] subjectAttributeNames) throws StoreException {
        List list = new ArrayList();
        String subject = null;
        if (xselector.getForwardSelector() != null) {
            subject = getSubjectAsString(xselector.getForwardSelector());
        }
        if (xselector.getCertPair() != null && xselector.getCertPair().getForward() != null) {
            subject = xselector.getCertPair().getForward().getSubjectX500Principal().getName("RFC1779");
        }
        if (subject != null) {
            for (String str : subjectAttributeNames) {
                String attrValue = parseDN(subject, str);
                list.addAll(search(attrNames, "*" + attrValue + "*", attrs));
            }
        }
        if (subject == null) {
            list.addAll(search(attrNames, "*", attrs));
        }
        return list;
    }

    private List attrCertSubjectSerialSearch(X509AttributeCertStoreSelector xselector, String[] attrs, String[] attrNames, String[] subjectAttributeNames) throws StoreException {
        List list = new ArrayList();
        String subject = null;
        Collection<String> serials = new HashSet();
        Principal[] principals = null;
        if (xselector.getHolder() != null) {
            if (xselector.getHolder().getSerialNumber() != null) {
                serials.add(xselector.getHolder().getSerialNumber().toString());
            }
            if (xselector.getHolder().getEntityNames() != null) {
                principals = xselector.getHolder().getEntityNames();
            }
        }
        if (xselector.getAttributeCert() != null) {
            if (xselector.getAttributeCert().getHolder().getEntityNames() != null) {
                principals = xselector.getAttributeCert().getHolder().getEntityNames();
            }
            serials.add(xselector.getAttributeCert().getSerialNumber().toString());
        }
        if (principals != null) {
            if (principals[0] instanceof X500Principal) {
                subject = ((X500Principal) principals[0]).getName("RFC1779");
            } else {
                subject = principals[0].getName();
            }
        }
        if (xselector.getSerialNumber() != null) {
            serials.add(xselector.getSerialNumber().toString());
        }
        if (subject != null) {
            for (String str : subjectAttributeNames) {
                String attrValue = parseDN(subject, str);
                list.addAll(search(attrNames, "*" + attrValue + "*", attrs));
            }
        }
        if (serials.size() > 0 && this.params.getSearchForSerialNumberIn() != null) {
            for (String serial : serials) {
                list.addAll(search(splitString(this.params.getSearchForSerialNumberIn()), serial, attrs));
            }
        }
        if (serials.size() == 0 && subject == null) {
            list.addAll(search(attrNames, "*", attrs));
        }
        return list;
    }

    private List cRLIssuerSearch(X509CRLStoreSelector xselector, String[] attrs, String[] attrNames, String[] issuerAttributeNames) throws StoreException {
        List list = new ArrayList();
        String issuer = null;
        Collection issuers = new HashSet();
        if (xselector.getIssuers() != null) {
            issuers.addAll(xselector.getIssuers());
        }
        if (xselector.getCertificateChecking() != null) {
            issuers.add(getCertificateIssuer(xselector.getCertificateChecking()));
        }
        if (xselector.getAttrCertificateChecking() != null) {
            Principal[] principals = xselector.getAttrCertificateChecking().getIssuer().getPrincipals();
            for (int i = 0; i < principals.length; i++) {
                if (principals[i] instanceof X500Principal) {
                    issuers.add(principals[i]);
                }
            }
        }
        Iterator it = issuers.iterator();
        while (it.hasNext()) {
            issuer = ((X500Principal) it.next()).getName("RFC1779");
            for (String str : issuerAttributeNames) {
                String attrValue = parseDN(issuer, str);
                list.addAll(search(attrNames, "*" + attrValue + "*", attrs));
            }
        }
        if (issuer == null) {
            list.addAll(search(attrNames, "*", attrs));
        }
        return list;
    }

    private List search(String[] attributeNames, String attributeValue, String[] attrs) throws StoreException {
        String filter;
        if (attributeNames == null) {
            filter = null;
        } else {
            String filter2 = "";
            if (attributeValue.equals("**")) {
                attributeValue = "*";
            }
            for (String str : attributeNames) {
                filter2 = filter2 + "(" + str + "=" + attributeValue + ")";
            }
            filter = "(|" + filter2 + ")";
        }
        String filter3 = "";
        for (String str2 : attrs) {
            filter3 = filter3 + "(" + str2 + "=*)";
        }
        String filter4 = "(|" + filter3 + ")";
        String filter5 = "(&" + filter + "" + filter4 + ")";
        if (filter == null) {
            filter5 = filter4;
        }
        List list = getFromCache(filter5);
        if (list != null) {
            return list;
        }
        DirContext ctx = null;
        List list2 = new ArrayList();
        try {
            ctx = connectLDAP();
            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(2);
            constraints.setCountLimit(0L);
            constraints.setReturningAttributes(attrs);
            NamingEnumeration results = ctx.search(this.params.getBaseDN(), filter5, constraints);
            while (results.hasMoreElements()) {
                SearchResult sr = (SearchResult) results.next();
                NamingEnumeration enumeration = ((Attribute) sr.getAttributes().getAll().next()).getAll();
                while (enumeration.hasMore()) {
                    list2.add(enumeration.next());
                }
            }
            addToCache(filter5, list2);
            if (null != ctx) {
                try {
                    ctx.close();
                } catch (Exception e) {
                }
            }
        } catch (NamingException e2) {
            if (null != ctx) {
                try {
                    ctx.close();
                } catch (Exception e3) {
                }
            }
        } catch (Throwable th) {
            if (null != ctx) {
                try {
                    ctx.close();
                } catch (Exception e4) {
                    throw th;
                }
            }
            throw th;
        }
        return list2;
    }

    private Set createCRLs(List list, X509CRLStoreSelector xselector) throws StoreException {
        Set crlSet = new HashSet();
        X509CRLParser parser = new X509CRLParser();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            try {
                parser.engineInit(new ByteArrayInputStream((byte[]) it.next()));
                X509CRL crl = (X509CRL) parser.engineRead();
                if (xselector.match((Object) crl)) {
                    crlSet.add(crl);
                }
            } catch (StreamParsingException e) {
            }
        }
        return crlSet;
    }

    private Set createCrossCertificatePairs(List list, X509CertPairStoreSelector xselector) throws StoreException {
        X509CertificatePair pair;
        Set certPairSet = new HashSet();
        int i = 0;
        while (i < list.size()) {
            try {
                try {
                    X509CertPairParser parser = new X509CertPairParser();
                    parser.engineInit(new ByteArrayInputStream((byte[]) list.get(i)));
                    pair = (X509CertificatePair) parser.engineRead();
                } catch (StreamParsingException e) {
                    byte[] forward = (byte[]) list.get(i);
                    byte[] reverse = (byte[]) list.get(i + 1);
                    pair = new X509CertificatePair(new CertificatePair(Certificate.getInstance(new ASN1InputStream(forward).readObject()), Certificate.getInstance(new ASN1InputStream(reverse).readObject())));
                    i++;
                }
                if (xselector.match(pair)) {
                    certPairSet.add(pair);
                }
            } catch (IOException e2) {
            } catch (CertificateParsingException e3) {
            }
            i++;
        }
        return certPairSet;
    }

    private Set createAttributeCertificates(List list, X509AttributeCertStoreSelector xselector) throws StoreException {
        Set certSet = new HashSet();
        Iterator it = list.iterator();
        X509AttrCertParser parser = new X509AttrCertParser();
        while (it.hasNext()) {
            try {
                parser.engineInit(new ByteArrayInputStream((byte[]) it.next()));
                X509AttributeCertificate cert = (X509AttributeCertificate) parser.engineRead();
                if (xselector.match(cert)) {
                    certSet.add(cert);
                }
            } catch (StreamParsingException e) {
            }
        }
        return certSet;
    }

    public Collection getAuthorityRevocationLists(X509CRLStoreSelector selector) throws StoreException {
        String[] attrs = splitString(this.params.getAuthorityRevocationListAttribute());
        String[] attrNames = splitString(this.params.getLdapAuthorityRevocationListAttributeName());
        String[] issuerAttributeNames = splitString(this.params.getAuthorityRevocationListIssuerAttributeName());
        List list = cRLIssuerSearch(selector, attrs, attrNames, issuerAttributeNames);
        Set resultSet = createCRLs(list, selector);
        if (resultSet.size() == 0) {
            X509CRLStoreSelector emptySelector = new X509CRLStoreSelector();
            List list2 = cRLIssuerSearch(emptySelector, attrs, attrNames, issuerAttributeNames);
            resultSet.addAll(createCRLs(list2, selector));
        }
        return resultSet;
    }

    public Collection getAttributeCertificateRevocationLists(X509CRLStoreSelector selector) throws StoreException {
        String[] attrs = splitString(this.params.getAttributeCertificateRevocationListAttribute());
        String[] attrNames = splitString(this.params.getLdapAttributeCertificateRevocationListAttributeName());
        String[] issuerAttributeNames = splitString(this.params.getAttributeCertificateRevocationListIssuerAttributeName());
        List list = cRLIssuerSearch(selector, attrs, attrNames, issuerAttributeNames);
        Set resultSet = createCRLs(list, selector);
        if (resultSet.size() == 0) {
            X509CRLStoreSelector emptySelector = new X509CRLStoreSelector();
            List list2 = cRLIssuerSearch(emptySelector, attrs, attrNames, issuerAttributeNames);
            resultSet.addAll(createCRLs(list2, selector));
        }
        return resultSet;
    }

    public Collection getAttributeAuthorityRevocationLists(X509CRLStoreSelector selector) throws StoreException {
        String[] attrs = splitString(this.params.getAttributeAuthorityRevocationListAttribute());
        String[] attrNames = splitString(this.params.getLdapAttributeAuthorityRevocationListAttributeName());
        String[] issuerAttributeNames = splitString(this.params.getAttributeAuthorityRevocationListIssuerAttributeName());
        List list = cRLIssuerSearch(selector, attrs, attrNames, issuerAttributeNames);
        Set resultSet = createCRLs(list, selector);
        if (resultSet.size() == 0) {
            X509CRLStoreSelector emptySelector = new X509CRLStoreSelector();
            List list2 = cRLIssuerSearch(emptySelector, attrs, attrNames, issuerAttributeNames);
            resultSet.addAll(createCRLs(list2, selector));
        }
        return resultSet;
    }

    public Collection getCrossCertificatePairs(X509CertPairStoreSelector selector) throws StoreException {
        String[] attrs = splitString(this.params.getCrossCertificateAttribute());
        String[] attrNames = splitString(this.params.getLdapCrossCertificateAttributeName());
        String[] subjectAttributeNames = splitString(this.params.getCrossCertificateSubjectAttributeName());
        List list = crossCertificatePairSubjectSearch(selector, attrs, attrNames, subjectAttributeNames);
        Set resultSet = createCrossCertificatePairs(list, selector);
        if (resultSet.size() == 0) {
            X509CertStoreSelector emptyCertselector = new X509CertStoreSelector();
            X509CertPairStoreSelector emptySelector = new X509CertPairStoreSelector();
            emptySelector.setForwardSelector(emptyCertselector);
            emptySelector.setReverseSelector(emptyCertselector);
            List list2 = crossCertificatePairSubjectSearch(emptySelector, attrs, attrNames, subjectAttributeNames);
            resultSet.addAll(createCrossCertificatePairs(list2, selector));
        }
        return resultSet;
    }

    public Collection getUserCertificates(X509CertStoreSelector selector) throws StoreException {
        String[] attrs = splitString(this.params.getUserCertificateAttribute());
        String[] attrNames = splitString(this.params.getLdapUserCertificateAttributeName());
        String[] subjectAttributeNames = splitString(this.params.getUserCertificateSubjectAttributeName());
        List list = certSubjectSerialSearch(selector, attrs, attrNames, subjectAttributeNames);
        Set resultSet = createCerts(list, selector);
        if (resultSet.size() == 0) {
            X509CertStoreSelector emptySelector = new X509CertStoreSelector();
            List list2 = certSubjectSerialSearch(emptySelector, attrs, attrNames, subjectAttributeNames);
            resultSet.addAll(createCerts(list2, selector));
        }
        return resultSet;
    }

    public Collection getAACertificates(X509AttributeCertStoreSelector selector) throws StoreException {
        String[] attrs = splitString(this.params.getAACertificateAttribute());
        String[] attrNames = splitString(this.params.getLdapAACertificateAttributeName());
        String[] subjectAttributeNames = splitString(this.params.getAACertificateSubjectAttributeName());
        List list = attrCertSubjectSerialSearch(selector, attrs, attrNames, subjectAttributeNames);
        Set resultSet = createAttributeCertificates(list, selector);
        if (resultSet.size() == 0) {
            X509AttributeCertStoreSelector emptySelector = new X509AttributeCertStoreSelector();
            List list2 = attrCertSubjectSerialSearch(emptySelector, attrs, attrNames, subjectAttributeNames);
            resultSet.addAll(createAttributeCertificates(list2, selector));
        }
        return resultSet;
    }

    public Collection getAttributeDescriptorCertificates(X509AttributeCertStoreSelector selector) throws StoreException {
        String[] attrs = splitString(this.params.getAttributeDescriptorCertificateAttribute());
        String[] attrNames = splitString(this.params.getLdapAttributeDescriptorCertificateAttributeName());
        String[] subjectAttributeNames = splitString(this.params.getAttributeDescriptorCertificateSubjectAttributeName());
        List list = attrCertSubjectSerialSearch(selector, attrs, attrNames, subjectAttributeNames);
        Set resultSet = createAttributeCertificates(list, selector);
        if (resultSet.size() == 0) {
            X509AttributeCertStoreSelector emptySelector = new X509AttributeCertStoreSelector();
            List list2 = attrCertSubjectSerialSearch(emptySelector, attrs, attrNames, subjectAttributeNames);
            resultSet.addAll(createAttributeCertificates(list2, selector));
        }
        return resultSet;
    }

    public Collection getCACertificates(X509CertStoreSelector selector) throws StoreException {
        String[] attrs = splitString(this.params.getCACertificateAttribute());
        String[] attrNames = splitString(this.params.getLdapCACertificateAttributeName());
        String[] subjectAttributeNames = splitString(this.params.getCACertificateSubjectAttributeName());
        List list = certSubjectSerialSearch(selector, attrs, attrNames, subjectAttributeNames);
        Set resultSet = createCerts(list, selector);
        if (resultSet.size() == 0) {
            X509CertStoreSelector emptySelector = new X509CertStoreSelector();
            List list2 = certSubjectSerialSearch(emptySelector, attrs, attrNames, subjectAttributeNames);
            resultSet.addAll(createCerts(list2, selector));
        }
        return resultSet;
    }

    public Collection getDeltaCertificateRevocationLists(X509CRLStoreSelector selector) throws StoreException {
        String[] attrs = splitString(this.params.getDeltaRevocationListAttribute());
        String[] attrNames = splitString(this.params.getLdapDeltaRevocationListAttributeName());
        String[] issuerAttributeNames = splitString(this.params.getDeltaRevocationListIssuerAttributeName());
        List list = cRLIssuerSearch(selector, attrs, attrNames, issuerAttributeNames);
        Set resultSet = createCRLs(list, selector);
        if (resultSet.size() == 0) {
            X509CRLStoreSelector emptySelector = new X509CRLStoreSelector();
            List list2 = cRLIssuerSearch(emptySelector, attrs, attrNames, issuerAttributeNames);
            resultSet.addAll(createCRLs(list2, selector));
        }
        return resultSet;
    }

    public Collection getAttributeCertificateAttributes(X509AttributeCertStoreSelector selector) throws StoreException {
        String[] attrs = splitString(this.params.getAttributeCertificateAttributeAttribute());
        String[] attrNames = splitString(this.params.getLdapAttributeCertificateAttributeAttributeName());
        String[] subjectAttributeNames = splitString(this.params.getAttributeCertificateAttributeSubjectAttributeName());
        List list = attrCertSubjectSerialSearch(selector, attrs, attrNames, subjectAttributeNames);
        Set resultSet = createAttributeCertificates(list, selector);
        if (resultSet.size() == 0) {
            X509AttributeCertStoreSelector emptySelector = new X509AttributeCertStoreSelector();
            List list2 = attrCertSubjectSerialSearch(emptySelector, attrs, attrNames, subjectAttributeNames);
            resultSet.addAll(createAttributeCertificates(list2, selector));
        }
        return resultSet;
    }

    public Collection getCertificateRevocationLists(X509CRLStoreSelector selector) throws StoreException {
        String[] attrs = splitString(this.params.getCertificateRevocationListAttribute());
        String[] attrNames = splitString(this.params.getLdapCertificateRevocationListAttributeName());
        String[] issuerAttributeNames = splitString(this.params.getCertificateRevocationListIssuerAttributeName());
        List list = cRLIssuerSearch(selector, attrs, attrNames, issuerAttributeNames);
        Set resultSet = createCRLs(list, selector);
        if (resultSet.size() == 0) {
            X509CRLStoreSelector emptySelector = new X509CRLStoreSelector();
            List list2 = cRLIssuerSearch(emptySelector, attrs, attrNames, issuerAttributeNames);
            resultSet.addAll(createCRLs(list2, selector));
        }
        return resultSet;
    }

    private synchronized void addToCache(String searchCriteria, List list) {
        Date now = new Date(System.currentTimeMillis());
        ArrayList arrayList = new ArrayList();
        arrayList.add(now);
        arrayList.add(list);
        if (this.cacheMap.containsKey(searchCriteria)) {
            this.cacheMap.put(searchCriteria, arrayList);
            return;
        }
        if (this.cacheMap.size() >= cacheSize) {
            long oldest = now.getTime();
            Object replace = null;
            for (Map.Entry entry : this.cacheMap.entrySet()) {
                long current = ((Date) ((List) entry.getValue()).get(0)).getTime();
                if (current < oldest) {
                    oldest = current;
                    replace = entry.getKey();
                }
            }
            this.cacheMap.remove(replace);
        }
        this.cacheMap.put(searchCriteria, arrayList);
    }

    private List getFromCache(String searchCriteria) {
        List entry = (List) this.cacheMap.get(searchCriteria);
        long now = System.currentTimeMillis();
        if (entry == null || ((Date) entry.get(0)).getTime() < now - lifeTime) {
            return null;
        }
        return (List) entry.get(1);
    }

    private String[] splitString(String str) {
        return str.split("\\s+");
    }

    private String getSubjectAsString(X509CertStoreSelector xselector) {
        try {
            byte[] encSubject = xselector.getSubjectAsBytes();
            if (encSubject != null) {
                return new X500Principal(encSubject).getName("RFC1779");
            }
            return null;
        } catch (IOException e) {
            throw new StoreException("exception processing name: " + e.getMessage(), e);
        }
    }

    private X500Principal getCertificateIssuer(X509Certificate cert) {
        return cert.getIssuerX500Principal();
    }
}
