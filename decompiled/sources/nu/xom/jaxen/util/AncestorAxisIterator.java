package nu.xom.jaxen.util;

import nu.xom.jaxen.Navigator;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/util/AncestorAxisIterator.class */
public class AncestorAxisIterator extends AncestorOrSelfAxisIterator {
    public AncestorAxisIterator(Object obj, Navigator navigator) {
        super(obj, navigator);
        next();
    }
}
