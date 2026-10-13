package com.ctc.wstx.shaded.msv_core.verifier.psvi;

import com.ctc.wstx.shaded.msv.relaxng_datatype.Datatype;
import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.grammar.AttributeExp;
import com.ctc.wstx.shaded.msv_core.grammar.ElementExp;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/verifier/psvi/TypedContentHandler.class */
public interface TypedContentHandler {
    void startDocument(ValidationContext validationContext) throws SAXException;

    void endDocument() throws SAXException;

    void characterChunk(String str, Datatype datatype) throws SAXException;

    void startElement(String str, String str2, String str3) throws SAXException;

    void endElement(String str, String str2, String str3, ElementExp elementExp) throws SAXException;

    void startAttribute(String str, String str2, String str3) throws SAXException;

    void endAttribute(String str, String str2, String str3, AttributeExp attributeExp) throws SAXException;

    void endAttributePart() throws SAXException;
}
