package nu.xom.jaxen.expr;

import java.util.List;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/jaxen/expr/FunctionCallExpr.class */
public interface FunctionCallExpr extends Expr {
    String getPrefix();

    String getFunctionName();

    void addParameter(Expr expr);

    List getParameters();
}
