package org.spongycastle.crypto.agreement;

import java.math.BigInteger;
import org.spongycastle.crypto.CipherParameters;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.digests.SM3Digest;
import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.crypto.params.ECPrivateKeyParameters;
import org.spongycastle.crypto.params.ParametersWithID;
import org.spongycastle.crypto.params.SM2KeyExchangePrivateParameters;
import org.spongycastle.crypto.params.SM2KeyExchangePublicParameters;
import org.spongycastle.math.ec.ECFieldElement;
import org.spongycastle.math.ec.ECPoint;
import org.spongycastle.util.Arrays;
import org.spongycastle.util.BigIntegers;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/agreement/SM2KeyExchange.class */
public class SM2KeyExchange {
    private final Digest digest;
    private byte[] userID;
    private ECPrivateKeyParameters staticKey;
    private ECPoint staticPubPoint;
    private ECPoint ephemeralPubPoint;
    private ECDomainParameters ecParams;
    private int curveLength;
    private int w;
    private ECPrivateKeyParameters ephemeralKey;
    private boolean initiator;

    public SM2KeyExchange() {
        this(new SM3Digest());
    }

    public SM2KeyExchange(Digest digest) {
        this.digest = digest;
    }

    public void init(CipherParameters privParam) {
        SM2KeyExchangePrivateParameters baseParam;
        if (privParam instanceof ParametersWithID) {
            baseParam = (SM2KeyExchangePrivateParameters) ((ParametersWithID) privParam).getParameters();
            this.userID = ((ParametersWithID) privParam).getID();
        } else {
            baseParam = (SM2KeyExchangePrivateParameters) privParam;
            this.userID = new byte[0];
        }
        this.initiator = baseParam.isInitiator();
        this.staticKey = baseParam.getStaticPrivateKey();
        this.ephemeralKey = baseParam.getEphemeralPrivateKey();
        this.ecParams = this.staticKey.getParameters();
        this.staticPubPoint = baseParam.getStaticPublicPoint();
        this.ephemeralPubPoint = baseParam.getEphemeralPublicPoint();
        this.curveLength = (this.ecParams.getCurve().getFieldSize() + 7) / 8;
        this.w = (this.ecParams.getCurve().getFieldSize() / 2) - 1;
    }

    public int getFieldSize() {
        return (this.staticKey.getParameters().getCurve().getFieldSize() + 7) / 8;
    }

    public byte[] calculateKey(int kLen, CipherParameters pubParam) {
        SM2KeyExchangePublicParameters otherPub;
        byte[] otherUserID;
        byte[] rv;
        if (pubParam instanceof ParametersWithID) {
            otherPub = (SM2KeyExchangePublicParameters) ((ParametersWithID) pubParam).getParameters();
            otherUserID = ((ParametersWithID) pubParam).getID();
        } else {
            otherPub = (SM2KeyExchangePublicParameters) pubParam;
            otherUserID = new byte[0];
        }
        byte[] za = getZ(this.digest, this.userID, this.staticPubPoint);
        byte[] zb = getZ(this.digest, otherUserID, otherPub.getStaticPublicKey().getQ());
        ECPoint U = calculateU(otherPub);
        if (this.initiator) {
            rv = kdf(U, za, zb, kLen);
        } else {
            rv = kdf(U, zb, za, kLen);
        }
        return rv;
    }

    public byte[][] calculateKeyWithConfirmation(int kLen, byte[] confirmationTag, CipherParameters pubParam) {
        SM2KeyExchangePublicParameters otherPub;
        byte[] otherUserID;
        if (pubParam instanceof ParametersWithID) {
            otherPub = (SM2KeyExchangePublicParameters) ((ParametersWithID) pubParam).getParameters();
            otherUserID = ((ParametersWithID) pubParam).getID();
        } else {
            otherPub = (SM2KeyExchangePublicParameters) pubParam;
            otherUserID = new byte[0];
        }
        if (this.initiator && confirmationTag == null) {
            throw new IllegalArgumentException("if initiating, confirmationTag must be set");
        }
        byte[] za = getZ(this.digest, this.userID, this.staticPubPoint);
        byte[] zb = getZ(this.digest, otherUserID, otherPub.getStaticPublicKey().getQ());
        ECPoint U = calculateU(otherPub);
        if (this.initiator) {
            byte[] rv = kdf(U, za, zb, kLen);
            byte[] inner = calculateInnerHash(this.digest, U, za, zb, this.ephemeralPubPoint, otherPub.getEphemeralPublicKey().getQ());
            byte[] s1 = S1(this.digest, U, inner);
            if (!Arrays.constantTimeAreEqual(s1, confirmationTag)) {
                throw new IllegalStateException("confirmation tag mismatch");
            }
            return new byte[][]{rv, S2(this.digest, U, inner)};
        }
        byte[] rv2 = kdf(U, zb, za, kLen);
        byte[] inner2 = calculateInnerHash(this.digest, U, zb, za, otherPub.getEphemeralPublicKey().getQ(), this.ephemeralPubPoint);
        return new byte[][]{rv2, S1(this.digest, U, inner2), S2(this.digest, U, inner2)};
    }

