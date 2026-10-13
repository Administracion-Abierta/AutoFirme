package es.gob.afirma.standalone.ui.preferences;

import es.gob.afirma.standalone.SimpleAfirmaMessages;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/preferences/FormatItem.class */
public final class FormatItem {
    private final String name;
    private boolean isRecommended = false;

    public FormatItem(String str) {
        this.name = str;
    }

    public String toString() {
        if (this.isRecommended) {
            return this.name + " " + SimpleAfirmaMessages.getString("ChangeFormatDialog.2");
        }
        return this.name;
    }

    public boolean isRecommended() {
        return this.isRecommended;
    }

    public void setRecommended(boolean z) {
        this.isRecommended = z;
    }

    public String getName() {
        return this.name;
    }
}
