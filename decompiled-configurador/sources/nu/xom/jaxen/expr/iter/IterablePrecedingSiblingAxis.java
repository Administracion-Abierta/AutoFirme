package nu.xom.jaxen.expr.iter;

import java.util.Iterator;
import nu.xom.jaxen.ContextSupport;
import nu.xom.jaxen.UnsupportedAxisException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/jaxen/expr/iter/IterablePrecedingSiblingAxis.class */
public class IterablePrecedingSiblingAxis extends IterableAxis {
    private static final long serialVersionUID = -3140080721715120745L;

    public IterablePrecedingSiblingAxis(int i) {
        super(i);
    }

    @Override // nu.xom.jaxen.expr.iter.IterableAxis
    public Iterator iterator(Object obj, ContextSupport contextSupport) throws UnsupportedAxisException {
        return contextSupport.getNavigator().getPrecedingSiblingAxisIterator(obj);
    }
}
