package nu.xom;

import org.apache.xml.security.c14n.implementations.CanonicalizerBase;
import org.xml.sax.Attributes;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/NonVerifyingHandler.class */
class NonVerifyingHandler extends XOMHandler {
    NonVerifyingHandler(NodeFactory nodeFactory) {
        super(nodeFactory);
    }

    @Override // nu.xom.XOMHandler, org.xml.sax.ContentHandler
    public void startElement(String str, String str2, String str3, Attributes attributes) {
        flushText();
        Element elementBuild = Element.build(str3, str, str2);
        if (this.parent == this.document) {
            this.document.setRootElement(elementBuild);
            this.inProlog = false;
        }
        this.current = elementBuild;
        this.parents.add(elementBuild);
        if (this.parent != this.document) {
            this.parent.fastInsertChild(elementBuild, this.parent.getChildCount());
        }
        String systemId = this.locator.getSystemId();
        if (systemId != null && !systemId.equals(this.documentBaseURI)) {
            elementBuild.setActualBaseURI(systemId);
        }
        int length = attributes.getLength();
        for (int i = 0; i < length; i++) {
            String qName = attributes.getQName(i);
            if (!qName.startsWith("xmlns:") && !qName.equals(CanonicalizerBase.XMLNS)) {
                elementBuild.fastAddAttribute(Attribute.build(qName, attributes.getURI(i), attributes.getValue(i), convertStringToType(attributes.getType(i)), attributes.getLocalName(i)));
            }
        }
        for (int i2 = 0; i2 < length; i2++) {
            String qName2 = attributes.getQName(i2);
            if (qName2.startsWith("xmlns:")) {
                String value = attributes.getValue(i2);
                String strSubstring = qName2.substring(6);
                if (!value.equals(elementBuild.getNamespaceURI(strSubstring))) {
                    elementBuild.addNamespaceDeclaration(strSubstring, value);
                }
            } else if (qName2.equals(CanonicalizerBase.XMLNS)) {
                String value2 = attributes.getValue(i2);
                if (!value2.equals(elementBuild.getNamespaceURI(""))) {
                    elementBuild.addNamespaceDeclaration("", value2);
                }
            }
        }
        this.parent = elementBuild;
    }

    @Override // nu.xom.XOMHandler, org.xml.sax.ContentHandler
    public void endElement(String str, String str2, String str3) {
        this.current = (ParentNode) this.parents.remove(this.parents.size() - 1);
        flushText();
        this.parent = this.current.getParent();
        if (this.parent.isDocument()) {
            ((Document) this.parent).setRootElement((Element) this.current);
        }
    }

    private void flushText() {
        if (this.buffer != null) {
            this.textString = this.buffer.toString();
            this.buffer = null;
        }
        if (this.textString != null) {
            this.parent.fastInsertChild(!this.inCDATA ? Text.build(this.textString) : CDATASection.build(this.textString), this.parent.getChildCount());
            this.textString = null;
        }
        this.inCDATA = false;
        this.finishedCDATA = false;
    }

    @Override // nu.xom.XOMHandler, org.xml.sax.ContentHandler
    public void processingInstruction(String str, String str2) {
        if (!this.inDTD) {
            flushText();
        } else if (!inInternalSubset()) {
            return;
        }
        ProcessingInstruction processingInstructionBuild = ProcessingInstruction.build(str, str2);
        if (this.inDTD) {
            this.internalDTDSubset.append("  ");
            this.internalDTDSubset.append(processingInstructionBuild.toXML());
            this.internalDTDSubset.append("\n");
        } else if (!this.inProlog) {
            this.parent.fastInsertChild(processingInstructionBuild, this.parent.getChildCount());
        } else {
            this.parent.fastInsertChild(processingInstructionBuild, this.position);
            this.position++;
        }
    }

    @Override // nu.xom.XOMHandler, org.xml.sax.ext.LexicalHandler
    public void startDTD(String str, String str2, String str3) {
        this.inDTD = true;
        DocType docTypeBuild = DocType.build(str, str2, str3);
        this.document.fastInsertChild(docTypeBuild, this.position);
        this.position++;
        this.internalDTDSubset = new StringBuffer();
        this.doctype = docTypeBuild;
    }

    @Override // nu.xom.XOMHandler, org.xml.sax.ext.LexicalHandler
    public void comment(char[] cArr, int i, int i2) {
        if (!this.inDTD) {
            flushText();
        } else if (!inInternalSubset()) {
            return;
        }
        Comment commentBuild = Comment.build(new String(cArr, i, i2));
        if (this.inDTD) {
            this.internalDTDSubset.append("  ");
            this.internalDTDSubset.append(commentBuild.toXML());
            this.internalDTDSubset.append("\n");
        } else if (!this.inProlog) {
            this.parent.fastInsertChild(commentBuild, this.parent.getChildCount());
        } else {
            this.parent.insertChild(commentBuild, this.position);
            this.position++;
        }
    }

    @Override // nu.xom.XOMHandler, org.xml.sax.ext.LexicalHandler
    public void endDTD() {
        this.inDTD = false;
        if (this.doctype != null) {
            this.doctype.fastSetInternalDTDSubset(this.internalDTDSubset.toString());
        }
    }
}
