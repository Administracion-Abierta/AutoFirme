package nu.xom.jaxen.expr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import nu.xom.jaxen.Context;
import nu.xom.jaxen.ContextSupport;
import nu.xom.jaxen.JaxenException;
import nu.xom.jaxen.Navigator;
import nu.xom.jaxen.UnresolvableException;
import nu.xom.jaxen.expr.iter.IterableAxis;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/jaxen/expr/DefaultNameStep.class */
public class DefaultNameStep extends DefaultStep implements NameStep {
    private static final long serialVersionUID = 428414912247718390L;
    private String prefix;
    private String localName;
    private boolean matchesAnyName;
    private boolean hasPrefix;

    public DefaultNameStep(IterableAxis iterableAxis, String str, String str2, PredicateSet predicateSet) {
        super(iterableAxis, predicateSet);
        this.prefix = str;
        this.localName = str2;
        this.matchesAnyName = "*".equals(str2);
        this.hasPrefix = this.prefix != null && this.prefix.length() > 0;
    }

    @Override // nu.xom.jaxen.expr.NameStep
    public String getPrefix() {
        return this.prefix;
    }

    @Override // nu.xom.jaxen.expr.NameStep
    public String getLocalName() {
        return this.localName;
    }

    public boolean isMatchesAnyName() {
        return this.matchesAnyName;
    }

    @Override // nu.xom.jaxen.expr.DefaultStep, nu.xom.jaxen.expr.Step
    public String getText() {
        StringBuffer stringBuffer = new StringBuffer(64);
        stringBuffer.append(getAxisName()).append("::");
        if (getPrefix() != null && getPrefix().length() > 0) {
            stringBuffer.append(getPrefix()).append(':');
        }
        return stringBuffer.append(getLocalName()).append(super.getText()).toString();
    }

    @Override // nu.xom.jaxen.expr.DefaultStep, nu.xom.jaxen.expr.Step
    public List evaluate(Context context) throws JaxenException {
        List nodeSet = context.getNodeSet();
        int size = nodeSet.size();
        if (size == 0) {
            return Collections.EMPTY_LIST;
        }
        ContextSupport contextSupport = context.getContextSupport();
        IterableAxis iterableAxis = getIterableAxis();
        boolean z = !this.matchesAnyName && iterableAxis.supportsNamedAccess(contextSupport);
        if (size == 1) {
            Object obj = nodeSet.get(0);
            if (!z) {
                Iterator it = iterableAxis.iterator(obj, contextSupport);
                if (it == null || !it.hasNext()) {
                    return Collections.EMPTY_LIST;
                }
                ArrayList arrayList = new ArrayList(size);
                while (it.hasNext()) {
                    Object next = it.next();
                    if (matches(next, contextSupport)) {
                        arrayList.add(next);
                    }
                }
                return getPredicateSet().evaluatePredicates(arrayList, contextSupport);
            }
            String strTranslateNamespacePrefixToUri = null;
            if (this.hasPrefix) {
                strTranslateNamespacePrefixToUri = contextSupport.translateNamespacePrefixToUri(this.prefix);
                if (strTranslateNamespacePrefixToUri == null) {
                    throw new UnresolvableException(new StringBuffer().append("XPath expression uses unbound namespace prefix ").append(this.prefix).toString());
                }
            }
            Iterator itNamedAccessIterator = iterableAxis.namedAccessIterator(obj, contextSupport, this.localName, this.prefix, strTranslateNamespacePrefixToUri);
            if (itNamedAccessIterator == null || !itNamedAccessIterator.hasNext()) {
                return Collections.EMPTY_LIST;
            }
            ArrayList arrayList2 = new ArrayList();
            while (itNamedAccessIterator.hasNext()) {
                arrayList2.add(itNamedAccessIterator.next());
            }
            return getPredicateSet().evaluatePredicates(arrayList2, contextSupport);
        }
        IdentitySet identitySet = new IdentitySet();
        ArrayList arrayList3 = new ArrayList(size);
        ArrayList arrayList4 = new ArrayList(size);
        if (z) {
            String strTranslateNamespacePrefixToUri2 = null;
            if (this.hasPrefix) {
                strTranslateNamespacePrefixToUri2 = contextSupport.translateNamespacePrefixToUri(this.prefix);
                if (strTranslateNamespacePrefixToUri2 == null) {
                    throw new UnresolvableException(new StringBuffer().append("XPath expression uses unbound namespace prefix ").append(this.prefix).toString());
                }
            }
            for (int i = 0; i < size; i++) {
                Iterator itNamedAccessIterator2 = iterableAxis.namedAccessIterator(nodeSet.get(i), contextSupport, this.localName, this.prefix, strTranslateNamespacePrefixToUri2);
                if (itNamedAccessIterator2 != null && itNamedAccessIterator2.hasNext()) {
                    while (itNamedAccessIterator2.hasNext()) {
                        arrayList3.add(itNamedAccessIterator2.next());
                    }
                    for (Object obj2 : getPredicateSet().evaluatePredicates(arrayList3, contextSupport)) {
                        if (!identitySet.contains(obj2)) {
                            identitySet.add(obj2);
                            arrayList4.add(obj2);
                        }
                    }
                    arrayList3.clear();
                }
            }
        } else {
            for (int i2 = 0; i2 < size; i2++) {
                Iterator itAxisIterator = axisIterator(nodeSet.get(i2), contextSupport);
                if (itAxisIterator != null && itAxisIterator.hasNext()) {
                    while (itAxisIterator.hasNext()) {
                        Object next2 = itAxisIterator.next();
                        if (matches(next2, contextSupport)) {
                            arrayList3.add(next2);
                        }
                    }
                    for (Object obj3 : getPredicateSet().evaluatePredicates(arrayList3, contextSupport)) {
                        if (!identitySet.contains(obj3)) {
                            identitySet.add(obj3);
                            arrayList4.add(obj3);
                        }
                    }
                    arrayList3.clear();
                }
            }
        }
        return arrayList4;
    }

