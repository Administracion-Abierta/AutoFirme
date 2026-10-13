package org.apache.pdfbox.pdmodel.interactive.action;

import java.io.IOException;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.filespecification.PDFileSpecification;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/action/PDActionThread.class */
public class PDActionThread extends PDAction {
    public static final String SUB_TYPE = "Thread";

    public PDActionThread() {
        setSubType(SUB_TYPE);
    }

    public PDActionThread(COSDictionary a) {
        super(a);
    }

    public COSBase getD() {
        return this.action.getDictionaryObject(COSName.D);
    }

    public void setD(COSBase d) {
        this.action.setItem(COSName.D, d);
    }

    public PDFileSpecification getFile() throws IOException {
        return PDFileSpecification.createFS(this.action.getDictionaryObject(COSName.F));
    }

    public void setFile(PDFileSpecification fs) {
        this.action.setItem(COSName.F, fs);
    }

    public COSBase getB() {
        return this.action.getDictionaryObject(COSName.B);
    }

    public void setB(COSBase b) {
        this.action.setItem(COSName.B, b);
    }
}
