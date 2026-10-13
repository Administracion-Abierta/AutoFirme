package nu.xom.jaxen.function;

import java.util.List;
import nu.xom.jaxen.Context;
import nu.xom.jaxen.Function;
import nu.xom.jaxen.FunctionCallException;
import nu.xom.jaxen.Navigator;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/function/NamespaceUriFunction.class */
public class NamespaceUriFunction implements Function {
    @Override // nu.xom.jaxen.Function
    public Object call(Context context, List list) throws FunctionCallException {
        if (list.size() == 0) {
            return evaluate(context.getNodeSet(), context.getNavigator());
        }
        if (list.size() == 1) {
            return evaluate(list, context.getNavigator());
        }
        throw new FunctionCallException("namespace-uri() requires zero or one argument.");
    }

    public static String evaluate(List list, Navigator navigator) throws FunctionCallException {
        if (list.isEmpty()) {
            return "";
        }
        Object obj = list.get(0);
        if (obj instanceof List) {
            return evaluate((List) obj, navigator);
        }
        if (navigator.isElement(obj)) {
            return navigator.getElementNamespaceUri(obj);
        }
        if (navigator.isAttribute(obj)) {
            String attributeNamespaceUri = navigator.getAttributeNamespaceUri(obj);
            return attributeNamespaceUri == null ? "" : attributeNamespaceUri;
        }
        if (navigator.isProcessingInstruction(obj) || navigator.isNamespace(obj) || navigator.isDocument(obj) || navigator.isComment(obj) || navigator.isText(obj)) {
            return "";
        }
        throw new FunctionCallException("The argument to the namespace-uri function must be a node-set");
    }
}
