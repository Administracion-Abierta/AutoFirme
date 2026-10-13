package com.sun.java.help.search;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/ConceptGroupGenerator.class */
class ConceptGroupGenerator implements CompressorIterator {
    private static final int NConceptsInGroup = 16;
    private static final int BitsInLabel = 4;
    private int _last;
    private ConceptData[] _table;
    private Decompressor _bits;
    private int _k1;
    private final int _k2 = 4;
    private ConceptData _cData;

    public ConceptGroupGenerator() {
        this._k2 = 4;
        this._k1 = 0;
        this._table = new ConceptData[16];
        this._last = 0;
        this._bits = null;
    }

    public ConceptGroupGenerator(byte[] bArr, int i, int i2) {
        this._k2 = 4;
        this._k1 = i2;
        this._table = new ConceptData[16];
        this._last = 0;
        this._bits = new ByteArrayDecompressor(bArr, i);
    }

    public void init(byte[] bArr, int i, int i2) {
        this._k1 = i2;
        this._bits = new ByteArrayDecompressor(bArr, i);
        this._last = 0;
        for (int i3 = 0; i3 < 16; i3++) {
            this._table[i3] = null;
        }
    }

    public void addTerms(int i, ConceptData conceptData) {
        this._table[i] = conceptData;
    }

    public int decodeConcepts(int i, int i2, int[] iArr) throws Exception {
        return this._bits.ascendingDecode(i, i2, iArr);
    }

    public int position() {
        return this._last;
    }

    @Override // com.sun.java.help.search.CompressorIterator
    public void value(int i) {
        this._last += i;
    }

    boolean next() throws Exception {
        while (this._bits.readNext(this._k1, this)) {
            try {
                ConceptData conceptData = this._table[this._bits.read(4)];
                this._cData = conceptData;
                if (conceptData != null) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println(this._bits);
                System.err.println(this._table);
                throw e;
            }
        }
        return false;
    }

    public void generateFillers(RoleFiller[] roleFillerArr) {
        this._cData.generateFillers(roleFillerArr, this._last);
    }
}
