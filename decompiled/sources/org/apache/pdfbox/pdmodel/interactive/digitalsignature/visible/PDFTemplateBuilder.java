package org.apache.pdfbox.pdmodel.interactive.digitalsignature.visible;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDSignatureField;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/digitalsignature/visible/PDFTemplateBuilder.class */
public interface PDFTemplateBuilder {
    @Deprecated
    void createAffineTransform(byte[] bArr);

    void createAffineTransform(AffineTransform affineTransform);

    void createPage(PDVisibleSignDesigner pDVisibleSignDesigner);

    void createTemplate(PDPage pDPage) throws IOException;

    void createAcroForm(PDDocument pDDocument);

    void createSignatureField(PDAcroForm pDAcroForm) throws IOException;

    void createSignature(PDSignatureField pDSignatureField, PDPage pDPage, String str) throws IOException;

    void createAcroFormDictionary(PDAcroForm pDAcroForm, PDSignatureField pDSignatureField) throws IOException;

    void createSignatureRectangle(PDSignatureField pDSignatureField, PDVisibleSignDesigner pDVisibleSignDesigner) throws IOException;

    void createProcSetArray();

    void createSignatureImage(PDDocument pDDocument, BufferedImage bufferedImage) throws IOException;

    @Deprecated
    void createFormatterRectangle(byte[] bArr);

    void createFormatterRectangle(int[] iArr);

    void createHolderFormStream(PDDocument pDDocument);

    void createHolderFormResources();

    void createHolderForm(PDResources pDResources, PDStream pDStream, PDRectangle pDRectangle);

    void createAppearanceDictionary(PDFormXObject pDFormXObject, PDSignatureField pDSignatureField) throws IOException;

    void createInnerFormStream(PDDocument pDDocument);

    void createInnerFormResource();

    void createInnerForm(PDResources pDResources, PDStream pDStream, PDRectangle pDRectangle);

    void insertInnerFormToHolderResources(PDFormXObject pDFormXObject, PDResources pDResources);

    void createImageFormStream(PDDocument pDDocument);

    void createImageFormResources();

    void createImageForm(PDResources pDResources, PDResources pDResources2, PDStream pDStream, PDRectangle pDRectangle, AffineTransform affineTransform, PDImageXObject pDImageXObject) throws IOException;

    void createBackgroundLayerForm(PDResources pDResources, PDRectangle pDRectangle) throws IOException;

    void injectProcSetArray(PDFormXObject pDFormXObject, PDPage pDPage, PDResources pDResources, PDResources pDResources2, PDResources pDResources3, COSArray cOSArray);

    void injectAppearanceStreams(PDStream pDStream, PDStream pDStream2, PDStream pDStream3, COSName cOSName, COSName cOSName2, COSName cOSName3, PDVisibleSignDesigner pDVisibleSignDesigner) throws IOException;

    void createVisualSignature(PDDocument pDDocument);

    void createWidgetDictionary(PDSignatureField pDSignatureField, PDResources pDResources) throws IOException;

    PDFTemplateStructure getStructure();

    void closeTemplate(PDDocument pDDocument) throws IOException;
}
