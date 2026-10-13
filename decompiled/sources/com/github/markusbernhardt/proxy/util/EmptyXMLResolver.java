package com.github.markusbernhardt.proxy.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirma.jar:com/github/markusbernhardt/proxy/util/EmptyXMLResolver.class */
public class EmptyXMLResolver implements EntityResolver {
    @Override // org.xml.sax.EntityResolver
    public InputSource resolveEntity(String arg0, String arg1) throws SAXException, IOException {
        return new InputSource(new ByteArrayInputStream("".getBytes()));
    }
}
