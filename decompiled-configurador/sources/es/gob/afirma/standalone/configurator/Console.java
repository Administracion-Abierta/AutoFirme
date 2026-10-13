package es.gob.afirma.standalone.configurator;

import java.awt.Component;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/standalone/configurator/Console.class */
interface Console {
    void showConsole();

    void print(String str);

    void dispose();

    Component getParentComponent();
}
