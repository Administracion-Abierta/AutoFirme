package es.uji.crypto.xades.jxades.security.xml.XAdES;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/CRLRefs.class */
public class CRLRefs extends XAdESStructure {
    private List<CRLRef> crlRefs;

    public CRLRefs(Node node, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        super(node, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
    }

    public List<CRLRef> getCRLRefs() {
        if (this.crlRefs == null) {
            List<Element> elements = getChildElementsNS("CRLRef");
            if (elements != null && elements.size() > 0) {
                this.crlRefs = new ArrayList(elements.size());
                for (Element element : elements) {
                    this.crlRefs.add(new CRLRef(element, this.xadesPrefix, this.xadesNamespace, this.xmlSignaturePrefix));
                }
            } else {
                this.crlRefs = Collections.emptyList();
            }
        }
        return this.crlRefs;
    }
}
