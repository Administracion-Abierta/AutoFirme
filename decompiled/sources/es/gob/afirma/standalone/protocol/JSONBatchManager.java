package es.gob.afirma.standalone.protocol;

import com.aowagie.text.ElementTags;
import es.gob.afirma.core.misc.Base64;
import es.gob.afirma.core.misc.protocol.ParameterException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/protocol/JSONBatchManager.class */
public class JSONBatchManager {
    private static final String ELEM_STOPONERROR = "stoponerror";
    private static final String ELEM_SINGLESIGNS = "singlesigns";
    private static final String ELEM_ID = "id";
    private static final String ELEM_SUBOPERATION = "suboperation";
    private static final String ELEM_DATAREFERENCE = "datareference";
    private static final String ELEM_FORMAT = "format";
    private static final String ELEM_ALGORITHM = "algorithm";
    private static final String ELEM_EXTRAPARAMS = "extraparams";
    private static final String RESP_ELEM_MAIN = "signs";
    private static final String RESP_ELEM_ID = "id";
    private static final String RESP_ELEM_RESULT = "result";
    private static final String RESP_ELEM_DESCRIPTION = "description";
    private static final String RESP_ELEM_SIGNATURE = "signature";

    public static BatchSignOperation parseBatchConfig(byte[] bArr) throws ParameterException {
        try {
            try {
                return parseBatchConfig(new JSONObject(new JSONTokener(new ByteArrayInputStream(bArr))));
            } catch (Exception e) {
                throw new ParameterException("La declaracion del lote suministrada no es correcta", e);
            }
        } catch (JSONException e2) {
            throw new JSONException("El JSON de definicion de lote de firmas no esta formado correctamente", e2);
        }
    }

    private static BatchSignOperation parseBatchConfig(JSONObject jSONObject) throws JSONException, ParameterException, IOException {
        boolean z = jSONObject.has(ELEM_STOPONERROR) && jSONObject.getBoolean(ELEM_STOPONERROR);
        SingleSignOperation.Operation operation = jSONObject.has(ELEM_SUBOPERATION) ? SingleSignOperation.Operation.getOperation(jSONObject.getString(ELEM_SUBOPERATION)) : SingleSignOperation.Operation.SIGN;
        if (!jSONObject.has("format")) {
            throw new ParameterException("El lote no declaraba el formato de firma");
        }
        String string = jSONObject.getString("format");
        if (!jSONObject.has(ELEM_ALGORITHM)) {
            throw new ParameterException("El lote no declaraba el algoritmo de firma");
        }
        String string2 = jSONObject.getString(ELEM_ALGORITHM);
        String string3 = jSONObject.has(ELEM_EXTRAPARAMS) ? jSONObject.getString(ELEM_EXTRAPARAMS) : null;
        Properties propertiesExpanExtraParams = string3 != null ? expanExtraParams(string3) : null;
        ArrayList arrayList = new ArrayList();
        JSONArray jSONArray = jSONObject.getJSONArray(ELEM_SINGLESIGNS);
        if (jSONArray != null) {
            for (int i = 0; i < jSONArray.length(); i++) {
                JSONObject jSONObject2 = jSONArray.getJSONObject(i);
                SingleSignOperation singleSignOperation = new SingleSignOperation();
                if (!jSONObject2.has(ElementTags.ID)) {
                    throw new ParameterException("No se ha incluido el identificador de un documento del lote");
                }
                singleSignOperation.setDocId(jSONObject2.getString(ElementTags.ID));
                if (!jSONObject2.has(ELEM_DATAREFERENCE)) {
                    throw new ParameterException("No se ha incluido la referencia de un documento del lote");
                }
                singleSignOperation.setData(Base64.decode(jSONObject2.getString(ELEM_DATAREFERENCE)));
                singleSignOperation.setAlgorithm(string2);
                if (jSONObject2.has(ELEM_SUBOPERATION)) {
                    singleSignOperation.setCryptoOperation(SingleSignOperation.Operation.getOperation(jSONObject2.getString(ELEM_SUBOPERATION)));
                } else {
                    singleSignOperation.setCryptoOperation(operation);
                }
                if (jSONObject2.has("format")) {
                    singleSignOperation.setFormat(jSONObject2.getString("format"));
                } else {
                    singleSignOperation.setFormat(string);
                }
                if (jSONObject2.has(ELEM_EXTRAPARAMS)) {
                    singleSignOperation.setExtraParams(expanExtraParams(jSONObject2.getString(ELEM_EXTRAPARAMS)));
                } else {
                    Properties properties = new Properties();
                    if (propertiesExpanExtraParams != null) {
                        properties.putAll(propertiesExpanExtraParams);
                    }
                    singleSignOperation.setExtraParams(properties);
                }
                arrayList.add(singleSignOperation);
            }
        }
        return new BatchSignOperation(z, arrayList);
    }

    private static Properties expanExtraParams(String str) throws IOException {
        if (str == null || str.isEmpty()) {
            return null;
        }
        String strReplace = new String(Base64.decode(str), StandardCharsets.UTF_8).trim().replace("\\n", "\n");
        Properties properties = new Properties();
        properties.load(new ByteArrayInputStream(strReplace.getBytes(StandardCharsets.UTF_8)));
        return properties;
    }

    public static String buildBatchResultJson(List<LocalBatchSigner.LocalSingleBatchResult> list) {
        JSONArray jSONArray = new JSONArray();
        for (LocalBatchSigner.LocalSingleBatchResult localSingleBatchResult : list) {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put(ElementTags.ID, localSingleBatchResult.getDocId());
            jSONObject.put(RESP_ELEM_RESULT, localSingleBatchResult.getResult());
            if (localSingleBatchResult.getDescription() != null) {
                jSONObject.put(RESP_ELEM_DESCRIPTION, localSingleBatchResult.getDescription());
            }
            if (localSingleBatchResult.getSignature() != null) {
                jSONObject.put(RESP_ELEM_SIGNATURE, Base64.encode(localSingleBatchResult.getSignature()));
            }
            jSONArray.put(jSONObject);
        }
        JSONObject jSONObject2 = new JSONObject();
        jSONObject2.put(RESP_ELEM_MAIN, jSONArray);
        return jSONObject2.toString();
    }
}
