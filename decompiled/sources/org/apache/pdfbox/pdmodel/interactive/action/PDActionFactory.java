package org.apache.pdfbox.pdmodel.interactive.action;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/action/PDActionFactory.class */
public final class PDActionFactory {
    private PDActionFactory() {
    }

    public static PDAction createAction(COSDictionary action) {
        PDAction retval = null;
        if (action != null) {
            String type = action.getNameAsString(COSName.S);
            if (PDActionJavaScript.SUB_TYPE.equals(type)) {
                retval = new PDActionJavaScript(action);
            } else if (PDActionGoTo.SUB_TYPE.equals(type)) {
                retval = new PDActionGoTo(action);
            } else if (PDActionLaunch.SUB_TYPE.equals(type)) {
                retval = new PDActionLaunch(action);
            } else if (PDActionRemoteGoTo.SUB_TYPE.equals(type)) {
                retval = new PDActionRemoteGoTo(action);
            } else if ("URI".equals(type)) {
                retval = new PDActionURI(action);
            } else if (PDActionNamed.SUB_TYPE.equals(type)) {
                retval = new PDActionNamed(action);
            } else if ("Sound".equals(type)) {
                retval = new PDActionSound(action);
            } else if (PDActionMovie.SUB_TYPE.equals(type)) {
                retval = new PDActionMovie(action);
            } else if (PDActionImportData.SUB_TYPE.equals(type)) {
                retval = new PDActionImportData(action);
            } else if (PDActionResetForm.SUB_TYPE.equals(type)) {
                retval = new PDActionResetForm(action);
            } else if (PDActionHide.SUB_TYPE.equals(type)) {
                retval = new PDActionHide(action);
            } else if (PDActionSubmitForm.SUB_TYPE.equals(type)) {
                retval = new PDActionSubmitForm(action);
            } else if (PDActionThread.SUB_TYPE.equals(type)) {
                retval = new PDActionThread(action);
            } else if (PDActionEmbeddedGoTo.SUB_TYPE.equals(type)) {
                retval = new PDActionEmbeddedGoTo(action);
            }
        }
        return retval;
    }
}
