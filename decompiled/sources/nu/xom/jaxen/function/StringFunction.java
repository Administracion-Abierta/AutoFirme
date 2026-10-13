package nu.xom.jaxen.function;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import nu.xom.jaxen.Context;
import nu.xom.jaxen.Function;
import nu.xom.jaxen.FunctionCallException;
import nu.xom.jaxen.JaxenRuntimeException;
import nu.xom.jaxen.Navigator;
import nu.xom.jaxen.UnsupportedAxisException;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/function/StringFunction.class */
public class StringFunction implements Function {
    private static DecimalFormat format = (DecimalFormat) NumberFormat.getInstance(Locale.ENGLISH);

    @Override // nu.xom.jaxen.Function
    public Object call(Context context, List list) throws FunctionCallException {
        int size = list.size();
        if (size == 0) {
            return evaluate(context.getNodeSet(), context.getNavigator());
        }
        if (size == 1) {
            return evaluate(list.get(0), context.getNavigator());
        }
        throw new FunctionCallException("string() takes at most argument.");
    }

    public static String evaluate(Object obj, Navigator navigator) {
        if (navigator != null) {
            try {
                if (navigator.isText(obj)) {
                    return navigator.getTextStringValue(obj);
                }
            } catch (UnsupportedAxisException e) {
                throw new JaxenRuntimeException(e);
            }
        }
        if (obj instanceof List) {
            List list = (List) obj;
            if (list.isEmpty()) {
                return "";
            }
            obj = list.get(0);
        }
        if (navigator != null) {
            if (navigator.isElement(obj)) {
                return navigator.getElementStringValue(obj);
            }
            if (navigator.isAttribute(obj)) {
                return navigator.getAttributeStringValue(obj);
            }
            if (navigator.isDocument(obj)) {
                Iterator childAxisIterator = navigator.getChildAxisIterator(obj);
                while (childAxisIterator.hasNext()) {
                    Object next = childAxisIterator.next();
                    if (navigator.isElement(next)) {
                        return navigator.getElementStringValue(next);
                    }
                }
            } else {
                if (navigator.isProcessingInstruction(obj)) {
                    return navigator.getProcessingInstructionData(obj);
                }
                if (navigator.isComment(obj)) {
                    return navigator.getCommentStringValue(obj);
                }
                if (navigator.isText(obj)) {
                    return navigator.getTextStringValue(obj);
                }
                if (navigator.isNamespace(obj)) {
                    return navigator.getNamespaceStringValue(obj);
                }
            }
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        if (obj instanceof Boolean) {
            return stringValue(((Boolean) obj).booleanValue());
        }
        return obj instanceof Number ? stringValue(((Number) obj).doubleValue()) : "";
    }

    private static String stringValue(double d) {
        String str;
        if (d == 0.0d) {
            return "0";
        }
        synchronized (format) {
            str = format.format(d);
        }
        return str;
    }

    private static String stringValue(boolean z) {
        return z ? "true" : "false";
    }

    static {
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
        decimalFormatSymbols.setNaN("NaN");
        decimalFormatSymbols.setInfinity("Infinity");
        format.setGroupingUsed(false);
        format.setMaximumFractionDigits(32);
        format.setDecimalFormatSymbols(decimalFormatSymbols);
    }
}
