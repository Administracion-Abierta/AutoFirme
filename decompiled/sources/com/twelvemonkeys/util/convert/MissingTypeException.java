package com.twelvemonkeys.util.convert;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/util/convert/MissingTypeException.class */
public class MissingTypeException extends ConversionException {
    public MissingTypeException() {
        super("Cannot convert, missing type");
    }

    public MissingTypeException(String str) {
        super(str);
    }
}
