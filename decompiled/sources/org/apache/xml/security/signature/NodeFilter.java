package org.apache.xml.security.signature;

import org.apache.xml.security.transforms.TransformationException;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/signature/NodeFilter.class */
public interface NodeFilter {
    int isNodeInclude(Node node) throws TransformationException;

    int isNodeIncludeDO(Node node, int i) throws TransformationException;
}
