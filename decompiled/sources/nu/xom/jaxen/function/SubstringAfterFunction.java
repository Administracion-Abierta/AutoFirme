package nu.xom.jaxen.function;

import java.util.List;
import nu.xom.jaxen.Context;
import nu.xom.jaxen.Function;
import nu.xom.jaxen.FunctionCallException;
import nu.xom.jaxen.Navigator;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/function/SubstringAfterFunction.class */
public class SubstringAfterFunction implements Function {
    @Override // nu.xom.jaxen.Function
    public Object call(Context context, List list) throws FunctionCallException {
        if (list.size() == 2) {
            return evaluate(list.get(0), list.get(1), context.getNavigator());
        }
        throw new FunctionCallException("substring-after() requires two arguments.");
    }

    public static String evaluate(Object obj, Object obj2, Navigator navigator) {
        String strEvaluate = StringFunction.evaluate(obj, navigator);
        String strEvaluate2 = StringFunction.evaluate(obj2, navigator);
        int iIndexOf = strEvaluate.indexOf(strEvaluate2);
        return iIndexOf < 0 ? "" : strEvaluate.substring(iIndexOf + strEvaluate2.length());
    }
}
