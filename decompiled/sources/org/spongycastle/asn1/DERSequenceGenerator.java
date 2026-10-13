package org.spongycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/DERSequenceGenerator.class */
public class DERSequenceGenerator extends DERGenerator {
    private final ByteArrayOutputStream _bOut;

    public DERSequenceGenerator(OutputStream out) throws IOException {
        super(out);
        this._bOut = new ByteArrayOutputStream();
    }

    public DERSequenceGenerator(OutputStream out, int tagNo, boolean isExplicit) throws IOException {
        super(out, tagNo, isExplicit);
        this._bOut = new ByteArrayOutputStream();
    }

    public void addObject(ASN1Encodable object) throws IOException {
        object.toASN1Primitive().encode(new DEROutputStream(this._bOut));
    }

    @Override // org.spongycastle.asn1.ASN1Generator
    public OutputStream getRawOutputStream() {
        return this._bOut;
    }

    public void close() throws IOException {
        writeDEREncoded(48, this._bOut.toByteArray());
    }
}
