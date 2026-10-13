package es.gob.afirma.standalone.ui.restoreconfig;

import java.io.File;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/restoreconfig/CertFilesInfo.class */
public class CertFilesInfo {
    private File sslCertFile;
    private File sslRootFile;
    private File sslKeyStoreFile;

    public File getSslCertFile() {
        return this.sslCertFile;
    }

    public void setSslCertFile(File file) {
        this.sslCertFile = file;
    }

    public File getSslRootFile() {
        return this.sslRootFile;
    }

    public void setSslRootFile(File file) {
        this.sslRootFile = file;
    }

    public File getSslKeyStoreFile() {
        return this.sslKeyStoreFile;
    }

    public void setSslKeyStoreFile(File file) {
        this.sslKeyStoreFile = file;
    }
}
