package org.bouncycastle.asn1;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/asn1/BERSetParser.class */
public class BERSetParser implements ASN1SetParser {
    private ASN1StreamParser _parser;

    BERSetParser(ASN1StreamParser aSN1StreamParser) {
        this._parser = aSN1StreamParser;
    }

    @Override // org.bouncycastle.asn1.ASN1SetParser
    public ASN1Encodable readObject() throws IOException {
        return this._parser.readObject();
    }

    @Override // org.bouncycastle.asn1.InMemoryRepresentable
    public ASN1Primitive getLoadedObject() throws IOException {
        return parse(this._parser);
    }

    @Override // org.bouncycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        try {
            return getLoadedObject();
        } catch (IOException e) {
            throw new ASN1ParsingException(e.getMessage(), e);
        }
    }

    static BERSet parse(ASN1StreamParser aSN1StreamParser) throws IOException {
        return new BERSet(aSN1StreamParser.readVector());
    }
}
