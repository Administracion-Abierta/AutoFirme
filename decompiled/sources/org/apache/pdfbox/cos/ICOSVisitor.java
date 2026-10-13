package org.apache.pdfbox.cos;

import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/cos/ICOSVisitor.class */
public interface ICOSVisitor {
    Object visitFromArray(COSArray cOSArray) throws IOException;

    Object visitFromBoolean(COSBoolean cOSBoolean) throws IOException;

    Object visitFromDictionary(COSDictionary cOSDictionary) throws IOException;

    Object visitFromDocument(COSDocument cOSDocument) throws IOException;

    Object visitFromFloat(COSFloat cOSFloat) throws IOException;

    Object visitFromInt(COSInteger cOSInteger) throws IOException;

    Object visitFromName(COSName cOSName) throws IOException;

    Object visitFromNull(COSNull cOSNull) throws IOException;

    Object visitFromStream(COSStream cOSStream) throws IOException;

    Object visitFromString(COSString cOSString) throws IOException;
}
