package es.gob.afirma.standalone.plugins;

import java.io.File;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/plugins/InputData.class */
public class InputData {
    private File dataFile;
    private String signatureFormat;

    public File getDataFile() {
        return this.dataFile;
    }

    public void setDataFile(File file) {
        this.dataFile = file;
    }

    public String getSignatureFormat() {
        return this.signatureFormat;
    }

    public void setSignatureFormat(String str) {
        this.signatureFormat = str;
    }
}
