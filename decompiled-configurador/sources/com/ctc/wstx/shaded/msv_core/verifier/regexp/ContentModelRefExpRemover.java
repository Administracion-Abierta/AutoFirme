package com.ctc.wstx.shaded.msv_core.verifier.regexp;

import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionCloner;
import com.ctc.wstx.shaded.msv_core.grammar.ExpressionPool;
import com.ctc.wstx.shaded.msv_core.grammar.OtherExp;
import com.ctc.wstx.shaded.msv_core.grammar.ReferenceExp;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/verifier/regexp/ContentModelRefExpRemover.class */
public class ContentModelRefExpRemover {

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/verifier/regexp/ContentModelRefExpRemover$Remover.class */
    private static class Remover extends ExpressionCloner {
        public Remover(ExpressionPool pool) {
            super(pool);
        }

        @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
        public Expression onElement(ElementExp exp) {
            return exp;
        }

        @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
        public Expression onAttribute(AttributeExp exp) {
            Expression content = exp.exp.visit(this);
            if (content == Expression.nullSet) {
                return Expression.nullSet;
            }
            return this.pool.createAttribute(exp.nameClass, content);
        }

        @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
        public Expression onRef(ReferenceExp exp) {
            return exp.exp.visit(this);
        }

        @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
        public Expression onOther(OtherExp exp) {
            return exp.exp.visit(this);
        }
    }
}
