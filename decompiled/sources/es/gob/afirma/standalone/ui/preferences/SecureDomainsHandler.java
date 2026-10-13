package es.gob.afirma.standalone.ui.preferences;

import es.gob.afirma.standalone.HttpManager;
import es.gob.afirma.standalone.configurator.common.PreferencesManager;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/preferences/SecureDomainsHandler.class */
public class SecureDomainsHandler {
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final String COMMA_SEPARATOR = ",";
    private static final String CRLF = "\r";
    private static final String LINE_BREAK = "\n";
    private final SecureDomainsPanel view;

    public SecureDomainsHandler(SecureDomainsPanel secureDomainsPanel) {
        this.view = secureDomainsPanel;
    }

    void loadViewData() {
        String str = PreferencesManager.get("secureDomainsList");
        if (str != null) {
            this.view.getSecureDomainsListTA().setText(str.replaceAll(COMMA_SEPARATOR, "\n"));
        }
    }

    void saveViewData() throws ConfigurationException {
        String strReplace = this.view.getSecureDomainsListTA().getText().trim().replace(CRLF, COMMA_SEPARATOR).replace("\n", COMMA_SEPARATOR);
        if (strReplace.isEmpty()) {
            PreferencesManager.remove("secureDomainsList");
        } else {
            PreferencesManager.put("secureDomainsList", strReplace);
        }
        HttpManager.setSecureDomains(strReplace);
        try {
            PreferencesManager.flush();
        } catch (Exception e) {
            LOGGER.severe("Error al guardar las preferencias de sitios seguros: " + e);
        }
    }
}
