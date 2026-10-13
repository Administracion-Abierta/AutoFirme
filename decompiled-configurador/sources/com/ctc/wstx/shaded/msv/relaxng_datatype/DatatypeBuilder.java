package com.ctc.wstx.shaded.msv.relaxng_datatype;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv/relaxng_datatype/DatatypeBuilder.class */
public interface DatatypeBuilder {
    void addParameter(String str, String str2, ValidationContext validationContext) throws DatatypeException;

    Datatype createDatatype() throws DatatypeException;
}
