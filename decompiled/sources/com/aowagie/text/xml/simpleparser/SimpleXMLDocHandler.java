package com.aowagie.text.xml.simpleparser;

import java.util.HashMap;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/xml/simpleparser/SimpleXMLDocHandler.class */
public interface SimpleXMLDocHandler {
    void startElement(String str, HashMap map);

    void endElement(String str);

    void startDocument();

    void endDocument();

    void text(String str);
}
