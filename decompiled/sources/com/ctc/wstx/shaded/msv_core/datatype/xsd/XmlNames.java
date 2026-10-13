package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.xsd_util.XmlChars;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/xsd/XmlNames.class */
public class XmlNames {
    private XmlNames() {
    }

    public static boolean isName(String value) {
        if (value == null || value.length() == 0) {
            return false;
        }
        char c = value.charAt(0);
        if (!XmlChars.isLetter(c) && c != '_' && c != ':') {
            return false;
        }
        for (int i = 1; i < value.length(); i++) {
            if (!XmlChars.isNameChar(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isUnqualifiedName(String value) {
        if (value == null || value.length() == 0) {
            return false;
        }
        char c = value.charAt(0);
        if (!XmlChars.isLetter(c) && c != '_') {
            return false;
        }
        for (int i = 1; i < value.length(); i++) {
            if (!XmlChars.isNCNameChar(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isQualifiedName(String value) {
        if (value == null || value.length() == 0) {
            return false;
        }
        int first = value.indexOf(58);
        if (first <= 0) {
            return isUnqualifiedName(value);
        }
        int last = value.lastIndexOf(58);
        return last == first && isUnqualifiedName(value.substring(0, first)) && isUnqualifiedName(value.substring(first + 1));
    }

    public static boolean isNmtoken(String token) {
        if (token == null || token.length() == 0) {
            return false;
        }
        int length = token.length();
        for (int i = 0; i < length; i++) {
            if (!XmlChars.isNameChar(token.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNCNmtoken(String token) {
        return isNmtoken(token) && token.indexOf(58) < 0;
    }

    public static boolean isNCName(String token) {
        return isName(token) && token.indexOf(58) < 0;
    }
}
