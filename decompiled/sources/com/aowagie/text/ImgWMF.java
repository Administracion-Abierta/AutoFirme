package com.aowagie.text;

import com.aowagie.text.pdf.PdfTemplate;
import com.aowagie.text.pdf.codec.wmf.InputMeta;
import com.aowagie.text.pdf.codec.wmf.MetaDo;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/ImgWMF.class */
public class ImgWMF extends Image {
    ImgWMF(Image image) {
        super(image);
    }

    ImgWMF(URL url) throws BadElementException, IOException {
        super(url);
        processParameters();
    }

    ImgWMF(byte[] img) throws BadElementException, IOException {
        super((URL) null);
        this.rawData = img;
        this.originalData = img;
        processParameters();
    }

    private void processParameters() throws BadElementException, IOException {
        InputStream is;
        String errorID;
        this.type = 35;
        this.originalType = 6;
        InputStream is2 = null;
        try {
            if (this.rawData == null) {
                is = this.url.openStream();
                errorID = this.url.toString();
            } else {
                is = new ByteArrayInputStream(this.rawData);
                errorID = "Byte array";
            }
            InputMeta in = new InputMeta(is);
            if (in.readInt() != -1698247209) {
                throw new BadElementException(errorID + " is not a valid placeable windows metafile.");
            }
            in.readWord();
            int left = in.readShort();
            int top = in.readShort();
            int right = in.readShort();
            int bottom = in.readShort();
            int inch = in.readWord();
            this.dpiX = 72;
            this.dpiY = 72;
            this.scaledHeight = ((bottom - top) / inch) * 72.0f;
            setTop(this.scaledHeight);
            this.scaledWidth = ((right - left) / inch) * 72.0f;
            setRight(this.scaledWidth);
            if (is != null) {
                is.close();
            }
            this.plainWidth = getWidth();
            this.plainHeight = getHeight();
        } catch (Throwable th) {
            if (0 != 0) {
                is2.close();
            }
            this.plainWidth = getWidth();
            this.plainHeight = getHeight();
            throw th;
        }
    }

    public void readWMF(PdfTemplate template) throws IOException, DocumentException {
        setTemplateData(template);
        template.setWidth(getWidth());
        template.setHeight(getHeight());
        InputStream is = null;
        try {
            if (this.rawData == null) {
                is = this.url.openStream();
            } else {
                is = new ByteArrayInputStream(this.rawData);
            }
            MetaDo meta = new MetaDo(is, template);
            meta.readAll();
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
}
