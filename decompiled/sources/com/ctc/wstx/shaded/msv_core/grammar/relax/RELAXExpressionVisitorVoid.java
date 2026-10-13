package com.ctc.wstx.shaded.msv_core.grammar.relax;

import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorVoid;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/grammar/relax/RELAXExpressionVisitorVoid.class */
public interface RELAXExpressionVisitorVoid extends ExpressionVisitorVoid {
    void onAttPool(AttPoolClause attPoolClause);

    void onTag(TagClause tagClause);

    void onElementRules(ElementRules elementRules);

    void onHedgeRules(HedgeRules hedgeRules);
}
