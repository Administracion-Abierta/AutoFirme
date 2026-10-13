package com.ctc.wstx.shaded.msv_core.grammar;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/grammar/ElementExp.class */
public abstract class ElementExp extends Expression implements NameClassAndExpression {
    public Expression contentModel;
    public boolean ignoreUndeclaredAttributes;
    private static final long serialVersionUID = 1;

    @Override // com.ctc.wstx.shaded.msv_core.grammar.NameClassAndExpression
    public abstract NameClass getNameClass();

    @Override // com.ctc.wstx.shaded.msv_core.grammar.NameClassAndExpression
    public final Expression getContentModel() {
        return this.contentModel;
    }

    public ElementExp(Expression contentModel, boolean ignoreUndeclaredAttributes) {
        super(contentModel.hashCode());
        this.contentModel = contentModel;
        this.ignoreUndeclaredAttributes = ignoreUndeclaredAttributes;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    protected final int calcHashCode() {
        return this.contentModel.hashCode();
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public final boolean equals(Object o) {
        return this == o;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public final Object visit(ExpressionVisitor visitor) {
        return visitor.onElement(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public final Expression visit(ExpressionVisitorExpression visitor) {
        return visitor.onElement(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public final boolean visit(ExpressionVisitorBoolean visitor) {
        return visitor.onElement(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public final void visit(ExpressionVisitorVoid visitor) {
        visitor.onElement(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    protected final boolean calcEpsilonReducibility() {
        return false;
    }
}
