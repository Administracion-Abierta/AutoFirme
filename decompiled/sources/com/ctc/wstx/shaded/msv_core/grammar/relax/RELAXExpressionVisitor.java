package com.ctc.wstx.shaded.msv_core.grammar.relax;

import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitor;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/grammar/relax/RELAXExpressionVisitor.class */
public interface RELAXExpressionVisitor extends ExpressionVisitor {
    Object onAttPool(AttPoolClause attPoolClause);

    Object onTag(TagClause tagClause);

    Object onElementRules(ElementRules elementRules);

    Object onHedgeRules(HedgeRules hedgeRules);
}
