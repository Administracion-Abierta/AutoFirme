package org.apache.pdfbox.pdmodel.fdf;

import java.io.IOException;
import java.io.Writer;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/fdf/FDFCatalog.class */
public class FDFCatalog implements COSObjectable {
    private COSDictionary catalog;

    public FDFCatalog() {
        this.catalog = new COSDictionary();
    }

    public FDFCatalog(COSDictionary cat) {
        this.catalog = cat;
    }

    public FDFCatalog(Element element) {
        this();
        FDFDictionary fdfDict = new FDFDictionary(element);
        setFDF(fdfDict);
    }

    public void writeXML(Writer output) throws IOException {
        FDFDictionary fdf = getFDF();
        fdf.writeXML(output);
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.catalog;
    }

    public String getVersion() {
        return this.catalog.getNameAsString(COSName.VERSION);
    }

    public void setVersion(String version) {
        this.catalog.setName(COSName.VERSION, version);
    }

    public FDFDictionary getFDF() {
        FDFDictionary retval;
        COSDictionary fdf = (COSDictionary) this.catalog.getDictionaryObject(COSName.FDF);
        if (fdf != null) {
            retval = new FDFDictionary(fdf);
        } else {
            retval = new FDFDictionary();
            setFDF(retval);
        }
        return retval;
    }

    public void setFDF(FDFDictionary fdf) {
        this.catalog.setItem(COSName.FDF, fdf);
    }

    public PDSignature getSignature() {
        PDSignature signature = null;
        COSDictionary sig = (COSDictionary) this.catalog.getDictionaryObject(COSName.SIG);
        if (sig != null) {
            signature = new PDSignature(sig);
        }
        return signature;
    }

    public void setSignature(PDSignature sig) {
        this.catalog.setItem(COSName.SIG, sig);
    }
}
