package nu.xom.jaxen.function.ext;

import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import nu.xom.jaxen.Function;
import nu.xom.jaxen.Navigator;
import nu.xom.jaxen.function.StringFunction;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/function/ext/LocaleFunctionSupport.class */
public abstract class LocaleFunctionSupport implements Function {
    protected Locale getLocale(Object obj, Navigator navigator) {
        if (obj instanceof Locale) {
            return (Locale) obj;
        }
        if (obj instanceof List) {
            List list = (List) obj;
            if (list.isEmpty()) {
                return null;
            }
            return getLocale(list.get(0), navigator);
        }
        String strEvaluate = StringFunction.evaluate(obj, navigator);
        if (strEvaluate == null || strEvaluate.length() <= 0) {
            return null;
        }
        return findLocale(strEvaluate);
    }

    protected Locale findLocale(String str) {
        StringTokenizer stringTokenizer = new StringTokenizer(str, "-");
        if (!stringTokenizer.hasMoreTokens()) {
            return null;
        }
        String strNextToken = stringTokenizer.nextToken();
        if (!stringTokenizer.hasMoreTokens()) {
            return findLocaleForLanguage(strNextToken);
        }
        String strNextToken2 = stringTokenizer.nextToken();
        return !stringTokenizer.hasMoreTokens() ? new Locale(strNextToken, strNextToken2) : new Locale(strNextToken, strNextToken2, stringTokenizer.nextToken());
    }

    protected Locale findLocaleForLanguage(String str) {
        String country;
        String variant;
        for (Locale locale : Locale.getAvailableLocales()) {
            if (str.equals(locale.getLanguage()) && (((country = locale.getCountry()) == null || country.length() == 0) && ((variant = locale.getVariant()) == null || variant.length() == 0))) {
                return locale;
            }
        }
        return null;
    }
}
