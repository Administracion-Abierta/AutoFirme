package es.gob.jmulticard.card.dnie;

import es.gob.jmulticard.JmcLogger;
import java.security.cert.X509Certificate;
import java.util.Locale;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/dnie/DnieCertParseUtil.class */
public final class DnieCertParseUtil {
    private final String name;
    private String sn1;
    private final String sn2;
    private final String num;

    public DnieCertParseUtil(X509Certificate c) {
        if (c == null) {
            throw new IllegalArgumentException("El certificado no puede ser nulo");
        }
        String dn = c.getSubjectX500Principal().toString();
        String cn = getCN(dn);
        cn = cn.contains("(") ? cn.substring(0, cn.indexOf(40)).trim() : cn;
        this.name = cn.substring(cn.indexOf(44) + 1).trim();
        this.sn1 = getRdnValueFromLdapName("SN", dn);
        if (this.sn1 == null) {
            this.sn1 = getRdnValueFromLdapName("SURNAME", dn);
        }
        if (this.sn1 == null) {
            this.sn1 = getRdnValueFromLdapName("OID.2.5.4.4", dn);
        }
        if (this.sn1 == null) {
            this.sn1 = getRdnValueFromLdapName("2.5.4.4", dn);
        }
        this.sn2 = cn.replace(",", "").replace(this.name, "").replace(this.sn1, "").trim();
        this.num = getRdnValueFromLdapName("SERIALNUMBER", dn);
    }

    public String getName() {
        return this.name;
    }

    public String getSurname1() {
        return this.sn1;
    }

    public String getSurname2() {
        return this.sn2;
    }

    public String getNumber() {
        return this.num;
    }

    private static String getCN(String principal) {
        if (principal == null) {
            return null;
        }
        String rdn = getRdnValueFromLdapName("cn", principal);
        if (rdn == null) {
            rdn = getRdnValueFromLdapName("ou", principal);
        }
        if (rdn != null) {
            return rdn;
        }
        int i = principal.indexOf(61);
        if (i != -1) {
            JmcLogger.warning("No se ha podido obtener el CN ni la OU, se devolvera el fragmento mas significativo");
            return getRdnValueFromLdapName(principal.substring(0, i), principal);
        }
        JmcLogger.warning("Principal no valido, se devolvera el valor de entrada");
        return principal;
    }

    public String toString() {
        return "Nombre: " + this.name + "\nPrimer apellido: " + this.sn1 + "\nSegundo apellido: " + this.sn2 + "\nNumero: " + this.num;
    }

    private static String getRdnValueFromLdapName(String rdn, String principal) {
        int offset2;
        int offset1 = 0;
        while (true) {
            int offset3 = principal.toLowerCase(Locale.US).indexOf(rdn.toLowerCase(), offset1);
            if (offset3 != -1) {
                if (offset3 > 0 && principal.charAt(offset3 - 1) != ',' && principal.charAt(offset3 - 1) != ' ') {
                    offset1 = offset3 + 1;
                } else {
                    offset1 = offset3 + rdn.length();
                    while (offset1 < principal.length() && principal.charAt(offset1) == ' ') {
                        offset1++;
                    }
                    if (offset1 >= principal.length()) {
                        return null;
                    }
                    if (principal.charAt(offset1) == '=') {
                        do {
                            offset1++;
                            if (offset1 >= principal.length()) {
                                break;
                            }
                        } while (principal.charAt(offset1) == ' ');
                        if (offset1 >= principal.length() || principal.charAt(offset1) == ',') {
                            return "";
                        }
                        if (principal.charAt(offset1) == '\"') {
                            int offset4 = offset1 + 1;
                            if (offset4 >= principal.length() || (offset2 = principal.indexOf(34, offset4)) == offset4) {
                                return "";
                            }
                            if (offset2 != -1) {
                                return principal.substring(offset4, offset2);
                            }
                            return principal.substring(offset4);
                        }
                        int offset5 = principal.indexOf(44, offset1);
                        if (offset5 != -1) {
                            return principal.substring(offset1, offset5).trim();
                        }
                        return principal.substring(offset1).trim();
                    }
                }
            } else {
                return null;
            }
        }
    }
}
