package com.ctc.wstx.sw;

import com.ctc.wstx.api.WriterConfig;
import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.sr.AttributeCollector;
import com.ctc.wstx.sr.InputElementStack;
import com.ctc.wstx.util.EmptyNamespaceContext;
import com.ctc.wstx.util.StringVector;
import java.io.IOException;
import java.util.Iterator;
import java.util.TreeSet;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;
import org.codehaus.stax2.ri.typed.AsciiValueEncoder;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/sw/NonNsStreamWriter.class */
public class NonNsStreamWriter extends TypedStreamWriter {
    final StringVector mElements;
    TreeSet<String> mAttrNames;

    public NonNsStreamWriter(XmlWriter xw, String enc, WriterConfig cfg) {
        super(xw, enc, cfg);
        this.mElements = new StringVector(32);
    }

    @Override // com.ctc.wstx.sw.BaseStreamWriter
    public NamespaceContext getNamespaceContext() {
        return EmptyNamespaceContext.getInstance();
    }

    @Override // com.ctc.wstx.sw.BaseStreamWriter
    public String getPrefix(String uri) {
        return null;
    }

    @Override // com.ctc.wstx.sw.BaseStreamWriter
    public void setDefaultNamespace(String uri) throws XMLStreamException {
        reportIllegalArg("Can not set default namespace for non-namespace writer.");
    }

    @Override // com.ctc.wstx.sw.BaseStreamWriter
    public void setNamespaceContext(NamespaceContext context) {
        reportIllegalArg("Can not set NamespaceContext for non-namespace writer.");
    }

