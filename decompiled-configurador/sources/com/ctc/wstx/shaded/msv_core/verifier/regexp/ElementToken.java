package com.ctc.wstx.shaded.msv_core.verifier.regexp;

import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/verifier/regexp/ElementToken.class */
public class ElementToken extends Token {
    final ElementExp[] acceptedPatterns;

    public ElementToken(ElementExp[] acceptedPatterns) {
        this.acceptedPatterns = acceptedPatterns;
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.regexp.Token
    public boolean match(ElementExp exp) {
        for (int i = 0; i < this.acceptedPatterns.length; i++) {
            if (this.acceptedPatterns[i] == exp) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        String s = "ElementToken";
        for (int i = 0; i < this.acceptedPatterns.length; i++) {
            s = s + "/" + this.acceptedPatterns[i].getNameClass().toString();
        }
        return s;
    }
}
