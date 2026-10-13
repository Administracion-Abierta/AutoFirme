package nu.xom.jaxen.expr;

import java.util.List;
import nu.xom.jaxen.Context;
import nu.xom.jaxen.JaxenException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/jaxen/expr/DefaultXPathExpr.class */
public class DefaultXPathExpr implements XPathExpr {
    private static final long serialVersionUID = 3007613096320896040L;
    private Expr rootExpr;

    public DefaultXPathExpr(Expr expr) {
        this.rootExpr = expr;
    }

    @Override // nu.xom.jaxen.expr.XPathExpr
    public Expr getRootExpr() {
        return this.rootExpr;
    }

    @Override // nu.xom.jaxen.expr.XPathExpr
    public void setRootExpr(Expr expr) {
        this.rootExpr = expr;
    }

    public String toString() {
        return new StringBuffer().append("[(DefaultXPath): ").append(getRootExpr()).append("]").toString();
    }

    @Override // nu.xom.jaxen.expr.XPathExpr
    public String getText() {
        return getRootExpr().getText();
    }

    @Override // nu.xom.jaxen.expr.XPathExpr
    public void simplify() {
        setRootExpr(getRootExpr().simplify());
    }

    @Override // nu.xom.jaxen.expr.XPathExpr
    public List asList(Context context) throws JaxenException {
        return DefaultExpr.convertToList(getRootExpr().evaluate(context));
    }
}
