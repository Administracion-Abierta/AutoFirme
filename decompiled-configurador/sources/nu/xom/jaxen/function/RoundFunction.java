package nu.xom.jaxen.function;

import java.util.List;
import nu.xom.jaxen.Context;
import nu.xom.jaxen.Function;
import nu.xom.jaxen.FunctionCallException;
import nu.xom.jaxen.Navigator;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/jaxen/function/RoundFunction.class */
public class RoundFunction implements Function {
    @Override // nu.xom.jaxen.Function
    public Object call(Context context, List list) throws FunctionCallException {
        if (list.size() == 1) {
            return evaluate(list.get(0), context.getNavigator());
        }
        throw new FunctionCallException("round() requires one argument.");
    }

    public static Double evaluate(Object obj, Navigator navigator) {
        Double dEvaluate = NumberFunction.evaluate(obj, navigator);
        return (dEvaluate.isNaN() || dEvaluate.isInfinite()) ? dEvaluate : new Double(Math.round(dEvaluate.doubleValue()));
    }
}
