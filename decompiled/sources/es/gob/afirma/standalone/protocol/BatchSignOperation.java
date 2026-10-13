package es.gob.afirma.standalone.protocol;

import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/protocol/BatchSignOperation.class */
public class BatchSignOperation {
    private final boolean stopOnError;
    private final List<SingleSignOperation> signs;

    public BatchSignOperation(boolean z, List<SingleSignOperation> list) {
        this.stopOnError = z;
        this.signs = list;
    }

    public boolean isStopOnError() {
        return this.stopOnError;
    }

    public List<SingleSignOperation> getSigns() {
        return this.signs;
    }
}
