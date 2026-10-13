package org.mozilla.javascript.xmlimpl;

import java.util.ArrayList;
import org.mozilla.javascript.Callable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.ScriptRuntime;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.xml.XMLObject;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/xmlimpl/XMLList.class */
class XMLList extends XMLObjectImpl implements Function {
    static final long serialVersionUID = -4543618751670781135L;
    private XmlNode.InternalList _annos;
    private XMLObjectImpl targetObject;
    private XmlNode.QName targetProperty;

    XMLList(XMLLibImpl lib, Scriptable scope, XMLObject prototype) {
        super(lib, scope, prototype);
        this.targetObject = null;
        this.targetProperty = null;
        this._annos = new XmlNode.InternalList();
    }

    XmlNode.InternalList getNodeList() {
        return this._annos;
    }

    void setTargets(XMLObjectImpl object, XmlNode.QName property) {
        this.targetObject = object;
        this.targetProperty = property;
    }

    private XML getXmlFromAnnotation(int index) {
        return getXML(this._annos, index);
    }

    @Override // org.mozilla.javascript.xmlimpl.XMLObjectImpl
    XML getXML() {
        if (length() == 1) {
            return getXmlFromAnnotation(0);
        }
        return null;
    }

    private void internalRemoveFromList(int index) {
        this._annos.remove(index);
    }

    void replace(int index, XML xml) {
        if (index < length()) {
            XmlNode.InternalList newAnnoList = new XmlNode.InternalList();
            newAnnoList.add(this._annos, 0, index);
            newAnnoList.add(xml);
            newAnnoList.add(this._annos, index + 1, length());
            this._annos = newAnnoList;
        }
    }

