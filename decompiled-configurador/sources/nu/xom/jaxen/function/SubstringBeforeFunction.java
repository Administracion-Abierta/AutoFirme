package nu.xom.jaxen.function;

import java.util.List;
import nu.xom.jaxen.Context;
import nu.xom.jaxen.Function;
import nu.xom.jaxen.FunctionCallException;
import nu.xom.jaxen.Navigator;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/jaxen/function/SubstringBeforeFunction.class */
public class SubstringBeforeFunction implements Function {
    @Override // nu.xom.jaxen.Function
    public Object call(Context context, List list) throws FunctionCallException {
        if (list.size() == 2) {
            return evaluate(list.get(0), list.get(1), context.getNavigator());
        }
        throw new FunctionCallException("substring-before() requires two arguments.");
    }

    public static String evaluate(Object obj, Object obj2, Navigator navigator) {
        String strEvaluate = StringFunction.evaluate(obj, navigator);
        int iIndexOf = strEvaluate.indexOf(StringFunction.evaluate(obj2, navigator));
        return iIndexOf < 0 ? "" : strEvaluate.substring(0, iIndexOf);
    }
}
