package org.bouncycastle.pqc.legacy.crypto.gmss;

import java.util.Vector;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.pqc.legacy.crypto.gmss.util.GMSSRandom;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.encoders.Hex;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/legacy/crypto/gmss/Treehash.class */
public class Treehash {
    private int maxHeight;
    private Vector tailStack;
    private Vector heightOfNodes;
    private byte[] firstNode;
    private byte[] seedActive;
    private byte[] seedNext;
    private int tailLength;
    private int firstNodeHeight;
    private boolean isInitialized;
    private boolean isFinished;
    private boolean seedInitialized;
    private Digest messDigestTree;

    public Treehash(Digest digest, byte[][] bArr, int[] iArr) {
        this.messDigestTree = digest;
        this.maxHeight = iArr[0];
        this.tailLength = iArr[1];
        this.firstNodeHeight = iArr[2];
        if (iArr[3] == 1) {
            this.isFinished = true;
        } else {
            this.isFinished = false;
        }
        if (iArr[4] == 1) {
            this.isInitialized = true;
        } else {
            this.isInitialized = false;
        }
        if (iArr[5] == 1) {
            this.seedInitialized = true;
        } else {
            this.seedInitialized = false;
        }
        this.heightOfNodes = new Vector();
        for (int i = 0; i < this.tailLength; i++) {
            this.heightOfNodes.addElement(Integers.valueOf(iArr[6 + i]));
        }
        this.firstNode = bArr[0];
        this.seedActive = bArr[1];
        this.seedNext = bArr[2];
        this.tailStack = new Vector();
        for (int i2 = 0; i2 < this.tailLength; i2++) {
            this.tailStack.addElement(bArr[3 + i2]);
        }
    }

    public Treehash(Vector vector, int i, Digest digest) {
        this.tailStack = vector;
        this.maxHeight = i;
        this.firstNode = null;
        this.isInitialized = false;
        this.isFinished = false;
        this.seedInitialized = false;
        this.messDigestTree = digest;
        this.seedNext = new byte[this.messDigestTree.getDigestSize()];
        this.seedActive = new byte[this.messDigestTree.getDigestSize()];
    }

    public void initializeSeed(byte[] bArr) {
        System.arraycopy(bArr, 0, this.seedNext, 0, this.messDigestTree.getDigestSize());
        this.seedInitialized = true;
    }

    public void initialize() {
        if (!this.seedInitialized) {
            throw new IllegalStateException("Seed " + this.maxHeight + " not initialized");
        }
        this.heightOfNodes = new Vector();
        this.tailLength = 0;
        this.firstNode = null;
        this.firstNodeHeight = -1;
        this.isInitialized = true;
        System.arraycopy(this.seedNext, 0, this.seedActive, 0, this.messDigestTree.getDigestSize());
    }

    public void update(GMSSRandom gMSSRandom, byte[] bArr) {
        if (this.isFinished) {
            System.err.println("No more update possible for treehash instance!");
            return;
        }
        if (!this.isInitialized) {
            System.err.println("Treehash instance not initialized before update");
            return;
        }
        byte[] bArr2 = new byte[this.messDigestTree.getDigestSize()];
        gMSSRandom.nextSeed(this.seedActive);
        if (this.firstNode == null) {
            this.firstNode = bArr;
            this.firstNodeHeight = 0;
        } else {
            byte[] bArr3 = bArr;
            int i = 0;
            while (this.tailLength > 0 && i == ((Integer) this.heightOfNodes.lastElement()).intValue()) {
                byte[] bArr4 = new byte[this.messDigestTree.getDigestSize() << 1];
                System.arraycopy(this.tailStack.lastElement(), 0, bArr4, 0, this.messDigestTree.getDigestSize());
                this.tailStack.removeElementAt(this.tailStack.size() - 1);
                this.heightOfNodes.removeElementAt(this.heightOfNodes.size() - 1);
                System.arraycopy(bArr3, 0, bArr4, this.messDigestTree.getDigestSize(), this.messDigestTree.getDigestSize());
                this.messDigestTree.update(bArr4, 0, bArr4.length);
                bArr3 = new byte[this.messDigestTree.getDigestSize()];
                this.messDigestTree.doFinal(bArr3, 0);
                i++;
                this.tailLength--;
            }
            this.tailStack.addElement(bArr3);
            this.heightOfNodes.addElement(Integers.valueOf(i));
            this.tailLength++;
            if (((Integer) this.heightOfNodes.lastElement()).intValue() == this.firstNodeHeight) {
                byte[] bArr5 = new byte[this.messDigestTree.getDigestSize() << 1];
                System.arraycopy(this.firstNode, 0, bArr5, 0, this.messDigestTree.getDigestSize());
                System.arraycopy(this.tailStack.lastElement(), 0, bArr5, this.messDigestTree.getDigestSize(), this.messDigestTree.getDigestSize());
                this.tailStack.removeElementAt(this.tailStack.size() - 1);
                this.heightOfNodes.removeElementAt(this.heightOfNodes.size() - 1);
                this.messDigestTree.update(bArr5, 0, bArr5.length);
                this.firstNode = new byte[this.messDigestTree.getDigestSize()];
                this.messDigestTree.doFinal(this.firstNode, 0);
                this.firstNodeHeight++;
                this.tailLength = 0;
            }
        }
        if (this.firstNodeHeight == this.maxHeight) {
            this.isFinished = true;
        }
    }

