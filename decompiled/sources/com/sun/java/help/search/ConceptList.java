package com.sun.java.help.search;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/ConceptList.class */
class ConceptList implements NonnegativeIntegerGenerator, CompressorIterator {
    private ByteArrayDecompressor _list;
    private byte _k;
    private int _value = 0;

    public ConceptList(byte[] bArr, int i) {
        this._k = bArr[i];
        this._list = new ByteArrayDecompressor(bArr, i + 1);
    }

    @Override // com.sun.java.help.search.CompressorIterator
    public void value(int i) {
        this._value += i;
    }

    @Override // com.sun.java.help.search.NonnegativeIntegerGenerator
    public int first() throws Exception {
        this._value = 0;
        if (this._list.readNext(this._k, this)) {
            return this._value;
        }
        return -1;
    }

    @Override // com.sun.java.help.search.NonnegativeIntegerGenerator
    public int next() throws Exception {
        if (this._list.readNext(this._k, this)) {
            return this._value;
        }
        return -1;
    }
}
