package org.apache.xml.security.parser;

import java.io.InputStream;
import org.w3c.dom.Document;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/parser/XMLParser.class */
public interface XMLParser {
    Document parse(InputStream inputStream, boolean z) throws XMLParserException;
}
