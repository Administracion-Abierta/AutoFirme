package com.ctc.wstx.shaded.msv_core.scanner.dtd;

import java.util.EventListener;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/scanner/dtd/DTDEventListener.class */
public interface DTDEventListener extends EventListener {
    public static final short CONTENT_MODEL_EMPTY = 0;
    public static final short CONTENT_MODEL_ANY = 1;
    public static final short CONTENT_MODEL_MIXED = 2;
    public static final short CONTENT_MODEL_CHILDREN = 3;
    public static final short USE_NORMAL = 0;
    public static final short USE_IMPLIED = 1;
    public static final short USE_FIXED = 2;
    public static final short USE_REQUIRED = 3;
    public static final short CHOICE = 0;
    public static final short SEQUENCE = 1;
    public static final short OCCURENCE_ZERO_OR_MORE = 0;
    public static final short OCCURENCE_ONE_OR_MORE = 1;
    public static final short OCCURENCE_ZERO_OR_ONE = 2;
    public static final short OCCURENCE_ONCE = 3;

    void setDocumentLocator(Locator locator);

    void processingInstruction(String str, String str2) throws SAXException;

    void notationDecl(String str, String str2, String str3) throws SAXException;

    void unparsedEntityDecl(String str, String str2, String str3, String str4) throws SAXException;

    void internalGeneralEntityDecl(String str, String str2) throws SAXException;

    void externalGeneralEntityDecl(String str, String str2, String str3) throws SAXException;

    void internalParameterEntityDecl(String str, String str2) throws SAXException;

    void externalParameterEntityDecl(String str, String str2, String str3) throws SAXException;

    void startDTD(InputEntity inputEntity) throws SAXException;

    void endDTD() throws SAXException;

    void comment(String str) throws SAXException;

    void characters(char[] cArr, int i, int i2) throws SAXException;

    void ignorableWhitespace(char[] cArr, int i, int i2) throws SAXException;

    void startCDATA() throws SAXException;

    void endCDATA() throws SAXException;

    void fatalError(SAXParseException sAXParseException) throws SAXException;

    void error(SAXParseException sAXParseException) throws SAXException;

    void warning(SAXParseException sAXParseException) throws SAXException;

    void startContentModel(String str, short s) throws SAXException;

    void endContentModel(String str, short s) throws SAXException;

    void attributeDecl(String str, String str2, String str3, String[] strArr, short s, String str4) throws SAXException;

    void childElement(String str, short s) throws SAXException;

    void mixedElement(String str) throws SAXException;

    void startModelGroup() throws SAXException;

    void endModelGroup(short s) throws SAXException;

    void connector(short s) throws SAXException;
}