    @Override // nu.xom.jaxen.expr.Step
    public boolean matches(Object obj, ContextSupport contextSupport) throws JaxenException {
        String namespacePrefix;
        Navigator navigator = contextSupport.getNavigator();
        String strTranslateNamespacePrefixToUri = null;
        String attributeNamespaceUri = null;
        if (navigator.isElement(obj)) {
            namespacePrefix = navigator.getElementName(obj);
            attributeNamespaceUri = navigator.getElementNamespaceUri(obj);
        } else {
            if (navigator.isText(obj)) {
                return false;
            }
            if (navigator.isAttribute(obj)) {
                if (getAxis() != 9) {
                    return false;
                }
                namespacePrefix = navigator.getAttributeName(obj);
                attributeNamespaceUri = navigator.getAttributeNamespaceUri(obj);
            } else {
                if (navigator.isDocument(obj) || !navigator.isNamespace(obj) || getAxis() != 10) {
                    return false;
                }
                namespacePrefix = navigator.getNamespacePrefix(obj);
            }
        }
        if (this.hasPrefix) {
            strTranslateNamespacePrefixToUri = contextSupport.translateNamespacePrefixToUri(this.prefix);
            if (strTranslateNamespacePrefixToUri == null) {
                throw new UnresolvableException(new StringBuffer().append("Cannot resolve namespace prefix '").append(this.prefix).append(OperatorName.SHOW_TEXT_LINE).toString());
            }
        } else if (this.matchesAnyName) {
            return true;
        }
        if (hasNamespace(strTranslateNamespacePrefixToUri) != hasNamespace(attributeNamespaceUri)) {
            return false;
        }
        if (this.matchesAnyName || namespacePrefix.equals(getLocalName())) {
            return matchesNamespaceURIs(strTranslateNamespacePrefixToUri, attributeNamespaceUri);
        }
        return false;
    }

    private boolean hasNamespace(String str) {
        return str != null && str.length() > 0;
    }

    protected boolean matchesNamespaceURIs(String str, String str2) {
        if (str == str2) {
            return true;
        }
        if (str == null) {
            return str2.length() == 0;
        }
        if (str2 == null) {
            return str.length() == 0;
        }
        return str.equals(str2);
    }

    @Override // nu.xom.jaxen.expr.DefaultStep
    public String toString() {
        return new StringBuffer().append("[(DefaultNameStep): ").append("".equals(getPrefix()) ? getLocalName() : new StringBuffer().append(getPrefix()).append(":").append(getLocalName()).toString()).append("]").toString();
    }
}
