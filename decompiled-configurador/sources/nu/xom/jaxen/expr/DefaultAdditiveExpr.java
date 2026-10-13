package nu.xom.jaxen.expr;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/jaxen/expr/DefaultAdditiveExpr.class */
abstract class DefaultAdditiveExpr extends DefaultArithExpr implements AdditiveExpr {
    DefaultAdditiveExpr(Expr expr, Expr expr2) {
        super(expr, expr2);
    }

    @Override // nu.xom.jaxen.expr.DefaultArithExpr, nu.xom.jaxen.expr.DefaultBinaryExpr
    public String toString() {
        return new StringBuffer().append("[(").append(getClass().getName()).append("): ").append(getLHS()).append(", ").append(getRHS()).append("]").toString();
    }
}
