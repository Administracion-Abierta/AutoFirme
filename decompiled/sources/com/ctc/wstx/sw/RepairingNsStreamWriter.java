package com.ctc.wstx.sw;

import com.ctc.wstx.api.WriterConfig;
import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.sr.AttributeCollector;
import com.ctc.wstx.sr.InputElementStack;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import org.codehaus.stax2.ri.typed.AsciiValueEncoder;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/sw/RepairingNsStreamWriter.class */
public final class RepairingNsStreamWriter extends BaseNsStreamWriter {
    protected final String mAutomaticNsPrefix;
    protected int[] mAutoNsSeq;
    protected String mSuggestedDefNs;
    protected HashMap<String, String> mSuggestedPrefixes;

    public RepairingNsStreamWriter(XmlWriter xw, String enc, WriterConfig cfg) {
        super(xw, enc, cfg, true);
        this.mAutoNsSeq = null;
        this.mSuggestedDefNs = null;
        this.mSuggestedPrefixes = null;
        this.mAutomaticNsPrefix = cfg.getAutomaticNsPrefix();
    }

    @Override // com.ctc.wstx.sw.BaseNsStreamWriter, com.ctc.wstx.sw.BaseStreamWriter
    public void writeAttribute(String nsURI, String localName, String value) throws XMLStreamException {
        if (!this.mStartElementOpen) {
            throwOutputError(ErrorConsts.WERR_ATTR_NO_ELEM);
        }
        doWriteAttr(localName, nsURI, findOrCreateAttrPrefix(null, nsURI, this.mCurrElem), value);
    }

    @Override // com.ctc.wstx.sw.BaseNsStreamWriter, com.ctc.wstx.sw.BaseStreamWriter
    public void writeAttribute(String prefix, String nsURI, String localName, String value) throws XMLStreamException {
        if (!this.mStartElementOpen) {
            throwOutputError(ErrorConsts.WERR_ATTR_NO_ELEM);
        }
        doWriteAttr(localName, nsURI, findOrCreateAttrPrefix(prefix, nsURI, this.mCurrElem), value);
    }

