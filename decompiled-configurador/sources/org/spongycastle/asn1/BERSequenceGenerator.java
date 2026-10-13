package org.spongycastle.asn1;

import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/BERSequenceGenerator.class */
public class BERSequenceGenerator extends BERGenerator {
    public BERSequenceGenerator(OutputStream out) throws IOException {
        super(out);
        writeBERHeader(48);
    }

    public BERSequenceGenerator(OutputStream out, int tagNo, boolean isExplicit) throws IOException {
        super(out, tagNo, isExplicit);
        writeBERHeader(48);
    }

    public void addObject(ASN1Encodable object) throws IOException {
        object.toASN1Primitive().encode(new BEROutputStream(this._out));
    }

    public void close() throws IOException {
        writeBEREnd();
    }
}
