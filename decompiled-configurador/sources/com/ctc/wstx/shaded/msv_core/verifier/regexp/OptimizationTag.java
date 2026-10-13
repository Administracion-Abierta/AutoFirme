package com.ctc.wstx.shaded.msv_core.verifier.regexp;

import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import java.util.Hashtable;
import java.util.Map;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/verifier/regexp/OptimizationTag.class */
final class OptimizationTag {
    public static final int STRING_NOTCOMPUTED = -1;
    Expression attributePrunedExpression;
    int stringCareLevel = -1;
    final Map simpleElementTokenResidual = new Hashtable();
    final Map transitions = new Hashtable();

    OptimizationTag() {
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/verifier/regexp/OptimizationTag$OwnerAndCont.class */
    protected static final class OwnerAndCont {
        final ElementExp owner;
        final Expression continuation;

        public OwnerAndCont(ElementExp owner, Expression cont) {
            this.owner = owner;
            this.continuation = cont;
        }
    }
}
