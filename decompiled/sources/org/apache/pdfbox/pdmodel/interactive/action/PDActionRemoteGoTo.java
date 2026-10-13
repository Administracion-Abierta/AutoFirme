package org.apache.pdfbox.pdmodel.interactive.action;

import java.io.IOException;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSBoolean;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.filespecification.PDFileSpecification;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/action/PDActionRemoteGoTo.class */
public class PDActionRemoteGoTo extends PDAction {
    public static final String SUB_TYPE = "GoToR";

    public PDActionRemoteGoTo() {
        setSubType(SUB_TYPE);
    }

    public PDActionRemoteGoTo(COSDictionary a) {
        super(a);
    }

    @Deprecated
    public String getS() {
        return this.action.getNameAsString(COSName.S);
    }

    @Deprecated
    public void setS(String s) {
        this.action.setName(COSName.S, s);
    }

    public PDFileSpecification getFile() throws IOException {
        return PDFileSpecification.createFS(this.action.getDictionaryObject(COSName.F));
    }

    public void setFile(PDFileSpecification fs) {
        this.action.setItem(COSName.F, fs);
    }

    public COSBase getD() {
        return this.action.getDictionaryObject(COSName.D);
    }

    public void setD(COSBase d) {
        this.action.setItem(COSName.D, d);
    }

    @Deprecated
    public boolean shouldOpenInNewWindow() {
        return this.action.getBoolean(COSName.NEW_WINDOW, true);
    }

    @Deprecated
    public void setOpenInNewWindow(boolean value) {
        this.action.setBoolean(COSName.NEW_WINDOW, value);
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
}
