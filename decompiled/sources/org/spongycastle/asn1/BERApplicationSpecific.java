package org.spongycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/BERApplicationSpecific.class */
public class BERApplicationSpecific extends ASN1ApplicationSpecific {
    BERApplicationSpecific(boolean isConstructed, int tag, byte[] octets) {
        super(isConstructed, tag, octets);
    }

    public BERApplicationSpecific(int tag, ASN1Encodable object) throws IOException {
        this(true, tag, object);
    }

    public BERApplicationSpecific(boolean constructed, int tag, ASN1Encodable object) throws IOException {
        super(constructed || object.toASN1Primitive().isConstructed(), tag, getEncoding(constructed, object));
    }

    private static byte[] getEncoding(boolean explicit, ASN1Encodable object) throws IOException {
        byte[] data = object.toASN1Primitive().getEncoded("BER");
        if (explicit) {
            return data;
        }
        int lenBytes = getLengthOfHeader(data);
        byte[] tmp = new byte[data.length - lenBytes];
        System.arraycopy(data, lenBytes, tmp, 0, tmp.length);
        return tmp;
    }

    public BERApplicationSpecific(int tagNo, ASN1EncodableVector vec) {
        super(true, tagNo, getEncodedVector(vec));
    }

    private static byte[] getEncodedVector(ASN1EncodableVector vec) {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        for (int i = 0; i != vec.size(); i++) {
            try {
                bOut.write(((ASN1Object) vec.get(i)).getEncoded("BER"));
            } catch (IOException e) {
                throw new ASN1ParsingException("malformed object: " + e, e);
            }
        }
        return bOut.toByteArray();
    }

    @Override // org.spongycastle.asn1.ASN1ApplicationSpecific, org.spongycastle.asn1.ASN1Primitive
    void encode(ASN1OutputStream out) throws IOException {
        int classBits = 64;
        if (this.isConstructed) {
            classBits = 64 | 32;
        }
        out.writeTag(classBits, this.tag);
        out.write(128);
        out.write(this.octets);
        out.write(0);
        out.write(0);
    }
}
