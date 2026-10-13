package org.bouncycastle.i18n;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.TimeZone;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/i18n/TextBundle.class */
public class TextBundle extends LocalizedMessage {
    public static final String TEXT_ENTRY = "text";

    public TextBundle(String str, String str2) throws NullPointerException {
        super(str, str2);
    }

    public TextBundle(String str, String str2, String str3) throws UnsupportedEncodingException, NullPointerException {
        super(str, str2, str3);
    }

    public TextBundle(String str, String str2, Object[] objArr) throws NullPointerException {
        super(str, str2, objArr);
    }

    public TextBundle(String str, String str2, String str3, Object[] objArr) throws UnsupportedEncodingException, NullPointerException {
        super(str, str2, str3, objArr);
    }

    public String getText(Locale locale, TimeZone timeZone) throws MissingEntryException {
        return getEntry("text", locale, timeZone);
    }

    public String getText(Locale locale) throws MissingEntryException {
        return getEntry("text", locale, TimeZone.getDefault());
    }
}
