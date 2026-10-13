package nu.xom.jaxen.expr;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/jaxen/expr/DefaultNotEqualsExpr.class */
class DefaultNotEqualsExpr extends DefaultEqualityExpr {
    private static final long serialVersionUID = -8001267398136979152L;

    DefaultNotEqualsExpr(Expr expr, Expr expr2) {
        super(expr, expr2);
    }

    @Override // nu.xom.jaxen.expr.DefaultBinaryExpr, nu.xom.jaxen.expr.BinaryExpr
    public String getOperator() {
        return "!=";
    }

    @Override // nu.xom.jaxen.expr.DefaultEqualityExpr, nu.xom.jaxen.expr.DefaultTruthExpr, nu.xom.jaxen.expr.DefaultBinaryExpr
    public String toString() {
        return new StringBuffer().append("[(DefaultNotEqualsExpr): ").append(getLHS()).append(", ").append(getRHS()).append("]").toString();
    }

    @Override // nu.xom.jaxen.expr.DefaultEqualityExpr
    protected boolean evaluateObjectObject(Object obj, Object obj2) {
        if (eitherIsNumber(obj, obj2)) {
            return ((Double) obj).doubleValue() != ((Double) obj2).doubleValue();
        }
        return !obj.equals(obj2);
    }
}
