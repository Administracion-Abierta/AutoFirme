package nu.xom.jaxen.expr.iter;

import java.io.Serializable;
import java.util.Iterator;
import nu.xom.jaxen.ContextSupport;
import nu.xom.jaxen.UnsupportedAxisException;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/expr/iter/IterableAxis.class */
public abstract class IterableAxis implements Serializable {
    private int value;

    public IterableAxis(int i) {
        this.value = i;
    }

    public int value() {
        return this.value;
    }

    public abstract Iterator iterator(Object obj, ContextSupport contextSupport) throws UnsupportedAxisException;

    public Iterator namedAccessIterator(Object obj, ContextSupport contextSupport, String str, String str2, String str3) throws UnsupportedAxisException {
        throw new UnsupportedOperationException("Named access unsupported");
    }

    public boolean supportsNamedAccess(ContextSupport contextSupport) {
        return false;
    }
}
