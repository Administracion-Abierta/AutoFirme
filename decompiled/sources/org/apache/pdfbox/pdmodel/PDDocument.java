package org.apache.pdfbox.pdmodel;

import java.awt.Point;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.io.RandomAccessBuffer;
import org.apache.pdfbox.io.RandomAccessBufferedFileInputStream;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.io.ScratchFile;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdfwriter.COSWriter;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.pdmodel.encryption.PDEncryption;
import org.apache.pdfbox.pdmodel.encryption.ProtectionPolicy;
import org.apache.pdfbox.pdmodel.encryption.SecurityHandler;
import org.apache.pdfbox.pdmodel.encryption.SecurityHandlerFactory;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceDictionary;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.ExternalSigningSupport;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.PDSignature;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureInterface;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SignatureOptions;
import org.apache.pdfbox.pdmodel.interactive.digitalsignature.SigningSupport;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDSignatureField;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/PDDocument.class */
public class PDDocument implements Closeable {
    private static final int[] RESERVE_BYTE_RANGE = {0, 1000000000, 1000000000, 1000000000};
    private static final Log LOG = LogFactory.getLog(PDDocument.class);
    private final COSDocument document;
    private PDDocumentInformation documentInformation;
    private PDDocumentCatalog documentCatalog;
    private PDEncryption encryption;
    private boolean allSecurityToBeRemoved;
    private Long documentId;
    private final RandomAccessRead pdfSource;
    private AccessPermission accessPermission;
    private final Set<PDFont> fontsToSubset;
    private final Set<TrueTypeFont> fontsToClose;
    private SignatureInterface signInterface;
    private SigningSupport signingSupport;
    private ResourceCache resourceCache;
    private boolean signatureAdded;

    static {
        try {
            WritableRaster raster = Raster.createBandedRaster(0, 1, 1, 3, new Point(0, 0));
            PDDeviceRGB.INSTANCE.toRGBImage(raster);
        } catch (IOException ex) {
            LOG.debug("voodoo error", ex);
        }
        try {
            COSNumber.get("0");
            COSNumber.get("1");
        } catch (IOException e) {
        }
    }

    public PDDocument() {
        this(MemoryUsageSetting.setupMainMemoryOnly());
    }

    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    public PDDocument(MemoryUsageSetting memUsageSetting) {
        this.fontsToSubset = new HashSet();
        this.fontsToClose = new HashSet();
        this.resourceCache = new DefaultResourceCache();
        this.signatureAdded = false;
        ScratchFile scratchFile = null;
        try {
            scratchFile = new ScratchFile(memUsageSetting);
        } catch (IOException ioe) {
            LOG.warn("Error initializing scratch file: " + ioe.getMessage() + ". Fall back to main memory usage only.");
            try {
                scratchFile = new ScratchFile(MemoryUsageSetting.setupMainMemoryOnly());
            } catch (IOException e) {
            }
        }
        this.document = new COSDocument(scratchFile);
        this.pdfSource = null;
        COSDictionary cOSDictionary = new COSDictionary();
        this.document.setTrailer(cOSDictionary);
        COSDictionary rootDictionary = new COSDictionary();
        cOSDictionary.setItem(COSName.ROOT, (COSBase) rootDictionary);
        rootDictionary.setItem(COSName.TYPE, COSName.CATALOG);
        rootDictionary.setItem(COSName.VERSION, COSName.getPDFName("1.4"));
        COSDictionary pages = new COSDictionary();
        rootDictionary.setItem(COSName.PAGES, (COSBase) pages);
        pages.setItem(COSName.TYPE, (COSBase) COSName.PAGES);
        COSArray kidsArray = new COSArray();
        pages.setItem(COSName.KIDS, (COSBase) kidsArray);
        pages.setItem(COSName.COUNT, (COSBase) COSInteger.ZERO);
    }

    public PDDocument(COSDocument doc) {
        this(doc, null);
    }

    public PDDocument(COSDocument doc, RandomAccessRead source) {
        this(doc, source, null);
    }

