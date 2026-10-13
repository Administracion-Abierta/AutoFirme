package org.codehaus.stax2.ri;

import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EndDocument;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.EntityDeclaration;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.StartElement;
import org.codehaus.stax2.evt.DTD2;
import org.codehaus.stax2.evt.XMLEventFactory2;
import org.codehaus.stax2.ri.evt.AttributeEventImpl;
import org.codehaus.stax2.ri.evt.CharactersEventImpl;
import org.codehaus.stax2.ri.evt.CommentEventImpl;
import org.codehaus.stax2.ri.evt.DTDEventImpl;
import org.codehaus.stax2.ri.evt.EndDocumentEventImpl;
import org.codehaus.stax2.ri.evt.EndElementEventImpl;
import org.codehaus.stax2.ri.evt.EntityReferenceEventImpl;
import org.codehaus.stax2.ri.evt.NamespaceEventImpl;
import org.codehaus.stax2.ri.evt.ProcInstrEventImpl;
import org.codehaus.stax2.ri.evt.StartDocumentEventImpl;
import org.codehaus.stax2.ri.evt.StartElementEventImpl;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/codehaus/stax2/ri/Stax2EventFactoryImpl.class */
public abstract class Stax2EventFactoryImpl extends XMLEventFactory2 {
    protected Location mLocation;

    protected abstract QName createQName(String str, String str2);

    protected abstract QName createQName(String str, String str2, String str3);

    public Attribute createAttribute(QName name, String value) {
        return new AttributeEventImpl(this.mLocation, name, value, true);
    }

    public Attribute createAttribute(String localName, String value) {
        return new AttributeEventImpl(this.mLocation, localName, null, null, value, true);
    }

    public Attribute createAttribute(String prefix, String nsURI, String localName, String value) {
        return new AttributeEventImpl(this.mLocation, localName, nsURI, prefix, value, true);
    }

    public Characters createCData(String content) {
        return new CharactersEventImpl(this.mLocation, content, true);
    }

    public Characters createCharacters(String content) {
        return new CharactersEventImpl(this.mLocation, content, false);
    }

    public Comment createComment(String text) {
        return new CommentEventImpl(this.mLocation, text);
    }

    public DTD createDTD(String dtd) {
        return new DTDEventImpl(this.mLocation, dtd);
    }

    public EndDocument createEndDocument() {
        return new EndDocumentEventImpl(this.mLocation);
    }

    public EndElement createEndElement(QName name, Iterator namespaces) {
        return new EndElementEventImpl(this.mLocation, name, namespaces);
    }

    public EndElement createEndElement(String prefix, String nsURI, String localName) {
        return createEndElement(createQName(nsURI, localName, prefix), null);
    }

    public EndElement createEndElement(String prefix, String nsURI, String localName, Iterator ns) {
        return createEndElement(createQName(nsURI, localName, prefix), ns);
    }

    public EntityReference createEntityReference(String name, EntityDeclaration decl) {
        return new EntityReferenceEventImpl(this.mLocation, decl);
    }

    public Characters createIgnorableSpace(String content) {
        return CharactersEventImpl.createIgnorableWS(this.mLocation, content);
    }

    public Namespace createNamespace(String nsURI) {
        return NamespaceEventImpl.constructDefaultNamespace(this.mLocation, nsURI);
    }

    public Namespace createNamespace(String prefix, String nsURI) {
        return NamespaceEventImpl.constructNamespace(this.mLocation, prefix, nsURI);
    }

    public ProcessingInstruction createProcessingInstruction(String target, String data) {
        return new ProcInstrEventImpl(this.mLocation, target, data);
    }

    public Characters createSpace(String content) {
        return CharactersEventImpl.createNonIgnorableWS(this.mLocation, content);
    }

    public StartDocument createStartDocument() {
        return new StartDocumentEventImpl(this.mLocation);
    }

    public StartDocument createStartDocument(String encoding) {
        return new StartDocumentEventImpl(this.mLocation, encoding);
    }

    public StartDocument createStartDocument(String encoding, String version) {
        return new StartDocumentEventImpl(this.mLocation, encoding, version);
    }

    public StartDocument createStartDocument(String encoding, String version, boolean standalone) {
        return new StartDocumentEventImpl(this.mLocation, encoding, version, true, standalone);
    }

    public StartElement createStartElement(QName name, Iterator attr, Iterator ns) {
        return createStartElement(name, attr, ns, null);
    }

    public StartElement createStartElement(String prefix, String nsURI, String localName) {
        return createStartElement(createQName(nsURI, localName, prefix), null, null, null);
    }

    public StartElement createStartElement(String prefix, String nsURI, String localName, Iterator attr, Iterator ns) {
        return createStartElement(createQName(nsURI, localName, prefix), attr, ns, null);
    }

    public StartElement createStartElement(String prefix, String nsURI, String localName, Iterator attr, Iterator ns, NamespaceContext nsCtxt) {
        return createStartElement(createQName(nsURI, localName, prefix), attr, ns, nsCtxt);
    }

    public void setLocation(Location loc) {
        this.mLocation = loc;
    }

    @Override // org.codehaus.stax2.evt.XMLEventFactory2
    public DTD2 createDTD(String rootName, String sysId, String pubId, String intSubset) {
        return new DTDEventImpl(this.mLocation, rootName, sysId, pubId, intSubset, null);
    }

    @Override // org.codehaus.stax2.evt.XMLEventFactory2
    public DTD2 createDTD(String rootName, String sysId, String pubId, String intSubset, Object processedDTD) {
        return new DTDEventImpl(this.mLocation, rootName, sysId, pubId, intSubset, processedDTD);
    }

    protected StartElement createStartElement(QName name, Iterator<?> attr, Iterator<?> ns, NamespaceContext ctxt) {
        return StartElementEventImpl.construct(this.mLocation, name, attr, ns, ctxt);
    }
}
