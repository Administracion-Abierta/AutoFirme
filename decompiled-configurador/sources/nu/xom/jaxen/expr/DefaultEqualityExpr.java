package nu.xom.jaxen.expr;

import java.util.Iterator;
import java.util.List;
import nu.xom.jaxen.Context;
import nu.xom.jaxen.JaxenException;
import nu.xom.jaxen.Navigator;
import nu.xom.jaxen.function.BooleanFunction;
import nu.xom.jaxen.function.NumberFunction;
import nu.xom.jaxen.function.StringFunction;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/jaxen/expr/DefaultEqualityExpr.class */
abstract class DefaultEqualityExpr extends DefaultTruthExpr implements EqualityExpr {
    DefaultEqualityExpr(Expr expr, Expr expr2) {
        super(expr, expr2);
    }

    @Override // nu.xom.jaxen.expr.DefaultTruthExpr, nu.xom.jaxen.expr.DefaultBinaryExpr
    public String toString() {
        return new StringBuffer().append("[(DefaultEqualityExpr): ").append(getLHS()).append(", ").append(getRHS()).append("]").toString();
    }

    @Override // nu.xom.jaxen.expr.Expr
    public Object evaluate(Context context) throws JaxenException {
        Object objEvaluate = getLHS().evaluate(context);
        Object objEvaluate2 = getRHS().evaluate(context);
        if (objEvaluate == null || objEvaluate2 == null) {
            return Boolean.FALSE;
        }
        Navigator navigator = context.getNavigator();
        if (bothAreSets(objEvaluate, objEvaluate2)) {
            return evaluateSetSet((List) objEvaluate, (List) objEvaluate2, navigator);
        }
        if (isSet(objEvaluate) && isBoolean(objEvaluate2)) {
            return Boolean.valueOf(evaluateObjectObject(((List) objEvaluate).isEmpty() ? Boolean.FALSE : Boolean.TRUE, (Boolean) objEvaluate2, navigator));
        }
        if (isBoolean(objEvaluate) && isSet(objEvaluate2)) {
            return Boolean.valueOf(evaluateObjectObject((Boolean) objEvaluate, ((List) objEvaluate2).isEmpty() ? Boolean.FALSE : Boolean.TRUE, navigator));
        }
        if (eitherIsSet(objEvaluate, objEvaluate2)) {
            return isSet(objEvaluate) ? evaluateSetSet((List) objEvaluate, convertToList(objEvaluate2), navigator) : evaluateSetSet(convertToList(objEvaluate), (List) objEvaluate2, navigator);
        }
        return Boolean.valueOf(evaluateObjectObject(objEvaluate, objEvaluate2, navigator));
    }

    private Boolean evaluateSetSet(List list, List list2, Navigator navigator) {
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
        if (eitherIsBoolean(obj, obj2)) {
            return evaluateObjectObject(BooleanFunction.evaluate(obj, navigator), BooleanFunction.evaluate(obj2, navigator));
        }
        return eitherIsNumber(obj, obj2) ? evaluateObjectObject(NumberFunction.evaluate(obj, navigator), NumberFunction.evaluate(obj2, navigator)) : evaluateObjectObject(StringFunction.evaluate(obj, navigator), StringFunction.evaluate(obj2, navigator));
    }

    protected abstract boolean evaluateObjectObject(Object obj, Object obj2);
}
