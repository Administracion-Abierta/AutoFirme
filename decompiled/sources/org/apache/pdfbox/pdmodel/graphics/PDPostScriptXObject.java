package org.apache.pdfbox.pdmodel.graphics;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/graphics/PDPostScriptXObject.class */
public class PDPostScriptXObject extends PDXObject {
    public PDPostScriptXObject(COSStream stream) {
        super(stream, COSName.PS);
    }
}
