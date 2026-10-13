package com.sun.java.help.search;

import java.io.IOException;
import java.net.URL;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/BtreeDictCompactor.class */
public class BtreeDictCompactor extends FullBtreeDict {
    private CompactorDictBlock _currentLeaf;
    private int _limit;
    private int _entry;
    private int _counter;
    private byte[] _lastKey = new byte[255];
    private int _lastKeyLength = 0;
    private int _nEntries = 0;
    private InternalBlockState _parent = null;
    private boolean debug = false;

    static int access$008(BtreeDictCompactor btreeDictCompactor) {
        int i = btreeDictCompactor._counter;
        btreeDictCompactor._counter = i + 1;
        return i;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/BtreeDictCompactor$CompactorDictBlock.class */
    private final class CompactorDictBlock extends FullBtreeDict.FullDictBlock {
        private final BtreeDictCompactor this$0;

        private CompactorDictBlock(BtreeDictCompactor btreeDictCompactor) {
            super(btreeDictCompactor);
            this.this$0 = btreeDictCompactor;
        }

        CompactorDictBlock(BtreeDictCompactor btreeDictCompactor, AnonymousClass1 anonymousClass1) {
            this(btreeDictCompactor);
        }

        private int restoreKey1(int i, byte[] bArr) {
            int iEntryKeyLength = entryKeyLength(i);
            int iEntryCompression = entryCompression(i);
            int iEntryKey = entryKey(i);
            while (true) {
                int i2 = iEntryKeyLength;
                iEntryKeyLength = i2 - 1;
                if (i2 > 0) {
                    int i3 = iEntryCompression;
                    iEntryCompression++;
                    int i4 = iEntryKey;
                    iEntryKey++;
                    bArr[i3] = this.data[i4];
                } else {
                    return iEntryCompression;
                }
            }
        }

        protected void doMap1(BtreeDictCompactor btreeDictCompactor, BtreeDictCompactor btreeDictCompactor2) throws Exception {
            byte[] bArr = new byte[255];
            int iFree = free();
            int iFirstEntry = firstEntry();
            if (this.isLeaf) {
                while (iFirstEntry < iFree) {
                    btreeDictCompactor2.store(bArr, restoreKey1(iFirstEntry, bArr), entryID(iFirstEntry));
                    iFirstEntry = nextEntry(iFirstEntry);
                }
                return;
            }
            btreeDictCompactor.lock(this);
            int i = 0;
            while (iFirstEntry < iFree) {
                btreeDictCompactor.accessBlock1(getChildIdx(i)).doMap1(btreeDictCompactor, btreeDictCompactor2);
                btreeDictCompactor2.store(bArr, restoreKey1(iFirstEntry, bArr), entryID(iFirstEntry));
                iFirstEntry = nextEntry(iFirstEntry);
                i++;
            }
            btreeDictCompactor.accessBlock1(getChildIdx(i)).doMap1(btreeDictCompactor, btreeDictCompactor2);
            btreeDictCompactor.unlock(this);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/BtreeDictCompactor$InternalBlockState.class */
    private final class InternalBlockState {
        private CompactorDictBlock _block;
        private byte[] _lastKey = new byte[255];
        private int _lastKeyLength;
        private int _entry;
        private int _nEntries;
        private int _limit;
        private InternalBlockState _parent;
        private final BtreeDictCompactor this$0;

        public InternalBlockState(BtreeDictCompactor btreeDictCompactor, int i) {
            this.this$0 = btreeDictCompactor;
            btreeDictCompactor.debug(new StringBuffer().append("NEW ROOT ").append(btreeDictCompactor._counter).toString());
            btreeDictCompactor.debug(btreeDictCompactor.params.toString());
            btreeDictCompactor.params.setRoot(btreeDictCompactor._counter);
            this._block = new CompactorDictBlock(btreeDictCompactor, null);
            this._block.isLeaf = false;
            this._block.setBlockNumber(BtreeDictCompactor.access$008(btreeDictCompactor));
            init(i);
        }

        private void init(int i) {
            this._entry = this._block.firstEntry();
            this._nEntries = 0;
            this._lastKeyLength = 0;
            this._limit = 2028;
            this._block.setChildIndex(0, i);
        }

        public void store(byte[] bArr, int i, int i2, int i3) {
            int i4 = 0;
            while (i4 < this._lastKeyLength && this._lastKey[i4] == bArr[i4]) {
                i4++;
            }
            int i5 = (6 + i) - i4;
            if (this._entry + i5 > this._limit) {
                this.this$0.debug("NEW: SPLIT INTERNAL");
                this._block.setFree(this._entry);
                this._block.setNumberOfEntries(this._nEntries);
                if (this._parent == null) {
                    this._parent = new InternalBlockState(this.this$0, this._block.number);
                }
                this._parent.store(bArr, i, i2, this.this$0.newBlock(this._block));
                init(i3);
                return;
            }
            this._block.makeEntry(this._entry, bArr, i2, i - i4, i4);
            this._entry += i5;
            this._nEntries++;
            this._block.setChildIndex(this._nEntries, i3);
            this._limit -= 4;
            this._lastKeyLength = i;
            System.arraycopy(bArr, i4, this._lastKey, i4, i - i4);
        }

        public void close() throws IOException {
            this._block.setFree(this._entry);
            this._block.setNumberOfEntries(this._nEntries);
            this.this$0.blockManager.writeBlock(this._block);
            if (this._parent == null) {
                this.this$0.debug(new StringBuffer().append("root: ").append(this._block.number).toString());
            } else {
                this._parent.close();
            }
        }
    }

    public BtreeDictCompactor(BtreeDictParameters btreeDictParameters, boolean z) throws Exception {
        this._entry = 0;
        this._counter = 0;
        init(btreeDictParameters, z, new BlockFactory(this) { // from class: com.sun.java.help.search.BtreeDictCompactor.1
            private final BtreeDictCompactor this$0;

            {
                this.this$0 = this;
            }

            @Override // com.sun.java.help.search.BlockFactory
            public Block makeBlock() {
                return new CompactorDictBlock(this.this$0, null);
            }
        });
        this._currentLeaf = new CompactorDictBlock(this, null);
        CompactorDictBlock compactorDictBlock = this._currentLeaf;
        int i = this._counter;
        this._counter = i + 1;
        compactorDictBlock.setBlockNumber(i);
        this._limit = 2038;
        this._entry = this._currentLeaf.firstEntry();
        this.params = btreeDictParameters;
    }

    protected CompactorDictBlock accessBlock1(int i) throws Exception {
        return (CompactorDictBlock) this.blockManager.accessBlock(i);
    }

    public void store(byte[] bArr, int i, int i2) {
        if (i2 > 0) {
            int i3 = 0;
            while (i3 < this._lastKeyLength && this._lastKey[i3] == bArr[i3]) {
                i3++;
            }
            int i4 = (6 + i) - i3;
            if (this._entry + i4 <= this._limit) {
                this._currentLeaf.makeEntry(this._entry, bArr, i2, i - i3, i3);
                this._entry += i4;
                this._nEntries++;
                this._lastKeyLength = i;
                System.arraycopy(bArr, i3, this._lastKey, i3, i - i3);
                return;
            }
            this._currentLeaf.setFree(this._entry);
            this._currentLeaf.setNumberOfEntries(this._nEntries);
            if (this._parent == null) {
                this._parent = new InternalBlockState(this, this._currentLeaf.number);
            }
            this._parent.store(bArr, i, i2, newBlock(this._currentLeaf));
            this._entry = this._currentLeaf.firstEntry();
            this._nEntries = 0;
            this._lastKeyLength = 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int newBlock(BtreeDict.DictBlock dictBlock) {
        int i = this._counter;
        this._counter = i + 1;
        try {
            this.blockManager.writeBlock(dictBlock);
            dictBlock.setBlockNumber(i);
            this.blockManager.writeBlock(dictBlock);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return i;
    }

    @Override // com.sun.java.help.search.BtreeDict
    public void close() throws IOException {
        this._currentLeaf.setFree(this._entry);
        this._currentLeaf.setNumberOfEntries(this._nEntries);
        this.blockManager.writeBlock(this._currentLeaf);
        if (this._parent == null) {
            debug(new StringBuffer().append("root: ").append(this._currentLeaf.number).toString());
        } else {
            this._parent.close();
        }
        this.blockManager.close();
    }

    public void compact(BtreeDictParameters btreeDictParameters) throws Exception {
        BtreeDictCompactor btreeDictCompactor = new BtreeDictCompactor(btreeDictParameters, true);
        ((CompactorDictBlock) this.blockManager.accessBlock(this.root)).doMap1(this, btreeDictCompactor);
        btreeDictCompactor.close();
        this.blockManager.close();
    }

    public static void main(String[] strArr) {
        try {
            new BtreeDictCompactor(new BtreeDictParameters(new Schema(null, strArr[0], false), "TMAP"), false).compact(new BtreeDictParameters(new URL("file", "", strArr[1]), 2048, 0, 24));
        } catch (Exception e) {
            System.err.println(e);
            e.printStackTrace();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void debug(String str) {
        if (this.debug) {
            System.err.println(new StringBuffer().append("BtreeDictCompactor: ").append(str).toString());
        }
    }
}
