package org.apache.xml.security.utils;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/utils/IdResolver.class */
@Deprecated
public final class IdResolver {
    private IdResolver() {
    }

    public static void registerElementById(Element element, Attr id) {
        element.setIdAttributeNode(id, true);
    }

    public static Element getElementById(Document doc, String id) {
        return doc.getElementById(id);
    }
}
