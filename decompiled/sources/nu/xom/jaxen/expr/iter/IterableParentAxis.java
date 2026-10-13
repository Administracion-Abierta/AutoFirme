package nu.xom.jaxen.expr.iter;

import java.util.Iterator;
import nu.xom.jaxen.ContextSupport;
import nu.xom.jaxen.UnsupportedAxisException;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/expr/iter/IterableParentAxis.class */
public class IterableParentAxis extends IterableAxis {
    private static final long serialVersionUID = -7521574185875636490L;

    public IterableParentAxis(int i) {
        super(i);
    }

    @Override // nu.xom.jaxen.expr.iter.IterableAxis
    public Iterator iterator(Object obj, ContextSupport contextSupport) throws UnsupportedAxisException {
        return contextSupport.getNavigator().getParentAxisIterator(obj);
    }
}
