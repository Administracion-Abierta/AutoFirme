package com.twelvemonkeys.util.convert;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/convert/NoAvailableConverterException.class */
public class NoAvailableConverterException extends ConversionException {
    public NoAvailableConverterException() {
        super("Cannot convert, no converter available for given type");
    }

    public NoAvailableConverterException(String str) {
        super(str);
    }
}
