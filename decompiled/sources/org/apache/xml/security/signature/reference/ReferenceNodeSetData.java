package org.apache.xml.security.signature.reference;

import java.util.Iterator;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/signature/reference/ReferenceNodeSetData.class */
public interface ReferenceNodeSetData extends ReferenceData {
    Iterator<Node> iterator();
}
