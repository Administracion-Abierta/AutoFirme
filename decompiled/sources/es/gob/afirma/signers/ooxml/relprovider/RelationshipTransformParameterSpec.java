package es.gob.afirma.signers.ooxml.relprovider;

import java.util.LinkedList;
import java.util.List;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/ooxml/relprovider/RelationshipTransformParameterSpec.class */
public final class RelationshipTransformParameterSpec implements TransformParameterSpec {
    private final List<String> sourceIds = new LinkedList();

    public void addRelationshipReference(String str) {
        this.sourceIds.add(str);
    }

    List<String> getSourceIds() {
        if (this.sourceIds != null) {
            return new LinkedList(this.sourceIds);
        }
        return null;
    }
}
