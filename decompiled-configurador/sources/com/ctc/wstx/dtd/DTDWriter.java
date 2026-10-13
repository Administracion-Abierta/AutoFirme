package com.ctc.wstx.dtd;

import com.ctc.wstx.exc.WstxIOException;
import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.XMLStreamException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/dtd/DTDWriter.class */
final class DTDWriter {
    final Writer mWriter;
    final boolean mIncludeComments;
    final boolean mIncludeConditionals;
    final boolean mIncludePEs;
    int mIsFlattening;
    int mFlattenStart = 0;

    public DTDWriter(Writer out, boolean inclComments, boolean inclCond, boolean inclPEs) {
        this.mIsFlattening = 0;
        this.mWriter = out;
        this.mIncludeComments = inclComments;
        this.mIncludeConditionals = inclCond;
        this.mIncludePEs = inclPEs;
        this.mIsFlattening = 1;
    }

    public boolean includeComments() {
        return this.mIncludeComments;
    }

    public boolean includeConditionals() {
        return this.mIncludeConditionals;
    }

    public boolean includeParamEntities() {
        return this.mIncludePEs;
    }

    public void disableOutput() {
        this.mIsFlattening--;
    }

    public void enableOutput(int newStart) {
        this.mIsFlattening++;
        this.mFlattenStart = newStart;
    }

    public void setFlattenStart(int ptr) {
        this.mFlattenStart = ptr;
    }

    public int getFlattenStart() {
        return this.mFlattenStart;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void flush(char[] buf, int upUntil) throws XMLStreamException {
        if (this.mFlattenStart < upUntil) {
            if (this.mIsFlattening > 0) {
                try {
                    this.mWriter.write(buf, this.mFlattenStart, upUntil - this.mFlattenStart);
                } catch (IOException ioe) {
                    throw new WstxIOException(ioe);
                }
            }
            this.mFlattenStart = upUntil;
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void output(String output) throws XMLStreamException {
        if (this.mIsFlattening > 0) {
            try {
                this.mWriter.write(output);
            } catch (IOException ioe) {
                throw new WstxIOException(ioe);
            }
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.xml.stream.XMLStreamException */
    public void output(char c) throws XMLStreamException {
        if (this.mIsFlattening > 0) {
            try {
                this.mWriter.write(c);
            } catch (IOException ioe) {
                throw new WstxIOException(ioe);
            }
        }
    }
}
