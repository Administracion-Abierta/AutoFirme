package org.bouncycastle.crypto.agreement.ecjpake;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Exceptions;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/crypto/agreement/ecjpake/ECJPAKEParticipant.class */
public class ECJPAKEParticipant {
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
    private String partnerParticipantId;
    private ECCurve.Fp ecCurve;
    private BigInteger ecca;
    private BigInteger eccb;
    private BigInteger q;
    private BigInteger h;
    private BigInteger n;
    private ECPoint g;
    private BigInteger x1;
    private BigInteger x2;
    private ECPoint gx1;
    private ECPoint gx2;
    private ECPoint gx3;
    private ECPoint gx4;
    private ECPoint b;
    private int state;

    public ECJPAKEParticipant(String str, char[] cArr) {
        this(str, cArr, ECJPAKECurves.NIST_P256);
    }

    public ECJPAKEParticipant(String str, char[] cArr, ECJPAKECurve eCJPAKECurve) {
        this(str, cArr, eCJPAKECurve, SHA256Digest.newInstance(), CryptoServicesRegistrar.getSecureRandom());
    }

    public ECJPAKEParticipant(String str, char[] cArr, ECJPAKECurve eCJPAKECurve, Digest digest, SecureRandom secureRandom) {
        ECJPAKEUtil.validateNotNull(str, "participantId");
        ECJPAKEUtil.validateNotNull(cArr, "password");
        ECJPAKEUtil.validateNotNull(eCJPAKECurve, "curve params");
        ECJPAKEUtil.validateNotNull(digest, "digest");
        ECJPAKEUtil.validateNotNull(secureRandom, "random");
        if (cArr.length == 0) {
            throw new IllegalArgumentException("Password must not be empty.");
        }
        this.participantId = str;
        this.password = Arrays.copyOf(cArr, cArr.length);
        this.ecCurve = eCJPAKECurve.getCurve();
        this.ecca = eCJPAKECurve.getA();
        this.eccb = eCJPAKECurve.getB();
        this.g = eCJPAKECurve.getG();
        this.h = eCJPAKECurve.getH();
        this.n = eCJPAKECurve.getN();
        this.q = eCJPAKECurve.getQ();
        this.digest = digest;
        this.random = secureRandom;
        this.state = 0;
    }

    public int getState() {
        return this.state;
    }

    public ECJPAKERound1Payload createRound1PayloadToSend() {
        if (this.state >= 10) {
            throw new IllegalStateException("Round1 payload already created for " + this.participantId);
        }
        this.x1 = ECJPAKEUtil.generateX1(this.n, this.random);
        this.x2 = ECJPAKEUtil.generateX1(this.n, this.random);
        this.gx1 = ECJPAKEUtil.calculateGx(this.g, this.x1);
        this.gx2 = ECJPAKEUtil.calculateGx(this.g, this.x2);
        ECSchnorrZKP eCSchnorrZKPCalculateZeroKnowledgeProof = ECJPAKEUtil.calculateZeroKnowledgeProof(this.g, this.n, this.x1, this.gx1, this.digest, this.participantId, this.random);
        ECSchnorrZKP eCSchnorrZKPCalculateZeroKnowledgeProof2 = ECJPAKEUtil.calculateZeroKnowledgeProof(this.g, this.n, this.x2, this.gx2, this.digest, this.participantId, this.random);
        this.state = 10;
        return new ECJPAKERound1Payload(this.participantId, this.gx1, this.gx2, eCSchnorrZKPCalculateZeroKnowledgeProof, eCSchnorrZKPCalculateZeroKnowledgeProof2);
    }

    public void validateRound1PayloadReceived(ECJPAKERound1Payload eCJPAKERound1Payload) throws CryptoException {
        if (this.state >= 20) {
            throw new IllegalStateException("Validation already attempted for round1 payload for" + this.participantId);
        }
        this.partnerParticipantId = eCJPAKERound1Payload.getParticipantId();
        this.gx3 = eCJPAKERound1Payload.getGx1();
        this.gx4 = eCJPAKERound1Payload.getGx2();
        ECSchnorrZKP knowledgeProofForX1 = eCJPAKERound1Payload.getKnowledgeProofForX1();
        ECSchnorrZKP knowledgeProofForX2 = eCJPAKERound1Payload.getKnowledgeProofForX2();
        ECJPAKEUtil.validateParticipantIdsDiffer(this.participantId, eCJPAKERound1Payload.getParticipantId());
        ECJPAKEUtil.validateZeroKnowledgeProof(this.g, this.gx3, knowledgeProofForX1, this.q, this.n, this.ecCurve, this.h, eCJPAKERound1Payload.getParticipantId(), this.digest);
        ECJPAKEUtil.validateZeroKnowledgeProof(this.g, this.gx4, knowledgeProofForX2, this.q, this.n, this.ecCurve, this.h, eCJPAKERound1Payload.getParticipantId(), this.digest);
        this.state = 20;
    }

