package nu.xom.jaxen.expr;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/jaxen/expr/PathExpr.class */
public interface PathExpr extends Expr {
    Expr getFilterExpr();

    void setFilterExpr(Expr expr);

    LocationPath getLocationPath();
}
