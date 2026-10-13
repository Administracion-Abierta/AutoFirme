package org.spongycastle.crypto.tls;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/DTLSReplayWindow.class */
class DTLSReplayWindow {
    private static final long VALID_SEQ_MASK = 281474976710655L;
    private static final long WINDOW_SIZE = 64;
    private long latestConfirmedSeq = -1;
    private long bitmap = 0;

    DTLSReplayWindow() {
    }

    boolean shouldDiscard(long seq) {
        if ((seq & VALID_SEQ_MASK) != seq) {
            return true;
        }
        if (seq <= this.latestConfirmedSeq) {
            long diff = this.latestConfirmedSeq - seq;
            if (diff >= WINDOW_SIZE || (this.bitmap & (1 << ((int) diff))) != 0) {
                return true;
            }
            return false;
        }
        return false;
    }

    void reportAuthenticated(long seq) {
        if ((seq & VALID_SEQ_MASK) != seq) {
            throw new IllegalArgumentException("'seq' out of range");
        }
        if (seq <= this.latestConfirmedSeq) {
            long diff = this.latestConfirmedSeq - seq;
            if (diff < WINDOW_SIZE) {
                this.bitmap |= 1 << ((int) diff);
                return;
            }
            return;
        }
        long diff2 = seq - this.latestConfirmedSeq;
        if (diff2 >= WINDOW_SIZE) {
            this.bitmap = 1L;
        } else {
            this.bitmap <<= (int) diff2;
            this.bitmap |= 1;
        }
        this.latestConfirmedSeq = seq;
    }

    void reset() {
        this.latestConfirmedSeq = -1L;
        this.bitmap = 0L;
    }
}
