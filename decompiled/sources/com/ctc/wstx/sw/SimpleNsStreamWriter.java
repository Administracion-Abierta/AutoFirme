package com.ctc.wstx.sw;

import com.ctc.wstx.api.WriterConfig;
import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.sr.AttributeCollector;
import com.ctc.wstx.sr.InputElementStack;
import java.io.IOException;
import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.StartElement;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/sw/SimpleNsStreamWriter.class */
public class SimpleNsStreamWriter extends BaseNsStreamWriter {
    public SimpleNsStreamWriter(XmlWriter xw, String enc, WriterConfig cfg) {
        super(xw, enc, cfg, false);
    }

    @Override // com.ctc.wstx.sw.BaseNsStreamWriter, com.ctc.wstx.sw.BaseStreamWriter
    public void writeAttribute(String nsURI, String localName, String value) throws XMLStreamException {
        if (!this.mStartElementOpen) {
            throwOutputError(ErrorConsts.WERR_ATTR_NO_ELEM);
        }
        String prefix = this.mCurrElem.getExplicitPrefix(nsURI);
        if (!this.mReturnNullForDefaultNamespace && prefix == null) {
            throwOutputError("Unbound namespace URI '" + nsURI + OperatorName.SHOW_TEXT_LINE);
        }
        doWriteAttr(localName, nsURI, prefix, value);
    }

