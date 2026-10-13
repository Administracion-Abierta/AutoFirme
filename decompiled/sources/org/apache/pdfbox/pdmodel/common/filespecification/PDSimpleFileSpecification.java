package org.apache.pdfbox.pdmodel.common.filespecification;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSString;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/filespecification/PDSimpleFileSpecification.class */
public class PDSimpleFileSpecification extends PDFileSpecification {
    private COSString file;

    public PDSimpleFileSpecification() {
        this.file = new COSString("");
    }

    public PDSimpleFileSpecification(COSString fileName) {
        this.file = fileName;
    }

    @Override // org.apache.pdfbox.pdmodel.common.filespecification.PDFileSpecification
    public String getFile() {
        return this.file.getString();
    }

    @Override // org.apache.pdfbox.pdmodel.common.filespecification.PDFileSpecification
    public void setFile(String fileName) {
        this.file = new COSString(fileName);
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSBase getCOSObject() {
        return this.file;
    }
}
