package nu.xom.jaxen.expr;

import nu.xom.jaxen.ContextSupport;
import nu.xom.jaxen.expr.iter.IterableAxis;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/expr/DefaultCommentNodeStep.class */
public class DefaultCommentNodeStep extends DefaultStep implements CommentNodeStep {
    private static final long serialVersionUID = 4340788283861875606L;

    public DefaultCommentNodeStep(IterableAxis iterableAxis, PredicateSet predicateSet) {
        super(iterableAxis, predicateSet);
    }

    @Override // nu.xom.jaxen.expr.DefaultStep
    public String toString() {
        return new StringBuffer().append("[(DefaultCommentNodeStep): ").append(getAxis()).append("]").toString();
    }

    @Override // nu.xom.jaxen.expr.DefaultStep, nu.xom.jaxen.expr.Step
    public String getText() {
        return new StringBuffer().append(getAxisName()).append("::comment()").toString();
    }

    @Override // nu.xom.jaxen.expr.Step
    public boolean matches(Object obj, ContextSupport contextSupport) {
        return contextSupport.getNavigator().isComment(obj);
    }
}
