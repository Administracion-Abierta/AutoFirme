package es.gob.afirma.core.misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/misc/BoundedBufferedReader.class */
public final class BoundedBufferedReader extends BufferedReader {
    private static final int DEFAULT_MAX_LINES = 4096;
    private static final int DEFAULT_MAX_LINE_LENGTH = 4096;
    private final int readerMaxLines;
    private final int readerMaxLineLen;
    private int currentLine;

    public BoundedBufferedReader(Reader reader, int i, int i2) {
        super(reader);
        this.currentLine = 1;
        if (i <= 0 || i2 <= 0) {
            throw new IllegalArgumentException("BoundedBufferedReader - maxLines and maxLineLen must be greater than 0");
        }
        this.readerMaxLines = i;
        this.readerMaxLineLen = i2;
    }

    public BoundedBufferedReader(Reader reader) {
        super(reader);
        this.currentLine = 1;
        this.readerMaxLines = 4096;
        this.readerMaxLineLen = 4096;
    }

    @Override // java.io.BufferedReader
    public String readLine() throws IOException {
        int i;
        if (this.currentLine > this.readerMaxLines) {
            throw new IOException("BoundedBufferedReader - Line read limit has been reached.");
        }
        this.currentLine++;
        int i2 = 0;
        char[] cArr = new char[this.readerMaxLineLen];
        int i3 = super.read();
        while (true) {
            i = i3;
            if (i != 13 && i != 10 && i >= 0) {
                int i4 = i2;
                i2++;
                cArr[i4] = (char) i;
                if (i2 >= this.readerMaxLineLen) {
                    break;
                }
                i3 = super.read();
            } else {
                break;
            }
        }
        if (i < 0) {
            if (i2 > 0) {
                return new String(cArr, 0, i2);
            }
            return null;
        }
        if (i == 13) {
            super.mark(1);
            if (super.read() != 10) {
                super.reset();
            }
        } else if (i != 10) {
            super.mark(1);
            int i5 = super.read();
            if (i5 == 13) {
                super.mark(1);
                if (super.read() != 10) {
                    super.reset();
                }
            } else if (i5 != 10) {
                super.reset();
            }
        }
        return new String(cArr, 0, i2);
    }
}
