package nu.xom.jaxen.expr;

import java.util.List;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/jaxen/expr/LocationPath.class */
public interface LocationPath extends Expr {
    void addStep(Step step);

    List getSteps();

    boolean isAbsolute();
}
