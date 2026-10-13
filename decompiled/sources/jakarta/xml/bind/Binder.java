package jakarta.xml.bind;

import javax.xml.validation.Schema;

/* JADX INFO: loaded from: autofirma.jar:jakarta/xml/bind/Binder.class */
public abstract class Binder<XmlNode> {
    public abstract Object unmarshal(XmlNode xmlnode) throws JAXBException;

    public abstract <T> JAXBElement<T> unmarshal(XmlNode xmlnode, Class<T> cls) throws JAXBException;

    public abstract void marshal(Object obj, XmlNode xmlnode) throws JAXBException;

    public abstract XmlNode getXMLNode(Object obj);

    public abstract Object getJAXBNode(XmlNode xmlnode);

    public abstract XmlNode updateXML(Object obj) throws JAXBException;

    public abstract XmlNode updateXML(Object obj, XmlNode xmlnode) throws JAXBException;

    public abstract Object updateJAXB(XmlNode xmlnode) throws JAXBException;

    public abstract void setSchema(Schema schema);

    public abstract Schema getSchema();

    public abstract void setEventHandler(ValidationEventHandler validationEventHandler) throws JAXBException;

    public abstract ValidationEventHandler getEventHandler() throws JAXBException;

    public abstract void setProperty(String str, Object obj) throws PropertyException;

    public abstract Object getProperty(String str) throws PropertyException;
}
