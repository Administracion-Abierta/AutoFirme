package com.ctc.wstx.shaded.msv_core.verifier.regexp;

import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionCloner;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.OtherExp;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/verifier/regexp/AttributePruner.class */
public class AttributePruner extends ExpressionCloner {
    public AttributePruner(ExpressionPool pool) {
        super(pool);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
    public Expression onAttribute(AttributeExp exp) {
        return Expression.nullSet;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
    public Expression onRef(ReferenceExp exp) {
        return exp.exp.visit(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
    public Expression onOther(OtherExp exp) {
        return exp.exp.visit(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
    public Expression onElement(ElementExp exp) {
        return exp;
    }

    public final Expression prune(Expression exp) {
        OptimizationTag ot = (OptimizationTag) exp.verifierTag;
        if (ot == null) {
            OptimizationTag optimizationTag = new OptimizationTag();
            ot = optimizationTag;
            exp.verifierTag = optimizationTag;
        } else if (ot.attributePrunedExpression != null) {
            return ot.attributePrunedExpression;
        }
        Expression r = exp.visit(this);
        ot.attributePrunedExpression = r;
        return r;
    }
}
