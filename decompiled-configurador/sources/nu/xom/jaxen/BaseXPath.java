package nu.xom.jaxen;

import java.io.Serializable;
import java.util.List;
import nu.xom.jaxen.expr.Expr;
import nu.xom.jaxen.expr.XPathExpr;
import nu.xom.jaxen.function.BooleanFunction;
import nu.xom.jaxen.function.NumberFunction;
import nu.xom.jaxen.function.StringFunction;
import nu.xom.jaxen.saxpath.SAXPathException;
import nu.xom.jaxen.saxpath.XPathReader;
import nu.xom.jaxen.saxpath.helpers.XPathReaderFactory;
import nu.xom.jaxen.util.SingletonList;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/jaxen/BaseXPath.class */
public class BaseXPath implements XPath, Serializable {
    private static final long serialVersionUID = -1993731281300293168L;
    private final String exprText;
    private final XPathExpr xpath;
    private ContextSupport support;
    private Navigator navigator;

    protected BaseXPath(String str) throws JaxenException {
        try {
            XPathReader xPathReaderCreateReader = XPathReaderFactory.createReader();
            JaxenHandler jaxenHandler = new JaxenHandler();
            xPathReaderCreateReader.setXPathHandler(jaxenHandler);
            xPathReaderCreateReader.parse(str);
            this.xpath = jaxenHandler.getXPathExpr();
            this.exprText = str;
        } catch (nu.xom.jaxen.saxpath.XPathSyntaxException e) {
            throw new XPathSyntaxException(e);
        } catch (SAXPathException e2) {
            throw new JaxenException(e2);
        }
    }

    public BaseXPath(String str, Navigator navigator) throws JaxenException {
        this(str);
        this.navigator = navigator;
    }

    @Override // nu.xom.jaxen.XPath
    public Object evaluate(Object obj) throws JaxenException {
        List listSelectNodes = selectNodes(obj);
        if (listSelectNodes != null && listSelectNodes.size() == 1) {
            Object obj2 = listSelectNodes.get(0);
            if ((obj2 instanceof String) || (obj2 instanceof Number) || (obj2 instanceof Boolean)) {
                return obj2;
            }
        }
        return listSelectNodes;
    }

    @Override // nu.xom.jaxen.XPath
    public List selectNodes(Object obj) throws JaxenException {
        return selectNodesForContext(getContext(obj));
    }

    @Override // nu.xom.jaxen.XPath
    public Object selectSingleNode(Object obj) throws JaxenException {
        List listSelectNodes = selectNodes(obj);
        if (listSelectNodes.isEmpty()) {
            return null;
        }
        return listSelectNodes.get(0);
    }

    @Override // nu.xom.jaxen.XPath
    public String valueOf(Object obj) throws JaxenException {
        return stringValueOf(obj);
    }

    @Override // nu.xom.jaxen.XPath
    public String stringValueOf(Object obj) throws JaxenException {
        Context context = getContext(obj);
        Object objSelectSingleNodeForContext = selectSingleNodeForContext(context);
        return objSelectSingleNodeForContext == null ? "" : StringFunction.evaluate(objSelectSingleNodeForContext, context.getNavigator());
    }

    @Override // nu.xom.jaxen.XPath
    public boolean booleanValueOf(Object obj) throws JaxenException {
        Context context = getContext(obj);
        List listSelectNodesForContext = selectNodesForContext(context);
        if (listSelectNodesForContext == null) {
            return false;
        }
        return BooleanFunction.evaluate(listSelectNodesForContext, context.getNavigator()).booleanValue();
    }

    @Override // nu.xom.jaxen.XPath
    public Number numberValueOf(Object obj) throws JaxenException {
        Context context = getContext(obj);
        return NumberFunction.evaluate(selectSingleNodeForContext(context), context.getNavigator());
    }

    @Override // nu.xom.jaxen.XPath
    public void addNamespace(String str, String str2) throws JaxenException {
        NamespaceContext namespaceContext = getNamespaceContext();
        if (!(namespaceContext instanceof SimpleNamespaceContext)) {
            throw new JaxenException("Operation not permitted while using a non-simple namespace context.");
        }
        ((SimpleNamespaceContext) namespaceContext).addNamespace(str, str2);
    }

    @Override // nu.xom.jaxen.XPath
    public void setNamespaceContext(NamespaceContext namespaceContext) {
        getContextSupport().setNamespaceContext(namespaceContext);
    }

    @Override // nu.xom.jaxen.XPath
    public void setFunctionContext(FunctionContext functionContext) {
        getContextSupport().setFunctionContext(functionContext);
    }

    @Override // nu.xom.jaxen.XPath
    public void setVariableContext(VariableContext variableContext) {
        getContextSupport().setVariableContext(variableContext);
    }

    @Override // nu.xom.jaxen.XPath
    public NamespaceContext getNamespaceContext() {
        return getContextSupport().getNamespaceContext();
    }

    @Override // nu.xom.jaxen.XPath
    public FunctionContext getFunctionContext() {
        return getContextSupport().getFunctionContext();
    }

    @Override // nu.xom.jaxen.XPath
    public VariableContext getVariableContext() {
        return getContextSupport().getVariableContext();
    }

    public Expr getRootExpr() {
        return this.xpath.getRootExpr();
    }

    public String toString() {
        return this.exprText;
    }

    public String debug() {
        return this.xpath.toString();
    }

    protected Context getContext(Object obj) {
        if (obj instanceof Context) {
            return (Context) obj;
        }
        Context context = new Context(getContextSupport());
        if (obj instanceof List) {
            context.setNodeSet((List) obj);
        } else {
            context.setNodeSet(new SingletonList(obj));
        }
        return context;
    }

    protected ContextSupport getContextSupport() {
        if (this.support == null) {
            this.support = new ContextSupport(createNamespaceContext(), createFunctionContext(), createVariableContext(), getNavigator());
        }
        return this.support;
    }

    @Override // nu.xom.jaxen.XPath
    public Navigator getNavigator() {
        return this.navigator;
    }

    protected FunctionContext createFunctionContext() {
        return XPathFunctionContext.getInstance();
    }

    protected NamespaceContext createNamespaceContext() {
        return new SimpleNamespaceContext();
    }

    protected VariableContext createVariableContext() {
        return new SimpleVariableContext();
    }

    protected List selectNodesForContext(Context context) throws JaxenException {
        return this.xpath.asList(context);
    }

    protected Object selectSingleNodeForContext(Context context) throws JaxenException {
        List listSelectNodesForContext = selectNodesForContext(context);
        if (listSelectNodesForContext.isEmpty()) {
            return null;
        }
        return listSelectNodesForContext.get(0);
    }
}
