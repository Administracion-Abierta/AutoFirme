package com.sun.java.help.search;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/BlockManager.class */
class BlockManager {
    private static RAFFileFactory factory = RAFFileFactory.create();
    private RAFFile file;
    private long blockSize;
    private boolean update;
    private int blockTableSize;
    private BlockDescriptor[] blockTab;
    private BlockFactory bfactory;
    private static final int INCR = 256;
    private Block dummy;
    private final int nBlocksLimit = 64;
    private int nBlocks = 0;
    private int oldest = 0;
    private int newest = 0;
    private boolean debug = false;

    /* JADX INFO: renamed from: com.sun.java.help.search.BlockManager$1, reason: invalid class name */
    /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/BlockManager$1.class */
    static class AnonymousClass1 {
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/BlockManager$BlockDescriptor.class */
    private class BlockDescriptor {
        public Block block;
        public boolean lock;
        public boolean modf;
        public int prev;
        public int next;
        private final BlockManager this$0;

        private BlockDescriptor(BlockManager blockManager) {
            this.this$0 = blockManager;
            this.block = null;
            this.lock = false;
            this.modf = false;
            this.prev = 0;
            this.next = 0;
        }

        BlockDescriptor(BlockManager blockManager, AnonymousClass1 anonymousClass1) {
            this(blockManager);
        }

        public void reset() {
            this.modf = false;
            this.lock = false;
            this.block = null;
        }
    }

    public BlockManager(BlockManagerParameters blockManagerParameters, boolean z, BlockFactory blockFactory) throws IOException {
        this.bfactory = null;
        this.bfactory = blockFactory;
        this.update = z;
        this.blockSize = blockManagerParameters.getBlockSize();
        debug(blockManagerParameters.getURL().toString());
        this.file = factory.get(blockManagerParameters.getURL(), z);
        debug(this.file.toString());
        this.dummy = blockFactory.makeBlock();
        if (this.file.length() == 0 && z) {
            this.dummy.setBlockNumber(0);
            writeBlock(this.dummy);
        }
        this.blockTableSize = (int) (this.file.length() / this.blockSize);
        this.blockTab = new BlockDescriptor[this.blockTableSize];
        mapBlock(0, new BlockDescriptor(this, null), blockFactory.makeBlock());
    }

    public void lockBlock(int i) {
        this.blockTab[i].lock = true;
    }

    public void unlockBlock(int i) {
        this.blockTab[i].lock = false;
    }

    public void setModified(int i) {
        this.blockTab[i].modf = true;
    }

    public void close() throws IOException {
        if (this.update) {
            for (int i = 0; i < this.blockTableSize; i++) {
                if (this.blockTab[i] != null && this.blockTab[i].modf) {
                    writeBlock(this.blockTab[i].block);
                }
            }
        }
        this.file.close();
    }

    public Block accessBlock(int i) throws Exception {
        if (this.blockTab[i] != null) {
            moveToFront(i);
        } else if (this.nBlocks < 64) {
            mapBlock(i, new BlockDescriptor(this, null), this.bfactory.makeBlock());
        } else {
            remapSomeBlock(i);
        }
        return this.blockTab[i].block;
    }

    public Block getNewBlock() throws Exception {
        int length = (int) (this.file.length() / this.blockSize);
        if (length > this.blockTableSize - 1) {
            BlockDescriptor[] blockDescriptorArr = new BlockDescriptor[this.blockTableSize + 256];
            System.arraycopy(this.blockTab, 0, blockDescriptorArr, 0, this.blockTableSize);
            this.blockTab = blockDescriptorArr;
            this.blockTableSize += 256;
        }
        if (this.nBlocks < 64) {
            Block blockMakeBlock = this.bfactory.makeBlock();
            blockMakeBlock.setBlockNumber(length);
            writeBlock(blockMakeBlock);
            addDescriptor(blockMakeBlock, length, new BlockDescriptor(this, null));
        } else {
            this.dummy.setBlockNumber(length);
            writeBlock(this.dummy);
            remapSomeBlock(length);
        }
        return this.blockTab[length].block;
    }

    private void mapBlock(int i, BlockDescriptor blockDescriptor, Block block) throws IOException {
        this.file.seek(this.blockSize * ((long) i));
        Block.readIn(this.file, block);
        addDescriptor(block, i, blockDescriptor);
    }

    private void addDescriptor(Block block, int i, BlockDescriptor blockDescriptor) {
        this.blockTab[i] = blockDescriptor;
        blockDescriptor.block = block;
        BlockDescriptor[] blockDescriptorArr = this.blockTab;
        int i2 = this.newest;
        blockDescriptor.prev = i2;
        blockDescriptorArr[i2].next = i;
        this.newest = i;
        this.nBlocks++;
    }

    private void remapSomeBlock(int i) throws Exception {
        int i2;
        int i3 = this.oldest;
        while (true) {
            i2 = i3;
            if (!this.blockTab[i2].lock || i2 == this.newest) {
                break;
            } else {
                i3 = this.blockTab[i2].next;
            }
        }
        if (this.blockTab[i2].lock) {
            throw new Exception("everything locked");
        }
        if (this.blockTab[i2].modf) {
            writeBlock(this.blockTab[i2].block);
        }
        this.nBlocks--;
        Block block = this.blockTab[i2].block;
        if (i2 == this.oldest) {
            this.oldest = this.blockTab[i2].next;
        } else if (i2 == this.newest) {
            this.newest = this.blockTab[i2].prev;
        } else {
            this.blockTab[this.blockTab[i2].next].prev = this.blockTab[i2].prev;
            this.blockTab[this.blockTab[i2].prev].next = this.blockTab[i2].next;
        }
        this.blockTab[i2].reset();
        mapBlock(i, this.blockTab[i2], block);
        this.blockTab[i2] = null;
    }

    private void moveToFront(int i) {
        if (i == this.oldest) {
            this.oldest = this.blockTab[i].next;
            this.blockTab[i].prev = this.newest;
            this.blockTab[this.newest].next = i;
            this.newest = i;
            return;
        }
        if (i != this.newest) {
            this.blockTab[this.blockTab[i].next].prev = this.blockTab[i].prev;
            this.blockTab[this.blockTab[i].prev].next = this.blockTab[i].next;
            this.blockTab[i].prev = this.newest;
            this.blockTab[this.newest].next = i;
            this.newest = i;
        }
    }

    public void writeBlock(Block block) throws IOException {
        this.file.seek(this.blockSize * ((long) block.number));
        block.writeOut(this.file);
    }

    public void mapBlocks(BlockProcessor blockProcessor) throws IOException {
        long length = this.file.length() / this.blockSize;
        Block blockMakeBlock = this.bfactory.makeBlock();
        this.file.seek(0L);
        for (int i = 0; i < length; i++) {
            blockProcessor.process(Block.readIn(this.file, blockMakeBlock));
        }
    }

    private void debug(String str) {
        if (this.debug) {
            System.err.println(new StringBuffer().append("Block Manager: ").append(str).toString());
        }
    }
}
