package org.apache.xml.security.signature;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.implementations.Canonicalizer11_OmitComments;
import org.apache.xml.security.c14n.implementations.Canonicalizer20010315OmitComments;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;
import org.apache.xml.security.parser.XMLParserException;
import org.apache.xml.security.utils.JavaUtils;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/signature/XMLSignatureInput.class */
public class XMLSignatureInput {
    private InputStream inputOctetStreamProxy;
    private Set<Node> inputNodeSet;
    private Node subNode;
    private Node excludeNode;
    private byte[] bytes;
    private String mimeType;
    private String sourceURI;
    private OutputStream outputStream;
    private String preCalculatedDigest;
    private boolean excludeComments = false;
    private boolean isNodeSet = false;
    private boolean secureValidation = true;
    private List<NodeFilter> nodeFilters = new ArrayList();
    private boolean needsToBeExpanded = false;

    public XMLSignatureInput(byte[] inputOctets) {
        this.bytes = inputOctets;
    }

    public XMLSignatureInput(InputStream inputOctetStream) {
        this.inputOctetStreamProxy = inputOctetStream;
    }

    public XMLSignatureInput(Node rootNode) {
        this.subNode = rootNode;
    }

    public XMLSignatureInput(Set<Node> inputNodeSet) {
        this.inputNodeSet = inputNodeSet;
    }

    public XMLSignatureInput(String preCalculatedDigest) {
        this.preCalculatedDigest = preCalculatedDigest;
    }

    public boolean isNeedsToBeExpanded() {
        return this.needsToBeExpanded;
    }

    public void setNeedsToBeExpanded(boolean needsToBeExpanded) {
        this.needsToBeExpanded = needsToBeExpanded;
    }

    public Set<Node> getNodeSet() throws IOException, XMLParserException {
        return getNodeSet(false);
    }

    public Set<Node> getInputNodeSet() {
        return this.inputNodeSet;
    }

