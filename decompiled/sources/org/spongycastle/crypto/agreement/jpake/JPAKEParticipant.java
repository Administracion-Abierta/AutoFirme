package org.spongycastle.crypto.agreement.jpake;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.spongycastle.crypto.CryptoException;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/agreement/jpake/JPAKEParticipant.class */
public class JPAKEParticipant {
    public static final int STATE_INITIALIZED = 0;
    public static final int STATE_ROUND_1_CREATED = 10;
    public static final int STATE_ROUND_1_VALIDATED = 20;
    public static final int STATE_ROUND_2_CREATED = 30;
    public static final int STATE_ROUND_2_VALIDATED = 40;
    public static final int STATE_KEY_CALCULATED = 50;
    public static final int STATE_ROUND_3_CREATED = 60;
    public static final int STATE_ROUND_3_VALIDATED = 70;
    private final String participantId;
    private char[] password;
    private final Digest digest;
    private final SecureRandom random;
    private final BigInteger p;
    private final BigInteger q;
    private final BigInteger g;
    private String partnerParticipantId;
    private BigInteger x1;
    private BigInteger x2;
    private BigInteger gx1;
    private BigInteger gx2;
    private BigInteger gx3;
    private BigInteger gx4;
    private BigInteger b;
    private int state;

    public JPAKEParticipant(String participantId, char[] password) {
        this(participantId, password, JPAKEPrimeOrderGroups.NIST_3072);
    }

    public JPAKEParticipant(String participantId, char[] password, JPAKEPrimeOrderGroup group) {
        this(participantId, password, group, new SHA256Digest(), new SecureRandom());
    }

    public JPAKEParticipant(String participantId, char[] password, JPAKEPrimeOrderGroup group, Digest digest, SecureRandom random) {
        JPAKEUtil.validateNotNull(participantId, "participantId");
        JPAKEUtil.validateNotNull(password, "password");
        JPAKEUtil.validateNotNull(group, "p");
        JPAKEUtil.validateNotNull(digest, "digest");
        JPAKEUtil.validateNotNull(random, "random");
        if (password.length == 0) {
            throw new IllegalArgumentException("Password must not be empty.");
        }
        this.participantId = participantId;
        this.password = Arrays.copyOf(password, password.length);
        this.p = group.getP();
        this.q = group.getQ();
        this.g = group.getG();
        this.digest = digest;
        this.random = random;
        this.state = 0;
    }

    public int getState() {
        return this.state;
    }

    public JPAKERound1Payload createRound1PayloadToSend() {
        if (this.state >= 10) {
            throw new IllegalStateException("Round1 payload already created for " + this.participantId);
        }
        this.x1 = JPAKEUtil.generateX1(this.q, this.random);
        this.x2 = JPAKEUtil.generateX2(this.q, this.random);
        this.gx1 = JPAKEUtil.calculateGx(this.p, this.g, this.x1);
        this.gx2 = JPAKEUtil.calculateGx(this.p, this.g, this.x2);
        BigInteger[] knowledgeProofForX1 = JPAKEUtil.calculateZeroKnowledgeProof(this.p, this.q, this.g, this.gx1, this.x1, this.participantId, this.digest, this.random);
        BigInteger[] knowledgeProofForX2 = JPAKEUtil.calculateZeroKnowledgeProof(this.p, this.q, this.g, this.gx2, this.x2, this.participantId, this.digest, this.random);
        this.state = 10;
        return new JPAKERound1Payload(this.participantId, this.gx1, this.gx2, knowledgeProofForX1, knowledgeProofForX2);
    }

    public void validateRound1PayloadReceived(JPAKERound1Payload round1PayloadReceived) throws CryptoException {
        if (this.state >= 20) {
            throw new IllegalStateException("Validation already attempted for round1 payload for" + this.participantId);
        }
        this.partnerParticipantId = round1PayloadReceived.getParticipantId();
        this.gx3 = round1PayloadReceived.getGx1();
        this.gx4 = round1PayloadReceived.getGx2();
        BigInteger[] knowledgeProofForX3 = round1PayloadReceived.getKnowledgeProofForX1();
        BigInteger[] knowledgeProofForX4 = round1PayloadReceived.getKnowledgeProofForX2();
        JPAKEUtil.validateParticipantIdsDiffer(this.participantId, round1PayloadReceived.getParticipantId());
        JPAKEUtil.validateGx4(this.gx4);
        JPAKEUtil.validateZeroKnowledgeProof(this.p, this.q, this.g, this.gx3, knowledgeProofForX3, round1PayloadReceived.getParticipantId(), this.digest);
        JPAKEUtil.validateZeroKnowledgeProof(this.p, this.q, this.g, this.gx4, knowledgeProofForX4, round1PayloadReceived.getParticipantId(), this.digest);
        this.state = 20;
    }

