package es.gob.afirma.standalone.configurator;

import es.gob.afirma.standalone.plugins.manager.PluginsManager;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/standalone/configurator/Configurator.class */
interface Configurator {
    void configure(Console console) throws ConfigurationException, GeneralSecurityException, IOException;

    void uninstall(Console console, PluginsManager pluginsManager);

    File getAplicationDirectory();

    File getAlternativeApplicationDirectory();
}
