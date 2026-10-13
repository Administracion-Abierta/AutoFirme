package es.uji.crypto.xades.jxades.security.xml.XAdES;

import es.uji.crypto.xades.jxades.util.ObjectId;
import java.util.TreeMap;

/* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/XadesElementsEnumeration.class */
public class XadesElementsEnumeration extends TreeMap<ObjectId, XadesElement> {
    public XadesElementsEnumeration(XadesElement[] xadesElements, XAdES xadesFilter) {
        for (XadesElement element : xadesElements) {
            XAdES[] xadesProfiles = element.getXAdES();
            if (xadesProfiles != null) {
                for (XAdES xades : xadesProfiles) {
                    if (xades != null && xades.equals(xadesFilter)) {
                        put(element.getObjectId(), element);
                        break;
                    }
                }
            }
        }
    }

    public XadesElementsEnumeration(XadesElement[] xadesElements) {
        for (XadesElement element : xadesElements) {
            put(element.getObjectId(), element);
        }
    }
}
