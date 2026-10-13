package com.sun.java.help.search;

import com.sun.jna.platform.win32.WinNT;
import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/Block.class */
class Block {
    public static final int HEADERLEN = 8;
    public static final int IDLEN = 4;
    public int number;
    public boolean isLeaf = true;
    public int free = 0;
    public byte[] data;
    private static boolean debug = false;

    public Block(int i) {
        this.data = null;
        this.data = new byte[i - 8];
    }

    public void setBlockNumber(int i) {
        this.number = i;
    }

    public void setFree(int i) {
    }

    public int integerAt(int i) {
        int i2 = i + 1;
        int i3 = (this.data[i] & 255) << 8;
        int i4 = i2 + 1;
        return ((((i3 | (this.data[i2] & 255)) << 8) | (this.data[i4] & 255)) << 8) | (this.data[i4 + 1] & 255);
    }

    public void setIntegerAt(int i, int i2) {
        int i3 = i + 3;
        while (i3 >= i) {
            this.data[i3] = (byte) (i2 & 255);
            i3--;
            i2 >>>= 8;
        }
    }

    public static Block readIn(RAFFile rAFFile, Block block) throws IOException {
        debug("readIn");
        block.number = rAFFile.readInt();
        int i = rAFFile.readInt();
        block.isLeaf = (i & Integer.MIN_VALUE) != 0;
        block.free = i & WinNT.MAXLONG;
        rAFFile.readFully(block.data);
        return block;
    }

    public void writeOut(RAFFile rAFFile) throws IOException {
        rAFFile.writeInt(this.number);
        rAFFile.writeInt(this.free | (this.isLeaf ? Integer.MIN_VALUE : 0));
        rAFFile.write(this.data);
    }

    private static void debug(String str) {
        if (debug) {
            System.err.println(new StringBuffer().append("Block: ").append(str).toString());
        }
    }
}
