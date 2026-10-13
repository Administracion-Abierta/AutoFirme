package es.gob.afirma.keystores.filters;

import es.gob.afirma.keystores.CertificateFilter;
import es.gob.afirma.keystores.MultipleCertificateFilter;
import es.gob.afirma.keystores.filters.rfc.KeyUsageFilter;
import es.gob.afirma.keystores.filters.rfc.RFC2254CertificateFilter;
import es.gob.afirma.keystores.filters.rfc.SscdFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/filters/CertFilterManager.class */
public final class CertFilterManager {
    private static final String HEADLESS_PROPERTY = "headless";
    private static final String MANDATORY_CERT_SELECTION_PROPERTY = "mandatoryCertSelection";
    private static final String FILTER_PREFIX_KEY = "filter";
    private static final String FILTERS_PREFIX_KEY = "filters";
    private static final String FILTERS_ENUM_SEPARATOR = ".";
    private static final String FILTERS_SEPARATOR = ";";
    private static final String FILTER_TYPE_DISABLE_EXTERNAL_STORES = "disableopeningexternalstores";
    private static final String FILTER_TYPE_DNIE = "dnie:";
    private static final String FILTER_TYPE_SSL = "ssl:";
    private static final String FILTER_TYPE_QUALIFIED = "qualified:";
    private static final String FILTER_TYPE_SIGNING_CERTIFICATE = "signingcert:";
    private static final String FILTER_TYPE_AUTHENTICATION_CERTIFICATE = "authcert:";
    private static final String FILTER_TYPE_NON_EXPIRED = "nonexpired:";
    private static final String FILTER_TYPE_SSCD = "sscd:";
    private static final String FILTER_TYPE_SUBJECT_RFC2254 = "subject.rfc2254:";
    private static final String FILTER_TYPE_SUBJECT_CONTAINS = "subject.contains:";
    private static final String FILTER_TYPE_ISSUER_RFC2254 = "issuer.rfc2254:";
    private static final String FILTER_TYPE_ISSUER_RFC2254_RECURSE = "issuer.rfc2254.recurse:";
    private static final String FILTER_TYPE_ISSUER_CONTAINS = "issuer.contains:";
    private static final String FILTER_TYPE_THUMBPRINT = "thumbprint:";
    private static final String FILTER_TYPE_POLICY_ID = "policyid:";
    private static final String FILTER_TYPE_PSEUDONYM = "pseudonym:";
    private static final String FILTER_TYPE_ENCODED_CERT = "encodedcert:";
    private static final String FILTER_PREFIX_KEYUSAGE = "keyusage.";
    private static final String FILTER_TYPE_KEYUSAGE_DIGITAL_SIGNATURE = "keyusage.digitalsignature:";
    private static final String FILTER_TYPE_KEYUSAGE_NON_REPUDIATION = "keyusage.nonrepudiation:";
    private static final String FILTER_TYPE_KEYUSAGE_KEY_ENCIPHERMENT = "keyusage.keyencipherment:";
    private static final String FILTER_TYPE_KEYUSAGE_DATA_ENCIPHERMENT = "keyusage.dataencipherment:";
    private static final String FILTER_TYPE_KEYUSAGE_KEY_AGREEMENT = "keyusage.keyagreement:";
    private static final String FILTER_TYPE_KEYUSAGE_KEY_CERT_SIGN = "keyusage.keycertsign:";
    private static final String FILTER_TYPE_KEYUSAGE_CRL_SIGN = "keyusage.crlsign:";
    private static final String FILTER_TYPE_KEYUSAGE_ENCIPHER_ONLY = "keyusage.encipheronly:";
    private static final String FILTER_TYPE_KEYUSAGE_DECIPHER_ONLY = "keyusage.decipheronly:";
    private static final String[] PATTERN_KEYUSAGES_FILTER = {FILTER_TYPE_KEYUSAGE_DIGITAL_SIGNATURE, FILTER_TYPE_KEYUSAGE_NON_REPUDIATION, FILTER_TYPE_KEYUSAGE_KEY_ENCIPHERMENT, FILTER_TYPE_KEYUSAGE_DATA_ENCIPHERMENT, FILTER_TYPE_KEYUSAGE_KEY_AGREEMENT, FILTER_TYPE_KEYUSAGE_KEY_CERT_SIGN, FILTER_TYPE_KEYUSAGE_CRL_SIGN, FILTER_TYPE_KEYUSAGE_ENCIPHER_ONLY, FILTER_TYPE_KEYUSAGE_DECIPHER_ONLY};
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private boolean mandatoryCertificate;
    private final List<CertificateFilter> filters;
    private boolean allowExternalStores;

