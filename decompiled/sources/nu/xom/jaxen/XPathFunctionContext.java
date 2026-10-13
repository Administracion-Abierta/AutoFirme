package nu.xom.jaxen;

import com.aowagie.text.ElementTags;
import nu.xom.jaxen.function.BooleanFunction;
import nu.xom.jaxen.function.CeilingFunction;
import nu.xom.jaxen.function.ConcatFunction;
import nu.xom.jaxen.function.ContainsFunction;
import nu.xom.jaxen.function.CountFunction;
import nu.xom.jaxen.function.FalseFunction;
import nu.xom.jaxen.function.FloorFunction;
import nu.xom.jaxen.function.IdFunction;
import nu.xom.jaxen.function.LangFunction;
import nu.xom.jaxen.function.LastFunction;
import nu.xom.jaxen.function.LocalNameFunction;
import nu.xom.jaxen.function.NameFunction;
import nu.xom.jaxen.function.NamespaceUriFunction;
import nu.xom.jaxen.function.NormalizeSpaceFunction;
import nu.xom.jaxen.function.NotFunction;
import nu.xom.jaxen.function.NumberFunction;
import nu.xom.jaxen.function.PositionFunction;
import nu.xom.jaxen.function.RoundFunction;
import nu.xom.jaxen.function.StartsWithFunction;
import nu.xom.jaxen.function.StringFunction;
import nu.xom.jaxen.function.StringLengthFunction;
import nu.xom.jaxen.function.SubstringAfterFunction;
import nu.xom.jaxen.function.SubstringBeforeFunction;
import nu.xom.jaxen.function.SubstringFunction;
import nu.xom.jaxen.function.SumFunction;
import nu.xom.jaxen.function.TranslateFunction;
import nu.xom.jaxen.function.TrueFunction;
import nu.xom.jaxen.function.ext.EndsWithFunction;
import nu.xom.jaxen.function.ext.EvaluateFunction;
import nu.xom.jaxen.function.ext.LowerFunction;
import nu.xom.jaxen.function.ext.UpperFunction;
import nu.xom.jaxen.function.xslt.DocumentFunction;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/XPathFunctionContext.class */
public class XPathFunctionContext extends SimpleFunctionContext {
    private static XPathFunctionContext instance = new XPathFunctionContext();

    public static FunctionContext getInstance() {
        return instance;
    }

    public XPathFunctionContext() {
        this(true);
    }

    public XPathFunctionContext(boolean z) {
        registerXPathFunctions();
        if (z) {
            registerXSLTFunctions();
            registerExtensionFunctions();
        }
    }

    private void registerXPathFunctions() {
        registerFunction(null, "boolean", new BooleanFunction());
        registerFunction(null, "ceiling", new CeilingFunction());
        registerFunction(null, "concat", new ConcatFunction());
        registerFunction(null, "contains", new ContainsFunction());
        registerFunction(null, "count", new CountFunction());
        registerFunction(null, "false", new FalseFunction());
        registerFunction(null, "floor", new FloorFunction());
        registerFunction(null, ElementTags.ID, new IdFunction());
        registerFunction(null, "lang", new LangFunction());
        registerFunction(null, "last", new LastFunction());
        registerFunction(null, "local-name", new LocalNameFunction());
        registerFunction(null, "name", new NameFunction());
        registerFunction(null, "namespace-uri", new NamespaceUriFunction());
        registerFunction(null, "normalize-space", new NormalizeSpaceFunction());
        registerFunction(null, "not", new NotFunction());
        registerFunction(null, "number", new NumberFunction());
        registerFunction(null, "position", new PositionFunction());
        registerFunction(null, "round", new RoundFunction());
        registerFunction(null, "starts-with", new StartsWithFunction());
        registerFunction(null, "string", new StringFunction());
        registerFunction(null, "string-length", new StringLengthFunction());
        registerFunction(null, "substring-after", new SubstringAfterFunction());
        registerFunction(null, "substring-before", new SubstringBeforeFunction());
        registerFunction(null, "substring", new SubstringFunction());
        registerFunction(null, "sum", new SumFunction());
        registerFunction(null, "true", new TrueFunction());
        registerFunction(null, "translate", new TranslateFunction());
    }

    private void registerXSLTFunctions() {
        registerFunction(null, "document", new DocumentFunction());
    }

    private void registerExtensionFunctions() {
        registerFunction(null, "evaluate", new EvaluateFunction());
        registerFunction(null, "lower-case", new LowerFunction());
        registerFunction(null, "upper-case", new UpperFunction());
        registerFunction(null, "ends-with", new EndsWithFunction());
    }
}
