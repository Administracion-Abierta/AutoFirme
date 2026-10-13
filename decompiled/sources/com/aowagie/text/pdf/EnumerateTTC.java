package com.aowagie.text.pdf;

import com.aowagie.text.DocumentException;
import java.io.IOException;
import java.util.HashMap;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/EnumerateTTC.class */
class EnumerateTTC extends TrueTypeFont {
    protected String[] names;

    EnumerateTTC(String ttcFile) throws IOException, DocumentException {
        this.fileName = ttcFile;
        this.rf = new RandomAccessFileOrArray(ttcFile);
        findNames();
    }

    EnumerateTTC(byte[] ttcArray) throws IOException, DocumentException {
        this.fileName = "Byte array TTC";
        this.rf = new RandomAccessFileOrArray(ttcArray);
        findNames();
    }

    void findNames() throws IOException, DocumentException {
        this.tables = new HashMap();
        try {
            String mainTag = readStandardString(4);
            if (!mainTag.equals("ttcf")) {
                throw new DocumentException(this.fileName + " is not a valid TTC file.");
            }
            this.rf.skipBytes(4);
            int dirCount = this.rf.readInt();
            this.names = new String[dirCount];
            int dirPos = this.rf.getFilePointer();
            for (int dirIdx = 0; dirIdx < dirCount; dirIdx++) {
                this.tables.clear();
                this.rf.seek(dirPos);
                this.rf.skipBytes(dirIdx * 4);
                this.directoryOffset = this.rf.readInt();
                this.rf.seek(this.directoryOffset);
                if (this.rf.readInt() != 65536) {
                    throw new DocumentException(this.fileName + " is not a valid TTF file.");
                }
                int num_tables = this.rf.readUnsignedShort();
                this.rf.skipBytes(6);
                for (int k = 0; k < num_tables; k++) {
                    String tag = readStandardString(4);
                    this.rf.skipBytes(4);
                    int[] table_location = {this.rf.readInt(), this.rf.readInt()};
                    this.tables.put(tag, table_location);
                }
                this.names[dirIdx] = getBaseFont();
            }
            if (this.rf != null) {
                this.rf.close();
            }
        } catch (Throwable th) {
            if (this.rf != null) {
                this.rf.close();
            }
            throw th;
        }
    }

    String[] getNames() {
        return this.names;
    }
}
