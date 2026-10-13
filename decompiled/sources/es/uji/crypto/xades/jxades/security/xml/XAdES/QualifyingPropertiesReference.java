package es.uji.crypto.xades.jxades.security.xml.XAdES;

import es.gob.afirma.signers.xades.XAdESConstants;
import javax.xml.crypto.dom.DOMStructure;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/QualifyingPropertiesReference.class */
public class QualifyingPropertiesReference extends DOMStructure {
    public QualifyingPropertiesReference(Node node, String xadesNamespace) {
        super(node.getOwnerDocument().createElementNS(xadesNamespace, XAdESConstants.TAG_QUALIFYING_PROPERTIES_REFERENCE));
    }
}