    public PDDocument(COSDocument doc, RandomAccessRead source, AccessPermission permission) {
        this.fontsToSubset = new HashSet();
        this.fontsToClose = new HashSet();
        this.resourceCache = new DefaultResourceCache();
        this.signatureAdded = false;
        this.document = doc;
        this.pdfSource = source;
        this.accessPermission = permission;
    }

    public void addPage(PDPage page) {
        getPages().add(page);
    }

    public void addSignature(PDSignature sigObject) throws IOException {
        addSignature(sigObject, new SignatureOptions());
    }

    public void addSignature(PDSignature sigObject, SignatureOptions options) throws IOException {
        addSignature(sigObject, null, options);
    }

    public void addSignature(PDSignature sigObject, SignatureInterface signatureInterface) throws IOException {
        addSignature(sigObject, signatureInterface, new SignatureOptions());
    }

    public void addSignature(PDSignature sigObject, SignatureInterface signatureInterface, SignatureOptions options) throws IOException {
        if (this.signatureAdded) {
            throw new IllegalStateException("Only one signature may be added in a document");
        }
        this.signatureAdded = true;
        int preferredSignatureSize = options.getPreferredSignatureSize();
        if (preferredSignatureSize > 0) {
            sigObject.setContents(new byte[preferredSignatureSize]);
        } else {
            sigObject.setContents(new byte[SignatureOptions.DEFAULT_SIGNATURE_SIZE]);
        }
        sigObject.setByteRange(RESERVE_BYTE_RANGE);
        this.signInterface = signatureInterface;
        int pageCount = getNumberOfPages();
        if (pageCount == 0) {
            throw new IllegalStateException("Cannot sign an empty document");
        }
        int startIndex = Math.min(Math.max(options.getPage(), 0), pageCount - 1);
        PDPage page = getPage(startIndex);
        PDDocumentCatalog catalog = getDocumentCatalog();
        PDAcroForm acroForm = catalog.getAcroForm(null);
        catalog.getCOSObject().setNeedToBeUpdated(true);
        if (acroForm == null) {
            acroForm = new PDAcroForm(this);
            catalog.setAcroForm(acroForm);
        } else {
            acroForm.getCOSObject().setNeedToBeUpdated(true);
        }
        PDSignatureField signatureField = null;
        COSBase cosFieldBase = acroForm.getCOSObject().getDictionaryObject(COSName.FIELDS);
        if (cosFieldBase instanceof COSArray) {
            COSArray fieldArray = (COSArray) cosFieldBase;
            fieldArray.setNeedToBeUpdated(true);
            signatureField = findSignatureField(acroForm.getFieldIterator(), sigObject);
        } else {
            acroForm.getCOSObject().setItem(COSName.FIELDS, (COSBase) new COSArray());
        }
        if (signatureField == null) {
            signatureField = new PDSignatureField(acroForm);
            signatureField.setValue(sigObject);
            signatureField.getWidgets().get(0).setPage(page);
        } else {
            sigObject.getCOSObject().setNeedToBeUpdated(true);
        }
        signatureField.getWidgets().get(0).setPrinted(true);
        List<PDField> acroFormFields = acroForm.getFields();
        acroForm.getCOSObject().setDirect(true);
        acroForm.setSignaturesExist(true);
        acroForm.setAppendOnly(true);
        boolean checkFields = checkSignatureField(acroForm.getFieldIterator(), signatureField);
        if (checkFields) {
            signatureField.getCOSObject().setNeedToBeUpdated(true);
        } else {
            acroFormFields.add(signatureField);
        }
        COSDocument visualSignature = options.getVisualSignature();
        if (visualSignature == null) {
            prepareNonVisibleSignature(signatureField);
            return;
        }
        prepareVisibleSignature(signatureField, acroForm, visualSignature);
        List<PDAnnotation> annotations = page.getAnnotations();
        page.setAnnotations(annotations);
        if (!(annotations instanceof COSArrayList) || !(acroFormFields instanceof COSArrayList) || !((COSArrayList) annotations).toList().equals(((COSArrayList) acroFormFields).toList()) || !checkFields) {
            PDAnnotationWidget widget = signatureField.getWidgets().get(0);
            if (checkSignatureAnnotation(annotations, widget)) {
                widget.getCOSObject().setNeedToBeUpdated(true);
            } else {
                annotations.add(widget);
            }
        }
        page.getCOSObject().setNeedToBeUpdated(true);
    }

