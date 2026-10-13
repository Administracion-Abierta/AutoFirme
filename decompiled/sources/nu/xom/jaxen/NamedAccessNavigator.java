package nu.xom.jaxen;

import java.util.Iterator;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/NamedAccessNavigator.class */
public interface NamedAccessNavigator extends Navigator {
    Iterator getChildAxisIterator(Object obj, String str, String str2, String str3) throws UnsupportedAxisException;

    Iterator getAttributeAxisIterator(Object obj, String str, String str2, String str3) throws UnsupportedAxisException;
}
