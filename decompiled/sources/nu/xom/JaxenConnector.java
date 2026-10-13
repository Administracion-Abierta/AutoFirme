package nu.xom;

import java.util.List;
import java.util.ListIterator;
import nu.xom.jaxen.BaseXPath;
import nu.xom.jaxen.FunctionContext;
import nu.xom.jaxen.JaxenException;
import nu.xom.jaxen.XPathFunctionContext;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/JaxenConnector.class */
class JaxenConnector extends BaseXPath {
    private static final long serialVersionUID = 9025734269448515308L;
    private static FunctionContext functionContext = new XPathFunctionContext(false);

    JaxenConnector(String str) throws JaxenException {
        super(str, new JaxenNavigator());
        setFunctionContext(functionContext);
    }

    @Override // nu.xom.jaxen.BaseXPath, nu.xom.jaxen.XPath
    public List selectNodes(Object obj) throws JaxenException {
        List listSelectNodes = super.selectNodes(obj);
        ListIterator listIterator = listSelectNodes.listIterator();
        while (listIterator.hasNext()) {
            Object next = listIterator.next();
            if (next instanceof List) {
                List list = (List) next;
                listIterator.set(list.get(0));
                if (list.size() > 1) {
                    ListIterator listIterator2 = list.listIterator(1);
                    while (listIterator2.hasNext()) {
                        listIterator.add(listIterator2.next());
                    }
                }
            } else {
                try {
                    if (((Node) next).isDocumentFragment()) {
                        listIterator.remove();
                        if (listSelectNodes.isEmpty()) {
                            throw new XPathException("Tried to get document node of disconnected subtree");
                        }
                    }
                } catch (ClassCastException e) {
                    throw new XPathTypeException(listSelectNodes.get(0));
                }
            }
        }
        return listSelectNodes;
    }
}
