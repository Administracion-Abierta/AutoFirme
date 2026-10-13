package com.twelvemonkeys.util.convert;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/convert/TypeMismathException.class */
public class TypeMismathException extends ConversionException {
    public TypeMismathException(Class cls) {
        super("Wrong type for conversion: " + cls.getName());
    }
}
