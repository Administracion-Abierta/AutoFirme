package org.spongycastle.crypto.agreement.jpake;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/agreement/jpake/JPAKERound3Payload.class */
public class JPAKERound3Payload {
    private final String participantId;
    private final BigInteger macTag;

    public JPAKERound3Payload(String participantId, BigInteger magTag) {
        this.participantId = participantId;
        this.macTag = magTag;
    }

    public String getParticipantId() {
        return this.participantId;
    }

    public BigInteger getMacTag() {
        return this.macTag;
    }
}
