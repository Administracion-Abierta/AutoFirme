package nu.xom.jaxen.function;

import java.util.Iterator;
import java.util.List;
import nu.xom.jaxen.Context;
import nu.xom.jaxen.Function;
import nu.xom.jaxen.FunctionCallException;
import nu.xom.jaxen.Navigator;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/function/SumFunction.class */
public class SumFunction implements Function {
    @Override // nu.xom.jaxen.Function
    public Object call(Context context, List list) throws FunctionCallException {
        if (list.size() == 1) {
            return evaluate(list.get(0), context.getNavigator());
        }
        throw new FunctionCallException("sum() requires one argument.");
    }

    public static Double evaluate(Object obj, Navigator navigator) throws FunctionCallException {
        double dDoubleValue = 0.0d;
        if (!(obj instanceof List)) {
            throw new FunctionCallException("The argument to the sum function must be a node-set");
        }
        Iterator it = ((List) obj).iterator();
        while (it.hasNext()) {
            dDoubleValue += NumberFunction.evaluate(it.next(), navigator).doubleValue();
        }
        return new Double(dDoubleValue);
    }
}
