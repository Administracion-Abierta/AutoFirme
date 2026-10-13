package es.gob.afirma.signers.batch.client;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/batch/client/BatchDataResult.class */
public class BatchDataResult {
    private final Result result;
    private final String description;
    private final String signId;

    /* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/batch/client/BatchDataResult$Result.class */
    public enum Result {
        NOT_STARTED,
        DONE_AND_SAVED,
        DONE_BUT_NOT_SAVED_YET,
        DONE_BUT_SAVED_SKIPPED,
        DONE_BUT_ERROR_SAVING,
        ERROR_PRE,
        ERROR_POST,
        SKIPPED,
        SAVE_ROLLBACKED
    }

    public boolean wasSaved() {
        return Result.DONE_AND_SAVED.equals(this.result);
    }

    public BatchDataResult(String str, Result result, String str2) {
        if (str == null) {
            throw new IllegalArgumentException("El identificador no puede ser nulo");
        }
        if (result == null) {
            throw new IllegalArgumentException("El resultado no puede ser nulo");
        }
        this.signId = str;
        this.result = result;
        this.description = str2;
    }

    public String getId() {
        return this.signId;
    }

    public Result getResult() {
        return this.result;
    }

    public String getDescription() {
        return this.description;
    }
}
