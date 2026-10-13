package org.spongycastle.cms;

import java.io.IOException;
import java.io.OutputStream;
import org.spongycastle.asn1.ASN1Encodable;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Sequence;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/cms/PKCS7ProcessableObject.class */
public class PKCS7ProcessableObject implements CMSTypedData {
    private final ASN1ObjectIdentifier type;
    private final ASN1Encodable structure;

    public PKCS7ProcessableObject(ASN1ObjectIdentifier type, ASN1Encodable structure) {
        this.type = type;
        this.structure = structure;
    }

    @Override // org.spongycastle.cms.CMSTypedData
    public ASN1ObjectIdentifier getContentType() {
        return this.type;
    }

    @Override // org.spongycastle.cms.CMSProcessable
    public void write(OutputStream cOut) throws IOException, CMSException {
        if (this.structure instanceof ASN1Sequence) {
            ASN1Sequence s = ASN1Sequence.getInstance(this.structure);
            for (ASN1Encodable enc : s) {
                cOut.write(enc.toASN1Primitive().getEncoded("DER"));
            }
            return;
        }
        byte[] encoded = this.structure.toASN1Primitive().getEncoded("DER");
        int index = 1;
        while ((encoded[index] & 255) > 127) {
            index++;
        }
        int index2 = index + 1;
        cOut.write(encoded, index2, encoded.length - index2);
    }

    @Override // org.spongycastle.cms.CMSProcessable
    public Object getContent() {
        return this.structure;
    }
}
