package org.apache.xml.security.c14n.implementations;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/c14n/implementations/Canonicalizer20010315WithComments.class */
public class Canonicalizer20010315WithComments extends Canonicalizer20010315 {
    public Canonicalizer20010315WithComments() {
        super(true);
    }

    @Override // org.apache.xml.security.c14n.CanonicalizerSpi
    public final String engineGetURI() {
        return "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments";
    }
}
