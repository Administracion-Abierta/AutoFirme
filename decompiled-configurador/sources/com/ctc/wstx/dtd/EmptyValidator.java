package com.ctc.wstx.dtd;

import com.ctc.wstx.util.PrefixedName;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/dtd/EmptyValidator.class */
public class EmptyValidator extends StructValidator {
    static final EmptyValidator sPcdataInstance = new EmptyValidator("No elements allowed in pure #PCDATA content model");
    static final EmptyValidator sEmptyInstance = new EmptyValidator("No elements allowed in EMPTY content model");
    final String mErrorMsg;

    private EmptyValidator(String errorMsg) {
        this.mErrorMsg = errorMsg;
    }

    public static EmptyValidator getPcdataInstance() {
        return sPcdataInstance;
    }

    public static EmptyValidator getEmptyInstance() {
        return sPcdataInstance;
    }

    @Override // com.ctc.wstx.dtd.StructValidator
    public StructValidator newInstance() {
        return this;
    }

    @Override // com.ctc.wstx.dtd.StructValidator
    public String tryToValidate(PrefixedName elemName) {
        return this.mErrorMsg;
    }

    @Override // com.ctc.wstx.dtd.StructValidator
    public String fullyValid() {
        return null;
    }
}
