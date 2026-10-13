package es.gob.afirma.standalone.configurator;

import java.awt.Component;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/standalone/configurator/PrintConsole.class */
final class PrintConsole implements Console {
    PrintConsole() {
    }

    @Override // es.gob.afirma.standalone.configurator.Console
    public void showConsole() {
    }

    @Override // es.gob.afirma.standalone.configurator.Console
    public void dispose() {
    }

    @Override // es.gob.afirma.standalone.configurator.Console
    public void print(String str) {
        System.out.println(str);
    }

    @Override // es.gob.afirma.standalone.configurator.Console
    public Component getParentComponent() {
        return null;
    }
}
