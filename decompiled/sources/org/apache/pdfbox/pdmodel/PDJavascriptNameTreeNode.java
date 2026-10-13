package org.apache.pdfbox.pdmodel;

import java.io.IOException;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.common.PDNameTreeNode;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionFactory;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionJavaScript;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/PDJavascriptNameTreeNode.class */
public class PDJavascriptNameTreeNode extends PDNameTreeNode<PDActionJavaScript> {
    public PDJavascriptNameTreeNode() {
    }

    public PDJavascriptNameTreeNode(COSDictionary dic) {
        super(dic);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // org.apache.pdfbox.pdmodel.common.PDNameTreeNode
    public PDActionJavaScript convertCOSToPD(COSBase base) throws IOException {
        if (!(base instanceof COSDictionary)) {
            throw new IOException("Error creating Javascript object, expected a COSDictionary and not " + base);
        }
        return (PDActionJavaScript) PDActionFactory.createAction((COSDictionary) base);
    }

    @Override // org.apache.pdfbox.pdmodel.common.PDNameTreeNode
    protected PDNameTreeNode<PDActionJavaScript> createChildNode(COSDictionary dic) {
        return new PDJavascriptNameTreeNode(dic);
    }
}
