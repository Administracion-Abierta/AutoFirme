package nu.xom.jaxen.function;

import java.util.Iterator;
import java.util.List;
import nu.xom.jaxen.Context;
import nu.xom.jaxen.Function;
import nu.xom.jaxen.FunctionCallException;
import nu.xom.jaxen.Navigator;
import nu.xom.jaxen.UnsupportedAxisException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/jaxen/function/LangFunction.class */
public class LangFunction implements Function {
    private static final String LANG_LOCALNAME = "lang";
    private static final String XMLNS_URI = "http://www.w3.org/XML/1998/namespace";

    @Override // nu.xom.jaxen.Function
    public Object call(Context context, List list) throws FunctionCallException {
        if (list.size() != 1) {
            throw new FunctionCallException("lang() requires exactly one argument.");
        }
        try {
            return evaluate(context.getNodeSet(), list.get(0), context.getNavigator());
        } catch (UnsupportedAxisException e) {
            throw new FunctionCallException("Can't evaluate lang()", e);
        }
    }

    private static Boolean evaluate(List list, Object obj, Navigator navigator) throws UnsupportedAxisException {
        return evaluate(list.get(0), StringFunction.evaluate(obj, navigator), navigator) ? Boolean.TRUE : Boolean.FALSE;
    }

    private static boolean evaluate(Object obj, String str, Navigator navigator) throws UnsupportedAxisException {
        Object parentNode = obj;
        if (!navigator.isElement(parentNode)) {
            parentNode = navigator.getParentNode(obj);
        }
        while (parentNode != null && navigator.isElement(parentNode)) {
            Iterator attributeAxisIterator = navigator.getAttributeAxisIterator(parentNode);
            while (attributeAxisIterator.hasNext()) {
                Object next = attributeAxisIterator.next();
                if (LANG_LOCALNAME.equals(navigator.getAttributeName(next)) && "http://www.w3.org/XML/1998/namespace".equals(navigator.getAttributeNamespaceUri(next))) {
                    return isSublang(navigator.getAttributeStringValue(next), str);
                }
            }
            parentNode = navigator.getParentNode(parentNode);
        }
        return false;
    }

    private static boolean isSublang(String str, String str2) {
        if (str.equalsIgnoreCase(str2)) {
            return true;
        }
        int length = str2.length();
        return str.length() > length && str.charAt(length) == '-' && str.substring(0, length).equalsIgnoreCase(str2);
    }
}
