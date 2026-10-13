package nu.xom.jaxen.expr.iter;

import java.util.Iterator;
import nu.xom.jaxen.ContextSupport;
import nu.xom.jaxen.UnsupportedAxisException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/jaxen/expr/iter/IterableAncestorOrSelfAxis.class */
public class IterableAncestorOrSelfAxis extends IterableAxis {
    private static final long serialVersionUID = 1;

    public IterableAncestorOrSelfAxis(int i) {
        super(i);
    }

    @Override // nu.xom.jaxen.expr.iter.IterableAxis
    public Iterator iterator(Object obj, ContextSupport contextSupport) throws UnsupportedAxisException {
        return contextSupport.getNavigator().getAncestorOrSelfAxisIterator(obj);
    }
}
