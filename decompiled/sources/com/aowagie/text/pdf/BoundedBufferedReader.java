package com.aowagie.text.pdf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/BoundedBufferedReader.class */
public final class BoundedBufferedReader extends BufferedReader {
    private static final int DEFAULT_MAX_LINES = 4096;
    private static final int DEFAULT_MAX_LINE_LENGTH = 4096;
    private final int readerMaxLines;
    private final int readerMaxLineLen;
    private int currentLine;

    public BoundedBufferedReader(Reader reader, int maxLines, int maxLineLen) {
        super(reader);
        this.currentLine = 1;
        if (maxLines <= 0 || maxLineLen <= 0) {
            throw new IllegalArgumentException("BoundedBufferedReader - maxLines and maxLineLen must be greater than 0");
        }
        this.readerMaxLines = maxLines;
        this.readerMaxLineLen = maxLineLen;
    }

    public BoundedBufferedReader(Reader reader) {
        super(reader);
        this.currentLine = 1;
        this.readerMaxLines = 4096;
        this.readerMaxLineLen = 4096;
    }

    @Override // java.io.BufferedReader
    public String readLine() throws IOException {
        int currentCharVal;
        if (this.currentLine > this.readerMaxLines) {
            throw new IOException("BoundedBufferedReader - Line read limit has been reached.");
        }
        this.currentLine++;
        int currentPos = 0;
        char[] data = new char[this.readerMaxLineLen];
        int i = super.read();
        while (true) {
            currentCharVal = i;
            if (currentCharVal != 13 && currentCharVal != 10 && currentCharVal >= 0) {
                int i2 = currentPos;
                currentPos++;
                data[i2] = (char) currentCharVal;
                if (currentPos >= this.readerMaxLineLen) {
                    break;
                }
                i = super.read();
            } else {
                break;
            }
        }
        if (currentCharVal < 0) {
            if (currentPos > 0) {
                return new String(data, 0, currentPos);
            }
            return null;
        }
        if (currentCharVal == 13) {
            super.mark(1);
            if (super.read() != 10) {
                super.reset();
            }
        } else if (currentCharVal != 10) {
            super.mark(1);
            int nextCharVal = super.read();
            if (nextCharVal == 13) {
                super.mark(1);
                if (super.read() != 10) {
                    super.reset();
                }
            } else if (nextCharVal != 10) {
                super.reset();
            }
        }
        return new String(data, 0, currentPos);
    }
}
