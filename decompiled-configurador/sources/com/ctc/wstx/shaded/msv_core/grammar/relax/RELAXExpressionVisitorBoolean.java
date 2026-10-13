package com.ctc.wstx.shaded.msv_core.grammar.relax;

import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorBoolean;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/relax/RELAXExpressionVisitorBoolean.class */
public interface RELAXExpressionVisitorBoolean extends ExpressionVisitorBoolean {
    boolean onAttPool(AttPoolClause attPoolClause);

    boolean onTag(TagClause tagClause);

    boolean onElementRules(ElementRules elementRules);

    boolean onHedgeRules(HedgeRules hedgeRules);
}
