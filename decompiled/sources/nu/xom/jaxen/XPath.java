package nu.xom.jaxen;

import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/XPath.class */
public interface XPath {
    Object evaluate(Object obj) throws JaxenException;

    String valueOf(Object obj) throws JaxenException;

    String stringValueOf(Object obj) throws JaxenException;

    boolean booleanValueOf(Object obj) throws JaxenException;

    Number numberValueOf(Object obj) throws JaxenException;

    List selectNodes(Object obj) throws JaxenException;

    Object selectSingleNode(Object obj) throws JaxenException;

    void addNamespace(String str, String str2) throws JaxenException;

    void setNamespaceContext(NamespaceContext namespaceContext);

    void setFunctionContext(FunctionContext functionContext);

    void setVariableContext(VariableContext variableContext);

    NamespaceContext getNamespaceContext();

    FunctionContext getFunctionContext();

    VariableContext getVariableContext();

    Navigator getNavigator();
}