    public CertFilterManager(List<CertificateFilter> list, boolean z, boolean z2) {
        this.mandatoryCertificate = false;
        this.filters = new ArrayList();
        this.allowExternalStores = true;
        if (list != null) {
            this.filters.addAll(list);
        }
        this.mandatoryCertificate = z;
        this.allowExternalStores = z2;
    }

    public CertFilterManager(Properties properties) {
        this.mandatoryCertificate = false;
        this.filters = new ArrayList();
        this.allowExternalStores = true;
        this.mandatoryCertificate = isMandatoryCertificate(properties);
        Iterator<String> it = getFilterValues(properties).iterator();
        while (it.hasNext()) {
            this.filters.add(parseFilter(it.next()));
        }
        if (this.filters.isEmpty()) {
            this.filters.add(new ExpiredCertificateFilter(false));
        }
    }

    private static boolean isMandatoryCertificate(Properties properties) {
        return (properties != null && Boolean.parseBoolean(properties.getProperty("headless"))) || (properties != null && properties.containsKey(MANDATORY_CERT_SELECTION_PROPERTY) && Boolean.FALSE.toString().equalsIgnoreCase(properties.getProperty(MANDATORY_CERT_SELECTION_PROPERTY)));
    }

    public boolean isExternalStoresOpeningAllowed() {
        return this.allowExternalStores;
    }

    private static List<String> getFilterValues(Properties properties) {
        ArrayList arrayList = new ArrayList();
        if (properties.containsKey(FILTER_PREFIX_KEY)) {
            arrayList.add(properties.getProperty(FILTER_PREFIX_KEY));
        } else if (properties.containsKey(FILTERS_PREFIX_KEY)) {
            arrayList.add(properties.getProperty(FILTERS_PREFIX_KEY));
        } else if (properties.containsKey("filters.1")) {
            for (int i = 1; properties.containsKey("filters." + i); i++) {
                arrayList.add(properties.getProperty("filters." + i));
            }
        }
        return arrayList;
    }

