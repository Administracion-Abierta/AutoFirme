package com.aowagie.text.pdf;

import es.gob.afirma.signvalidation.ISignatureFormatDetector;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfFileSpecification.class */
public class PdfFileSpecification extends PdfDictionary {
    private PdfWriter writer;
    private PdfIndirectReference ref;

    public PdfFileSpecification() {
        super(PdfName.FILESPEC);
    }

    public static PdfFileSpecification fileEmbedded(PdfWriter writer, String filePath, String fileDisplay, byte[] fileStore) throws IOException {
        return fileEmbedded(writer, filePath, fileDisplay, fileStore, 9);
    }

    private static PdfFileSpecification fileEmbedded(PdfWriter writer, String filePath, String fileDisplay, byte[] fileStore, int compressionLevel) throws IOException {
        return fileEmbedded(writer, filePath, fileDisplay, fileStore, null, null, compressionLevel);
    }

    private static PdfFileSpecification fileEmbedded(PdfWriter writer, String filePath, String fileDisplay, byte[] fileStore, String mimeType, PdfDictionary fileParameter, int compressionLevel) throws IOException {
        PdfEFStream stream;
        PdfFileSpecification fs = new PdfFileSpecification();
        fs.writer = writer;
        fs.put(PdfName.F, new PdfString(fileDisplay));
        fs.setUnicodeFileName(fileDisplay, false);
        InputStream in = null;
        try {
            PdfIndirectReference refFileLength = writer.getPdfIndirectReference();
            if (fileStore == null) {
                File file = new File(filePath);
                if (file.canRead()) {
                    in = new FileInputStream(filePath);
                } else if (filePath.startsWith("file:/") || filePath.startsWith("http://") || filePath.startsWith("https://") || filePath.startsWith("jar:")) {
                    in = new URL(filePath).openStream();
                } else {
                    in = BaseFont.getResourceStream(filePath);
                    if (in == null) {
                        throw new IOException(filePath + " not found as file or resource.");
                    }
                }
                stream = new PdfEFStream(in, writer);
            } else {
                stream = new PdfEFStream(fileStore);
            }
            stream.put(PdfName.TYPE, PdfName.EMBEDDEDFILE);
            stream.flateCompress(compressionLevel);
            stream.put(PdfName.PARAMS, refFileLength);
            if (mimeType != null) {
                stream.put(PdfName.SUBTYPE, new PdfName(mimeType));
            }
            PdfIndirectReference ref = writer.addToBody(stream).getIndirectReference();
            if (fileStore == null) {
                stream.writeLength();
            }
            PdfDictionary params = new PdfDictionary();
            if (fileParameter != null) {
                params.merge(fileParameter);
            }
            params.put(PdfName.SIZE, new PdfNumber(stream.getRawLength()));
            writer.addToBody(params, refFileLength);
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                }
            }
            PdfDictionary f = new PdfDictionary();
            f.put(PdfName.F, ref);
            f.put(PdfName.UF, ref);
            fs.put(PdfName.EF, f);
            return fs;
        } catch (Throwable th) {
            if (0 != 0) {
                try {
                    in.close();
                } catch (Exception e2) {
                }
            }
            throw th;
        }
    }

    public static PdfFileSpecification fileExtern(PdfWriter writer, String filePath) {
        PdfFileSpecification fs = new PdfFileSpecification();
        fs.writer = writer;
        fs.put(PdfName.F, new PdfString(filePath));
        fs.setUnicodeFileName(filePath, false);
        return fs;
    }

    public PdfIndirectReference getReference() throws IOException {
        if (this.ref != null) {
            return this.ref;
        }
        this.ref = this.writer.addToBody(this).getIndirectReference();
        return this.ref;
    }

    public void setMultiByteFileName(byte[] fileName) {
        put(PdfName.F, new PdfString(fileName).setHexWriting(true));
    }

    private void setUnicodeFileName(String filename, boolean unicode) {
        put(PdfName.UF, new PdfString(filename, unicode ? PdfObject.TEXT_UNICODE : ISignatureFormatDetector.FORMAT_PDF));
    }

    public void setVolatile(boolean volatile_file) {
        put(PdfName.V, new PdfBoolean(volatile_file));
    }

    void addDescription(String description, boolean unicode) {
        put(PdfName.DESC, new PdfString(description, unicode ? PdfObject.TEXT_UNICODE : ISignatureFormatDetector.FORMAT_PDF));
    }
}
