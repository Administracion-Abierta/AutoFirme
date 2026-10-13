package com.ctc.wstx.shaded.msv_core.reader;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.util.StartTagInfo;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/ExpressionWithChildState.class */
public abstract class ExpressionWithChildState extends ExpressionState implements ExpressionOwner {
    protected Expression exp;

    protected abstract Expression castExpression(Expression expression, Expression expression2);

    @Override // com.ctc.wstx.shaded.msv_core.reader.State
    protected void startSelf() {
        super.startSelf();
        this.exp = initialExpression();
    }

    protected Expression initialExpression() {
        return null;
    }

    protected Expression defaultExpression() {
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionOwner
    public final void onEndChild(Expression childExpression) {
        this.exp = castExpression(this.exp, childExpression);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionState
    protected final Expression makeExpression() {
        if (this.exp == null) {
            this.exp = defaultExpression();
        }
        if (this.exp == null) {
            this.reader.reportError(GrammarReader.ERR_MISSING_CHILD_EXPRESSION);
            this.exp = Expression.nullSet;
        }
        return annealExpression(this.exp);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.SimpleState
    protected State createChildState(StartTagInfo tag) {
        return this.reader.createExpressionChildState(this, tag);
    }

    protected Expression annealExpression(Expression exp) {
        return exp;
    }
}
