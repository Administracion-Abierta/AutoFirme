package es.gob.afirma.standalone.ui;

import java.io.File;
import java.security.cert.X509Certificate;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/SignatureResultViewer.class */
public interface SignatureResultViewer {
    void showResultsInfo(List<SignOperationConfig> list, File file, X509Certificate x509Certificate);

    void showResultsInfo(byte[] bArr, SignOperationConfig signOperationConfig, X509Certificate x509Certificate);
}