    private PDSignatureField findSignatureField(Iterator<PDField> fieldIterator, PDSignature sigObject) {
        PDSignature signature;
        PDSignatureField signatureField = null;
        while (fieldIterator.hasNext()) {
            PDField pdField = fieldIterator.next();
            if ((pdField instanceof PDSignatureField) && (signature = ((PDSignatureField) pdField).getSignature()) != null && signature.getCOSObject().equals(sigObject.getCOSObject())) {
                signatureField = (PDSignatureField) pdField;
                break;
            }
        }
        return signatureField;
    }

    private boolean checkSignatureField(Iterator<PDField> fieldIterator, PDSignatureField signatureField) {
        while (fieldIterator.hasNext()) {
            PDField field = fieldIterator.next();
            if ((field instanceof PDSignatureField) && field.getCOSObject().equals(signatureField.getCOSObject())) {
                return true;
            }
        }
        return false;
    }

    private boolean checkSignatureAnnotation(List<PDAnnotation> annotations, PDAnnotationWidget widget) {
        for (PDAnnotation annotation : annotations) {
            if (annotation.getCOSObject().equals(widget.getCOSObject())) {
                return true;
            }
        }
        return false;
    }

    private void prepareVisibleSignature(PDSignatureField signatureField, PDAcroForm acroForm, COSDocument visualSignature) {
        boolean annotNotFound = true;
        boolean sigFieldNotFound = true;
        for (COSObject cosObject : visualSignature.getObjects()) {
            if (!annotNotFound && !sigFieldNotFound) {
                break;
            }
            COSBase base = cosObject.getObject();
            if (base instanceof COSDictionary) {
                COSDictionary cosBaseDict = (COSDictionary) base;
                COSBase type = cosBaseDict.getDictionaryObject(COSName.TYPE);
                if (annotNotFound && COSName.ANNOT.equals(type)) {
                    assignSignatureRectangle(signatureField, cosBaseDict);
                    annotNotFound = false;
                }
                COSBase fieldType = cosBaseDict.getDictionaryObject(COSName.FT);
                COSBase apDict = cosBaseDict.getDictionaryObject(COSName.AP);
                if (sigFieldNotFound && COSName.SIG.equals(fieldType) && (apDict instanceof COSDictionary)) {
                    assignAppearanceDictionary(signatureField, (COSDictionary) apDict);
                    assignAcroFormDefaultResource(acroForm, cosBaseDict);
                    sigFieldNotFound = false;
                }
            }
        }
        if (annotNotFound || sigFieldNotFound) {
            throw new IllegalArgumentException("Template is missing required objects");
        }
    }

    private void assignSignatureRectangle(PDSignatureField signatureField, COSDictionary annotDict) {
        COSArray rectArray = (COSArray) annotDict.getDictionaryObject(COSName.RECT);
        PDRectangle existingRectangle = signatureField.getWidgets().get(0).getRectangle();
        if (existingRectangle == null || existingRectangle.getCOSArray().size() != 4) {
            PDRectangle rect = new PDRectangle(rectArray);
            signatureField.getWidgets().get(0).setRectangle(rect);
        }
    }

    private void assignAppearanceDictionary(PDSignatureField signatureField, COSDictionary apDict) {
        PDAppearanceDictionary ap = new PDAppearanceDictionary(apDict);
        apDict.setDirect(true);
        signatureField.getWidgets().get(0).setAppearance(ap);
    }

    private void assignAcroFormDefaultResource(PDAcroForm acroForm, COSDictionary newDict) {
        COSBase newBase = newDict.getDictionaryObject(COSName.DR);
        if (newBase instanceof COSDictionary) {
            COSDictionary newDR = (COSDictionary) newBase;
            PDResources defaultResources = acroForm.getDefaultResources();
            if (defaultResources == null) {
                acroForm.getCOSObject().setItem(COSName.DR, (COSBase) newDR);
                newDR.setDirect(true);
                newDR.setNeedToBeUpdated(true);
                return;
            }
            COSDictionary oldDR = defaultResources.getCOSObject();
            COSBase newXObjectBase = newDR.getItem(COSName.XOBJECT);
            COSBase oldXObjectBase = oldDR.getItem(COSName.XOBJECT);
            if ((newXObjectBase instanceof COSDictionary) && (oldXObjectBase instanceof COSDictionary)) {
                ((COSDictionary) oldXObjectBase).addAll((COSDictionary) newXObjectBase);
                oldDR.setNeedToBeUpdated(true);
            }
        }
    }

