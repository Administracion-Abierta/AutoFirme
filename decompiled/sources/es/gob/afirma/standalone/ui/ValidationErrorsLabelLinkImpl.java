package es.gob.afirma.standalone.ui;

import es.gob.afirma.signvalidation.SignValidity;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/ui/ValidationErrorsLabelLinkImpl.class */
public final class ValidationErrorsLabelLinkImpl implements LabelLinkListener {
    private final byte[] signData;
    private final List<SignValidity> generalValidation;

    public ValidationErrorsLabelLinkImpl(byte[] bArr, List<SignValidity> list) {
        this.generalValidation = list;
        this.signData = bArr;
    }

    @Override // es.gob.afirma.standalone.ui.LabelLinkListener
    public void openLink() {
        new ValidationInfoDialog(null, this.signData, this.generalValidation).setVisible(true);
    }
}
