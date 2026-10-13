package nu.xom.jaxen.expr;

import nu.xom.jaxen.Context;
import nu.xom.jaxen.JaxenException;
import nu.xom.jaxen.function.NumberFunction;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/expr/DefaultUnaryExpr.class */
class DefaultUnaryExpr extends DefaultExpr implements UnaryExpr {
    private static final long serialVersionUID = 2303714238683092334L;
    private Expr expr;

    DefaultUnaryExpr(Expr expr) {
        this.expr = expr;
    }

    @Override // nu.xom.jaxen.expr.UnaryExpr
    public Expr getExpr() {
        return this.expr;
    }

    public String toString() {
        return new StringBuffer().append("[(DefaultUnaryExpr): ").append(getExpr()).append("]").toString();
    }

    @Override // nu.xom.jaxen.expr.Expr
    public String getText() {
        return new StringBuffer().append("-(").append(getExpr().getText()).append(")").toString();
    }

    @Override // nu.xom.jaxen.expr.DefaultExpr, nu.xom.jaxen.expr.Expr
    public Expr simplify() {
        this.expr = this.expr.simplify();
        return this;
    }

    @Override // nu.xom.jaxen.expr.Expr
    public Object evaluate(Context context) throws JaxenException {
        return new Double(NumberFunction.evaluate(getExpr().evaluate(context), context.getNavigator()).doubleValue() * (-1.0d));
    }
}
