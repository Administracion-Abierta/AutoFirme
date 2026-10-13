package nu.xom.jaxen.expr;

import java.io.Serializable;
import nu.xom.jaxen.Context;
import nu.xom.jaxen.JaxenException;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/expr/Expr.class */
public interface Expr extends Serializable {
    String getText();

    Expr simplify();

    Object evaluate(Context context) throws JaxenException;
}