    public JPAKERound2Payload createRound2PayloadToSend() {
        if (this.state >= 30) {
            throw new IllegalStateException("Round2 payload already created for " + this.participantId);
        }
        if (this.state < 20) {
            throw new IllegalStateException("Round1 payload must be validated prior to creating Round2 payload for " + this.participantId);
        }
        BigInteger gA = JPAKEUtil.calculateGA(this.p, this.gx1, this.gx3, this.gx4);
        BigInteger s = JPAKEUtil.calculateS(this.password);
        BigInteger x2s = JPAKEUtil.calculateX2s(this.q, this.x2, s);
        BigInteger A = JPAKEUtil.calculateA(this.p, this.q, gA, x2s);
        BigInteger[] knowledgeProofForX2s = JPAKEUtil.calculateZeroKnowledgeProof(this.p, this.q, gA, A, x2s, this.participantId, this.digest, this.random);
        this.state = 30;
        return new JPAKERound2Payload(this.participantId, A, knowledgeProofForX2s);
    }

    public void validateRound2PayloadReceived(JPAKERound2Payload round2PayloadReceived) throws CryptoException {
        if (this.state >= 40) {
            throw new IllegalStateException("Validation already attempted for round2 payload for" + this.participantId);
        }
        if (this.state < 20) {
            throw new IllegalStateException("Round1 payload must be validated prior to validating Round2 payload for " + this.participantId);
        }
        BigInteger gB = JPAKEUtil.calculateGA(this.p, this.gx3, this.gx1, this.gx2);
        this.b = round2PayloadReceived.getA();
        BigInteger[] knowledgeProofForX4s = round2PayloadReceived.getKnowledgeProofForX2s();
        JPAKEUtil.validateParticipantIdsDiffer(this.participantId, round2PayloadReceived.getParticipantId());
        JPAKEUtil.validateParticipantIdsEqual(this.partnerParticipantId, round2PayloadReceived.getParticipantId());
        JPAKEUtil.validateGa(gB);
        JPAKEUtil.validateZeroKnowledgeProof(this.p, this.q, gB, this.b, knowledgeProofForX4s, round2PayloadReceived.getParticipantId(), this.digest);
        this.state = 40;
    }

    public BigInteger calculateKeyingMaterial() {
        if (this.state >= 50) {
            throw new IllegalStateException("Key already calculated for " + this.participantId);
        }
        if (this.state < 40) {
            throw new IllegalStateException("Round2 payload must be validated prior to creating key for " + this.participantId);
        }
        BigInteger s = JPAKEUtil.calculateS(this.password);
        Arrays.fill(this.password, (char) 0);
        this.password = null;
        BigInteger keyingMaterial = JPAKEUtil.calculateKeyingMaterial(this.p, this.q, this.gx4, this.x2, s, this.b);
        this.x1 = null;
        this.x2 = null;
        this.b = null;
        this.state = 50;
        return keyingMaterial;
    }

    public JPAKERound3Payload createRound3PayloadToSend(BigInteger keyingMaterial) {
        if (this.state >= 60) {
            throw new IllegalStateException("Round3 payload already created for " + this.participantId);
        }
        if (this.state < 50) {
            throw new IllegalStateException("Keying material must be calculated prior to creating Round3 payload for " + this.participantId);
        }
        BigInteger macTag = JPAKEUtil.calculateMacTag(this.participantId, this.partnerParticipantId, this.gx1, this.gx2, this.gx3, this.gx4, keyingMaterial, this.digest);
        this.state = 60;
        return new JPAKERound3Payload(this.participantId, macTag);
    }

    public void validateRound3PayloadReceived(JPAKERound3Payload round3PayloadReceived, BigInteger keyingMaterial) throws CryptoException {
        if (this.state >= 70) {
            throw new IllegalStateException("Validation already attempted for round3 payload for" + this.participantId);
        }
        if (this.state < 50) {
            throw new IllegalStateException("Keying material must be calculated validated prior to validating Round3 payload for " + this.participantId);
        }
        JPAKEUtil.validateParticipantIdsDiffer(this.participantId, round3PayloadReceived.getParticipantId());
        JPAKEUtil.validateParticipantIdsEqual(this.partnerParticipantId, round3PayloadReceived.getParticipantId());
        JPAKEUtil.validateMacTag(this.participantId, this.partnerParticipantId, this.gx1, this.gx2, this.gx3, this.gx4, keyingMaterial, this.digest, round3PayloadReceived.getMacTag());
        this.gx1 = null;
        this.gx2 = null;
        this.gx3 = null;
        this.gx4 = null;
        this.state = 70;
    }
}
