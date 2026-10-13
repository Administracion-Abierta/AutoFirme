package nu.xom.jaxen.expr;

import nu.xom.jaxen.Context;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/jaxen/expr/DefaultLiteralExpr.class */
class DefaultLiteralExpr extends DefaultExpr implements LiteralExpr {
    private static final long serialVersionUID = -953829179036273338L;
    private String literal;

    DefaultLiteralExpr(String str) {
        this.literal = str;
    }

    @Override // nu.xom.jaxen.expr.LiteralExpr
    public String getLiteral() {
        return this.literal;
    }

    public String toString() {
        return new StringBuffer().append("[(DefaultLiteralExpr): ").append(getLiteral()).append("]").toString();
    }

    @Override // nu.xom.jaxen.expr.Expr
    public String getText() {
        return this.literal.indexOf(34) == -1 ? new StringBuffer().append("\"").append(getLiteral()).append("\"").toString() : new StringBuffer().append("'").append(getLiteral()).append("'").toString();
    }

    @Override // nu.xom.jaxen.expr.Expr
    public Object evaluate(Context context) {
        return getLiteral();
    }
}
