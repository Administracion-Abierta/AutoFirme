package nu.xom.jaxen.function;

import java.util.HashMap;
import java.util.List;
import nu.xom.jaxen.Context;
import nu.xom.jaxen.Function;
import nu.xom.jaxen.FunctionCallException;
import nu.xom.jaxen.Navigator;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/function/TranslateFunction.class */
public class TranslateFunction implements Function {
    @Override // nu.xom.jaxen.Function
    public Object call(Context context, List list) throws FunctionCallException {
        if (list.size() == 3) {
            return evaluate(list.get(0), list.get(1), list.get(2), context.getNavigator());
        }
        throw new FunctionCallException("translate() requires three arguments.");
    }

    public static String evaluate(Object obj, Object obj2, Object obj3, Navigator navigator) throws FunctionCallException {
        String strEvaluate = StringFunction.evaluate(obj, navigator);
        String strEvaluate2 = StringFunction.evaluate(obj2, navigator);
        String strEvaluate3 = StringFunction.evaluate(obj3, navigator);
        HashMap map = new HashMap();
        String[] unicodeCharacters = toUnicodeCharacters(strEvaluate2);
        String[] unicodeCharacters2 = toUnicodeCharacters(strEvaluate3);
        int length = unicodeCharacters.length;
        int length2 = unicodeCharacters2.length;
        for (int i = 0; i < length; i++) {
            String str = unicodeCharacters[i];
            if (!map.containsKey(str)) {
                if (i < length2) {
                    map.put(str, unicodeCharacters2[i]);
                } else {
                    map.put(str, null);
                }
            }
        }
        StringBuffer stringBuffer = new StringBuffer(strEvaluate.length());
        for (String str2 : toUnicodeCharacters(strEvaluate)) {
            if (map.containsKey(str2)) {
                String str3 = (String) map.get(str2);
                if (str3 != null) {
                    stringBuffer.append(str3);
                }
            } else {
                stringBuffer.append(str2);
            }
        }
        return stringBuffer.toString();
    }

    private static String[] toUnicodeCharacters(String str) throws FunctionCallException {
        String[] strArr = new String[str.length()];
        int i = 0;
        int i2 = 0;
        while (i2 < str.length()) {
            char cCharAt = str.charAt(i2);
            if (isHighSurrogate(cCharAt)) {
                try {
                    char cCharAt2 = str.charAt(i2 + 1);
                    if (!isLowSurrogate(cCharAt2)) {
                        throw new FunctionCallException("Mismatched surrogate pair in translate function");
                    }
                    strArr[i] = new StringBuffer().append(cCharAt).append("").append(cCharAt2).toString().intern();
                    i2++;
                } catch (StringIndexOutOfBoundsException e) {
                    throw new FunctionCallException("High surrogate without low surrogate at end of string passed to translate function");
                }
            } else {
                strArr[i] = String.valueOf(cCharAt).intern();
            }
            i++;
            i2++;
        }
        if (i == strArr.length) {
            return strArr;
        }
        String[] strArr2 = new String[i];
        System.arraycopy(strArr, 0, strArr2, 0, i);
        return strArr2;
    }

    private static boolean isHighSurrogate(char c) {
        return c >= 55296 && c <= 56319;
    }

    private static boolean isLowSurrogate(char c) {
        return c >= 56320 && c <= 57343;
    }
}
