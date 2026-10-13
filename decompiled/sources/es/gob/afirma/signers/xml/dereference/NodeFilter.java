package es.gob.afirma.signers.xml.dereference;

import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/xml/dereference/NodeFilter.class */
interface NodeFilter {
    int isNodeInclude(Node node);

    int isNodeIncludeDO(Node node, int i);
}
