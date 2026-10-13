package es.gob.afirma.standalone.ui;

import es.gob.afirma.core.ui.AOUIFactory;
import es.gob.afirma.standalone.SimpleAfirmaMessages;
import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/URLLabelLinkImpl.class */
public final class URLLabelLinkImpl implements LabelLinkListener {
    private URI url;

    public URLLabelLinkImpl(String str) {
        this.url = null;
        try {
            this.url = new URI(str);
        } catch (URISyntaxException e) {
            AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("SignResultPanel.0") + SimpleAfirmaMessages.getString("SignResultPanel.33"), SimpleAfirmaMessages.getString("SimpleAfirma.7"), 0, e);
        }
    }

    @Override // es.gob.afirma.standalone.ui.LabelLinkListener
    public void openLink() {
        try {
            Desktop.getDesktop().browse(this.url);
        } catch (Exception e) {
            AOUIFactory.showErrorMessage(SimpleAfirmaMessages.getString("SignResultPanel.0") + SimpleAfirmaMessages.getString("SignResultPanel.33"), SimpleAfirmaMessages.getString("SimpleAfirma.7"), 0, e);
        }
    }
}
