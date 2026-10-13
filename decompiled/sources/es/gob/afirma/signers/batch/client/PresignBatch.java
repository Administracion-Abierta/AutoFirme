package es.gob.afirma.signers.batch.client;

import es.gob.afirma.core.signers.TriphaseData;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/batch/client/PresignBatch.class */
public class PresignBatch {
    private TriphaseData triphaseData;
    private List<BatchDataResult> errors;

    public PresignBatch(TriphaseData triphaseData, List<BatchDataResult> list) {
        this.triphaseData = triphaseData;
        this.errors = list;
    }

    public TriphaseData getTriphaseData() {
        return this.triphaseData;
    }

    public List<BatchDataResult> getErrors() {
        return this.errors;
    }

    public void setTriphaseData(TriphaseData triphaseData) {
        this.triphaseData = triphaseData;
    }

    public void setErrors(List<BatchDataResult> list) {
        this.errors = list;
    }
}
