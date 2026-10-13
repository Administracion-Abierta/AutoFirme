package org.apache.pdfbox.pdmodel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDDestinationOrAction;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.common.PDPageLabels;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDMarkInfo;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureTreeRoot;
import org.apache.pdfbox.pdmodel.fixup.AcroFormDefaultFixup;
import org.apache.pdfbox.pdmodel.fixup.PDDocumentFixup;
import org.apache.pdfbox.pdmodel.graphics.color.PDOutputIntent;
import org.apache.pdfbox.pdmodel.graphics.optionalcontent.PDOptionalContentProperties;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionFactory;
import org.apache.pdfbox.pdmodel.interactive.action.PDDocumentCatalogAdditionalActions;
import org.apache.pdfbox.pdmodel.interactive.action.PDURIDictionary;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDNamedDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.pagenavigation.PDThread;
import org.apache.pdfbox.pdmodel.interactive.viewerpreferences.PDViewerPreferences;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/PDDocumentCatalog.class */
public class PDDocumentCatalog implements COSObjectable {
    private static final Log LOG = LogFactory.getLog(PDDocumentCatalog.class);
    private final COSDictionary root;
    private final PDDocument document;
    private PDDocumentFixup acroFormFixupApplied;
    private PDAcroForm cachedAcroForm;

    public PDDocumentCatalog(PDDocument doc) {
        this.document = doc;
        this.root = new COSDictionary();
        this.root.setItem(COSName.TYPE, (COSBase) COSName.CATALOG);
        this.document.getDocument().getTrailer().setItem(COSName.ROOT, (COSBase) this.root);
    }

    public PDDocumentCatalog(PDDocument doc, COSDictionary rootDictionary) {
        this.document = doc;
        this.root = rootDictionary;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.root;
    }

    public PDAcroForm getAcroForm() {
        return getAcroForm(new AcroFormDefaultFixup(this.document));
    }

    public PDAcroForm getAcroForm(PDDocumentFixup acroFormFixup) {
        if (acroFormFixup != null && acroFormFixup != this.acroFormFixupApplied) {
            acroFormFixup.apply();
            this.cachedAcroForm = null;
            this.acroFormFixupApplied = acroFormFixup;
        } else if (this.acroFormFixupApplied != null) {
            LOG.debug("AcroForm content has already been retrieved with fixes applied - original content changed because of that");
        }
        if (this.cachedAcroForm == null) {
            COSDictionary dict = (COSDictionary) this.root.getDictionaryObject(COSName.ACRO_FORM);
            this.cachedAcroForm = dict == null ? null : new PDAcroForm(this.document, dict);
        }
        return this.cachedAcroForm;
    }

    public void setAcroForm(PDAcroForm acroForm) {
        this.root.setItem(COSName.ACRO_FORM, acroForm);
        this.cachedAcroForm = null;
    }

    public PDPageTree getPages() {
        return new PDPageTree((COSDictionary) this.root.getDictionaryObject(COSName.PAGES), this.document);
    }

    public PDViewerPreferences getViewerPreferences() {
        COSBase base = this.root.getDictionaryObject(COSName.VIEWER_PREFERENCES);
        if (base instanceof COSDictionary) {
            return new PDViewerPreferences((COSDictionary) base);
        }
        return null;
    }

    public void setViewerPreferences(PDViewerPreferences prefs) {
        this.root.setItem(COSName.VIEWER_PREFERENCES, prefs);
    }

    public PDDocumentOutline getDocumentOutline() {
        COSBase cosObj = this.root.getDictionaryObject(COSName.OUTLINES);
        if (cosObj instanceof COSDictionary) {
            return new PDDocumentOutline((COSDictionary) cosObj);
        }
        return null;
    }

    public void setDocumentOutline(PDDocumentOutline outlines) {
        this.root.setItem(COSName.OUTLINES, outlines);
    }

