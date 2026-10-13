package com.ctc.wstx.shaded.msv_core.grammar;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/grammar/UnaryExp.class */
public abstract class UnaryExp extends Expression {
    public final Expression exp;
    private static final long serialVersionUID = 1;

    protected UnaryExp(Expression exp) {
        super(exp.hashCode());
        this.exp = exp;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    protected final int calcHashCode() {
        return this.exp.hashCode();
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public boolean equals(Object o) {
        return getClass().equals(o.getClass()) && ((UnaryExp) o).exp == this.exp;
    }
}
