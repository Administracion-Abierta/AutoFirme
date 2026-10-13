package com.ctc.wstx.io;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/io/BufferRecycler.class */
public final class BufferRecycler {
    private volatile char[] mSmallCBuffer = null;
    private volatile char[] mMediumCBuffer = null;
    private volatile char[] mFullCBuffer = null;
    private volatile byte[] mFullBBuffer = null;

    public synchronized char[] getSmallCBuffer(int minSize) {
        char[] result = this.mSmallCBuffer;
        if (result != null && result.length >= minSize) {
            this.mSmallCBuffer = null;
            return result;
        }
        return null;
    }

    public synchronized void returnSmallCBuffer(char[] buffer) {
        this.mSmallCBuffer = buffer;
    }

    public synchronized char[] getMediumCBuffer(int minSize) {
        char[] result = this.mMediumCBuffer;
        if (result != null && result.length >= minSize) {
            this.mMediumCBuffer = null;
            return result;
        }
        return null;
    }

    public synchronized void returnMediumCBuffer(char[] buffer) {
        this.mMediumCBuffer = buffer;
    }

    public synchronized char[] getFullCBuffer(int minSize) {
        char[] result = this.mFullCBuffer;
        if (result != null && result.length >= minSize) {
            this.mFullCBuffer = null;
            return result;
        }
        return null;
    }

    public synchronized void returnFullCBuffer(char[] buffer) {
        this.mFullCBuffer = buffer;
    }

    public synchronized byte[] getFullBBuffer(int minSize) {
        byte[] result = this.mFullBBuffer;
        if (result != null && result.length >= minSize) {
            this.mFullBBuffer = null;
            return result;
        }
        return null;
    }

    public synchronized void returnFullBBuffer(byte[] buffer) {
        this.mFullBBuffer = buffer;
    }
}
