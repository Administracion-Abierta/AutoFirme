package com.aowagie.text.xml;

import es.gob.afirma.keystores.filters.PolicyIdFilter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/xml/XmlDomWriter.class */
public class XmlDomWriter {
    private PrintWriter fOut;
    private boolean fCanonical;
    private boolean fXML11;

    public void setCanonical(boolean canonical) {
        this.fCanonical = canonical;
    }

    public void setOutput(OutputStream stream, String encoding) throws UnsupportedEncodingException {
        if (encoding == null) {
            encoding = "UTF8";
        }
        Writer writer = new OutputStreamWriter(stream, encoding);
        this.fOut = new PrintWriter(writer);
    }

    public void setOutput(Writer writer) {
        this.fOut = writer instanceof PrintWriter ? (PrintWriter) writer : new PrintWriter(writer);
    }

    public void write(Node node) {
        if (node == null) {
            return;
        }
        short type = node.getNodeType();
        switch (type) {
            case 1:
                this.fOut.print('<');
                this.fOut.print(node.getNodeName());
                Attr[] attrs = sortAttributes(node.getAttributes());
                for (Attr attr : attrs) {
                    this.fOut.print(' ');
                    this.fOut.print(attr.getNodeName());
                    this.fOut.print("=\"");
                    normalizeAndPrint(attr.getNodeValue(), true);
                    this.fOut.print('\"');
                }
                this.fOut.print('>');
                this.fOut.flush();
                Node firstChild = node.getFirstChild();
                while (true) {
                    Node child = firstChild;
                    if (child != null) {
                        write(child);
                        firstChild = child.getNextSibling();
                    }
                    break;
                }
                break;
            case 3:
                normalizeAndPrint(node.getNodeValue(), false);
                this.fOut.flush();
                break;
            case 4:
                if (this.fCanonical) {
                    normalizeAndPrint(node.getNodeValue(), false);
                } else {
                    this.fOut.print("<![CDATA[");
                    this.fOut.print(node.getNodeValue());
                    this.fOut.print("]]>");
                }
                this.fOut.flush();
                break;
            case 5:
                if (this.fCanonical) {
                    Node firstChild2 = node.getFirstChild();
                    while (true) {
                        Node child2 = firstChild2;
                        if (child2 != null) {
                            write(child2);
                            firstChild2 = child2.getNextSibling();
                        }
                    }
                } else {
                    this.fOut.print('&');
                    this.fOut.print(node.getNodeName());
                    this.fOut.print(';');
                    this.fOut.flush();
                }
                break;
            case 7:
                this.fOut.print("<?");
                this.fOut.print(node.getNodeName());
                String data = node.getNodeValue();
                if (data != null && data.length() > 0) {
                    this.fOut.print(' ');
                    this.fOut.print(data);
                }
                this.fOut.print("?>");
                this.fOut.flush();
                break;
            case 8:
                if (!this.fCanonical) {
                    this.fOut.print("<!--");
                    String comment = node.getNodeValue();
                    if (comment != null && comment.length() > 0) {
                        this.fOut.print(comment);
                    }
                    this.fOut.print("-->");
                    this.fOut.flush();
                }
                break;
            case 9:
                Document document = (Document) node;
                this.fXML11 = false;
                if (!this.fCanonical) {
                    if (this.fXML11) {
                        this.fOut.println("<?xml version=\"1.1\" encoding=\"UTF-8\"?>");
                    } else {
                        this.fOut.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
                    }
                    this.fOut.flush();
                    write(document.getDoctype());
                }
                write(document.getDocumentElement());
                break;
            case 10:
                DocumentType doctype = (DocumentType) node;
                this.fOut.print("<!DOCTYPE ");
                this.fOut.print(doctype.getName());
                String publicId = doctype.getPublicId();
                String systemId = doctype.getSystemId();
                if (publicId != null) {
                    this.fOut.print(" PUBLIC '");
                    this.fOut.print(publicId);
                    this.fOut.print("' '");
                    this.fOut.print(systemId);
                    this.fOut.print('\'');
                } else if (systemId != null) {
                    this.fOut.print(" SYSTEM '");
                    this.fOut.print(systemId);
                    this.fOut.print('\'');
                }
                String internalSubset = doctype.getInternalSubset();
                if (internalSubset != null) {
                    this.fOut.println(" [");
                    this.fOut.print(internalSubset);
                    this.fOut.print(']');
                }
                this.fOut.println('>');
                break;
        }
        if (type == 1) {
            this.fOut.print("</");
            this.fOut.print(node.getNodeName());
            this.fOut.print('>');
            this.fOut.flush();
        }
    }

    private Attr[] sortAttributes(NamedNodeMap attrs) {
        int len = attrs != null ? attrs.getLength() : 0;
        Attr[] array = new Attr[len];
        for (int i = 0; i < len; i++) {
            array[i] = (Attr) attrs.item(i);
        }
        for (int i2 = 0; i2 < len - 1; i2++) {
            String name = array[i2].getNodeName();
            int index = i2;
            for (int j = i2 + 1; j < len; j++) {
                String curName = array[j].getNodeName();
                if (curName.compareTo(name) < 0) {
                    name = curName;
                    index = j;
                }
            }
            if (index != i2) {
                Attr temp = array[i2];
                array[i2] = array[index];
                array[index] = temp;
            }
        }
        return array;
    }

    private void normalizeAndPrint(String s, boolean isAttValue) {
        int len = s != null ? s.length() : 0;
        for (int i = 0; i < len; i++) {
            char c = s.charAt(i);
            normalizeAndPrint(c, isAttValue);
        }
    }

    private void normalizeAndPrint(char c, boolean isAttValue) {
        switch (c) {
            case '\n':
                if (this.fCanonical) {
                    this.fOut.print("&#xA;");
                    return;
                }
                break;
            case '\r':
                this.fOut.print("&#xD;");
                return;
            case '\"':
                if (isAttValue) {
                    this.fOut.print("&quot;");
                    return;
                } else {
                    this.fOut.print(OperatorName.SHOW_TEXT_LINE_AND_SPACE);
                    return;
                }
            case '&':
                this.fOut.print("&amp;");
                return;
            case '<':
                this.fOut.print("&lt;");
                return;
            case '>':
                this.fOut.print("&gt;");
                return;
        }
        if ((this.fXML11 && ((c >= 1 && c <= 31 && c != '\t' && c != '\n') || ((c >= 127 && c <= 159) || c == 8232))) || (isAttValue && (c == '\t' || c == '\n'))) {
            this.fOut.print("&#x");
            this.fOut.print(Integer.toHexString(c).toUpperCase());
            this.fOut.print(PolicyIdFilter.OID_SEPARATOR);
            return;
        }
        this.fOut.print(c);
    }
}
