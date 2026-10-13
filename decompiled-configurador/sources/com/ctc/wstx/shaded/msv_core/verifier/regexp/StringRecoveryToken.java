package com.ctc.wstx.shaded.msv_core.verifier.regexp;

import com.ctc.wstx.shaded.msv_core.grammar.DataExp;
import com.ctc.wstx.shaded.msv_core.grammar.ListExp;
import com.ctc.wstx.shaded.msv_core.grammar.ValueExp;
import com.ctc.wstx.shaded.msv_core.util.DatatypeRef;
import java.util.HashSet;
import java.util.Set;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/verifier/regexp/StringRecoveryToken.class */
class StringRecoveryToken extends StringToken {
    final Set failedExps;

    StringRecoveryToken(StringToken base) {
        this(base, new HashSet());
    }

    StringRecoveryToken(StringToken base, Set failedExps) {
        super(base.resCalc, base.literal, base.context, (DatatypeRef) null);
        this.failedExps = failedExps;
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.regexp.StringToken, com.ctc.wstx.shaded.msv_core.verifier.regexp.Token
    public boolean match(DataExp exp) {
        if (super.match(exp)) {
            return true;
        }
        this.failedExps.add(exp);
        return true;
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.regexp.StringToken, com.ctc.wstx.shaded.msv_core.verifier.regexp.Token
    public boolean match(ValueExp exp) {
        if (super.match(exp)) {
            return true;
        }
        this.failedExps.add(exp);
        return true;
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.regexp.StringToken, com.ctc.wstx.shaded.msv_core.verifier.regexp.Token
    public boolean match(ListExp exp) {
        super.match(exp);
        return true;
    }

    @Override // com.ctc.wstx.shaded.msv_core.verifier.regexp.StringToken
    protected StringToken createChildStringToken(String literal, DatatypeRef dtRef) {
        return new StringRecoveryToken(new StringToken(this.resCalc, literal, this.context, dtRef));
    }
}
