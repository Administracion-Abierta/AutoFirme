package org.apache.fontbox.cmap;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/cmap/CodespaceRange.class */
public class CodespaceRange {
    private byte[] startBytes;
    private byte[] endBytes;
    private int[] start;
    private int[] end;
    private int codeLength;

    public CodespaceRange(byte[] startBytes, byte[] endBytes) {
        this.codeLength = 0;
        byte[] correctedStartBytes = startBytes;
        if (startBytes.length != endBytes.length && startBytes.length == 1 && startBytes[0] == 0) {
            correctedStartBytes = new byte[endBytes.length];
        } else if (startBytes.length != endBytes.length) {
            throw new IllegalArgumentException("The start and the end values must not have different lengths.");
        }
        this.start = new int[correctedStartBytes.length];
        this.end = new int[endBytes.length];
        for (int i = 0; i < correctedStartBytes.length; i++) {
            this.start[i] = correctedStartBytes[i] & 255;
            this.end[i] = endBytes[i] & 255;
        }
        this.codeLength = endBytes.length;
    }

    public CodespaceRange() {
        this.codeLength = 0;
    }

    public int getCodeLength() {
        return this.codeLength;
    }

    public byte[] getEnd() {
        return this.endBytes;
    }

    void setEnd(byte[] endBytes) {
        this.endBytes = endBytes;
        this.end = new int[endBytes.length];
        for (int i = 0; i < endBytes.length; i++) {
            this.end[i] = endBytes[i] & 255;
        }
    }

    public byte[] getStart() {
        return this.startBytes;
    }

    void setStart(byte[] startBytes) {
        this.startBytes = startBytes;
        this.start = new int[startBytes.length];
        for (int i = 0; i < startBytes.length; i++) {
            this.start[i] = startBytes[i] & 255;
        }
        this.codeLength = startBytes.length;
    }

    public boolean matches(byte[] code) {
        return isFullMatch(code, code.length);
    }

    public boolean isFullMatch(byte[] code, int codeLen) {
        if (this.codeLength != codeLen) {
            return false;
        }
        for (int i = 0; i < this.codeLength; i++) {
            int codeAsInt = code[i] & 255;
            if (codeAsInt < this.start[i] || codeAsInt > this.end[i]) {
                return false;
            }
        }
        return true;
    }
}
