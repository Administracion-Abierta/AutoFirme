package com.ctc.wstx.shaded.msv_core.grammar;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/ReferenceExp.class */
public class ReferenceExp extends Expression {
    public Expression exp;
    public final String name;
    private static final long serialVersionUID = 1;

    public ReferenceExp(String name) {
        this.exp = null;
        this.name = name;
    }

    public ReferenceExp(String name, Expression exp) {
        this(name);
        this.exp = exp;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    protected final int calcHashCode() {
        return System.identityHashCode(this);
    }

    public boolean isDefined() {
        return this.exp != null;
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
        return visitor.onRef(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public final Expression visit(ExpressionVisitorExpression visitor) {
        return visitor.onRef(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public final boolean visit(ExpressionVisitorBoolean visitor) {
        return visitor.onRef(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public final void visit(ExpressionVisitorVoid visitor) {
        visitor.onRef(this);
    }
}