    private void prepareNonVisibleSignature(PDSignatureField signatureField) {
        signatureField.getWidgets().get(0).setRectangle(new PDRectangle());
        PDAppearanceDictionary appearanceDictionary = new PDAppearanceDictionary();
        PDAppearanceStream appearanceStream = new PDAppearanceStream(this);
        appearanceStream.setBBox(new PDRectangle());
        appearanceDictionary.setNormalAppearance(appearanceStream);
        signatureField.getWidgets().get(0).setAppearance(appearanceDictionary);
    }

    @Deprecated
    public void addSignatureField(List<PDSignatureField> sigFields, SignatureInterface signatureInterface, SignatureOptions options) throws IOException {
        PDDocumentCatalog catalog = getDocumentCatalog();
        catalog.getCOSObject().setNeedToBeUpdated(true);
        PDAcroForm acroForm = catalog.getAcroForm(null);
        if (acroForm == null) {
            acroForm = new PDAcroForm(this);
            catalog.setAcroForm(acroForm);
        }
        COSDictionary acroFormDict = acroForm.getCOSObject();
        acroFormDict.setDirect(true);
        acroFormDict.setNeedToBeUpdated(true);
        if (!acroForm.isSignaturesExist()) {
            acroForm.setSignaturesExist(true);
        }
        List<PDField> acroformFields = acroForm.getFields();
        for (PDSignatureField sigField : sigFields) {
            sigField.getCOSObject().setNeedToBeUpdated(true);
            boolean checkSignatureField = checkSignatureField(acroForm.getFieldIterator(), sigField);
            if (checkSignatureField) {
                sigField.getCOSObject().setNeedToBeUpdated(true);
            } else {
                acroformFields.add(sigField);
            }
            if (sigField.getSignature() != null) {
                sigField.getCOSObject().setNeedToBeUpdated(true);
                if (options == null) {
                }
                addSignature(sigField.getSignature(), signatureInterface, options);
            }
        }
    }

    public void removePage(PDPage page) {
        getPages().remove(page);
    }

    public void removePage(int pageNumber) {
        getPages().remove(pageNumber);
    }

    public PDPage importPage(PDPage page) throws IOException {
        PDPage importedPage = new PDPage(new COSDictionary(page.getCOSObject()), this.resourceCache);
        PDStream dest = new PDStream(this, page.getContents(), COSName.FLATE_DECODE);
        importedPage.setContents(dest);
        addPage(importedPage);
        importedPage.setCropBox(new PDRectangle(page.getCropBox().getCOSArray()));
        importedPage.setMediaBox(new PDRectangle(page.getMediaBox().getCOSArray()));
        importedPage.setRotation(page.getRotation());
        if (page.getResources() != null && !page.getCOSObject().containsKey(COSName.RESOURCES)) {
            LOG.warn("inherited resources of source document are not imported to destination page");
            LOG.warn("call importedPage.setResources(page.getResources()) to do this");
        }
        return importedPage;
    }

    public COSDocument getDocument() {
        return this.document;
    }

    public PDDocumentInformation getDocumentInformation() {
        if (this.documentInformation == null) {
            COSDictionary trailer = this.document.getTrailer();
            COSDictionary infoDic = trailer.getCOSDictionary(COSName.INFO);
            if (infoDic == null) {
                infoDic = new COSDictionary();
                trailer.setItem(COSName.INFO, (COSBase) infoDic);
            }
            this.documentInformation = new PDDocumentInformation(infoDic);
        }
        return this.documentInformation;
    }

    public void setDocumentInformation(PDDocumentInformation info) {
        this.documentInformation = info;
        this.document.getTrailer().setItem(COSName.INFO, (COSBase) info.getCOSObject());
    }

