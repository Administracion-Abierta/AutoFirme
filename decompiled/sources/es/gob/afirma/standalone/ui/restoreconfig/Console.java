package es.gob.afirma.standalone.ui.restoreconfig;

import java.awt.Component;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/restoreconfig/Console.class */
interface Console {
    void showConsole();

    void print(String str);

    void dispose();

    Component getParentComponent();
}
