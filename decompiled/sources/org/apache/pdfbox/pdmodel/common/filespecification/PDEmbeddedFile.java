package org.apache.pdfbox.pdmodel.common.filespecification;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDStream;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/common/filespecification/PDEmbeddedFile.class */
public class PDEmbeddedFile extends PDStream {
    public PDEmbeddedFile(PDDocument document) {
        super(document);
        getCOSObject().setName(COSName.TYPE, "EmbeddedFile");
    }

    public PDEmbeddedFile(COSStream str) {
        super(str);
    }

    public PDEmbeddedFile(PDDocument doc, InputStream str) throws IOException {
        super(doc, str);
        getCOSObject().setName(COSName.TYPE, "EmbeddedFile");
    }

    public PDEmbeddedFile(PDDocument doc, InputStream input, COSName filter) throws IOException {
        super(doc, input, filter);
        getCOSObject().setName(COSName.TYPE, "EmbeddedFile");
    }

    public void setSubtype(String mimeType) {
        getCOSObject().setName(COSName.SUBTYPE, mimeType);
    }

    public String getSubtype() {
        return getCOSObject().getNameAsString(COSName.SUBTYPE);
    }

    public int getSize() {
        return getCOSObject().getEmbeddedInt("Params", "Size");
    }

    public void setSize(int size) {
        getCOSObject().setEmbeddedInt("Params", "Size", size);
    }

    public Calendar getCreationDate() throws IOException {
        return getCOSObject().getEmbeddedDate("Params", "CreationDate");
    }

    public void setCreationDate(Calendar creation) {
        getCOSObject().setEmbeddedDate("Params", "CreationDate", creation);
    }

    public Calendar getModDate() throws IOException {
        return getCOSObject().getEmbeddedDate("Params", "ModDate");
    }

    public void setModDate(Calendar mod) {
        getCOSObject().setEmbeddedDate("Params", "ModDate", mod);
    }

    public String getCheckSum() {
        return getCOSObject().getEmbeddedString("Params", "CheckSum");
    }

    public void setCheckSum(String checksum) {
        getCOSObject().setEmbeddedString("Params", "CheckSum", checksum);
    }

    public String getMacSubtype() {
        String retval = null;
        COSDictionary params = (COSDictionary) getCOSObject().getDictionaryObject(COSName.PARAMS);
        if (params != null) {
            retval = params.getEmbeddedString("Mac", "Subtype");
        }
        return retval;
    }

    public void setMacSubtype(String macSubtype) {
        COSDictionary params = (COSDictionary) getCOSObject().getDictionaryObject(COSName.PARAMS);
        if (params == null && macSubtype != null) {
            params = new COSDictionary();
            getCOSObject().setItem(COSName.PARAMS, (COSBase) params);
        }
        if (params != null) {
            params.setEmbeddedString("Mac", "Subtype", macSubtype);
        }
    }

    public String getMacCreator() {
        String retval = null;
        COSDictionary params = (COSDictionary) getCOSObject().getDictionaryObject(COSName.PARAMS);
        if (params != null) {
            retval = params.getEmbeddedString("Mac", "Creator");
        }
        return retval;
    }

    public void setMacCreator(String macCreator) {
        COSDictionary params = (COSDictionary) getCOSObject().getDictionaryObject(COSName.PARAMS);
        if (params == null && macCreator != null) {
            params = new COSDictionary();
            getCOSObject().setItem(COSName.PARAMS, (COSBase) params);
        }
        if (params != null) {
            params.setEmbeddedString("Mac", "Creator", macCreator);
        }
    }

    public String getMacResFork() {
        String retval = null;
        COSDictionary params = (COSDictionary) getCOSObject().getDictionaryObject(COSName.PARAMS);
        if (params != null) {
            retval = params.getEmbeddedString("Mac", "ResFork");
        }
        return retval;
    }

    public void setMacResFork(String macResFork) {
        COSDictionary params = (COSDictionary) getCOSObject().getDictionaryObject(COSName.PARAMS);
        if (params == null && macResFork != null) {
            params = new COSDictionary();
            getCOSObject().setItem(COSName.PARAMS, (COSBase) params);
        }
        if (params != null) {
            params.setEmbeddedString("Mac", "ResFork", macResFork);
        }
    }
}
