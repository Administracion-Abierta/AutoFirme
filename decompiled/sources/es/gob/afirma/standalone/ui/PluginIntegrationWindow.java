package es.gob.afirma.standalone.ui;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/PluginIntegrationWindow.class */
public enum PluginIntegrationWindow {
    INPUT_DATA,
    SINGLE_RESULT,
    MULTI_RESULT,
    VISOR;

    public static PluginIntegrationWindow getWindow(String str) {
        switch (str.toLowerCase()) {
            case "input":
            case "input_data":
                return INPUT_DATA;
            case "single_result":
                return SINGLE_RESULT;
            case "multi_result":
                return MULTI_RESULT;
            case "visor":
                return VISOR;
            default:
                return null;
        }
    }
}
