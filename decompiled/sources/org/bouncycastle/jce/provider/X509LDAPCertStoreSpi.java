package org.bouncycastle.jce.provider;

import com.aowagie.text.ElementTags;
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
import org.apache.xml.security.keys.content.x509.XMLX509Certificate;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.x509.CertificatePair;
import org.bouncycastle.jce.X509LDAPCertStoreParameters;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jce/provider/X509LDAPCertStoreSpi.class */
public class X509LDAPCertStoreSpi extends CertStoreSpi {
    private static String[] FILTER_ESCAPE_TABLE = new String[93];
    private static String LDAP_PROVIDER;
    private static String REFERRALS_IGNORE;
    private static final String SEARCH_SECURITY_LEVEL = "none";
    private static final String URL_CONTEXT_PREFIX = "com.sun.jndi.url";
    private X509LDAPCertStoreParameters params;

    public X509LDAPCertStoreSpi(CertStoreParameters certStoreParameters) throws InvalidAlgorithmParameterException {
        super(certStoreParameters);
        if (!(certStoreParameters instanceof X509LDAPCertStoreParameters)) {
            throw new InvalidAlgorithmParameterException(X509LDAPCertStoreSpi.class.getName() + ": parameter must be a " + X509LDAPCertStoreParameters.class.getName() + " object\n" + certStoreParameters.toString());
        }
        this.params = (X509LDAPCertStoreParameters) certStoreParameters;
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
        String strSubstring = str.substring(Strings.toLowerCase(str).indexOf(Strings.toLowerCase(str2)) + str2.length());
        int iIndexOf = strSubstring.indexOf(44);
        if (iIndexOf == -1) {
            iIndexOf = strSubstring.length();
        }
        while (strSubstring.charAt(iIndexOf - 1) == '\\') {
            iIndexOf = strSubstring.indexOf(44, iIndexOf + 1);
            if (iIndexOf == -1) {
                iIndexOf = strSubstring.length();
            }
        }
        String strSubstring2 = strSubstring.substring(0, iIndexOf);
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
        return filterEncode(strSubstring3);
    }

