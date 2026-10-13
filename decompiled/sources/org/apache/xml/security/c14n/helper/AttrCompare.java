package org.apache.xml.security.c14n.helper;

import java.io.Serializable;
import java.util.Comparator;
import org.apache.xml.security.c14n.implementations.CanonicalizerBase;
import org.w3c.dom.Attr;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/c14n/helper/AttrCompare.class */
public class AttrCompare implements Comparator<Attr>, Serializable {
    private static final long serialVersionUID = -7113259629930576230L;
    private static final int ATTR0_BEFORE_ATTR1 = -1;
    private static final int ATTR1_BEFORE_ATTR0 = 1;
    private static final String XMLNS = "http://www.w3.org/2000/xmlns/";

    @Override // java.util.Comparator
    public int compare(Attr attr0, Attr attr1) {
        String namespaceURI0 = attr0.getNamespaceURI();
        String namespaceURI1 = attr1.getNamespaceURI();
        boolean isNamespaceAttr0 = "http://www.w3.org/2000/xmlns/".equals(namespaceURI0);
        boolean isNamespaceAttr1 = "http://www.w3.org/2000/xmlns/".equals(namespaceURI1);
        if (isNamespaceAttr0) {
            if (isNamespaceAttr1) {
                String localname0 = attr0.getLocalName();
                String localname1 = attr1.getLocalName();
                if (CanonicalizerBase.XMLNS.equals(localname0)) {
                    localname0 = "";
                }
                if (CanonicalizerBase.XMLNS.equals(localname1)) {
                    localname1 = "";
                }
                return localname0.compareTo(localname1);
            }
            return -1;
        }
        if (isNamespaceAttr1) {
            return 1;
        }
        if (namespaceURI0 == null) {
            if (namespaceURI1 == null) {
                String name0 = attr0.getName();
                String name1 = attr1.getName();
                return name0.compareTo(name1);
            }
            return -1;
        }
        if (namespaceURI1 == null) {
            return 1;
        }
        int a = namespaceURI0.compareTo(namespaceURI1);
        if (a != 0) {
            return a;
        }
        return attr0.getLocalName().compareTo(attr1.getLocalName());
    }
}
