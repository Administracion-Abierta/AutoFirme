package com.twelvemonkeys.util.convert;

import com.twelvemonkeys.lang.StringUtil;
import com.twelvemonkeys.util.Time;
import com.twelvemonkeys.util.TimeFormat;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/convert/TimeConverter.class */
public class TimeConverter extends NumberConverter {
    @Override // com.twelvemonkeys.util.convert.NumberConverter, com.twelvemonkeys.util.convert.PropertyConverter
    public Object toObject(String str, Class cls, String str2) throws ConversionException {
        TimeFormat timeFormat;
        if (StringUtil.isEmpty(str)) {
            return null;
        }
        try {
            if (str2 == null) {
                timeFormat = TimeFormat.getInstance();
            } else {
                timeFormat = getTimeFormat(str2);
            }
            return timeFormat.parse(str);
        } catch (RuntimeException e) {
            throw new ConversionException(e);
        }
    }

    @Override // com.twelvemonkeys.util.convert.NumberConverter, com.twelvemonkeys.util.convert.PropertyConverter
    public String toString(Object obj, String str) throws ConversionException {
        if (obj == null) {
            return null;
        }
        if (!(obj instanceof Time)) {
            throw new TypeMismathException(obj.getClass());
        }
        try {
            if (StringUtil.isEmpty(str)) {
                return obj.toString();
            }
            return getTimeFormat(str).format((Time) obj);
        } catch (RuntimeException e) {
            throw new ConversionException(e);
        }
    }

    private TimeFormat getTimeFormat(String str) {
        return (TimeFormat) getFormat(TimeFormat.class, str);
    }
}
