package org.codehaus.stax2.ri.dom;

import com.ctc.wstx.cfg.XmlConsts;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.transform.dom.DOMSource;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;
import org.codehaus.stax2.AttributeInfo;
import org.codehaus.stax2.DTDInfo;
import org.codehaus.stax2.LocationInfo;
import org.codehaus.stax2.XMLStreamLocation2;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.ri.EmptyIterator;
import org.codehaus.stax2.ri.EmptyNamespaceContext;
import org.codehaus.stax2.ri.SingletonIterator;
import org.codehaus.stax2.ri.Stax2Util;
import org.codehaus.stax2.ri.typed.StringBase64Decoder;
import org.codehaus.stax2.ri.typed.ValueDecoderFactory;
import org.codehaus.stax2.typed.Base64Variant;
import org.codehaus.stax2.typed.Base64Variants;
import org.codehaus.stax2.typed.TypedArrayDecoder;
import org.codehaus.stax2.typed.TypedValueDecoder;
import org.codehaus.stax2.typed.TypedXMLStreamException;
import org.codehaus.stax2.validation.DTDValidationSchema;
import org.codehaus.stax2.validation.ValidationProblemHandler;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidator;
import org.w3c.dom.Attr;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/ri/dom/DOMWrappingReader.class */
public abstract class DOMWrappingReader implements XMLStreamReader2, AttributeInfo, DTDInfo, LocationInfo, NamespaceContext, XMLStreamConstants {
    protected static final int INT_SPACE = 32;
    private static final int MASK_GET_TEXT = 6768;
    private static final int MASK_GET_TEXT_XXX = 4208;
    private static final int MASK_GET_ELEMENT_TEXT = 4688;
    protected static final int MASK_TYPED_ACCESS_BINARY = 4178;
    protected static final int ERR_STATE_NOT_START_ELEM = 1;
    protected static final int ERR_STATE_NOT_ELEM = 2;
    protected static final int ERR_STATE_NOT_PI = 3;
    protected static final int ERR_STATE_NOT_TEXTUAL = 4;
    protected static final int ERR_STATE_NOT_TEXTUAL_XXX = 5;
    protected static final int ERR_STATE_NOT_TEXTUAL_OR_ELEM = 6;
    protected static final int ERR_STATE_NO_LOCALNAME = 7;
    protected final String _systemId;
    protected final Node _rootNode;
    protected final boolean _cfgNsAware;
    protected final boolean _coalescing;
    protected Node _currNode;
    protected String _coalescedText;
    protected ValueDecoderFactory _decoderFactory;
    protected boolean _cfgInternNames = false;
    protected boolean _cfgInternNsURIs = false;
    protected int _currEvent = 7;
    protected int _depth = 0;
    protected Stax2Util.TextBuffer _textBuffer = new Stax2Util.TextBuffer();
    protected List<Node> _attrList = null;
    protected List<String> _nsDeclList = null;
    protected StringBase64Decoder _base64Decoder = null;

    protected abstract void throwStreamException(String str, Location location) throws XMLStreamException;

    public abstract Object getProperty(String str);

    @Override // org.codehaus.stax2.XMLStreamReader2
    public abstract boolean isPropertySupported(String str);

    @Override // org.codehaus.stax2.XMLStreamReader2
    public abstract boolean setProperty(String str, Object obj);

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected DOMWrappingReader(DOMSource src, boolean nsAware, boolean coalescing) throws XMLStreamException {
        Node treeRoot = src.getNode();
        if (treeRoot == null) {
            throw new IllegalArgumentException("Can not pass null Node for constructing a DOM-based XMLStreamReader");
        }
        this._cfgNsAware = nsAware;
        this._coalescing = coalescing;
        this._systemId = src.getSystemId();
        switch (treeRoot.getNodeType()) {
            case 1:
            case 9:
            case 11:
                this._currNode = treeRoot;
                this._rootNode = treeRoot;
                return;
            default:
                throw new XMLStreamException("Can not create an XMLStreamReader for a DOM node of type " + treeRoot.getClass());
        }
    }

    protected void setInternNames(boolean state) {
        this._cfgInternNames = state;
    }

    protected void setInternNsURIs(boolean state) {
        this._cfgInternNsURIs = state;
    }

    public String getCharacterEncodingScheme() {
        return null;
    }

    public String getEncoding() {
        return getCharacterEncodingScheme();
    }

    public String getVersion() {
        return null;
    }

    public boolean isStandalone() {
        return false;
    }

    public boolean standaloneSet() {
        return false;
    }

    @Override // org.codehaus.stax2.AttributeInfo, org.codehaus.stax2.validation.ValidationContext
    public int getAttributeCount() {
        if (this._currEvent != 1) {
            reportWrongState(1);
        }
        if (this._attrList == null) {
            _calcNsAndAttrLists(true);
        }
        return this._attrList.size();
    }

    public String getAttributeLocalName(int index) {
        if (this._currEvent != 1) {
            reportWrongState(1);
        }
        if (this._attrList == null) {
            _calcNsAndAttrLists(true);
        }
        if (index >= this._attrList.size() || index < 0) {
            handleIllegalAttrIndex(index);
            return null;
        }
        Attr attr = (Attr) this._attrList.get(index);
        return _internName(_safeGetLocalName(attr));
    }

    public QName getAttributeName(int index) {
        if (this._currEvent != 1) {
            reportWrongState(1);
        }
        if (this._attrList == null) {
            _calcNsAndAttrLists(true);
        }
        if (index >= this._attrList.size() || index < 0) {
            handleIllegalAttrIndex(index);
            return null;
        }
        Attr attr = (Attr) this._attrList.get(index);
        return _constructQName(attr.getNamespaceURI(), _safeGetLocalName(attr), attr.getPrefix());
    }

    public String getAttributeNamespace(int index) {
        if (this._currEvent != 1) {
            reportWrongState(1);
        }
        if (this._attrList == null) {
            _calcNsAndAttrLists(true);
        }
        if (index >= this._attrList.size() || index < 0) {
            handleIllegalAttrIndex(index);
            return null;
        }
        Attr attr = (Attr) this._attrList.get(index);
        return _internNsURI(attr.getNamespaceURI());
    }

    public String getAttributePrefix(int index) {
        if (this._currEvent != 1) {
            reportWrongState(1);
        }
        if (this._attrList == null) {
            _calcNsAndAttrLists(true);
        }
        if (index >= this._attrList.size() || index < 0) {
            handleIllegalAttrIndex(index);
            return null;
        }
        Attr attr = (Attr) this._attrList.get(index);
        return _internName(attr.getPrefix());
    }

