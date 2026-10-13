package com.twelvemonkeys.util.convert;

import com.twelvemonkeys.lang.BeanUtil;
import com.twelvemonkeys.lang.StringUtil;
import java.lang.reflect.InvocationTargetException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/convert/DateConverter.class */
public class DateConverter extends NumberConverter {
    @Override // com.twelvemonkeys.util.convert.NumberConverter, com.twelvemonkeys.util.convert.PropertyConverter
    public Object toObject(String str, Class cls, String str2) throws ConversionException {
        DateFormat dateFormat;
        if (StringUtil.isEmpty(str)) {
            return null;
        }
        try {
            if (str2 == null) {
                dateFormat = DateFormat.getDateTimeInstance();
            } else {
                dateFormat = getDateFormat(str2);
            }
            Date date = StringUtil.toDate(str, dateFormat);
            if (cls != Date.class) {
                try {
                    date = (Date) BeanUtil.createInstance(cls, new Long(date.getTime()));
                } catch (ClassCastException e) {
                    throw new TypeMismathException(cls);
                } catch (InvocationTargetException e2) {
                    throw new ConversionException(e2);
                }
            }
            return date;
        } catch (RuntimeException e3) {
            throw new ConversionException(e3);
        }
    }

    @Override // com.twelvemonkeys.util.convert.NumberConverter, com.twelvemonkeys.util.convert.PropertyConverter
    public String toString(Object obj, String str) throws ConversionException {
        if (obj == null) {
            return null;
        }
        if (!(obj instanceof Date)) {
            throw new TypeMismathException(obj.getClass());
        }
        try {
            if (StringUtil.isEmpty(str)) {
                return DateFormat.getDateTimeInstance().format(obj);
            }
            return getDateFormat(str).format(obj);
        } catch (RuntimeException e) {
            throw new ConversionException(e);
        }
    }

    private DateFormat getDateFormat(String str) {
        return (DateFormat) getFormat(SimpleDateFormat.class, str, Locale.US);
    }
}
