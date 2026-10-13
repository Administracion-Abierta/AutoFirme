package org.apache.pdfbox.pdmodel.fixup.processor;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/fixup/processor/AcroFormGenerateAppearancesProcessor.class */
public class AcroFormGenerateAppearancesProcessor extends AbstractProcessor {
    private static final Log LOG = LogFactory.getLog(AcroFormGenerateAppearancesProcessor.class);

    public AcroFormGenerateAppearancesProcessor(PDDocument document) {
        super(document);
    }

    @Override // org.apache.pdfbox.pdmodel.fixup.processor.PDDocumentProcessor
    public void process() {
        PDAcroForm acroForm = this.document.getDocumentCatalog().getAcroForm(null);
        if (acroForm != null && acroForm.getNeedAppearances()) {
            try {
                LOG.debug("trying to generate appearance streams for fields as NeedAppearances is true()");
                acroForm.refreshAppearances();
                acroForm.setNeedAppearances(false);
            } catch (IOException ioe) {
                LOG.debug("couldn't generate appearance stream for some fields - check output");
                LOG.debug(ioe.getMessage());
            } catch (IllegalArgumentException iae) {
                LOG.debug("couldn't generate appearance stream for some fields - check output");
                LOG.debug(iae.getMessage());
            }
        }
    }
}
