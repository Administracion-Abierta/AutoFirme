package org.bouncycastle.crypto.agreement.jpake;

import java.math.BigInteger;
import org.apache.pdfbox.pdmodel.common.PDPageLabelRange;
import org.bouncycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/agreement/jpake/JPAKERound2Payload.class */
public class JPAKERound2Payload {
    private final String participantId;
    private final BigInteger a;
    private final BigInteger[] knowledgeProofForX2s;

    public JPAKERound2Payload(String str, BigInteger bigInteger, BigInteger[] bigIntegerArr) {
        JPAKEUtil.validateNotNull(str, "participantId");
        JPAKEUtil.validateNotNull(bigInteger, PDPageLabelRange.STYLE_LETTERS_LOWER);
        JPAKEUtil.validateNotNull(bigIntegerArr, "knowledgeProofForX2s");
        this.participantId = str;
        this.a = bigInteger;
        this.knowledgeProofForX2s = Arrays.copyOf(bigIntegerArr, bigIntegerArr.length);
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
