package nu.xom.jaxen.expr;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/expr/DefaultArithExpr.class */
abstract class DefaultArithExpr extends DefaultBinaryExpr {
    DefaultArithExpr(Expr expr, Expr expr2) {
        super(expr, expr2);
    }

    @Override // nu.xom.jaxen.expr.DefaultBinaryExpr
    public String toString() {
        return new StringBuffer().append("[(DefaultArithExpr): ").append(getLHS()).append(", ").append(getRHS()).append("]").toString();
    }
}
