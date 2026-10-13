package es.uji.crypto.xades.jxades.util;

import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/util/ISO8601DateFormat.class */
public class ISO8601DateFormat extends SimpleDateFormat {
    public ISO8601DateFormat() {
        super("yyyy-MM-dd'T'HH:mm:ssZ");
    }

    @Override // java.text.SimpleDateFormat, java.text.DateFormat
    public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
        StringBuffer sb = super.format(date, toAppendTo, fieldPosition);
        int size = sb.length();
        sb.insert(size - 2, ':');
        return sb;
    }

    @Override // java.text.SimpleDateFormat, java.text.DateFormat
    public Date parse(String source, ParsePosition pos) {
        int size = source.length();
        if (source.charAt(size - 3) == ':') {
            source = source.substring(0, size - 3) + source.substring(size - 2);
        }
        return super.parse(source, pos);
    }
}
