package nu.xom.jaxen;

import java.io.Serializable;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/jaxen/ContextSupport.class */
public class ContextSupport implements Serializable {
    private static final long serialVersionUID = 4494082174713652559L;
    private transient FunctionContext functionContext;
    private NamespaceContext namespaceContext;
    private VariableContext variableContext;
    private Navigator navigator;

    public ContextSupport() {
    }

    public ContextSupport(NamespaceContext namespaceContext, FunctionContext functionContext, VariableContext variableContext, Navigator navigator) {
        setNamespaceContext(namespaceContext);
        setFunctionContext(functionContext);
        setVariableContext(variableContext);
        this.navigator = navigator;
    }

    public void setNamespaceContext(NamespaceContext namespaceContext) {
        this.namespaceContext = namespaceContext;
    }

    public NamespaceContext getNamespaceContext() {
        return this.namespaceContext;
    }

    public void setFunctionContext(FunctionContext functionContext) {
        this.functionContext = functionContext;
    }

    public FunctionContext getFunctionContext() {
        return this.functionContext;
    }

    public void setVariableContext(VariableContext variableContext) {
        this.variableContext = variableContext;
    }

    public VariableContext getVariableContext() {
        return this.variableContext;
    }

    public Navigator getNavigator() {
        return this.navigator;
    }

    public String translateNamespacePrefixToUri(String str) {
        if (CanonicalizerBase.XML.equals(str)) {
            return "http://www.w3.org/XML/1998/namespace";
        }
        NamespaceContext namespaceContext = getNamespaceContext();
        if (namespaceContext != null) {
            return namespaceContext.translateNamespacePrefixToUri(str);
        }
        return null;
    }

    public Object getVariableValue(String str, String str2, String str3) throws UnresolvableException {
        VariableContext variableContext = getVariableContext();
        if (variableContext != null) {
            return variableContext.getVariableValue(str, str2, str3);
        }
        throw new UnresolvableException("No variable context installed");
    }

    public Function getFunction(String str, String str2, String str3) throws UnresolvableException {
        FunctionContext functionContext = getFunctionContext();
        if (functionContext != null) {
            return functionContext.getFunction(str, str2, str3);
        }
        throw new UnresolvableException("No function context installed");
    }
}
