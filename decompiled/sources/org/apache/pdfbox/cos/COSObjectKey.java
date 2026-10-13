package org.apache.pdfbox.cos;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/cos/COSObjectKey.class */
public class COSObjectKey implements Comparable<COSObjectKey> {
    private final long number;
    private int generation;

    public COSObjectKey(COSObject object) {
        this(object.getObjectNumber(), object.getGenerationNumber());
    }

    public COSObjectKey(long num, int gen) {
        this.number = num;
        this.generation = gen;
    }

    public boolean equals(Object obj) {
        COSObjectKey objToBeCompared = obj instanceof COSObjectKey ? (COSObjectKey) obj : null;
        return objToBeCompared != null && objToBeCompared.getNumber() == getNumber() && objToBeCompared.getGeneration() == getGeneration();
    }

    public int getGeneration() {
        return this.generation;
    }

    public void fixGeneration(int genNumber) {
        this.generation = genNumber;
    }

    public long getNumber() {
        return this.number;
    }

    public int hashCode() {
        return Long.valueOf((this.number << 4) + ((long) this.generation)).hashCode();
    }

    public String toString() {
        return this.number + " " + this.generation + " R";
    }

    @Override // java.lang.Comparable
    public int compareTo(COSObjectKey other) {
        if (getNumber() < other.getNumber()) {
            return -1;
        }
        if (getNumber() > other.getNumber()) {
            return 1;
        }
        if (getGeneration() < other.getGeneration()) {
            return -1;
        }
        if (getGeneration() > other.getGeneration()) {
            return 1;
        }
        return 0;
    }
}
