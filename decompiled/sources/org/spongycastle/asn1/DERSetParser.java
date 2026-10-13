package org.spongycastle.asn1;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/DERSetParser.class */
public class DERSetParser implements ASN1SetParser {
    private ASN1StreamParser _parser;

    DERSetParser(ASN1StreamParser parser) {
        this._parser = parser;
    }

    @Override // org.spongycastle.asn1.ASN1SetParser
    public ASN1Encodable readObject() throws IOException {
        return this._parser.readObject();
    }

    @Override // org.spongycastle.asn1.InMemoryRepresentable
    public ASN1Primitive getLoadedObject() throws IOException {
        return new DERSet(this._parser.readVector(), false);
    }

    @Override // org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        try {
            return getLoadedObject();
        } catch (IOException e) {
            throw new ASN1ParsingException(e.getMessage(), e);
        }
    }
}
