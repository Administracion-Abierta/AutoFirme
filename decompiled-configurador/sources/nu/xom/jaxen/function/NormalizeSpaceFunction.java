package nu.xom.jaxen.function;

import java.util.List;
import nu.xom.jaxen.Context;
import nu.xom.jaxen.Function;
import nu.xom.jaxen.FunctionCallException;
import nu.xom.jaxen.Navigator;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/jaxen/function/NormalizeSpaceFunction.class */
public class NormalizeSpaceFunction implements Function {
    @Override // nu.xom.jaxen.Function
    public Object call(Context context, List list) throws FunctionCallException {
        if (list.size() == 0) {
            return evaluate(context.getNodeSet(), context.getNavigator());
        }
        if (list.size() == 1) {
            return evaluate(list.get(0), context.getNavigator());
        }
        throw new FunctionCallException("normalize-space() cannot have more than one argument");
    }

    public static String evaluate(Object obj, Navigator navigator) {
        char[] charArray = StringFunction.evaluate(obj, navigator).toCharArray();
        int i = 0;
        int i2 = 0;
        boolean z = false;
        int i3 = 0;
        while (i3 < charArray.length) {
            if (isXMLSpace(charArray[i3])) {
                if (z) {
                    int i4 = i;
                    i++;
                    charArray[i4] = ' ';
                }
                do {
                    i3++;
                    if (i3 >= charArray.length) {
                        break;
                    }
                } while (isXMLSpace(charArray[i3]));
            } else {
                int i5 = i;
                i++;
                int i6 = i3;
                i3++;
                charArray[i5] = charArray[i6];
                z = true;
                i2 = i;
            }
        }
        return new String(charArray, 0, i2);
    }

    private static boolean isXMLSpace(char c) {
        return c == ' ' || c == '\n' || c == '\r' || c == '\t';
    }
}
