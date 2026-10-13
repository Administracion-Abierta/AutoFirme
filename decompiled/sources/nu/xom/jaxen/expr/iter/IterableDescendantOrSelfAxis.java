package nu.xom.jaxen.expr.iter;

import java.util.Iterator;
import nu.xom.jaxen.ContextSupport;
import nu.xom.jaxen.UnsupportedAxisException;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/expr/iter/IterableDescendantOrSelfAxis.class */
public class IterableDescendantOrSelfAxis extends IterableAxis {
    private static final long serialVersionUID = 2956703237251023850L;

    public IterableDescendantOrSelfAxis(int i) {
        super(i);
    }

    @Override // nu.xom.jaxen.expr.iter.IterableAxis
    public Iterator iterator(Object obj, ContextSupport contextSupport) throws UnsupportedAxisException {
        return contextSupport.getNavigator().getDescendantOrSelfAxisIterator(obj);
    }
}
