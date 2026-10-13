package nu.xom.jaxen.function.ext;

import java.util.Collections;
import java.util.List;
import nu.xom.jaxen.Context;
import nu.xom.jaxen.ContextSupport;
import nu.xom.jaxen.Function;
import nu.xom.jaxen.FunctionCallException;
import nu.xom.jaxen.Navigator;
import nu.xom.jaxen.XPath;
import nu.xom.jaxen.function.StringFunction;
import nu.xom.jaxen.saxpath.SAXPathException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/jaxen/function/ext/EvaluateFunction.class */
public class EvaluateFunction implements Function {
    @Override // nu.xom.jaxen.Function
    public Object call(Context context, List list) throws FunctionCallException {
        if (list.size() == 1) {
            return evaluate(context, list.get(0));
        }
        throw new FunctionCallException("evaluate() requires one argument");
    }

    public static List evaluate(Context context, Object obj) throws FunctionCallException {
        if (context.getNodeSet().size() == 0) {
            return Collections.EMPTY_LIST;
        }
        Navigator navigator = context.getNavigator();
        try {
            XPath xPath = navigator.parseXPath(obj instanceof String ? (String) obj : StringFunction.evaluate(obj, navigator));
            ContextSupport contextSupport = context.getContextSupport();
            xPath.setVariableContext(contextSupport.getVariableContext());
            xPath.setFunctionContext(contextSupport.getFunctionContext());
            xPath.setNamespaceContext(contextSupport.getNamespaceContext());
            return xPath.selectNodes(context.duplicate());
        } catch (SAXPathException e) {
            throw new FunctionCallException(e.toString());
        }
    }
}
