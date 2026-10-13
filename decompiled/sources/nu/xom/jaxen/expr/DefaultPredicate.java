package nu.xom.jaxen.expr;

import nu.xom.jaxen.Context;
import nu.xom.jaxen.JaxenException;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/expr/DefaultPredicate.class */
class DefaultPredicate implements Predicate {
    private static final long serialVersionUID = -4140068594075364971L;
    private Expr expr;

    DefaultPredicate(Expr expr) {
        setExpr(expr);
    }

    @Override // nu.xom.jaxen.expr.Predicate
    public Expr getExpr() {
        return this.expr;
    }

    @Override // nu.xom.jaxen.expr.Predicate
    public void setExpr(Expr expr) {
        this.expr = expr;
    }

    @Override // nu.xom.jaxen.expr.Predicate
    public String getText() {
        return new StringBuffer().append("[").append(getExpr().getText()).append("]").toString();
    }

    public String toString() {
        return new StringBuffer().append("[(DefaultPredicate): ").append(getExpr()).append("]").toString();
    }

    @Override // nu.xom.jaxen.expr.Predicate
    public void simplify() {
        setExpr(getExpr().simplify());
    }

    @Override // nu.xom.jaxen.expr.Predicate
    public Object evaluate(Context context) throws JaxenException {
        return getExpr().evaluate(context);
    }
}
