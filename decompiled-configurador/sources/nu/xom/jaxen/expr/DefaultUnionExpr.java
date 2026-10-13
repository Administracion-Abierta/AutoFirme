package nu.xom.jaxen.expr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import nu.xom.jaxen.Context;
import nu.xom.jaxen.JaxenException;
import nu.xom.jaxen.XPathSyntaxException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/jaxen/expr/DefaultUnionExpr.class */
public class DefaultUnionExpr extends DefaultBinaryExpr implements UnionExpr {
    private static final long serialVersionUID = 7629142718276852707L;

    public DefaultUnionExpr(Expr expr, Expr expr2) {
        super(expr, expr2);
    }

    @Override // nu.xom.jaxen.expr.DefaultBinaryExpr, nu.xom.jaxen.expr.BinaryExpr
    public String getOperator() {
        return "|";
    }

    @Override // nu.xom.jaxen.expr.DefaultBinaryExpr
    public String toString() {
        return new StringBuffer().append("[(DefaultUnionExpr): ").append(getLHS()).append(", ").append(getRHS()).append("]").toString();
    }

    @Override // nu.xom.jaxen.expr.Expr
    public Object evaluate(Context context) throws JaxenException {
        ArrayList arrayList = new ArrayList();
        try {
            List list = (List) getLHS().evaluate(context);
            List list2 = (List) getRHS().evaluate(context);
            HashSet hashSet = new HashSet();
            arrayList.addAll(list);
            hashSet.addAll(list);
            for (Object obj : list2) {
                if (!hashSet.contains(obj)) {
                    arrayList.add(obj);
                    hashSet.add(obj);
                }
            }
            Collections.sort(arrayList, new NodeComparator(context.getNavigator()));
            return arrayList;
        } catch (ClassCastException e) {
            throw new XPathSyntaxException(getText(), context.getPosition(), "Unions are only allowed over node-sets");
        }
    }
}
