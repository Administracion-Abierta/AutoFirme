package com.sun.java.help.search;

import java.io.UnsupportedEncodingException;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/BtreeDict.class */
public class BtreeDict {
    protected static final int ENTHEADERLEN = 6;
    protected static final int BLOCKSIZE = 2048;
    protected static final int DATALEN = 2040;
    protected static final int MaxKeyLength = 255;
    protected static final int lastPtrIndex = 508;
    protected BlockManager blockManager;
    protected int root;
    protected int[] blocks;
    private boolean debug = false;

    /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/BtreeDict$DictBlock.class */
    protected class DictBlock extends Block {
        private final BtreeDict this$0;

        public DictBlock(BtreeDict btreeDict) {
            super(2048);
            this.this$0 = btreeDict;
        }

        public int free() {
            return this.free + firstEntry();
        }

        public int numberOfEntries() {
            return integerAt(0);
        }

        public int nthPointer(int i) {
            return integerAt(4 * (i + 1));
        }

        public int getChildIdx(int i) {
            return nthPointer(BtreeDict.lastPtrIndex - i);
        }

        public int entryKeyLength(int i) {
            return this.data[i] & 255;
        }

        public int entryCompression(int i) {
            return this.data[i + 1] & 255;
        }

        public int entryID(int i) {
            return integerAt(i + 2);
        }

        public int entryLength(int i) {
            return 6 + entryKeyLength(i);
        }

        public int entryKey(int i) {
            return i + 6;
        }

        public int firstEntry() {
            return 4;
        }

        public int nextEntry(int i) {
            return i + entryLength(i);
        }

