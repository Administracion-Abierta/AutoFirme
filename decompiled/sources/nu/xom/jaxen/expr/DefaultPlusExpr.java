package nu.xom.jaxen.expr;

import nu.xom.jaxen.Context;
import nu.xom.jaxen.JaxenException;
import nu.xom.jaxen.function.NumberFunction;
import org.slf4j.Marker;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/expr/DefaultPlusExpr.class */
class DefaultPlusExpr extends DefaultAdditiveExpr {
    private static final long serialVersionUID = -1426954461146769374L;

    DefaultPlusExpr(Expr expr, Expr expr2) {
        super(expr, expr2);
    }

    @Override // nu.xom.jaxen.expr.DefaultBinaryExpr, nu.xom.jaxen.expr.BinaryExpr
    public String getOperator() {
        return Marker.ANY_NON_NULL_MARKER;
    }

    @Override // nu.xom.jaxen.expr.Expr
    public Object evaluate(Context context) throws JaxenException {
        return new Double(NumberFunction.evaluate(getLHS().evaluate(context), context.getNavigator()).doubleValue() + NumberFunction.evaluate(getRHS().evaluate(context), context.getNavigator()).doubleValue());
    }
}
