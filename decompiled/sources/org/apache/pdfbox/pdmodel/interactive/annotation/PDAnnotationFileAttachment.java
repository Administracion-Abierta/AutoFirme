package org.apache.pdfbox.pdmodel.interactive.annotation;

import java.io.IOException;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.filespecification.PDFileSpecification;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/annotation/PDAnnotationFileAttachment.class */
public class PDAnnotationFileAttachment extends PDAnnotationMarkup {
    public static final String ATTACHMENT_NAME_PUSH_PIN = "PushPin";
    public static final String ATTACHMENT_NAME_GRAPH = "Graph";
    public static final String ATTACHMENT_NAME_PAPERCLIP = "Paperclip";
    public static final String ATTACHMENT_NAME_TAG = "Tag";
    public static final String SUB_TYPE = "FileAttachment";

    public PDAnnotationFileAttachment() {
        getCOSObject().setName(COSName.SUBTYPE, "FileAttachment");
    }

    public PDAnnotationFileAttachment(COSDictionary field) {
        super(field);
    }

    public PDFileSpecification getFile() throws IOException {
        return PDFileSpecification.createFS(getCOSObject().getDictionaryObject("FS"));
    }

    public void setFile(PDFileSpecification file) {
        getCOSObject().setItem("FS", file);
    }

    public String getAttachmentName() {
        return getCOSObject().getNameAsString(COSName.NAME, ATTACHMENT_NAME_PUSH_PIN);
    }

    @Deprecated
    public void setAttachementName(String name) {
        getCOSObject().setName(COSName.NAME, name);
    }

    public void setAttachmentName(String name) {
        getCOSObject().setName(COSName.NAME, name);
    }
}
