package org.bouncycastle.pkix.util;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.TimeZone;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pkix/util/MessageBundle.class */
public class MessageBundle extends TextBundle {
    public static final String TITLE_ENTRY = "title";

    public MessageBundle(String str, String str2) throws NullPointerException {
        super(str, str2);
    }

    public MessageBundle(String str, String str2, String str3) throws UnsupportedEncodingException, NullPointerException {
        super(str, str2, str3);
    }

    public MessageBundle(String str, String str2, Object[] objArr) throws NullPointerException {
        super(str, str2, objArr);
    }

    public MessageBundle(String str, String str2, String str3, Object[] objArr) throws UnsupportedEncodingException, NullPointerException {
        super(str, str2, str3, objArr);
    }

    public String getTitle(Locale locale, TimeZone timeZone) throws MissingEntryException {
        return getEntry("title", locale, timeZone);
    }

    public String getTitle(Locale locale) throws MissingEntryException {
        return getEntry("title", locale, TimeZone.getDefault());
    }
}
