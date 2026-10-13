package nu.xom.jaxen.expr;

import java.util.Iterator;
import java.util.List;
import nu.xom.jaxen.util.SingleObjectIterator;
import nu.xom.jaxen.util.SingletonList;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/expr/DefaultExpr.class */
public abstract class DefaultExpr implements Expr {
    @Override // nu.xom.jaxen.expr.Expr
    public Expr simplify() {
        return this;
    }

    public static Iterator convertToIterator(Object obj) {
        if (obj instanceof Iterator) {
            return (Iterator) obj;
        }
        return obj instanceof List ? ((List) obj).iterator() : new SingleObjectIterator(obj);
    }

    public static List convertToList(Object obj) {
        return obj instanceof List ? (List) obj : new SingletonList(obj);
    }
}