    private void insert(int index, XML xml) {
        if (index < length()) {
            XmlNode.InternalList newAnnoList = new XmlNode.InternalList();
            newAnnoList.add(this._annos, 0, index);
            newAnnoList.add(xml);
            newAnnoList.add(this._annos, index, length());
            this._annos = newAnnoList;
        }
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public String getClassName() {
        return "XMLList";
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public Object get(int index, Scriptable start) {
        if (index >= 0 && index < length()) {
            return getXmlFromAnnotation(index);
        }
        return Scriptable.NOT_FOUND;
    }

    @Override // org.mozilla.javascript.xmlimpl.XMLObjectImpl
    boolean hasXMLProperty(XMLName xmlName) {
        return getPropertyList(xmlName).length() > 0;
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public boolean has(int index, Scriptable start) {
        return 0 <= index && index < length();
    }

    @Override // org.mozilla.javascript.xmlimpl.XMLObjectImpl
    void putXMLProperty(XMLName xmlName, Object value) {
        if (value == null) {
            value = "null";
        } else if (value instanceof Undefined) {
            value = "undefined";
        }
        if (length() > 1) {
            throw ScriptRuntime.typeError("Assignment to lists with more than one item is not supported");
        }
        if (length() == 0) {
            if (this.targetObject != null && this.targetProperty != null && this.targetProperty.getLocalName() != null && this.targetProperty.getLocalName().length() > 0) {
                XML xmlValue = newTextElementXML(null, this.targetProperty, null);
                addToList(xmlValue);
                if (xmlName.isAttributeName()) {
                    setAttribute(xmlName, value);
                } else {
                    XML xml = item(0);
                    xml.putXMLProperty(xmlName, value);
                    replace(0, item(0));
                }
                XMLName name2 = XMLName.formProperty(this.targetProperty.getNamespace().getUri(), this.targetProperty.getLocalName());
                this.targetObject.putXMLProperty(name2, this);
                replace(0, this.targetObject.getXML().getLastXmlChild());
                return;
            }
            throw ScriptRuntime.typeError("Assignment to empty XMLList without targets not supported");
        }
        if (xmlName.isAttributeName()) {
            setAttribute(xmlName, value);
            return;
        }
        XML xml2 = item(0);
        xml2.putXMLProperty(xmlName, value);
        replace(0, item(0));
    }

    @Override // org.mozilla.javascript.xmlimpl.XMLObjectImpl
    Object getXMLProperty(XMLName name) {
        return getPropertyList(name);
    }

    private void replaceNode(XML xml, XML with) {
        xml.replaceWith(with);
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public void put(int index, Scriptable start, Object value) {
        XMLObject xmlValue;
        XMLObject xMLObjectCopy;
        Object parent;
        Object obj = Undefined.instance;
        if (value == null) {
            value = "null";
        } else if (value instanceof Undefined) {
            value = "undefined";
        }
        if (value instanceof XMLObject) {
            xmlValue = (XMLObject) value;
        } else if (this.targetProperty == null) {
            xmlValue = newXMLFromJs(value.toString());
        } else {
            xmlValue = item(index);
            if (xmlValue == null) {
                XML x = item(0);
                if (x == null) {
                    xMLObjectCopy = newTextElementXML(null, this.targetProperty, null);
                } else {
                    xMLObjectCopy = x.copy();
                }
                xmlValue = xMLObjectCopy;
            }
            ((XML) xmlValue).setChildren(value);
        }
        if (index < length()) {
            parent = item(index).parent();
        } else if (length() == 0) {
            parent = this.targetObject != null ? this.targetObject.getXML() : parent();
        } else {
            parent = parent();
        }
        if (parent instanceof XML) {
            XML xmlParent = (XML) parent;
            if (index < length()) {
                XML xmlNode = getXmlFromAnnotation(index);
                if (xmlValue instanceof XML) {
                    replaceNode(xmlNode, (XML) xmlValue);
                    replace(index, xmlNode);
                    return;
                }
                if (xmlValue instanceof XMLList) {
                    XMLList list = (XMLList) xmlValue;
                    if (list.length() > 0) {
                        int lastIndexAdded = xmlNode.childIndex();
                        replaceNode(xmlNode, list.item(0));
                        replace(index, list.item(0));
                        for (int i = 1; i < list.length(); i++) {
                            xmlParent.insertChildAfter(xmlParent.getXmlChild(lastIndexAdded), list.item(i));
                            lastIndexAdded++;
                            insert(index + i, list.item(i));
                        }
                        return;
                    }
                    return;
                }
                return;
            }
            xmlParent.appendChild(xmlValue);
            addToList(xmlParent.getLastXmlChild());
            return;
        }
        if (index < length()) {
            XML xmlNode2 = getXML(this._annos, index);
            if (xmlValue instanceof XML) {
                replaceNode(xmlNode2, (XML) xmlValue);
                replace(index, xmlNode2);
                return;
            }
            if (xmlValue instanceof XMLList) {
                XMLList list2 = (XMLList) xmlValue;
                if (list2.length() > 0) {
                    replaceNode(xmlNode2, list2.item(0));
                    replace(index, list2.item(0));
                    for (int i2 = 1; i2 < list2.length(); i2++) {
                        insert(index + i2, list2.item(i2));
                    }
                    return;
                }
                return;
            }
            return;
        }
        addToList(xmlValue);
    }

    private XML getXML(XmlNode.InternalList _annos, int index) {
        if (index >= 0 && index < length()) {
            return xmlFromNode(_annos.item(index));
        }
        return null;
    }

    @Override // org.mozilla.javascript.xmlimpl.XMLObjectImpl
    void deleteXMLProperty(XMLName name) {
        for (int i = 0; i < length(); i++) {
            XML xml = getXmlFromAnnotation(i);
            if (xml.isElement()) {
                xml.deleteXMLProperty(name);
            }
        }
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public void delete(int index) {
        if (index >= 0 && index < length()) {
            XML xml = getXmlFromAnnotation(index);
            xml.remove();
            internalRemoveFromList(index);
        }
    }

    @Override // org.mozilla.javascript.ScriptableObject, org.mozilla.javascript.Scriptable
    public Object[] getIds() {
        Object[] enumObjs;
        if (isPrototype()) {
            enumObjs = new Object[0];
        } else {
            enumObjs = new Object[length()];
            for (int i = 0; i < enumObjs.length; i++) {
                enumObjs[i] = Integer.valueOf(i);
            }
        }
        return enumObjs;
    }

    public Object[] getIdsForDebug() {
        return getIds();
    }

    void remove() {
        int nLen = length();
        for (int i = nLen - 1; i >= 0; i--) {
            XML xml = getXmlFromAnnotation(i);
            if (xml != null) {
                xml.remove();
                internalRemoveFromList(i);
            }
        }
    }

    XML item(int index) {
        return this._annos != null ? getXmlFromAnnotation(index) : createEmptyXML();
    }

    private void setAttribute(XMLName xmlName, Object value) {
        for (int i = 0; i < length(); i++) {
            XML xml = getXmlFromAnnotation(i);
            xml.setAttribute(xmlName, value);
        }
    }

    void addToList(Object toAdd) {
        this._annos.addToList(toAdd);
    }

    @Override // org.mozilla.javascript.xmlimpl.XMLObjectImpl
    XMLList child(int index) {
        XMLList result = newXMLList();
        for (int i = 0; i < length(); i++) {
            result.addToList(getXmlFromAnnotation(i).child(index));
        }
        return result;
    }

    @Override // org.mozilla.javascript.xmlimpl.XMLObjectImpl
    XMLList child(XMLName xmlName) {
        XMLList result = newXMLList();
        for (int i = 0; i < length(); i++) {
            result.addToList(getXmlFromAnnotation(i).child(xmlName));
        }
        return result;
    }

    @Override // org.mozilla.javascript.xmlimpl.XMLObjectImpl
    void addMatches(XMLList rv, XMLName name) {
        for (int i = 0; i < length(); i++) {
            getXmlFromAnnotation(i).addMatches(rv, name);
        }
    }

    @Override // org.mozilla.javascript.xmlimpl.XMLObjectImpl
    XMLList children() {
        ArrayList<XML> list = new ArrayList<>();
        for (int i = 0; i < length(); i++) {
            XML xml = getXmlFromAnnotation(i);
            if (xml != null) {
                XMLList childList = xml.children();
                int cChildren = childList.length();
                for (int j = 0; j < cChildren; j++) {
                    list.add(childList.item(j));
                }
            }
        }
        XMLList allChildren = newXMLList();
        int sz = list.size();
        for (int i2 = 0; i2 < sz; i2++) {
            allChildren.addToList(list.get(i2));
        }
        return allChildren;
    }

    @Override // org.mozilla.javascript.xmlimpl.XMLObjectImpl
    XMLList comments() {
        XMLList result = newXMLList();
        for (int i = 0; i < length(); i++) {
            XML xml = getXmlFromAnnotation(i);
            result.addToList(xml.comments());
        }
        return result;
    }

    @Override // org.mozilla.javascript.xmlimpl.XMLObjectImpl
    XMLList elements(XMLName name) {
        XMLList rv = newXMLList();
        for (int i = 0; i < length(); i++) {
            XML xml = getXmlFromAnnotation(i);
            rv.addToList(xml.elements(name));
        }
        return rv;
    }

    @Override // org.mozilla.javascript.xmlimpl.XMLObjectImpl
    boolean contains(Object xml) {
        boolean result = false;
        for (int i = 0; i < length(); i++) {
            XML member = getXmlFromAnnotation(i);
            if (member.equivalentXml(xml)) {
                result = true;
                break;
            }
        }
        return result;
    }

    @Override // org.mozilla.javascript.xmlimpl.XMLObjectImpl
    XMLObjectImpl copy() {
        XMLList result = newXMLList();
        for (int i = 0; i < length(); i++) {
            XML xml = getXmlFromAnnotation(i);
            result.addToList(xml.copy());
        }
        return result;
    }

    @Override // org.mozilla.javascript.xmlimpl.XMLObjectImpl
    boolean hasOwnProperty(XMLName xmlName) {
        if (!isPrototype()) {
            return getPropertyList(xmlName).length() > 0;
        }
        String property = xmlName.localName();
        return findPrototypeId(property) != 0;
    }

    @Override // org.mozilla.javascript.xmlimpl.XMLObjectImpl
    boolean hasComplexContent() {
        boolean complexContent;
        int length = length();
        if (length == 0) {
            complexContent = false;
        } else if (length == 1) {
            complexContent = getXmlFromAnnotation(0).hasComplexContent();
        } else {
            complexContent = false;
            for (int i = 0; i < length; i++) {
                XML nextElement = getXmlFromAnnotation(i);
                if (nextElement.isElement()) {
                    complexContent = true;
                    break;
                }
            }
        }
        return complexContent;
    }

    @Override // org.mozilla.javascript.xmlimpl.XMLObjectImpl
    boolean hasSimpleContent() {
        if (length() == 0) {
            return true;
        }
        if (length() == 1) {
            return getXmlFromAnnotation(0).hasSimpleContent();
        }
        for (int i = 0; i < length(); i++) {
            XML nextElement = getXmlFromAnnotation(i);
            if (nextElement.isElement()) {
                return false;
            }
        }
        return true;
    }

    @Override // org.mozilla.javascript.xmlimpl.XMLObjectImpl
    int length() {
        int result = 0;
        if (this._annos != null) {
            result = this._annos.length();
        }
        return result;
    }

    @Override // org.mozilla.javascript.xmlimpl.XMLObjectImpl
    void normalize() {
        for (int i = 0; i < length(); i++) {
            getXmlFromAnnotation(i).normalize();
        }
    }

    @Override // org.mozilla.javascript.xmlimpl.XMLObjectImpl
    Object parent() {
        if (length() == 0) {
            return Undefined.instance;
        }
        XML candidateParent = null;
        for (int i = 0; i < length(); i++) {
            Object currParent = getXmlFromAnnotation(i).parent();
            if (!(currParent instanceof XML)) {
                return Undefined.instance;
            }
            XML xml = (XML) currParent;
            if (i == 0) {
                candidateParent = xml;
            } else if (!candidateParent.is(xml)) {
                return Undefined.instance;
            }
        }
        return candidateParent;
    }

    @Override // org.mozilla.javascript.xmlimpl.XMLObjectImpl
    XMLList processingInstructions(XMLName xmlName) {
        XMLList result = newXMLList();
        for (int i = 0; i < length(); i++) {
            XML xml = getXmlFromAnnotation(i);
            result.addToList(xml.processingInstructions(xmlName));
        }
        return result;
    }

    @Override // org.mozilla.javascript.xmlimpl.XMLObjectImpl
    boolean propertyIsEnumerable(Object name) {
        long index;
        if (name instanceof Integer) {
            index = ((Integer) name).intValue();
        } else if (name instanceof Number) {
            double x = ((Number) name).doubleValue();
            index = (long) x;
            if (index != x) {
                return false;
            }
            if (index == 0 && 1.0d / x < 0.0d) {
                return false;
            }
        } else {
            String s = ScriptRuntime.toString(name);
            index = ScriptRuntime.testUint32String(s);
        }
        return 0 <= index && index < ((long) length());
    }

    @Override // org.mozilla.javascript.xmlimpl.XMLObjectImpl
    XMLList text() {
        XMLList result = newXMLList();
        for (int i = 0; i < length(); i++) {
            result.addToList(getXmlFromAnnotation(i).text());
        }
        return result;
    }

    @Override // org.mozilla.javascript.xmlimpl.XMLObjectImpl
    public String toString() {
        if (hasSimpleContent()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < length(); i++) {
                XML next = getXmlFromAnnotation(i);
                if (!next.isComment() && !next.isProcessingInstruction()) {
                    sb.append(next.toString());
                }
            }
            return sb.toString();
        }
        return toXMLString();
    }

    @Override // org.mozilla.javascript.xmlimpl.XMLObjectImpl
    String toSource(int indent) {
        return toXMLString();
    }

    @Override // org.mozilla.javascript.xmlimpl.XMLObjectImpl
    String toXMLString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length(); i++) {
            if (getProcessor().isPrettyPrinting() && i != 0) {
                sb.append('\n');
            }
            sb.append(getXmlFromAnnotation(i).toXMLString());
        }
        return sb.toString();
    }

    @Override // org.mozilla.javascript.xmlimpl.XMLObjectImpl
    Object valueOf() {
        return this;
    }

    @Override // org.mozilla.javascript.xmlimpl.XMLObjectImpl
    boolean equivalentXml(Object target) {
        boolean result = false;
        if ((target instanceof Undefined) && length() == 0) {
            result = true;
        } else if (length() == 1) {
            result = getXmlFromAnnotation(0).equivalentXml(target);
        } else if (target instanceof XMLList) {
            XMLList otherList = (XMLList) target;
            if (otherList.length() == length()) {
                result = true;
                for (int i = 0; i < length(); i++) {
                    if (!getXmlFromAnnotation(i).equivalentXml(otherList.getXmlFromAnnotation(i))) {
                        result = false;
                        break;
                    }
                }
            }
        }
        return result;
    }

    private XMLList getPropertyList(XMLName name) {
        XMLList propertyList = newXMLList();
        XmlNode.QName qname = null;
        if (!name.isDescendants() && !name.isAttributeName()) {
            qname = name.toQname();
        }
        propertyList.setTargets(this, qname);
        for (int i = 0; i < length(); i++) {
            propertyList.addToList(getXmlFromAnnotation(i).getPropertyList(name));
        }
        return propertyList;
    }

    private Object applyOrCall(boolean isApply, Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        String methodName = isApply ? "apply" : "call";
        if (!(thisObj instanceof XMLList) || ((XMLList) thisObj).targetProperty == null) {
            throw ScriptRuntime.typeError1("msg.isnt.function", methodName);
        }
        return ScriptRuntime.applyOrCall(isApply, cx, scope, thisObj, args);
    }

    @Override // org.mozilla.javascript.xmlimpl.XMLObjectImpl
    protected Object jsConstructor(Context cx, boolean inNewExpr, Object[] args) {
        if (args.length == 0) {
            return newXMLList();
        }
        Object arg0 = args[0];
        if (!inNewExpr && (arg0 instanceof XMLList)) {
            return arg0;
        }
        return newXMLListFrom(arg0);
    }

    @Override // org.mozilla.javascript.xml.XMLObject
    public Scriptable getExtraMethodSource(Context cx) {
        if (length() == 1) {
            return getXmlFromAnnotation(0);
        }
        return null;
    }

    @Override // org.mozilla.javascript.Function, org.mozilla.javascript.Callable
    public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
        if (this.targetProperty == null) {
            throw ScriptRuntime.notFunctionError(this);
        }
        String methodName = this.targetProperty.getLocalName();
        boolean isApply = methodName.equals("apply");
        if (isApply || methodName.equals("call")) {
            return applyOrCall(isApply, cx, scope, thisObj, args);
        }
        if (!(thisObj instanceof XMLObject)) {
            throw ScriptRuntime.typeError1("msg.incompat.call", methodName);
        }
        Object func = null;
        Scriptable sobj = thisObj;
        while (sobj instanceof XMLObject) {
            XMLObject xmlObject = (XMLObject) sobj;
            func = xmlObject.getFunctionProperty(cx, methodName);
            if (func != Scriptable.NOT_FOUND) {
                break;
            }
            sobj = xmlObject.getExtraMethodSource(cx);
            if (sobj != null) {
                thisObj = sobj;
                if (!(sobj instanceof XMLObject)) {
                    func = ScriptableObject.getProperty(sobj, methodName);
                }
            }
        }
        if (!(func instanceof Callable)) {
            throw ScriptRuntime.notFunctionError(thisObj, func, methodName);
        }
        return ((Callable) func).call(cx, scope, thisObj, args);
    }

    @Override // org.mozilla.javascript.Function
    public Scriptable construct(Context cx, Scriptable scope, Object[] args) {
        throw ScriptRuntime.typeError1("msg.not.ctor", "XMLList");
    }
}
