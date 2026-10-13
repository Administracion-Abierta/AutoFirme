package org.spongycastle.i18n;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.TimeZone;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/i18n/ErrorBundle.class */
public class ErrorBundle extends MessageBundle {
    public static final String SUMMARY_ENTRY = "summary";
    public static final String DETAIL_ENTRY = "details";

    public ErrorBundle(String resource, String id) throws NullPointerException {
        super(resource, id);
    }

    public ErrorBundle(String resource, String id, String encoding) throws UnsupportedEncodingException, NullPointerException {
        super(resource, id, encoding);
    }

    public ErrorBundle(String resource, String id, Object[] arguments) throws NullPointerException {
        super(resource, id, arguments);
    }

    public ErrorBundle(String resource, String id, String encoding, Object[] arguments) throws UnsupportedEncodingException, NullPointerException {
        super(resource, id, encoding, arguments);
    }

    public String getSummary(Locale loc, TimeZone timezone) throws MissingEntryException {
        return getEntry("summary", loc, timezone);
    }

    public String getSummary(Locale loc) throws MissingEntryException {
        return getEntry("summary", loc, TimeZone.getDefault());
    }

    public String getDetail(Locale loc, TimeZone timezone) throws MissingEntryException {
        return getEntry("details", loc, timezone);
    }

    public String getDetail(Locale loc) throws MissingEntryException {
        return getEntry("details", loc, TimeZone.getDefault());
    }
}
