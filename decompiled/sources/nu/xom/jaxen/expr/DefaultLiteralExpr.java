package nu.xom.jaxen.expr;

import nu.xom.jaxen.Context;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/expr/DefaultLiteralExpr.class */
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
        return this.literal.indexOf(34) == -1 ? new StringBuffer().append(OperatorName.SHOW_TEXT_LINE_AND_SPACE).append(getLiteral()).append(OperatorName.SHOW_TEXT_LINE_AND_SPACE).toString() : new StringBuffer().append(OperatorName.SHOW_TEXT_LINE).append(getLiteral()).append(OperatorName.SHOW_TEXT_LINE).toString();
    }

    @Override // nu.xom.jaxen.expr.Expr
    public Object evaluate(Context context) {
        return getLiteral();
    }
}
