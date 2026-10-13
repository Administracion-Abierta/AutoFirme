package org.spongycastle.crypto.util;

import java.io.IOException;
import org.spongycastle.asn1.ASN1EncodableVector;
import org.spongycastle.asn1.ASN1OctetString;
import org.spongycastle.asn1.ASN1TaggedObject;
import org.spongycastle.asn1.DERSequence;
import org.spongycastle.asn1.DERTaggedObject;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/util/DEROtherInfo.class */
public class DEROtherInfo {
    private final DERSequence sequence;

    /* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/util/DEROtherInfo$Builder.class */
    public static final class Builder {
        private final AlgorithmIdentifier algorithmID;
        private final ASN1OctetString partyUVInfo;
        private final ASN1OctetString partyVInfo;
        private ASN1TaggedObject suppPubInfo;
        private ASN1TaggedObject suppPrivInfo;

        public Builder(AlgorithmIdentifier algorithmID, byte[] partyUInfo, byte[] partyVInfo) {
            this.algorithmID = algorithmID;
            this.partyUVInfo = DerUtil.getOctetString(partyUInfo);
            this.partyVInfo = DerUtil.getOctetString(partyVInfo);
        }

        public Builder withSuppPubInfo(byte[] suppPubInfo) {
            this.suppPubInfo = new DERTaggedObject(false, 0, DerUtil.getOctetString(suppPubInfo));
            return this;
        }

        public Builder withSuppPrivInfo(byte[] suppPrivInfo) {
            this.suppPrivInfo = new DERTaggedObject(false, 1, DerUtil.getOctetString(suppPrivInfo));
            return this;
        }

        public DEROtherInfo build() {
            ASN1EncodableVector v = new ASN1EncodableVector();
            v.add(this.algorithmID);
            v.add(this.partyUVInfo);
            v.add(this.partyVInfo);
            if (this.suppPubInfo != null) {
                v.add(this.suppPubInfo);
            }
            if (this.suppPrivInfo != null) {
                v.add(this.suppPrivInfo);
            }
            return new DEROtherInfo(new DERSequence(v));
        }
    }

    private DEROtherInfo(DERSequence sequence) {
        this.sequence = sequence;
    }

    public byte[] getEncoded() throws IOException {
        return this.sequence.getEncoded();
    }
}
