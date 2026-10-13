package nu.xom.jaxen.expr;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/expr/DefaultLessThanExpr.class */
class DefaultLessThanExpr extends DefaultRelationalExpr {
    private static final long serialVersionUID = 8423816025305001283L;

    DefaultLessThanExpr(Expr expr, Expr expr2) {
        super(expr, expr2);
    }

    @Override // nu.xom.jaxen.expr.DefaultBinaryExpr, nu.xom.jaxen.expr.BinaryExpr
    public String getOperator() {
        return "<";
    }

    @Override // nu.xom.jaxen.expr.DefaultRelationalExpr
    protected boolean evaluateDoubleDouble(Double d, Double d2) {
        return d.doubleValue() < d2.doubleValue();
    }
}