        public void restoreKeyInBuffer(int i, byte[] bArr) {
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
                    return;
                }
            }
        }

        public String restoreKey(int i, byte[] bArr) {
            int iEntryKeyLength = entryKeyLength(i);
            int iEntryCompression = entryCompression(i);
            int iEntryKey = entryKey(i);
            while (true) {
                int i2 = iEntryKeyLength;
                iEntryKeyLength = i2 - 1;
                if (i2 <= 0) {
                    break;
                }
                int i3 = iEntryCompression;
                iEntryCompression++;
                int i4 = iEntryKey;
                iEntryKey++;
                bArr[i3] = this.data[i4];
            }
            String str = null;
            try {
                str = new String(bArr, 0, iEntryCompression, "UTF8");
            } catch (UnsupportedEncodingException e) {
            }
            return str;
        }

        public String findID(int i) throws Exception {
            byte[] bArr = new byte[255];
            int iFree = free();
            int iFirstEntry = firstEntry();
            while (true) {
                int i2 = iFirstEntry;
                if (i2 < iFree) {
                    if (entryID(i2) == i) {
                        return restoreKey(i2, bArr);
                    }
                    restoreKeyInBuffer(i2, bArr);
                    iFirstEntry = nextEntry(i2);
                } else {
                    throw new Exception("ID not found in block");
                }
            }
        }

        protected void setBlockNumbers(int[] iArr) {
            int iFirstEntry = firstEntry();
            while (true) {
                int i = iFirstEntry;
                if (i < this.free) {
                    iArr[entryID(i)] = this.number;
                    iFirstEntry = nextEntry(i);
                } else {
                    return;
                }
            }
        }
    }

    protected BtreeDict() {
    }

    public BtreeDict(BtreeDictParameters btreeDictParameters) throws Exception {
        init(btreeDictParameters, false, new BlockFactory(this) { // from class: com.sun.java.help.search.BtreeDict.1
            private final BtreeDict this$0;

            {
                this.this$0 = this;
            }

            @Override // com.sun.java.help.search.BlockFactory
            public Block makeBlock() {
                return new DictBlock(this.this$0);
            }
        });
        this.blocks = new int[btreeDictParameters.getFreeID()];
        setBlocks(this.blocks);
    }

    public int fetch(String str) throws Exception {
        byte[] bytes = str.getBytes("UTF8");
        byte[] bArr = new byte[bytes.length + 1];
        System.arraycopy(bytes, 0, bArr, 0, bytes.length);
        bArr[bytes.length] = 0;
        return find(accessBlock(this.root), bArr);
    }

    public String fetch(int i) throws Exception {
        return findID(this.blocks[i], i);
    }

    public void close() throws Exception {
        this.blockManager.close();
    }

    protected void init(BtreeDictParameters btreeDictParameters, boolean z, BlockFactory blockFactory) throws Exception {
        this.blockManager = new BlockManager(btreeDictParameters, z, blockFactory);
        this.root = btreeDictParameters.getRootPosition();
    }

    protected void lock(Block block) {
        this.blockManager.lockBlock(block.number);
    }

    protected void unlock(Block block) {
        this.blockManager.unlockBlock(block.number);
    }

    protected DictBlock accessBlock(int i) throws Exception {
        return (DictBlock) this.blockManager.accessBlock(i);
    }

    protected DictBlock child(DictBlock dictBlock, int i) throws Exception {
        return accessBlock(dictBlock.getChildIdx(i));
    }

    private String findID(int i, int i2) throws Exception {
        return accessBlock(i).findID(i2);
    }

    private int find(DictBlock dictBlock, byte[] bArr, int i) throws Exception {
        if (dictBlock.isLeaf) {
            return 0;
        }
        return find(child(dictBlock, i), bArr);
    }

    private int find(DictBlock dictBlock, byte[] bArr) throws Exception {
        int length = bArr.length - 1;
        int iFirstEntry = dictBlock.firstEntry();
        int iFree = dictBlock.free();
        int i = 0;
        int iEntryCompression = 0;
        int i2 = 0;
        while (iFirstEntry != iFree) {
            if (iEntryCompression == i) {
                int iEntryKeyLength = dictBlock.entryKeyLength(iFirstEntry);
                int iEntryKey = dictBlock.entryKey(iFirstEntry);
                int i3 = 0;
                while (i3 < iEntryKeyLength && bArr[i] == dictBlock.data[iEntryKey + i3]) {
                    i++;
                    i3++;
                }
                if (i3 == iEntryKeyLength) {
                    if (i == length) {
                        return dictBlock.entryID(iFirstEntry);
                    }
                } else if ((bArr[i] & 255) < (dictBlock.data[iEntryKey + i3] & 255)) {
                    return find(dictBlock, bArr, i2);
                }
            } else if (iEntryCompression < i) {
                return find(dictBlock, bArr, iFirstEntry == iFree ? dictBlock.numberOfEntries() : i2);
            }
            do {
                iFirstEntry = dictBlock.nextEntry(iFirstEntry);
                i2++;
            } while (dictBlock.entryCompression(iFirstEntry) > i);
            iEntryCompression = dictBlock.entryCompression(iFirstEntry);
        }
        return find(dictBlock, bArr, dictBlock.numberOfEntries());
    }

    private void setBlocks(int[] iArr) throws Exception {
        long jCurrentTimeMillis = System.currentTimeMillis();
        this.blockManager.mapBlocks(new BlockProcessor(this, iArr) { // from class: com.sun.java.help.search.BtreeDict.2
            private final int[] val$blocks;
            private final BtreeDict this$0;

            {
                this.this$0 = this;
                this.val$blocks = iArr;
            }

            @Override // com.sun.java.help.search.BlockProcessor
            public void process(Block block) {
                ((DictBlock) block).setBlockNumbers(this.val$blocks);
            }
        });
        debug(new StringBuffer().append(System.currentTimeMillis() - jCurrentTimeMillis).append(" msec; TMAP").toString());
    }

    private void debug(String str) {
        if (this.debug) {
            System.err.println(new StringBuffer().append("BtreeDict: ").append(str).toString());
        }
    }

    public static void main(String[] strArr) {
        try {
            new BtreeDict(new BtreeDictParameters(new Schema(null, strArr[0], false), "TMAP"));
        } catch (Exception e) {
            System.err.println(e);
            e.printStackTrace();
        }
    }
}
