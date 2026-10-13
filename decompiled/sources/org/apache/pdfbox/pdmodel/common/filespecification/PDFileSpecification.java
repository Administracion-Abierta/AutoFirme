package org.apache.pdfbox.pdmodel.common.filespecification;

import java.io.IOException;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/filespecification/PDFileSpecification.class */
public abstract class PDFileSpecification implements COSObjectable {
    public abstract String getFile();

    public abstract void setFile(String str);

    public static PDFileSpecification createFS(COSBase base) throws IOException {
        PDFileSpecification retval = null;
        if (base != null) {
            if (base instanceof COSString) {
                retval = new PDSimpleFileSpecification((COSString) base);
            } else if (base instanceof COSDictionary) {
                retval = new PDComplexFileSpecification((COSDictionary) base);
            } else {
                throw new IOException("Error: Unknown file specification " + base);
            }
        }
        return retval;
    }
}
