package nu.xom.jaxen.expr;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/jaxen/expr/BinaryExpr.class */
public interface BinaryExpr extends Expr {
    Expr getLHS();

    Expr getRHS();

    String getOperator();
}
