package nu.xom.jaxen.function;

import java.util.List;
import nu.xom.jaxen.Context;
import nu.xom.jaxen.Function;
import nu.xom.jaxen.FunctionCallException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/jaxen/function/FalseFunction.class */
public class FalseFunction implements Function {
    @Override // nu.xom.jaxen.Function
    public Object call(Context context, List list) throws FunctionCallException {
        if (list.size() == 0) {
            return evaluate();
        }
        throw new FunctionCallException("false() requires no arguments.");
    }

    public static Boolean evaluate() {
        return Boolean.FALSE;
    }
}
