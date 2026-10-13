package nu.xom.jaxen.expr;

import nu.xom.jaxen.Context;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/expr/DefaultNumberExpr.class */
class DefaultNumberExpr extends DefaultExpr implements NumberExpr {
    private static final long serialVersionUID = -6021898973386269611L;
    private Double number;

    DefaultNumberExpr(Double d) {
        this.number = d;
    }

    @Override // nu.xom.jaxen.expr.NumberExpr
    public Number getNumber() {
        return this.number;
    }

    public String toString() {
        return new StringBuffer().append("[(DefaultNumberExpr): ").append(getNumber()).append("]").toString();
    }

    @Override // nu.xom.jaxen.expr.Expr
    public String getText() {
        return getNumber().toString();
    }

    @Override // nu.xom.jaxen.expr.Expr
    public Object evaluate(Context context) {
        return getNumber();
    }
}
