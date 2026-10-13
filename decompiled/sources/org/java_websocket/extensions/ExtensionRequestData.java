package org.java_websocket.extensions;

import es.gob.afirma.keystores.filters.PolicyIdFilter;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.pdfbox.contentstream.operator.OperatorName;

/* JADX INFO: loaded from: autofirma.jar:org/java_websocket/extensions/ExtensionRequestData.class */
public class ExtensionRequestData {
    public static final String EMPTY_VALUE = "";
    private Map<String, String> extensionParameters = new LinkedHashMap();
    private String extensionName;

    private ExtensionRequestData() {
    }

    public static ExtensionRequestData parseExtensionRequest(String extensionRequest) {
        ExtensionRequestData extensionData = new ExtensionRequestData();
        String[] parts = extensionRequest.split(PolicyIdFilter.OID_SEPARATOR);
        extensionData.extensionName = parts[0].trim();
        for (int i = 1; i < parts.length; i++) {
            String[] keyValue = parts[i].split("=");
            String value = "";
            if (keyValue.length > 1) {
                String tempValue = keyValue[1].trim();
                if ((tempValue.startsWith(OperatorName.SHOW_TEXT_LINE_AND_SPACE) && tempValue.endsWith(OperatorName.SHOW_TEXT_LINE_AND_SPACE)) || (tempValue.startsWith(OperatorName.SHOW_TEXT_LINE) && tempValue.endsWith(OperatorName.SHOW_TEXT_LINE) && tempValue.length() > 2)) {
                    tempValue = tempValue.substring(1, tempValue.length() - 1);
                }
                value = tempValue;
            }
            extensionData.extensionParameters.put(keyValue[0].trim(), value);
        }
        return extensionData;
    }

    public String getExtensionName() {
        return this.extensionName;
    }

    public Map<String, String> getExtensionParameters() {
        return this.extensionParameters;
    }
}
