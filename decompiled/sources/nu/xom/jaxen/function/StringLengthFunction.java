package nu.xom.jaxen.function;

import java.util.List;
import nu.xom.jaxen.Context;
import nu.xom.jaxen.Function;
import nu.xom.jaxen.FunctionCallException;
import nu.xom.jaxen.Navigator;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/function/StringLengthFunction.class */
public class StringLengthFunction implements Function {
    @Override // nu.xom.jaxen.Function
    public Object call(Context context, List list) throws FunctionCallException {
        if (list.size() == 0) {
            return evaluate(context.getNodeSet(), context.getNavigator());
        }
        if (list.size() == 1) {
            return evaluate(list.get(0), context.getNavigator());
        }
        throw new FunctionCallException("string-length() requires one argument.");
    }

    public static Double evaluate(Object obj, Navigator navigator) throws FunctionCallException {
        String strEvaluate = StringFunction.evaluate(obj, navigator);
        char[] charArray = strEvaluate.toCharArray();
        int i = 0;
        int i2 = 0;
        while (i2 < charArray.length) {
            char c = charArray[i2];
            i++;
            if (c >= 55296 && c <= 57343) {
                try {
                    char c2 = charArray[i2 + 1];
                    if (c2 < 56320 || c2 > 57343) {
                        throw new FunctionCallException(new StringBuffer().append("Bad surrogate pair in string ").append(strEvaluate).toString());
                    }
                    i2++;
                } catch (ArrayIndexOutOfBoundsException e) {
                    throw new FunctionCallException(new StringBuffer().append("Bad surrogate pair in string ").append(strEvaluate).toString());
                }
            }
            i2++;
        }
        return new Double(i);
    }
}
