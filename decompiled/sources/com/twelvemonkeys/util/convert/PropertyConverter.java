package com.twelvemonkeys.util.convert;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/convert/PropertyConverter.class */
public interface PropertyConverter {
    Object toObject(String str, Class cls, String str2) throws ConversionException;

    String toString(Object obj, String str) throws ConversionException;
}
