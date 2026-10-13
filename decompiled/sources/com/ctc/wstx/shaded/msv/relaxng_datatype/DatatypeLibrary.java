package com.ctc.wstx.shaded.msv.relaxng_datatype;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv/relaxng_datatype/DatatypeLibrary.class */
public interface DatatypeLibrary {
    DatatypeBuilder createDatatypeBuilder(String str) throws DatatypeException;

    Datatype createDatatype(String str) throws DatatypeException;
}
