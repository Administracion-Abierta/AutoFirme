package org.apache.pdfbox.pdmodel;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/PDDocumentNameDictionary.class */
public class PDDocumentNameDictionary implements COSObjectable {
    private final COSDictionary nameDictionary;
    private final PDDocumentCatalog catalog;

    public PDDocumentNameDictionary(PDDocumentCatalog cat) {
        COSBase names = cat.getCOSObject().getDictionaryObject(COSName.NAMES);
        if (names != null) {
            this.nameDictionary = (COSDictionary) names;
        } else {
            this.nameDictionary = new COSDictionary();
            cat.getCOSObject().setItem(COSName.NAMES, (COSBase) this.nameDictionary);
        }
        this.catalog = cat;
    }

    public PDDocumentNameDictionary(PDDocumentCatalog cat, COSDictionary names) {
        this.catalog = cat;
        this.nameDictionary = names;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.nameDictionary;
    }

    public PDDestinationNameTreeNode getDests() {
        PDDestinationNameTreeNode dests = null;
        COSDictionary dic = (COSDictionary) this.nameDictionary.getDictionaryObject(COSName.DESTS);
        if (dic == null) {
            dic = (COSDictionary) this.catalog.getCOSObject().getDictionaryObject(COSName.DESTS);
        }
        if (dic != null) {
            dests = new PDDestinationNameTreeNode(dic);
        }
        return dests;
    }

    public void setDests(PDDestinationNameTreeNode dests) {
        this.nameDictionary.setItem(COSName.DESTS, dests);
        this.catalog.getCOSObject().setItem(COSName.DESTS, (COSObjectable) null);
    }

    public PDEmbeddedFilesNameTreeNode getEmbeddedFiles() {
        PDEmbeddedFilesNameTreeNode retval = null;
        COSDictionary dic = (COSDictionary) this.nameDictionary.getDictionaryObject(COSName.EMBEDDED_FILES);
        if (dic != null) {
            retval = new PDEmbeddedFilesNameTreeNode(dic);
        }
        return retval;
    }

    public void setEmbeddedFiles(PDEmbeddedFilesNameTreeNode ef) {
        this.nameDictionary.setItem(COSName.EMBEDDED_FILES, ef);
    }

    public PDJavascriptNameTreeNode getJavaScript() {
        PDJavascriptNameTreeNode retval = null;
        COSDictionary dic = (COSDictionary) this.nameDictionary.getDictionaryObject(COSName.JAVA_SCRIPT);
        if (dic != null) {
            retval = new PDJavascriptNameTreeNode(dic);
        }
        return retval;
    }

    public void setJavascript(PDJavascriptNameTreeNode js) {
        this.nameDictionary.setItem(COSName.JAVA_SCRIPT, js);
    }
}
