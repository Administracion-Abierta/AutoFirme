package com.aowagie.text.pdf;

import com.aowagie.text.DocWriter;
import com.aowagie.text.Document;
import com.aowagie.text.Rectangle;
import com.twelvemonkeys.imageio.metadata.tiff.TIFF;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/pdf/PdfContents.class */
class PdfContents extends PdfStream {
    static final byte[] SAVESTATE = DocWriter.getISOBytes("q\n");
    static final byte[] RESTORESTATE = DocWriter.getISOBytes("Q\n");
    static final byte[] ROTATE90 = DocWriter.getISOBytes("0 1 -1 0 ");
    static final byte[] ROTATE180 = DocWriter.getISOBytes("-1 0 0 -1 ");
    static final byte[] ROTATE270 = DocWriter.getISOBytes("0 -1 1 0 ");
    static final byte[] ROTATEFINAL = DocWriter.getISOBytes(" cm\n");

    PdfContents(PdfContentByte under, PdfContentByte content, PdfContentByte text, PdfContentByte secondContent, Rectangle page) throws BadPdfFormatException {
        OutputStream out;
        try {
            Deflater deflater = null;
            this.streamBytes = new ByteArrayOutputStream();
            if (Document.compress) {
                this.compressed = true;
                this.compressionLevel = text.getPdfWriter().getCompressionLevel();
                deflater = new Deflater(this.compressionLevel);
                out = new DeflaterOutputStream(this.streamBytes, deflater);
            } else {
                out = this.streamBytes;
            }
            int rotation = page.getRotation();
            switch (rotation) {
                case 90:
                    out.write(ROTATE90);
                    out.write(DocWriter.getISOBytes(ByteBuffer.formatDouble(page.getTop())));
                    out.write(32);
                    out.write(48);
                    out.write(ROTATEFINAL);
                    break;
                case 180:
                    out.write(ROTATE180);
                    out.write(DocWriter.getISOBytes(ByteBuffer.formatDouble(page.getRight())));
                    out.write(32);
                    out.write(DocWriter.getISOBytes(ByteBuffer.formatDouble(page.getTop())));
                    out.write(ROTATEFINAL);
                    break;
                case TIFF.TAG_IMAGE_DESCRIPTION /* 270 */:
                    out.write(ROTATE270);
                    out.write(48);
                    out.write(32);
                    out.write(DocWriter.getISOBytes(ByteBuffer.formatDouble(page.getRight())));
                    out.write(ROTATEFINAL);
                    break;
            }
            if (under.size() > 0) {
                out.write(SAVESTATE);
                under.getInternalBuffer().writeTo(out);
                out.write(RESTORESTATE);
            }
            if (content.size() > 0) {
                out.write(SAVESTATE);
                content.getInternalBuffer().writeTo(out);
                out.write(RESTORESTATE);
            }
            if (text != null) {
                out.write(SAVESTATE);
                text.getInternalBuffer().writeTo(out);
                out.write(RESTORESTATE);
            }
            if (secondContent.size() > 0) {
                secondContent.getInternalBuffer().writeTo(out);
            }
            out.close();
            if (deflater != null) {
                deflater.end();
            }
            put(PdfName.LENGTH, new PdfNumber(this.streamBytes.size()));
            if (this.compressed) {
                put(PdfName.FILTER, PdfName.FLATEDECODE);
            }
        } catch (Exception e) {
            throw new BadPdfFormatException(e.getMessage());
        }
    }
}
