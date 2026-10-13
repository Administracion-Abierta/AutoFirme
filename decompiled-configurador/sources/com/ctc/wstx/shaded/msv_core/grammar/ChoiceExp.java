package com.ctc.wstx.shaded.msv_core.grammar;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/ChoiceExp.class */
public final class ChoiceExp extends BinaryExp {
    private static final long serialVersionUID = 1;

    ChoiceExp(Expression left, Expression right) {
        super(left, right);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public Object visit(ExpressionVisitor visitor) {
        return visitor.onChoice(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public Expression visit(ExpressionVisitorExpression visitor) {
        return visitor.onChoice(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public boolean visit(ExpressionVisitorBoolean visitor) {
        return visitor.onChoice(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public void visit(ExpressionVisitorVoid visitor) {
        visitor.onChoice(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    protected boolean calcEpsilonReducibility() {
        return this.exp1.isEpsilonReducible() || this.exp2.isEpsilonReducible();
    }
}
