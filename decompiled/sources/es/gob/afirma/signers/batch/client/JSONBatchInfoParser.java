package es.gob.afirma.signers.batch.client;

import java.io.ByteArrayInputStream;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/batch/client/JSONBatchInfoParser.class */
public class JSONBatchInfoParser {
    private static final String JSELEMENT_SIGNS = "signs";
    private static final String JSELEMENT_ID = "id";
    private static final String JSELEMENT_RESULT = "result";
    private static final String JSELEMENT_DESCRIPTION = "description";

    static BatchInfo parse(byte[] bArr) throws JSONException {
        try {
            return new JSONBatchInfo(new JSONObject(new JSONTokener(new ByteArrayInputStream(bArr))));
        } catch (JSONException e) {
            throw new JSONException("El JSON del lote de firmas no esta formado correctamente", e);
        }
    }

    static JSONObject buildEmptyResult() {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put(JSELEMENT_SIGNS, new JSONArray());
        return jSONObject;
    }

    static JSONObject buildResult(List<BatchDataResult> list) {
        JSONArray jSONArray = new JSONArray();
        for (BatchDataResult batchDataResult : list) {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("id", batchDataResult.getId());
            jSONObject.put(JSELEMENT_RESULT, batchDataResult.getResult().name());
            if (batchDataResult.getDescription() != null) {
                jSONObject.put(JSELEMENT_DESCRIPTION, batchDataResult.getDescription());
            }
            jSONArray.put(jSONObject);
        }
        JSONObject jSONObject2 = new JSONObject();
        jSONObject2.put(JSELEMENT_SIGNS, jSONArray);
        return jSONObject2;
    }
}
