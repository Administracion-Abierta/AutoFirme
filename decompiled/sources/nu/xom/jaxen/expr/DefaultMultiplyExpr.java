package nu.xom.jaxen.expr;

import nu.xom.jaxen.Context;
import nu.xom.jaxen.JaxenException;
import nu.xom.jaxen.function.NumberFunction;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/expr/DefaultMultiplyExpr.class */
class DefaultMultiplyExpr extends DefaultMultiplicativeExpr {
    private static final long serialVersionUID = 2760053878102260365L;

    DefaultMultiplyExpr(Expr expr, Expr expr2) {
        super(expr, expr2);
    }

    @Override // nu.xom.jaxen.expr.DefaultBinaryExpr, nu.xom.jaxen.expr.BinaryExpr
    public String getOperator() {
        return "*";
    }

    @Override // nu.xom.jaxen.expr.Expr
    public Object evaluate(Context context) throws JaxenException {
        return new Double(NumberFunction.evaluate(getLHS().evaluate(context), context.getNavigator()).doubleValue() * NumberFunction.evaluate(getRHS().evaluate(context), context.getNavigator()).doubleValue());
    }
}
