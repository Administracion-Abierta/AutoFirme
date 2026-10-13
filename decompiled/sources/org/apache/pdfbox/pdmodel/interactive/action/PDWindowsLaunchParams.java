package org.apache.pdfbox.pdmodel.interactive.action;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/action/PDWindowsLaunchParams.class */
public class PDWindowsLaunchParams implements COSObjectable {
    public static final String OPERATION_OPEN = "open";
    public static final String OPERATION_PRINT = "print";
    protected COSDictionary params;

    public PDWindowsLaunchParams() {
        this.params = new COSDictionary();
    }

    public PDWindowsLaunchParams(COSDictionary p) {
        this.params = p;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.params;
    }

    public String getFilename() {
        return this.params.getString(COSName.F);
    }

    public void setFilename(String file) {
        this.params.setString(COSName.F, file);
    }

    public String getDirectory() {
        return this.params.getString(COSName.D);
    }

    public void setDirectory(String dir) {
        this.params.setString(COSName.D, dir);
    }

    public String getOperation() {
        return this.params.getString(COSName.O, OPERATION_OPEN);
    }

    public void setOperation(String op) {
        this.params.setString(COSName.D, op);
    }

    public String getExecuteParam() {
        return this.params.getString(COSName.P);
    }

    public void setExecuteParam(String param) {
        this.params.setString(COSName.P, param);
    }
}
