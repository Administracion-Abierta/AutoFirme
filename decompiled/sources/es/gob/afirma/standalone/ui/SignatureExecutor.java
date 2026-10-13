package es.gob.afirma.standalone.ui;

import java.security.KeyStore;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/SignatureExecutor.class */
public interface SignatureExecutor {
    void initSignTask(List<SignOperationConfig> list);

    void relaunchTask(KeyStore.PrivateKeyEntry privateKeyEntry, List<SignOperationConfig> list);

    void finishTask();
}
