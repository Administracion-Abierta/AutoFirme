package org.apache.xml.security.stax.impl.transformer.canonicalizer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.apache.xml.security.c14n.implementations.UtfHelpper;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.Transformer;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.apache.xml.security.stax.ext.XMLSecurityUtils;
import org.apache.xml.security.stax.ext.stax.XMLSecAttribute;
import org.apache.xml.security.stax.ext.stax.XMLSecComment;
import org.apache.xml.security.stax.ext.stax.XMLSecEndElement;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecEventFactory;
import org.apache.xml.security.stax.ext.stax.XMLSecNamespace;
import org.apache.xml.security.stax.ext.stax.XMLSecProcessingInstruction;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;
import org.apache.xml.security.stax.impl.processor.input.XMLEventReaderInputProcessor;
import org.apache.xml.security.stax.impl.transformer.TransformIdentity;
import org.apache.xml.security.utils.UnsyncByteArrayInputStream;
import org.apache.xml.security.utils.UnsyncByteArrayOutputStream;
import org.spongycastle.asn1.eac.EACTags;
import org.spongycastle.crypto.tls.CipherSuite;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/impl/transformer/canonicalizer/CanonicalizerBase.class */
public abstract class CanonicalizerBase extends TransformIdentity {
    protected static final String XML = "xml";
    protected static final String XMLNS = "xmlns";
    protected static final char DOUBLEPOINT = ':';
    private boolean includeComments;
    static final byte[] _END_PI = {63, 62};
    static final byte[] _BEGIN_PI = {60, 63};
    static final byte[] _END_COMM = {45, 45, 62};
    static final byte[] _BEGIN_COMM = {60, 33, 45, 45};
    static final byte[] __XA_ = {38, 35, 120, 65, 59};
    static final byte[] __X9_ = {38, 35, 120, 57, 59};
    static final byte[] _QUOT_ = {38, 113, 117, 111, 116, 59};
    static final byte[] __XD_ = {38, 35, 120, 68, 59};
    static final byte[] _GT_ = {38, 103, 116, 59};
    static final byte[] _LT_ = {38, 108, 116, 59};
    static final byte[] _END_TAG = {60, 47};
    static final byte[] _AMP_ = {38, 97, 109, 112, 59};
    static final byte[] EQUAL_STRING = {61, 34};
    static final byte[] NEWLINE = {10};
    private static final Map<String, byte[]> CACHE = Collections.synchronizedMap(new WeakHashMap());
    private final C14NStack<XMLSecEvent> outputStack = new C14NStack<>();
    private DocumentLevel currentDocumentLevel = DocumentLevel.NODE_BEFORE_DOCUMENT_ELEMENT;
    protected boolean firstCall = true;

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/impl/transformer/canonicalizer/CanonicalizerBase$DocumentLevel.class */
    private enum DocumentLevel {
        NODE_BEFORE_DOCUMENT_ELEMENT,
        NODE_NOT_BEFORE_OR_AFTER_DOCUMENT_ELEMENT,
        NODE_AFTER_DOCUMENT_ELEMENT
    }

    public CanonicalizerBase(boolean includeComments) {
        this.includeComments = false;
        this.includeComments = includeComments;
    }

    @Override // org.apache.xml.security.stax.impl.transformer.TransformIdentity, org.apache.xml.security.stax.ext.Transformer
    public void setProperties(Map<String, Object> properties) throws XMLSecurityException {
        throw new UnsupportedOperationException("InclusiveNamespace-PrefixList not supported");
    }

    @Override // org.apache.xml.security.stax.impl.transformer.TransformIdentity, org.apache.xml.security.stax.ext.Transformer
    public void setTransformer(Transformer transformer) throws XMLSecurityException {
        setOutputStream(new UnsyncByteArrayOutputStream());
        super.setTransformer(transformer);
    }

