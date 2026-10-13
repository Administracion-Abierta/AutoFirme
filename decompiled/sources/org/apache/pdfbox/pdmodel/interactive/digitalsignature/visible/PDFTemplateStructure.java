package org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible;

import java.awt.geom.AffineTransform;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdfwriter.COSWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceDictionary;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDSignatureField;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/digitalsignature/visible/PDFTemplateStructure.class */
public class PDFTemplateStructure {
    private PDPage page;
    private PDDocument template;
    private PDAcroForm acroForm;
    private PDSignatureField signatureField;
    private PDSignature pdSignature;
    private COSDictionary acroFormDictionary;
    private PDRectangle signatureRectangle;
    private AffineTransform affineTransform;
    private COSArray procSet;
    private PDImageXObject image;
    private PDRectangle formatterRectangle;
    private PDStream holderFormStream;
    private PDResources holderFormResources;
    private PDFormXObject holderForm;
    private PDAppearanceDictionary appearanceDictionary;
    private PDStream innerFormStream;
    private PDResources innerFormResources;
    private PDFormXObject innerForm;
    private PDStream imageFormStream;
    private PDResources imageFormResources;
    private List<PDField> acroFormFields;
    private COSName innerFormName;
    private COSName imageFormName;
    private COSName imageName;
    private COSDocument visualSignature;
    private PDFormXObject imageForm;
    private COSDictionary widgetDictionary;

    public PDPage getPage() {
        return this.page;
    }

    public void setPage(PDPage page) {
        this.page = page;
    }

    public PDDocument getTemplate() {
        return this.template;
    }

    public void setTemplate(PDDocument template) {
        this.template = template;
    }

    public PDAcroForm getAcroForm() {
        return this.acroForm;
    }

    public void setAcroForm(PDAcroForm acroForm) {
        this.acroForm = acroForm;
    }

    public PDSignatureField getSignatureField() {
        return this.signatureField;
    }

    public void setSignatureField(PDSignatureField signatureField) {
        this.signatureField = signatureField;
    }

    public PDSignature getPdSignature() {
        return this.pdSignature;
    }

    public void setPdSignature(PDSignature pdSignature) {
        this.pdSignature = pdSignature;
    }

    public COSDictionary getAcroFormDictionary() {
        return this.acroFormDictionary;
    }

    public void setAcroFormDictionary(COSDictionary acroFormDictionary) {
        this.acroFormDictionary = acroFormDictionary;
    }

    public PDRectangle getSignatureRectangle() {
        return this.signatureRectangle;
    }

    public void setSignatureRectangle(PDRectangle signatureRectangle) {
        this.signatureRectangle = signatureRectangle;
    }

    public AffineTransform getAffineTransform() {
        return this.affineTransform;
    }

    public void setAffineTransform(AffineTransform affineTransform) {
        this.affineTransform = affineTransform;
    }

    public COSArray getProcSet() {
        return this.procSet;
    }

    public void setProcSet(COSArray procSet) {
        this.procSet = procSet;
    }

    public PDImageXObject getImage() {
        return this.image;
    }

    public void setImage(PDImageXObject image) {
        this.image = image;
    }

    public PDRectangle getFormatterRectangle() {
        return this.formatterRectangle;
    }

    public void setFormatterRectangle(PDRectangle formatterRectangle) {
        this.formatterRectangle = formatterRectangle;
    }

    public PDStream getHolderFormStream() {
        return this.holderFormStream;
    }

    public void setHolderFormStream(PDStream holderFormStream) {
        this.holderFormStream = holderFormStream;
    }

    public PDFormXObject getHolderForm() {
        return this.holderForm;
    }

    public void setHolderForm(PDFormXObject holderForm) {
        this.holderForm = holderForm;
    }

    public PDResources getHolderFormResources() {
        return this.holderFormResources;
    }

    public void setHolderFormResources(PDResources holderFormResources) {
        this.holderFormResources = holderFormResources;
    }

    public PDAppearanceDictionary getAppearanceDictionary() {
        return this.appearanceDictionary;
    }

    public void setAppearanceDictionary(PDAppearanceDictionary appearanceDictionary) {
        this.appearanceDictionary = appearanceDictionary;
    }

    public PDStream getInnerFormStream() {
        return this.innerFormStream;
    }

    public void setInnterFormStream(PDStream innerFormStream) {
        this.innerFormStream = innerFormStream;
    }

    public PDResources getInnerFormResources() {
        return this.innerFormResources;
    }

    public void setInnerFormResources(PDResources innerFormResources) {
        this.innerFormResources = innerFormResources;
    }

    public PDFormXObject getInnerForm() {
        return this.innerForm;
    }

    public void setInnerForm(PDFormXObject innerForm) {
        this.innerForm = innerForm;
    }

    public COSName getInnerFormName() {
        return this.innerFormName;
    }

    public void setInnerFormName(COSName innerFormName) {
        this.innerFormName = innerFormName;
    }

    public PDStream getImageFormStream() {
        return this.imageFormStream;
    }

    public void setImageFormStream(PDStream imageFormStream) {
        this.imageFormStream = imageFormStream;
    }

    public PDResources getImageFormResources() {
        return this.imageFormResources;
    }

    public void setImageFormResources(PDResources imageFormResources) {
        this.imageFormResources = imageFormResources;
    }

    public PDFormXObject getImageForm() {
        return this.imageForm;
    }

    public void setImageForm(PDFormXObject imageForm) {
        this.imageForm = imageForm;
    }

    public COSName getImageFormName() {
        return this.imageFormName;
    }

    public void setImageFormName(COSName imageFormName) {
        this.imageFormName = imageFormName;
    }

    public COSName getImageName() {
        return this.imageName;
    }

    public void setImageName(COSName imageName) {
        this.imageName = imageName;
    }

    public COSDocument getVisualSignature() {
        return this.visualSignature;
    }

    public void setVisualSignature(COSDocument visualSignature) {
        this.visualSignature = visualSignature;
    }

    public List<PDField> getAcroFormFields() {
        return this.acroFormFields;
    }

    public void setAcroFormFields(List<PDField> acroFormFields) {
        this.acroFormFields = acroFormFields;
    }

    @Deprecated
    public ByteArrayInputStream getTemplateAppearanceStream() throws IOException {
        COSDocument visualSignature = getVisualSignature();
        ByteArrayOutputStream memoryOut = new ByteArrayOutputStream();
        COSWriter memoryWriter = new COSWriter(memoryOut);
        memoryWriter.write(visualSignature);
        ByteArrayInputStream input = new ByteArrayInputStream(memoryOut.toByteArray());
        getTemplate().close();
        return input;
    }

    public COSDictionary getWidgetDictionary() {
        return this.widgetDictionary;
    }

    public void setWidgetDictionary(COSDictionary widgetDictionary) {
        this.widgetDictionary = widgetDictionary;
    }
}
