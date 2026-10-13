package es.gob.afirma.signers.ooxml.relprovider;

import java.util.Comparator;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/ooxml/relprovider/RelationshipComparator.class */
final class RelationshipComparator implements Comparator<Element> {
    RelationshipComparator() {
    }

    @Override // java.util.Comparator
    public int compare(Element element, Element element2) {
        return element.getAttribute("Id").compareTo(element2.getAttribute("Id"));
    }
}