    private ECPoint calculateU(SM2KeyExchangePublicParameters otherPub) {
        BigInteger x1 = reduce(this.ephemeralPubPoint.getAffineXCoord().toBigInteger());
        BigInteger tA = this.staticKey.getD().add(x1.multiply(this.ephemeralKey.getD())).mod(this.ecParams.getN());
        BigInteger x2 = reduce(otherPub.getEphemeralPublicKey().getQ().getAffineXCoord().toBigInteger());
        ECPoint B0 = otherPub.getEphemeralPublicKey().getQ().multiply(x2).normalize();
        ECPoint B1 = otherPub.getStaticPublicKey().getQ().add(B0).normalize();
        return B1.multiply(this.ecParams.getH().multiply(tA)).normalize();
    }

    private byte[] kdf(ECPoint u, byte[] za, byte[] zb, int klen) {
        int ct = 1;
        int v = this.digest.getDigestSize() * 8;
        byte[] buf = new byte[this.digest.getDigestSize()];
        byte[] rv = new byte[(klen + 7) / 8];
        int off = 0;
        for (int i = 1; i <= ((klen + v) - 1) / v; i++) {
            addFieldElement(this.digest, u.getAffineXCoord());
            addFieldElement(this.digest, u.getAffineYCoord());
            this.digest.update(za, 0, za.length);
            this.digest.update(zb, 0, zb.length);
            this.digest.update((byte) (ct >> 24));
            this.digest.update((byte) (ct >> 16));
            this.digest.update((byte) (ct >> 8));
            this.digest.update((byte) ct);
            this.digest.doFinal(buf, 0);
            if (off + buf.length < rv.length) {
                System.arraycopy(buf, 0, rv, off, buf.length);
            } else {
                System.arraycopy(buf, 0, rv, off, rv.length - off);
            }
            off += buf.length;
            ct++;
        }
        return rv;
    }

    private BigInteger reduce(BigInteger x) {
        return x.and(BigInteger.valueOf(1L).shiftLeft(this.w).subtract(BigInteger.valueOf(1L))).setBit(this.w);
    }

    private byte[] S1(Digest digest, ECPoint u, byte[] inner) {
        byte[] rv = new byte[digest.getDigestSize()];
        digest.update((byte) 2);
        addFieldElement(digest, u.getAffineYCoord());
        digest.update(inner, 0, inner.length);
        digest.doFinal(rv, 0);
        return rv;
    }

    private byte[] calculateInnerHash(Digest digest, ECPoint u, byte[] za, byte[] zb, ECPoint p1, ECPoint p2) {
        addFieldElement(digest, u.getAffineXCoord());
        digest.update(za, 0, za.length);
        digest.update(zb, 0, zb.length);
        addFieldElement(digest, p1.getAffineXCoord());
        addFieldElement(digest, p1.getAffineYCoord());
        addFieldElement(digest, p2.getAffineXCoord());
        addFieldElement(digest, p2.getAffineYCoord());
        byte[] rv = new byte[digest.getDigestSize()];
        digest.doFinal(rv, 0);
        return rv;
    }

    private byte[] S2(Digest digest, ECPoint u, byte[] inner) {
        byte[] rv = new byte[digest.getDigestSize()];
        digest.update((byte) 3);
        addFieldElement(digest, u.getAffineYCoord());
        digest.update(inner, 0, inner.length);
        digest.doFinal(rv, 0);
        return rv;
    }

    private byte[] getZ(Digest digest, byte[] userID, ECPoint pubPoint) {
        addUserID(digest, userID);
        addFieldElement(digest, this.ecParams.getCurve().getA());
        addFieldElement(digest, this.ecParams.getCurve().getB());
        addFieldElement(digest, this.ecParams.getG().getAffineXCoord());
        addFieldElement(digest, this.ecParams.getG().getAffineYCoord());
        addFieldElement(digest, pubPoint.getAffineXCoord());
        addFieldElement(digest, pubPoint.getAffineYCoord());
        byte[] rv = new byte[digest.getDigestSize()];
        digest.doFinal(rv, 0);
        return rv;
    }

    private void addUserID(Digest digest, byte[] userID) {
        int len = userID.length * 8;
        digest.update((byte) ((len >> 8) & 255));
        digest.update((byte) (len & 255));
        digest.update(userID, 0, userID.length);
    }

    private void addFieldElement(Digest digest, ECFieldElement v) {
        byte[] p = BigIntegers.asUnsignedByteArray(this.curveLength, v.toBigInteger());
        digest.update(p, 0, p.length);
    }
}
