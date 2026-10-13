package nu.xom.jaxen.expr.iter;

import java.util.Iterator;
import nu.xom.jaxen.ContextSupport;
import nu.xom.jaxen.NamedAccessNavigator;
import nu.xom.jaxen.UnsupportedAxisException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/jaxen/expr/iter/IterableChildAxis.class */
public class IterableChildAxis extends IterableAxis {
    private static final long serialVersionUID = 1;

    public IterableChildAxis(int i) {
        super(i);
    }

    @Override // nu.xom.jaxen.expr.iter.IterableAxis
    public Iterator iterator(Object obj, ContextSupport contextSupport) throws UnsupportedAxisException {
        return contextSupport.getNavigator().getChildAxisIterator(obj);
    }

    @Override // nu.xom.jaxen.expr.iter.IterableAxis
    public Iterator namedAccessIterator(Object obj, ContextSupport contextSupport, String str, String str2, String str3) throws UnsupportedAxisException {
        return ((NamedAccessNavigator) contextSupport.getNavigator()).getChildAxisIterator(obj, str, str2, str3);
    }

    @Override // nu.xom.jaxen.expr.iter.IterableAxis
    public boolean supportsNamedAccess(ContextSupport contextSupport) {
        return contextSupport.getNavigator() instanceof NamedAccessNavigator;
    }
}
