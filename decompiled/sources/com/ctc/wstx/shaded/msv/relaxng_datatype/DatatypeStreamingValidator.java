package com.ctc.wstx.shaded.msv.relaxng_datatype;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv/relaxng_datatype/DatatypeStreamingValidator.class */
public interface DatatypeStreamingValidator {
    void addCharacters(char[] cArr, int i, int i2);

    boolean isValid();

    void checkValid() throws DatatypeException;
}
