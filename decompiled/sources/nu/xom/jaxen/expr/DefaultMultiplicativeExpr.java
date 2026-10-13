package nu.xom.jaxen.expr;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/expr/DefaultMultiplicativeExpr.class */
abstract class DefaultMultiplicativeExpr extends DefaultArithExpr implements MultiplicativeExpr {
    DefaultMultiplicativeExpr(Expr expr, Expr expr2) {
        super(expr, expr2);
    }

    @Override // nu.xom.jaxen.expr.DefaultArithExpr, nu.xom.jaxen.expr.DefaultBinaryExpr
    public String toString() {
        return new StringBuffer().append("[(DefaultMultiplicativeExpr): ").append(getLHS()).append(", ").append(getRHS()).append("]").toString();
    }
}
