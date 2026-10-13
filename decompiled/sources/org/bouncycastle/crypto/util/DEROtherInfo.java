package org.bouncycastle.crypto.util;

import java.io.IOException;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/util/DEROtherInfo.class */
public class DEROtherInfo {
    private final DERSequence sequence;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/util/DEROtherInfo$Builder.class */
    public static final class Builder {
        private final AlgorithmIdentifier algorithmID;
        private final ASN1OctetString partyUInfo;
        private final ASN1OctetString partyVInfo;
        private ASN1TaggedObject suppPubInfo;
        private ASN1TaggedObject suppPrivInfo;

        public Builder(AlgorithmIdentifier algorithmIdentifier, byte[] bArr, byte[] bArr2) {
            this.algorithmID = algorithmIdentifier;
            this.partyUInfo = DerUtil.getOctetString(bArr);
            this.partyVInfo = DerUtil.getOctetString(bArr2);
        }

        public Builder withSuppPubInfo(byte[] bArr) {
            this.suppPubInfo = new DERTaggedObject(false, 0, (ASN1Encodable) DerUtil.getOctetString(bArr));
            return this;
        }

        public Builder withSuppPrivInfo(byte[] bArr) {
            this.suppPrivInfo = new DERTaggedObject(false, 1, (ASN1Encodable) DerUtil.getOctetString(bArr));
            return this;
        }

        public DEROtherInfo build() {
            ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
            aSN1EncodableVector.add(this.algorithmID);
            aSN1EncodableVector.add(this.partyUInfo);
            aSN1EncodableVector.add(this.partyVInfo);
            if (this.suppPubInfo != null) {
                aSN1EncodableVector.add(this.suppPubInfo);
            }
            if (this.suppPrivInfo != null) {
                aSN1EncodableVector.add(this.suppPrivInfo);
            }
            return new DEROtherInfo(new DERSequence(aSN1EncodableVector));
        }
    }

    private DEROtherInfo(DERSequence dERSequence) {
        this.sequence = dERSequence;
    }

    public byte[] getEncoded() throws IOException {
        return this.sequence.getEncoded();
    }
}
