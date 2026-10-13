package nu.xom.jaxen.function;

import java.util.List;
import nu.xom.jaxen.Context;
import nu.xom.jaxen.Function;
import nu.xom.jaxen.FunctionCallException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/jaxen/function/TrueFunction.class */
public class TrueFunction implements Function {
    @Override // nu.xom.jaxen.Function
    public Object call(Context context, List list) throws FunctionCallException {
        if (list.size() == 0) {
            return evaluate();
        }
        throw new FunctionCallException("true() requires no arguments.");
    }

    public static Boolean evaluate() {
        return Boolean.TRUE;
    }
}
