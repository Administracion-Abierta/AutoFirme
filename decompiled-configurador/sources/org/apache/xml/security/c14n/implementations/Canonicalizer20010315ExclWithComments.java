package org.apache.xml.security.c14n.implementations;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/c14n/implementations/Canonicalizer20010315ExclWithComments.class */
public class Canonicalizer20010315ExclWithComments extends Canonicalizer20010315Excl {
    public Canonicalizer20010315ExclWithComments() {
        super(true);
    }

    @Override // org.apache.xml.security.c14n.CanonicalizerSpi
    public final String engineGetURI() {
        return "http://www.w3.org/2001/10/xml-exc-c14n#WithComments";
    }
}
