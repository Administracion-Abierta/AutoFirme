package com.ctc.wstx.shaded.msv_core.verifier.regexp;

import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/verifier/regexp/AnyElementToken.class */
public final class AnyElementToken extends ElementToken {
    public static final Token theInstance = new AnyElementToken();

    private AnyElementToken() {
        super(null);
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.regexp.ElementToken, com.ctc.wstx.shaded.msv_core.verifier.regexp.Token
    public boolean match(ElementExp exp) {
        return true;
    }
}
