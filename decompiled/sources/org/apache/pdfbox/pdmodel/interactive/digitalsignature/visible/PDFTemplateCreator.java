package org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible;

import java.awt.geom.AffineTransform;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdfwriter.COSWriter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDSignatureField;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/digitalsignature/visible/PDFTemplateCreator.class */
public class PDFTemplateCreator {
    private final PDFTemplateBuilder pdfBuilder;
    private static final Log LOG = LogFactory.getLog(PDFTemplateCreator.class);

    public PDFTemplateCreator(PDFTemplateBuilder templateBuilder) {
        this.pdfBuilder = templateBuilder;
    }

    public PDFTemplateStructure getPdfStructure() {
        return this.pdfBuilder.getStructure();
    }

    public InputStream buildPDF(PDVisibleSignDesigner properties) throws IOException {
        LOG.info("pdf building has been started");
        PDFTemplateStructure pdfStructure = this.pdfBuilder.getStructure();
        this.pdfBuilder.createProcSetArray();
        this.pdfBuilder.createPage(properties);
        PDPage page = pdfStructure.getPage();
        this.pdfBuilder.createTemplate(page);
        PDDocument template = pdfStructure.getTemplate();
        this.pdfBuilder.createAcroForm(template);
        PDAcroForm acroForm = pdfStructure.getAcroForm();
        this.pdfBuilder.createSignatureField(acroForm);
        PDSignatureField pdSignatureField = pdfStructure.getSignatureField();
        this.pdfBuilder.createSignature(pdSignatureField, page, "");
        this.pdfBuilder.createAcroFormDictionary(acroForm, pdSignatureField);
        this.pdfBuilder.createAffineTransform(properties.getTransform());
        AffineTransform transform = pdfStructure.getAffineTransform();
        this.pdfBuilder.createSignatureRectangle(pdSignatureField, properties);
        this.pdfBuilder.createFormatterRectangle(properties.getFormatterRectangleParameters());
        PDRectangle bbox = pdfStructure.getFormatterRectangle();
        this.pdfBuilder.createSignatureImage(template, properties.getImage());
        this.pdfBuilder.createHolderFormStream(template);
        PDStream holderFormStream = pdfStructure.getHolderFormStream();
        this.pdfBuilder.createHolderFormResources();
        PDResources holderFormResources = pdfStructure.getHolderFormResources();
        this.pdfBuilder.createHolderForm(holderFormResources, holderFormStream, bbox);
        this.pdfBuilder.createAppearanceDictionary(pdfStructure.getHolderForm(), pdSignatureField);
        this.pdfBuilder.createInnerFormStream(template);
        this.pdfBuilder.createInnerFormResource();
        PDResources innerFormResource = pdfStructure.getInnerFormResources();
        this.pdfBuilder.createInnerForm(innerFormResource, pdfStructure.getInnerFormStream(), bbox);
        PDFormXObject innerForm = pdfStructure.getInnerForm();
        this.pdfBuilder.insertInnerFormToHolderResources(innerForm, holderFormResources);
        this.pdfBuilder.createImageFormStream(template);
        PDStream imageFormStream = pdfStructure.getImageFormStream();
        this.pdfBuilder.createImageFormResources();
        PDResources imageFormResources = pdfStructure.getImageFormResources();
        this.pdfBuilder.createImageForm(imageFormResources, innerFormResource, imageFormStream, bbox, transform, pdfStructure.getImage());
        this.pdfBuilder.createBackgroundLayerForm(innerFormResource, bbox);
        this.pdfBuilder.injectProcSetArray(innerForm, page, innerFormResource, imageFormResources, holderFormResources, pdfStructure.getProcSet());
        COSName imageFormName = pdfStructure.getImageFormName();
        COSName imageName = pdfStructure.getImageName();
        COSName innerFormName = pdfStructure.getInnerFormName();
        this.pdfBuilder.injectAppearanceStreams(holderFormStream, imageFormStream, imageFormStream, imageFormName, imageName, innerFormName, properties);
        this.pdfBuilder.createVisualSignature(template);
        this.pdfBuilder.createWidgetDictionary(pdSignatureField, holderFormResources);
        InputStream in = getVisualSignatureAsStream(pdfStructure.getVisualSignature());
        LOG.info("stream returning started, size= " + in.available());
        template.close();
        return in;
    }

    private InputStream getVisualSignatureAsStream(COSDocument visualSignature) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        COSWriter writer = new COSWriter(baos);
        writer.write(visualSignature);
        writer.close();
        return new ByteArrayInputStream(baos.toByteArray());
    }
}