    public List<PDThread> getThreads() {
        COSArray array = (COSArray) this.root.getDictionaryObject(COSName.THREADS);
        if (array == null) {
            array = new COSArray();
            this.root.setItem(COSName.THREADS, (COSBase) array);
        }
        List<PDThread> pdObjects = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            pdObjects.add(new PDThread((COSDictionary) array.getObject(i)));
        }
        return new COSArrayList(pdObjects, array);
    }

    public void setThreads(List<PDThread> threads) {
        this.root.setItem(COSName.THREADS, (COSBase) COSArrayList.converterToCOSArray(threads));
    }

    public PDMetadata getMetadata() {
        COSBase metaObj = this.root.getDictionaryObject(COSName.METADATA);
        if (metaObj instanceof COSStream) {
            return new PDMetadata((COSStream) metaObj);
        }
        return null;
    }

    public void setMetadata(PDMetadata meta) {
        this.root.setItem(COSName.METADATA, meta);
    }

    public void setOpenAction(PDDestinationOrAction action) {
        this.root.setItem(COSName.OPEN_ACTION, action);
    }

    public PDDestinationOrAction getOpenAction() throws IOException {
        COSBase openAction = this.root.getDictionaryObject(COSName.OPEN_ACTION);
        if (openAction instanceof COSDictionary) {
            return PDActionFactory.createAction((COSDictionary) openAction);
        }
        if (openAction instanceof COSArray) {
            return PDDestination.create(openAction);
        }
        return null;
    }

    public PDDocumentCatalogAdditionalActions getActions() {
        COSDictionary addAction = (COSDictionary) this.root.getDictionaryObject(COSName.AA);
        if (addAction == null) {
            addAction = new COSDictionary();
            this.root.setItem(COSName.AA, (COSBase) addAction);
        }
        return new PDDocumentCatalogAdditionalActions(addAction);
    }

    public void setActions(PDDocumentCatalogAdditionalActions actions) {
        this.root.setItem(COSName.AA, actions);
    }

    public PDDocumentNameDictionary getNames() {
        COSDictionary names = (COSDictionary) this.root.getDictionaryObject(COSName.NAMES);
        if (names == null) {
            return null;
        }
        return new PDDocumentNameDictionary(this, names);
    }

    public PDDocumentNameDestinationDictionary getDests() {
        PDDocumentNameDestinationDictionary nameDic = null;
        COSDictionary dests = (COSDictionary) this.root.getDictionaryObject(COSName.DESTS);
        if (dests != null) {
            nameDic = new PDDocumentNameDestinationDictionary(dests);
        }
        return nameDic;
    }

    public PDPageDestination findNamedDestinationPage(PDNamedDestination namedDest) throws IOException {
        PDDocumentNameDestinationDictionary nameDestDict;
        PDDestinationNameTreeNode destsTree;
        PDPageDestination pageDestination = null;
        PDDocumentNameDictionary namesDict = getNames();
        if (namesDict != null && (destsTree = namesDict.getDests()) != null) {
            pageDestination = destsTree.getValue(namedDest.getNamedDestination());
        }
        if (pageDestination == null && (nameDestDict = getDests()) != null) {
            String name = namedDest.getNamedDestination();
            pageDestination = (PDPageDestination) nameDestDict.getDestination(name);
        }
        return pageDestination;
    }

    public void setNames(PDDocumentNameDictionary names) {
        this.root.setItem(COSName.NAMES, names);
    }

    public PDMarkInfo getMarkInfo() {
        COSDictionary dic = (COSDictionary) this.root.getDictionaryObject(COSName.MARK_INFO);
        if (dic == null) {
            return null;
        }
        return new PDMarkInfo(dic);
    }

    public void setMarkInfo(PDMarkInfo markInfo) {
        this.root.setItem(COSName.MARK_INFO, markInfo);
    }

    public List<PDOutputIntent> getOutputIntents() {
        List<PDOutputIntent> retval = new ArrayList<>();
        COSArray array = (COSArray) this.root.getDictionaryObject(COSName.OUTPUT_INTENTS);
        if (array != null) {
            for (COSBase cosBase : array) {
                if (cosBase instanceof COSObject) {
                    cosBase = ((COSObject) cosBase).getObject();
                }
                PDOutputIntent oi = new PDOutputIntent((COSDictionary) cosBase);
                retval.add(oi);
            }
        }
        return retval;
    }

    public void addOutputIntent(PDOutputIntent outputIntent) {
        COSArray array = (COSArray) this.root.getDictionaryObject(COSName.OUTPUT_INTENTS);
        if (array == null) {
            array = new COSArray();
            this.root.setItem(COSName.OUTPUT_INTENTS, (COSBase) array);
        }
        array.add(outputIntent.getCOSObject());
    }

    public void setOutputIntents(List<PDOutputIntent> outputIntents) {
        COSArray array = new COSArray();
        for (PDOutputIntent intent : outputIntents) {
            array.add(intent.getCOSObject());
        }
        this.root.setItem(COSName.OUTPUT_INTENTS, (COSBase) array);
    }

    public PageMode getPageMode() {
        String mode = this.root.getNameAsString(COSName.PAGE_MODE);
        if (mode != null) {
            try {
                return PageMode.fromString(mode);
            } catch (IllegalArgumentException e) {
                return PageMode.USE_NONE;
            }
        }
        return PageMode.USE_NONE;
    }

    public void setPageMode(PageMode mode) {
        this.root.setName(COSName.PAGE_MODE, mode.stringValue());
    }

    public PageLayout getPageLayout() {
        String mode = this.root.getNameAsString(COSName.PAGE_LAYOUT);
        if (mode != null) {
            return PageLayout.fromString(mode);
        }
        return PageLayout.SINGLE_PAGE;
    }

    public void setPageLayout(PageLayout layout) {
        this.root.setName(COSName.PAGE_LAYOUT, layout.stringValue());
    }

    public PDURIDictionary getURI() {
        COSDictionary uri = (COSDictionary) this.root.getDictionaryObject(COSName.URI);
        if (uri == null) {
            return null;
        }
        return new PDURIDictionary(uri);
    }

    public void setURI(PDURIDictionary uri) {
        this.root.setItem(COSName.URI, uri);
    }

    public PDStructureTreeRoot getStructureTreeRoot() {
        COSDictionary dict = this.root.getCOSDictionary(COSName.STRUCT_TREE_ROOT);
        if (dict == null) {
            return null;
        }
        return new PDStructureTreeRoot(dict);
    }

    public void setStructureTreeRoot(PDStructureTreeRoot treeRoot) {
        this.root.setItem(COSName.STRUCT_TREE_ROOT, treeRoot);
    }

    public String getLanguage() {
        return this.root.getString(COSName.LANG);
    }

    public void setLanguage(String language) {
        this.root.setString(COSName.LANG, language);
    }

    public String getVersion() {
        return this.root.getNameAsString(COSName.VERSION);
    }

    public void setVersion(String version) {
        this.root.setName(COSName.VERSION, version);
    }

    public PDPageLabels getPageLabels() throws IOException {
        COSDictionary dict = (COSDictionary) this.root.getDictionaryObject(COSName.PAGE_LABELS);
        if (dict == null) {
            return null;
        }
        return new PDPageLabels(this.document, dict);
    }

    public void setPageLabels(PDPageLabels labels) {
        this.root.setItem(COSName.PAGE_LABELS, labels);
    }

    public PDOptionalContentProperties getOCProperties() {
        COSDictionary dict = (COSDictionary) this.root.getDictionaryObject(COSName.OCPROPERTIES);
        if (dict == null) {
            return null;
        }
        return new PDOptionalContentProperties(dict);
    }

    public void setOCProperties(PDOptionalContentProperties ocProperties) {
        this.root.setItem(COSName.OCPROPERTIES, ocProperties);
        if (ocProperties != null && this.document.getVersion() < 1.5d) {
            this.document.setVersion(1.5f);
        }
    }
}
