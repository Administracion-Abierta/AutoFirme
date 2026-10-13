package org.apache.pdfbox.pdmodel.fdf;

import java.io.IOException;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.filespecification.PDFileSpecification;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/fdf/FDFNamedPageReference.class */
public class FDFNamedPageReference implements COSObjectable {
    private final COSDictionary ref;

    public FDFNamedPageReference() {
        this.ref = new COSDictionary();
    }

    public FDFNamedPageReference(COSDictionary r) {
        this.ref = r;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.ref;
    }

    public String getName() {
        return this.ref.getString(COSName.NAME);
    }

    public void setName(String name) {
        this.ref.setString(COSName.NAME, name);
    }

    public PDFileSpecification getFileSpecification() throws IOException {
        return PDFileSpecification.createFS(this.ref.getDictionaryObject(COSName.F));
    }

    public void setFileSpecification(PDFileSpecification fs) {
        this.ref.setItem(COSName.F, fs);
    }
}
