package org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/documentnavigation/outline/PDDocumentOutline.class */
public final class PDDocumentOutline extends PDOutlineNode {
    public PDDocumentOutline() {
        getCOSObject().setName(COSName.TYPE, COSName.OUTLINES.getName());
    }

    public PDDocumentOutline(COSDictionary dic) {
        super(dic);
        getCOSObject().setName(COSName.TYPE, COSName.OUTLINES.getName());
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineNode
    public boolean isNodeOpen() {
        return true;
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineNode
    public void openNode() {
    }

    @Override // org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineNode
    public void closeNode() {
    }
}
