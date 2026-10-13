package com.ctc.wstx.io;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.util.TextBuffer;
import java.io.IOException;
import java.io.Reader;
import javax.xml.stream.XMLStreamException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/io/BranchingReaderSource.class */
public final class BranchingReaderSource extends ReaderSource {
    TextBuffer mBranchBuffer;
    int mBranchStartOffset;
    boolean mConvertLFs;
    boolean mGotCR;

    public BranchingReaderSource(ReaderConfig cfg, String pubId, SystemId sysId, Reader r, boolean realClose) {
        super(cfg, null, null, pubId, sysId, r, realClose);
        this.mBranchBuffer = null;
        this.mBranchStartOffset = 0;
        this.mConvertLFs = false;
        this.mGotCR = false;
    }

    @Override // com.ctc.wstx.io.ReaderSource, com.ctc.wstx.io.BaseInputSource, com.ctc.wstx.io.WstxInputSource
    public int readInto(WstxInputData reader) throws XMLStreamException, IOException {
        if (this.mBranchBuffer != null) {
            if (this.mInputLast > this.mBranchStartOffset) {
                appendBranched(this.mBranchStartOffset, this.mInputLast);
            }
            this.mBranchStartOffset = 0;
        }
        return super.readInto(reader);
    }

    @Override // com.ctc.wstx.io.ReaderSource, com.ctc.wstx.io.BaseInputSource, com.ctc.wstx.io.WstxInputSource
    public boolean readMore(WstxInputData reader, int minAmount) throws XMLStreamException, IOException {
        if (this.mBranchBuffer != null) {
            int ptr = reader.mInputPtr;
            int currAmount = this.mInputLast - ptr;
            if (currAmount > 0) {
                if (ptr > this.mBranchStartOffset) {
                    appendBranched(this.mBranchStartOffset, ptr);
                }
                this.mBranchStartOffset = 0;
            }
        }
        return super.readMore(reader, minAmount);
    }

    public void startBranch(TextBuffer tb, int startOffset, boolean convertLFs) {
        this.mBranchBuffer = tb;
        this.mBranchStartOffset = startOffset;
        this.mConvertLFs = convertLFs;
        this.mGotCR = false;
    }

    public void endBranch(int endOffset) {
        if (this.mBranchBuffer != null) {
            if (endOffset > this.mBranchStartOffset) {
                appendBranched(this.mBranchStartOffset, endOffset);
            }
            this.mBranchBuffer = null;
        }
    }

    private void appendBranched(int startOffset, int pastEnd) {
        if (this.mConvertLFs) {
            char[] inBuf = this.mBuffer;
            char[] outBuf = this.mBranchBuffer.getCurrentSegment();
            int outPtr = this.mBranchBuffer.getCurrentSegmentSize();
            if (this.mGotCR && inBuf[startOffset] == '\n') {
                startOffset++;
            }
            while (startOffset < pastEnd) {
                int i = startOffset;
                startOffset++;
                char c = inBuf[i];
                if (c == '\r') {
                    if (startOffset < pastEnd) {
                        if (inBuf[startOffset] == '\n') {
                            startOffset++;
                        }
                    } else {
                        this.mGotCR = true;
                    }
                    c = '\n';
                }
                int i2 = outPtr;
                outPtr++;
                outBuf[i2] = c;
                if (outPtr >= outBuf.length) {
                    outBuf = this.mBranchBuffer.finishCurrentSegment();
                    outPtr = 0;
                }
            }
            this.mBranchBuffer.setCurrentLength(outPtr);
            return;
        }
        this.mBranchBuffer.append(this.mBuffer, startOffset, pastEnd - startOffset);
    }
}
