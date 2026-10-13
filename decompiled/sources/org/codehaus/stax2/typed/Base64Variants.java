package org.codehaus.stax2.typed;

import com.sun.jna.platform.win32.WinNT;
import org.slf4j.Marker;

/* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/typed/Base64Variants.class */
public final class Base64Variants {
    static final String STD_BASE64_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    public static final Base64Variant MIME = new Base64Variant("MIME", STD_BASE64_ALPHABET, true, '=', 76);
    public static final Base64Variant MIME_NO_LINEFEEDS = new Base64Variant(MIME, "MIME-NO-LINEFEEDS", WinNT.MAXLONG);
    public static final Base64Variant PEM = new Base64Variant(MIME, "PEM", true, '=', 64);
    public static final Base64Variant MODIFIED_FOR_URL;

    static {
        StringBuffer sb = new StringBuffer(STD_BASE64_ALPHABET);
        sb.setCharAt(sb.indexOf(Marker.ANY_NON_NULL_MARKER), '-');
        sb.setCharAt(sb.indexOf("/"), '_');
        MODIFIED_FOR_URL = new Base64Variant("MODIFIED-FOR-URL", sb.toString(), false, (char) 0, WinNT.MAXLONG);
    }

    public static Base64Variant getDefaultVariant() {
        return MIME;
    }
}
