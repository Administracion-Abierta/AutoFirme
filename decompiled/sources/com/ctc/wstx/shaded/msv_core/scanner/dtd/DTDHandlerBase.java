package com.ctc.wstx.shaded.msv_core.scanner.dtd;

import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/scanner/dtd/DTDHandlerBase.class */
public class DTDHandlerBase implements DTDEventListener {
    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void processingInstruction(String target, String data) throws SAXException {
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void setDocumentLocator(Locator loc) {
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void fatalError(SAXParseException e) throws SAXException {
        throw e;
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void error(SAXParseException e) throws SAXException {
        throw e;
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void warning(SAXParseException err) throws SAXException {
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void notationDecl(String name, String publicId, String systemId) throws SAXException {
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) throws SAXException {
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void endDTD() throws SAXException {
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void externalGeneralEntityDecl(String n, String p, String s) throws SAXException {
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void internalGeneralEntityDecl(String n, String v) throws SAXException {
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void externalParameterEntityDecl(String n, String p, String s) throws SAXException {
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void internalParameterEntityDecl(String n, String v) throws SAXException {
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void startDTD(InputEntity in) throws SAXException {
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void comment(String n) throws SAXException {
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void characters(char[] ch, int start, int length) throws SAXException {
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void startCDATA() throws SAXException {
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void endCDATA() throws SAXException {
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void startContentModel(String elementName, short contentModelType) throws SAXException {
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void endContentModel(String elementName, short contentModelType) throws SAXException {
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void attributeDecl(String elementName, String attributeName, String attributeType, String[] enumeration, short attributeUse, String defaultValue) throws SAXException {
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void childElement(String elementName, short occurence) throws SAXException {
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void mixedElement(String elementName) throws SAXException {
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void startModelGroup() throws SAXException {
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void endModelGroup(short occurence) throws SAXException {
    }

    @Override // com.ctc.wstx.shaded.msv_core.scanner.dtd.DTDEventListener
    public void connector(short connectorType) throws SAXException {
    }
}
