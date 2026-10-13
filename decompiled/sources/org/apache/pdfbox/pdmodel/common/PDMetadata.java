package org.apache.pdfbox.pdmodel.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDDocument;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/PDMetadata.class */
public class PDMetadata extends PDStream {
    public PDMetadata(PDDocument document) {
        super(document);
        getCOSObject().setName(COSName.TYPE, "Metadata");
        getCOSObject().setName(COSName.SUBTYPE, "XML");
    }

    public PDMetadata(PDDocument doc, InputStream str) throws IOException {
        super(doc, str);
        getCOSObject().setName(COSName.TYPE, "Metadata");
        getCOSObject().setName(COSName.SUBTYPE, "XML");
    }

    public PDMetadata(COSStream str) {
        super(str);
    }

    public InputStream exportXMPMetadata() throws IOException {
        return createInputStream();
    }

    public void importXMPMetadata(byte[] xmp) throws IOException {
        OutputStream os = createOutputStream();
        os.write(xmp);
        os.close();
    }
}
