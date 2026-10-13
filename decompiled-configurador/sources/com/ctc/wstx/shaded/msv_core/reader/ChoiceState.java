package com.ctc.wstx.shaded.msv_core.reader;

import com.ctc.wstx.shaded.msv_core.grammar.Expression;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/ChoiceState.class */
public class ChoiceState extends ExpressionWithChildState {
    protected boolean allowEmptyChoice;

    public ChoiceState() {
        this(false);
    }

    public ChoiceState(boolean allowEmptyChoice) {
        this.allowEmptyChoice = allowEmptyChoice;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState
    protected Expression initialExpression() {
        if (this.allowEmptyChoice) {
            return Expression.nullSet;
        }
        return null;
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionWithChildState
    protected Expression castExpression(Expression exp, Expression child) {
        return exp == null ? child : this.reader.pool.createChoice(exp, child);
    }
}