    public String getAttributeType(int index) {
        if (this._currEvent != 1) {
            reportWrongState(1);
        }
        if (this._attrList == null) {
            _calcNsAndAttrLists(true);
        }
        if (index >= this._attrList.size() || index < 0) {
            handleIllegalAttrIndex(index);
            return null;
        }
        return "CDATA";
    }

    public String getAttributeValue(int index) {
        if (this._currEvent != 1) {
            reportWrongState(1);
        }
        if (this._attrList == null) {
            _calcNsAndAttrLists(true);
        }
        if (index >= this._attrList.size() || index < 0) {
            handleIllegalAttrIndex(index);
            return null;
        }
        Attr attr = (Attr) this._attrList.get(index);
        return attr.getValue();
    }

    public String getAttributeValue(String nsURI, String localName) {
        if (this._currEvent != 1) {
            reportWrongState(1);
        }
        Element elem = (Element) this._currNode;
        NamedNodeMap attrs = elem.getAttributes();
        if (nsURI != null && nsURI.length() == 0) {
            nsURI = null;
        }
        Attr attr = (Attr) attrs.getNamedItemNS(nsURI, localName);
        if (attr == null) {
            return null;
        }
        return attr.getValue();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public String getElementText() throws XMLStreamException {
        if (this._currEvent != 1) {
            reportParseProblem(1);
        }
        if (this._coalescing) {
            String text = null;
            while (true) {
                int type = next();
                if (type == 2) {
                    break;
                }
                if (type != 5 && type != 3) {
                    if (((1 << type) & MASK_GET_ELEMENT_TEXT) == 0) {
                        reportParseProblem(4);
                    }
                    if (text == null) {
                        text = getText();
                    } else {
                        text = text + getText();
                    }
                }
            }
            return text == null ? "" : text;
        }
        this._textBuffer.reset();
        while (true) {
            int type2 = next();
            if (type2 != 2) {
                if (type2 != 5 && type2 != 3) {
                    if (((1 << type2) & MASK_GET_ELEMENT_TEXT) == 0) {
                        reportParseProblem(4);
                    }
                    this._textBuffer.append(getText());
                }
            } else {
                return this._textBuffer.get();
            }
        }
    }

    public int getEventType() {
        return this._currEvent;
    }

    public String getLocalName() {
        if (this._currEvent == 1 || this._currEvent == 2) {
            return _internName(_safeGetLocalName(this._currNode));
        }
        if (this._currEvent != 9) {
            reportWrongState(7);
        }
        return _internName(this._currNode.getNodeName());
    }

    @Override // org.codehaus.stax2.LocationInfo
    public final Location getLocation() {
        return getStartLocation();
    }

    public QName getName() {
        if (this._currEvent != 1 && this._currEvent != 2) {
            reportWrongState(1);
        }
        return _constructQName(this._currNode.getNamespaceURI(), _safeGetLocalName(this._currNode), this._currNode.getPrefix());
    }

    public NamespaceContext getNamespaceContext() {
        return this;
    }

    public int getNamespaceCount() {
        if (this._currEvent != 1 && this._currEvent != 2) {
            reportWrongState(2);
        }
        if (this._nsDeclList == null) {
            if (!this._cfgNsAware) {
                return 0;
            }
            _calcNsAndAttrLists(this._currEvent == 1);
        }
        return this._nsDeclList.size() / 2;
    }

    public String getNamespacePrefix(int index) {
        if (this._currEvent != 1 && this._currEvent != 2) {
            reportWrongState(2);
        }
        if (this._nsDeclList == null) {
            if (!this._cfgNsAware) {
                handleIllegalNsIndex(index);
            }
            _calcNsAndAttrLists(this._currEvent == 1);
        }
        if (index < 0 || index + index >= this._nsDeclList.size()) {
            handleIllegalNsIndex(index);
        }
        return this._nsDeclList.get(index + index);
    }

    public String getNamespaceURI() {
        if (this._currEvent != 1 && this._currEvent != 2) {
            reportWrongState(2);
        }
        return _internNsURI(this._currNode.getNamespaceURI());
    }

    public String getNamespaceURI(int index) {
        if (this._currEvent != 1 && this._currEvent != 2) {
            reportWrongState(2);
        }
        if (this._nsDeclList == null) {
            if (!this._cfgNsAware) {
                handleIllegalNsIndex(index);
            }
            _calcNsAndAttrLists(this._currEvent == 1);
        }
        if (index < 0 || index + index >= this._nsDeclList.size()) {
            handleIllegalNsIndex(index);
        }
        return this._nsDeclList.get(index + index + 1);
    }

    public String getPIData() {
        if (this._currEvent != 3) {
            reportWrongState(3);
        }
        return this._currNode.getNodeValue();
    }

    public String getPITarget() {
        if (this._currEvent != 3) {
            reportWrongState(3);
        }
        return _internName(this._currNode.getNodeName());
    }

    public String getPrefix() {
        if (this._currEvent != 1 && this._currEvent != 2) {
            reportWrongState(2);
        }
        return _internName(this._currNode.getPrefix());
    }

    public String getText() {
        if (this._coalescedText != null) {
            return this._coalescedText;
        }
        if (((1 << this._currEvent) & MASK_GET_TEXT) == 0) {
            reportWrongState(4);
        }
        return this._currNode.getNodeValue();
    }

    public char[] getTextCharacters() {
        String text = getText();
        return text.toCharArray();
    }

    public int getTextCharacters(int sourceStart, char[] target, int targetStart, int len) {
        if (((1 << this._currEvent) & 4208) == 0) {
            reportWrongState(5);
        }
        String text = getText();
        if (len > text.length()) {
            len = text.length();
        }
        text.getChars(sourceStart, sourceStart + len, target, targetStart);
        return len;
    }

    public int getTextLength() {
        if (((1 << this._currEvent) & 4208) == 0) {
            reportWrongState(5);
        }
        return getText().length();
    }

    public int getTextStart() {
        if (((1 << this._currEvent) & 4208) == 0) {
            reportWrongState(5);
            return 0;
        }
        return 0;
    }

    public boolean hasName() {
        return this._currEvent == 1 || this._currEvent == 2;
    }

    public boolean hasNext() {
        return this._currEvent != 8;
    }

    public boolean hasText() {
        return ((1 << this._currEvent) & MASK_GET_TEXT) != 0;
    }

    public boolean isAttributeSpecified(int index) {
        if (this._currEvent != 1) {
            reportWrongState(1);
        }
        Element elem = (Element) this._currNode;
        Attr attr = (Attr) elem.getAttributes().item(index);
        if (attr == null) {
            handleIllegalAttrIndex(index);
            return false;
        }
        return attr.getSpecified();
    }

    public boolean isCharacters() {
        return this._currEvent == 4;
    }

    public boolean isEndElement() {
        return this._currEvent == 2;
    }

    public boolean isStartElement() {
        return this._currEvent == 1;
    }

    public boolean isWhiteSpace() {
        if (this._currEvent != 4 && this._currEvent != 12) {
            return this._currEvent == 6;
        }
        String text = getText();
        int len = text.length();
        for (int i = 0; i < len; i++) {
            if (text.charAt(i) > ' ') {
                return false;
            }
        }
        return true;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void require(int type, String nsUri, String localName) throws XMLStreamException {
        int curr = this._currEvent;
        if (curr != type && (curr == 12 || curr == 6)) {
            curr = 4;
        }
        if (type != curr) {
            throwStreamException("Required type " + Stax2Util.eventTypeDesc(type) + ", current type " + Stax2Util.eventTypeDesc(curr));
        }
        if (localName != null) {
            if (curr != 1 && curr != 2 && curr != 9) {
                throwStreamException("Required a non-null local name, but current token not a START_ELEMENT, END_ELEMENT or ENTITY_REFERENCE (was " + Stax2Util.eventTypeDesc(this._currEvent) + ")");
            }
            String n = getLocalName();
            if (n != localName && !n.equals(localName)) {
                throwStreamException("Required local name '" + localName + "'; current local name '" + n + "'.");
            }
        }
        if (nsUri != null) {
            if (curr != 1 && curr != 2) {
                throwStreamException("Required non-null NS URI, but current token not a START_ELEMENT or END_ELEMENT (was " + Stax2Util.eventTypeDesc(curr) + ")");
            }
            String uri = getNamespaceURI();
            if (nsUri.length() == 0) {
                if (uri != null && uri.length() > 0) {
                    throwStreamException("Required empty namespace, instead have '" + uri + "'.");
                    return;
                }
                return;
            }
            if (nsUri != uri && !nsUri.equals(uri)) {
                throwStreamException("Required namespace '" + nsUri + "'; have '" + uri + "'.");
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    /* JADX WARN: Code duplicated, block: B:44:0x01dc  */
    /* JADX WARN: Code duplicated, block: B:46:0x01e3  */
    /* JADX WARN: Code duplicated, block: B:47:0x01ec  */
    /* JADX WARN: Code duplicated, block: B:48:0x01f5  */
    /* JADX WARN: Code duplicated, block: B:49:0x01fd  */
    /* JADX WARN: Code duplicated, block: B:50:0x0206  */
    /* JADX WARN: Code duplicated, block: B:51:0x020e  */
    /* JADX WARN: Code duplicated, block: B:52:0x0217  */
    /* JADX WARN: Code duplicated, block: B:53:0x021f  */
    /* JADX WARN: Code duplicated, block: B:55:0x0226  */
    /* JADX WARN: Code duplicated, block: B:56:0x022e  */
    /* JADX WARN: Code duplicated, block: B:57:0x0236  */
    /* JADX WARN: Code duplicated, block: B:59:0x026a  */
    public int next() throws XMLStreamException {
        this._coalescedText = null;
        switch (this._currEvent) {
            case 1:
                this._depth++;
                this._attrList = null;
                Node firstChild = this._currNode.getFirstChild();
                if (firstChild == null) {
                    this._currEvent = 2;
                    return 2;
                }
                this._nsDeclList = null;
                this._currNode = firstChild;
                switch (this._currNode.getNodeType()) {
                    case 1:
                        this._currEvent = 1;
                        break;
                    case 2:
                    case 6:
                    case 12:
                        throw new XMLStreamException("Internal error: unexpected DOM node type " + ((int) this._currNode.getNodeType()) + " (attr/entity/notation?), for node '" + this._currNode + OperatorName.SHOW_TEXT_LINE);
                    case 3:
                        if (this._coalescing) {
                            coalesceText(4);
                        } else {
                            this._currEvent = 4;
                        }
                        break;
                    case 4:
                        if (this._coalescing) {
                            coalesceText(12);
                        } else {
                            this._currEvent = 12;
                        }
                        break;
                    case 5:
                        this._currEvent = 9;
                        break;
                    case 7:
                        this._currEvent = 3;
                        break;
                    case 8:
                        this._currEvent = 5;
                        break;
                    case 9:
                    case 11:
                    default:
                        throw new XMLStreamException("Internal error: unrecognized DOM node type " + ((int) this._currNode.getNodeType()) + ", for node '" + this._currNode + OperatorName.SHOW_TEXT_LINE);
                    case 10:
                        this._currEvent = 11;
                        break;
                }
                return this._currEvent;
            case 2:
                this._depth--;
                this._attrList = null;
                this._nsDeclList = null;
                if (this._currNode == this._rootNode) {
                    this._currEvent = 8;
                    return 8;
                }
            case 3:
            case 4:
            case 5:
            case 6:
            default:
                Node next = this._currNode.getNextSibling();
                if (next != null) {
                    this._currNode = next;
                    switch (this._currNode.getNodeType()) {
                        case 1:
                            this._currEvent = 1;
                            break;
                        case 2:
                        case 6:
                        case 12:
                            throw new XMLStreamException("Internal error: unexpected DOM node type " + ((int) this._currNode.getNodeType()) + " (attr/entity/notation?), for node '" + this._currNode + OperatorName.SHOW_TEXT_LINE);
                        case 3:
                            if (this._coalescing) {
                                coalesceText(4);
                            } else {
                                this._currEvent = 4;
                            }
                            break;
                        case 4:
                            if (this._coalescing) {
                                coalesceText(12);
                            } else {
                                this._currEvent = 12;
                            }
                            break;
                        case 5:
                            this._currEvent = 9;
                            break;
                        case 7:
                            this._currEvent = 3;
                            break;
                        case 8:
                            this._currEvent = 5;
                            break;
                        case 9:
                        case 11:
                        default:
                            throw new XMLStreamException("Internal error: unrecognized DOM node type " + ((int) this._currNode.getNodeType()) + ", for node '" + this._currNode + OperatorName.SHOW_TEXT_LINE);
                        case 10:
                            this._currEvent = 11;
                            break;
                    }
                    return this._currEvent;
                }
                this._currNode = this._currNode.getParentNode();
                int type = this._currNode.getNodeType();
                if (type == 1) {
                    this._currEvent = 2;
                    return 2;
                }
                if (this._currNode != this._rootNode || (type != 9 && type != 11)) {
                    throw new XMLStreamException("Internal error: non-element parent node (" + type + ") that is not the initial root node");
                }
                this._currEvent = 8;
                return 8;
            case 7:
                switch (this._currNode.getNodeType()) {
                    case 1:
                        this._currEvent = 1;
                        return 1;
                    case 9:
                    case 11:
                        this._currNode = this._currNode.getFirstChild();
                        if (this._currNode == null) {
                            this._currEvent = 8;
                            return 8;
                        }
                        break;
                    default:
                        throw new XMLStreamException("Internal error: unexpected DOM root node type " + ((int) this._currNode.getNodeType()) + " for node '" + this._currNode + OperatorName.SHOW_TEXT_LINE);
                }
                switch (this._currNode.getNodeType()) {
                    case 1:
                        this._currEvent = 1;
                        break;
                    case 2:
                    case 6:
                    case 12:
                        throw new XMLStreamException("Internal error: unexpected DOM node type " + ((int) this._currNode.getNodeType()) + " (attr/entity/notation?), for node '" + this._currNode + OperatorName.SHOW_TEXT_LINE);
                    case 3:
                        if (this._coalescing) {
                            coalesceText(4);
                        } else {
                            this._currEvent = 4;
                        }
                        break;
                    case 4:
                        if (this._coalescing) {
                            coalesceText(12);
                        } else {
                            this._currEvent = 12;
                        }
                        break;
                    case 5:
                        this._currEvent = 9;
                        break;
                    case 7:
                        this._currEvent = 3;
                        break;
                    case 8:
                        this._currEvent = 5;
                        break;
                    case 9:
                    case 11:
                    default:
                        throw new XMLStreamException("Internal error: unrecognized DOM node type " + ((int) this._currNode.getNodeType()) + ", for node '" + this._currNode + OperatorName.SHOW_TEXT_LINE);
                    case 10:
                        this._currEvent = 11;
                        break;
                }
                return this._currEvent;
            case 8:
                throw new NoSuchElementException("Can not call next() after receiving END_DOCUMENT");
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public int nextTag() throws XMLStreamException {
        while (true) {
            int next = next();
            switch (next) {
                case 1:
                case 2:
                    return next;
                case 3:
                case 5:
                case 6:
                    continue;
                case 4:
                case 12:
                    if (!isWhiteSpace()) {
                        throwStreamException("Received non-all-whitespace CHARACTERS or CDATA event in nextTag().");
                        break;
                    }
                    break;
            }
            throwStreamException("Received event " + Stax2Util.eventTypeDesc(next) + ", instead of START_ELEMENT or END_ELEMENT.");
        }
    }

    public void close() throws XMLStreamException {
    }

    @Override // javax.xml.namespace.NamespaceContext
    public String getNamespaceURI(String prefix) {
        boolean defaultNs = prefix == null || prefix.length() == 0;
        for (Node n = this._currNode; n != null; n = n.getParentNode()) {
            NamedNodeMap attrs = n.getAttributes();
            if (attrs != null) {
                int len = attrs.getLength();
                for (int i = 0; i < len; i++) {
                    Node attr = attrs.item(i);
                    String thisPrefix = attr.getPrefix();
                    if (thisPrefix == null || thisPrefix.length() == 0) {
                        if (defaultNs && CanonicalizerBase.XMLNS.equals(attr.getLocalName())) {
                            return attr.getNodeValue();
                        }
                    } else if (!defaultNs && CanonicalizerBase.XMLNS.equals(thisPrefix) && prefix.equals(attr.getLocalName())) {
                        return attr.getNodeValue();
                    }
                }
            }
        }
        return null;
    }

    @Override // javax.xml.namespace.NamespaceContext
    public String getPrefix(String namespaceURI) {
        if (namespaceURI == null) {
            namespaceURI = "";
        }
        for (Node n = this._currNode; n != null; n = n.getParentNode()) {
            NamedNodeMap attrs = n.getAttributes();
            int len = attrs.getLength();
            for (int i = 0; i < len; i++) {
                Node attr = attrs.item(i);
                String thisPrefix = attr.getPrefix();
                if (thisPrefix == null || thisPrefix.length() == 0) {
                    if (CanonicalizerBase.XMLNS.equals(attr.getLocalName()) && namespaceURI.equals(attr.getNodeValue())) {
                        return "";
                    }
                } else if (CanonicalizerBase.XMLNS.equals(thisPrefix) && namespaceURI.equals(attr.getNodeValue())) {
                    return attr.getLocalName();
                }
            }
        }
        return null;
    }

    @Override // javax.xml.namespace.NamespaceContext
    public Iterator<String> getPrefixes(String namespaceURI) {
        String prefix = getPrefix(namespaceURI);
        if (prefix == null) {
            return EmptyIterator.getInstance();
        }
        return SingletonIterator.create(prefix);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public boolean getElementAsBoolean() throws XMLStreamException {
        ValueDecoderFactory.BooleanDecoder dec = _decoderFactory().getBooleanDecoder();
        getElementAs(dec);
        return dec.getValue();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public int getElementAsInt() throws XMLStreamException {
        ValueDecoderFactory.IntDecoder dec = _decoderFactory().getIntDecoder();
        getElementAs(dec);
        return dec.getValue();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public long getElementAsLong() throws XMLStreamException {
        ValueDecoderFactory.LongDecoder dec = _decoderFactory().getLongDecoder();
        getElementAs(dec);
        return dec.getValue();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public float getElementAsFloat() throws XMLStreamException {
        ValueDecoderFactory.FloatDecoder dec = _decoderFactory().getFloatDecoder();
        getElementAs(dec);
        return dec.getValue();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public double getElementAsDouble() throws XMLStreamException {
        ValueDecoderFactory.DoubleDecoder dec = _decoderFactory().getDoubleDecoder();
        getElementAs(dec);
        return dec.getValue();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public BigInteger getElementAsInteger() throws XMLStreamException {
        ValueDecoderFactory.IntegerDecoder dec = _decoderFactory().getIntegerDecoder();
        getElementAs(dec);
        return dec.getValue();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public BigDecimal getElementAsDecimal() throws XMLStreamException {
        ValueDecoderFactory.DecimalDecoder dec = _decoderFactory().getDecimalDecoder();
        getElementAs(dec);
        return dec.getValue();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public QName getElementAsQName() throws XMLStreamException {
        ValueDecoderFactory.QNameDecoder dec = _decoderFactory().getQNameDecoder(getNamespaceContext());
        getElementAs(dec);
        return dec.getValue();
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public byte[] getElementAsBinary() throws XMLStreamException {
        return getElementAsBinary(Base64Variants.getDefaultVariant());
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public byte[] getElementAsBinary(Base64Variant v) throws XMLStreamException {
        Stax2Util.ByteAggregator aggr = _base64Decoder().getByteAggregator();
        byte[] bArrStartAggregation = aggr.startAggregation();
        while (true) {
            byte[] buffer = bArrStartAggregation;
            int offset = 0;
            int len = buffer.length;
            do {
                int readCount = readElementAsBinary(buffer, offset, len, v);
                if (readCount < 1) {
                    return aggr.aggregateAll(buffer, offset);
                }
                offset += readCount;
                len -= readCount;
            } while (len > 0);
            bArrStartAggregation = aggr.addFullBlock(buffer);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public void getElementAs(TypedValueDecoder tvd) throws XMLStreamException {
        String value = Stax2Util.trimSpaces(getElementText());
        try {
            if (value == null) {
                tvd.handleEmptyValue();
            } else {
                tvd.decode(value);
            }
        } catch (IllegalArgumentException iae) {
            throw _constructTypeException(iae, value);
        }
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public int readElementAsIntArray(int[] value, int from, int length) throws XMLStreamException {
        return readElementAsArray(_decoderFactory().getIntArrayDecoder(value, from, length));
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public int readElementAsLongArray(long[] value, int from, int length) throws XMLStreamException {
        return readElementAsArray(_decoderFactory().getLongArrayDecoder(value, from, length));
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public int readElementAsFloatArray(float[] value, int from, int length) throws XMLStreamException {
        return readElementAsArray(_decoderFactory().getFloatArrayDecoder(value, from, length));
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public int readElementAsDoubleArray(double[] value, int from, int length) throws XMLStreamException {
        return readElementAsArray(_decoderFactory().getDoubleArrayDecoder(value, from, length));
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public int readElementAsArray(TypedArrayDecoder tad) throws XMLStreamException {
        if (this._currEvent == 1) {
            Node fc = this._currNode.getFirstChild();
            if (fc == null) {
                this._currEvent = 2;
                return -1;
            }
            this._coalescedText = coalesceTypedText(fc);
            this._currEvent = 4;
            this._currNode = this._currNode.getLastChild();
        } else {
            if (this._currEvent != 4 && this._currEvent != 12) {
                if (this._currEvent == 2) {
                    return -1;
                }
                reportWrongState(6);
            }
            if (this._coalescedText == null) {
                throw new IllegalStateException("First call to readElementAsArray() must be for a START_ELEMENT, not directly for a textual event");
            }
        }
        String input = this._coalescedText;
        int end = input.length();
        int ptr = 0;
        int count = 0;
        String value = null;
        loop0: while (ptr < end) {
            while (input.charAt(ptr) <= ' ') {
                try {
                    try {
                        ptr++;
                        if (ptr >= end) {
                            break loop0;
                        }
                    } catch (IllegalArgumentException iae) {
                        Location loc = getLocation();
                        throw new TypedXMLStreamException(value, iae.getMessage(), loc, iae);
                    }
                } catch (Throwable th) {
                    int len = end - ptr;
                    this._coalescedText = len < 1 ? "" : input.substring(ptr);
                    throw th;
                }
            }
            int start = ptr;
            int ptr2 = ptr + 1;
            while (ptr2 < end && input.charAt(ptr2) > ' ') {
                ptr2++;
            }
            count++;
            value = input.substring(start, ptr2);
            ptr = ptr2 + 1;
            if (tad.decodeValue(value)) {
                break;
            }
        }
        int len2 = end - ptr;
        this._coalescedText = len2 < 1 ? "" : input.substring(ptr);
        if (count < 1) {
            this._currEvent = 2;
            this._currNode = this._currNode.getParentNode();
            return -1;
        }
        return count;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    private String coalesceTypedText(Node firstNode) throws XMLStreamException {
        this._textBuffer.reset();
        this._attrList = null;
        Node nextSibling = firstNode;
        while (true) {
            Node n = nextSibling;
            if (n != null) {
                switch (n.getNodeType()) {
                    case 1:
                        throwStreamException("Element content can not contain child START_ELEMENT when using Typed Access methods");
                        break;
                    case 2:
                    case 5:
                    case 6:
                    default:
                        throwStreamException("Unexpected DOM node type (" + ((int) n.getNodeType()) + ") when trying to decode Typed content");
                        continue;
                        nextSibling = n.getNextSibling();
                        break;
                    case 3:
                    case 4:
                        break;
                    case 7:
                    case 8:
                        continue;
                        nextSibling = n.getNextSibling();
                        break;
                }
                this._textBuffer.append(n.getNodeValue());
                nextSibling = n.getNextSibling();
            } else {
                return this._textBuffer.get();
            }
        }
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public int readElementAsBinary(byte[] resultBuffer, int offset, int maxLength) throws XMLStreamException {
        return readElementAsBinary(resultBuffer, offset, maxLength, Base64Variants.getDefaultVariant());
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public int readElementAsBinary(byte[] resultBuffer, int offset, int maxLength, Base64Variant v) throws XMLStreamException {
        int type;
        if (resultBuffer == null) {
            throw new IllegalArgumentException("resultBuffer is null");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Illegal offset (" + offset + "), must be [0, " + resultBuffer.length + "[");
        }
        if (maxLength < 1 || offset + maxLength > resultBuffer.length) {
            if (maxLength == 0) {
                return 0;
            }
            throw new IllegalArgumentException("Illegal maxLength (" + maxLength + "), has to be positive number, and offset+maxLength can not exceed" + resultBuffer.length);
        }
        StringBase64Decoder dec = _base64Decoder();
        int type2 = this._currEvent;
        if (((1 << type2) & MASK_TYPED_ACCESS_BINARY) == 0) {
            if (type2 == 2) {
                if (!dec.hasData()) {
                    return -1;
                }
            } else {
                reportWrongState(6);
            }
        }
        if (type2 == 1) {
            while (true) {
                int type3 = next();
                if (type3 == 2) {
                    return -1;
                }
                if (type3 != 5 && type3 != 3) {
                    if (((1 << type3) & MASK_GET_ELEMENT_TEXT) == 0) {
                        reportParseProblem(4);
                    }
                    dec.init(v, true, getText());
                    break;
                }
            }
        }
        int totalCount = 0;
        while (true) {
            try {
                int count = dec.decode(resultBuffer, offset, maxLength);
                offset += count;
                totalCount += count;
                maxLength -= count;
                if (maxLength < 1 || this._currEvent == 2) {
                    break;
                }
                while (true) {
                    type = next();
                    if (type != 5 && type != 3 && type != 6) {
                        break;
                    }
                }
                if (type == 2) {
                    int left = dec.endOfContent();
                    if (left < 0) {
                        throw _constructTypeException("Incomplete base64 triplet at the end of decoded content", "");
                    }
                    if (left <= 0) {
                        break;
                    }
                } else {
                    if (((1 << type) & MASK_GET_ELEMENT_TEXT) == 0) {
                        reportParseProblem(4);
                    }
                    dec.init(v, false, getText());
                }
            } catch (IllegalArgumentException iae) {
                throw _constructTypeException(iae, "");
            }
        }
        if (totalCount > 0) {
            return totalCount;
        }
        return -1;
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public int getAttributeIndex(String namespaceURI, String localName) {
        return findAttributeIndex(namespaceURI, localName);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public boolean getAttributeAsBoolean(int index) throws XMLStreamException {
        ValueDecoderFactory.BooleanDecoder dec = _decoderFactory().getBooleanDecoder();
        getAttributeAs(index, dec);
        return dec.getValue();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public int getAttributeAsInt(int index) throws XMLStreamException {
        ValueDecoderFactory.IntDecoder dec = _decoderFactory().getIntDecoder();
        getAttributeAs(index, dec);
        return dec.getValue();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public long getAttributeAsLong(int index) throws XMLStreamException {
        ValueDecoderFactory.LongDecoder dec = _decoderFactory().getLongDecoder();
        getAttributeAs(index, dec);
        return dec.getValue();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public float getAttributeAsFloat(int index) throws XMLStreamException {
        ValueDecoderFactory.FloatDecoder dec = _decoderFactory().getFloatDecoder();
        getAttributeAs(index, dec);
        return dec.getValue();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public double getAttributeAsDouble(int index) throws XMLStreamException {
        ValueDecoderFactory.DoubleDecoder dec = _decoderFactory().getDoubleDecoder();
        getAttributeAs(index, dec);
        return dec.getValue();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public BigInteger getAttributeAsInteger(int index) throws XMLStreamException {
        ValueDecoderFactory.IntegerDecoder dec = _decoderFactory().getIntegerDecoder();
        getAttributeAs(index, dec);
        return dec.getValue();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public BigDecimal getAttributeAsDecimal(int index) throws XMLStreamException {
        ValueDecoderFactory.DecimalDecoder dec = _decoderFactory().getDecimalDecoder();
        getAttributeAs(index, dec);
        return dec.getValue();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public QName getAttributeAsQName(int index) throws XMLStreamException {
        ValueDecoderFactory.QNameDecoder dec = _decoderFactory().getQNameDecoder(getNamespaceContext());
        getAttributeAs(index, dec);
        return dec.getValue();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public final void getAttributeAs(int index, TypedValueDecoder tvd) throws XMLStreamException {
        String value = Stax2Util.trimSpaces(getAttributeValue(index));
        try {
            if (value == null) {
                tvd.handleEmptyValue();
            } else {
                tvd.decode(value);
            }
        } catch (IllegalArgumentException iae) {
            throw _constructTypeException(iae, value);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public int[] getAttributeAsIntArray(int index) throws XMLStreamException {
        ValueDecoderFactory.IntArrayDecoder dec = _decoderFactory().getIntArrayDecoder();
        _getAttributeAsArray(dec, getAttributeValue(index));
        return dec.getValues();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public long[] getAttributeAsLongArray(int index) throws XMLStreamException {
        ValueDecoderFactory.LongArrayDecoder dec = _decoderFactory().getLongArrayDecoder();
        _getAttributeAsArray(dec, getAttributeValue(index));
        return dec.getValues();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public float[] getAttributeAsFloatArray(int index) throws XMLStreamException {
        ValueDecoderFactory.FloatArrayDecoder dec = _decoderFactory().getFloatArrayDecoder();
        _getAttributeAsArray(dec, getAttributeValue(index));
        return dec.getValues();
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public double[] getAttributeAsDoubleArray(int index) throws XMLStreamException {
        ValueDecoderFactory.DoubleArrayDecoder dec = _decoderFactory().getDoubleArrayDecoder();
        _getAttributeAsArray(dec, getAttributeValue(index));
        return dec.getValues();
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public int getAttributeAsArray(int index, TypedArrayDecoder tad) throws XMLStreamException {
        return _getAttributeAsArray(tad, getAttributeValue(index));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected int _getAttributeAsArray(TypedArrayDecoder tad, String attrValue) throws XMLStreamException {
        int ptr = 0;
        int end = attrValue.length();
        String lexical = null;
        int count = 0;
        loop0: while (ptr < end) {
            while (attrValue.charAt(ptr) <= ' ') {
                try {
                    ptr++;
                    if (ptr >= end) {
                        break loop0;
                    }
                } catch (IllegalArgumentException iae) {
                    Location loc = getLocation();
                    throw new TypedXMLStreamException(lexical, iae.getMessage(), loc, iae);
                }
            }
            int start = ptr;
            int ptr2 = ptr + 1;
            while (ptr2 < end && attrValue.charAt(ptr2) > ' ') {
                ptr2++;
            }
            int tokenEnd = ptr2;
            ptr = ptr2 + 1;
            lexical = attrValue.substring(start, tokenEnd);
            count++;
            if (tad.decodeValue(lexical) && !checkExpand(tad)) {
                break;
            }
        }
        return count;
    }

    private final boolean checkExpand(TypedArrayDecoder tad) {
        if (tad instanceof ValueDecoderFactory.BaseArrayDecoder) {
            ((ValueDecoderFactory.BaseArrayDecoder) tad).expand();
            return true;
        }
        return false;
    }

    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public byte[] getAttributeAsBinary(int index) throws XMLStreamException {
        return getAttributeAsBinary(index, Base64Variants.getDefaultVariant());
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.typed.TypedXMLStreamReader
    public byte[] getAttributeAsBinary(int index, Base64Variant v) throws XMLStreamException {
        String lexical = getAttributeValue(index);
        StringBase64Decoder dec = _base64Decoder();
        dec.init(v, true, lexical);
        try {
            return dec.decodeCompletely();
        } catch (IllegalArgumentException iae) {
            throw _constructTypeException(iae, lexical);
        }
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    @Deprecated
    public Object getFeature(String name) {
        throw new IllegalArgumentException("Unrecognized feature \"" + name + OperatorName.SHOW_TEXT_LINE_AND_SPACE);
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    @Deprecated
    public void setFeature(String name, Object value) {
        throw new IllegalArgumentException("Unrecognized feature \"" + name + OperatorName.SHOW_TEXT_LINE_AND_SPACE);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.codehaus.stax2.XMLStreamReader2
    public void skipElement() throws XMLStreamException {
        if (this._currEvent != 1) {
            reportWrongState(1);
        }
        int nesting = 1;
        while (true) {
            int type = next();
            if (type == 1) {
                nesting++;
            } else if (type == 2) {
                nesting--;
                if (nesting == 0) {
                    return;
                }
            } else {
                continue;
            }
        }
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    public AttributeInfo getAttributeInfo() throws XMLStreamException {
        if (this._currEvent != 1) {
            reportWrongState(1);
        }
        return this;
    }

    @Override // org.codehaus.stax2.AttributeInfo, org.codehaus.stax2.validation.ValidationContext
    public int findAttributeIndex(String nsURI, String localName) {
        if (this._currEvent != 1) {
            reportWrongState(1);
        }
        Element elem = (Element) this._currNode;
        NamedNodeMap attrs = elem.getAttributes();
        if (nsURI != null && nsURI.length() == 0) {
            nsURI = null;
        }
        int len = attrs.getLength();
        for (int i = 0; i < len; i++) {
            Node attr = attrs.item(i);
            String ln = _safeGetLocalName(attr);
            if (localName.equals(ln)) {
                String thisUri = attr.getNamespaceURI();
                boolean isEmpty = thisUri == null || thisUri.length() == 0;
                if (nsURI == null) {
                    if (isEmpty) {
                        return i;
                    }
                } else if (!isEmpty && nsURI.equals(thisUri)) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override // org.codehaus.stax2.AttributeInfo
    public int getIdAttributeIndex() {
        return -1;
    }

    @Override // org.codehaus.stax2.AttributeInfo
    public int getNotationAttributeIndex() {
        return -1;
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    public DTDInfo getDTDInfo() throws XMLStreamException {
        if (this._currEvent != 11) {
            return null;
        }
        return this;
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    public final LocationInfo getLocationInfo() {
        return this;
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    public int getText(Writer w, boolean preserveContents) throws XMLStreamException, IOException {
        String text = getText();
        w.write(text);
        return text.length();
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    public int getDepth() {
        return this._depth;
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    public boolean isEmptyElement() throws XMLStreamException {
        return false;
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    public NamespaceContext getNonTransientNamespaceContext() {
        return EmptyNamespaceContext.getInstance();
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    public String getPrefixedName() {
        switch (this._currEvent) {
            case 1:
            case 2:
                String prefix = this._currNode.getPrefix();
                String ln = _safeGetLocalName(this._currNode);
                if (prefix == null) {
                    return _internName(ln);
                }
                StringBuffer sb = new StringBuffer(ln.length() + 1 + prefix.length());
                sb.append(prefix);
                sb.append(':');
                sb.append(ln);
                return _internName(sb.toString());
            case 3:
                return getPITarget();
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 10:
            default:
                throw new IllegalStateException("Current state (" + Stax2Util.eventTypeDesc(this._currEvent) + ") not START_ELEMENT, END_ELEMENT, ENTITY_REFERENCE, PROCESSING_INSTRUCTION or DTD");
            case 9:
                return getLocalName();
            case 11:
                return getDTDRootName();
        }
    }

    @Override // org.codehaus.stax2.XMLStreamReader2
    public void closeCompletely() throws XMLStreamException {
    }

    @Override // org.codehaus.stax2.DTDInfo
    public Object getProcessedDTD() {
        return null;
    }

    @Override // org.codehaus.stax2.DTDInfo
    public String getDTDRootName() {
        if (this._currEvent == 11) {
            return _internName(((DocumentType) this._currNode).getName());
        }
        return null;
    }

    @Override // org.codehaus.stax2.DTDInfo
    public String getDTDPublicId() {
        if (this._currEvent == 11) {
            return ((DocumentType) this._currNode).getPublicId();
        }
        return null;
    }

    @Override // org.codehaus.stax2.DTDInfo
    public String getDTDSystemId() {
        if (this._currEvent == 11) {
            return ((DocumentType) this._currNode).getSystemId();
        }
        return null;
    }

    @Override // org.codehaus.stax2.DTDInfo
    public String getDTDInternalSubset() {
        return null;
    }

    @Override // org.codehaus.stax2.DTDInfo
    public DTDValidationSchema getProcessedDTDSchema() {
        return null;
    }

    @Override // org.codehaus.stax2.LocationInfo
    public long getStartingByteOffset() {
        return -1L;
    }

    @Override // org.codehaus.stax2.LocationInfo
    public long getStartingCharOffset() {
        return 0L;
    }

    @Override // org.codehaus.stax2.LocationInfo
    public long getEndingByteOffset() throws XMLStreamException {
        return -1L;
    }

    @Override // org.codehaus.stax2.LocationInfo
    public long getEndingCharOffset() throws XMLStreamException {
        return -1L;
    }

    @Override // org.codehaus.stax2.LocationInfo
    public XMLStreamLocation2 getStartLocation() {
        return XMLStreamLocation2.NOT_AVAILABLE;
    }

    @Override // org.codehaus.stax2.LocationInfo
    public XMLStreamLocation2 getCurrentLocation() {
        return XMLStreamLocation2.NOT_AVAILABLE;
    }

    @Override // org.codehaus.stax2.LocationInfo
    public final XMLStreamLocation2 getEndLocation() throws XMLStreamException {
        return XMLStreamLocation2.NOT_AVAILABLE;
    }

    @Override // org.codehaus.stax2.validation.Validatable
    public XMLValidator validateAgainst(XMLValidationSchema schema) throws XMLStreamException {
        return null;
    }

    @Override // org.codehaus.stax2.validation.Validatable
    public XMLValidator stopValidatingAgainst(XMLValidationSchema schema) throws XMLStreamException {
        return null;
    }

    @Override // org.codehaus.stax2.validation.Validatable
    public XMLValidator stopValidatingAgainst(XMLValidator validator) throws XMLStreamException {
        return null;
    }

    @Override // org.codehaus.stax2.validation.Validatable
    public ValidationProblemHandler setValidationProblemHandler(ValidationProblemHandler h) {
        return null;
    }

    protected void coalesceText(int initialType) {
        int type;
        this._textBuffer.reset();
        this._textBuffer.append(this._currNode.getNodeValue());
        while (true) {
            Node n = this._currNode.getNextSibling();
            if (n == null || !((type = n.getNodeType()) == 3 || type == 4)) {
                break;
            }
            this._currNode = n;
            this._textBuffer.append(this._currNode.getNodeValue());
        }
        this._coalescedText = this._textBuffer.get();
        this._currEvent = 4;
    }

    private QName _constructQName(String uri, String ln, String prefix) {
        return new QName(_internNsURI(uri), _internName(ln), _internName(prefix));
    }

    /* JADX WARN: Code duplicated, block: B:41:0x00fe  */
    private void _calcNsAndAttrLists(boolean attrsToo) {
        String prefix;
        NamedNodeMap attrsIn = this._currNode.getAttributes();
        int len = attrsIn.getLength();
        if (len == 0) {
            this._attrList = Collections.emptyList();
            this._nsDeclList = Collections.emptyList();
            return;
        }
        if (!this._cfgNsAware) {
            this._attrList = new ArrayList(len);
            for (int i = 0; i < len; i++) {
                this._attrList.add(attrsIn.item(i));
            }
            this._nsDeclList = Collections.emptyList();
            return;
        }
        ArrayList<Node> attrsOut = null;
        ArrayList<String> nsOut = null;
        for (int i2 = 0; i2 < len; i2++) {
            Node attr = attrsIn.item(i2);
            String prefix2 = attr.getPrefix();
            if (prefix2 == null || prefix2.length() == 0) {
                if (!CanonicalizerBase.XMLNS.equals(attr.getLocalName())) {
                    if (attrsToo) {
                        if (attrsOut == null) {
                            attrsOut = new ArrayList<>(len - i2);
                        }
                        attrsOut.add(attr);
                    }
                } else {
                    prefix = null;
                    if (nsOut == null) {
                        nsOut = new ArrayList<>((len - i2) * 2);
                    }
                    nsOut.add(_internName(prefix));
                    nsOut.add(_internNsURI(attr.getNodeValue()));
                }
            } else if (!CanonicalizerBase.XMLNS.equals(prefix2)) {
                if (attrsToo) {
                    if (attrsOut == null) {
                        attrsOut = new ArrayList<>(len - i2);
                    }
                    attrsOut.add(attr);
                }
            } else {
                prefix = attr.getLocalName();
                if (nsOut == null) {
                    nsOut = new ArrayList<>((len - i2) * 2);
                }
                nsOut.add(_internName(prefix));
                nsOut.add(_internNsURI(attr.getNodeValue()));
            }
        }
        this._attrList = attrsOut == null ? Collections.emptyList() : attrsOut;
        this._nsDeclList = nsOut == null ? Collections.emptyList() : nsOut;
    }

    private void handleIllegalAttrIndex(int index) {
        Element elem = (Element) this._currNode;
        NamedNodeMap attrs = elem.getAttributes();
        int len = attrs.getLength();
        String msg = "Illegal attribute index " + index + "; element <" + elem.getNodeName() + "> has " + (len == 0 ? XmlConsts.XML_SA_NO : String.valueOf(len)) + " attributes";
        throw new IllegalArgumentException(msg);
    }

    private void handleIllegalNsIndex(int index) {
        String msg = "Illegal namespace declaration index " + index + " (has " + getNamespaceCount() + " ns declarations)";
        throw new IllegalArgumentException(msg);
    }

    private String _safeGetLocalName(Node n) {
        String ln = n.getLocalName();
        if (ln == null) {
            ln = n.getNodeName();
        }
        return ln;
    }

    protected void reportWrongState(int errorType) {
        throw new IllegalStateException(findErrorDesc(errorType, this._currEvent));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void reportParseProblem(int errorType) throws XMLStreamException {
        throwStreamException(findErrorDesc(errorType, this._currEvent));
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    protected void throwStreamException(String msg) throws XMLStreamException {
        throwStreamException(msg, getErrorLocation());
    }

    protected Location getErrorLocation() {
        Location loc = getCurrentLocation();
        if (loc == null) {
            loc = getLocation();
        }
        return loc;
    }

    protected TypedXMLStreamException _constructTypeException(IllegalArgumentException iae, String lexicalValue) {
        String msg = iae.getMessage();
        if (msg == null) {
            msg = "";
        }
        Location loc = getStartLocation();
        if (loc == null) {
            return new TypedXMLStreamException(lexicalValue, msg, iae);
        }
        return new TypedXMLStreamException(lexicalValue, msg, loc);
    }

    protected TypedXMLStreamException _constructTypeException(String msg, String lexicalValue) {
        Location loc = getStartLocation();
        if (loc == null) {
            return new TypedXMLStreamException(lexicalValue, msg);
        }
        return new TypedXMLStreamException(lexicalValue, msg, loc);
    }

    protected ValueDecoderFactory _decoderFactory() {
        if (this._decoderFactory == null) {
            this._decoderFactory = new ValueDecoderFactory();
        }
        return this._decoderFactory;
    }

    protected StringBase64Decoder _base64Decoder() {
        if (this._base64Decoder == null) {
            this._base64Decoder = new StringBase64Decoder();
        }
        return this._base64Decoder;
    }

    protected String findErrorDesc(int errorType, int currEvent) {
        String evtDesc = Stax2Util.eventTypeDesc(currEvent);
        switch (errorType) {
            case 1:
                return "Current event " + evtDesc + ", needs to be START_ELEMENT";
            case 2:
                return "Current event " + evtDesc + ", needs to be START_ELEMENT or END_ELEMENT";
            case 3:
                return "Current event (" + evtDesc + ") needs to be PROCESSING_INSTRUCTION";
            case 4:
                return "Current event (" + evtDesc + ") not a textual event";
            case 5:
                return "Current event " + evtDesc + ", needs to be one of CHARACTERS, CDATA, SPACE or COMMENT";
            case 6:
                return "Current event (" + evtDesc + " not START_ELEMENT, END_ELEMENT, CHARACTERS or CDATA";
            case 7:
                return "Current event (" + evtDesc + ") has no local name";
            default:
                return "Internal error (unrecognized error type: " + errorType + ")";
        }
    }

    protected String _internName(String name) {
        if (name == null) {
            return "";
        }
        return this._cfgInternNames ? name.intern() : name;
    }

    protected String _internNsURI(String uri) {
        if (uri == null) {
            return "";
        }
        return this._cfgInternNsURIs ? uri.intern() : uri;
    }
}
