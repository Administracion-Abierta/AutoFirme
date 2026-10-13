package org.spongycastle.crypto.agreement.jpake;

import java.math.BigInteger;
import org.apache.pdfbox.pdmodel.common.PDPageLabelRange;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/agreement/jpake/JPAKERound2Payload.class */
public class JPAKERound2Payload {
    private final String participantId;
    private final BigInteger a;
    private final BigInteger[] knowledgeProofForX2s;

    public JPAKERound2Payload(String participantId, BigInteger a, BigInteger[] knowledgeProofForX2s) {
        JPAKEUtil.validateNotNull(participantId, "participantId");
        JPAKEUtil.validateNotNull(a, PDPageLabelRange.STYLE_LETTERS_LOWER);
        JPAKEUtil.validateNotNull(knowledgeProofForX2s, "knowledgeProofForX2s");
        this.participantId = participantId;
        this.a = a;
        this.knowledgeProofForX2s = Arrays.copyOf(knowledgeProofForX2s, knowledgeProofForX2s.length);
    }

    public String getParticipantId() {
        return this.participantId;
    }

    public BigInteger getA() {
        return this.a;
    }

    public BigInteger[] getKnowledgeProofForX2s() {
        return Arrays.copyOf(this.knowledgeProofForX2s, this.knowledgeProofForX2s.length);
    }
}
