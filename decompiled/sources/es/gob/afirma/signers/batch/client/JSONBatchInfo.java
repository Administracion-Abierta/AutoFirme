package es.gob.afirma.signers.batch.client;

import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/batch/client/JSONBatchInfo.class */
public class JSONBatchInfo implements BatchInfo {
    private static final String JSELEM_SINGLESIGNS = "singlesigns";
    private static final String JSELEM_DATAREFERENCE = "datareference";
    private static final String JSELEM_FORMAT = "format";
    private static final String JSELEM_SUBOPERATION = "suboperation";
    private static final String JSELEM_EXTRAPARAMS = "extraparams";
    private static final String JSELEM_ID = "id";
    private static final String JSELEM_RESULT = "result";
    private static final String JSELEM_DESCRIPTION = "description";
    private final JSONObject batch;

    public JSONBatchInfo(JSONObject jSONObject) {
        this.batch = jSONObject;
    }

    @Override // es.gob.afirma.signers.batch.client.BatchInfo
    public void updateResults(List<BatchDataResult> list) {
        JSONArray jSONArray = this.batch.getJSONArray(JSELEM_SINGLESIGNS);
        for (int i = 0; i < jSONArray.length(); i++) {
            JSONObject jSONObject = jSONArray.getJSONObject(i);
            String string = jSONObject.getString("id");
            boolean z = false;
            for (int i2 = 0; i2 < list.size() && !z; i2++) {
                BatchDataResult batchDataResult = list.get(i2);
                if (batchDataResult.getId().equals(string)) {
                    jSONObject.remove(JSELEM_DATAREFERENCE);
                    jSONObject.remove("format");
                    jSONObject.remove(JSELEM_SUBOPERATION);
                    jSONObject.remove(JSELEM_EXTRAPARAMS);
                    jSONObject.put(JSELEM_RESULT, batchDataResult.getResult().name());
                    if (batchDataResult.getDescription() != null) {
                        jSONObject.put(JSELEM_DESCRIPTION, batchDataResult.getDescription());
                    }
                    z = true;
                }
            }
        }
    }

    @Override // es.gob.afirma.signers.batch.client.BatchInfo
    public String getInfoString() {
        return this.batch.toString();
    }
}
