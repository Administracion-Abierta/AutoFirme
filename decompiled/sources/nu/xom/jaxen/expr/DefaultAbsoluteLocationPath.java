package nu.xom.jaxen.expr;

import java.util.Collections;
import java.util.List;
import nu.xom.jaxen.Context;
import nu.xom.jaxen.ContextSupport;
import nu.xom.jaxen.JaxenException;
import nu.xom.jaxen.Navigator;
import nu.xom.jaxen.util.SingletonList;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/expr/DefaultAbsoluteLocationPath.class */
public class DefaultAbsoluteLocationPath extends DefaultLocationPath {
    private static final long serialVersionUID = 2174836928310146874L;

    @Override // nu.xom.jaxen.expr.DefaultLocationPath
    public String toString() {
        return new StringBuffer().append("[(DefaultAbsoluteLocationPath): ").append(super.toString()).append("]").toString();
    }

    @Override // nu.xom.jaxen.expr.DefaultLocationPath, nu.xom.jaxen.expr.LocationPath
    public boolean isAbsolute() {
        return true;
    }

    @Override // nu.xom.jaxen.expr.DefaultLocationPath, nu.xom.jaxen.expr.Expr
    public String getText() {
        return new StringBuffer().append("/").append(super.getText()).toString();
    }

    @Override // nu.xom.jaxen.expr.DefaultLocationPath, nu.xom.jaxen.expr.Expr
    public Object evaluate(Context context) throws JaxenException {
        Object documentNode;
        ContextSupport contextSupport = context.getContextSupport();
        Navigator navigator = contextSupport.getNavigator();
        Context context2 = new Context(contextSupport);
        List nodeSet = context.getNodeSet();
        if (!nodeSet.isEmpty() && (documentNode = navigator.getDocumentNode(nodeSet.get(0))) != null) {
            context2.setNodeSet(new SingletonList(documentNode));
            return super.evaluate(context2);
        }
        return Collections.EMPTY_LIST;
    }
}
