package com.ctc.wstx.shaded.msv_core.grammar;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/ListExp.class */
public final class ListExp extends UnaryExp {
    private static final long serialVersionUID = 1;

    ListExp(Expression exp) {
        super(exp);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public Object visit(ExpressionVisitor visitor) {
        return visitor.onList(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public Expression visit(ExpressionVisitorExpression visitor) {
        return visitor.onList(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public boolean visit(ExpressionVisitorBoolean visitor) {
        return visitor.onList(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public void visit(ExpressionVisitorVoid visitor) {
        visitor.onList(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    protected boolean calcEpsilonReducibility() {
        return this.exp.isEpsilonReducible();
    }
}
