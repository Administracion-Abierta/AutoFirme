package es.gob.afirma.signers.ooxml.relprovider;

import java.security.Provider;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/ooxml/relprovider/OOXMLProvider.class */
public final class OOXMLProvider extends Provider {
    private static final long serialVersionUID = 1;
    public static final String RELATIONSHIP_TRANSFORM_PROVIDER_NAME = "OOXMLProvider";

    public OOXMLProvider() {
        super(RELATIONSHIP_TRANSFORM_PROVIDER_NAME, 1.0d, "OOXML Security Provider");
        put("TransformService.http://schemas.openxmlformats.org/package/2006/RelationshipTransform", RelationshipTransformService.class.getName());
        put("TransformService.http://schemas.openxmlformats.org/package/2006/RelationshipTransform MechanismType", "DOM");
    }
}
