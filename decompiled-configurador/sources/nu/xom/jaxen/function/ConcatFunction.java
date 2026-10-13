package nu.xom.jaxen.function;

import java.util.Iterator;
import java.util.List;
import nu.xom.jaxen.Context;
import nu.xom.jaxen.Function;
import nu.xom.jaxen.FunctionCallException;
import nu.xom.jaxen.Navigator;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/jaxen/function/ConcatFunction.class */
public class ConcatFunction implements Function {
    @Override // nu.xom.jaxen.Function
    public Object call(Context context, List list) throws FunctionCallException {
        if (list.size() >= 2) {
            return evaluate(list, context.getNavigator());
        }
        throw new FunctionCallException("concat() requires at least two arguments");
    }

    public static String evaluate(List list, Navigator navigator) {
        StringBuffer stringBuffer = new StringBuffer();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            stringBuffer.append(StringFunction.evaluate(it.next(), navigator));
        }
        return stringBuffer.toString();
    }
}
