package org.bouncycastle.crypto.agreement.ecjpake;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/agreement/ecjpake/ECJPAKERound3Payload.class */
public class ECJPAKERound3Payload {
    private final String participantId;
    private final BigInteger macTag;

    public ECJPAKERound3Payload(String str, BigInteger bigInteger) {
        this.participantId = str;
        this.macTag = bigInteger;
    }

    public String getParticipantId() {
        return this.participantId;
    }

    public BigInteger getMacTag() {
        return this.macTag;
    }
}
