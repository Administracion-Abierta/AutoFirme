package nu.xom.jaxen;

import java.util.HashMap;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/SimpleFunctionContext.class */
public class SimpleFunctionContext implements FunctionContext {
    private HashMap functions = new HashMap();

    public void registerFunction(String str, String str2, Function function) {
        this.functions.put(new QualifiedName(str, str2), function);
    }

    @Override // nu.xom.jaxen.FunctionContext
    public Function getFunction(String str, String str2, String str3) throws UnresolvableException {
        QualifiedName qualifiedName = new QualifiedName(str, str3);
        if (this.functions.containsKey(qualifiedName)) {
            return (Function) this.functions.get(qualifiedName);
        }
        throw new UnresolvableException(new StringBuffer().append("No Such Function ").append(qualifiedName.getClarkForm()).toString());
    }
}
