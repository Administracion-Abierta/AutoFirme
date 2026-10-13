package org.bouncycastle.asn1;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.bouncycastle.util.Longs;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/LocaleUtil.class */
public class LocaleUtil {
    private static final Map localeCache = new HashMap();
    public static Locale EN_Locale = forEN();

    private static Locale forEN() {
        if ("en".equalsIgnoreCase(Locale.getDefault().getLanguage())) {
            return Locale.getDefault();
        }
        Locale[] availableLocales = Locale.getAvailableLocales();
        for (int i = 0; i != availableLocales.length; i++) {
            if ("en".equalsIgnoreCase(availableLocales[i].getLanguage())) {
                return availableLocales[i];
            }
        }
        return Locale.getDefault();
    }

    static Date epochAdjust(Date date) throws ParseException {
        Locale locale = Locale.getDefault();
        if (locale == null) {
            return date;
        }
        synchronized (localeCache) {
            Long lLongValueOf = (Long) localeCache.get(locale);
            if (lLongValueOf == null) {
                lLongValueOf = longValueOf(new SimpleDateFormat("yyyyMMddHHmmssz").parse("19700101000000GMT+00:00").getTime());
                localeCache.put(locale, lLongValueOf);
            }
            if (lLongValueOf.longValue() == 0) {
                return date;
            }
            return new Date(date.getTime() - lLongValueOf.longValue());
        }
    }

    private static Long longValueOf(long j) {
        return Longs.valueOf(j);
    }
}
