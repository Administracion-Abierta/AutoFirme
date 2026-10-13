package nu.xom.jaxen.function;

import java.util.List;
import nu.xom.jaxen.Context;
import nu.xom.jaxen.Function;
import nu.xom.jaxen.FunctionCallException;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/function/PositionFunction.class */
public class PositionFunction implements Function {
    @Override // nu.xom.jaxen.Function
    public Object call(Context context, List list) throws FunctionCallException {
        if (list.size() == 0) {
            return evaluate(context);
        }
        throw new FunctionCallException("position() does not take any arguments.");
    }

    public static Double evaluate(Context context) {
        return new Double(context.getPosition());
    }
}
