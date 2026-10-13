package org.apache.pdfbox.pdmodel.interactive.action;

import java.io.IOException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSBoolean;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.filespecification.PDFileSpecification;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/action/PDActionEmbeddedGoTo.class */
public class PDActionEmbeddedGoTo extends PDAction {
    public static final String SUB_TYPE = "GoToE";

    public PDActionEmbeddedGoTo() {
        setSubType(SUB_TYPE);
    }

    public PDActionEmbeddedGoTo(COSDictionary a) {
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
                    throw new IllegalArgumentException("Destination of a GoToE action must be a page dictionary object");
                }
            }
        }
        getCOSObject().setItem(COSName.D, d);
    }

    public PDFileSpecification getFile() throws IOException {
        return PDFileSpecification.createFS(getCOSObject().getDictionaryObject(COSName.F));
    }

    public void setFile(PDFileSpecification fs) {
        getCOSObject().setItem(COSName.F, fs);
    }

    public OpenMode getOpenInNewWindow() {
        if (getCOSObject().getDictionaryObject(COSName.NEW_WINDOW) instanceof COSBoolean) {
            COSBoolean b = (COSBoolean) getCOSObject().getDictionaryObject(COSName.NEW_WINDOW);
            return b.getValue() ? OpenMode.NEW_WINDOW : OpenMode.SAME_WINDOW;
        }
        return OpenMode.USER_PREFERENCE;
    }

    public void setOpenInNewWindow(OpenMode value) {
        if (null == value) {
            getCOSObject().removeItem(COSName.NEW_WINDOW);
        }
        switch (value) {
            case USER_PREFERENCE:
                getCOSObject().removeItem(COSName.NEW_WINDOW);
                break;
            case SAME_WINDOW:
                getCOSObject().setBoolean(COSName.NEW_WINDOW, false);
                break;
            case NEW_WINDOW:
                getCOSObject().setBoolean(COSName.NEW_WINDOW, true);
                break;
        }
    }

    public PDTargetDirectory getTargetDirectory() {
        COSBase base = getCOSObject().getDictionaryObject(COSName.T);
        if (base instanceof COSDictionary) {
            return new PDTargetDirectory((COSDictionary) base);
        }
        return null;
    }

    public void setTargetDirectory(PDTargetDirectory targetDirectory) {
        getCOSObject().setItem(COSName.T, targetDirectory);
    }
}