    @Override // com.ctc.wstx.sw.BaseStreamWriter
    public void setPrefix(String prefix, String uri) throws XMLStreamException {
        reportIllegalArg("Can not set namespace prefix for non-namespace writer.");
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.BaseStreamWriter
    public void writeAttribute(String localName, String value) throws XMLStreamException {
        if (!this.mStartElementOpen && this.mCheckStructure) {
            reportNwfStructure(ErrorConsts.WERR_ATTR_NO_ELEM);
        }
        if (this.mCheckAttrs) {
            if (this.mAttrNames == null) {
                this.mAttrNames = new TreeSet<>();
            }
            if (!this.mAttrNames.add(localName)) {
                reportNwfAttr("Trying to write attribute '" + localName + "' twice");
            }
        }
        if (this.mValidator != null) {
            this.mValidator.validateAttribute(localName, "", "", value);
        }
        try {
            this.mWriter.writeAttribute(localName, value);
        } catch (IOException ioe) {
            throwFromIOE(ioe);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.BaseStreamWriter
    public void writeAttribute(String nsURI, String localName, String value) throws XMLStreamException {
        writeAttribute(localName, value);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.BaseStreamWriter
    public void writeAttribute(String prefix, String nsURI, String localName, String value) throws XMLStreamException {
        writeAttribute(localName, value);
    }

    @Override // com.ctc.wstx.sw.BaseStreamWriter
    public void writeDefaultNamespace(String nsURI) throws XMLStreamException {
        reportIllegalMethod("Can not call writeDefaultNamespace namespaces with non-namespace writer.");
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.BaseStreamWriter
    public void writeEmptyElement(String localName) throws XMLStreamException {
        doWriteStartElement(localName);
        this.mEmptyElement = true;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.BaseStreamWriter
    public void writeEmptyElement(String nsURI, String localName) throws XMLStreamException {
        writeEmptyElement(localName);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.BaseStreamWriter
    public void writeEmptyElement(String prefix, String localName, String nsURI) throws XMLStreamException {
        writeEmptyElement(localName);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.BaseStreamWriter
    public void writeEndElement() throws XMLStreamException {
        doWriteEndTag(null, this.mCfgAutomaticEmptyElems);
    }

    @Override // com.ctc.wstx.sw.BaseStreamWriter
    public void writeNamespace(String prefix, String nsURI) throws XMLStreamException {
        reportIllegalMethod("Can not set write namespaces with non-namespace writer.");
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.BaseStreamWriter
    public void writeStartElement(String localName) throws XMLStreamException {
        doWriteStartElement(localName);
        this.mEmptyElement = false;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.BaseStreamWriter
    public void writeStartElement(String nsURI, String localName) throws XMLStreamException {
        writeStartElement(localName);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.BaseStreamWriter
    public void writeStartElement(String prefix, String localName, String nsURI) throws XMLStreamException {
        writeStartElement(localName);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.BaseStreamWriter, org.codehaus.stax2.ri.Stax2WriterImpl, org.codehaus.stax2.XMLStreamWriter2
    public void writeFullEndElement() throws XMLStreamException {
        doWriteEndTag(null, false);
    }

    @Override // com.ctc.wstx.sw.BaseStreamWriter, org.codehaus.stax2.validation.ValidationContext
    public QName getCurrentElementName() {
        if (this.mElements.isEmpty()) {
            return null;
        }
        return new QName(this.mElements.getLastString());
    }

    @Override // com.ctc.wstx.sw.BaseStreamWriter, org.codehaus.stax2.validation.ValidationContext
    public String getNamespaceURI(String prefix) {
        return null;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.BaseStreamWriter
    public void writeStartElement(StartElement elem) throws XMLStreamException {
        QName name = elem.getName();
        writeStartElement(name.getLocalPart());
        Iterator<Attribute> it = elem.getAttributes();
        while (it.hasNext()) {
            Attribute attr = it.next();
            QName name2 = attr.getName();
            writeAttribute(name2.getLocalPart(), attr.getValue());
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.BaseStreamWriter
    public void writeEndElement(QName name) throws XMLStreamException {
        doWriteEndTag(this.mCheckStructure ? name.getLocalPart() : null, this.mCfgAutomaticEmptyElems);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.TypedStreamWriter
    protected void writeTypedAttribute(String prefix, String nsURI, String localName, AsciiValueEncoder enc) throws XMLStreamException {
        if (!this.mStartElementOpen && this.mCheckStructure) {
            reportNwfStructure(ErrorConsts.WERR_ATTR_NO_ELEM);
        }
        if (this.mCheckAttrs) {
            if (this.mAttrNames == null) {
                this.mAttrNames = new TreeSet<>();
            }
            if (!this.mAttrNames.add(localName)) {
                reportNwfAttr("Trying to write attribute '" + localName + "' twice");
            }
        }
        try {
            if (this.mValidator == null) {
                this.mWriter.writeTypedAttribute(localName, enc);
            } else {
                this.mWriter.writeTypedAttribute(null, localName, null, enc, this.mValidator, getCopyBuffer());
            }
        } catch (IOException ioe) {
            throwFromIOE(ioe);
        }
    }

    @Override // com.ctc.wstx.sw.BaseStreamWriter
    protected void closeStartElement(boolean emptyElem) throws XMLStreamException {
        this.mStartElementOpen = false;
        if (this.mAttrNames != null) {
            this.mAttrNames.clear();
        }
        try {
            if (emptyElem) {
                this.mWriter.writeStartTagEmptyEnd();
            } else {
                this.mWriter.writeStartTagEnd();
            }
        } catch (IOException ioe) {
            throwFromIOE(ioe);
        }
        if (this.mValidator != null) {
            this.mVldContent = this.mValidator.validateElementAndAttributes();
        }
        if (emptyElem) {
            String localName = this.mElements.removeLast();
            if (this.mElements.isEmpty()) {
                this.mState = 3;
            }
            if (this.mValidator != null) {
                this.mVldContent = this.mValidator.validateElementEnd(localName, "", "");
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.BaseStreamWriter
    public void copyStartElement(InputElementStack elemStack, AttributeCollector attrCollector) throws XMLStreamException, IOException {
        int specifiedCount;
        int nsCount;
        String prefix;
        String prefix2;
        String ln = elemStack.getLocalName();
        boolean nsAware = elemStack.isNamespaceAware();
        if (nsAware && (prefix2 = elemStack.getPrefix()) != null && prefix2.length() > 0) {
            ln = prefix2 + ":" + ln;
        }
        writeStartElement(ln);
        if (nsAware && (nsCount = elemStack.getCurrentNsCount()) > 0) {
            for (int i = 0; i < nsCount; i++) {
                String prefix3 = elemStack.getLocalNsPrefix(i);
                if (prefix3 == null || prefix3.length() == 0) {
                    prefix = CanonicalizerBase.XML;
                } else {
                    prefix = "xmlns:" + prefix3;
                }
                writeAttribute(prefix, elemStack.getLocalNsURI(i));
            }
        }
        if (this.mCfgCopyDefaultAttrs) {
            specifiedCount = attrCollector.getCount();
        } else {
            specifiedCount = attrCollector.getSpecifiedCount();
        }
        int attrCount = specifiedCount;
        if (attrCount > 0) {
            for (int i2 = 0; i2 < attrCount; i2++) {
                attrCollector.writeAttribute(i2, this.mWriter, this.mValidator);
            }
        }
    }

    @Override // com.ctc.wstx.sw.BaseStreamWriter
    protected String getTopElementDesc() {
        return this.mElements.isEmpty() ? "#root" : this.mElements.getLastString();
    }

    @Override // com.ctc.wstx.sw.BaseStreamWriter
    public String validateQNamePrefix(QName name) {
        return name.getPrefix();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private void doWriteStartElement(String localName) throws XMLStreamException {
        this.mAnyOutput = true;
        if (this.mStartElementOpen) {
            closeStartElement(this.mEmptyElement);
        } else if (this.mState == 1) {
            verifyRootElement(localName, null);
        } else if (this.mState == 3) {
            if (this.mCheckStructure) {
                reportNwfStructure(ErrorConsts.WERR_PROLOG_SECOND_ROOT, localName);
            }
            this.mState = 2;
        }
        if (this.mValidator != null) {
            this.mValidator.validateElementStart(localName, "", "");
        }
        this.mStartElementOpen = true;
        this.mElements.addString(localName);
        try {
            this.mWriter.writeStartTagStart(localName);
        } catch (IOException ioe) {
            throwFromIOE(ioe);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private void doWriteEndTag(String expName, boolean allowEmpty) throws XMLStreamException {
        if (this.mStartElementOpen && this.mEmptyElement) {
            this.mEmptyElement = false;
            closeStartElement(true);
        }
        if (this.mState != 2) {
            reportNwfStructure("No open start element, when trying to write end element");
        }
        String localName = this.mElements.removeLast();
        if (this.mCheckStructure && expName != null && !localName.equals(expName)) {
            reportNwfStructure("Mismatching close element name, '" + localName + "'; expected '" + expName + "'.");
        }
        if (this.mStartElementOpen) {
            if (this.mValidator != null) {
                this.mVldContent = this.mValidator.validateElementAndAttributes();
            }
            this.mStartElementOpen = false;
            if (this.mAttrNames != null) {
                this.mAttrNames.clear();
            }
            try {
                if (allowEmpty) {
                    this.mWriter.writeStartTagEmptyEnd();
                    if (this.mElements.isEmpty()) {
                        this.mState = 3;
                    }
                    if (this.mValidator != null) {
                        this.mVldContent = this.mValidator.validateElementEnd(localName, "", "");
                        return;
                    }
                    return;
                }
                this.mWriter.writeStartTagEnd();
            } catch (IOException ioe) {
                throwFromIOE(ioe);
            }
        }
        try {
            this.mWriter.writeEndTag(localName);
        } catch (IOException ioe2) {
            throwFromIOE(ioe2);
        }
        if (this.mElements.isEmpty()) {
            this.mState = 3;
        }
        if (this.mValidator != null) {
            this.mVldContent = this.mValidator.validateElementEnd(localName, "", "");
        }
    }
}
