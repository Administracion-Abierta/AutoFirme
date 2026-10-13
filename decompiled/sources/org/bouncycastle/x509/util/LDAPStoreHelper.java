package org.bouncycastle.x509.util;

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
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.CertificatePair;
import org.bouncycastle.jce.X509LDAPCertStoreParameters;
import org.bouncycastle.jce.provider.X509AttrCertParser;
import org.bouncycastle.jce.provider.X509CRLParser;
import org.bouncycastle.jce.provider.X509CertPairParser;
import org.bouncycastle.jce.provider.X509CertParser;
import org.bouncycastle.util.StoreException;
import org.bouncycastle.util.Strings;
import org.bouncycastle.x509.X509AttributeCertStoreSelector;
import org.bouncycastle.x509.X509AttributeCertificate;
import org.bouncycastle.x509.X509CRLStoreSelector;
import org.bouncycastle.x509.X509CertPairStoreSelector;
import org.bouncycastle.x509.X509CertStoreSelector;
import org.bouncycastle.x509.X509CertificatePair;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/x509/util/LDAPStoreHelper.class */
public class LDAPStoreHelper {
    private X509LDAPCertStoreParameters params;
    private static final String SEARCH_SECURITY_LEVEL = "none";
    private static final String URL_CONTEXT_PREFIX = "com.sun.jndi.url";
    private Map cacheMap = new HashMap(cacheSize);
    private static String LDAP_PROVIDER = "com.sun.jndi.ldap.LdapCtxFactory";
    private static String REFERRALS_IGNORE = ElementTags.IGNORE;
    private static int cacheSize = 32;
    private static long lifeTime = DateUtil.MINUTE;

    public LDAPStoreHelper(X509LDAPCertStoreParameters x509LDAPCertStoreParameters) {
        this.params = x509LDAPCertStoreParameters;
    }

    private DirContext connectLDAP() throws NamingException {
        Properties properties = new Properties();
        properties.setProperty("java.naming.factory.initial", LDAP_PROVIDER);
        properties.setProperty("java.naming.batchsize", "0");
        properties.setProperty("java.naming.provider.url", this.params.getLdapURL());
        properties.setProperty("java.naming.factory.url.pkgs", URL_CONTEXT_PREFIX);
        properties.setProperty("java.naming.referral", REFERRALS_IGNORE);
        properties.setProperty("java.naming.security.authentication", SEARCH_SECURITY_LEVEL);
        return new InitialDirContext(properties);
    }

    private String parseDN(String str, String str2) {
        int iIndexOf = Strings.toLowerCase(str).indexOf(Strings.toLowerCase(str2) + "=");
        if (iIndexOf == -1) {
            return "";
        }
        String strSubstring = str.substring(iIndexOf + str2.length());
        int iIndexOf2 = strSubstring.indexOf(44);
        if (iIndexOf2 == -1) {
            iIndexOf2 = strSubstring.length();
        }
        while (strSubstring.charAt(iIndexOf2 - 1) == '\\') {
            iIndexOf2 = strSubstring.indexOf(44, iIndexOf2 + 1);
            if (iIndexOf2 == -1) {
                iIndexOf2 = strSubstring.length();
            }
        }
        String strSubstring2 = strSubstring.substring(0, iIndexOf2);
        String strSubstring3 = strSubstring2.substring(strSubstring2.indexOf(61) + 1);
        if (strSubstring3.charAt(0) == ' ') {
            strSubstring3 = strSubstring3.substring(1);
        }
        if (strSubstring3.startsWith(OperatorName.SHOW_TEXT_LINE_AND_SPACE)) {
            strSubstring3 = strSubstring3.substring(1);
        }
        if (strSubstring3.endsWith(OperatorName.SHOW_TEXT_LINE_AND_SPACE)) {
            strSubstring3 = strSubstring3.substring(0, strSubstring3.length() - 1);
        }
        return strSubstring3;
    }