    @Override // com.ctc.wstx.sw.BaseNsStreamWriter, com.ctc.wstx.sw.BaseStreamWriter
    public void writeDefaultNamespace(String nsURI) throws XMLStreamException {
        if (!this.mStartElementOpen) {
            throwOutputError("Trying to write a namespace declaration when there is no open start element.");
        }
        String prefix = this.mCurrElem.getPrefix();
        if (prefix != null && prefix.length() > 0) {
            this.mCurrElem.setDefaultNsUri(nsURI);
            doWriteDefaultNs(nsURI);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.BaseNsStreamWriter, com.ctc.wstx.sw.BaseStreamWriter
    public void writeNamespace(String prefix, String nsURI) throws XMLStreamException {
        if (prefix == null || prefix.length() == 0) {
            writeDefaultNamespace(nsURI);
            return;
        }
        if (!this.mStartElementOpen) {
            throwOutputError("Trying to write a namespace declaration when there is no open start element.");
        }
        int value = this.mCurrElem.isPrefixValid(prefix, nsURI, true);
        if (value == 0) {
            this.mCurrElem.addPrefix(prefix, nsURI);
            doWriteNamespace(prefix, nsURI);
        }
    }

    @Override // com.ctc.wstx.sw.BaseNsStreamWriter, com.ctc.wstx.sw.BaseStreamWriter
    public void setDefaultNamespace(String uri) throws XMLStreamException {
        this.mSuggestedDefNs = (uri == null || uri.length() == 0) ? null : uri;
    }

    @Override // com.ctc.wstx.sw.BaseNsStreamWriter
    public void doSetPrefix(String prefix, String uri) throws XMLStreamException {
        if (uri == null || uri.length() == 0) {
            if (this.mSuggestedPrefixes != null) {
                Iterator<Map.Entry<String, String>> it = this.mSuggestedPrefixes.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<String, String> en = it.next();
                    String thisP = en.getValue();
                    if (thisP.equals(prefix)) {
                        it.remove();
                    }
                }
                return;
            }
            return;
        }
        if (this.mSuggestedPrefixes == null) {
            this.mSuggestedPrefixes = new HashMap<>(16);
        }
        this.mSuggestedPrefixes.put(uri, prefix);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.BaseNsStreamWriter, com.ctc.wstx.sw.BaseStreamWriter
    public void writeStartElement(StartElement elem) throws XMLStreamException {
        QName name = elem.getName();
        writeStartElement(name.getPrefix(), name.getLocalPart(), name.getNamespaceURI());
        Iterator<Attribute> it = elem.getAttributes();
        while (it.hasNext()) {
            Attribute attr = it.next();
            QName name2 = attr.getName();
            writeAttribute(name2.getPrefix(), name2.getNamespaceURI(), name2.getLocalPart(), attr.getValue());
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.BaseNsStreamWriter, com.ctc.wstx.sw.TypedStreamWriter
    protected void writeTypedAttribute(String prefix, String nsURI, String localName, AsciiValueEncoder enc) throws XMLStreamException {
        super.writeTypedAttribute(findOrCreateAttrPrefix(prefix, nsURI, this.mCurrElem), nsURI, localName, enc);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.BaseNsStreamWriter
    protected void writeStartOrEmpty(String localName, String nsURI) throws XMLStreamException {
        checkStartElement(localName, "");
        String prefix = findElemPrefix(nsURI, this.mCurrElem);
        if (this.mOutputElemPool != null) {
            SimpleOutputElement newCurr = this.mOutputElemPool;
            this.mOutputElemPool = newCurr.reuseAsChild(this.mCurrElem, prefix, localName, nsURI);
            this.mPoolSize--;
            this.mCurrElem = newCurr;
        } else {
            this.mCurrElem = this.mCurrElem.createChild(prefix, localName, nsURI);
        }
        if (prefix != null) {
            if (this.mValidator != null) {
                this.mValidator.validateElementStart(localName, nsURI, prefix);
            }
            doWriteStartTag(prefix, localName);
            return;
        }
        String prefix2 = generateElemPrefix(null, nsURI, this.mCurrElem);
        if (this.mValidator != null) {
            this.mValidator.validateElementStart(localName, nsURI, prefix2);
        }
        this.mCurrElem.setPrefix(prefix2);
        doWriteStartTag(prefix2, localName);
        if (prefix2 == null || prefix2.length() == 0) {
            this.mCurrElem.setDefaultNsUri(nsURI);
            doWriteDefaultNs(nsURI);
        } else {
            this.mCurrElem.addPrefix(prefix2, nsURI);
            doWriteNamespace(prefix2, nsURI);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.BaseNsStreamWriter
    protected void writeStartOrEmpty(String suggPrefix, String localName, String nsURI) throws XMLStreamException {
        checkStartElement(localName, suggPrefix);
        String actPrefix = validateElemPrefix(suggPrefix, nsURI, this.mCurrElem);
        if (actPrefix != null) {
            if (this.mValidator != null) {
                this.mValidator.validateElementStart(localName, nsURI, actPrefix);
            }
            if (this.mOutputElemPool != null) {
                SimpleOutputElement newCurr = this.mOutputElemPool;
                this.mOutputElemPool = newCurr.reuseAsChild(this.mCurrElem, actPrefix, localName, nsURI);
                this.mPoolSize--;
                this.mCurrElem = newCurr;
            } else {
                this.mCurrElem = this.mCurrElem.createChild(actPrefix, localName, nsURI);
            }
            doWriteStartTag(actPrefix, localName);
            return;
        }
        if (suggPrefix == null) {
            suggPrefix = "";
        }
        String actPrefix2 = generateElemPrefix(suggPrefix, nsURI, this.mCurrElem);
        if (this.mValidator != null) {
            this.mValidator.validateElementStart(localName, nsURI, actPrefix2);
        }
        if (this.mOutputElemPool != null) {
            SimpleOutputElement newCurr2 = this.mOutputElemPool;
            this.mOutputElemPool = newCurr2.reuseAsChild(this.mCurrElem, actPrefix2, localName, nsURI);
            this.mPoolSize--;
            this.mCurrElem = newCurr2;
        } else {
            this.mCurrElem = this.mCurrElem.createChild(actPrefix2, localName, nsURI);
        }
        this.mCurrElem.setPrefix(actPrefix2);
        doWriteStartTag(actPrefix2, localName);
        if (actPrefix2 == null || actPrefix2.length() == 0) {
            this.mCurrElem.setDefaultNsUri(nsURI);
            doWriteDefaultNs(nsURI);
        } else {
            this.mCurrElem.addPrefix(actPrefix2, nsURI);
            doWriteNamespace(actPrefix2, nsURI);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.BaseStreamWriter
    public final void copyStartElement(InputElementStack elemStack, AttributeCollector ac) throws XMLStreamException, IOException {
        String prefix = elemStack.getPrefix();
        String uri = elemStack.getNsURI();
        writeStartElement(prefix, elemStack.getLocalName(), uri);
        int nsCount = elemStack.getCurrentNsCount();
        if (nsCount > 0) {
            for (int i = 0; i < nsCount; i++) {
                writeNamespace(elemStack.getLocalNsPrefix(i), elemStack.getLocalNsURI(i));
            }
        }
        int attrCount = this.mCfgCopyDefaultAttrs ? ac.getCount() : ac.getSpecifiedCount();
        if (attrCount > 0) {
            for (int i2 = 0; i2 < attrCount; i2++) {
                String uri2 = ac.getURI(i2);
                String prefix2 = ac.getPrefix(i2);
                if (prefix2 != null && prefix2.length() != 0) {
                    prefix2 = findOrCreateAttrPrefix(prefix2, uri2, this.mCurrElem);
                }
                if (prefix2 == null || prefix2.length() == 0) {
                    this.mWriter.writeAttribute(ac.getLocalName(i2), ac.getValue(i2));
                } else {
                    this.mWriter.writeAttribute(prefix2, ac.getLocalName(i2), ac.getValue(i2));
                }
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // com.ctc.wstx.sw.BaseStreamWriter
    public String validateQNamePrefix(QName name) throws XMLStreamException {
        String uri = name.getNamespaceURI();
        String suggPrefix = name.getPrefix();
        String actPrefix = validateElemPrefix(suggPrefix, uri, this.mCurrElem);
        if (actPrefix == null) {
            if (suggPrefix == null) {
                suggPrefix = "";
            }
            actPrefix = generateElemPrefix(suggPrefix, uri, this.mCurrElem);
            if (actPrefix == null || actPrefix.length() == 0) {
                writeDefaultNamespace(uri);
            } else {
                writeNamespace(actPrefix, uri);
            }
        }
        return actPrefix;
    }

    protected final String findElemPrefix(String nsURI, SimpleOutputElement elem) throws XMLStreamException {
        if (nsURI == null || nsURI.length() == 0) {
            String currDefNsURI = elem.getDefaultNsUri();
            if (currDefNsURI != null && currDefNsURI.length() > 0) {
                return null;
            }
            return "";
        }
        return this.mCurrElem.getPrefix(nsURI);
    }

    protected final String generateElemPrefix(String suggPrefix, String nsURI, SimpleOutputElement elem) throws XMLStreamException {
        if (nsURI == null || nsURI.length() == 0) {
            return "";
        }
        if (suggPrefix == null) {
            if (this.mSuggestedDefNs != null && this.mSuggestedDefNs.equals(nsURI)) {
                suggPrefix = "";
            } else {
                suggPrefix = this.mSuggestedPrefixes == null ? null : this.mSuggestedPrefixes.get(nsURI);
                if (suggPrefix == null) {
                    if (this.mAutoNsSeq == null) {
                        this.mAutoNsSeq = new int[1];
                        this.mAutoNsSeq[0] = 1;
                    }
                    suggPrefix = elem.generateMapping(this.mAutomaticNsPrefix, nsURI, this.mAutoNsSeq);
                }
            }
        }
        return suggPrefix;
    }

    protected final String findOrCreateAttrPrefix(String suggPrefix, String nsURI, SimpleOutputElement elem) throws XMLStreamException {
        if (nsURI == null || nsURI.length() == 0) {
            return null;
        }
        if (suggPrefix != null) {
            int status = elem.isPrefixValid(suggPrefix, nsURI, false);
            if (status == 1) {
                return suggPrefix;
            }
            if (status == 0) {
                elem.addPrefix(suggPrefix, nsURI);
                doWriteNamespace(suggPrefix, nsURI);
                return suggPrefix;
            }
        }
        String prefix = elem.getExplicitPrefix(nsURI);
        if (prefix != null) {
            return prefix;
        }
        if (suggPrefix != null) {
            prefix = suggPrefix;
        } else if (this.mSuggestedPrefixes != null) {
            prefix = this.mSuggestedPrefixes.get(nsURI);
        }
        if (prefix != null && (prefix.length() == 0 || elem.getNamespaceURI(prefix) != null)) {
            prefix = null;
        }
        if (prefix == null) {
            if (this.mAutoNsSeq == null) {
                this.mAutoNsSeq = new int[1];
                this.mAutoNsSeq[0] = 1;
            }
            prefix = this.mCurrElem.generateMapping(this.mAutomaticNsPrefix, nsURI, this.mAutoNsSeq);
        }
        elem.addPrefix(prefix, nsURI);
        doWriteNamespace(prefix, nsURI);
        return prefix;
    }

    private final String validateElemPrefix(String prefix, String nsURI, SimpleOutputElement elem) throws XMLStreamException {
        if (nsURI == null || nsURI.length() == 0) {
            String currURL = elem.getDefaultNsUri();
            if (currURL == null || currURL.length() == 0) {
                return "";
            }
            return null;
        }
        int status = elem.isPrefixValid(prefix, nsURI, true);
        if (status == 1) {
            return prefix;
        }
        return null;
    }
}
