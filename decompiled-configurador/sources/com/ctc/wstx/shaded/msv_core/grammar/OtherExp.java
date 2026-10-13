package com.ctc.wstx.shaded.msv_core.grammar;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/OtherExp.class */
public class OtherExp extends Expression {
    public Expression exp;
    private static final long serialVersionUID = 1;

    public String printName() {
        String className = getClass().getName();
        int idx = className.lastIndexOf(46);
        if (idx >= 0) {
            className = className.substring(idx + 1);
        }
        return className;
    }

    public OtherExp() {
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    protected final int calcHashCode() {
        return System.identityHashCode(this);
    }

    public OtherExp(Expression exp) {
        this();
        this.exp = exp;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public boolean equals(Object o) {
        return this == o;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    protected boolean calcEpsilonReducibility() {
        if (this.exp == null) {
            return false;
        }
        return this.exp.isEpsilonReducible();
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public final Object visit(ExpressionVisitor visitor) {
        return visitor.onOther(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public final Expression visit(ExpressionVisitorExpression visitor) {
        return visitor.onOther(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public final boolean visit(ExpressionVisitorBoolean visitor) {
        return visitor.onOther(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public final void visit(ExpressionVisitorVoid visitor) {
        visitor.onOther(this);
    }
}
