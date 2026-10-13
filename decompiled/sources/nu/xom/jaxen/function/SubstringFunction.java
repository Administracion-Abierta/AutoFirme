package nu.xom.jaxen.function;

import java.util.List;
import nu.xom.jaxen.Context;
import nu.xom.jaxen.Function;
import nu.xom.jaxen.FunctionCallException;
import nu.xom.jaxen.Navigator;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/function/SubstringFunction.class */
public class SubstringFunction implements Function {
    @Override // nu.xom.jaxen.Function
    public Object call(Context context, List list) throws FunctionCallException {
        int iIntValue;
        int size = list.size();
        if (size < 2 || size > 3) {
            throw new FunctionCallException("substring() requires two or three arguments.");
        }
        Navigator navigator = context.getNavigator();
        String strEvaluate = StringFunction.evaluate(list.get(0), navigator);
        if (strEvaluate == null || (iIntValue = StringLengthFunction.evaluate(list.get(0), navigator).intValue()) == 0) {
            return "";
        }
        Double dEvaluate = NumberFunction.evaluate(list.get(1), navigator);
        if (dEvaluate.isNaN()) {
            return "";
        }
        int iIntValue2 = RoundFunction.evaluate(dEvaluate, navigator).intValue() - 1;
        int iIntValue3 = iIntValue;
        if (size == 3) {
            Double dEvaluate2 = NumberFunction.evaluate(list.get(2), navigator);
            iIntValue3 = !dEvaluate2.isNaN() ? RoundFunction.evaluate(dEvaluate2, navigator).intValue() : 0;
        }
        if (iIntValue3 < 0) {
            return "";
        }
        int i = iIntValue2 + iIntValue3;
        if (size == 2) {
            i = iIntValue;
        }
        if (iIntValue2 < 0) {
            iIntValue2 = 0;
        } else if (iIntValue2 > iIntValue) {
            return "";
        }
        if (i > iIntValue) {
            i = iIntValue;
        } else if (i < iIntValue2) {
            return "";
        }
        return iIntValue == strEvaluate.length() ? strEvaluate.substring(iIntValue2, i) : unicodeSubstring(strEvaluate, iIntValue2, i);
    }

    private static String unicodeSubstring(String str, int i, int i2) {
        StringBuffer stringBuffer = new StringBuffer(str.length());
        int i3 = 0;
        for (int i4 = 0; i4 < i2; i4++) {
            char cCharAt = str.charAt(i3);
            if (i4 >= i) {
                stringBuffer.append(cCharAt);
            }
            if (cCharAt >= 55296) {
                i3++;
                if (i4 >= i) {
                    stringBuffer.append(str.charAt(i3));
                }
            }
            i3++;
        }
        return stringBuffer.toString();
    }
}
