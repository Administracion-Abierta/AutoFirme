package com.ctc.wstx.shaded.msv_core.grammar;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/grammar/AttributeExp.class */
public class AttributeExp extends Expression implements NameClassAndExpression {
    public final NameClass nameClass;
    public final Expression exp;
    private static final long serialVersionUID = 1;

    @Override // com.ctc.wstx.shaded.msv_core.grammar.NameClassAndExpression
    public final NameClass getNameClass() {
        return this.nameClass;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.NameClassAndExpression
    public final Expression getContentModel() {
        return this.exp;
    }

    public AttributeExp(NameClass nameClass, Expression exp) {
        super(nameClass.hashCode() + exp.hashCode());
        this.nameClass = nameClass;
        this.exp = exp;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    protected final int calcHashCode() {
        return this.nameClass.hashCode() + this.exp.hashCode();
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public boolean equals(Object o) {
        if (o.getClass() != AttributeExp.class) {
            return false;
        }
        AttributeExp rhs = (AttributeExp) o;
        return rhs.nameClass.equals(this.nameClass) && rhs.exp.equals(this.exp);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public Object visit(ExpressionVisitor visitor) {
        return visitor.onAttribute(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public Expression visit(ExpressionVisitorExpression visitor) {
        return visitor.onAttribute(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public boolean visit(ExpressionVisitorBoolean visitor) {
        return visitor.onAttribute(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public void visit(ExpressionVisitorVoid visitor) {
        visitor.onAttribute(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    protected boolean calcEpsilonReducibility() {
        return false;
    }
}
