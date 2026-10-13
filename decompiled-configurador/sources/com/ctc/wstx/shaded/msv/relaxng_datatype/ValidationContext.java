package com.ctc.wstx.shaded.msv.relaxng_datatype;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv/relaxng_datatype/ValidationContext.class */
public interface ValidationContext {
    String resolveNamespacePrefix(String str);

    String getBaseUri();

    boolean isUnparsedEntity(String str);

    boolean isNotation(String str);
}
