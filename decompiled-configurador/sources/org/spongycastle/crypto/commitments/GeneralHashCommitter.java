package org.spongycastle.crypto.commitments;

import java.security.SecureRandom;
import org.spongycastle.crypto.Commitment;
import org.spongycastle.crypto.Committer;
import org.spongycastle.crypto.DataLengthException;
import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.ExtendedDigest;
import org.spongycastle.util.Arrays;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/crypto/commitments/GeneralHashCommitter.class */
public class GeneralHashCommitter implements Committer {
    private final Digest digest;
    private final int byteLength;
    private final SecureRandom random;

    public GeneralHashCommitter(ExtendedDigest digest, SecureRandom random) {
        this.digest = digest;
        this.byteLength = digest.getByteLength();
        this.random = random;
    }

    @Override // org.spongycastle.crypto.Committer
    public Commitment commit(byte[] message) {
        if (message.length > this.byteLength / 2) {
            throw new DataLengthException("Message to be committed to too large for digest.");
        }
        byte[] w = new byte[this.byteLength - message.length];
        this.random.nextBytes(w);
        return new Commitment(w, calculateCommitment(w, message));
    }

    @Override // org.spongycastle.crypto.Committer
    public boolean isRevealed(Commitment commitment, byte[] message) {
        if (message.length + commitment.getSecret().length != this.byteLength) {
            throw new DataLengthException("Message and witness secret lengths do not match.");
        }
        byte[] calcCommitment = calculateCommitment(commitment.getSecret(), message);
        return Arrays.constantTimeAreEqual(commitment.getCommitment(), calcCommitment);
    }

    private byte[] calculateCommitment(byte[] w, byte[] message) {
        byte[] commitment = new byte[this.digest.getDigestSize()];
        this.digest.update(w, 0, w.length);
        this.digest.update(message, 0, message.length);
        this.digest.update((byte) (message.length >>> 8));
        this.digest.update((byte) message.length);
        this.digest.doFinal(commitment, 0);
        return commitment;
    }
}
