package org.apache.xml.security.transforms.implementations;

import org.apache.xml.security.c14n.implementations.Canonicalizer20010315Excl;
import org.apache.xml.security.c14n.implementations.Canonicalizer20010315ExclWithComments;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/transforms/implementations/TransformC14NExclusiveWithComments.class */
public class TransformC14NExclusiveWithComments extends TransformC14NExclusive {
    @Override // org.apache.xml.security.transforms.implementations.TransformC14NExclusive, org.apache.xml.security.transforms.TransformSpi
    protected String engineGetURI() {
        return "http://www.w3.org/2001/10/xml-exc-c14n#WithComments";
    }

    @Override // org.apache.xml.security.transforms.implementations.TransformC14NExclusive
    protected Canonicalizer20010315Excl getCanonicalizer() {
        return new Canonicalizer20010315ExclWithComments();
    }
}
