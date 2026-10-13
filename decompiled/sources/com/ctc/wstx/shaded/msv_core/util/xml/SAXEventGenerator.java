package com.ctc.wstx.shaded.msv_core.util.xml;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.LocatorImpl;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/util/xml/SAXEventGenerator.class */
public class SAXEventGenerator {
    public static void parse(Document dom, final ContentHandler handler) throws SAXException {
        DOMVisitor visitor = new DOMVisitor() { // from class: com.ctc.wstx.shaded.msv_core.util.xml.SAXEventGenerator.1
            @Override // com.ctc.wstx.shaded.msv_core.util.xml.DOMVisitor
            public void visit(Element e) {
                int attLen = e.getAttributes().getLength();
                AttributesImpl atts = new AttributesImpl();
                for (int i = 0; i < attLen; i++) {
                    Attr a = (Attr) e.getAttributes().item(i);
                    String uri = a.getNamespaceURI();
                    String local = a.getLocalName();
                    if (uri == null) {
                        uri = "";
                    }
                    if (local == null) {
                        local = a.getName();
                    }
                    atts.addAttribute(uri, local, a.getName(), null, a.getValue());
                }
                try {
                    String uri2 = e.getNamespaceURI();
                    String local2 = e.getLocalName();
                    if (uri2 == null) {
                        uri2 = "";
                    }
                    if (local2 == null) {
                        local2 = e.getNodeName();
                    }
                    handler.startElement(uri2, local2, e.getNodeName(), atts);
                    super.visit(e);
                    handler.endElement(uri2, local2, e.getNodeName());
                } catch (SAXException x) {
                    throw new SAXWrapper(x);
                }
            }

            @Override // com.ctc.wstx.shaded.msv_core.util.xml.DOMVisitor
            public void visitNode(Node n) {
                if (n.getNodeType() == 3 || n.getNodeType() == 4) {
                    String text = n.getNodeValue();
                    try {
                        handler.characters(text.toCharArray(), 0, text.length());
                    } catch (SAXException x) {
                        throw new SAXWrapper(x);
                    }
                }
                super.visitNode(n);
            }
        };
        handler.setDocumentLocator(new LocatorImpl());
        handler.startDocument();
        try {
            visitor.visit(dom);
            handler.endDocument();
        } catch (SAXWrapper w) {
            throw w.e;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/util/xml/SAXEventGenerator$SAXWrapper.class */
    private static class SAXWrapper extends RuntimeException {
        SAXException e;

        SAXWrapper(SAXException e) {
            this.e = e;
        }
    }
}
