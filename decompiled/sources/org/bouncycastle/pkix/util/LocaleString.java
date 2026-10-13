package org.bouncycastle.pkix.util;

import java.io.UnsupportedEncodingException;
import java.util.Locale;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pkix/util/LocaleString.class */
public class LocaleString extends LocalizedMessage {
    public LocaleString(String str, String str2) {
        super(str, str2);
    }

    public LocaleString(String str, String str2, String str3) throws UnsupportedEncodingException, NullPointerException {
        super(str, str2, str3);
    }

    public LocaleString(String str, String str2, String str3, Object[] objArr) throws UnsupportedEncodingException, NullPointerException {
        super(str, str2, str3, objArr);
    }

    public String getLocaleString(Locale locale) {
        return getEntry(null, locale, null);
    }
}
