package es.gob.afirma.standalone.configurator;

import java.awt.Component;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/standalone/configurator/IoConsole.class */
final class IoConsole implements Console {
    private final java.io.Console console;

    IoConsole(java.io.Console console) {
        if (console == null) {
            throw new IllegalArgumentException("La consola de Java no puede ser nula");
        }
        this.console = console;
    }

    @Override // es.gob.afirma.standalone.configurator.Console
    public void showConsole() {
    }

    @Override // es.gob.afirma.standalone.configurator.Console
    public void dispose() {
    }

    @Override // es.gob.afirma.standalone.configurator.Console
    public void print(String str) {
        this.console.printf(str, new Object[0]);
    }

    @Override // es.gob.afirma.standalone.configurator.Console
    public Component getParentComponent() {
        return null;
    }
}
