package org.apache.pdfbox.pdmodel;

import java.io.IOException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDDestination;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/PDDocumentNameDestinationDictionary.class */
public class PDDocumentNameDestinationDictionary implements COSObjectable {
    private final COSDictionary nameDictionary;

    public PDDocumentNameDestinationDictionary(COSDictionary dict) {
        this.nameDictionary = dict;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.nameDictionary;
    }

    public PDDestination getDestination(String name) throws IOException {
        COSBase item = this.nameDictionary.getDictionaryObject(name);
        if (item instanceof COSArray) {
            return PDDestination.create(item);
        }
        if (item instanceof COSDictionary) {
            COSDictionary dict = (COSDictionary) item;
            if (dict.containsKey(COSName.D)) {
                return PDDestination.create(dict.getDictionaryObject(COSName.D));
            }
            return null;
        }
        return null;
    }
}
