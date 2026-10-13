package nu.xom.jaxen.expr;

import nu.xom.jaxen.Context;
import nu.xom.jaxen.UnresolvableException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/jaxen/expr/DefaultVariableReferenceExpr.class */
class DefaultVariableReferenceExpr extends DefaultExpr implements VariableReferenceExpr {
    private static final long serialVersionUID = 8832095437149358674L;
    private String prefix;
    private String localName;

    DefaultVariableReferenceExpr(String str, String str2) {
        this.prefix = str;
        this.localName = str2;
    }

    @Override // nu.xom.jaxen.expr.VariableReferenceExpr
    public String getPrefix() {
        return this.prefix;
    }

    @Override // nu.xom.jaxen.expr.VariableReferenceExpr
    public String getVariableName() {
        return this.localName;
    }

    public String toString() {
        return new StringBuffer().append("[(DefaultVariableReferenceExpr): ").append(getQName()).append("]").toString();
    }

    private String getQName() {
        return "".equals(this.prefix) ? this.localName : new StringBuffer().append(this.prefix).append(":").append(this.localName).toString();
    }

    @Override // nu.xom.jaxen.expr.Expr
    public String getText() {
        return new StringBuffer().append("$").append(getQName()).toString();
    }

    @Override // nu.xom.jaxen.expr.Expr
    public Object evaluate(Context context) throws UnresolvableException {
        String prefix = getPrefix();
        String strTranslateNamespacePrefixToUri = null;
        if (prefix != null && !"".equals(prefix)) {
            strTranslateNamespacePrefixToUri = context.translateNamespacePrefixToUri(prefix);
        }
        return context.getVariableValue(strTranslateNamespacePrefixToUri, prefix, this.localName);
    }
}
