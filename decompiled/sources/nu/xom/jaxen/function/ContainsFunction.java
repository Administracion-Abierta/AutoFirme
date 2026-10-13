package nu.xom.jaxen.function;

import java.util.List;
import nu.xom.jaxen.Context;
import nu.xom.jaxen.Function;
import nu.xom.jaxen.FunctionCallException;
import nu.xom.jaxen.Navigator;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/function/ContainsFunction.class */
public class ContainsFunction implements Function {
    @Override // nu.xom.jaxen.Function
    public Object call(Context context, List list) throws FunctionCallException {
        if (list.size() == 2) {
            return evaluate(list.get(0), list.get(1), context.getNavigator());
        }
        throw new FunctionCallException("contains() requires two arguments.");
    }

    public static Boolean evaluate(Object obj, Object obj2, Navigator navigator) {
        return StringFunction.evaluate(obj, navigator).indexOf(StringFunction.evaluate(obj2, navigator)) >= 0 ? Boolean.TRUE : Boolean.FALSE;
    }
}
