package nu.xom.jaxen;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/jaxen/SimpleNamespaceContext.class */
public class SimpleNamespaceContext implements NamespaceContext, Serializable {
    private static final long serialVersionUID = -808928409643497762L;
    private Map namespaces;

    public SimpleNamespaceContext() {
        this.namespaces = new HashMap();
    }

    public SimpleNamespaceContext(Map map) {
        for (Map.Entry entry : map.entrySet()) {
            if (!(entry.getKey() instanceof String) || !(entry.getValue() instanceof String)) {
                throw new ClassCastException("Non-string namespace binding");
            }
        }
        this.namespaces = new HashMap(map);
    }

    public void addElementNamespaces(Navigator navigator, Object obj) throws UnsupportedAxisException {
        Iterator namespaceAxisIterator = navigator.getNamespaceAxisIterator(obj);
        while (namespaceAxisIterator.hasNext()) {
            Object next = namespaceAxisIterator.next();
            String namespacePrefix = navigator.getNamespacePrefix(next);
            String namespaceStringValue = navigator.getNamespaceStringValue(next);
            if (translateNamespacePrefixToUri(namespacePrefix) == null) {
                addNamespace(namespacePrefix, namespaceStringValue);
            }
        }
    }

    public void addNamespace(String str, String str2) {
        this.namespaces.put(str, str2);
    }

    @Override // nu.xom.jaxen.NamespaceContext
    public String translateNamespacePrefixToUri(String str) {
        if (this.namespaces.containsKey(str)) {
            return (String) this.namespaces.get(str);
        }
        return null;
    }
}
