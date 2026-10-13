package org.apache.pdfbox.pdmodel.fixup.processor;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/fixup/processor/AcroFormDefaultsProcessor.class */
public class AcroFormDefaultsProcessor extends AbstractProcessor {
    public AcroFormDefaultsProcessor(PDDocument document) {
        super(document);
    }

    @Override // org.apache.pdfbox.pdmodel.fixup.processor.PDDocumentProcessor
    public void process() {
        PDAcroForm acroForm = this.document.getDocumentCatalog().getAcroForm(null);
        if (acroForm != null) {
            verifyOrCreateDefaults(acroForm);
        }
    }

    private void verifyOrCreateDefaults(PDAcroForm acroForm) {
        if (acroForm.getDefaultAppearance().length() == 0) {
            acroForm.setDefaultAppearance("/Helv 0 Tf 0 g ");
            acroForm.getCOSObject().setNeedToBeUpdated(true);
        }
        PDResources defaultResources = acroForm.getDefaultResources();
        if (defaultResources == null) {
            defaultResources = new PDResources();
            acroForm.setDefaultResources(defaultResources);
            acroForm.getCOSObject().setNeedToBeUpdated(true);
        }
        COSDictionary fontDict = defaultResources.getCOSObject().getCOSDictionary(COSName.FONT);
        if (fontDict == null) {
            fontDict = new COSDictionary();
            defaultResources.getCOSObject().setItem(COSName.FONT, (COSBase) fontDict);
        }
        if (!fontDict.containsKey(COSName.HELV)) {
            defaultResources.put(COSName.HELV, PDType1Font.HELVETICA);
            defaultResources.getCOSObject().setNeedToBeUpdated(true);
            fontDict.setNeedToBeUpdated(true);
        }
        if (!fontDict.containsKey(COSName.ZA_DB)) {
            defaultResources.put(COSName.ZA_DB, PDType1Font.ZAPF_DINGBATS);
            defaultResources.getCOSObject().setNeedToBeUpdated(true);
            fontDict.setNeedToBeUpdated(true);
        }
    }
}
