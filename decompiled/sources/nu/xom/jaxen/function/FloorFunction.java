package nu.xom.jaxen.function;

import java.util.List;
import nu.xom.jaxen.Context;
import nu.xom.jaxen.Function;
import nu.xom.jaxen.FunctionCallException;
import nu.xom.jaxen.Navigator;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/function/FloorFunction.class */
public class FloorFunction implements Function {
    @Override // nu.xom.jaxen.Function
    public Object call(Context context, List list) throws FunctionCallException {
        if (list.size() == 1) {
            return evaluate(list.get(0), context.getNavigator());
        }
        throw new FunctionCallException("floor() requires one argument.");
    }

    public static Double evaluate(Object obj, Navigator navigator) {
        return new Double(Math.floor(NumberFunction.evaluate(obj, navigator).doubleValue()));
    }
}
