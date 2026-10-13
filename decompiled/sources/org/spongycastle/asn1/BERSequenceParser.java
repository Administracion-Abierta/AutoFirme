package org.spongycastle.asn1;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/BERSequenceParser.class */
public class BERSequenceParser implements ASN1SequenceParser {
    private ASN1StreamParser _parser;

    BERSequenceParser(ASN1StreamParser parser) {
        this._parser = parser;
    }

    @Override // org.spongycastle.asn1.ASN1SequenceParser
    public ASN1Encodable readObject() throws IOException {
        return this._parser.readObject();
    }

    @Override // org.spongycastle.asn1.InMemoryRepresentable
    public ASN1Primitive getLoadedObject() throws IOException {
        return new BERSequence(this._parser.readVector());
    }

    @Override // org.spongycastle.asn1.ASN1Encodable
    public ASN1Primitive toASN1Primitive() {
        try {
            return getLoadedObject();
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }
}