    private CertificateFilter parseFilter(String str) {
        ArrayList arrayList = new ArrayList();
        String[] strArrSplit = str.split(";");
        Arrays.sort(strArrSplit);
        for (int i = 0; i < strArrSplit.length; i++) {
            String str2 = strArrSplit[i];
            if (str2.toLowerCase().startsWith(FILTER_TYPE_DNIE)) {
                arrayList.add(new SignatureDNIeFilter());
            } else if (str2.toLowerCase().equals(FILTER_TYPE_DISABLE_EXTERNAL_STORES)) {
                this.allowExternalStores = false;
            } else if (str2.toLowerCase().startsWith(FILTER_TYPE_SSL)) {
                arrayList.add(new SSLFilter(str2.substring(FILTER_TYPE_SSL.length())));
            } else if (str2.toLowerCase().startsWith(FILTER_TYPE_QUALIFIED)) {
                arrayList.add(new QualifiedCertificatesFilter(str2.substring(FILTER_TYPE_QUALIFIED.length())));
            } else if (str2.toLowerCase().startsWith(FILTER_TYPE_SIGNING_CERTIFICATE)) {
                arrayList.add(new SigningCertificateFilter());
            } else if (str2.toLowerCase().startsWith(FILTER_TYPE_AUTHENTICATION_CERTIFICATE)) {
                arrayList.add(new AuthCertificateFilter());
            } else if (str2.toLowerCase().startsWith(FILTER_PREFIX_KEYUSAGE)) {
                arrayList.add(new KeyUsageFilter(generateKeyUsageFiltersPattern(strArrSplit, i)));
            } else if (str2.toLowerCase().startsWith(FILTER_TYPE_NON_EXPIRED)) {
                boolean z = (str2.equalsIgnoreCase(FILTER_TYPE_NON_EXPIRED) || Boolean.parseBoolean(str2.substring(FILTER_TYPE_NON_EXPIRED.length()))) ? false : true;
                arrayList.add(new ExpiredCertificateFilter(z));
            } else if (str2.toLowerCase().startsWith(FILTER_TYPE_SSCD)) {
                arrayList.add(new SscdFilter());
            } else if (str2.toLowerCase().startsWith(FILTER_TYPE_SUBJECT_RFC2254)) {
                arrayList.add(new RFC2254CertificateFilter(str2.substring(FILTER_TYPE_SUBJECT_RFC2254.length()), null));
            } else if (str2.toLowerCase().startsWith(FILTER_TYPE_SUBJECT_CONTAINS)) {
                arrayList.add(new TextContainedCertificateFilter(new String[]{str2.substring(FILTER_TYPE_SUBJECT_CONTAINS.length())}, null));
            } else if (str2.toLowerCase().startsWith(FILTER_TYPE_ISSUER_RFC2254)) {
                arrayList.add(new RFC2254CertificateFilter(null, str2.substring(FILTER_TYPE_ISSUER_RFC2254.length())));
            } else if (str2.toLowerCase().startsWith(FILTER_TYPE_ISSUER_RFC2254_RECURSE)) {
                arrayList.add(new RFC2254CertificateFilter(null, str2.substring(FILTER_TYPE_ISSUER_RFC2254_RECURSE.length()), true));
            } else if (str2.toLowerCase().startsWith(FILTER_TYPE_ISSUER_CONTAINS)) {
                arrayList.add(new TextContainedCertificateFilter(null, new String[]{str2.substring(FILTER_TYPE_ISSUER_CONTAINS.length())}));
            } else if (str2.toLowerCase().startsWith(FILTER_TYPE_THUMBPRINT)) {
                String[] strArrSplit2 = str2.substring(FILTER_TYPE_THUMBPRINT.length()).split(":");
                if (strArrSplit2.length == 2) {
                    arrayList.add(new ThumbPrintCertificateFilter(strArrSplit2[0], strArrSplit2.length > 1 ? strArrSplit2[1] : null));
                }
            } else if (str2.toLowerCase().startsWith(FILTER_TYPE_POLICY_ID)) {
                String strSubstring = str2.substring(FILTER_TYPE_POLICY_ID.length());
                if (strSubstring != null && !strSubstring.isEmpty()) {
                    arrayList.add(new PolicyIdFilter((List<String>) Arrays.asList(strSubstring.split(","))));
                }
            } else if (str2.toLowerCase().startsWith(FILTER_TYPE_PSEUDONYM)) {
                String strSubstring2 = PseudonymFilter.VALUE_PSEUDONYM_AND_OTHERS;
                if (!str2.toLowerCase().equals(FILTER_TYPE_PSEUDONYM)) {
                    strSubstring2 = str2.substring(FILTER_TYPE_PSEUDONYM.length());
                }
                arrayList.add(new PseudonymFilter(strSubstring2));
            } else if (str2.toLowerCase().startsWith(FILTER_TYPE_ENCODED_CERT)) {
                arrayList.add(new EncodedCertificateFilter(str2.substring(FILTER_TYPE_ENCODED_CERT.length())));
            } else {
                LOGGER.warning("Se omitira el filtro '" + str2 + "' por no estar reconocido");
            }
        }
        if (arrayList.size() == 1) {
            return (CertificateFilter) arrayList.get(0);
        }
        return new MultipleCertificateFilter((CertificateFilter[]) arrayList.toArray(new CertificateFilter[arrayList.size()]));
    }

    private static Boolean[] generateKeyUsageFiltersPattern(String[] strArr, int i) {
        int i2 = i;
        String str = strArr[i];
        Boolean[] boolArr = new Boolean[PATTERN_KEYUSAGES_FILTER.length];
        do {
            processKeyUsageFilterDeclaration(str, boolArr);
            if (strArr.length <= i2 + 1 || !strArr[i2 + 1].startsWith(FILTER_PREFIX_KEYUSAGE)) {
                break;
            }
            i2++;
            str = strArr[i2];
        } while (str.toLowerCase().startsWith(FILTER_PREFIX_KEYUSAGE));
        return boolArr;
    }

    private static void processKeyUsageFilterDeclaration(String str, Boolean[] boolArr) {
        int i = -1;
        for (int i2 = 0; i < 0 && i2 < PATTERN_KEYUSAGES_FILTER.length; i2++) {
            if (str.toLowerCase().startsWith(PATTERN_KEYUSAGES_FILTER[i2])) {
                i = i2;
            }
        }
        if (i >= 0) {
            String strSubstring = str.substring(str.indexOf(58) + 1);
            boolArr[i] = strSubstring.equalsIgnoreCase("null") ? null : Boolean.valueOf(strSubstring);
        }
    }

    public List<CertificateFilter> getFilters() {
        return new ArrayList(this.filters);
    }

    public boolean isMandatoryCertificate() {
        return this.mandatoryCertificate;
    }
}
