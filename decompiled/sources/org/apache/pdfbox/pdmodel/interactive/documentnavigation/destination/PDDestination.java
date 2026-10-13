package org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination;

import java.io.IOException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.common.PDDestinationOrAction;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/documentnavigation/destination/PDDestination.class */
public abstract class PDDestination implements PDDestinationOrAction {
    public static PDDestination create(COSBase base) throws IOException {
        PDDestination retval = null;
        if (base != null) {
            if ((base instanceof COSArray) && ((COSArray) base).size() > 1 && (((COSArray) base).getObject(1) instanceof COSName)) {
                COSArray array = (COSArray) base;
                COSName type = (COSName) array.getObject(1);
                String typeString = type.getName();
                if (typeString.equals("Fit") || typeString.equals("FitB")) {
                    retval = new PDPageFitDestination(array);
                } else if (typeString.equals("FitV") || typeString.equals("FitBV")) {
                    retval = new PDPageFitHeightDestination(array);
                } else if (typeString.equals("FitR")) {
                    retval = new PDPageFitRectangleDestination(array);
                } else if (typeString.equals("FitH") || typeString.equals("FitBH")) {
                    retval = new PDPageFitWidthDestination(array);
                } else if (typeString.equals("XYZ")) {
                    retval = new PDPageXYZDestination(array);
                } else {
                    throw new IOException("Unknown destination type: " + type.getName());
                }
            } else if (base instanceof COSString) {
                retval = new PDNamedDestination((COSString) base);
            } else if (base instanceof COSName) {
                retval = new PDNamedDestination((COSName) base);
            } else {
                throw new IOException("Error: can't convert to Destination " + base);
            }
        }
        return retval;
    }
}