    private Set createCerts(List list, X509CertStoreSelector x509CertStoreSelector) throws StoreException {
        HashSet hashSet = new HashSet();
        Iterator it = list.iterator();
        X509CertParser x509CertParser = new X509CertParser();
        while (it.hasNext()) {
            try {
                x509CertParser.engineInit(new ByteArrayInputStream((byte[]) it.next()));
                X509Certificate x509Certificate = (X509Certificate) x509CertParser.engineRead();
                if (x509CertStoreSelector.match((Object) x509Certificate)) {
                    hashSet.add(x509Certificate);
                }
            } catch (Exception e) {
            }
        }
        return hashSet;
    }

    private List certSubjectSerialSearch(X509CertStoreSelector x509CertStoreSelector, String[] strArr, String[] strArr2, String[] strArr3) throws StoreException {
        ArrayList arrayList = new ArrayList();
        String subjectAsString = getSubjectAsString(x509CertStoreSelector);
        String string = x509CertStoreSelector.getSerialNumber() != null ? x509CertStoreSelector.getSerialNumber().toString() : null;
        if (x509CertStoreSelector.getCertificate() != null) {
            subjectAsString = x509CertStoreSelector.getCertificate().getSubjectX500Principal().getName("RFC1779");
            string = x509CertStoreSelector.getCertificate().getSerialNumber().toString();
        }
        if (subjectAsString != null) {
            for (String str : strArr3) {
                arrayList.addAll(search(strArr2, "*" + parseDN(subjectAsString, str) + "*", strArr));
            }
        }
        if (string != null && this.params.getSearchForSerialNumberIn() != null) {
            arrayList.addAll(search(splitString(this.params.getSearchForSerialNumberIn()), string, strArr));
        }
        if (string == null && subjectAsString == null) {
            arrayList.addAll(search(strArr2, "*", strArr));
        }
        return arrayList;
    }

    private List crossCertificatePairSubjectSearch(X509CertPairStoreSelector x509CertPairStoreSelector, String[] strArr, String[] strArr2, String[] strArr3) throws StoreException {
        ArrayList arrayList = new ArrayList();
        String subjectAsString = x509CertPairStoreSelector.getForwardSelector() != null ? getSubjectAsString(x509CertPairStoreSelector.getForwardSelector()) : null;
        if (x509CertPairStoreSelector.getCertPair() != null && x509CertPairStoreSelector.getCertPair().getForward() != null) {
            subjectAsString = x509CertPairStoreSelector.getCertPair().getForward().getSubjectX500Principal().getName("RFC1779");
        }
        if (subjectAsString != null) {
            for (String str : strArr3) {
                arrayList.addAll(search(strArr2, "*" + parseDN(subjectAsString, str) + "*", strArr));
            }
        }
        if (subjectAsString == null) {
            arrayList.addAll(search(strArr2, "*", strArr));
        }
        return arrayList;
    }

    private List attrCertSubjectSerialSearch(X509AttributeCertStoreSelector x509AttributeCertStoreSelector, String[] strArr, String[] strArr2, String[] strArr3) throws StoreException {
        ArrayList arrayList = new ArrayList();
        HashSet hashSet = new HashSet();
        Principal[] entityNames = null;
        if (x509AttributeCertStoreSelector.getHolder() != null) {
            if (x509AttributeCertStoreSelector.getHolder().getSerialNumber() != null) {
                hashSet.add(x509AttributeCertStoreSelector.getHolder().getSerialNumber().toString());
            }
            if (x509AttributeCertStoreSelector.getHolder().getEntityNames() != null) {
                entityNames = x509AttributeCertStoreSelector.getHolder().getEntityNames();
            }
        }
        if (x509AttributeCertStoreSelector.getAttributeCert() != null) {
            if (x509AttributeCertStoreSelector.getAttributeCert().getHolder().getEntityNames() != null) {
                entityNames = x509AttributeCertStoreSelector.getAttributeCert().getHolder().getEntityNames();
            }
            hashSet.add(x509AttributeCertStoreSelector.getAttributeCert().getSerialNumber().toString());
        }
        String name = entityNames != null ? entityNames[0] instanceof X500Principal ? ((X500Principal) entityNames[0]).getName("RFC1779") : entityNames[0].getName() : null;
        if (x509AttributeCertStoreSelector.getSerialNumber() != null) {
            hashSet.add(x509AttributeCertStoreSelector.getSerialNumber().toString());
        }
        if (name != null) {
            for (String str : strArr3) {
                arrayList.addAll(search(strArr2, "*" + parseDN(name, str) + "*", strArr));
            }
        }
        if (hashSet.size() > 0 && this.params.getSearchForSerialNumberIn() != null) {
            Iterator it = hashSet.iterator();
            while (it.hasNext()) {
                arrayList.addAll(search(splitString(this.params.getSearchForSerialNumberIn()), (String) it.next(), strArr));
            }
        }
        if (hashSet.size() == 0 && name == null) {
            arrayList.addAll(search(strArr2, "*", strArr));
        }
        return arrayList;
    }

