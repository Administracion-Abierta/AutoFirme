package nu.xom.jaxen.function;

import java.util.List;
import nu.xom.jaxen.Context;
import nu.xom.jaxen.Function;
import nu.xom.jaxen.FunctionCallException;
import nu.xom.jaxen.Navigator;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/jaxen/function/NotFunction.class */
public class NotFunction implements Function {
    @Override // nu.xom.jaxen.Function
    public Object call(Context context, List list) throws FunctionCallException {
        if (list.size() == 1) {
            return evaluate(list.get(0), context.getNavigator());
        }
        throw new FunctionCallException("not() requires one argument.");
    }

    public static Boolean evaluate(Object obj, Navigator navigator) {
        return BooleanFunction.evaluate(obj, navigator).booleanValue() ? Boolean.FALSE : Boolean.TRUE;
    }
}
