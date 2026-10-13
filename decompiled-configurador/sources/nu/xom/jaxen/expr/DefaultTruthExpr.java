package nu.xom.jaxen.expr;

import java.util.List;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/jaxen/expr/DefaultTruthExpr.class */
abstract class DefaultTruthExpr extends DefaultBinaryExpr {
    DefaultTruthExpr(Expr expr, Expr expr2) {
        super(expr, expr2);
    }

    @Override // nu.xom.jaxen.expr.DefaultBinaryExpr
    public String toString() {
        return new StringBuffer().append("[(DefaultTruthExpr): ").append(getLHS()).append(", ").append(getRHS()).append("]").toString();
    }

    protected boolean bothAreSets(Object obj, Object obj2) {
        return (obj instanceof List) && (obj2 instanceof List);
    }

    protected boolean eitherIsSet(Object obj, Object obj2) {
        return (obj instanceof List) || (obj2 instanceof List);
    }

    protected boolean isSet(Object obj) {
        return obj instanceof List;
    }

    protected boolean isBoolean(Object obj) {
        return obj instanceof Boolean;
    }

    protected boolean setIsEmpty(List list) {
        return list == null || list.size() == 0;
    }

    protected boolean eitherIsBoolean(Object obj, Object obj2) {
        return (obj instanceof Boolean) || (obj2 instanceof Boolean);
    }

    protected boolean bothAreBoolean(Object obj, Object obj2) {
        return (obj instanceof Boolean) && (obj2 instanceof Boolean);
    }

    protected boolean eitherIsNumber(Object obj, Object obj2) {
        return (obj instanceof Number) || (obj2 instanceof Number);
    }
}
