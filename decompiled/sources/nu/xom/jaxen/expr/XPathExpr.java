package nu.xom.jaxen.expr;

import java.io.Serializable;
import java.util.List;
import nu.xom.jaxen.Context;
import nu.xom.jaxen.JaxenException;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/expr/XPathExpr.class */
public interface XPathExpr extends Serializable {
    Expr getRootExpr();

    void setRootExpr(Expr expr);

    String getText();

    void simplify();

    List asList(Context context) throws JaxenException;
}
