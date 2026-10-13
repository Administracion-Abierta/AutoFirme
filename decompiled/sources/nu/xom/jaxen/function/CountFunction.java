package nu.xom.jaxen.function;

import java.util.List;
import nu.xom.jaxen.Context;
import nu.xom.jaxen.Function;
import nu.xom.jaxen.FunctionCallException;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/function/CountFunction.class */
public class CountFunction implements Function {
    @Override // nu.xom.jaxen.Function
    public Object call(Context context, List list) throws FunctionCallException {
        if (list.size() == 1) {
            return evaluate(list.get(0));
        }
        throw new FunctionCallException("count() requires one argument.");
    }

    public static Double evaluate(Object obj) throws FunctionCallException {
        if (obj instanceof List) {
            return new Double(((List) obj).size());
        }
        throw new FunctionCallException("count() function can only be used for node-sets");
    }
}
