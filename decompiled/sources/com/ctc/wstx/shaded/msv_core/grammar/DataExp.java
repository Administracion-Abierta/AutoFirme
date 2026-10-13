package com.ctc.wstx.shaded.msv_core.grammar;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.util.StringPair;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/grammar/DataExp.class */
public final class DataExp extends Expression implements DataOrValueExp {
    public final Datatype dt;
    public final StringPair name;
    public final Expression except;
    private static final long serialVersionUID = 1;

    @Override // com.ctc.wstx.shaded.msv_core.grammar.DataOrValueExp
    public Datatype getType() {
        return this.dt;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.DataOrValueExp
    public StringPair getName() {
        return this.name;
    }

    protected DataExp(Datatype dt, StringPair typeName, Expression except) {
        super(dt.hashCode() + except.hashCode());
        this.dt = dt;
        this.name = typeName;
        this.except = except;
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    protected final int calcHashCode() {
        return this.dt.hashCode() + this.except.hashCode();
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public boolean equals(Object o) {
        if (o == null || o.getClass() != getClass()) {
            return false;
        }
        DataExp rhs = (DataExp) o;
        if (this.except != rhs.except) {
            return false;
        }
        return rhs.dt.equals(this.dt);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public Object visit(ExpressionVisitor visitor) {
        return visitor.onData(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public Expression visit(ExpressionVisitorExpression visitor) {
        return visitor.onData(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public boolean visit(ExpressionVisitorBoolean visitor) {
        return visitor.onData(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    public void visit(ExpressionVisitorVoid visitor) {
        visitor.onData(this);
    }

    @Override // com.ctc.wstx.shaded.msv_core.grammar.Expression
    protected boolean calcEpsilonReducibility() {
        XSDatatype xdt = (XSDatatype) this.dt;
        if (this.except == Expression.nullSet && xdt.isAlwaysValid()) {
            return true;
        }
        return false;
    }
}
