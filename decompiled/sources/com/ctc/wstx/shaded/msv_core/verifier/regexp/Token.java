package com.ctc.wstx.shaded.msv_core.verifier.regexp;

import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.DataExp;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.ListExp;
import com.ctc.wstx.shaded.msv_core.grammar.ValueExp;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/verifier/regexp/Token.class */
public abstract class Token {
    public boolean match(ElementExp p) {
        return false;
    }

    public boolean match(AttributeExp p) {
        return false;
    }

    public boolean match(DataExp p) {
        return false;
    }

    public boolean match(ValueExp p) {
        return false;
    }

    public boolean match(ListExp p) {
        return false;
    }

    public boolean matchAnyString() {
        return false;
    }

    boolean isIgnorable() {
        return false;
    }
}