    protected List<XMLSecNamespace> getCurrentUtilizedNamespaces(XMLSecStartElement xmlSecStartElement, C14NStack<XMLSecEvent> outputStack) {
        XMLSecNamespace resultNamespace;
        List<XMLSecNamespace> utilizedNamespaces = Collections.emptyList();
        XMLSecNamespace elementNamespace = xmlSecStartElement.getElementNamespace();
        XMLSecNamespace found = (XMLSecNamespace) outputStack.containsOnStack(elementNamespace);
        if (found == null || found.getNamespaceURI() == null || !found.getNamespaceURI().equals(elementNamespace.getNamespaceURI())) {
            utilizedNamespaces = new ArrayList(2);
            utilizedNamespaces.add(elementNamespace);
            outputStack.peek().add(elementNamespace);
        }
        List<XMLSecNamespace> declaredNamespaces = xmlSecStartElement.getOnElementDeclaredNamespaces();
        for (int i = 0; i < declaredNamespaces.size(); i++) {
            XMLSecNamespace comparableNamespace = declaredNamespaces.get(i);
            XMLSecNamespace resultNamespace2 = (XMLSecNamespace) outputStack.containsOnStack(comparableNamespace);
            if (resultNamespace2 == null || resultNamespace2.getNamespaceURI() == null || !resultNamespace2.getNamespaceURI().equals(comparableNamespace.getNamespaceURI())) {
                if (utilizedNamespaces == Collections.emptyList()) {
                    utilizedNamespaces = new ArrayList(2);
                }
                utilizedNamespaces.add(comparableNamespace);
                outputStack.peek().add(comparableNamespace);
            }
        }
        List<XMLSecAttribute> comparableAttributes = xmlSecStartElement.getOnElementDeclaredAttributes();
        for (int i2 = 0; i2 < comparableAttributes.size(); i2++) {
            XMLSecAttribute xmlSecAttribute = comparableAttributes.get(i2);
            XMLSecNamespace attributeNamespace = xmlSecAttribute.getAttributeNamespace();
            if (!"xml".equals(attributeNamespace.getPrefix()) && attributeNamespace.getNamespaceURI() != null && !attributeNamespace.getNamespaceURI().isEmpty() && ((resultNamespace = (XMLSecNamespace) outputStack.containsOnStack(attributeNamespace)) == null || resultNamespace.getNamespaceURI() == null || !resultNamespace.getNamespaceURI().equals(attributeNamespace.getNamespaceURI()))) {
                if (utilizedNamespaces == Collections.emptyList()) {
                    utilizedNamespaces = new ArrayList(2);
                }
                utilizedNamespaces.add(attributeNamespace);
                outputStack.peek().add(attributeNamespace);
            }
        }
        return utilizedNamespaces;
    }

