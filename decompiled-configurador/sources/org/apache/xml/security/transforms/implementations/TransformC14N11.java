package org.apache.xml.security.transforms.implementations;

import org.apache.xml.security.c14n.implementations.Canonicalizer11_OmitComments;
import org.apache.xml.security.c14n.implementations.Canonicalizer20010315;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/transforms/implementations/TransformC14N11.class */
public class TransformC14N11 extends TransformC14N {
    @Override // org.apache.xml.security.transforms.implementations.TransformC14N, org.apache.xml.security.transforms.TransformSpi
    protected String engineGetURI() {
        return "http://www.w3.org/2006/12/xml-c14n11";
    }

    @Override // org.apache.xml.security.transforms.implementations.TransformC14N
    protected Canonicalizer20010315 getCanonicalizer() {
        return new Canonicalizer11_OmitComments();
    }
}
