package es.gob.afirma.ui.core.jse.certificateselection;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import javax.security.auth.x500.X500Principal;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/ui/core/jse/certificateselection/PrincipalStructure.class */
public class PrincipalStructure {
    static final String CN = "CN";
    static final String L = "L";
    static final String ST = "ST";
    static final String O = "O";
    static final String OU = "OU";
    static final String C = "C";
    static final String STREET = "STREET";
    static final String DC = "DC";
    static final String UID = "UID";
    static final String SERIALNUMBER = "2.5.4.5";
    static final String SURNAME = "2.5.4.4";
    static final String GIVENNAME = "2.5.4.42";
    static final String TITLE = "2.5.4.12";
    static final String PSEUDONYM = "2.5.4.65";
    static final String ORGANIZATION_IDENTIFIER = "2.5.4.97";
    static final String DESCRIPTION = "2.5.4.13";
    private final String principal;

    public PrincipalStructure(X500Principal x500Principal) {
        this.principal = x500Principal.getName("RFC2253");
    }

    public String getRDNvalue(String str) {
        int iIndexOf;
        int length = 0;
        while (true) {
            int iIndexOf2 = this.principal.toLowerCase(Locale.US).indexOf(str.toLowerCase(), length);
            if (iIndexOf2 != -1) {
                if (iIndexOf2 > 0 && this.principal.charAt(iIndexOf2 - 1) != ',' && this.principal.charAt(iIndexOf2 - 1) != ' ') {
                    length = iIndexOf2 + 1;
                } else {
                    length = iIndexOf2 + str.length();
                    while (length < this.principal.length() && this.principal.charAt(length) == ' ') {
                        length++;
                    }
                    if (length >= this.principal.length()) {
                        return null;
                    }
                    if (this.principal.charAt(length) == '=') {
                        do {
                            length++;
                            if (length >= this.principal.length()) {
                                break;
                            }
                        } while (this.principal.charAt(length) == ' ');
                        if (length >= this.principal.length() || this.principal.charAt(length) == ',') {
                            return "";
                        }
                        if (this.principal.charAt(length) == '\"') {
                            int i = length + 1;
                            if (i >= this.principal.length() || (iIndexOf = this.principal.indexOf(34, i)) == i) {
                                return "";
                            }
                            if (iIndexOf != -1) {
                                return recoverText(this.principal.substring(i, iIndexOf));
                            }
                            return recoverText(this.principal.substring(i));
                        }
                        int iIndexOf3 = this.principal.indexOf(44, length);
                        if (iIndexOf3 != -1) {
                            return recoverText(this.principal.substring(length, iIndexOf3).trim());
                        }
                        return recoverText(this.principal.substring(length).trim());
                    }
                }
            } else {
                return null;
            }
        }
    }

    private static String recoverText(String str) {
        if (str.length() < 5 || str.charAt(0) != '#') {
            return str;
        }
        return decodeHexadecimal(str.substring(5));
    }

    private static String decodeHexadecimal(String str) {
        return new String(hexStringToByteArray(str), StandardCharsets.UTF_8);
    }

    public static byte[] hexStringToByteArray(String str) {
        int length = str.length();
        byte[] bArr = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            bArr[i / 2] = (byte) ((Character.digit(str.charAt(i), 16) << 4) + Character.digit(str.charAt(i + 1), 16));
        }
        return bArr;
    }
}