    public PDDocumentCatalog getDocumentCatalog() {
        if (this.documentCatalog == null) {
            COSDictionary trailer = this.document.getTrailer();
            COSBase dictionary = trailer.getDictionaryObject(COSName.ROOT);
            if (dictionary instanceof COSDictionary) {
                this.documentCatalog = new PDDocumentCatalog(this, (COSDictionary) dictionary);
            } else {
                this.documentCatalog = new PDDocumentCatalog(this);
            }
        }
        return this.documentCatalog;
    }

    public boolean isEncrypted() {
        return this.document.isEncrypted();
    }

    public PDEncryption getEncryption() {
        if (this.encryption == null && isEncrypted()) {
            this.encryption = new PDEncryption(this.document.getEncryptionDictionary());
        }
        return this.encryption;
    }

    public void setEncryptionDictionary(PDEncryption encryption) throws IOException {
        this.encryption = encryption;
    }

    public PDSignature getLastSignatureDictionary() throws IOException {
        List<PDSignature> signatureDictionaries = getSignatureDictionaries();
        int size = signatureDictionaries.size();
        if (size > 0) {
            return signatureDictionaries.get(size - 1);
        }
        return null;
    }

    public List<PDSignatureField> getSignatureFields() throws IOException {
        List<PDSignatureField> fields = new ArrayList<>();
        PDAcroForm acroForm = getDocumentCatalog().getAcroForm(null);
        if (acroForm != null) {
            for (PDField field : acroForm.getFieldTree()) {
                if (field instanceof PDSignatureField) {
                    fields.add((PDSignatureField) field);
                }
            }
        }
        return fields;
    }

    public List<PDSignature> getSignatureDictionaries() throws IOException {
        List<PDSignature> signatures = new ArrayList<>();
        for (PDSignatureField field : getSignatureFields()) {
            COSBase value = field.getCOSObject().getDictionaryObject(COSName.V);
            if (value != null) {
                signatures.add(new PDSignature((COSDictionary) value));
            }
        }
        return signatures;
    }

    public void registerTrueTypeFontForClosing(TrueTypeFont ttf) {
        this.fontsToClose.add(ttf);
    }

    Set<PDFont> getFontsToSubset() {
        return this.fontsToSubset;
    }

    public static PDDocument load(File file) throws IOException {
        return load(file, "", MemoryUsageSetting.setupMainMemoryOnly());
    }

    public static PDDocument load(File file, MemoryUsageSetting memUsageSetting) throws IOException {
        return load(file, "", (InputStream) null, (String) null, memUsageSetting);
    }

    public static PDDocument load(File file, String password) throws IOException {
        return load(file, password, (InputStream) null, (String) null, MemoryUsageSetting.setupMainMemoryOnly());
    }

    public static PDDocument load(File file, String password, MemoryUsageSetting memUsageSetting) throws IOException {
        return load(file, password, (InputStream) null, (String) null, memUsageSetting);
    }

    public static PDDocument load(File file, String password, InputStream keyStore, String alias) throws IOException {
        return load(file, password, keyStore, alias, MemoryUsageSetting.setupMainMemoryOnly());
    }

    public static PDDocument load(File file, String password, InputStream keyStore, String alias, MemoryUsageSetting memUsageSetting) throws IOException {
        RandomAccessBufferedFileInputStream raFile = new RandomAccessBufferedFileInputStream(file);
        try {
            return load(raFile, password, keyStore, alias, memUsageSetting);
        } catch (IOException ioe) {
            IOUtils.closeQuietly(raFile);
            throw ioe;
        }
    }

    private static PDDocument load(RandomAccessBufferedFileInputStream raFile, String password, InputStream keyStore, String alias, MemoryUsageSetting memUsageSetting) throws IOException {
        ScratchFile scratchFile = new ScratchFile(memUsageSetting);
        try {
            PDFParser parser = new PDFParser(raFile, password, keyStore, alias, scratchFile);
            parser.parse();
            return parser.getPDDocument();
        } catch (IOException ioe) {
            IOUtils.closeQuietly(scratchFile);
            throw ioe;
        }
    }

