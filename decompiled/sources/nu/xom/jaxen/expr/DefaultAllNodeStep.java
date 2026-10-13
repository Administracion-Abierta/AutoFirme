package nu.xom.jaxen.expr;

import nu.xom.jaxen.ContextSupport;
import nu.xom.jaxen.expr.iter.IterableAxis;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/expr/DefaultAllNodeStep.class */
public class DefaultAllNodeStep extends DefaultStep implements AllNodeStep {
    private static final long serialVersionUID = 292886316770123856L;

    public DefaultAllNodeStep(IterableAxis iterableAxis, PredicateSet predicateSet) {
        super(iterableAxis, predicateSet);
    }

    @Override // nu.xom.jaxen.expr.DefaultStep
    public String toString() {
        return new StringBuffer().append("[(DefaultAllNodeStep): ").append(getAxisName()).append("]").toString();
    }

    @Override // nu.xom.jaxen.expr.DefaultStep, nu.xom.jaxen.expr.Step
    public String getText() {
        return new StringBuffer().append(getAxisName()).append("::node()").append(super.getText()).toString();
    }

    @Override // nu.xom.jaxen.expr.Step
    public boolean matches(Object obj, ContextSupport contextSupport) {
        return true;
    }
}