    @Override // java.security.cert.CertStoreSpi
    public Collection engineGetCertificates(CertSelector certSelector) throws CertStoreException {
        if (!(certSelector instanceof X509CertSelector)) {
            throw new CertStoreException("selector is not a X509CertSelector");
        }
        X509CertSelector x509CertSelector = (X509CertSelector) certSelector;
        HashSet hashSet = new HashSet();
        Set<byte[]> endCertificates = getEndCertificates(x509CertSelector);
        endCertificates.addAll(getCACertificates(x509CertSelector));
        endCertificates.addAll(getCrossCertificates(x509CertSelector));
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance(XMLX509Certificate.JCA_CERT_ID, BouncyCastleProvider.PROVIDER_NAME);
            for (byte[] bArr : endCertificates) {
                if (bArr != null && bArr.length != 0) {
                    ArrayList arrayList = new ArrayList();
                    arrayList.add(bArr);
                    try {
                        CertificatePair certificatePair = CertificatePair.getInstance(new ASN1InputStream(bArr).readObject());
                        arrayList.clear();
                        if (certificatePair.getForward() != null) {
                            arrayList.add(certificatePair.getForward().getEncoded());
                        }
                        if (certificatePair.getReverse() != null) {
                            arrayList.add(certificatePair.getReverse().getEncoded());
                        }
                    } catch (IOException e) {
                    } catch (IllegalArgumentException e2) {
                    }
                    Iterator it = arrayList.iterator();
                    while (it.hasNext()) {
                        try {
                            Certificate certificateGenerateCertificate = certificateFactory.generateCertificate(new ByteArrayInputStream((byte[]) it.next()));
                            if (x509CertSelector.match(certificateGenerateCertificate)) {
                                hashSet.add(certificateGenerateCertificate);
                            }
                        } catch (Exception e3) {
                        }
                    }
                }
            }
            return hashSet;
        } catch (Exception e4) {
            throw new CertStoreException("certificate cannot be constructed from LDAP result: " + e4);
        }
    }

    private Set certSubjectSerialSearch(X509CertSelector x509CertSelector, String[] strArr, String str, String str2) throws CertStoreException {
        String name;
        HashSet hashSet = new HashSet();
        try {
            if (x509CertSelector.getSubjectAsBytes() == null && x509CertSelector.getSubjectAsString() == null && x509CertSelector.getCertificate() == null) {
                hashSet.addAll(search(str, "*", strArr));
            } else {
                String string = null;
                if (x509CertSelector.getCertificate() != null) {
                    name = x509CertSelector.getCertificate().getSubjectX500Principal().getName("RFC1779");
                    string = x509CertSelector.getCertificate().getSerialNumber().toString();
                } else {
                    name = x509CertSelector.getSubjectAsBytes() != null ? new X500Principal(x509CertSelector.getSubjectAsBytes()).getName("RFC1779") : x509CertSelector.getSubjectAsString();
                }
                hashSet.addAll(search(str, "*" + parseDN(name, str2) + "*", strArr));
                if (string != null && this.params.getSearchForSerialNumberIn() != null) {
                    hashSet.addAll(search(this.params.getSearchForSerialNumberIn(), "*" + string + "*", strArr));
                }
            }
            return hashSet;
        } catch (IOException e) {
            throw new CertStoreException("exception processing selector: " + e);
        }
    }

    private Set getEndCertificates(X509CertSelector x509CertSelector) throws CertStoreException {
        return certSubjectSerialSearch(x509CertSelector, new String[]{this.params.getUserCertificateAttribute()}, this.params.getLdapUserCertificateAttributeName(), this.params.getUserCertificateSubjectAttributeName());
    }

    private Set getCACertificates(X509CertSelector x509CertSelector) throws CertStoreException {
        String[] strArr = {this.params.getCACertificateAttribute()};
        Set setCertSubjectSerialSearch = certSubjectSerialSearch(x509CertSelector, strArr, this.params.getLdapCACertificateAttributeName(), this.params.getCACertificateSubjectAttributeName());
        if (setCertSubjectSerialSearch.isEmpty()) {
            setCertSubjectSerialSearch.addAll(search(null, "*", strArr));
        }
        return setCertSubjectSerialSearch;
    }

    private Set getCrossCertificates(X509CertSelector x509CertSelector) throws CertStoreException {
        String[] strArr = {this.params.getCrossCertificateAttribute()};
        Set setCertSubjectSerialSearch = certSubjectSerialSearch(x509CertSelector, strArr, this.params.getLdapCrossCertificateAttributeName(), this.params.getCrossCertificateSubjectAttributeName());
        if (setCertSubjectSerialSearch.isEmpty()) {
            setCertSubjectSerialSearch.addAll(search(null, "*", strArr));
        }
        return setCertSubjectSerialSearch;
    }

    @Override // java.security.cert.CertStoreSpi
    public Collection engineGetCRLs(CRLSelector cRLSelector) throws CertStoreException {
        String[] strArr = {this.params.getCertificateRevocationListAttribute()};
        if (!(cRLSelector instanceof X509CRLSelector)) {
            throw new CertStoreException("selector is not a X509CRLSelector");
        }
        X509CRLSelector x509CRLSelector = (X509CRLSelector) cRLSelector;
        HashSet hashSet = new HashSet();
        String ldapCertificateRevocationListAttributeName = this.params.getLdapCertificateRevocationListAttributeName();
        HashSet hashSet2 = new HashSet();
        if (x509CRLSelector.getIssuerNames() != null) {
            for (Object obj : x509CRLSelector.getIssuerNames()) {
                hashSet2.addAll(search(ldapCertificateRevocationListAttributeName, "*" + (obj instanceof String ? parseDN((String) obj, this.params.getCertificateRevocationListIssuerAttributeName()) : parseDN(new X500Principal((byte[]) obj).getName("RFC1779"), this.params.getCertificateRevocationListIssuerAttributeName())) + "*", strArr));
            }
        } else {
            hashSet2.addAll(search(ldapCertificateRevocationListAttributeName, "*", strArr));
        }
        hashSet2.addAll(search(null, "*", strArr));
        Iterator it = hashSet2.iterator();
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance(XMLX509Certificate.JCA_CERT_ID, BouncyCastleProvider.PROVIDER_NAME);
            while (it.hasNext()) {
                CRL crlGenerateCRL = certificateFactory.generateCRL(new ByteArrayInputStream((byte[]) it.next()));
                if (x509CRLSelector.match(crlGenerateCRL)) {
                    hashSet.add(crlGenerateCRL);
                }
            }
            return hashSet;
        } catch (Exception e) {
            throw new CertStoreException("CRL cannot be constructed from LDAP result " + e);
        }
    }

    private String filterEncode(String str) {
        if (str == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(str.length() * 2);
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char cCharAt = str.charAt(i);
            if (cCharAt < FILTER_ESCAPE_TABLE.length) {
                sb.append(FILTER_ESCAPE_TABLE[cCharAt]);
            } else {
                sb.append(cCharAt);
            }
        }
        return sb.toString();
    }

    private Set search(String str, String str2, String[] strArr) throws CertStoreException {
        String str3 = str + "=" + str2;
        if (str == null) {
            str3 = null;
        }
        DirContext dirContextConnectLDAP = null;
        HashSet hashSet = new HashSet();
        try {
            try {
                dirContextConnectLDAP = connectLDAP();
                SearchControls searchControls = new SearchControls();
                searchControls.setSearchScope(2);
                searchControls.setCountLimit(0L);
                for (String str4 : strArr) {
                    String[] strArr2 = {str4};
                    searchControls.setReturningAttributes(strArr2);
                    String str5 = "(&(" + str3 + ")(" + strArr2[0] + "=*))";
                    if (str3 == null) {
                        str5 = "(" + strArr2[0] + "=*)";
                    }
                    NamingEnumeration namingEnumerationSearch = dirContextConnectLDAP.search(this.params.getBaseDN(), str5, searchControls);
                    while (namingEnumerationSearch.hasMoreElements()) {
                        NamingEnumeration all = ((Attribute) ((SearchResult) namingEnumerationSearch.next()).getAttributes().getAll().next()).getAll();
                        while (all.hasMore()) {
                            hashSet.add(all.next());
                        }
                    }
                }
                if (null != dirContextConnectLDAP) {
                    try {
                        dirContextConnectLDAP.close();
                    } catch (Exception e) {
                    }
                }
                return hashSet;
            } catch (Throwable th) {
                if (null != dirContextConnectLDAP) {
                    try {
                        dirContextConnectLDAP.close();
                    } catch (Exception e2) {
                        throw th;
                    }
                }
                throw th;
            }
        } catch (Exception e3) {
            throw new CertStoreException("Error getting results from LDAP directory " + e3);
        }
    }

    static {
        char c = 0;
        while (true) {
            char c2 = c;
            if (c2 >= FILTER_ESCAPE_TABLE.length) {
                FILTER_ESCAPE_TABLE[42] = "\\2a";
                FILTER_ESCAPE_TABLE[40] = "\\28";
                FILTER_ESCAPE_TABLE[41] = "\\29";
                FILTER_ESCAPE_TABLE[92] = "\\5c";
                FILTER_ESCAPE_TABLE[0] = "\\00";
                LDAP_PROVIDER = "com.sun.jndi.ldap.LdapCtxFactory";
                REFERRALS_IGNORE = ElementTags.IGNORE;
                return;
            }
            FILTER_ESCAPE_TABLE[c2] = String.valueOf(c2);
            c = (char) (c2 + 1);
        }
    }
}
