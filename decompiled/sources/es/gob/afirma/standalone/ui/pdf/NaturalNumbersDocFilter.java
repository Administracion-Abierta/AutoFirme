package es.gob.afirma.standalone.ui.pdf;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/pdf/NaturalNumbersDocFilter.class */
class NaturalNumbersDocFilter extends DocumentFilter {
    NaturalNumbersDocFilter() {
    }

    public void insertString(DocumentFilter.FilterBypass filterBypass, int i, String str, AttributeSet attributeSet) throws BadLocationException {
        if (filterBypass.getDocument().getLength() + str.length() > 4) {
            return;
        }
        boolean z = true;
        for (int i2 = 0; z && i2 < str.length(); i2++) {
            if (!Character.isDigit(str.charAt(i2))) {
                z = false;
            }
        }
        if (z) {
            super.insertString(filterBypass, i, str, attributeSet);
        }
    }

    public void remove(DocumentFilter.FilterBypass filterBypass, int i, int i2) throws BadLocationException {
        super.remove(filterBypass, i, i2);
    }

    public void replace(DocumentFilter.FilterBypass filterBypass, int i, int i2, String str, AttributeSet attributeSet) throws BadLocationException {
        if ((filterBypass.getDocument().getLength() - i2) + str.length() > 4) {
            return;
        }
        boolean z = true;
        for (int i3 = 0; z && i3 < str.length(); i3++) {
            if (!Character.isDigit(str.charAt(i3))) {
                z = false;
            }
        }
        if (z) {
            super.replace(filterBypass, i, i2, str, attributeSet);
        }
    }
}
