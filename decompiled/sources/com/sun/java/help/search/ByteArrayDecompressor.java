package com.sun.java.help.search;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/ByteArrayDecompressor.class */
class ByteArrayDecompressor extends Decompressor {
    private byte[] _array;
    private int _index;
    private int _index0;

    public ByteArrayDecompressor(byte[] bArr, int i) {
        initReading(bArr, i);
    }

    public void initReading(byte[] bArr, int i) {
        this._array = bArr;
        this._index0 = i;
        this._index = i;
        initReading();
    }

    public int bytesRead() {
        return this._index - this._index0;
    }

    @Override // com.sun.java.help.search.Decompressor
    protected int getNextByte() throws Exception {
        byte[] bArr = this._array;
        int i = this._index;
        this._index = i + 1;
        return bArr[i] & 255;
    }
}