    public static PDDocument load(InputStream input) throws IOException {
        return load(input, "", (InputStream) null, (String) null, MemoryUsageSetting.setupMainMemoryOnly());
    }

    public static PDDocument load(InputStream input, MemoryUsageSetting memUsageSetting) throws IOException {
        return load(input, "", (InputStream) null, (String) null, memUsageSetting);
    }

    public static PDDocument load(InputStream input, String password) throws IOException {
        return load(input, password, (InputStream) null, (String) null, MemoryUsageSetting.setupMainMemoryOnly());
    }

    public static PDDocument load(InputStream input, String password, InputStream keyStore, String alias) throws IOException {
        return load(input, password, keyStore, alias, MemoryUsageSetting.setupMainMemoryOnly());
    }

    public static PDDocument load(InputStream input, String password, MemoryUsageSetting memUsageSetting) throws IOException {
        return load(input, password, (InputStream) null, (String) null, memUsageSetting);
    }

    public static PDDocument load(InputStream input, String password, InputStream keyStore, String alias, MemoryUsageSetting memUsageSetting) throws IOException {
        ScratchFile scratchFile = new ScratchFile(memUsageSetting);
        try {
            RandomAccessRead source = scratchFile.createBuffer(input);
            PDFParser parser = new PDFParser(source, password, keyStore, alias, scratchFile);
            parser.parse();
            return parser.getPDDocument();
        } catch (IOException ioe) {
            IOUtils.closeQuietly(scratchFile);
            throw ioe;
        }
    }

    public static PDDocument load(byte[] input) throws IOException {
        return load(input, "");
    }

    public static PDDocument load(byte[] input, String password) throws IOException {
        return load(input, password, (InputStream) null, (String) null);
    }

    public static PDDocument load(byte[] input, String password, InputStream keyStore, String alias) throws IOException {
        return load(input, password, keyStore, alias, MemoryUsageSetting.setupMainMemoryOnly());
    }

    public static PDDocument load(byte[] input, String password, InputStream keyStore, String alias, MemoryUsageSetting memUsageSetting) throws IOException {
        ScratchFile scratchFile = new ScratchFile(memUsageSetting);
        RandomAccessRead source = new RandomAccessBuffer(input);
        PDFParser parser = new PDFParser(source, password, keyStore, alias, scratchFile);
        parser.parse();
        return parser.getPDDocument();
    }

    public void save(String fileName) throws IOException {
        save(new File(fileName));
    }

    public void save(File file) throws IOException {
        save(new BufferedOutputStream(new FileOutputStream(file)));
    }

    public void save(OutputStream output) throws IOException {
        if (this.document.isClosed()) {
            throw new IOException("Cannot save a document which has been closed");
        }
        for (PDFont font : this.fontsToSubset) {
            font.subset();
        }
        this.fontsToSubset.clear();
        COSWriter writer = new COSWriter(output);
        try {
            writer.write(this);
        } finally {
            writer.close();
        }
    }

    public void saveIncremental(OutputStream output) throws IOException {
        COSWriter writer = null;
        try {
            if (this.pdfSource == null) {
                throw new IllegalStateException("document was not loaded from a file or a stream");
            }
            COSWriter writer2 = new COSWriter(output, this.pdfSource);
            writer2.write(this, this.signInterface);
            if (writer2 != null) {
                writer2.close();
            }
        } catch (Throwable th) {
            if (0 != 0) {
                writer.close();
            }
            throw th;
        }
    }

