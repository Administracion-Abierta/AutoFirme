package nu.xom.jaxen.expr.iter;

import java.util.Iterator;
import nu.xom.jaxen.ContextSupport;
import nu.xom.jaxen.UnsupportedAxisException;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/expr/iter/IterableFollowingSiblingAxis.class */
public class IterableFollowingSiblingAxis extends IterableAxis {
    private static final long serialVersionUID = 4412705219546610009L;

    public IterableFollowingSiblingAxis(int i) {
        super(i);
    }

    @Override // nu.xom.jaxen.expr.iter.IterableAxis
    public Iterator iterator(Object obj, ContextSupport contextSupport) throws UnsupportedAxisException {
        return contextSupport.getNavigator().getFollowingSiblingAxisIterator(obj);
    }
}
