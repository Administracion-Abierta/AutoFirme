package nu.xom.jaxen.function;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import nu.xom.jaxen.Context;
import nu.xom.jaxen.Function;
import nu.xom.jaxen.FunctionCallException;
import nu.xom.jaxen.Navigator;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/jaxen/function/IdFunction.class */
public class IdFunction implements Function {
    @Override // nu.xom.jaxen.Function
    public Object call(Context context, List list) throws FunctionCallException {
        if (list.size() == 1) {
            return evaluate(context.getNodeSet(), list.get(0), context.getNavigator());
        }
        throw new FunctionCallException("id() requires one argument");
    }

    public static List evaluate(List list, Object obj, Navigator navigator) {
        if (list.size() == 0) {
            return Collections.EMPTY_LIST;
        }
        ArrayList arrayList = new ArrayList();
        Object obj2 = list.get(0);
        if (obj instanceof List) {
            Iterator it = ((List) obj).iterator();
            while (it.hasNext()) {
                arrayList.addAll(evaluate(list, StringFunction.evaluate(it.next(), navigator), navigator));
            }
        } else {
            StringTokenizer stringTokenizer = new StringTokenizer(StringFunction.evaluate(obj, navigator), " \t\n\r");
            while (stringTokenizer.hasMoreTokens()) {
                Object elementById = navigator.getElementById(obj2, stringTokenizer.nextToken());
                if (elementById != null) {
                    arrayList.add(elementById);
                }
            }
        }
        return arrayList;
    }
}
