package com.ctc.wstx.shaded.msv_core.grammar;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/ExpressionCloner.class */
public abstract class ExpressionCloner implements ExpressionVisitorExpression {
    protected final ExpressionPool pool;

    protected ExpressionCloner(ExpressionPool pool) {
        this.pool = pool;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
    public Expression onChoice(ChoiceExp exp) {
        Expression np1 = exp.exp1.visit(this);
        Expression np2 = exp.exp2.visit(this);
        return (exp.exp1 == np1 && exp.exp2 == np2) ? exp : this.pool.createChoice(np1, np2);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
    public Expression onOneOrMore(OneOrMoreExp exp) {
        Expression np = exp.exp.visit(this);
        return exp.exp == np ? exp : this.pool.createOneOrMore(np);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
    public Expression onMixed(MixedExp exp) {
        Expression body = exp.exp.visit(this);
        return exp.exp == body ? exp : this.pool.createMixed(body);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
    public Expression onList(ListExp exp) {
        Expression body = exp.exp.visit(this);
        return exp.exp == body ? exp : this.pool.createList(body);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
    public Expression onSequence(SequenceExp exp) {
        Expression np1 = exp.exp1.visit(this);
        Expression np2 = exp.exp2.visit(this);
        return (exp.exp1 == np1 && exp.exp2 == np2) ? exp : this.pool.createSequence(np1, np2);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
    public Expression onConcur(ConcurExp exp) {
        return this.pool.createConcur(exp.exp1.visit(this), exp.exp2.visit(this));
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
    public Expression onInterleave(InterleaveExp exp) {
        return this.pool.createInterleave(exp.exp1.visit(this), exp.exp2.visit(this));
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
    public Expression onEpsilon() {
        return Expression.epsilon;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
    public Expression onNullSet() {
        return Expression.nullSet;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
    public Expression onAnyString() {
        return Expression.anyString;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
    public Expression onData(DataExp exp) {
        return exp;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.ExpressionVisitorExpression
    public Expression onValue(ValueExp exp) {
        return exp;
    }
}
