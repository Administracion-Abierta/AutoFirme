package es.uji.crypto.xades.jxades.security.xml.XAdES;

import es.uji.crypto.xades.jxades.util.ObjectId;
import es.uji.crypto.xades.jxades.util.OccursRequirement;

/* JADX INFO: loaded from: autofirma.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/XadesElement.class */
public interface XadesElement {
    XAdES[] getXAdES();

    ObjectId getObjectId();

    String getElementName();

    OccursRequirement getOccursRequirement();

    XadesElement getParent();
}
