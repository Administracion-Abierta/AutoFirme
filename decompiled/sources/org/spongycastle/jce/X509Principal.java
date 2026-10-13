package org.spongycastle.jce;

import java.io.IOException;
import java.security.Principal;
import java.util.Hashtable;
import java.util.Vector;
import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x509.X509Name;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/jce/X509Principal.class */
public class X509Principal extends X509Name implements Principal {
    private static ASN1Sequence readSequence(ASN1InputStream aIn) throws IOException {
        try {
            return ASN1Sequence.getInstance(aIn.readObject());
        } catch (IllegalArgumentException e) {
            throw new IOException("not an ASN.1 Sequence: " + e);
        }
    }

    public X509Principal(byte[] bytes) throws IOException {
        super(readSequence(new ASN1InputStream(bytes)));
    }

    public X509Principal(X509Name name) {
        super((ASN1Sequence) name.toASN1Primitive());
    }

    public X509Principal(X500Name name) {
        super((ASN1Sequence) name.toASN1Primitive());
    }

    public X509Principal(Hashtable attributes) {
        super(attributes);
    }

    public X509Principal(Vector ordering, Hashtable attributes) {
        super(ordering, attributes);
    }

    public X509Principal(Vector oids, Vector values) {
        super(oids, values);
    }

    public X509Principal(String dirName) {
        super(dirName);
    }

    public X509Principal(boolean reverse, String dirName) {
        super(reverse, dirName);
    }

    public X509Principal(boolean reverse, Hashtable lookUp, String dirName) {
        super(reverse, lookUp, dirName);
    }

    @Override // java.security.Principal
    public String getName() {
        return toString();
    }

    @Override // org.spongycastle.asn1.ASN1Object, org.spongycastle.util.Encodable
    public byte[] getEncoded() {
        try {
            return getEncoded("DER");
        } catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
    }
}