    private List cRLIssuerSearch(X509CRLStoreSelector x509CRLStoreSelector, String[] strArr, String[] strArr2, String[] strArr3) throws StoreException {
        ArrayList arrayList = new ArrayList();
        String name = null;
        HashSet hashSet = new HashSet();
        if (x509CRLStoreSelector.getIssuers() != null) {
            hashSet.addAll(x509CRLStoreSelector.getIssuers());
        }
        if (x509CRLStoreSelector.getCertificateChecking() != null) {
            hashSet.add(getCertificateIssuer(x509CRLStoreSelector.getCertificateChecking()));
        }
        if (x509CRLStoreSelector.getAttrCertificateChecking() != null) {
            Principal[] principals = x509CRLStoreSelector.getAttrCertificateChecking().getIssuer().getPrincipals();
            for (int i = 0; i < principals.length; i++) {
                if (principals[i] instanceof X500Principal) {
                    hashSet.add(principals[i]);
                }
            }
        }
        Iterator it = hashSet.iterator();
        while (it.hasNext()) {
            name = ((X500Principal) it.next()).getName("RFC1779");
            for (String str : strArr3) {
                arrayList.addAll(search(strArr2, "*" + parseDN(name, str) + "*", strArr));
            }
        }
        if (name == null) {
            arrayList.addAll(search(strArr2, "*", strArr));
        }
        return arrayList;
    }

    private List search(String[] strArr, String str, String[] strArr2) throws StoreException {
        String str2;
        if (strArr == null) {
            str2 = null;
        } else {
            String str3 = "";
            if (str.equals("**")) {
                str = "*";
            }
            for (String str4 : strArr) {
                str3 = str3 + "(" + str4 + "=" + str + ")";
            }
            str2 = "(|" + str3 + ")";
        }
        String str5 = "";
        for (String str6 : strArr2) {
            str5 = str5 + "(" + str6 + "=*)";
        }
        String str7 = "(|" + str5 + ")";
        String str8 = "(&" + str2 + "" + str7 + ")";
        if (str2 == null) {
            str8 = str7;
        }
        List fromCache = getFromCache(str8);
        if (fromCache != null) {
            return fromCache;
        }
        DirContext dirContextConnectLDAP = null;
        ArrayList arrayList = new ArrayList();
        try {
            dirContextConnectLDAP = connectLDAP();
            SearchControls searchControls = new SearchControls();
            searchControls.setSearchScope(2);
            searchControls.setCountLimit(0L);
            searchControls.setReturningAttributes(strArr2);
            NamingEnumeration namingEnumerationSearch = dirContextConnectLDAP.search(this.params.getBaseDN(), str8, searchControls);
            while (namingEnumerationSearch.hasMoreElements()) {
                NamingEnumeration all = ((Attribute) ((SearchResult) namingEnumerationSearch.next()).getAttributes().getAll().next()).getAll();
                while (all.hasMore()) {
                    arrayList.add(all.next());
                }
            }
            addToCache(str8, arrayList);
            if (null != dirContextConnectLDAP) {
                try {
                    dirContextConnectLDAP.close();
                } catch (Exception e) {
                }
            }
        } catch (NamingException e2) {
            if (null != dirContextConnectLDAP) {
                try {
                    dirContextConnectLDAP.close();
                } catch (Exception e3) {
                }
            }
        } catch (Throwable th) {
            if (null != dirContextConnectLDAP) {
                try {
                    dirContextConnectLDAP.close();
                } catch (Exception e4) {
                    throw th;
                }
            }
            throw th;
        }
        return arrayList;
    }