    public ECJPAKERound2Payload createRound2PayloadToSend() {
        if (this.state >= 30) {
            throw new IllegalStateException("Round2 payload already created for " + this.participantId);
        }
        if (this.state < 20) {
            throw new IllegalStateException("Round1 payload must be validated prior to creating Round2 payload for " + this.participantId);
        }
        ECPoint eCPointCalculateGA = ECJPAKEUtil.calculateGA(this.gx1, this.gx3, this.gx4);
        BigInteger bigIntegerCalculateX2s = ECJPAKEUtil.calculateX2s(this.n, this.x2, calculateS());
        ECPoint eCPointCalculateA = ECJPAKEUtil.calculateA(eCPointCalculateGA, bigIntegerCalculateX2s);
        ECSchnorrZKP eCSchnorrZKPCalculateZeroKnowledgeProof = ECJPAKEUtil.calculateZeroKnowledgeProof(eCPointCalculateGA, this.n, bigIntegerCalculateX2s, eCPointCalculateA, this.digest, this.participantId, this.random);
        this.state = 30;
        return new ECJPAKERound2Payload(this.participantId, eCPointCalculateA, eCSchnorrZKPCalculateZeroKnowledgeProof);
    }

    public void validateRound2PayloadReceived(ECJPAKERound2Payload eCJPAKERound2Payload) throws CryptoException {
        if (this.state >= 40) {
            throw new IllegalStateException("Validation already attempted for round2 payload for" + this.participantId);
        }
        if (this.state < 20) {
            throw new IllegalStateException("Round1 payload must be validated prior to validating Round2 payload for " + this.participantId);
        }
        ECPoint eCPointCalculateGA = ECJPAKEUtil.calculateGA(this.gx3, this.gx1, this.gx2);
        this.b = eCJPAKERound2Payload.getA();
        ECSchnorrZKP knowledgeProofForX2s = eCJPAKERound2Payload.getKnowledgeProofForX2s();
        ECJPAKEUtil.validateParticipantIdsDiffer(this.participantId, eCJPAKERound2Payload.getParticipantId());
        ECJPAKEUtil.validateParticipantIdsEqual(this.partnerParticipantId, eCJPAKERound2Payload.getParticipantId());
        ECJPAKEUtil.validateZeroKnowledgeProof(eCPointCalculateGA, this.b, knowledgeProofForX2s, this.q, this.n, this.ecCurve, this.h, eCJPAKERound2Payload.getParticipantId(), this.digest);
        this.state = 40;
    }

    public BigInteger calculateKeyingMaterial() {
        if (this.state >= 50) {
            throw new IllegalStateException("Key already calculated for " + this.participantId);
        }
        if (this.state < 40) {
            throw new IllegalStateException("Round2 payload must be validated prior to creating key for " + this.participantId);
        }
        BigInteger bigIntegerCalculateS = calculateS();
        Arrays.fill(this.password, (char) 0);
        this.password = null;
        BigInteger bigIntegerCalculateKeyingMaterial = ECJPAKEUtil.calculateKeyingMaterial(this.n, this.gx4, this.x2, bigIntegerCalculateS, this.b);
        this.x1 = null;
        this.x2 = null;
        this.b = null;
        this.state = 50;
        return bigIntegerCalculateKeyingMaterial;
    }

    public ECJPAKERound3Payload createRound3PayloadToSend(BigInteger bigInteger) {
        if (this.state >= 60) {
            throw new IllegalStateException("Round3 payload already created for " + this.participantId);
        }
        if (this.state < 50) {
            throw new IllegalStateException("Keying material must be calculated prior to creating Round3 payload for " + this.participantId);
        }
        BigInteger bigIntegerCalculateMacTag = ECJPAKEUtil.calculateMacTag(this.participantId, this.partnerParticipantId, this.gx1, this.gx2, this.gx3, this.gx4, bigInteger, this.digest);
        this.state = 60;
        return new ECJPAKERound3Payload(this.participantId, bigIntegerCalculateMacTag);
    }

    public void validateRound3PayloadReceived(ECJPAKERound3Payload eCJPAKERound3Payload, BigInteger bigInteger) throws CryptoException {
        if (this.state >= 70) {
            throw new IllegalStateException("Validation already attempted for round3 payload for" + this.participantId);
        }
        if (this.state < 50) {
            throw new IllegalStateException("Keying material must be calculated validated prior to validating Round3 payload for " + this.participantId);
        }
        ECJPAKEUtil.validateParticipantIdsDiffer(this.participantId, eCJPAKERound3Payload.getParticipantId());
        ECJPAKEUtil.validateParticipantIdsEqual(this.partnerParticipantId, eCJPAKERound3Payload.getParticipantId());
        ECJPAKEUtil.validateMacTag(this.participantId, this.partnerParticipantId, this.gx1, this.gx2, this.gx3, this.gx4, bigInteger, this.digest, eCJPAKERound3Payload.getMacTag());
        this.gx1 = null;
        this.gx2 = null;
        this.gx3 = null;
        this.gx4 = null;
        this.state = 70;
    }

    private BigInteger calculateS() {
        try {
            return ECJPAKEUtil.calculateS(this.n, this.password);
        } catch (CryptoException e) {
            throw Exceptions.illegalStateException(e.getMessage(), e);
        }
    }
}