    protected List<XMLSecAttribute> getCurrentUtilizedAttributes(XMLSecStartElement xmlSecStartElement, C14NStack<XMLSecEvent> outputStack) {
        List<XMLSecAttribute> comparableAttributes = xmlSecStartElement.getOnElementDeclaredAttributes();
        if (comparableAttributes.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList(comparableAttributes);
    }

    protected List<XMLSecNamespace> getInitialUtilizedNamespaces(XMLSecStartElement xmlSecStartElement, C14NStack<XMLSecEvent> outputStack) {
        List<XMLSecNamespace> utilizedNamespaces = new ArrayList<>();
        List<XMLSecNamespace> visibleNamespaces = new ArrayList<>();
        xmlSecStartElement.getNamespacesFromCurrentScope(visibleNamespaces);
        for (int i = 0; i < visibleNamespaces.size(); i++) {
            XMLSecNamespace comparableNamespace = visibleNamespaces.get(i);
            XMLSecNamespace found = (XMLSecNamespace) outputStack.containsOnStack(comparableNamespace);
            if (found != null) {
                utilizedNamespaces.remove(comparableNamespace);
            }
            outputStack.peek().add(comparableNamespace);
            if (!comparableNamespace.getNamespaceURI().isEmpty() || !comparableNamespace.getPrefix().isEmpty()) {
                utilizedNamespaces.add(comparableNamespace);
            }
        }
        return utilizedNamespaces;
    }

    protected List<XMLSecAttribute> getInitialUtilizedAttributes(XMLSecStartElement xmlSecStartElement, C14NStack<XMLSecEvent> outputStack) {
        List<XMLSecAttribute> utilizedAttributes = Collections.emptyList();
        List<XMLSecAttribute> comparableAttributes = new ArrayList<>();
        xmlSecStartElement.getAttributesFromCurrentScope(comparableAttributes);
        for (int i = 0; i < comparableAttributes.size(); i++) {
            XMLSecAttribute comparableAttribute = comparableAttributes.get(i);
            if ("xml".equals(comparableAttribute.getName().getPrefix()) && outputStack.containsOnStack(comparableAttribute) == null) {
                if (utilizedAttributes == Collections.emptyList()) {
                    utilizedAttributes = new ArrayList(2);
                }
                utilizedAttributes.add(comparableAttribute);
                outputStack.peek().add(comparableAttribute);
            }
        }
        List<XMLSecAttribute> elementAttributes = xmlSecStartElement.getOnElementDeclaredAttributes();
        for (int i2 = 0; i2 < elementAttributes.size(); i2++) {
            XMLSecAttribute comparableAttribute2 = elementAttributes.get(i2);
            QName attributeName = comparableAttribute2.getName();
            if (!"xml".equals(attributeName.getPrefix())) {
                if (utilizedAttributes == Collections.emptyList()) {
                    utilizedAttributes = new ArrayList(2);
                }
                utilizedAttributes.add(comparableAttribute2);
            }
        }
        return utilizedAttributes;
    }

    @Override // org.apache.xml.security.stax.impl.transformer.TransformIdentity, org.apache.xml.security.stax.ext.Transformer
    public XMLSecurityConstants.TransformMethod getPreferredTransformMethod(XMLSecurityConstants.TransformMethod forInput) {
        switch (forInput) {
            case XMLSecEvent:
                return XMLSecurityConstants.TransformMethod.XMLSecEvent;
            case InputStream:
                return XMLSecurityConstants.TransformMethod.InputStream;
            default:
                throw new IllegalArgumentException("Unsupported class " + forInput.name());
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.apache.xml.security.stax.impl.transformer.TransformIdentity, org.apache.xml.security.stax.ext.Transformer
    public void transform(XMLSecEvent xMLSecEvent) throws XMLStreamException {
        List<XMLSecNamespace> currentUtilizedNamespaces;
        List<XMLSecAttribute> currentUtilizedAttributes;
        try {
            OutputStream outputStream = getOutputStream();
            switch (xMLSecEvent.getEventType()) {
                case 1:
                    XMLSecStartElement xMLSecStartElementMo458asStartElement = xMLSecEvent.mo458asStartElement();
                    this.currentDocumentLevel = DocumentLevel.NODE_NOT_BEFORE_OR_AFTER_DOCUMENT_ELEMENT;
                    this.outputStack.push((XMLSecEvent) Collections.emptyList());
                    if (this.firstCall) {
                        currentUtilizedNamespaces = new ArrayList();
                        currentUtilizedAttributes = new ArrayList();
                        this.outputStack.peek().add(XMLSecEventFactory.createXMLSecNamespace(null, ""));
                        this.outputStack.push((XMLSecEvent) Collections.emptyList());
                        currentUtilizedNamespaces.addAll(getInitialUtilizedNamespaces(xMLSecStartElementMo458asStartElement, this.outputStack));
                        currentUtilizedAttributes.addAll(getInitialUtilizedAttributes(xMLSecStartElementMo458asStartElement, this.outputStack));
                        this.firstCall = false;
                    } else {
                        currentUtilizedNamespaces = getCurrentUtilizedNamespaces(xMLSecStartElementMo458asStartElement, this.outputStack);
                        currentUtilizedAttributes = getCurrentUtilizedAttributes(xMLSecStartElementMo458asStartElement, this.outputStack);
                    }
                    outputStream.write(60);
                    String prefix = xMLSecStartElementMo458asStartElement.getName().getPrefix();
                    if (prefix != null && !prefix.isEmpty()) {
                        UtfHelpper.writeByte(prefix, outputStream, CACHE);
                        outputStream.write(58);
                    }
                    UtfHelpper.writeByte(xMLSecStartElementMo458asStartElement.getName().getLocalPart(), outputStream, CACHE);
                    if (!currentUtilizedNamespaces.isEmpty()) {
                        Collections.sort(currentUtilizedNamespaces);
                        for (XMLSecNamespace xMLSecNamespace : currentUtilizedNamespaces) {
                            if (!namespaceIsAbsolute(xMLSecNamespace.getNamespaceURI())) {
                                throw new XMLStreamException("namespace is relative encountered: " + xMLSecNamespace.getNamespaceURI());
                            }
                            if (xMLSecNamespace.isDefaultNamespaceDeclaration()) {
                                outputAttrToWriter(null, "xmlns", xMLSecNamespace.getNamespaceURI(), outputStream, CACHE);
                            } else {
                                outputAttrToWriter("xmlns", xMLSecNamespace.getPrefix(), xMLSecNamespace.getNamespaceURI(), outputStream, CACHE);
                            }
                        }
                    }
                    if (!currentUtilizedAttributes.isEmpty()) {
                        Collections.sort(currentUtilizedAttributes);
                        for (XMLSecAttribute xMLSecAttribute : currentUtilizedAttributes) {
                            QName name = xMLSecAttribute.getName();
                            String prefix2 = name.getPrefix();
                            if (prefix2 != null && !prefix2.isEmpty()) {
                                outputAttrToWriter(prefix2, name.getLocalPart(), xMLSecAttribute.getValue(), outputStream, CACHE);
                            } else {
                                outputAttrToWriter(null, name.getLocalPart(), xMLSecAttribute.getValue(), outputStream, CACHE);
                            }
                        }
                    }
                    outputStream.write(62);
                    break;
                case 2:
                    XMLSecEndElement xMLSecEndElementAsEndElement = xMLSecEvent.mo457asEndElement();
                    String prefix3 = xMLSecEndElementAsEndElement.getName().getPrefix();
                    outputStream.write(_END_TAG);
                    if (prefix3 != null && !prefix3.isEmpty()) {
                        UtfHelpper.writeByte(prefix3, outputStream, CACHE);
                        outputStream.write(58);
                    }
                    UtfHelpper.writeByte(xMLSecEndElementAsEndElement.getName().getLocalPart(), outputStream, CACHE);
                    outputStream.write(62);
                    this.outputStack.pop();
                    if (this.outputStack.size() == 1) {
                        this.currentDocumentLevel = DocumentLevel.NODE_AFTER_DOCUMENT_ELEMENT;
                    }
                    break;
                case 3:
                    outputPItoWriter((XMLSecProcessingInstruction) xMLSecEvent, outputStream, this.currentDocumentLevel);
                    break;
                case 4:
                    if (this.currentDocumentLevel == DocumentLevel.NODE_NOT_BEFORE_OR_AFTER_DOCUMENT_ELEMENT) {
                        outputTextToWriter(xMLSecEvent.mo456asCharacters().getText(), outputStream);
                    }
                    break;
                case 5:
                    if (this.includeComments) {
                        outputCommentToWriter((XMLSecComment) xMLSecEvent, outputStream, this.currentDocumentLevel);
                    }
                    break;
                case 6:
                    if (this.currentDocumentLevel == DocumentLevel.NODE_NOT_BEFORE_OR_AFTER_DOCUMENT_ELEMENT) {
                        outputTextToWriter(xMLSecEvent.mo456asCharacters().getText(), outputStream);
                    }
                    break;
                case 7:
                    this.currentDocumentLevel = DocumentLevel.NODE_BEFORE_DOCUMENT_ELEMENT;
                    break;
                case 9:
                    throw new XMLStreamException("illegal event :" + XMLSecurityUtils.getXMLEventAsString(xMLSecEvent));
                case 10:
                    throw new XMLStreamException("illegal event :" + XMLSecurityUtils.getXMLEventAsString(xMLSecEvent));
                case 12:
                    outputTextToWriter(xMLSecEvent.mo456asCharacters().getData(), outputStream);
                    break;
                case 13:
                    throw new XMLStreamException("illegal event :" + XMLSecurityUtils.getXMLEventAsString(xMLSecEvent));
                case 14:
                    throw new XMLStreamException("illegal event :" + XMLSecurityUtils.getXMLEventAsString(xMLSecEvent));
                case 15:
                    throw new XMLStreamException("illegal event :" + XMLSecurityUtils.getXMLEventAsString(xMLSecEvent));
            }
        } catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.apache.xml.security.stax.impl.transformer.TransformIdentity, org.apache.xml.security.stax.ext.Transformer
    public void transform(InputStream inputStream) throws XMLStreamException {
        XMLSecEvent xmlSecEvent;
        XMLEventReaderInputProcessor xmlEventReaderInputProcessor = new XMLEventReaderInputProcessor(null, getXmlInputFactory().createXMLStreamReader(inputStream));
        do {
            try {
                xmlSecEvent = xmlEventReaderInputProcessor.processEvent(null);
                transform(xmlSecEvent);
            } catch (XMLSecurityException e) {
                throw new XMLStreamException(e);
            }
        } while (xmlSecEvent.getEventType() != 8);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    @Override // org.apache.xml.security.stax.impl.transformer.TransformIdentity, org.apache.xml.security.stax.ext.Transformer
    public void doFinal() throws XMLStreamException {
        if (getTransformer() != null) {
            UnsyncByteArrayOutputStream baos = (UnsyncByteArrayOutputStream) getOutputStream();
            try {
                InputStream is = new UnsyncByteArrayInputStream(baos.toByteArray());
                Throwable th = null;
                try {
                    try {
                        getTransformer().transform(is);
                        getTransformer().doFinal();
                        if (0 != 0) {
                            try {
                                is.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            is.close();
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        throw th3;
                    }
                } catch (Throwable th4) {
                    if (th != null) {
                        try {
                            is.close();
                        } catch (Throwable th5) {
                            th.addSuppressed(th5);
                        }
                    } else {
                        is.close();
                    }
                    throw th4;
                }
            } catch (IOException ex) {
                throw new XMLStreamException(ex);
            }
        }
    }

    protected static void outputAttrToWriter(String prefix, String name, String value, OutputStream writer, Map<String, byte[]> CACHE2) throws IOException {
        byte[] bArr;
        writer.write(32);
        if (prefix != null) {
            UtfHelpper.writeByte(prefix, writer, CACHE2);
            UtfHelpper.writeCodePointToUtf8(58, writer);
        }
        UtfHelpper.writeByte(name, writer, CACHE2);
        writer.write(EQUAL_STRING);
        int length = value.length();
        int i = 0;
        while (i < length) {
            int c = value.codePointAt(i);
            i += Character.charCount(c);
            switch (c) {
                case 9:
                    bArr = __X9_;
                    break;
                case 10:
                    bArr = __XA_;
                    break;
                case 13:
                    bArr = __XD_;
                    break;
                case 34:
                    bArr = _QUOT_;
                    break;
                case EACTags.CARD_EFFECTIVE_DATE /* 38 */:
                    bArr = _AMP_;
                    break;
                case 60:
                    bArr = _LT_;
                    break;
                default:
                    if (c < 128) {
                        writer.write(c);
                    } else {
                        UtfHelpper.writeCodePointToUtf8(c, writer);
                        continue;
                    }
                    break;
            }
            byte[] toWrite = bArr;
            writer.write(toWrite);
        }
        writer.write(34);
    }

    protected static void outputTextToWriter(String text, OutputStream writer) throws IOException {
        byte[] bArr;
        int length = text.length();
        int i = 0;
        while (i < length) {
            int c = text.codePointAt(i);
            i += Character.charCount(c);
            switch (c) {
                case 13:
                    bArr = __XD_;
                    break;
                case EACTags.CARD_EFFECTIVE_DATE /* 38 */:
                    bArr = _AMP_;
                    break;
                case 60:
                    bArr = _LT_;
                    break;
                case CipherSuite.TLS_DH_DSS_WITH_AES_128_CBC_SHA256 /* 62 */:
                    bArr = _GT_;
                    break;
                default:
                    if (c < 128) {
                        writer.write(c);
                    } else {
                        UtfHelpper.writeCodePointToUtf8(c, writer);
                        continue;
                    }
                    break;
            }
            byte[] toWrite = bArr;
            writer.write(toWrite);
        }
    }

    protected static void outputTextToWriter(char[] text, OutputStream writer) throws IOException {
        int c;
        byte[] toWrite;
        int length = text.length;
        int i = 0;
        while (i < length) {
            if (Character.isHighSurrogate(text[i]) && i + 1 != length && Character.isLowSurrogate(text[i + 1])) {
                char c2 = text[i];
                i++;
                c = Character.toCodePoint(c2, text[i]);
            } else {
                c = text[i];
            }
            switch (c) {
                case 13:
                    toWrite = __XD_;
                    break;
                case EACTags.CARD_EFFECTIVE_DATE /* 38 */:
                    toWrite = _AMP_;
                    break;
                case 60:
                    toWrite = _LT_;
                    break;
                case CipherSuite.TLS_DH_DSS_WITH_AES_128_CBC_SHA256 /* 62 */:
                    toWrite = _GT_;
                    break;
                default:
                    if (c < 128) {
                        writer.write(c);
                        continue;
                    } else {
                        UtfHelpper.writeCodePointToUtf8(c, writer);
                    }
                    i++;
                    break;
            }
            writer.write(toWrite);
            i++;
        }
    }

    protected static void outputPItoWriter(XMLSecProcessingInstruction currentPI, OutputStream writer, DocumentLevel position) throws IOException {
        if (position == DocumentLevel.NODE_AFTER_DOCUMENT_ELEMENT) {
            writer.write(NEWLINE);
        }
        writer.write(_BEGIN_PI);
        String target = currentPI.getTarget();
        int length = target.length();
        int i = 0;
        while (i < length) {
            int c = target.codePointAt(i);
            i += Character.charCount(c);
            if (c == 13) {
                writer.write(__XD_);
            } else if (c < 128) {
                writer.write(c);
            } else {
                UtfHelpper.writeCodePointToUtf8(c, writer);
            }
        }
        String data = currentPI.getData();
        int length2 = data.length();
        if (length2 > 0) {
            writer.write(32);
            int i2 = 0;
            while (i2 < length2) {
                int c2 = data.codePointAt(i2);
                i2 += Character.charCount(c2);
                if (c2 == 13) {
                    writer.write(__XD_);
                } else {
                    UtfHelpper.writeCodePointToUtf8(c2, writer);
                }
            }
        }
        writer.write(_END_PI);
        if (position == DocumentLevel.NODE_BEFORE_DOCUMENT_ELEMENT) {
            writer.write(NEWLINE);
        }
    }

    protected static void outputCommentToWriter(XMLSecComment currentComment, OutputStream writer, DocumentLevel position) throws IOException {
        if (position == DocumentLevel.NODE_AFTER_DOCUMENT_ELEMENT) {
            writer.write(NEWLINE);
        }
        writer.write(_BEGIN_COMM);
        String data = currentComment.getText();
        int length = data.length();
        int i = 0;
        while (i < length) {
            int c = data.codePointAt(i);
            i += Character.charCount(c);
            if (c == 13) {
                writer.write(__XD_);
            } else if (c < 128) {
                writer.write(c);
            } else {
                UtfHelpper.writeCodePointToUtf8(c, writer);
            }
        }
        writer.write(_END_COMM);
        if (position == DocumentLevel.NODE_BEFORE_DOCUMENT_ELEMENT) {
            writer.write(NEWLINE);
        }
    }

    private boolean namespaceIsAbsolute(String namespaceValue) {
        return namespaceValue.isEmpty() || namespaceValue.indexOf(58) > 0;
    }

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/stax/impl/transformer/canonicalizer/CanonicalizerBase$C14NStack.class */
    public static class C14NStack<E> extends ArrayDeque<List<Comparable>> {
        public Object containsOnStack(Object o) {
            int idx;
            Iterator<E> it = super.iterator();
            while (it.hasNext()) {
                List list = (List) it.next();
                if (!list.isEmpty() && (idx = list.lastIndexOf(o)) != -1) {
                    return list.get(idx);
                }
            }
            return null;
        }

        @Override // java.util.ArrayDeque, java.util.Deque, java.util.Queue
        public List<Comparable> peek() {
            List<Comparable> list = (List) super.peekFirst();
            if (list == Collections.emptyList()) {
                super.removeFirst();
                list = new ArrayList();
                super.addFirst(list);
            }
            return list;
        }

        @Override // java.util.ArrayDeque, java.util.Deque
        public List<Comparable> peekFirst() {
            throw new UnsupportedOperationException("Use peek()");
        }
    }
}
