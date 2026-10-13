package es.gob.afirma.signers.batch.client;

import es.gob.afirma.core.signers.TriphaseData;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/batch/client/JSONPreSignBatchParser.class */
public class JSONPreSignBatchParser {
    private static final String JSELEM_TD = "td";
    private static final String JSELEM_RESULTS = "results";
    private static final String JSELEM_ID = "id";
    private static final String JSELEM_RESULT = "result";
    private static final String JSELEM_DESCRIPTION = "description";

    public static PresignBatch parseFromJSON(byte[] bArr) throws JSONException {
        try {
            JSONObject jSONObject = new JSONObject(new JSONTokener(new ByteArrayInputStream(bArr)));
            TriphaseData fromJSON = null;
            if (jSONObject.has(JSELEM_TD)) {
                fromJSON = TriphaseDataParser.parseFromJSON(jSONObject.getJSONObject(JSELEM_TD));
            }
            List<BatchDataResult> batchDataResults = null;
            if (jSONObject.has(JSELEM_RESULTS)) {
                batchDataResults = parseBatchDataResults(jSONObject.getJSONArray(JSELEM_RESULTS));
            }
            return new PresignBatch(fromJSON, batchDataResults);
        } catch (JSONException e) {
            throw new JSONException("El JSON de prefirma del lote no esta formado correctamente", e);
        }
    }

    private static List<BatchDataResult> parseBatchDataResults(JSONArray jSONArray) {
        if (jSONArray.isEmpty()) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < jSONArray.length(); i++) {
            JSONObject jSONObject = jSONArray.getJSONObject(i);
            if (!jSONObject.has("id") || !jSONObject.has(JSELEM_RESULT)) {
                throw new JSONException("Se obtuvo un resultado parcial sin identificador o resultado");
            }
            try {
                BatchDataResult.Result resultValueOf = BatchDataResult.Result.valueOf(jSONObject.getString(JSELEM_RESULT));
                String string = jSONObject.getString("id");
                String string2 = null;
                if (jSONObject.has(JSELEM_DESCRIPTION)) {
                    string2 = jSONObject.getString(JSELEM_DESCRIPTION);
                }
                arrayList.add(new BatchDataResult(string, resultValueOf, string2));
            } catch (Exception e) {
                throw new JSONException("Estado de firma trifasica desconocido", e);
            }
        }
        return arrayList;
    }
}
