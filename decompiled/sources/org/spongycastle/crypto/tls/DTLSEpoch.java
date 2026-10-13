package org.spongycastle.crypto.tls;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/crypto/tls/DTLSEpoch.class */
class DTLSEpoch {
    private final int epoch;
    private final TlsCipher cipher;
    private final DTLSReplayWindow replayWindow = new DTLSReplayWindow();
    private long sequenceNumber = 0;

    DTLSEpoch(int epoch, TlsCipher cipher) {
        if (epoch < 0) {
            throw new IllegalArgumentException("'epoch' must be >= 0");
        }
        if (cipher == null) {
            throw new IllegalArgumentException("'cipher' cannot be null");
        }
        this.epoch = epoch;
        this.cipher = cipher;
    }

    long allocateSequenceNumber() {
        long j = this.sequenceNumber;
        this.sequenceNumber = j + 1;
        return j;
    }

    TlsCipher getCipher() {
        return this.cipher;
    }

    int getEpoch() {
        return this.epoch;
    }

    DTLSReplayWindow getReplayWindow() {
        return this.replayWindow;
    }

    long getSequenceNumber() {
        return this.sequenceNumber;
    }
}