    @Override // com.ctc.wstx.sw.BaseNsStreamWriter, com.ctc.wstx.sw.BaseStreamWriter
    public void writeAttribute(String prefix, String nsURI, String localName, String value) throws XMLStreamException {
        if (!this.mStartElementOpen) {
            throwOutputError(ErrorConsts.WERR_ATTR_NO_ELEM);
        }
        doWriteAttr(localName, nsURI, prefix, value);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.BaseNsStreamWriter, com.ctc.wstx.sw.BaseStreamWriter
    public void writeDefaultNamespace(String nsURI) throws XMLStreamException {
        if (!this.mStartElementOpen) {
            throwOutputError("Trying to write a namespace declaration when there is no open start element.");
        }
        setDefaultNamespace(nsURI);
        doWriteDefaultNs(nsURI);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.BaseNsStreamWriter, com.ctc.wstx.sw.BaseStreamWriter
    public void writeNamespace(String prefix, String nsURI) throws XMLStreamException {
        if (prefix == null || prefix.length() == 0 || prefix.equals(CanonicalizerBase.XMLNS)) {
            writeDefaultNamespace(nsURI);
            return;
        }
        if (!this.mStartElementOpen) {
            throwOutputError("Trying to write a namespace declaration when there is no open start element.");
        }
        if (!this.mXml11 && nsURI.length() == 0) {
            throwOutputError(ErrorConsts.ERR_NS_EMPTY);
        }
        setPrefix(prefix, nsURI);
        doWriteNamespace(prefix, nsURI);
    }

    @Override // com.ctc.wstx.sw.BaseNsStreamWriter, com.ctc.wstx.sw.BaseStreamWriter
    public void setDefaultNamespace(String uri) throws XMLStreamException {
        this.mCurrElem.setDefaultNsUri(uri);
    }

    @Override // com.ctc.wstx.sw.BaseNsStreamWriter
    public void doSetPrefix(String prefix, String uri) throws XMLStreamException {
        this.mCurrElem.addPrefix(prefix, uri);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.BaseNsStreamWriter, com.ctc.wstx.sw.BaseStreamWriter
    public void writeStartElement(StartElement elem) throws XMLStreamException {
        QName name = elem.getName();
        Iterator<Namespace> it = elem.getNamespaces();
        while (it.hasNext()) {
            Namespace ns = it.next();
            String prefix = ns.getPrefix();
            if (prefix == null || prefix.length() == 0) {
                setDefaultNamespace(ns.getNamespaceURI());
            } else {
                setPrefix(prefix, ns.getNamespaceURI());
            }
        }
        String nsURI = name.getNamespaceURI();
        if (nsURI == null) {
            writeStartElement(name.getLocalPart());
        } else {
            writeStartElement(name.getPrefix(), name.getLocalPart(), nsURI);
        }
        Iterator<Namespace> it2 = elem.getNamespaces();
        while (it2.hasNext()) {
            Namespace ns2 = it2.next();
            String prefix2 = ns2.getPrefix();
            if (prefix2 == null || prefix2.length() == 0) {
                writeDefaultNamespace(ns2.getNamespaceURI());
            } else {
                writeNamespace(prefix2, ns2.getNamespaceURI());
            }
        }
        Iterator<Attribute> ait = elem.getAttributes();
        while (ait.hasNext()) {
            Attribute attr = ait.next();
            QName name2 = attr.getName();
            String nsURI2 = name2.getNamespaceURI();
            if (nsURI2 != null && nsURI2.length() > 0) {
                writeAttribute(name2.getPrefix(), nsURI2, name2.getLocalPart(), attr.getValue());
            } else {
                writeAttribute(name2.getLocalPart(), attr.getValue());
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.BaseNsStreamWriter
    protected void writeStartOrEmpty(String localName, String nsURI) throws XMLStreamException {
        String prefix = this.mCurrElem.getPrefix(nsURI);
        if (prefix == null) {
            throw new XMLStreamException("Unbound namespace URI '" + nsURI + OperatorName.SHOW_TEXT_LINE);
        }
        checkStartElement(localName, prefix);
        if (this.mValidator != null) {
            this.mValidator.validateElementStart(localName, nsURI, prefix);
        }
        if (this.mOutputElemPool != null) {
            SimpleOutputElement newCurr = this.mOutputElemPool;
            this.mOutputElemPool = newCurr.reuseAsChild(this.mCurrElem, prefix, localName, nsURI);
            this.mPoolSize--;
            this.mCurrElem = newCurr;
        } else {
            this.mCurrElem = this.mCurrElem.createChild(prefix, localName, nsURI);
        }
        doWriteStartTag(prefix, localName);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.BaseNsStreamWriter
    protected void writeStartOrEmpty(String prefix, String localName, String nsURI) throws XMLStreamException {
        checkStartElement(localName, prefix);
        if (this.mValidator != null) {
            this.mValidator.validateElementStart(localName, nsURI, prefix);
        }
        if (this.mOutputElemPool != null) {
            SimpleOutputElement newCurr = this.mOutputElemPool;
            this.mOutputElemPool = newCurr.reuseAsChild(this.mCurrElem, prefix, localName, nsURI);
            this.mPoolSize--;
            this.mCurrElem = newCurr;
        } else {
            this.mCurrElem = this.mCurrElem.createChild(prefix, localName, nsURI);
        }
        doWriteStartTag(prefix, localName);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.BaseStreamWriter
    public final void copyStartElement(InputElementStack elemStack, AttributeCollector attrCollector) throws XMLStreamException, IOException {
        int specifiedCount;
        int nsCount = elemStack.getCurrentNsCount();
        if (nsCount > 0) {
            for (int i = 0; i < nsCount; i++) {
                String prefix = elemStack.getLocalNsPrefix(i);
                String uri = elemStack.getLocalNsURI(i);
                if (prefix == null || prefix.length() == 0) {
                    setDefaultNamespace(uri);
                } else {
                    setPrefix(prefix, uri);
                }
            }
        }
        writeStartElement(elemStack.getPrefix(), elemStack.getLocalName(), elemStack.getNsURI());
        if (nsCount > 0) {
            for (int i2 = 0; i2 < nsCount; i2++) {
                String prefix2 = elemStack.getLocalNsPrefix(i2);
                String uri2 = elemStack.getLocalNsURI(i2);
                if (prefix2 == null || prefix2.length() == 0) {
                    writeDefaultNamespace(uri2);
                } else {
                    writeNamespace(prefix2, uri2);
                }
            }
        }
        if (this.mCfgCopyDefaultAttrs) {
            specifiedCount = attrCollector.getCount();
        } else {
            specifiedCount = attrCollector.getSpecifiedCount();
        }
        int attrCount = specifiedCount;
        if (attrCount > 0) {
            for (int i3 = 0; i3 < attrCount; i3++) {
                attrCollector.writeAttribute(i3, this.mWriter, this.mValidator);
            }
        }
    }

    @Override // com.ctc.wstx.sw.BaseStreamWriter
    public String validateQNamePrefix(QName name) {
        return name.getPrefix();
    }
}
