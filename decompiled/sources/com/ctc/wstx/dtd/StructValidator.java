package com.ctc.wstx.dtd;

import com.ctc.wstx.util.PrefixedName;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/dtd/StructValidator.class */
public abstract class StructValidator {
    public abstract StructValidator newInstance();

    public abstract String tryToValidate(PrefixedName prefixedName);

    public abstract String fullyValid();
}
