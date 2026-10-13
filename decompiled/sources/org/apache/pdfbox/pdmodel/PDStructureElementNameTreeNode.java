package org.apache.pdfbox.pdmodel;

import java.io.IOException;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.common.PDNameTreeNode;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureElement;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/PDStructureElementNameTreeNode.class */
public class PDStructureElementNameTreeNode extends PDNameTreeNode<PDStructureElement> {
    public PDStructureElementNameTreeNode() {
    }

    public PDStructureElementNameTreeNode(COSDictionary dic) {
        super(dic);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.pdfbox.pdmodel.common.PDNameTreeNode
    public PDStructureElement convertCOSToPD(COSBase base) throws IOException {
        return new PDStructureElement((COSDictionary) base);
    }

    @Override // org.apache.pdfbox.pdmodel.common.PDNameTreeNode
    protected PDNameTreeNode<PDStructureElement> createChildNode(COSDictionary dic) {
        return new PDStructureElementNameTreeNode(dic);
    }
}
