package org.apache.pdfbox.pdmodel.interactive.digitalsignature;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessBuffer;
import org.apache.pdfbox.io.RandomAccessBufferedFileInputStream;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible.PDVisibleSigProperties;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/digitalsignature/SignatureOptions.class */
public class SignatureOptions implements Closeable {
    private COSDocument visualSignature;
    private int preferredSignatureSize;
    public static final int DEFAULT_SIGNATURE_SIZE = 9472;
    private RandomAccessRead pdfSource = null;
    private int pageNo = 0;

    public void setPage(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPage() {
        return this.pageNo;
    }

    public void setVisualSignature(File file) throws IOException {
        initFromRandomAccessRead(new RandomAccessBufferedFileInputStream(file));
    }

    public void setVisualSignature(InputStream is) throws IOException {
        initFromRandomAccessRead(new RandomAccessBuffer(is));
    }

    private void initFromRandomAccessRead(RandomAccessRead rar) throws IOException {
        this.pdfSource = rar;
        PDFParser parser = new PDFParser(this.pdfSource);
        parser.parse();
        this.visualSignature = parser.getDocument();
    }

    public void setVisualSignature(PDVisibleSigProperties visSignatureProperties) throws IOException {
        setVisualSignature(visSignatureProperties.getVisibleSignature());
    }

    public COSDocument getVisualSignature() {
        return this.visualSignature;
    }

    public int getPreferredSignatureSize() {
        return this.preferredSignatureSize;
    }

    public void setPreferredSignatureSize(int size) {
        if (size > 0) {
            this.preferredSignatureSize = size;
        }
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        try {
            if (this.visualSignature != null) {
                this.visualSignature.close();
            }
        } finally {
            if (this.pdfSource != null) {
                this.pdfSource.close();
            }
        }
    }
}