    public Set<Node> getNodeSet(boolean circumvent) throws IOException, XMLParserException {
        if (this.inputNodeSet != null) {
            return this.inputNodeSet;
        }
        if (this.inputOctetStreamProxy == null && this.subNode != null) {
            if (circumvent) {
                XMLUtils.circumventBug2650(XMLUtils.getOwnerDocument(this.subNode));
            }
            this.inputNodeSet = new LinkedHashSet();
            XMLUtils.getSet(this.subNode, this.inputNodeSet, this.excludeNode, this.excludeComments);
            return this.inputNodeSet;
        }
        if (isOctetStream()) {
            convertToNodes();
            Set<Node> result = new LinkedHashSet<>();
            XMLUtils.getSet(this.subNode, result, null, false);
            return result;
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

    public InputStream getOctetStreamReal() {
        return this.inputOctetStreamProxy;
    }

    public byte[] getBytes() throws CanonicalizationException, IOException {
        byte[] inputBytes = getBytesFromInputStream();
        if (inputBytes != null) {
            return inputBytes;
        }
        if (isOctetStream() || isElement() || isNodeSet()) {
            Canonicalizer20010315OmitComments c14nizer = new Canonicalizer20010315OmitComments();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Throwable th = null;
            try {
                try {
                    c14nizer.engineCanonicalize(this, baos, this.secureValidation);
                    this.bytes = baos.toByteArray();
                    if (0 != 0) {
                        try {
                            baos.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        baos.close();
                    }
                } catch (Throwable th3) {
                    th = th3;
                    throw th3;
                }
            } catch (Throwable th4) {
                if (th != null) {
                    try {
                        baos.close();
                    } catch (Throwable th5) {
                        th.addSuppressed(th5);
                    }
                } else {
                    baos.close();
                }
                throw th4;
            }
        }
        return this.bytes;
    }

    public boolean isNodeSet() {
        return (this.inputOctetStreamProxy == null && this.inputNodeSet != null) || this.isNodeSet;
    }

    public boolean isElement() {
        return this.inputOctetStreamProxy == null && this.subNode != null && this.inputNodeSet == null && !this.isNodeSet;
    }

    public boolean isOctetStream() {
        return !(this.inputOctetStreamProxy == null && this.bytes == null) && this.inputNodeSet == null && this.subNode == null;
    }

    public boolean isOutputStreamSet() {
        return this.outputStream != null;
    }

    public boolean isByteArray() {
        return this.bytes != null && this.inputNodeSet == null && this.subNode == null;
    }

    public boolean isPreCalculatedDigest() {
        return this.preCalculatedDigest != null;
    }

    public boolean isInitialized() {
        return isOctetStream() || isNodeSet();
    }

    public String getMIMEType() {
        return this.mimeType;
    }

    public void setMIMEType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getSourceURI() {
        return this.sourceURI;
    }

    public void setSourceURI(String sourceURI) {
        this.sourceURI = sourceURI;
    }

    public String toString() {
        if (isNodeSet()) {
            return "XMLSignatureInput/NodeSet/" + this.inputNodeSet.size() + " nodes/" + getSourceURI();
        }
        if (isElement()) {
            return "XMLSignatureInput/Element/" + this.subNode + " exclude " + this.excludeNode + " comments:" + this.excludeComments + "/" + getSourceURI();
        }
        try {
            byte[] bytes = getBytes();
            return "XMLSignatureInput/OctetStream/" + (bytes != null ? bytes.length : 0) + " octets/" + getSourceURI();
        } catch (IOException | CanonicalizationException e) {
            return "XMLSignatureInput/OctetStream//" + getSourceURI();
        }
    }

    public String getHTMLRepresentation() throws XMLSignatureException {
        XMLSignatureInputDebugger db = new XMLSignatureInputDebugger(this);
        return db.getHTMLRepresentation();
    }

    public String getHTMLRepresentation(Set<String> inclusiveNamespaces) throws XMLSignatureException {
        XMLSignatureInputDebugger db = new XMLSignatureInputDebugger(this, inclusiveNamespaces);
        return db.getHTMLRepresentation();
    }

    public Node getExcludeNode() {
        return this.excludeNode;
    }

    public void setExcludeNode(Node excludeNode) {
        this.excludeNode = excludeNode;
    }

    public Node getSubNode() {
        return this.subNode;
    }

    public boolean isExcludeComments() {
        return this.excludeComments;
    }

    public void setExcludeComments(boolean excludeComments) {
        this.excludeComments = excludeComments;
    }

    public void updateOutputStream(OutputStream diOs) throws CanonicalizationException, IOException {
        updateOutputStream(diOs, false);
    }

    public void updateOutputStream(OutputStream diOs, boolean c14n11) throws CanonicalizationException, IOException {
        CanonicalizerBase c14nizer;
        if (diOs == this.outputStream) {
            return;
        }
        if (this.bytes != null) {
            diOs.write(this.bytes);
            return;
        }
        if (this.inputOctetStreamProxy == null) {
            if (c14n11) {
                c14nizer = new Canonicalizer11_OmitComments();
            } else {
                c14nizer = new Canonicalizer20010315OmitComments();
            }
            c14nizer.engineCanonicalize(this, diOs, this.secureValidation);
            return;
        }
        byte[] buffer = new byte[4096];
        while (true) {
            try {
                int bytesread = this.inputOctetStreamProxy.read(buffer);
                if (bytesread != -1) {
                    diOs.write(buffer, 0, bytesread);
                } else {
                    return;
                }
            } catch (IOException ex) {
                this.inputOctetStreamProxy.close();
                throw ex;
            }
        }
    }

    public void setOutputStream(OutputStream os) {
        this.outputStream = os;
    }

    private byte[] getBytesFromInputStream() throws IOException {
        if (this.bytes != null) {
            return this.bytes;
        }
        if (this.inputOctetStreamProxy == null) {
            return null;
        }
        try {
            this.bytes = JavaUtils.getBytesFromStream(this.inputOctetStreamProxy);
            return this.bytes;
        } finally {
            this.inputOctetStreamProxy.close();
        }
    }

    public void addNodeFilter(NodeFilter filter) throws IOException, XMLParserException {
        if (isOctetStream()) {
            convertToNodes();
        }
        this.nodeFilters.add(filter);
    }

    public List<NodeFilter> getNodeFilters() {
        return this.nodeFilters;
    }

    public void setNodeSet(boolean b) {
        this.isNodeSet = b;
    }

    private void convertToNodes() throws IOException, XMLParserException {
        try {
            Document doc = XMLUtils.read(getOctetStream(), this.secureValidation);
            this.subNode = doc;
        } finally {
            if (this.inputOctetStreamProxy != null) {
                this.inputOctetStreamProxy.close();
            }
            this.inputOctetStreamProxy = null;
            this.bytes = null;
        }
    }

    public boolean isSecureValidation() {
        return this.secureValidation;
    }

    public void setSecureValidation(boolean secureValidation) {
        this.secureValidation = secureValidation;
    }

    public String getPreCalculatedDigest() {
        return this.preCalculatedDigest;
    }
}
