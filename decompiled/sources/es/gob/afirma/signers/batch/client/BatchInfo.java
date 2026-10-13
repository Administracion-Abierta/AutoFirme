package es.gob.afirma.signers.batch.client;

import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/batch/client/BatchInfo.class */
public interface BatchInfo {
    void updateResults(List<BatchDataResult> list);

    String getInfoString();
}
