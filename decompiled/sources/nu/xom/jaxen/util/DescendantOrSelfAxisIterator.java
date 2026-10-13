package nu.xom.jaxen.util;

import nu.xom.jaxen.Navigator;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/util/DescendantOrSelfAxisIterator.class */
public class DescendantOrSelfAxisIterator extends DescendantAxisIterator {
    public DescendantOrSelfAxisIterator(Object obj, Navigator navigator) {
        super(navigator, new SingleObjectIterator(obj));
    }
}
