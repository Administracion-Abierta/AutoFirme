package nu.xom.jaxen.function.xslt;

import java.util.List;
import nu.xom.jaxen.Context;
import nu.xom.jaxen.Function;
import nu.xom.jaxen.FunctionCallException;
import nu.xom.jaxen.Navigator;
import nu.xom.jaxen.function.StringFunction;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/function/xslt/DocumentFunction.class */
public class DocumentFunction implements Function {
    @Override // nu.xom.jaxen.Function
    public Object call(Context context, List list) throws FunctionCallException {
        if (list.size() != 1) {
            throw new FunctionCallException("document() requires one argument.");
        }
        Navigator navigator = context.getNavigator();
        return evaluate(StringFunction.evaluate(list.get(0), navigator), navigator);
    }

    public static Object evaluate(String str, Navigator navigator) throws FunctionCallException {
        return navigator.getDocument(str);
    }
}
