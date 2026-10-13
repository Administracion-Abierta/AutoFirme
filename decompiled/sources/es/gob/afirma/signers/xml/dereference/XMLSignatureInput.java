package es.gob.afirma.signers.xml.dereference;

import com.aowagie.text.xml.XMLConstants;
import es.gob.afirma.core.misc.AOUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import nu.xom.canonical.Canonicalizer;
import nu.xom.converters.DOMConverter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/xml/dereference/XMLSignatureInput.class */
final class XMLSignatureInput {
    private Node subNode;
    private DocumentBuilderFactory dfactory;
    private InputStream inputOctetStreamProxy = null;
    private Set<Node> inputNodeSet = null;
    private final Node excludeNode = null;
    private final boolean excludeComments = false;
    private final boolean isNodeSet = false;
    private byte[] bytes = null;
    private final String mimeType = null;
    private final String sourceURI = null;
    private final List<NodeFilter> nodeFilters = new ArrayList();
    private final boolean needsToBeExpanded = false;

    public XMLSignatureInput(Node node) {
        this.subNode = null;
        this.subNode = node;
    }

    public boolean isNeedsToBeExpanded() {
        getClass();
        return false;
    }

    public Set<Node> getNodeSet() throws ParserConfigurationException, SAXException, IOException {
        return getNodeSet(false);
    }

    public Set<Node> getNodeSet(boolean z) throws ParserConfigurationException, SAXException, IOException {
        if (this.inputNodeSet != null) {
            return this.inputNodeSet;
        }
        if (this.inputOctetStreamProxy == null && this.subNode != null) {
            if (z) {
                XMLUtils.circumventBug2650(XMLUtils.getOwnerDocument(this.subNode));
            }
            this.inputNodeSet = new LinkedHashSet();
            Node node = this.subNode;
            Set<Node> set = this.inputNodeSet;
            Node node2 = this.excludeNode;
            getClass();
            XMLUtils.getSet(node, set, node2, false);
            return this.inputNodeSet;
        }
        if (isOctetStream()) {
            convertToNodes();
            LinkedHashSet linkedHashSet = new LinkedHashSet();
            XMLUtils.getSet(this.subNode, linkedHashSet, null, false);
            return linkedHashSet;
        }
        throw new RuntimeException("getNodeSet() called but no input data present");
    }

    public InputStream getOctetStream() throws IOException {
        if (this.inputOctetStreamProxy != null) {
            return this.inputOctetStreamProxy;
        }
        if (this.bytes != null) {
            this.inputOctetStreamProxy = new ByteArrayInputStream(this.bytes);
            return this.inputOctetStreamProxy;
        }
        return null;
    }

    public byte[] getBytes() throws IOException {
        byte[] bytesFromInputStream = getBytesFromInputStream();
        if (bytesFromInputStream != null) {
            return bytesFromInputStream;
        }
        if (getSubNode() instanceof Element) {
            return canonicalizeXml((Element) getSubNode(), "http://www.w3.org/TR/2001/REC-xml-c14n-20010315");
        }
        throw new IOException("Cannot get canonicalized bytes from non Element-type Node");
    }

    public boolean isNodeSet() {
        if (this.inputOctetStreamProxy == null && this.inputNodeSet != null) {
            return true;
        }
        getClass();
        return false;
    }

    public boolean isOctetStream() {
        return !(this.inputOctetStreamProxy == null && this.bytes == null) && this.inputNodeSet == null && this.subNode == null;
    }

    public String getMIMEType() {
        return this.mimeType;
    }

    public String getSourceURI() {
        return this.sourceURI;
    }

    public Node getSubNode() {
        return this.subNode;
    }

    boolean isExcludeComments() {
        getClass();
        return false;
    }

    private byte[] getBytesFromInputStream() throws IOException {
        if (this.bytes != null) {
            return this.bytes;
        }
        if (this.inputOctetStreamProxy == null) {
            return null;
        }
        try {
            this.bytes = AOUtil.getDataFromInputStream(this.inputOctetStreamProxy);
            return this.bytes;
        } finally {
            this.inputOctetStreamProxy.close();
        }
    }

    public List<NodeFilter> getNodeFilters() {
        return this.nodeFilters;
    }

    void convertToNodes() throws ParserConfigurationException, SAXException, IOException {
        if (this.dfactory == null) {
            this.dfactory = DocumentBuilderFactory.newInstance();
            this.dfactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, Boolean.TRUE.booleanValue());
            this.dfactory.setValidating(false);
            this.dfactory.setNamespaceAware(true);
        }
        DocumentBuilder documentBuilderNewDocumentBuilder = this.dfactory.newDocumentBuilder();
        try {
            try {
                documentBuilderNewDocumentBuilder.setErrorHandler(new ErrorHandler() { // from class: es.gob.afirma.signers.xml.dereference.XMLSignatureInput.1
                    @Override // org.xml.sax.ErrorHandler
                    public void warning(SAXParseException sAXParseException) throws SAXException {
                    }

                    @Override // org.xml.sax.ErrorHandler
                    public void error(SAXParseException sAXParseException) throws SAXException {
                    }

                    @Override // org.xml.sax.ErrorHandler
                    public void fatalError(SAXParseException sAXParseException) throws SAXException {
                    }
                });
                InputStream octetStream = getOctetStream();
                Throwable th = null;
                try {
                    try {
                        Document document = documentBuilderNewDocumentBuilder.parse(octetStream);
                        if (octetStream != null) {
                            if (0 != 0) {
                                try {
                                    octetStream.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            } else {
                                octetStream.close();
                            }
                        }
                        this.subNode = document;
                        if (this.inputOctetStreamProxy != null) {
                            this.inputOctetStreamProxy.close();
                        }
                        this.inputOctetStreamProxy = null;
                        this.bytes = null;
                    } catch (Throwable th3) {
                        th = th3;
                        throw th3;
                    }
                } catch (Throwable th4) {
                    if (octetStream != null) {
                        if (th != null) {
                            try {
                                octetStream.close();
                            } catch (Throwable th5) {
                                th.addSuppressed(th5);
                            }
                        } else {
                            octetStream.close();
                        }
                    }
                    throw th4;
                }
            } catch (SAXException e) {
                Logger.getLogger("es.gob.afirma").warning("No se ha encontrado un conjunto de nodos bien formado: " + e);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byteArrayOutputStream.write("<container>".getBytes(StandardCharsets.UTF_8));
                byteArrayOutputStream.write(getBytes());
                byteArrayOutputStream.write("</container>".getBytes(StandardCharsets.UTF_8));
                this.subNode = documentBuilderNewDocumentBuilder.parse(new ByteArrayInputStream(byteArrayOutputStream.toByteArray())).getDocumentElement().getFirstChild().getFirstChild();
                if (this.inputOctetStreamProxy != null) {
                    this.inputOctetStreamProxy.close();
                }
                this.inputOctetStreamProxy = null;
                this.bytes = null;
            }
        } catch (Throwable th6) {
            if (this.inputOctetStreamProxy != null) {
                this.inputOctetStreamProxy.close();
            }
            this.inputOctetStreamProxy = null;
            this.bytes = null;
            throw th6;
        }
    }

    private static byte[] canonicalizeXml(Element element, String str) throws IOException {
        nu.xom.Element elementConvert = DOMConverter.convert(element);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        new Canonicalizer(byteArrayOutputStream, str).write(elementConvert);
        return byteArrayOutputStream.toByteArray();
    }
}
