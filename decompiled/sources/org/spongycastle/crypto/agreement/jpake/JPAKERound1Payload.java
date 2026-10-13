package org.spongycastle.crypto.agreement.jpake;

import java.math.BigInteger;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/agreement/jpake/JPAKERound1Payload.class */
public class JPAKERound1Payload {
    private final String participantId;
    private final BigInteger gx1;
    private final BigInteger gx2;
    private final BigInteger[] knowledgeProofForX1;
    private final BigInteger[] knowledgeProofForX2;

    public JPAKERound1Payload(String participantId, BigInteger gx1, BigInteger gx2, BigInteger[] knowledgeProofForX1, BigInteger[] knowledgeProofForX2) {
        JPAKEUtil.validateNotNull(participantId, "participantId");
        JPAKEUtil.validateNotNull(gx1, "gx1");
        JPAKEUtil.validateNotNull(gx2, "gx2");
        JPAKEUtil.validateNotNull(knowledgeProofForX1, "knowledgeProofForX1");
        JPAKEUtil.validateNotNull(knowledgeProofForX2, "knowledgeProofForX2");
        this.participantId = participantId;
        this.gx1 = gx1;
        this.gx2 = gx2;
        this.knowledgeProofForX1 = Arrays.copyOf(knowledgeProofForX1, knowledgeProofForX1.length);
        this.knowledgeProofForX2 = Arrays.copyOf(knowledgeProofForX2, knowledgeProofForX2.length);
    }

    public String getParticipantId() {
        return this.participantId;
    }

    public BigInteger getGx1() {
        return this.gx1;
    }

    public BigInteger getGx2() {
        return this.gx2;
    }

    public BigInteger[] getKnowledgeProofForX1() {
        return Arrays.copyOf(this.knowledgeProofForX1, this.knowledgeProofForX1.length);
    }

    public BigInteger[] getKnowledgeProofForX2() {
        return Arrays.copyOf(this.knowledgeProofForX2, this.knowledgeProofForX2.length);
    }
}
