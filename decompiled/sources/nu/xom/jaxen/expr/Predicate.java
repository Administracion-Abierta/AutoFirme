package nu.xom.jaxen.expr;

import java.io.Serializable;
import nu.xom.jaxen.Context;
import nu.xom.jaxen.JaxenException;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/expr/Predicate.class */
public interface Predicate extends Serializable {
    Expr getExpr();

    void setExpr(Expr expr);

    void simplify();

    String getText();

    Object evaluate(Context context) throws JaxenException;
}
