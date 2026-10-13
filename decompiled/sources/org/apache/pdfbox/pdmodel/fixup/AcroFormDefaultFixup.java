package org.apache.pdfbox.pdmodel.fixup;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.fixup.processor.AcroFormDefaultsProcessor;
import org.apache.pdfbox.pdmodel.fixup.processor.AcroFormGenerateAppearancesProcessor;
import org.apache.pdfbox.pdmodel.fixup.processor.AcroFormOrphanWidgetsProcessor;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/fixup/AcroFormDefaultFixup.class */
public class AcroFormDefaultFixup extends AbstractFixup {
    public AcroFormDefaultFixup(PDDocument document) {
        super(document);
    }

    @Override // org.apache.pdfbox.pdmodel.fixup.PDDocumentFixup
    public void apply() {
        new AcroFormDefaultsProcessor(this.document).process();
        PDAcroForm acroForm = this.document.getDocumentCatalog().getAcroForm(null);
        if (acroForm != null && acroForm.getNeedAppearances()) {
            if (acroForm.getFields().isEmpty()) {
                new AcroFormOrphanWidgetsProcessor(this.document).process();
            }
            new AcroFormGenerateAppearancesProcessor(this.document).process();
        }
    }
}
