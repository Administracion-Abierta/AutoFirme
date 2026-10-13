package nu.xom.jaxen.expr;

import nu.xom.jaxen.ContextSupport;
import nu.xom.jaxen.expr.iter.IterableAxis;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/jaxen/expr/DefaultTextNodeStep.class */
public class DefaultTextNodeStep extends DefaultStep implements TextNodeStep {
    private static final long serialVersionUID = -3821960984972022948L;

    public DefaultTextNodeStep(IterableAxis iterableAxis, PredicateSet predicateSet) {
        super(iterableAxis, predicateSet);
    }

    @Override // nu.xom.jaxen.expr.Step
    public boolean matches(Object obj, ContextSupport contextSupport) {
        return contextSupport.getNavigator().isText(obj);
    }

    @Override // nu.xom.jaxen.expr.DefaultStep, nu.xom.jaxen.expr.Step
    public String getText() {
        return new StringBuffer().append(getAxisName()).append("::text()").append(super.getText()).toString();
    }
}
