package nu.xom.jaxen.function;

import java.util.Iterator;
import java.util.List;
import nu.xom.jaxen.Context;
import nu.xom.jaxen.Function;
import nu.xom.jaxen.FunctionCallException;
import nu.xom.jaxen.Navigator;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/function/NumberFunction.class */
public class NumberFunction implements Function {
    private static final Double NaN = new Double(Double.NaN);

    @Override // nu.xom.jaxen.Function
    public Object call(Context context, List list) throws FunctionCallException {
        if (list.size() == 1) {
            return evaluate(list.get(0), context.getNavigator());
        }
        if (list.size() == 0) {
            return evaluate(context.getNodeSet(), context.getNavigator());
        }
        throw new FunctionCallException("number() takes at most one argument.");
    }

    public static Double evaluate(Object obj, Navigator navigator) {
        if (obj instanceof Double) {
            return (Double) obj;
        }
        if (obj instanceof String) {
            try {
                return new Double((String) obj);
            } catch (NumberFormatException e) {
                return NaN;
            }
        }
        if ((obj instanceof List) || (obj instanceof Iterator)) {
            return evaluate(StringFunction.evaluate(obj, navigator), navigator);
        }
        if (navigator.isElement(obj) || navigator.isAttribute(obj) || navigator.isText(obj) || navigator.isComment(obj) || navigator.isProcessingInstruction(obj) || navigator.isDocument(obj) || navigator.isNamespace(obj)) {
            return evaluate(StringFunction.evaluate(obj, navigator), navigator);
        }
        if (obj instanceof Boolean) {
            return Boolean.TRUE.equals(obj) ? new Double(1.0d) : new Double(0.0d);
        }
        return NaN;
    }

    public static boolean isNaN(double d) {
        return Double.isNaN(d);
    }

    public static boolean isNaN(Double d) {
        return d.equals(NaN);
    }
}
