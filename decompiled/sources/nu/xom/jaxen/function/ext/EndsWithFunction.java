package nu.xom.jaxen.function.ext;

import java.util.List;
import nu.xom.jaxen.Context;
import nu.xom.jaxen.Function;
import nu.xom.jaxen.FunctionCallException;
import nu.xom.jaxen.Navigator;
import nu.xom.jaxen.function.StringFunction;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/function/ext/EndsWithFunction.class */
public class EndsWithFunction implements Function {
    @Override // nu.xom.jaxen.Function
    public Object call(Context context, List list) throws FunctionCallException {
        if (list.size() == 2) {
            return evaluate(list.get(0), list.get(1), context.getNavigator());
        }
        throw new FunctionCallException("ends-with() requires two arguments.");
    }

    public static Boolean evaluate(Object obj, Object obj2, Navigator navigator) {
        return StringFunction.evaluate(obj, navigator).endsWith(StringFunction.evaluate(obj2, navigator)) ? Boolean.TRUE : Boolean.FALSE;
    }
}
