package org.apache.pdfbox.pdmodel.interactive.form;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.util.XMLUtil;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/form/PDXFAResource.class */
public final class PDXFAResource implements COSObjectable {
    private static final int BUFFER_SIZE = 1024;
    private final COSBase xfa;

    public PDXFAResource(COSBase xfaBase) {
        this.xfa = xfaBase;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSBase getCOSObject() {
        return this.xfa;
    }

    public byte[] getBytes() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = null;
        try {
            if (getCOSObject() instanceof COSArray) {
                byte[] xfaBytes = new byte[1024];
                COSArray cosArray = (COSArray) getCOSObject();
                for (int i = 1; i < cosArray.size(); i += 2) {
                    COSBase cosObj = cosArray.getObject(i);
                    if (cosObj instanceof COSStream) {
                        is = ((COSStream) cosObj).createInputStream();
                        while (true) {
                            int nRead = is.read(xfaBytes, 0, xfaBytes.length);
                            if (nRead == -1) {
                                break;
                            }
                            baos.write(xfaBytes, 0, nRead);
                        }
                        baos.flush();
                    }
                }
            } else if (this.xfa.getCOSObject() instanceof COSStream) {
                byte[] xfaBytes2 = new byte[1024];
                is = ((COSStream) this.xfa.getCOSObject()).createInputStream();
                while (true) {
                    int nRead2 = is.read(xfaBytes2, 0, xfaBytes2.length);
                    if (nRead2 == -1) {
                        break;
                    }
                    baos.write(xfaBytes2, 0, nRead2);
                }
                baos.flush();
            }
            if (is != null) {
                is = is;
            }
            return baos.toByteArray();
        } finally {
            if (0 != 0) {
                is.close();
            }
        }
    }

    public Document getDocument() throws ParserConfigurationException, SAXException, IOException {
        return XMLUtil.parse(new ByteArrayInputStream(getBytes()), true);
    }
}
