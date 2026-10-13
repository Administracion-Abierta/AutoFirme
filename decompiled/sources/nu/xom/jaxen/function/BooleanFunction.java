package nu.xom.jaxen.function;

import java.util.List;
import nu.xom.jaxen.Context;
import nu.xom.jaxen.Function;
import nu.xom.jaxen.FunctionCallException;
import nu.xom.jaxen.Navigator;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/function/BooleanFunction.class */
public class BooleanFunction implements Function {
    @Override // nu.xom.jaxen.Function
    public Object call(Context context, List list) throws FunctionCallException {
        if (list.size() == 1) {
            return evaluate(list.get(0), context.getNavigator());
        }
        throw new FunctionCallException("boolean() requires one argument");
    }

    public static Boolean evaluate(Object obj, Navigator navigator) {
        if (obj instanceof List) {
            List list = (List) obj;
            if (list.size() == 0) {
                return Boolean.FALSE;
            }
            obj = list.get(0);
        }
        if (obj instanceof Boolean) {
            return (Boolean) obj;
        }
        if (obj instanceof Number) {
            double dDoubleValue = ((Number) obj).doubleValue();
            return (dDoubleValue == 0.0d || Double.isNaN(dDoubleValue)) ? Boolean.FALSE : Boolean.TRUE;
        }
        if (obj instanceof String) {
            return ((String) obj).length() > 0 ? Boolean.TRUE : Boolean.FALSE;
        }
        return obj != null ? Boolean.TRUE : Boolean.FALSE;
    }
}
