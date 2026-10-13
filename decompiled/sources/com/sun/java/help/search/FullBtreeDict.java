package com.sun.java.help.search;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.StringTokenizer;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/FullBtreeDict.class */
public class FullBtreeDict extends BtreeDict {
    protected BtreeDictParameters params;
    private boolean update;
    private boolean debug;

    /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/FullBtreeDict$Entry.class */
    private final class Entry {
        public byte[] key;
        public int id;
        public int block = -1;
        private final FullBtreeDict this$0;

        public Entry(FullBtreeDict fullBtreeDict, byte[] bArr, int i, int i2) {
            this.this$0 = fullBtreeDict;
            this.key = new byte[i + 1];
            System.arraycopy(bArr, 0, this.key, 0, i);
            this.key[i] = 0;
            this.id = i2;
        }

        public boolean smallerThan(Entry entry) {
            for (int i = 0; i < Math.min(this.key.length, entry.key.length); i++) {
                if (this.key[i] != entry.key[i]) {
                    return (this.key[i] & 255) < (entry.key[i] & 255);
                }
            }
            return false;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/FullBtreeDict$FullDictBlock.class */
    protected class FullDictBlock extends BtreeDict.DictBlock {
        private final FullBtreeDict this$0;

        protected FullDictBlock(FullBtreeDict fullBtreeDict) {
            super(fullBtreeDict);
            this.this$0 = fullBtreeDict;
        }

        @Override // com.sun.java.help.search.Block
        public void setFree(int i) {
            this.free = i - firstEntry();
            byte[] bArr = this.data;
            this.data[i + 1] = 0;
            bArr[i] = 0;
        }

        public void setNumberOfEntries(int i) {
            setIntegerAt(0, i);
        }

        protected void setChildIndex(int i, int i2) {
            setIntegerAt(4 * ((508 - i) + 1), i2);
        }

        public void setEntryID(int i, int i2) {
            setIntegerAt(i + 2, i2);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean insert(Entry entry) {
            byte[] bArr = entry.key;
            int length = bArr.length - 1;
            int iFree = free();
            int iFirstEntry = firstEntry();
            int i = 0;
            int iEntryCompression = 0;
            int i2 = 0;
            while (iFirstEntry != iFree) {
                if (iEntryCompression == i) {
                    int iEntryKeyLength = entryKeyLength(iFirstEntry);
                    int iEntryKey = entryKey(iFirstEntry);
                    int i3 = i;
                    int i4 = 0;
                    while (i4 < iEntryKeyLength && bArr[i] == this.data[iEntryKey + i4]) {
                        i++;
                        i4++;
                    }
                    if (i4 == iEntryKeyLength) {
                        if (i == length) {
                            setEntryID(iFirstEntry, entry.id);
                            return true;
                        }
                    } else if ((bArr[i] & 255) < (this.data[iEntryKey + i4] & 255)) {
                        return insert(entry, iFirstEntry, i3, i, i2);
                    }
                } else if (iEntryCompression < i) {
                    return insert(entry, iFirstEntry, i, iEntryCompression, iFirstEntry == iFree ? numberOfEntries() : i2);
                }
                do {
                    iFirstEntry = nextEntry(iFirstEntry);
                    i2++;
                } while (entryCompression(iFirstEntry) > i);
                iEntryCompression = entryCompression(iFirstEntry);
            }
            return insert(entry, iFirstEntry, i, 0, numberOfEntries());
        }

        public void makeEntry(int i, byte[] bArr, int i2, int i3, int i4) {
            this.data[i] = (byte) i3;
            this.data[i + 1] = (byte) i4;
            setEntryID(i, i2);
            System.arraycopy(bArr, i4, this.data, entryKey(i), i3);
        }

        private boolean insert(Entry entry, int i, int i2, int i3, int i4) {
            byte[] bArr = entry.key;
            int length = (bArr.length - 1) - i2;
            int iFree = free();
            int i5 = 6 + length;
            int iEntryCompression = 0;
            if (i < iFree && entryCompression(i) < i3) {
                iEntryCompression = i3 - entryCompression(i);
            }
            if ((iFree + i5) - iEntryCompression <= (this.isLeaf ? 2038 : 4 * ((508 - numberOfEntries()) - 1))) {
                if (i < iFree) {
                    int i6 = iEntryCompression > 0 ? i + 6 + iEntryCompression : i;
                    System.arraycopy(this.data, i6, this.data, (i6 + i5) - iEntryCompression, iFree - i6);
                    if (iEntryCompression > 0) {
                        byte[] bArr2 = this.data;
                        bArr2[i] = (byte) (bArr2[i] - iEntryCompression);
                        byte[] bArr3 = this.data;
                        int i7 = i + 1;
                        bArr3[i7] = (byte) (bArr3[i7] + iEntryCompression);
                        System.arraycopy(this.data, i, this.data, i + i5, 6);
                    }
                }
                makeEntry(i, bArr, entry.id, length, i2);
                if (!this.isLeaf) {
                    int iNumberOfEntries = 4 * ((508 - numberOfEntries()) + 1);
                    System.arraycopy(this.data, iNumberOfEntries, this.data, iNumberOfEntries - 4, 4 * (numberOfEntries() - i4));
                    setChildIndex(i4 + 1, entry.block);
                }
                setFree((iFree + i5) - iEntryCompression);
                setNumberOfEntries(numberOfEntries() + 1);
                return true;
            }
            return false;
        }

        public int insertInternal(Entry entry) {
            byte[] bArr = entry.key;
            int length = bArr.length - 1;
            int iFirstEntry = firstEntry();
            int iFree = free();
            int i = 0;
            int iEntryCompression = 0;
            int i2 = 0;
            while (iFirstEntry != iFree) {
                if (iEntryCompression == i) {
                    int iEntryKeyLength = entryKeyLength(iFirstEntry);
                    int iEntryKey = entryKey(iFirstEntry);
                    int i3 = 0;
                    while (i3 < iEntryKeyLength && bArr[i] == this.data[iEntryKey + i3]) {
                        i++;
                        i3++;
                    }
                    if (i3 == iEntryKeyLength) {
                        if (i == length) {
                            setEntryID(iFirstEntry, entry.id);
                            return -1;
                        }
                    } else if ((bArr[i] & 255) < (this.data[iEntryKey + i3] & 255)) {
                        return i2;
                    }
                } else if (iEntryCompression < i) {
                    return iFirstEntry >= iFree ? numberOfEntries() : i2;
                }
                do {
                    iFirstEntry = nextEntry(iFirstEntry);
                    i2++;
                } while (entryCompression(iFirstEntry) > i);
                iEntryCompression = entryCompression(iFirstEntry);
            }
            return numberOfEntries();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Entry split(FullDictBlock fullDictBlock) {
            int i;
            byte[] bArr = new byte[255];
            int iFree = free();
            int i2 = iFree / 2;
            int i3 = 0;
            fullDictBlock.isLeaf = this.isLeaf;
            int iFirstEntry = firstEntry();
            while (true) {
                i = iFirstEntry;
                if (i >= i2) {
                    break;
                }
                restoreKeyInBuffer(i, bArr);
                i3++;
                iFirstEntry = nextEntry(i);
            }
            int iNumberOfEntries = (numberOfEntries() - i3) - 1;
            restoreKeyInBuffer(i, bArr);
            Entry entry = new Entry(this.this$0, bArr, entryKeyLength(i) + entryCompression(i), entryID(i));
            entry.block = fullDictBlock.number;
            int iNextEntry = nextEntry(i);
            restoreKeyInBuffer(iNextEntry, bArr);
            int iEntryKeyLength = entryKeyLength(iNextEntry) + entryCompression(iNextEntry);
            int iFirstEntry2 = firstEntry();
            fullDictBlock.makeEntry(iFirstEntry2, bArr, entryID(iNextEntry), iEntryKeyLength, 0);
            int iNextEntry2 = nextEntry(iNextEntry);
            System.arraycopy(this.data, iNextEntry2, fullDictBlock.data, fullDictBlock.nextEntry(iFirstEntry2), iFree - iNextEntry2);
            fullDictBlock.setNumberOfEntries(iNumberOfEntries);
            fullDictBlock.setFree((fullDictBlock.nextEntry(iFirstEntry2) + iFree) - iNextEntry2);
            if (!this.isLeaf) {
                int iNumberOfEntries2 = 4 * ((508 - numberOfEntries()) + 1);
                System.arraycopy(this.data, iNumberOfEntries2, fullDictBlock.data, iNumberOfEntries2 + (4 * (i3 + 1)), 4 * (iNumberOfEntries + 1));
            }
            setFree(i);
            setNumberOfEntries(i3);
            return entry;
        }

        public void initInternal(int i, Entry entry) {
            this.isLeaf = false;
            setNumberOfEntries(1);
            setChildIndex(0, i);
            setChildIndex(1, entry.block);
            int iFirstEntry = firstEntry();
            makeEntry(iFirstEntry, entry.key, entry.id, entry.key.length - 1, 0);
            setFree(nextEntry(iFirstEntry));
        }
    }

    protected FullBtreeDict() {
        this.update = false;
        this.debug = false;
    }

    public FullBtreeDict(BtreeDictParameters btreeDictParameters, boolean z) throws Exception {
        this.update = false;
        this.debug = false;
        init(btreeDictParameters, z, new BlockFactory(this) { // from class: com.sun.java.help.search.FullBtreeDict.1
            private final FullBtreeDict this$0;

            {
                this.this$0 = this;
            }

            @Override // com.sun.java.help.search.BlockFactory
            public Block makeBlock() {
                return new FullDictBlock(this.this$0);
            }
        });
        this.blocks = new int[300000];
        this.params = btreeDictParameters;
        this.update = z;
    }

    public void close(int i) throws Exception {
        this.params.setFreeID(i);
        if (this.update) {
            this.params.updateSchema();
        }
        super.close();
    }

    public void store(String str, int i) throws Exception {
        byte[] bytes = str.getBytes("UTF8");
        Entry entryInsert = insert((FullDictBlock) accessBlock(this.root), new Entry(this, bytes, bytes.length, i));
        if (entryInsert != null) {
            FullDictBlock newBlock = getNewBlock();
            newBlock.initInternal(this.root, entryInsert);
            int[] iArr = this.blocks;
            int i2 = entryInsert.id;
            int i3 = newBlock.number;
            this.root = i3;
            iArr[i2] = i3;
            this.params.setRoot(this.root);
        }
    }

    private void setModified(Block block) {
        this.blockManager.setModified(block.number);
    }

    private FullDictBlock getNewBlock() throws Exception {
        FullDictBlock fullDictBlock = (FullDictBlock) this.blockManager.getNewBlock();
        setModified(fullDictBlock);
        return fullDictBlock;
    }

    private Entry insert(FullDictBlock fullDictBlock, Entry entry) throws Exception {
        if (fullDictBlock.isLeaf) {
            return insertHere(fullDictBlock, entry);
        }
        int iInsertInternal = fullDictBlock.insertInternal(entry);
        if (iInsertInternal != -1) {
            try {
                lock(fullDictBlock);
                Entry entryInsert = insert((FullDictBlock) child(fullDictBlock, iInsertInternal), entry);
                return entryInsert == null ? null : insertHere(fullDictBlock, entryInsert);
            } finally {
                unlock(fullDictBlock);
            }
        }
        return null;
    }

    private Entry insertHere(FullDictBlock fullDictBlock, Entry entry) throws Exception {
        setModified(fullDictBlock);
        if (fullDictBlock.insert(entry)) {
            return null;
        }
        FullDictBlock newBlock = getNewBlock();
        Entry entrySplit = fullDictBlock.split(newBlock);
        newBlock.setBlockNumbers(this.blocks);
        if (!(entrySplit.smallerThan(entry) ? newBlock : fullDictBlock).insert(entry)) {
            throw new Exception("entry didn't fit into a freshly split block");
        }
        return entrySplit;
    }

    public static void main(String[] strArr) {
        try {
            BtreeDictParameters btreeDictParameters = new BtreeDictParameters(new Schema(null, strArr[0], true), "TMAP");
            if (!btreeDictParameters.readState()) {
                btreeDictParameters.setBlockSize(2048);
                btreeDictParameters.setRoot(0);
                btreeDictParameters.setFreeID(1);
            }
            FullBtreeDict fullBtreeDict = new FullBtreeDict(btreeDictParameters, true);
            int freeID = btreeDictParameters.getFreeID();
            LineNumberReader lineNumberReader = new LineNumberReader(new BufferedReader(new FileReader(strArr[1])));
            while (true) {
                String line = lineNumberReader.readLine();
                if (line != null) {
                    StringTokenizer stringTokenizer = new StringTokenizer(line, " ");
                    while (stringTokenizer.hasMoreTokens()) {
                        String strNextToken = stringTokenizer.nextToken();
                        if (strNextToken.equals("storing")) {
                            int i = freeID;
                            freeID++;
                            fullBtreeDict.store(stringTokenizer.nextToken(), i);
                        } else if (strNextToken.equals("fetching")) {
                            fullBtreeDict.fetch(stringTokenizer.nextToken());
                        }
                    }
                } else {
                    lineNumberReader.close();
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void debug(String str) {
        if (this.debug) {
            System.err.println(new StringBuffer().append("FullBtreeDict: ").append(str).toString());
        }
    }
}