    private Set createCRLs(List list, X509CRLStoreSelector x509CRLStoreSelector) throws StoreException {
        HashSet hashSet = new HashSet();
        X509CRLParser x509CRLParser = new X509CRLParser();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            try {
                x509CRLParser.engineInit(new ByteArrayInputStream((byte[]) it.next()));
                X509CRL x509crl = (X509CRL) x509CRLParser.engineRead();
                if (x509CRLStoreSelector.match((Object) x509crl)) {
                    hashSet.add(x509crl);
                }
            } catch (StreamParsingException e) {
            }
        }
        return hashSet;
    }

    private Set createCrossCertificatePairs(List list, X509CertPairStoreSelector x509CertPairStoreSelector) throws StoreException {
        X509CertificatePair x509CertificatePair;
        HashSet hashSet = new HashSet();
        int i = 0;
        while (i < list.size()) {
            try {
                try {
                    X509CertPairParser x509CertPairParser = new X509CertPairParser();
                    x509CertPairParser.engineInit(new ByteArrayInputStream((byte[]) list.get(i)));
                    x509CertificatePair = (X509CertificatePair) x509CertPairParser.engineRead();
                } catch (StreamParsingException e) {
                    x509CertificatePair = new X509CertificatePair(new CertificatePair(Certificate.getInstance(new ASN1InputStream((byte[]) list.get(i)).readObject()), Certificate.getInstance(new ASN1InputStream((byte[]) list.get(i + 1)).readObject())));
                    i++;
                }
                if (x509CertPairStoreSelector.match(x509CertificatePair)) {
                    hashSet.add(x509CertificatePair);
                }
            } catch (IOException e2) {
            } catch (CertificateParsingException e3) {
            }
            i++;
        }
        return hashSet;
    }

    private Set createAttributeCertificates(List list, X509AttributeCertStoreSelector x509AttributeCertStoreSelector) throws StoreException {
        HashSet hashSet = new HashSet();
        Iterator it = list.iterator();
        X509AttrCertParser x509AttrCertParser = new X509AttrCertParser();
        while (it.hasNext()) {
            try {
                x509AttrCertParser.engineInit(new ByteArrayInputStream((byte[]) it.next()));
                X509AttributeCertificate x509AttributeCertificate = (X509AttributeCertificate) x509AttrCertParser.engineRead();
                if (x509AttributeCertStoreSelector.match(x509AttributeCertificate)) {
                    hashSet.add(x509AttributeCertificate);
                }
            } catch (StreamParsingException e) {
            }
        }
        return hashSet;
    }

    public Collection getAuthorityRevocationLists(X509CRLStoreSelector x509CRLStoreSelector) throws StoreException {
        String[] strArrSplitString = splitString(this.params.getAuthorityRevocationListAttribute());
        String[] strArrSplitString2 = splitString(this.params.getLdapAuthorityRevocationListAttributeName());
        String[] strArrSplitString3 = splitString(this.params.getAuthorityRevocationListIssuerAttributeName());
        Set setCreateCRLs = createCRLs(cRLIssuerSearch(x509CRLStoreSelector, strArrSplitString, strArrSplitString2, strArrSplitString3), x509CRLStoreSelector);
        if (setCreateCRLs.size() == 0) {
            setCreateCRLs.addAll(createCRLs(cRLIssuerSearch(new X509CRLStoreSelector(), strArrSplitString, strArrSplitString2, strArrSplitString3), x509CRLStoreSelector));
        }
        return setCreateCRLs;
    }

    public Collection getAttributeCertificateRevocationLists(X509CRLStoreSelector x509CRLStoreSelector) throws StoreException {
        String[] strArrSplitString = splitString(this.params.getAttributeCertificateRevocationListAttribute());
        String[] strArrSplitString2 = splitString(this.params.getLdapAttributeCertificateRevocationListAttributeName());
        String[] strArrSplitString3 = splitString(this.params.getAttributeCertificateRevocationListIssuerAttributeName());
        Set setCreateCRLs = createCRLs(cRLIssuerSearch(x509CRLStoreSelector, strArrSplitString, strArrSplitString2, strArrSplitString3), x509CRLStoreSelector);
        if (setCreateCRLs.size() == 0) {
            setCreateCRLs.addAll(createCRLs(cRLIssuerSearch(new X509CRLStoreSelector(), strArrSplitString, strArrSplitString2, strArrSplitString3), x509CRLStoreSelector));
        }
        return setCreateCRLs;
    }

    public Collection getAttributeAuthorityRevocationLists(X509CRLStoreSelector x509CRLStoreSelector) throws StoreException {
        String[] strArrSplitString = splitString(this.params.getAttributeAuthorityRevocationListAttribute());
        String[] strArrSplitString2 = splitString(this.params.getLdapAttributeAuthorityRevocationListAttributeName());
        String[] strArrSplitString3 = splitString(this.params.getAttributeAuthorityRevocationListIssuerAttributeName());
        Set setCreateCRLs = createCRLs(cRLIssuerSearch(x509CRLStoreSelector, strArrSplitString, strArrSplitString2, strArrSplitString3), x509CRLStoreSelector);
        if (setCreateCRLs.size() == 0) {
            setCreateCRLs.addAll(createCRLs(cRLIssuerSearch(new X509CRLStoreSelector(), strArrSplitString, strArrSplitString2, strArrSplitString3), x509CRLStoreSelector));
        }
        return setCreateCRLs;
    }

    public Collection getCrossCertificatePairs(X509CertPairStoreSelector x509CertPairStoreSelector) throws StoreException {
        String[] strArrSplitString = splitString(this.params.getCrossCertificateAttribute());
        String[] strArrSplitString2 = splitString(this.params.getLdapCrossCertificateAttributeName());
        String[] strArrSplitString3 = splitString(this.params.getCrossCertificateSubjectAttributeName());
        Set setCreateCrossCertificatePairs = createCrossCertificatePairs(crossCertificatePairSubjectSearch(x509CertPairStoreSelector, strArrSplitString, strArrSplitString2, strArrSplitString3), x509CertPairStoreSelector);
        if (setCreateCrossCertificatePairs.size() == 0) {
            X509CertStoreSelector x509CertStoreSelector = new X509CertStoreSelector();
            X509CertPairStoreSelector x509CertPairStoreSelector2 = new X509CertPairStoreSelector();
            x509CertPairStoreSelector2.setForwardSelector(x509CertStoreSelector);
            x509CertPairStoreSelector2.setReverseSelector(x509CertStoreSelector);
            setCreateCrossCertificatePairs.addAll(createCrossCertificatePairs(crossCertificatePairSubjectSearch(x509CertPairStoreSelector2, strArrSplitString, strArrSplitString2, strArrSplitString3), x509CertPairStoreSelector));
        }
        return setCreateCrossCertificatePairs;
    }

    public Collection getUserCertificates(X509CertStoreSelector x509CertStoreSelector) throws StoreException {
        String[] strArrSplitString = splitString(this.params.getUserCertificateAttribute());
        String[] strArrSplitString2 = splitString(this.params.getLdapUserCertificateAttributeName());
        String[] strArrSplitString3 = splitString(this.params.getUserCertificateSubjectAttributeName());
        Set setCreateCerts = createCerts(certSubjectSerialSearch(x509CertStoreSelector, strArrSplitString, strArrSplitString2, strArrSplitString3), x509CertStoreSelector);
        if (setCreateCerts.size() == 0) {
            setCreateCerts.addAll(createCerts(certSubjectSerialSearch(new X509CertStoreSelector(), strArrSplitString, strArrSplitString2, strArrSplitString3), x509CertStoreSelector));
        }
        return setCreateCerts;
    }

    public Collection getAACertificates(X509AttributeCertStoreSelector x509AttributeCertStoreSelector) throws StoreException {
        String[] strArrSplitString = splitString(this.params.getAACertificateAttribute());
        String[] strArrSplitString2 = splitString(this.params.getLdapAACertificateAttributeName());
        String[] strArrSplitString3 = splitString(this.params.getAACertificateSubjectAttributeName());
        Set setCreateAttributeCertificates = createAttributeCertificates(attrCertSubjectSerialSearch(x509AttributeCertStoreSelector, strArrSplitString, strArrSplitString2, strArrSplitString3), x509AttributeCertStoreSelector);
        if (setCreateAttributeCertificates.size() == 0) {
            setCreateAttributeCertificates.addAll(createAttributeCertificates(attrCertSubjectSerialSearch(new X509AttributeCertStoreSelector(), strArrSplitString, strArrSplitString2, strArrSplitString3), x509AttributeCertStoreSelector));
        }
        return setCreateAttributeCertificates;
    }

    public Collection getAttributeDescriptorCertificates(X509AttributeCertStoreSelector x509AttributeCertStoreSelector) throws StoreException {
        String[] strArrSplitString = splitString(this.params.getAttributeDescriptorCertificateAttribute());
        String[] strArrSplitString2 = splitString(this.params.getLdapAttributeDescriptorCertificateAttributeName());
        String[] strArrSplitString3 = splitString(this.params.getAttributeDescriptorCertificateSubjectAttributeName());
        Set setCreateAttributeCertificates = createAttributeCertificates(attrCertSubjectSerialSearch(x509AttributeCertStoreSelector, strArrSplitString, strArrSplitString2, strArrSplitString3), x509AttributeCertStoreSelector);
        if (setCreateAttributeCertificates.size() == 0) {
            setCreateAttributeCertificates.addAll(createAttributeCertificates(attrCertSubjectSerialSearch(new X509AttributeCertStoreSelector(), strArrSplitString, strArrSplitString2, strArrSplitString3), x509AttributeCertStoreSelector));
        }
        return setCreateAttributeCertificates;
    }

    public Collection getCACertificates(X509CertStoreSelector x509CertStoreSelector) throws StoreException {
        String[] strArrSplitString = splitString(this.params.getCACertificateAttribute());
        String[] strArrSplitString2 = splitString(this.params.getLdapCACertificateAttributeName());
        String[] strArrSplitString3 = splitString(this.params.getCACertificateSubjectAttributeName());
        Set setCreateCerts = createCerts(certSubjectSerialSearch(x509CertStoreSelector, strArrSplitString, strArrSplitString2, strArrSplitString3), x509CertStoreSelector);
        if (setCreateCerts.size() == 0) {
            setCreateCerts.addAll(createCerts(certSubjectSerialSearch(new X509CertStoreSelector(), strArrSplitString, strArrSplitString2, strArrSplitString3), x509CertStoreSelector));
        }
        return setCreateCerts;
    }

    public Collection getDeltaCertificateRevocationLists(X509CRLStoreSelector x509CRLStoreSelector) throws StoreException {
        String[] strArrSplitString = splitString(this.params.getDeltaRevocationListAttribute());
        String[] strArrSplitString2 = splitString(this.params.getLdapDeltaRevocationListAttributeName());
        String[] strArrSplitString3 = splitString(this.params.getDeltaRevocationListIssuerAttributeName());
        Set setCreateCRLs = createCRLs(cRLIssuerSearch(x509CRLStoreSelector, strArrSplitString, strArrSplitString2, strArrSplitString3), x509CRLStoreSelector);
        if (setCreateCRLs.size() == 0) {
            setCreateCRLs.addAll(createCRLs(cRLIssuerSearch(new X509CRLStoreSelector(), strArrSplitString, strArrSplitString2, strArrSplitString3), x509CRLStoreSelector));
        }
        return setCreateCRLs;
    }

    public Collection getAttributeCertificateAttributes(X509AttributeCertStoreSelector x509AttributeCertStoreSelector) throws StoreException {
        String[] strArrSplitString = splitString(this.params.getAttributeCertificateAttributeAttribute());
        String[] strArrSplitString2 = splitString(this.params.getLdapAttributeCertificateAttributeAttributeName());
        String[] strArrSplitString3 = splitString(this.params.getAttributeCertificateAttributeSubjectAttributeName());
        Set setCreateAttributeCertificates = createAttributeCertificates(attrCertSubjectSerialSearch(x509AttributeCertStoreSelector, strArrSplitString, strArrSplitString2, strArrSplitString3), x509AttributeCertStoreSelector);
        if (setCreateAttributeCertificates.size() == 0) {
            setCreateAttributeCertificates.addAll(createAttributeCertificates(attrCertSubjectSerialSearch(new X509AttributeCertStoreSelector(), strArrSplitString, strArrSplitString2, strArrSplitString3), x509AttributeCertStoreSelector));
        }
        return setCreateAttributeCertificates;
    }

    public Collection getCertificateRevocationLists(X509CRLStoreSelector x509CRLStoreSelector) throws StoreException {
        String[] strArrSplitString = splitString(this.params.getCertificateRevocationListAttribute());
        String[] strArrSplitString2 = splitString(this.params.getLdapCertificateRevocationListAttributeName());
        String[] strArrSplitString3 = splitString(this.params.getCertificateRevocationListIssuerAttributeName());
        Set setCreateCRLs = createCRLs(cRLIssuerSearch(x509CRLStoreSelector, strArrSplitString, strArrSplitString2, strArrSplitString3), x509CRLStoreSelector);
        if (setCreateCRLs.size() == 0) {
            setCreateCRLs.addAll(createCRLs(cRLIssuerSearch(new X509CRLStoreSelector(), strArrSplitString, strArrSplitString2, strArrSplitString3), x509CRLStoreSelector));
        }
        return setCreateCRLs;
    }

    private synchronized void addToCache(String str, List list) {
        Date date = new Date(System.currentTimeMillis());
        ArrayList arrayList = new ArrayList();
        arrayList.add(date);
        arrayList.add(list);
        if (this.cacheMap.containsKey(str)) {
            this.cacheMap.put(str, arrayList);
            return;
        }
        if (this.cacheMap.size() >= cacheSize) {
            long time = date.getTime();
            Object key = null;
            for (Map.Entry entry : this.cacheMap.entrySet()) {
                long time2 = ((Date) ((List) entry.getValue()).get(0)).getTime();
                if (time2 < time) {
                    time = time2;
                    key = entry.getKey();
                }
            }
            this.cacheMap.remove(key);
        }
        this.cacheMap.put(str, arrayList);
    }

    private List getFromCache(String str) {
        List list = (List) this.cacheMap.get(str);
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (list == null || ((Date) list.get(0)).getTime() < jCurrentTimeMillis - lifeTime) {
            return null;
        }
        return (List) list.get(1);
    }

    private String[] splitString(String str) {
        return str.split("\\s+");
    }

    private String getSubjectAsString(X509CertStoreSelector x509CertStoreSelector) {
        try {
            byte[] subjectAsBytes = x509CertStoreSelector.getSubjectAsBytes();
            if (subjectAsBytes != null) {
                return new X500Principal(subjectAsBytes).getName("RFC1779");
            }
            return null;
        } catch (IOException e) {
            throw new StoreException("exception processing name: " + e.getMessage(), e);
        }
    }

    private X500Principal getCertificateIssuer(X509Certificate x509Certificate) {
        return x509Certificate.getIssuerX500Principal();
    }
}
