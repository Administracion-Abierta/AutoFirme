package org.bouncycastle.crypto.agreement.jpake;

import java.math.BigInteger;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/agreement/jpake/JPAKERound3Payload.class */
public class JPAKERound3Payload {
    private final String participantId;
    private final BigInteger macTag;

    public JPAKERound3Payload(String str, BigInteger bigInteger) {
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
