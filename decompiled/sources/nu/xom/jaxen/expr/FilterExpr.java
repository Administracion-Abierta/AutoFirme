package nu.xom.jaxen.expr;

import nu.xom.jaxen.Context;
import nu.xom.jaxen.JaxenException;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/expr/FilterExpr.class */
public interface FilterExpr extends Expr, Predicated {
    boolean asBoolean(Context context) throws JaxenException;

    Expr getExpr();
}
