package com.sun.java.help.search;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/Entry.class */
class Entry {
    public byte[] key;
    public int id;
    public int block = -1;

    public Entry(byte[] bArr, int i, int i2) {
        this.key = new byte[i + 1];
        System.arraycopy(bArr, 0, this.key, 0, i);
        this.key[i] = 0;
        this.id = i2;
    }

    public byte[] getKey() {
        return this.key;
    }

    public int getID() {
        return this.id;
    }

    public boolean smallerThan(Entry entry) {
        for (int i = 0; i < Math.min(this.key.length, entry.key.length); i++) {
            if (this.key[i] != entry.key[i]) {
                return (this.key[i] & 255) - (entry.key[i] & 255) < 0;
            }
        }
        return false;
    }
}
