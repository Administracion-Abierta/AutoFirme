package nu.xom.jaxen.expr.iter;

import java.util.Iterator;
import nu.xom.jaxen.ContextSupport;
import nu.xom.jaxen.UnsupportedAxisException;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/expr/iter/IterablePrecedingAxis.class */
public class IterablePrecedingAxis extends IterableAxis {
    private static final long serialVersionUID = 587333938258540052L;

    public IterablePrecedingAxis(int i) {
        super(i);
    }

    @Override // nu.xom.jaxen.expr.iter.IterableAxis
    public Iterator iterator(Object obj, ContextSupport contextSupport) throws UnsupportedAxisException {
        return contextSupport.getNavigator().getPrecedingAxisIterator(obj);
    }
}
