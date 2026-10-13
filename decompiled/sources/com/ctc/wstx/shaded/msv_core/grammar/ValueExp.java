package com.ctc.wstx.shaded.msv_core.grammar;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv_core.util.StringPair;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/grammar/ValueExp.class */
public final class ValueExp extends Expression implements DataOrValueExp {
    public final Datatype dt;
    public final Object value;
    public final StringPair name;
    private static final long serialVersionUID = 1;

    @Override // com.ctc.wstx.shaded.msv_core.grammar.DataOrValueExp
    public Datatype getType() {
        return this.dt;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.DataOrValueExp
    public StringPair getName() {
        return this.name;
    }

    protected ValueExp(Datatype dt, StringPair typeName, Object value) {
        super(dt.hashCode() + dt.valueHashCode(value));
        this.dt = dt;
        this.name = typeName;
        this.value = value;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    protected final int calcHashCode() {
        return this.dt.hashCode() + this.dt.valueHashCode(this.value);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public boolean equals(Object o) {
        if (o == null || o.getClass() != getClass()) {
            return false;
        }
        ValueExp rhs = (ValueExp) o;
        if (rhs.dt.equals(this.dt)) {
            return this.dt.sameValue(this.value, rhs.value);
        }
        return false;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public Object visit(ExpressionVisitor visitor) {
        return visitor.onValue(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public Expression visit(ExpressionVisitorExpression visitor) {
        return visitor.onValue(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public boolean visit(ExpressionVisitorBoolean visitor) {
        return visitor.onValue(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public void visit(ExpressionVisitorVoid visitor) {
        visitor.onValue(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    protected boolean calcEpsilonReducibility() {
        return false;
    }
}
