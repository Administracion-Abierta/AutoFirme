package nu.xom.jaxen.function.ext;

import java.util.List;
import java.util.Locale;
import nu.xom.jaxen.Context;
import nu.xom.jaxen.FunctionCallException;
import nu.xom.jaxen.Navigator;
import nu.xom.jaxen.function.StringFunction;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/function/ext/UpperFunction.class */
public class UpperFunction extends LocaleFunctionSupport {
    @Override // nu.xom.jaxen.Function
    public Object call(Context context, List list) throws FunctionCallException {
        Navigator navigator = context.getNavigator();
        int size = list.size();
        if (size <= 0) {
            throw new FunctionCallException("upper-case() requires at least one argument.");
        }
        Object obj = list.get(0);
        Locale locale = null;
        if (size > 1) {
            locale = getLocale(list.get(1), navigator);
        }
        return evaluate(obj, locale, navigator);
    }

    public static String evaluate(Object obj, Locale locale, Navigator navigator) {
        String strEvaluate = StringFunction.evaluate(obj, navigator);
        if (locale == null) {
            locale = Locale.ENGLISH;
        }
        return strEvaluate.toUpperCase(locale);
    }
}
