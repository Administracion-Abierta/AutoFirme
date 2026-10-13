package org.bouncycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/DERSequenceGenerator.class */
public class DERSequenceGenerator extends DERGenerator {
    private final ByteArrayOutputStream _bOut;

    public DERSequenceGenerator(OutputStream outputStream) throws IOException {
        super(outputStream);
        this._bOut = new ByteArrayOutputStream();
    }

    public DERSequenceGenerator(OutputStream outputStream, int i, boolean z) throws IOException {
        super(outputStream, i, z);
        this._bOut = new ByteArrayOutputStream();
    }

    public void addObject(ASN1Encodable aSN1Encodable) throws IOException {
        aSN1Encodable.toASN1Primitive().encodeTo(this._bOut, "DER");
    }

    public void addObject(ASN1Primitive aSN1Primitive) throws IOException {
        aSN1Primitive.encodeTo(this._bOut, "DER");
    }

    @Override // org.bouncycastle.asn1.ASN1Generator
    public OutputStream getRawOutputStream() {
        return this._bOut;
    }

    public void close() throws IOException {
        writeDEREncoded(48, this._bOut.toByteArray());
    }
}
