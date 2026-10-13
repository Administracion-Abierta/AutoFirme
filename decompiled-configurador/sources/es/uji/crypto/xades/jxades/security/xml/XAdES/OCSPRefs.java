package es.uji.crypto.xades.jxades.security.xml.XAdES;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/OCSPRefs.class */
public class OCSPRefs extends XAdESStructure {
    private List<OCSPRef> ocspRefs;

    public OCSPRefs(Node node, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        super(node, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
    }

    public List<OCSPRef> getOCSPRefs() {
        if (this.ocspRefs == null) {
            List<Element> elements = getChildElementsNS("OCSPRef");
            if (elements != null && elements.size() > 0) {
                this.ocspRefs = new ArrayList(elements.size());
                for (Element element : elements) {
                    this.ocspRefs.add(new OCSPRef(element, this.xadesPrefix, this.xadesNamespace, this.xmlSignaturePrefix));
                }
            } else {
                this.ocspRefs = Collections.emptyList();
            }
        }
        return this.ocspRefs;
    }
}
