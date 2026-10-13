package org.apache.fontbox.cff;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/cff/CFFDataInput.class */
public class CFFDataInput extends DataInput {
    public CFFDataInput(byte[] buffer) {
        super(buffer);
    }

    public int readCard8() throws IOException {
        return readUnsignedByte();
    }

    public int readCard16() throws IOException {
        return readUnsignedShort();
    }

    public int readOffset(int offSize) throws IOException {
        int value = 0;
        for (int i = 0; i < offSize; i++) {
            value = (value << 8) | readUnsignedByte();
        }
        return value;
    }

    public int readOffSize() throws IOException {
        int offSize = readUnsignedByte();
        if (offSize < 1 || offSize > 4) {
            throw new IOException("Illegal (< 1 or > 4) offSize value " + offSize + " in CFF font at position " + (getPosition() - 1));
        }
        return offSize;
    }

    public int readSID() throws IOException {
        return readUnsignedShort();
    }
}
