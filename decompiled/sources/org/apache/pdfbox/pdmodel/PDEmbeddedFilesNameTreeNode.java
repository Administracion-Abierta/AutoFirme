package org.apache.pdfbox.pdmodel;

import java.io.IOException;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.common.PDNameTreeNode;
import org.apache.pdfbox.pdmodel.common.filespecification.PDComplexFileSpecification;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/PDEmbeddedFilesNameTreeNode.class */
public class PDEmbeddedFilesNameTreeNode extends PDNameTreeNode<PDComplexFileSpecification> {
    public PDEmbeddedFilesNameTreeNode() {
    }

    public PDEmbeddedFilesNameTreeNode(COSDictionary dic) {
        super(dic);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.pdfbox.pdmodel.common.PDNameTreeNode
    public PDComplexFileSpecification convertCOSToPD(COSBase base) throws IOException {
        return new PDComplexFileSpecification((COSDictionary) base);
    }

    @Override // org.apache.pdfbox.pdmodel.common.PDNameTreeNode
    protected PDNameTreeNode<PDComplexFileSpecification> createChildNode(COSDictionary dic) {
        return new PDEmbeddedFilesNameTreeNode(dic);
    }
}
