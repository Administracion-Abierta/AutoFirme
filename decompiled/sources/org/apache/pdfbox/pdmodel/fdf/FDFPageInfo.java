package org.apache.pdfbox.pdmodel.fdf;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/fdf/FDFPageInfo.class */
public class FDFPageInfo implements COSObjectable {
    private final COSDictionary pageInfo;

    public FDFPageInfo() {
        this.pageInfo = new COSDictionary();
    }

    public FDFPageInfo(COSDictionary p) {
        this.pageInfo = p;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.pageInfo;
    }
}
