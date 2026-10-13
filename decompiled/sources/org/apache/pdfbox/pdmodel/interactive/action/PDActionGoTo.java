package org.apache.pdfbox.pdmodel.interactive.action;

import java.io.IOException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/action/PDActionGoTo.class */
public class PDActionGoTo extends PDAction {
    public static final String SUB_TYPE = "GoTo";

    public PDActionGoTo() {
        setSubType(SUB_TYPE);
    }

    public PDActionGoTo(COSDictionary a) {
        super(a);
    }

    public PDDestination getDestination() throws IOException {
        return PDDestination.create(getCOSObject().getDictionaryObject(COSName.D));
    }

    public void setDestination(PDDestination d) {
        if (d instanceof PDPageDestination) {
            PDPageDestination pageDest = (PDPageDestination) d;
            COSArray destArray = pageDest.getCOSObject();
            if (destArray.size() >= 1) {
                COSBase page = destArray.getObject(0);
                if (!(page instanceof COSDictionary)) {
                    throw new IllegalArgumentException("Destination of a GoTo action must be a page dictionary object");
                }
            }
        }
        getCOSObject().setItem(COSName.D, d);
    }
}