    public void saveIncremental(OutputStream output, Set<COSDictionary> objectsToWrite) throws IOException {
        if (this.pdfSource == null) {
            throw new IllegalStateException("document was not loaded from a file or a stream");
        }
        COSWriter writer = null;
        try {
            COSWriter writer2 = new COSWriter(output, this.pdfSource, objectsToWrite);
            writer2.write(this, this.signInterface);
            if (writer2 != null) {
            }
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public ExternalSigningSupport saveIncrementalForExternalSigning(OutputStream output) throws IOException {
        if (this.pdfSource == null) {
            throw new IllegalStateException("document was not loaded from a file or a stream");
        }
        PDSignature foundSignature = null;
        for (PDSignature sig : getSignatureDictionaries()) {
            foundSignature = sig;
            if (sig.getCOSObject().isNeedToBeUpdated()) {
                break;
            }
        }
        int[] byteRange = foundSignature.getByteRange();
        if (!Arrays.equals(byteRange, RESERVE_BYTE_RANGE)) {
            throw new IllegalStateException("signature reserve byte range has been changed after addSignature(), please set the byte range that existed after addSignature()");
        }
        COSWriter writer = new COSWriter(output, this.pdfSource);
        writer.write(this);
        this.signingSupport = new SigningSupport(writer);
        return this.signingSupport;
    }

    public PDPage getPage(int pageIndex) {
        return getDocumentCatalog().getPages().get(pageIndex);
    }

    public PDPageTree getPages() {
        return getDocumentCatalog().getPages();
    }

    public int getNumberOfPages() {
        return getDocumentCatalog().getPages().getCount();
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (!this.document.isClosed()) {
            IOException firstException = IOUtils.closeAndLogException(this.document, LOG, "COSDocument", this.signingSupport != null ? IOUtils.closeAndLogException(this.signingSupport, LOG, "SigningSupport", null) : null);
            if (this.pdfSource != null) {
                firstException = IOUtils.closeAndLogException(this.pdfSource, LOG, "RandomAccessRead pdfSource", firstException);
            }
            for (TrueTypeFont ttf : this.fontsToClose) {
                firstException = IOUtils.closeAndLogException(ttf, LOG, "TrueTypeFont", firstException);
            }
            if (firstException != null) {
                throw firstException;
            }
        }
    }

    public void protect(ProtectionPolicy policy) throws IOException {
        if (isAllSecurityToBeRemoved()) {
            LOG.warn("do not call setAllSecurityToBeRemoved(true) before calling protect(), as protect() implies setAllSecurityToBeRemoved(false)");
            setAllSecurityToBeRemoved(false);
        }
        if (!isEncrypted()) {
            this.encryption = new PDEncryption();
        }
        SecurityHandler securityHandler = SecurityHandlerFactory.INSTANCE.newSecurityHandlerForPolicy(policy);
        if (securityHandler == null) {
            throw new IOException("No security handler for policy " + policy);
        }
        getEncryption().setSecurityHandler(securityHandler);
    }

    public AccessPermission getCurrentAccessPermission() {
        if (this.accessPermission == null) {
            this.accessPermission = AccessPermission.getOwnerAccessPermission();
        }
        return this.accessPermission;
    }

    public boolean isAllSecurityToBeRemoved() {
        return this.allSecurityToBeRemoved;
    }

    public void setAllSecurityToBeRemoved(boolean removeAllSecurity) {
        this.allSecurityToBeRemoved = removeAllSecurity;
    }

    public Long getDocumentId() {
        return this.documentId;
    }

    public void setDocumentId(Long docId) {
        this.documentId = docId;
    }

    public float getVersion() {
        float headerVersionFloat = getDocument().getVersion();
        if (headerVersionFloat >= 1.4f) {
            String catalogVersion = getDocumentCatalog().getVersion();
            float catalogVersionFloat = -1.0f;
            if (catalogVersion != null) {
                try {
                    catalogVersionFloat = Float.parseFloat(catalogVersion);
                } catch (NumberFormatException exception) {
                    LOG.error("Can't extract the version number of the document catalog.", exception);
                }
            }
            return Math.max(catalogVersionFloat, headerVersionFloat);
        }
        return headerVersionFloat;
    }

    public void setVersion(float newVersion) {
        float currentVersion = getVersion();
        if (newVersion == currentVersion) {
            return;
        }
        if (newVersion < currentVersion) {
            LOG.error("It's not allowed to downgrade the version of a pdf.");
        } else if (getDocument().getVersion() >= 1.4f) {
            getDocumentCatalog().setVersion(Float.toString(newVersion));
        } else {
            getDocument().setVersion(newVersion);
        }
    }

    public ResourceCache getResourceCache() {
        return this.resourceCache;
    }

    public void setResourceCache(ResourceCache resourceCache) {
        this.resourceCache = resourceCache;
    }
}
