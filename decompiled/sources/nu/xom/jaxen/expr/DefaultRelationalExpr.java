package nu.xom.jaxen.expr;

import java.util.Iterator;
import java.util.List;
import nu.xom.jaxen.Context;
import nu.xom.jaxen.JaxenException;
import nu.xom.jaxen.Navigator;
import nu.xom.jaxen.function.NumberFunction;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/expr/DefaultRelationalExpr.class */
abstract class DefaultRelationalExpr extends DefaultTruthExpr implements RelationalExpr {
    DefaultRelationalExpr(Expr expr, Expr expr2) {
        super(expr, expr2);
    }

    @Override // nu.xom.jaxen.expr.DefaultTruthExpr, nu.xom.jaxen.expr.DefaultBinaryExpr
    public String toString() {
        return new StringBuffer().append("[(DefaultRelationalExpr): ").append(getLHS()).append(", ").append(getRHS()).append("]").toString();
    }

    @Override // nu.xom.jaxen.expr.Expr
    public Object evaluate(Context context) throws JaxenException {
        Object objEvaluate = getLHS().evaluate(context);
        Object objEvaluate2 = getRHS().evaluate(context);
        Navigator navigator = context.getNavigator();
        if (bothAreSets(objEvaluate, objEvaluate2)) {
            return evaluateSetSet((List) objEvaluate, (List) objEvaluate2, navigator);
        }
        if (eitherIsSet(objEvaluate, objEvaluate2)) {
            return isSet(objEvaluate) ? evaluateSetSet((List) objEvaluate, convertToList(objEvaluate2), navigator) : evaluateSetSet(convertToList(objEvaluate), (List) objEvaluate2, navigator);
        }
        return evaluateObjectObject(objEvaluate, objEvaluate2, navigator) ? Boolean.TRUE : Boolean.FALSE;
    }

    private Object evaluateSetSet(List list, List list2, Navigator navigator) {
        if (setIsEmpty(list) || setIsEmpty(list2)) {
            return Boolean.FALSE;
        }
        for (Object obj : list) {
            Iterator it = list2.iterator();
            while (it.hasNext()) {
                if (evaluateObjectObject(obj, it.next(), navigator)) {
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.FALSE;
    }

    private boolean evaluateObjectObject(Object obj, Object obj2, Navigator navigator) {
        if (obj == null || obj2 == null) {
            return false;
        }
        Double dEvaluate = NumberFunction.evaluate(obj, navigator);
        Double dEvaluate2 = NumberFunction.evaluate(obj2, navigator);
        if (NumberFunction.isNaN(dEvaluate) || NumberFunction.isNaN(dEvaluate2)) {
            return false;
        }
        return evaluateDoubleDouble(dEvaluate, dEvaluate2);
    }

    protected abstract boolean evaluateDoubleDouble(Double d, Double d2);
}
