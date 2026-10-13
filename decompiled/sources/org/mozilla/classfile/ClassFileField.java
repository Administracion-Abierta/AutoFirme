package org.mozilla.classfile;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/classfile/ClassFileField.class */
final class ClassFileField {
    private short itsNameIndex;
    private short itsTypeIndex;
    private short itsFlags;
    private boolean itsHasAttributes = false;
    private short itsAttr1;
    private short itsAttr2;
    private short itsAttr3;
    private int itsIndex;

    ClassFileField(short nameIndex, short typeIndex, short flags) {
        this.itsNameIndex = nameIndex;
        this.itsTypeIndex = typeIndex;
        this.itsFlags = flags;
    }

    void setAttributes(short attr1, short attr2, short attr3, int index) {
        this.itsHasAttributes = true;
        this.itsAttr1 = attr1;
        this.itsAttr2 = attr2;
        this.itsAttr3 = attr3;
        this.itsIndex = index;
    }

    int write(byte[] data, int offset) {
        int offset2;
        int offset3 = ClassFileWriter.putInt16(this.itsTypeIndex, data, ClassFileWriter.putInt16(this.itsNameIndex, data, ClassFileWriter.putInt16(this.itsFlags, data, offset)));
        if (!this.itsHasAttributes) {
            offset2 = ClassFileWriter.putInt16(0, data, offset3);
        } else {
            offset2 = ClassFileWriter.putInt16(this.itsIndex, data, ClassFileWriter.putInt16(this.itsAttr3, data, ClassFileWriter.putInt16(this.itsAttr2, data, ClassFileWriter.putInt16(this.itsAttr1, data, ClassFileWriter.putInt16(1, data, offset3)))));
        }
        return offset2;
    }

    int getWriteSize() {
        int size;
        if (!this.itsHasAttributes) {
            size = 6 + 2;
        } else {
            size = 6 + 10;
        }
        return size;
    }
}
