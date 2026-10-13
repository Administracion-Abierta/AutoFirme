package org.spongycastle.asn1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.spongycastle.util.encoders.Hex;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/asn1/DERApplicationSpecific.class */
public class DERApplicationSpecific extends ASN1ApplicationSpecific {
    DERApplicationSpecific(boolean isConstructed, int tag, byte[] octets) {
        super(isConstructed, tag, octets);
    }

    public DERApplicationSpecific(int tag, byte[] octets) {
        this(false, tag, octets);
    }

    public DERApplicationSpecific(int tag, ASN1Encodable object) throws IOException {
        this(true, tag, object);
    }

    public DERApplicationSpecific(boolean constructed, int tag, ASN1Encodable object) throws IOException {
        super(constructed || object.toASN1Primitive().isConstructed(), tag, getEncoding(constructed, object));
    }

    private static byte[] getEncoding(boolean explicit, ASN1Encodable object) throws IOException {
        byte[] data = object.toASN1Primitive().getEncoded("DER");
        if (explicit) {
            return data;
        }
        int lenBytes = getLengthOfHeader(data);
        byte[] tmp = new byte[data.length - lenBytes];
        System.arraycopy(data, lenBytes, tmp, 0, tmp.length);
        return tmp;
    }

    public DERApplicationSpecific(int tagNo, ASN1EncodableVector vec) {
        super(true, tagNo, getEncodedVector(vec));
    }

    private static byte[] getEncodedVector(ASN1EncodableVector vec) {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        for (int i = 0; i != vec.size(); i++) {
            try {
                bOut.write(((ASN1Object) vec.get(i)).getEncoded("DER"));
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
        out.writeEncoded(classBits, this.tag, this.octets);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[");
        if (isConstructed()) {
            sb.append("CONSTRUCTED ");
        }
        sb.append("APPLICATION ");
        sb.append(Integer.toString(getApplicationTag()));
        sb.append("]");
        if (this.octets != null) {
            sb.append(" #");
            sb.append(Hex.toHexString(this.octets));
        } else {
            sb.append(" #null");
        }
        sb.append(" ");
        return sb.toString();
    }
}
