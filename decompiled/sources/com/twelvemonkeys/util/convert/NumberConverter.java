package com.twelvemonkeys.util.convert;

import com.twelvemonkeys.lang.BeanUtil;
import com.twelvemonkeys.lang.StringUtil;
import com.twelvemonkeys.util.LRUHashMap;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/convert/NumberConverter.class */
public class NumberConverter implements PropertyConverter {
    private static final DecimalFormatSymbols SYMBOLS = new DecimalFormatSymbols(Locale.US);
    private static final NumberFormat sDefaultFormat = new DecimalFormat("#0.#", SYMBOLS);
    private static final Map<String, Format> sFormats = new LRUHashMap(50);

    @Override // com.twelvemonkeys.util.convert.PropertyConverter
    public Object toObject(String str, Class cls, String str2) throws ConversionException {
        NumberFormat numberFormat;
        Number number;
        if (StringUtil.isEmpty(str)) {
            return null;
        }
        try {
            if (cls.equals(BigInteger.class)) {
                return new BigInteger(str);
            }
            if (cls.equals(BigDecimal.class)) {
                return new BigDecimal(str);
            }
            if (str2 == null) {
                numberFormat = sDefaultFormat;
            } else {
                numberFormat = getNumberFormat(str2);
            }
            synchronized (numberFormat) {
                number = numberFormat.parse(str);
            }
            if (cls == Integer.TYPE || cls == Integer.class) {
                return Integer.valueOf(number.intValue());
            }
            if (cls == Long.TYPE || cls == Long.class) {
                return Long.valueOf(number.longValue());
            }
            if (cls == Double.TYPE || cls == Double.class) {
                return Double.valueOf(number.doubleValue());
            }
            if (cls == Float.TYPE || cls == Float.class) {
                return Float.valueOf(number.floatValue());
            }
            if (cls == Byte.TYPE || cls == Byte.class) {
                return Byte.valueOf(number.byteValue());
            }
            if (cls == Short.TYPE || cls == Short.class) {
                return Short.valueOf(number.shortValue());
            }
            return number;
        } catch (RuntimeException e) {
            throw new ConversionException(e);
        } catch (ParseException e2) {
            throw new ConversionException(e2);
        }
    }

    @Override // com.twelvemonkeys.util.convert.PropertyConverter
    public String toString(Object obj, String str) throws ConversionException {
        String str2;
        if (obj == null) {
            return null;
        }
        if (!(obj instanceof Number)) {
            throw new TypeMismathException(obj.getClass());
        }
        try {
            if (StringUtil.isEmpty(str)) {
                return sDefaultFormat.format(obj);
            }
            NumberFormat numberFormat = getNumberFormat(str);
            synchronized (numberFormat) {
                str2 = numberFormat.format(obj);
            }
            return str2;
        } catch (RuntimeException e) {
            throw new ConversionException(e);
        }
    }

    private NumberFormat getNumberFormat(String str) {
        return (NumberFormat) getFormat(DecimalFormat.class, str, SYMBOLS);
    }

    protected final Format getFormat(Class cls, Object... objArr) {
        Format format;
        synchronized (sFormats) {
            String str = cls.getName() + ":" + Arrays.toString(objArr);
            Format format2 = sFormats.get(str);
            if (format2 == null) {
                try {
                    format2 = (Format) BeanUtil.createInstance(cls, objArr);
                    sFormats.put(str, format2);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
            format = format2;
        }
        return format;
    }
}
