package com.ctc.wstx.shaded.msv_core.grammar.relax;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/grammar/relax/RELAXExpressionVisitorExpression.class */
public interface RELAXExpressionVisitorExpression extends ExpressionVisitorExpression {
    Expression onAttPool(AttPoolClause attPoolClause);

    Expression onTag(TagClause tagClause);

    Expression onElementRules(ElementRules elementRules);

    Expression onHedgeRules(HedgeRules hedgeRules);
}
