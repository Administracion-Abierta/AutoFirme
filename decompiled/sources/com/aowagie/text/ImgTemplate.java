package com.aowagie.text;

import com.aowagie.text.pdf.PdfTemplate;
import java.net.URL;

/* JADX INFO: loaded from: autofirma.jar:com/aowagie/text/ImgTemplate.class */
public class ImgTemplate extends Image {
    ImgTemplate(Image image) {
        super(image);
    }

    ImgTemplate(PdfTemplate template) throws BadElementException {
        super((URL) null);
        if (template == null) {
            throw new BadElementException("The template can not be null.");
        }
        if (template.getType() == 3) {
            throw new BadElementException("A pattern can not be used as a template to create an image.");
        }
        this.type = 35;
        this.scaledHeight = template.getHeight();
        setTop(this.scaledHeight);
        this.scaledWidth = template.getWidth();
        setRight(this.scaledWidth);
        setTemplateData(template);
        this.plainWidth = getWidth();
        this.plainHeight = getHeight();
    }
}
