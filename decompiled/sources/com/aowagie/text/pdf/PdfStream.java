package com.aowagie.text.pdf;

import com.aowagie.text.DocWriter;
import com.aowagie.text.Document;
import com.aowagie.text.ExceptionConverter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfStream.class */
public class PdfStream extends PdfDictionary {
    public static final int DEFAULT_COMPRESSION = -1;
    public static final int NO_COMPRESSION = 0;
    public static final int BEST_COMPRESSION = 9;
    protected InputStream inputStream;
    private PdfIndirectReference ref;
    private PdfWriter writer;
    protected int rawLength;
    static final byte[] STARTSTREAM = DocWriter.getISOBytes("stream\n");
    static final byte[] ENDSTREAM = DocWriter.getISOBytes("\nendstream");
    protected boolean compressed = false;
    protected int compressionLevel = 0;
    protected ByteArrayOutputStream streamBytes = null;
    protected int inputStreamLength = -1;

    public PdfStream(byte[] bytes) {
        this.type = 7;
        this.bytes = bytes;
        this.rawLength = bytes.length;
        put(PdfName.LENGTH, new PdfNumber(bytes.length));
    }

    PdfStream(InputStream inputStream, PdfWriter writer) {
        this.type = 7;
        this.inputStream = inputStream;
        this.writer = writer;
        this.ref = writer.getPdfIndirectReference();
        put(PdfName.LENGTH, this.ref);
    }

    protected PdfStream() {
        this.type = 7;
    }

    void writeLength() throws IOException {
        if (this.inputStream == null) {
            throw new UnsupportedOperationException("writeLength() can only be called in a contructed PdfStream(InputStream,PdfWriter).");
        }
        if (this.inputStreamLength == -1) {
            throw new IOException("writeLength() can only be called after output of the stream body.");
        }
        this.writer.addToBody((PdfObject) new PdfNumber(this.inputStreamLength), this.ref, false);
    }

    public int getRawLength() {
        return this.rawLength;
    }

    void flateCompress(int compressionLevel) {
        if (!Document.compress || this.compressed) {
            return;
        }
        this.compressionLevel = compressionLevel;
        if (this.inputStream != null) {
            this.compressed = true;
            return;
        }
        PdfObject filter = PdfReader.getPdfObject(get(PdfName.FILTER));
        if (filter != null) {
            if (filter.isName()) {
                if (PdfName.FLATEDECODE.equals(filter)) {
                    return;
                }
            } else if (filter.isArray()) {
                if (((PdfArray) filter).contains(PdfName.FLATEDECODE)) {
                    return;
                }
            } else {
                throw new RuntimeException("Stream could not be compressed: filter is not a name or array.");
            }
        }
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Deflater deflater = new Deflater(compressionLevel);
            DeflaterOutputStream zip = new DeflaterOutputStream(stream, deflater);
            if (this.streamBytes != null) {
                this.streamBytes.writeTo(zip);
            } else {
                zip.write(this.bytes);
            }
            zip.close();
            deflater.end();
            this.streamBytes = stream;
            this.bytes = null;
            put(PdfName.LENGTH, new PdfNumber(this.streamBytes.size()));
            if (filter == null) {
                put(PdfName.FILTER, PdfName.FLATEDECODE);
            } else {
                PdfArray filters = new PdfArray(filter);
                filters.add(PdfName.FLATEDECODE);
                put(PdfName.FILTER, filters);
            }
            this.compressed = true;
        } catch (IOException ioe) {
            throw new ExceptionConverter(ioe);
        }
    }

    protected void superToPdf(PdfWriter writer, OutputStream os) throws IOException {
        super.toPdf(writer, os);
    }

    @Override // com.aowagie.text.pdf.PdfDictionary, com.aowagie.text.pdf.PdfObject
    public void toPdf(PdfWriter writer, OutputStream os) throws IOException {
        byte[] b;
        PdfObject filter;
        if (this.inputStream != null && this.compressed) {
            put(PdfName.FILTER, PdfName.FLATEDECODE);
        }
        PdfEncryption crypto = null;
        if (writer != null) {
            crypto = writer.getEncryption();
        }
        if (crypto != null && (filter = get(PdfName.FILTER)) != null) {
            if (PdfName.CRYPT.equals(filter)) {
                crypto = null;
            } else if (filter.isArray()) {
                PdfArray a = (PdfArray) filter;
                if (!a.isEmpty() && PdfName.CRYPT.equals(a.getPdfObject(0))) {
                    crypto = null;
                }
            }
        }
        PdfObject nn = get(PdfName.LENGTH);
        if (crypto != null && nn != null && nn.isNumber()) {
            int sz = ((PdfNumber) nn).intValue();
            put(PdfName.LENGTH, new PdfNumber(crypto.calculateStreamSize(sz)));
            superToPdf(writer, os);
            put(PdfName.LENGTH, nn);
        } else {
            superToPdf(writer, os);
        }
        os.write(STARTSTREAM);
        if (this.inputStream != null) {
            this.rawLength = 0;
            DeflaterOutputStream def = null;
            OutputStreamCounter osc = new OutputStreamCounter(os);
            OutputStreamEncryption ose = null;
            OutputStream fout = osc;
            if (crypto != null && !crypto.isEmbeddedFilesOnly()) {
                OutputStreamEncryption encryptionStream = crypto.getEncryptionStream(fout);
                ose = encryptionStream;
                fout = encryptionStream;
            }
            Deflater deflater = null;
            if (this.compressed) {
                deflater = new Deflater(this.compressionLevel);
                DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(fout, deflater, 32768);
                def = deflaterOutputStream;
                fout = deflaterOutputStream;
            }
            byte[] buf = new byte[4192];
            while (true) {
                int n = this.inputStream.read(buf);
                if (n <= 0) {
                    break;
                }
                fout.write(buf, 0, n);
                this.rawLength += n;
            }
            if (def != null) {
                def.finish();
                deflater.end();
            }
            if (ose != null) {
                ose.finish();
            }
            this.inputStreamLength = osc.getCounter();
        } else if (crypto != null && !crypto.isEmbeddedFilesOnly()) {
            if (this.streamBytes != null) {
                b = crypto.encryptByteArray(this.streamBytes.toByteArray());
            } else {
                b = crypto.encryptByteArray(this.bytes);
            }
            os.write(b);
        } else if (this.streamBytes != null) {
            this.streamBytes.writeTo(os);
        } else {
            os.write(this.bytes);
        }
        os.write(ENDSTREAM);
    }

    void writeContent(OutputStream os) throws IOException {
        if (this.streamBytes != null) {
            this.streamBytes.writeTo(os);
        } else if (this.bytes != null) {
            os.write(this.bytes);
        }
    }

    @Override // com.aowagie.text.pdf.PdfDictionary, com.aowagie.text.pdf.PdfObject
    public String toString() {
        if (get(PdfName.TYPE) == null) {
            return "Stream";
        }
        return "Stream of type: " + get(PdfName.TYPE);
    }
}
