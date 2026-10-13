package es.gob.afirma.signers.xmldsig;

import es.gob.afirma.signers.xml.Utils;
import java.util.ArrayList;
import java.util.List;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.Transform;
import org.apache.xml.security.stax.ext.XMLSecurityConstants;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/xmldsig/XmlDSigUtil.class */
final class XmlDSigUtil {
    private XmlDSigUtil() {
    }

    static List<Reference> cleanReferencesList(List<Reference> list) {
        ArrayList arrayList = new ArrayList();
        if (list == null) {
            return arrayList;
        }
        for (Reference reference : list) {
            if (reference.getId() == null) {
                ArrayList arrayList2 = null;
                boolean z = false;
                for (Object obj : reference.getTransforms()) {
                    if (obj instanceof Transform) {
                        if (!"http://www.w3.org/2000/09/xmldsig#base64".equals(((Transform) obj).getAlgorithm())) {
                            if (arrayList2 == null) {
                                arrayList2 = new ArrayList();
                            }
                            arrayList2.add((Transform) obj);
                        } else {
                            z = true;
                        }
                    }
                }
                if (z) {
                    arrayList.add(Utils.getDOMFactory().newReference(reference.getURI(), reference.getDigestMethod(), arrayList2, reference.getType(), reference.getId()));
                } else {
                    arrayList.add(reference);
                }
            } else {
                arrayList.add(reference);
            }
        }
        return arrayList;
    }

    static String guessXmlDSigNamespacePrefix(Element element) {
        String str = new String(Utils.writeXML(element, null, null, null));
        int iCountSubstring = Utils.countSubstring(str, "<Signature");
        int iCountSubstring2 = Utils.countSubstring(str, "<ds:Signature");
        int iCountSubstring3 = Utils.countSubstring(str, "<dsig:Signature");
        int iCountSubstring4 = Utils.countSubstring(str, "<dsig11:Signature");
        if (iCountSubstring2 >= iCountSubstring && iCountSubstring2 >= iCountSubstring3 && iCountSubstring2 >= iCountSubstring4) {
            return "ds";
        }
        if (iCountSubstring >= iCountSubstring2 && iCountSubstring >= iCountSubstring3 && iCountSubstring >= iCountSubstring4) {
            return "";
        }
        if (iCountSubstring3 >= iCountSubstring && iCountSubstring3 >= iCountSubstring2 && iCountSubstring3 >= iCountSubstring4) {
            return XMLSecurityConstants.PREFIX_DSIG;
        }
        if (iCountSubstring4 >= iCountSubstring && iCountSubstring4 >= iCountSubstring3 && iCountSubstring4 >= iCountSubstring2) {
            return XMLSecurityConstants.PREFIX_DSIG11;
        }
        return "ds";
    }
}
