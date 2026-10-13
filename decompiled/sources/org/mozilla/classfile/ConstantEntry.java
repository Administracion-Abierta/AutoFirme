package org.mozilla.classfile;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/classfile/ConstantEntry.class */
final class ConstantEntry {
    private int type;
    private int intval;
    private long longval;
    private String str1;
    private String str2;
    private int hashcode;

    ConstantEntry(int type, int intval, String str1, String str2) {
        this.type = type;
        this.intval = intval;
        this.str1 = str1;
        this.str2 = str2;
        this.hashcode = type ^ (intval + (str1.hashCode() * str2.hashCode()));
    }

    public int hashCode() {
        return this.hashcode;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ConstantEntry)) {
            return false;
        }
        ConstantEntry entry = (ConstantEntry) obj;
        if (this.type != entry.type) {
            return false;
        }
        switch (this.type) {
            case 3:
            case 4:
                return this.intval == entry.intval;
            case 5:
            case 6:
                return this.longval == entry.longval;
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            default:
                throw new RuntimeException("unsupported constant type");
            case 12:
                return this.str1.equals(entry.str1) && this.str2.equals(entry.str2);
            case 18:
                return this.intval == entry.intval && this.str1.equals(entry.str1) && this.str2.equals(entry.str2);
        }
    }
}
