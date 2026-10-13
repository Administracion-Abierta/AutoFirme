package org.bouncycastle.crypto.agreement.ecjpake;

import org.apache.pdfbox.pdmodel.common.PDPageLabelRange;
import org.bouncycastle.math.ec.ECPoint;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/agreement/ecjpake/ECJPAKERound2Payload.class */
public class ECJPAKERound2Payload {
    private final String participantId;
    private final ECPoint a;
    private final ECSchnorrZKP knowledgeProofForX2s;

    public ECJPAKERound2Payload(String str, ECPoint eCPoint, ECSchnorrZKP eCSchnorrZKP) {
        ECJPAKEUtil.validateNotNull(str, "participantId");
        ECJPAKEUtil.validateNotNull(eCPoint, PDPageLabelRange.STYLE_LETTERS_LOWER);
        ECJPAKEUtil.validateNotNull(eCSchnorrZKP, "knowledgeProofForX2s");
        this.participantId = str;
        this.a = eCPoint;
        this.knowledgeProofForX2s = eCSchnorrZKP;
    }

    public String getParticipantId() {
        return this.participantId;
    }

    public ECPoint getA() {
        return this.a;
    }

    public ECSchnorrZKP getKnowledgeProofForX2s() {
        return this.knowledgeProofForX2s;
    }
}
