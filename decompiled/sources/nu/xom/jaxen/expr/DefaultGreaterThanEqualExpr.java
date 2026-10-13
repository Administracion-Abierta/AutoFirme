package nu.xom.jaxen.expr;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/expr/DefaultGreaterThanEqualExpr.class */
class DefaultGreaterThanEqualExpr extends DefaultRelationalExpr {
    private static final long serialVersionUID = -7848747981787197470L;

    DefaultGreaterThanEqualExpr(Expr expr, Expr expr2) {
        super(expr, expr2);
    }

    @Override // nu.xom.jaxen.expr.DefaultBinaryExpr, nu.xom.jaxen.expr.BinaryExpr
    public String getOperator() {
        return ">=";
    }

    @Override // nu.xom.jaxen.expr.DefaultRelationalExpr
    protected boolean evaluateDoubleDouble(Double d, Double d2) {
        return d.compareTo(d2) >= 0;
    }
}
