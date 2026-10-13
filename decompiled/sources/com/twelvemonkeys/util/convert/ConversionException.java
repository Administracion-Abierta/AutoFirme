package com.twelvemonkeys.util.convert;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/convert/ConversionException.class */
public class ConversionException extends IllegalArgumentException {
    public ConversionException(String str) {
        super(str);
    }

    public ConversionException(Throwable th) {
        super(th != null ? th.getMessage() : null, th);
    }

    public ConversionException(String str, Throwable th) {
        super(str, th);
    }
}