    public void destroy() {
        this.isInitialized = false;
        this.isFinished = false;
        this.firstNode = null;
        this.tailLength = 0;
        this.firstNodeHeight = -1;
    }

    public int getLowestNodeHeight() {
        if (this.firstNode == null) {
            return this.maxHeight;
        }
        return this.tailLength == 0 ? this.firstNodeHeight : Math.min(this.firstNodeHeight, ((Integer) this.heightOfNodes.lastElement()).intValue());
    }

    public int getFirstNodeHeight() {
        return this.firstNode == null ? this.maxHeight : this.firstNodeHeight;
    }

    public boolean wasInitialized() {
        return this.isInitialized;
    }

    public boolean wasFinished() {
        return this.isFinished;
    }

    public byte[] getFirstNode() {
        return this.firstNode;
    }

    public byte[] getSeedActive() {
        return this.seedActive;
    }

    public void setFirstNode(byte[] bArr) {
        if (!this.isInitialized) {
            initialize();
        }
        this.firstNode = bArr;
        this.firstNodeHeight = this.maxHeight;
        this.isFinished = true;
    }

    public void updateNextSeed(GMSSRandom gMSSRandom) {
        gMSSRandom.nextSeed(this.seedNext);
    }

    public Vector getTailStack() {
        return this.tailStack;
    }

    public byte[][] getStatByte() {
        byte[][] bArr = new byte[3 + this.tailLength][this.messDigestTree.getDigestSize()];
        bArr[0] = this.firstNode;
        bArr[1] = this.seedActive;
        bArr[2] = this.seedNext;
        for (int i = 0; i < this.tailLength; i++) {
            bArr[3 + i] = (byte[]) this.tailStack.elementAt(i);
        }
        return bArr;
    }

    public int[] getStatInt() {
        int[] iArr = new int[6 + this.tailLength];
        iArr[0] = this.maxHeight;
        iArr[1] = this.tailLength;
        iArr[2] = this.firstNodeHeight;
        if (this.isFinished) {
            iArr[3] = 1;
        } else {
            iArr[3] = 0;
        }
        if (this.isInitialized) {
            iArr[4] = 1;
        } else {
            iArr[4] = 0;
        }
        if (this.seedInitialized) {
            iArr[5] = 1;
        } else {
            iArr[5] = 0;
        }
        for (int i = 0; i < this.tailLength; i++) {
            iArr[6 + i] = ((Integer) this.heightOfNodes.elementAt(i)).intValue();
        }
        return iArr;
    }

    public String toString() {
        String str = "Treehash    : ";
        for (int i = 0; i < 6 + this.tailLength; i++) {
            str = str + getStatInt()[i] + " ";
        }
        for (int i2 = 0; i2 < 3 + this.tailLength; i2++) {
            str = getStatByte()[i2] != null ? str + new String(Hex.encode(getStatByte()[i2])) + " " : str + "null ";
        }
        return str + "  " + this.messDigestTree.getDigestSize();
    }
}
