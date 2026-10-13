package es.gob.afirma.standalone;

import es.gob.afirma.keystores.AOKeyStoreManager;
import es.gob.afirma.keystores.AOKeyStoreManagerException;
import java.awt.Component;
import javax.swing.SwingWorker;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/SimpleKeyStoreManagerWorker.class */
final class SimpleKeyStoreManagerWorker extends SwingWorker<Void, String> {
    private final SimpleAfirma simpleAFirma;
    private final Component parent;
    private final boolean dnie;
    private final boolean forced;
    private AOKeyStoreManager ksm;

    SimpleKeyStoreManagerWorker(SimpleAfirma simpleAfirma, Component component, boolean z, boolean z2) {
        this.simpleAFirma = simpleAfirma;
        this.parent = component;
        this.dnie = z;
        this.forced = z2;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX INFO: renamed from: doInBackground, reason: merged with bridge method [inline-methods] */
    public Void m679doInBackground() throws AOKeyStoreManagerException {
        try {
            this.ksm = SimpleKeyStoreManager.getKeyStore(this.dnie, this.forced, this.parent);
            return null;
        } catch (NoDnieFoundException e) {
            this.ksm = null;
            return null;
        }
    }

    protected void done() {
        if (this.simpleAFirma != null && this.ksm != null) {
            this.simpleAFirma.setKeyStoreManager(this.ksm);
        }
    }
}
