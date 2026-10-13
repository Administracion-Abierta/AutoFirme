package org.spongycastle.asn1;

import java.io.IOException;
import org.spongycastle.crypto.tls.CipherSuite;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/asn1/DLTaggedObject.class */
public class DLTaggedObject extends ASN1TaggedObject {
    private static final byte[] ZERO_BYTES = new byte[0];

    public DLTaggedObject(boolean explicit, int tagNo, ASN1Encodable obj) {
        super(explicit, tagNo, obj);
    }

    @Override // org.spongycastle.asn1.ASN1Primitive
    boolean isConstructed() {
        if (this.empty || this.explicit) {
            return true;
        }
        ASN1Primitive primitive = this.obj.toASN1Primitive().toDLObject();
        return primitive.isConstructed();
    }

    @Override // org.spongycastle.asn1.ASN1Primitive
    int encodedLength() throws IOException {
        if (!this.empty) {
            int length = this.obj.toASN1Primitive().toDLObject().encodedLength();
            if (this.explicit) {
                return StreamUtil.calculateTagLength(this.tagNo) + StreamUtil.calculateBodyLength(length) + length;
            }
            return StreamUtil.calculateTagLength(this.tagNo) + (length - 1);
        }
        return StreamUtil.calculateTagLength(this.tagNo) + 1;
    }

    @Override // org.spongycastle.asn1.ASN1TaggedObject, org.spongycastle.asn1.ASN1Primitive
    void encode(ASN1OutputStream out) throws IOException {
        int flags;
        if (!this.empty) {
            ASN1Primitive primitive = this.obj.toASN1Primitive().toDLObject();
            if (this.explicit) {
                out.writeTag(CipherSuite.TLS_DH_RSA_WITH_AES_128_GCM_SHA256, this.tagNo);
                out.writeLength(primitive.encodedLength());
                out.writeObject(primitive);
                return;
            } else {
                if (primitive.isConstructed()) {
                    flags = 160;
                } else {
                    flags = 128;
                }
                out.writeTag(flags, this.tagNo);
                out.writeImplicitObject(primitive);
                return;
            }
        }
        out.writeEncoded(CipherSuite.TLS_DH_RSA_WITH_AES_128_GCM_SHA256, this.tagNo, ZERO_BYTES);
    }
}
