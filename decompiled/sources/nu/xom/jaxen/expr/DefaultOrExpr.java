package nu.xom.jaxen.expr;

import nu.xom.jaxen.Context;
import nu.xom.jaxen.JaxenException;
import nu.xom.jaxen.Navigator;
import nu.xom.jaxen.function.BooleanFunction;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/expr/DefaultOrExpr.class */
class DefaultOrExpr extends DefaultLogicalExpr {
    private static final long serialVersionUID = 4894552680753026730L;

    DefaultOrExpr(Expr expr, Expr expr2) {
        super(expr, expr2);
    }

    @Override // nu.xom.jaxen.expr.DefaultBinaryExpr, nu.xom.jaxen.expr.BinaryExpr
    public String getOperator() {
        return "or";
    }

    @Override // nu.xom.jaxen.expr.DefaultTruthExpr, nu.xom.jaxen.expr.DefaultBinaryExpr
    public String toString() {
        return new StringBuffer().append("[(DefaultOrExpr): ").append(getLHS()).append(", ").append(getRHS()).append("]").toString();
    }

    @Override // nu.xom.jaxen.expr.Expr
    public Object evaluate(Context context) throws JaxenException {
        Navigator navigator = context.getNavigator();
        if (!BooleanFunction.evaluate(getLHS().evaluate(context), navigator).booleanValue() && !BooleanFunction.evaluate(getRHS().evaluate(context), navigator).booleanValue()) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
}
