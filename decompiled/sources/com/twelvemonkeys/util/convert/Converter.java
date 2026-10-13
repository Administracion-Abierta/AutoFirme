package com.twelvemonkeys.util.convert;

import com.twelvemonkeys.util.Time;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/convert/Converter.class */
public abstract class Converter implements PropertyConverter {
    protected static final Converter sInstance = new ConverterImpl();
    protected final Map<Class, PropertyConverter> converters = new Hashtable();

    @Override // com.twelvemonkeys.util.convert.PropertyConverter
    public abstract Object toObject(String str, Class cls, String str2) throws ConversionException;

    @Override // com.twelvemonkeys.util.convert.PropertyConverter
    public abstract String toString(Object obj, String str) throws ConversionException;

    static {
        DefaultConverter defaultConverter = new DefaultConverter();
        registerConverter(Object.class, defaultConverter);
        registerConverter(Boolean.TYPE, defaultConverter);
        NumberConverter numberConverter = new NumberConverter();
        registerConverter(Number.class, numberConverter);
        registerConverter(Byte.TYPE, numberConverter);
        registerConverter(Double.TYPE, numberConverter);
        registerConverter(Float.TYPE, numberConverter);
        registerConverter(Integer.TYPE, numberConverter);
        registerConverter(Long.TYPE, numberConverter);
        registerConverter(Short.TYPE, numberConverter);
        registerConverter(Date.class, new DateConverter());
        registerConverter(Time.class, new TimeConverter());
    }

    protected Converter() {
    }

    public static Converter getInstance() {
        return sInstance;
    }

    public static void registerConverter(Class<?> cls, PropertyConverter propertyConverter) {
        getInstance().converters.put(cls, propertyConverter);
    }

    public static void unregisterConverter(Class<?> cls) {
        getInstance().converters.remove(cls);
    }

    public Object toObject(String str, Class cls) throws ConversionException {
        return toObject(str, cls, null);
    }

    public String toString(Object obj) throws ConversionException {
        return toString(obj, null);
    }
}
