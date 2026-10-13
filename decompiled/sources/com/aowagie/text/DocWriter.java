package com.aowagie.text;

import com.aowagie.text.pdf.OutputStreamCounter;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/DocWriter.class */
public abstract class DocWriter implements DocListener {
    private static final byte SPACE = 32;
    private static final byte EQUALS = 61;
    private static final byte QUOTE = 34;
    private static final byte GT = 62;
    private static final byte FORWARD = 47;
    protected OutputStreamCounter os;
    protected boolean open = false;
    private final boolean pause = false;
    private boolean closeStream = true;

    protected DocWriter() {
    }

    protected DocWriter(Document document, OutputStream os) {
        this.os = new OutputStreamCounter(new BufferedOutputStream(os));
    }

    @Override // com.aowagie.text.ElementListener
    public boolean add(Element element) throws DocumentException {
        return false;
    }

    @Override // com.aowagie.text.DocListener
    public void open() {
        this.open = true;
    }

    @Override // com.aowagie.text.DocListener
    public boolean setPageSize(Rectangle pageSize) {
        return true;
    }

    @Override // com.aowagie.text.DocListener
    public boolean setMargins(float marginLeft, float marginRight, float marginTop, float marginBottom) {
        return false;
    }

    @Override // com.aowagie.text.DocListener
    public boolean newPage() {
        if (!this.open) {
            return false;
        }
        return true;
    }

    @Override // com.aowagie.text.DocListener
    public void setHeader(HeaderFooter header) {
    }

    @Override // com.aowagie.text.DocListener
    public void resetHeader() {
    }

    @Override // com.aowagie.text.DocListener
    public void setFooter(HeaderFooter footer) {
    }

    @Override // com.aowagie.text.DocListener
    public void resetFooter() {
    }

    @Override // com.aowagie.text.DocListener
    public void resetPageCount() {
    }

    @Override // com.aowagie.text.DocListener
    public void setPageCount(int pageN) {
    }

    @Override // com.aowagie.text.DocListener
    public void close() {
        this.open = false;
        try {
            this.os.flush();
            if (this.closeStream) {
                this.os.close();
            }
        } catch (IOException ioe) {
            throw new ExceptionConverter(ioe);
        }
    }

    public static final byte[] getISOBytes(String text) {
        if (text == null) {
            return null;
        }
        int len = text.length();
        byte[] b = new byte[len];
        for (int k = 0; k < len; k++) {
            b[k] = (byte) text.charAt(k);
        }
        return b;
    }

    public boolean isPaused() {
        getClass();
        return false;
    }

    private void write(String string) throws IOException {
        this.os.write(getISOBytes(string));
    }

    private void write(String key, String value) throws IOException {
        this.os.write(32);
        write(key);
        this.os.write(61);
        this.os.write(34);
        write(value);
        this.os.write(34);
    }

    public boolean isCloseStream() {
        return this.closeStream;
    }

    public void setCloseStream(boolean closeStream) {
        this.closeStream = closeStream;
    }

    @Override // com.aowagie.text.DocListener
    public boolean setMarginMirroring(boolean MarginMirroring) {
        return false;
    }

    @Override // com.aowagie.text.DocListener
    public boolean setMarginMirroringTopBottom(boolean MarginMirroring) {
        return false;
    }
}
