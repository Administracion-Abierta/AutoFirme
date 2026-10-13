package nu.xom.jaxen.function;

import java.util.List;
import nu.xom.jaxen.Context;
import nu.xom.jaxen.Function;
import nu.xom.jaxen.FunctionCallException;
import nu.xom.jaxen.Navigator;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/jaxen/function/StartsWithFunction.class */
public class StartsWithFunction implements Function {
    @Override // nu.xom.jaxen.Function
    public Object call(Context context, List list) throws FunctionCallException {
        if (list.size() == 2) {
            return evaluate(list.get(0), list.get(1), context.getNavigator());
        }
        throw new FunctionCallException("starts-with() requires two arguments.");
    }

    public static Boolean evaluate(Object obj, Object obj2, Navigator navigator) {
        return StringFunction.evaluate(obj, navigator).startsWith(StringFunction.evaluate(obj2, navigator)) ? Boolean.TRUE : Boolean.FALSE;
    }
}
