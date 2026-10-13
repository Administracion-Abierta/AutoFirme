package nu.xom.jaxen.function;

import java.util.List;
import nu.xom.jaxen.Context;
import nu.xom.jaxen.Function;
import nu.xom.jaxen.FunctionCallException;
import nu.xom.jaxen.Navigator;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/function/LocalNameFunction.class */
public class LocalNameFunction implements Function {
    @Override // nu.xom.jaxen.Function
    public Object call(Context context, List list) throws FunctionCallException {
        if (list.size() == 0) {
            return evaluate(context.getNodeSet(), context.getNavigator());
        }
        if (list.size() == 1) {
            return evaluate(list, context.getNavigator());
        }
        throw new FunctionCallException("local-name() requires zero or one argument.");
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
            return navigator.getElementName(obj);
        }
        if (navigator.isAttribute(obj)) {
            return navigator.getAttributeName(obj);
        }
        if (navigator.isProcessingInstruction(obj)) {
            return navigator.getProcessingInstructionTarget(obj);
        }
        if (navigator.isNamespace(obj)) {
            return navigator.getNamespacePrefix(obj);
        }
        if (navigator.isDocument(obj) || navigator.isComment(obj) || navigator.isText(obj)) {
            return "";
        }
        throw new FunctionCallException("The argument to the local-name function must be a node-set");
    }
}
