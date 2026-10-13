package org.apache.pdfbox.pdmodel;

import java.io.IOException;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.PDNameTreeNode;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/PDDestinationNameTreeNode.class */
public class PDDestinationNameTreeNode extends PDNameTreeNode<PDPageDestination> {
    public PDDestinationNameTreeNode() {
    }

    public PDDestinationNameTreeNode(COSDictionary dic) {
        super(dic);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.pdfbox.pdmodel.common.PDNameTreeNode
    public PDPageDestination convertCOSToPD(COSBase base) throws IOException {
        COSBase destination = base;
        if (base instanceof COSDictionary) {
            destination = ((COSDictionary) base).getDictionaryObject(COSName.D);
        }
        return (PDPageDestination) PDDestination.create(destination);
    }

    @Override // org.apache.pdfbox.pdmodel.common.PDNameTreeNode
    protected PDNameTreeNode<PDPageDestination> createChildNode(COSDictionary dic) {
        return new PDDestinationNameTreeNode(dic);
    }
}
