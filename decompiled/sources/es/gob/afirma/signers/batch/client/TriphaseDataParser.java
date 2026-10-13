package es.gob.afirma.signers.batch.client;

import com.aowagie.text.ElementTags;
import es.gob.afirma.core.misc.protocol.ParameterException;
import es.gob.afirma.core.signers.TriphaseData;
import es.gob.afirma.signers.xades.XAdESExtraParams;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/signers/batch/client/TriphaseDataParser.class */
public class TriphaseDataParser {
    static final String DEFAULT_URL_ENCODING = StandardCharsets.UTF_8.name();

    public static TriphaseData parseFromJSON(byte[] bArr) {
        return parseFromJSON(new JSONObject(new JSONTokener(new ByteArrayInputStream(bArr))));
    }

    public static TriphaseData parseFromJSON(JSONObject jSONObject) {
        JSONArray jSONArray = null;
        if (jSONObject.has("signs")) {
            jSONArray = jSONObject.getJSONArray("signs");
        }
        String string = null;
        if (jSONObject.has(XAdESExtraParams.FORMAT)) {
            string = jSONObject.getString(XAdESExtraParams.FORMAT);
        }
        ArrayList arrayList = new ArrayList();
        if (jSONArray != null) {
            for (int i = 0; i < jSONArray.length(); i++) {
                JSONArray jSONArray2 = jSONArray.getJSONObject(i).getJSONArray("signinfo");
                for (int i2 = 0; i2 < jSONArray2.length(); i2++) {
                    JSONObject jSONObject2 = jSONArray2.getJSONObject(i2);
                    arrayList.add(new TriphaseData.TriSign(parseParamsJSON(jSONObject2.getJSONObject("params")), jSONObject2.getString(ElementTags.ID), jSONObject2.optString("signid", null)));
                }
            }
        } else {
            JSONArray jSONArray3 = jSONObject.getJSONArray("signinfo");
            for (int i3 = 0; i3 < jSONArray3.length(); i3++) {
                JSONObject jSONObject3 = jSONArray3.getJSONObject(i3);
                arrayList.add(new TriphaseData.TriSign(parseParamsJSON(jSONObject3.getJSONObject("params")), jSONObject3.getString(ElementTags.ID), jSONObject3.optString("signid", null)));
            }
        }
        return new TriphaseData(arrayList, string);
    }

    private static Map<String, String> parseParamsJSON(JSONObject jSONObject) {
        ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
        Map<String, Object> map = jSONObject.toMap();
        for (String str : map.keySet()) {
            concurrentHashMap.put(str, (String) map.get(str));
        }
        return concurrentHashMap;
    }

    public static String triphaseDataToJsonString(TriphaseData triphaseData) {
        JSONArray jSONArray = new JSONArray();
        for (TriphaseData.TriSign triSign : triphaseData.getTriSigns()) {
            JSONObject jSONObject = new JSONObject();
            if (triSign.getId() != null) {
                jSONObject.put(ElementTags.ID, triSign.getId());
            }
            if (triSign.getSignatureId() != null) {
                jSONObject.put("signid", triSign.getSignatureId());
            }
            JSONObject jSONObject2 = new JSONObject();
            for (String str : triSign.getDict().keySet()) {
                jSONObject2.put(str, triSign.getProperty(str));
            }
            jSONObject.put("params", jSONObject2);
            jSONArray.put(jSONObject);
        }
        JSONObject jSONObject3 = new JSONObject();
        if (triphaseData.getFormat() != null) {
            jSONObject3.put(XAdESExtraParams.FORMAT, triphaseData.getFormat());
        }
        jSONObject3.put("signinfo", jSONArray);
        return jSONObject3.toString();
    }

    public static Map<String, String> parseParamsListJson(byte[] bArr) throws ParameterException {
        HashMap map = new HashMap();
        JSONArray jSONArray = new JSONObject(new JSONTokener(new ByteArrayInputStream(bArr))).getJSONArray("params");
        for (int i = 0; i < jSONArray.length(); i++) {
            JSONObject jSONObject = jSONArray.getJSONObject(i);
            if (!jSONObject.has(OperatorName.NON_STROKING_CMYK) || !jSONObject.has(OperatorName.CURVE_TO_REPLICATE_INITIAL_POINT)) {
                throw new ParameterException("El JSON no tiene la forma esperada");
            }
            String string = jSONObject.getString(OperatorName.NON_STROKING_CMYK);
            String string2 = jSONObject.getString(OperatorName.CURVE_TO_REPLICATE_INITIAL_POINT);
            try {
                map.put(string, URLDecoder.decode(string2, DEFAULT_URL_ENCODING));
            } catch (UnsupportedEncodingException e) {
                Logger.getLogger("es.gob.afirma").warning("Codificacion no soportada para la URL (" + DEFAULT_URL_ENCODING + "): " + e);
                map.put(string, string2);
            }
        }
        return map;
    }
}
