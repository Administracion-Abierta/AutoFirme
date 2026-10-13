package es.gob.afirma.standalone.configurator.common.xmlwise;

import java.util.Iterator;
import java.util.LinkedList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/standalone/configurator/common/xmlwise/XmlElement.class */
public class XmlElement extends LinkedList<XmlElement> {
    private final XmlElementAttributes m_attributes;
    private final String m_value;
    private final String m_name;

    public XmlElement(Element element) {
        this.m_attributes = new XmlElementAttributes(element);
        NodeList childNodes = element.getChildNodes();
        this.m_name = element.getNodeName();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node nodeItem = childNodes.item(i);
            if (nodeItem.getNodeType() == 1) {
                add(new XmlElement((Element) nodeItem));
            }
            if (nodeItem.getNodeType() == 3) {
                sb.append(nodeItem.getNodeValue());
            }
        }
        this.m_value = sb.toString();
    }

    public XmlElement(String str, String str2) {
        this.m_attributes = new XmlElementAttributes();
        this.m_name = str;
        this.m_value = str2;
    }

    public XmlElement(String str) {
        this.m_attributes = new XmlElementAttributes();
        this.m_name = str;
        this.m_value = "";
    }

    public XmlElement getUnique(String str) throws XmlParseException {
        LinkedList<XmlElement> linkedList = get(str);
        if (linkedList.size() != 1) {
            throw new XmlParseException("Unexpected number of elements of type " + str + " in element <" + getName() + ">");
        }
        return linkedList.getFirst();
    }

    public int getIntAttribute(String str) throws XmlParseException {
        return getAttributes().getInt(str);
    }

    public int getIntAttribute(String str, int i) throws XmlParseException {
        return containsAttribute(str) ? getIntAttribute(str) : i;
    }

    public double getDoubleAttribute(String str) throws XmlParseException {
        return getAttributes().getDouble(str);
    }

    public double getDoubleAttribute(String str, double d) throws XmlParseException {
        return containsAttribute(str) ? getDoubleAttribute(str) : d;
    }

    public String getAttribute(String str, String str2) {
        String attribute = getAttribute(str);
        return attribute == null ? str2 : attribute;
    }

    public String getAttribute(String str) {
        return getAttributes().get(str);
    }

    public boolean getBoolAttribute(String str) throws XmlParseException {
        return getAttributes().getBoolean(str);
    }

    public boolean getBoolAttribute(String str, boolean z) throws XmlParseException {
        return containsAttribute(str) ? getBoolAttribute(str) : z;
    }

    public LinkedList<XmlElement> get(String str) {
        LinkedList<XmlElement> linkedList = new LinkedList<>();
        Iterator it = iterator();
        while (it.hasNext()) {
            XmlElement xmlElement = (XmlElement) it.next();
            if (xmlElement.getName().equals(str)) {
                linkedList.add(xmlElement);
            }
        }
        return linkedList;
    }

    public void setAttribute(String str, Object obj) {
        this.m_attributes.put(str, obj.toString());
    }

    public boolean removeAttribute(String str) {
        return this.m_attributes.remove(str) != null;
    }

    public boolean contains(String str) {
        Iterator it = iterator();
        while (it.hasNext()) {
            if (((XmlElement) it.next()).getName().equals(str)) {
                return true;
            }
        }
        return false;
    }

    public String toXml() {
        StringBuilder sbAppend = new StringBuilder("<").append(this.m_name);
        if (this.m_attributes.size() > 0) {
            sbAppend.append(this.m_attributes.toXml());
        }
        if (isEmpty() && this.m_value.length() == 0) {
            sbAppend.append("/>");
        } else {
            sbAppend.append('>');
            sbAppend.append(Xmlwise.escapeXML(this.m_value));
            Iterator it = iterator();
            while (it.hasNext()) {
                sbAppend.append(((XmlElement) it.next()).toXml());
            }
            sbAppend.append("</").append(this.m_name).append('>');
        }
        return sbAppend.toString();
    }

    public String getValue() {
        return this.m_value;
    }

    public XmlElementAttributes getAttributes() {
        return this.m_attributes;
    }

    public String getName() {
        return this.m_name;
    }

    public boolean containsAttribute(String str) {
        return getAttributes().containsKey(str);
    }
}
