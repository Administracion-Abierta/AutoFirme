package org.apache.pdfbox.multipdf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDDocumentNameDestinationDictionary;
import org.apache.pdfbox.pdmodel.PDDocumentNameDictionary;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.PDStructureElementNameTreeNode;
import org.apache.pdfbox.pdmodel.PageMode;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDDestinationOrAction;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.common.PDNameTreeNode;
import org.apache.pdfbox.pdmodel.common.PDNumberTreeNode;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDMarkInfo;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDParentTreeValue;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureElement;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureTreeRoot;
import org.apache.pdfbox.pdmodel.graphics.color.PDOutputIntent;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionGoTo;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDNonTerminalField;
import org.apache.pdfbox.pdmodel.interactive.viewerpreferences.PDViewerPreferences;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/multipdf/PDFMergerUtility.class */
public class PDFMergerUtility {
    private static final Log LOG = LogFactory.getLog(PDFMergerUtility.class);
    private String destinationFileName;
    private OutputStream destinationStream;
    private boolean ignoreAcroFormErrors = false;
    private PDDocumentInformation destinationDocumentInformation = null;
    private PDMetadata destinationMetadata = null;
    private DocumentMergeMode documentMergeMode = DocumentMergeMode.PDFBOX_LEGACY_MODE;
    private AcroFormMergeMode acroFormMergeMode = AcroFormMergeMode.PDFBOX_LEGACY_MODE;
    private int nextFieldNum = 1;
    private final List<Object> sources = new ArrayList();

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/multipdf/PDFMergerUtility$AcroFormMergeMode.class */
    public enum AcroFormMergeMode {
        JOIN_FORM_FIELDS_MODE,
        PDFBOX_LEGACY_MODE
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/multipdf/PDFMergerUtility$DocumentMergeMode.class */
    public enum DocumentMergeMode {
        OPTIMIZE_RESOURCES_MODE,
        PDFBOX_LEGACY_MODE
    }

    public AcroFormMergeMode getAcroFormMergeMode() {
        return this.acroFormMergeMode;
    }

    public void setAcroFormMergeMode(AcroFormMergeMode theAcroFormMergeMode) {
        this.acroFormMergeMode = theAcroFormMergeMode;
    }

    public void setDocumentMergeMode(DocumentMergeMode theDocumentMergeMode) {
        this.documentMergeMode = theDocumentMergeMode;
    }

    public DocumentMergeMode getDocumentMergeMode() {
        return this.documentMergeMode;
    }

    public String getDestinationFileName() {
        return this.destinationFileName;
    }

    public void setDestinationFileName(String destination) {
        this.destinationFileName = destination;
    }

    public OutputStream getDestinationStream() {
        return this.destinationStream;
    }

    public void setDestinationStream(OutputStream destStream) {
        this.destinationStream = destStream;
    }

    public PDDocumentInformation getDestinationDocumentInformation() {
        return this.destinationDocumentInformation;
    }

    public void setDestinationDocumentInformation(PDDocumentInformation info) {
        this.destinationDocumentInformation = info;
    }

    public PDMetadata getDestinationMetadata() {
        return this.destinationMetadata;
    }

    public void setDestinationMetadata(PDMetadata meta) {
        this.destinationMetadata = meta;
    }

    public void addSource(String source) throws FileNotFoundException {
        addSource(new File(source));
    }

    public void addSource(File source) throws FileNotFoundException {
        this.sources.add(source);
    }

    public void addSource(InputStream source) {
        this.sources.add(source);
    }

    public void addSources(List<InputStream> sourcesList) {
        this.sources.addAll(sourcesList);
    }

    @Deprecated
    public void mergeDocuments() throws IOException {
        mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
    }

    public void mergeDocuments(MemoryUsageSetting memUsageSetting) throws IOException {
        if (this.documentMergeMode == DocumentMergeMode.PDFBOX_LEGACY_MODE) {
            legacyMergeDocuments(memUsageSetting);
        } else if (this.documentMergeMode == DocumentMergeMode.OPTIMIZE_RESOURCES_MODE) {
            optimizedMergeDocuments(memUsageSetting);
        }
    }

    private void optimizedMergeDocuments(MemoryUsageSetting memUsageSetting) throws IOException {
        PDDocument sourceDoc;
        PDDocument destination = null;
        try {
            destination = new PDDocument(memUsageSetting);
            PDFCloneUtility cloner = new PDFCloneUtility(destination);
            for (Object sourceObject : this.sources) {
                try {
                    if (sourceObject instanceof File) {
                        sourceDoc = PDDocument.load((File) sourceObject, memUsageSetting);
                    } else {
                        sourceDoc = PDDocument.load((InputStream) sourceObject, memUsageSetting);
                    }
                    for (PDPage page : sourceDoc.getPages()) {
                        PDPage newPage = new PDPage((COSDictionary) cloner.cloneForNewDocument(page.getCOSObject()));
                        newPage.setCropBox(page.getCropBox());
                        newPage.setMediaBox(page.getMediaBox());
                        newPage.setRotation(page.getRotation());
                        PDResources resources = page.getResources();
                        if (resources != null) {
                            newPage.setResources(new PDResources((COSDictionary) cloner.cloneForNewDocument(resources)));
                        } else {
                            newPage.setResources(new PDResources());
                        }
                        destination.addPage(newPage);
                    }
                    IOUtils.closeQuietly(sourceDoc);
                } catch (Throwable th) {
                    IOUtils.closeQuietly(null);
                    throw th;
                }
            }
            if (this.destinationStream == null) {
                destination.save(this.destinationFileName);
            } else {
                destination.save(this.destinationStream);
            }
            IOUtils.closeQuietly(destination);
        } catch (Throwable th2) {
            IOUtils.closeQuietly(destination);
            throw th2;
        }
    }

    private void legacyMergeDocuments(MemoryUsageSetting memUsageSetting) throws IOException {
        MemoryUsageSetting partitionedCopy;
        PDDocument sourceDoc;
        if (this.sources.size() > 0) {
            List<PDDocument> tobeclosed = new ArrayList<>(this.sources.size());
            try {
                if (memUsageSetting != null) {
                    partitionedCopy = memUsageSetting.getPartitionedCopy(this.sources.size() + 1);
                } else {
                    partitionedCopy = MemoryUsageSetting.setupMainMemoryOnly();
                }
                MemoryUsageSetting partitionedMemSetting = partitionedCopy;
                PDDocument destination = new PDDocument(partitionedMemSetting);
                for (Object sourceObject : this.sources) {
                    if (sourceObject instanceof File) {
                        sourceDoc = PDDocument.load((File) sourceObject, partitionedMemSetting);
                    } else {
                        sourceDoc = PDDocument.load((InputStream) sourceObject, partitionedMemSetting);
                    }
                    tobeclosed.add(sourceDoc);
                    appendDocument(destination, sourceDoc);
                }
                if (this.destinationDocumentInformation != null) {
                    destination.setDocumentInformation(this.destinationDocumentInformation);
                }
                if (this.destinationMetadata != null) {
                    destination.getDocumentCatalog().setMetadata(this.destinationMetadata);
                }
                if (this.destinationStream == null) {
                    destination.save(this.destinationFileName);
                } else {
                    destination.save(this.destinationStream);
                }
                if (destination != null) {
                    IOUtils.closeAndLogException(destination, LOG, "PDDocument", null);
                }
                for (PDDocument doc : tobeclosed) {
                    IOUtils.closeAndLogException(doc, LOG, "PDDocument", null);
                }
            } catch (Throwable th) {
                if (0 != 0) {
                    IOUtils.closeAndLogException(null, LOG, "PDDocument", null);
                }
                for (PDDocument doc2 : tobeclosed) {
                    IOUtils.closeAndLogException(doc2, LOG, "PDDocument", null);
                }
                throw th;
            }
        }
    }

    public void appendDocument(PDDocument destination, PDDocument source) throws IOException {
        PDPageDestination pageDestination;
        PDNumberTreeNode srcParentTree;
        COSArray destNums;
        PDOutlineItem destLastOutlineItem;
        PDPage page;
        if (source.getDocument().isClosed()) {
            throw new IOException("Error: source PDF is closed.");
        }
        if (destination.getDocument().isClosed()) {
            throw new IOException("Error: destination PDF is closed.");
        }
        PDDocumentCatalog srcCatalog = source.getDocumentCatalog();
        if (isDynamicXfa(srcCatalog.getAcroForm())) {
            throw new IOException("Error: can't merge source document containing dynamic XFA form content.");
        }
        PDDocumentInformation destInfo = destination.getDocumentInformation();
        PDDocumentInformation srcInfo = source.getDocumentInformation();
        mergeInto(srcInfo.getCOSObject(), destInfo.getCOSObject(), Collections.emptySet());
        float destVersion = destination.getVersion();
        float srcVersion = source.getVersion();
        if (destVersion < srcVersion) {
            destination.setVersion(srcVersion);
        }
        int pageIndexOpenActionDest = -1;
        PDDocumentCatalog destCatalog = destination.getDocumentCatalog();
        if (destCatalog.getOpenAction() == null) {
            PDDestinationOrAction openAction = null;
            try {
                openAction = srcCatalog.getOpenAction();
            } catch (IOException ex) {
                LOG.error("Invalid OpenAction ignored", ex);
            }
            PDDestination openActionDestination = null;
            if (openAction instanceof PDActionGoTo) {
                openActionDestination = ((PDActionGoTo) openAction).getDestination();
            } else if (openAction instanceof PDDestination) {
                openActionDestination = (PDDestination) openAction;
            }
            if ((openActionDestination instanceof PDPageDestination) && (page = ((PDPageDestination) openActionDestination).getPage()) != null) {
                pageIndexOpenActionDest = srcCatalog.getPages().indexOf(page);
            }
            destCatalog.setOpenAction(openAction);
        }
        PDFCloneUtility cloner = new PDFCloneUtility(destination);
        mergeAcroForm(cloner, destCatalog, srcCatalog);
        COSArray destThreads = (COSArray) destCatalog.getCOSObject().getDictionaryObject(COSName.THREADS);
        COSArray srcThreads = (COSArray) cloner.cloneForNewDocument(destCatalog.getCOSObject().getDictionaryObject(COSName.THREADS));
        if (destThreads == null) {
            destCatalog.getCOSObject().setItem(COSName.THREADS, (COSBase) srcThreads);
        } else {
            destThreads.addAll(srcThreads);
        }
        PDDocumentNameDictionary destNames = destCatalog.getNames();
        PDDocumentNameDictionary srcNames = srcCatalog.getNames();
        if (srcNames != null) {
            if (destNames == null) {
                destCatalog.getCOSObject().setItem(COSName.NAMES, cloner.cloneForNewDocument(srcNames));
            } else {
                cloner.cloneMerge(srcNames, destNames);
            }
        }
        if (destNames != null && destNames.getCOSObject().containsKey(COSName.ID_TREE)) {
            destNames.getCOSObject().removeItem(COSName.ID_TREE);
            LOG.warn("Removed /IDTree from /Names dictionary, doesn't belong there");
        }
        PDDocumentNameDestinationDictionary srcDests = srcCatalog.getDests();
        if (srcDests != null) {
            PDDocumentNameDestinationDictionary destDests = destCatalog.getDests();
            if (destDests == null) {
                destCatalog.getCOSObject().setItem(COSName.DESTS, cloner.cloneForNewDocument(srcDests));
            } else {
                cloner.cloneMerge(srcDests, destDests);
            }
        }
        PDDocumentOutline srcOutline = srcCatalog.getDocumentOutline();
        if (srcOutline != null) {
            PDDocumentOutline destOutline = destCatalog.getDocumentOutline();
            if (destOutline == null || destOutline.getFirstChild() == null) {
                PDDocumentOutline cloned = new PDDocumentOutline((COSDictionary) cloner.cloneForNewDocument(srcOutline));
                destCatalog.setDocumentOutline(cloned);
            } else {
                PDOutlineItem firstChild = destOutline.getFirstChild();
                while (true) {
                    destLastOutlineItem = firstChild;
                    PDOutlineItem outlineItem = destLastOutlineItem.getNextSibling();
                    if (outlineItem == null) {
                        break;
                    } else {
                        firstChild = outlineItem;
                    }
                }
                for (PDOutlineItem item : srcOutline.children()) {
                    COSDictionary clonedDict = (COSDictionary) cloner.cloneForNewDocument(item);
                    clonedDict.removeItem(COSName.PREV);
                    clonedDict.removeItem(COSName.NEXT);
                    PDOutlineItem clonedItem = new PDOutlineItem(clonedDict);
                    destLastOutlineItem.insertSiblingAfter(clonedItem);
                    destLastOutlineItem = destLastOutlineItem.getNextSibling();
                }
            }
        }
        PageMode destPageMode = destCatalog.getPageMode();
        if (destPageMode == null) {
            PageMode srcPageMode = srcCatalog.getPageMode();
            destCatalog.setPageMode(srcPageMode);
        }
        COSDictionary srcLabels = srcCatalog.getCOSObject().getCOSDictionary(COSName.PAGE_LABELS);
        if (srcLabels != null) {
            int destPageCount = destination.getNumberOfPages();
            COSDictionary destLabels = destCatalog.getCOSObject().getCOSDictionary(COSName.PAGE_LABELS);
            if (destLabels == null) {
                COSDictionary destLabels2 = new COSDictionary();
                destNums = new COSArray();
                destLabels2.setItem(COSName.NUMS, (COSBase) destNums);
                destCatalog.getCOSObject().setItem(COSName.PAGE_LABELS, (COSBase) destLabels2);
            } else {
                destNums = (COSArray) destLabels.getDictionaryObject(COSName.NUMS);
            }
            COSArray srcNums = (COSArray) srcLabels.getDictionaryObject(COSName.NUMS);
            if (srcNums != null) {
                int startSize = destNums.size();
                for (int i = 0; i < srcNums.size(); i += 2) {
                    COSBase base = srcNums.getObject(i);
                    if (!(base instanceof COSNumber)) {
                        LOG.error("page labels ignored, index " + i + " should be a number, but is " + base);
                        while (destNums.size() > startSize) {
                            destNums.remove(startSize);
                        }
                        break;
                    } else {
                        COSNumber labelIndex = (COSNumber) base;
                        long labelIndexValue = labelIndex.intValue();
                        destNums.add((COSBase) COSInteger.get(labelIndexValue + ((long) destPageCount)));
                        destNums.add(cloner.cloneForNewDocument(srcNums.getObject(i + 1)));
                    }
                }
            }
        }
        COSStream destMetadata = destCatalog.getCOSObject().getCOSStream(COSName.METADATA);
        COSStream srcMetadata = srcCatalog.getCOSObject().getCOSStream(COSName.METADATA);
        if (destMetadata == null && srcMetadata != null) {
            try {
                PDStream newStream = new PDStream(destination, (InputStream) srcMetadata.createInputStream(), (COSName) null);
                mergeInto(srcMetadata, newStream.getCOSObject(), new HashSet(Arrays.asList(COSName.FILTER, COSName.LENGTH)));
                destCatalog.getCOSObject().setItem(COSName.METADATA, newStream);
            } catch (IOException ex2) {
                LOG.error("Metadata skipped because it could not be read", ex2);
            }
        }
        COSDictionary destOCP = destCatalog.getCOSObject().getCOSDictionary(COSName.OCPROPERTIES);
        COSDictionary srcOCP = srcCatalog.getCOSObject().getCOSDictionary(COSName.OCPROPERTIES);
        if (destOCP == null && srcOCP != null) {
            destCatalog.getCOSObject().setItem(COSName.OCPROPERTIES, cloner.cloneForNewDocument(srcOCP));
        } else if (destOCP != null && srcOCP != null) {
            cloner.cloneMerge(srcOCP, destOCP);
        }
        mergeOutputIntents(cloner, srcCatalog, destCatalog);
        boolean mergeStructTree = false;
        int destParentTreeNextKey = -1;
        Map<Integer, COSObjectable> srcNumberTreeAsMap = null;
        Map<Integer, COSObjectable> destNumberTreeAsMap = null;
        PDStructureTreeRoot srcStructTree = srcCatalog.getStructureTreeRoot();
        PDStructureTreeRoot destStructTree = destCatalog.getStructureTreeRoot();
        if (destStructTree == null && srcStructTree != null) {
            destStructTree = new PDStructureTreeRoot();
            destCatalog.setStructureTreeRoot(destStructTree);
            destStructTree.setParentTree(new PDNumberTreeNode(PDParentTreeValue.class));
            for (PDPage page2 : destCatalog.getPages()) {
                page2.getCOSObject().removeItem(COSName.STRUCT_PARENTS);
                for (PDAnnotation ann : page2.getAnnotations()) {
                    ann.getCOSObject().removeItem(COSName.STRUCT_PARENT);
                }
            }
        }
        if (destStructTree != null) {
            PDNumberTreeNode destParentTree = destStructTree.getParentTree();
            destParentTreeNextKey = destStructTree.getParentTreeNextKey();
            if (destParentTree != null) {
                destNumberTreeAsMap = getNumberTreeAsMap(destParentTree);
                if (destParentTreeNextKey < 0) {
                    if (destNumberTreeAsMap.isEmpty()) {
                        destParentTreeNextKey = 0;
                    } else {
                        destParentTreeNextKey = ((Integer) Collections.max(destNumberTreeAsMap.keySet())).intValue() + 1;
                    }
                }
                if (destParentTreeNextKey >= 0 && srcStructTree != null && (srcParentTree = srcStructTree.getParentTree()) != null) {
                    srcNumberTreeAsMap = getNumberTreeAsMap(srcParentTree);
                    if (!srcNumberTreeAsMap.isEmpty()) {
                        mergeStructTree = true;
                    }
                }
            }
        }
        Map<COSDictionary, COSDictionary> objMapping = new HashMap<>();
        int pageIndex = 0;
        for (PDPage page3 : srcCatalog.getPages()) {
            PDPage newPage = new PDPage((COSDictionary) cloner.cloneForNewDocument(page3.getCOSObject()));
            if (!mergeStructTree) {
                newPage.getCOSObject().removeItem(COSName.STRUCT_PARENTS);
                for (PDAnnotation ann2 : newPage.getAnnotations()) {
                    ann2.getCOSObject().removeItem(COSName.STRUCT_PARENT);
                }
            }
            newPage.setCropBox(page3.getCropBox());
            newPage.setMediaBox(page3.getMediaBox());
            newPage.setRotation(page3.getRotation());
            PDResources resources = page3.getResources();
            if (resources != null) {
                newPage.setResources(new PDResources((COSDictionary) cloner.cloneForNewDocument(resources)));
            } else {
                newPage.setResources(new PDResources());
            }
            if (mergeStructTree) {
                updateStructParentEntries(newPage, destParentTreeNextKey);
                objMapping.put(page3.getCOSObject(), newPage.getCOSObject());
                List<PDAnnotation> oldAnnots = page3.getAnnotations();
                List<PDAnnotation> newAnnots = newPage.getAnnotations();
                for (int i2 = 0; i2 < oldAnnots.size(); i2++) {
                    objMapping.put(oldAnnots.get(i2).getCOSObject(), newAnnots.get(i2).getCOSObject());
                }
            }
            destination.addPage(newPage);
            if (pageIndex == pageIndexOpenActionDest) {
                PDDestinationOrAction openAction2 = destCatalog.getOpenAction();
                if (openAction2 instanceof PDActionGoTo) {
                    pageDestination = (PDPageDestination) ((PDActionGoTo) openAction2).getDestination();
                } else {
                    pageDestination = (PDPageDestination) openAction2;
                }
                pageDestination.setPage(newPage);
            }
            pageIndex++;
        }
        if (mergeStructTree) {
            updatePageReferences(cloner, srcNumberTreeAsMap, objMapping);
            int maxSrcKey = -1;
            for (Map.Entry<Integer, COSObjectable> entry : srcNumberTreeAsMap.entrySet()) {
                int srcKey = entry.getKey().intValue();
                maxSrcKey = Math.max(srcKey, maxSrcKey);
                destNumberTreeAsMap.put(Integer.valueOf(destParentTreeNextKey + srcKey), cloner.cloneForNewDocument(entry.getValue()));
            }
            PDNumberTreeNode newParentTreeNode = new PDNumberTreeNode(PDParentTreeValue.class);
            newParentTreeNode.setNumbers(destNumberTreeAsMap);
            destStructTree.setParentTree(newParentTreeNode);
            destStructTree.setParentTreeNextKey(destParentTreeNextKey + maxSrcKey + 1);
            mergeKEntries(cloner, srcStructTree, destStructTree);
            mergeRoleMap(srcStructTree, destStructTree);
            mergeIDTree(cloner, srcStructTree, destStructTree);
            mergeMarkInfo(destCatalog, srcCatalog);
            mergeLanguage(destCatalog, srcCatalog);
            mergeViewerPreferences(destCatalog, srcCatalog);
        }
    }

    private void mergeViewerPreferences(PDDocumentCatalog destCatalog, PDDocumentCatalog srcCatalog) {
        PDViewerPreferences srcViewerPreferences = srcCatalog.getViewerPreferences();
        if (srcViewerPreferences == null) {
            return;
        }
        PDViewerPreferences destViewerPreferences = destCatalog.getViewerPreferences();
        if (destViewerPreferences == null) {
            destViewerPreferences = new PDViewerPreferences(new COSDictionary());
            destCatalog.setViewerPreferences(destViewerPreferences);
        }
        mergeInto(srcViewerPreferences.getCOSObject(), destViewerPreferences.getCOSObject(), Collections.emptySet());
        if (srcViewerPreferences.hideToolbar() || destViewerPreferences.hideToolbar()) {
            destViewerPreferences.setHideToolbar(true);
        }
        if (srcViewerPreferences.hideMenubar() || destViewerPreferences.hideMenubar()) {
            destViewerPreferences.setHideMenubar(true);
        }
        if (srcViewerPreferences.hideWindowUI() || destViewerPreferences.hideWindowUI()) {
            destViewerPreferences.setHideWindowUI(true);
        }
        if (srcViewerPreferences.fitWindow() || destViewerPreferences.fitWindow()) {
            destViewerPreferences.setFitWindow(true);
        }
        if (srcViewerPreferences.centerWindow() || destViewerPreferences.centerWindow()) {
            destViewerPreferences.setCenterWindow(true);
        }
        if (srcViewerPreferences.displayDocTitle() || destViewerPreferences.displayDocTitle()) {
            destViewerPreferences.setDisplayDocTitle(true);
        }
    }

    private void mergeLanguage(PDDocumentCatalog destCatalog, PDDocumentCatalog srcCatalog) {
        String srcLanguage;
        if (destCatalog.getLanguage() == null && (srcLanguage = srcCatalog.getLanguage()) != null) {
            destCatalog.setLanguage(srcLanguage);
        }
    }

    private void mergeMarkInfo(PDDocumentCatalog destCatalog, PDDocumentCatalog srcCatalog) {
        PDMarkInfo destMark = destCatalog.getMarkInfo();
        PDMarkInfo srcMark = srcCatalog.getMarkInfo();
        if (destMark == null) {
            destMark = new PDMarkInfo();
        }
        if (srcMark == null) {
            srcMark = new PDMarkInfo();
        }
        destMark.setMarked(true);
        destMark.setSuspect(srcMark.isSuspect() || destMark.isSuspect());
        destMark.setSuspect(srcMark.usesUserProperties() || destMark.usesUserProperties());
        destCatalog.setMarkInfo(destMark);
    }

    private void mergeKEntries(PDFCloneUtility cloner, PDStructureTreeRoot srcStructTree, PDStructureTreeRoot destStructTree) throws IOException {
        COSArray kLevelOneArray;
        COSBase srcKEntry = srcStructTree.getK();
        COSArray srcKArray = new COSArray();
        COSBase clonedSrcKEntry = cloner.cloneForNewDocument(srcKEntry);
        if (clonedSrcKEntry instanceof COSArray) {
            srcKArray.addAll((COSArray) clonedSrcKEntry);
        } else if (clonedSrcKEntry instanceof COSDictionary) {
            srcKArray.add(clonedSrcKEntry);
        }
        if (srcKArray.size() == 0) {
            return;
        }
        COSArray dstKArray = new COSArray();
        COSBase dstKEntry = destStructTree.getK();
        if (dstKEntry instanceof COSArray) {
            dstKArray.addAll((COSArray) dstKEntry);
        } else if (dstKEntry instanceof COSDictionary) {
            dstKArray.add(dstKEntry);
        }
        if (dstKArray.size() == 1 && (dstKArray.getObject(0) instanceof COSDictionary)) {
            COSDictionary topKDict = (COSDictionary) dstKArray.getObject(0);
            if (COSName.DOCUMENT.equals(topKDict.getCOSName(COSName.S)) && (kLevelOneArray = topKDict.getCOSArray(COSName.K)) != null) {
                boolean onlyDocuments = hasOnlyDocumentsOrParts(kLevelOneArray);
                if (onlyDocuments) {
                    kLevelOneArray.addAll(srcKArray);
                    updateParentEntry(kLevelOneArray, topKDict, COSName.PART);
                    return;
                }
            }
        }
        if (dstKArray.size() == 0) {
            updateParentEntry(srcKArray, destStructTree.getCOSObject(), null);
            destStructTree.setK(srcKArray);
            return;
        }
        dstKArray.addAll(srcKArray);
        COSDictionary kLevelZeroDict = new COSDictionary();
        COSName newStructureType = hasOnlyDocumentsOrParts(dstKArray) ? COSName.PART : null;
        updateParentEntry(dstKArray, kLevelZeroDict, newStructureType);
        kLevelZeroDict.setItem(COSName.K, (COSBase) dstKArray);
        kLevelZeroDict.setItem(COSName.P, destStructTree);
        kLevelZeroDict.setItem(COSName.S, (COSBase) COSName.DOCUMENT);
        destStructTree.setK(kLevelZeroDict);
    }

    private boolean hasOnlyDocumentsOrParts(COSArray kLevelOneArray) {
        for (int i = 0; i < kLevelOneArray.size(); i++) {
            COSBase base = kLevelOneArray.getObject(i);
            if (!(base instanceof COSDictionary)) {
                return false;
            }
            COSDictionary dict = (COSDictionary) base;
            COSName sEntry = dict.getCOSName(COSName.S);
            if (!COSName.DOCUMENT.equals(sEntry) && !COSName.PART.equals(sEntry)) {
                return false;
            }
        }
        return true;
    }

    private void updateParentEntry(COSArray kArray, COSDictionary newParent, COSName newStructureType) {
        for (int i = 0; i < kArray.size(); i++) {
            COSBase subEntry = kArray.getObject(i);
            if (subEntry instanceof COSDictionary) {
                COSDictionary dictEntry = (COSDictionary) subEntry;
                dictEntry.setItem(COSName.P, (COSBase) newParent);
                if (newStructureType != null) {
                    dictEntry.setItem(COSName.S, (COSBase) newStructureType);
                }
            }
        }
    }

    private void mergeIDTree(PDFCloneUtility cloner, PDStructureTreeRoot srcStructTree, PDStructureTreeRoot destStructTree) throws IOException {
        PDNameTreeNode<PDStructureElement> srcIDTree = srcStructTree.getIDTree();
        if (srcIDTree == null) {
            return;
        }
        PDNameTreeNode<PDStructureElement> destIDTree = destStructTree.getIDTree();
        if (destIDTree == null) {
            destIDTree = new PDStructureElementNameTreeNode();
        }
        Map<String, PDStructureElement> srcNames = getIDTreeAsMap(srcIDTree);
        Map<String, PDStructureElement> destNames = getIDTreeAsMap(destIDTree);
        for (Map.Entry<String, PDStructureElement> entry : srcNames.entrySet()) {
            if (destNames.containsKey(entry.getKey())) {
                LOG.warn("key " + entry.getKey() + " already exists in destination IDTree");
            } else {
                destNames.put(entry.getKey(), new PDStructureElement((COSDictionary) cloner.cloneForNewDocument(entry.getValue().getCOSObject())));
            }
        }
        PDNameTreeNode<PDStructureElement> destIDTree2 = new PDStructureElementNameTreeNode();
        destIDTree2.setNames(destNames);
        destStructTree.setIDTree(destIDTree2);
    }

    static Map<String, PDStructureElement> getIDTreeAsMap(PDNameTreeNode<PDStructureElement> idTree) throws IOException {
        Map<String, PDStructureElement> names;
        Map<String, T> names2 = idTree.getNames();
        if (names2 == 0) {
            names = new LinkedHashMap<>();
        } else {
            names = new LinkedHashMap<>(names2);
        }
        List<PDNameTreeNode<T>> kids = idTree.getKids();
        if (kids != 0) {
            Iterator it = kids.iterator();
            while (it.hasNext()) {
                PDNameTreeNode<PDStructureElement> kid = (PDNameTreeNode) it.next();
                names.putAll(getIDTreeAsMap(kid));
            }
        }
        return names;
    }

    static Map<Integer, COSObjectable> getNumberTreeAsMap(PDNumberTreeNode tree) throws IOException {
        Map<Integer, COSObjectable> numbers;
        Map<Integer, COSObjectable> numbers2 = tree.getNumbers();
        if (numbers2 == null) {
            numbers = new LinkedHashMap<>();
        } else {
            numbers = new LinkedHashMap<>(numbers2);
        }
        List<PDNumberTreeNode> kids = tree.getKids();
        if (kids != null) {
            for (PDNumberTreeNode kid : kids) {
                numbers.putAll(getNumberTreeAsMap(kid));
            }
        }
        return numbers;
    }

    private void mergeRoleMap(PDStructureTreeRoot srcStructTree, PDStructureTreeRoot destStructTree) {
        COSDictionary srcDict = srcStructTree.getCOSObject().getCOSDictionary(COSName.ROLE_MAP);
        COSDictionary destDict = destStructTree.getCOSObject().getCOSDictionary(COSName.ROLE_MAP);
        if (srcDict == null) {
            return;
        }
        if (destDict == null) {
            destStructTree.getCOSObject().setItem(COSName.ROLE_MAP, (COSBase) srcDict);
            return;
        }
        for (Map.Entry<COSName, COSBase> entry : srcDict.entrySet()) {
            COSBase destValue = destDict.getDictionaryObject(entry.getKey());
            if (destValue == null || !destValue.equals(entry.getValue())) {
                if (destDict.containsKey(entry.getKey())) {
                    LOG.warn("key " + entry.getKey() + " already exists in destination RoleMap");
                } else {
                    destDict.setItem(entry.getKey(), entry.getValue());
                }
            }
        }
    }

    private void mergeOutputIntents(PDFCloneUtility cloner, PDDocumentCatalog srcCatalog, PDDocumentCatalog destCatalog) throws IOException {
        List<PDOutputIntent> srcOutputIntents = srcCatalog.getOutputIntents();
        List<PDOutputIntent> dstOutputIntents = destCatalog.getOutputIntents();
        for (PDOutputIntent srcOI : srcOutputIntents) {
            String srcOCI = srcOI.getOutputConditionIdentifier();
            if (srcOCI != null && !"Custom".equals(srcOCI)) {
                boolean skip = false;
                for (PDOutputIntent dstOI : dstOutputIntents) {
                    if (dstOI.getOutputConditionIdentifier().equals(srcOCI)) {
                        skip = true;
                        break;
                    }
                }
                if (skip) {
                }
            }
            destCatalog.addOutputIntent(new PDOutputIntent((COSDictionary) cloner.cloneForNewDocument(srcOI)));
            dstOutputIntents.add(srcOI);
        }
    }

    private void mergeAcroForm(PDFCloneUtility cloner, PDDocumentCatalog destCatalog, PDDocumentCatalog srcCatalog) throws IOException {
        try {
            PDAcroForm destAcroForm = destCatalog.getAcroForm();
            PDAcroForm srcAcroForm = srcCatalog.getAcroForm();
            if (destAcroForm == null && srcAcroForm != null) {
                destCatalog.getCOSObject().setItem(COSName.ACRO_FORM, cloner.cloneForNewDocument(srcAcroForm.getCOSObject()));
            } else if (srcAcroForm != null) {
                if (this.acroFormMergeMode == AcroFormMergeMode.PDFBOX_LEGACY_MODE) {
                    acroFormLegacyMode(cloner, destAcroForm, srcAcroForm);
                } else if (this.acroFormMergeMode == AcroFormMergeMode.JOIN_FORM_FIELDS_MODE) {
                    acroFormJoinFieldsMode(cloner, destAcroForm, srcAcroForm);
                }
            }
        } catch (IOException e) {
            if (!this.ignoreAcroFormErrors) {
                throw new IOException(e);
            }
        }
    }

    private void acroFormJoinFieldsMode(PDFCloneUtility cloner, PDAcroForm destAcroForm, PDAcroForm srcAcroForm) throws IOException {
        COSArray destFields;
        List<PDField> srcFields = srcAcroForm.getFields();
        if (srcFields != null && !srcFields.isEmpty()) {
            COSBase base = destAcroForm.getCOSObject().getItem(COSName.FIELDS);
            if (base instanceof COSArray) {
                destFields = (COSArray) base;
            } else {
                destFields = new COSArray();
            }
            for (PDField srcField : srcAcroForm.getFieldTree()) {
                PDField destinationField = destAcroForm.getField(srcField.getFullyQualifiedName());
                if (destinationField == null) {
                    COSDictionary importedField = (COSDictionary) cloner.cloneForNewDocument(srcField.getCOSObject());
                    destFields.add((COSBase) importedField);
                } else {
                    mergeFields(cloner, destinationField, srcField);
                }
            }
            destAcroForm.getCOSObject().setItem(COSName.FIELDS, (COSBase) destFields);
        }
    }

    private void mergeFields(PDFCloneUtility cloner, PDField destField, PDField srcField) {
        if ((destField instanceof PDNonTerminalField) && (srcField instanceof PDNonTerminalField)) {
            LOG.info("Skipping non terminal field " + srcField.getFullyQualifiedName());
            return;
        }
        if (destField.getFieldType() == "Tx" && destField.getFieldType() == "Tx") {
            if (destField.getCOSObject().containsKey(COSName.KIDS)) {
                COSArray widgets = destField.getCOSObject().getCOSArray(COSName.KIDS);
                for (PDAnnotationWidget srcWidget : srcField.getWidgets()) {
                    try {
                        widgets.add(cloner.cloneForNewDocument(srcWidget.getCOSObject()));
                    } catch (IOException e) {
                        LOG.warn("Unable to clone widget for source field " + srcField.getFullyQualifiedName());
                    }
                }
                return;
            }
            COSArray widgets2 = new COSArray();
            try {
                COSDictionary widgetAsCOS = (COSDictionary) cloner.cloneForNewDocument(destField.getWidgets().get(0));
                cleanupWidgetCOSDictionary(widgetAsCOS, true);
                widgetAsCOS.setItem(COSName.PARENT, destField);
                widgets2.add((COSBase) widgetAsCOS);
                for (PDAnnotationWidget srcWidget2 : srcField.getWidgets()) {
                    try {
                        COSDictionary widgetAsCOS2 = (COSDictionary) cloner.cloneForNewDocument(srcWidget2.getCOSObject());
                        cleanupWidgetCOSDictionary(widgetAsCOS2, false);
                        widgetAsCOS2.setItem(COSName.PARENT, destField);
                        widgets2.add((COSBase) widgetAsCOS2);
                    } catch (IOException e2) {
                        LOG.warn("Unable to clone widget for source field " + srcField.getFullyQualifiedName());
                    }
                }
                destField.getCOSObject().setItem(COSName.KIDS, (COSBase) widgets2);
                cleanupFieldCOSDictionary(destField.getCOSObject());
                return;
            } catch (IOException e3) {
                LOG.warn("Unable to clone widget for destination field " + destField.getFullyQualifiedName());
                return;
            }
        }
        LOG.info("Only merging two text fields is currently supported");
        LOG.info("Skipping merging of " + srcField.getFullyQualifiedName() + " into " + destField.getFullyQualifiedName());
    }

    private void cleanupFieldCOSDictionary(COSDictionary fieldCos) {
        fieldCos.removeItem(COSName.F);
        fieldCos.removeItem(COSName.MK);
        fieldCos.removeItem(COSName.P);
        fieldCos.removeItem(COSName.RECT);
        fieldCos.removeItem(COSName.SUBTYPE);
        fieldCos.removeItem(COSName.TYPE);
    }

    private void cleanupWidgetCOSDictionary(COSDictionary widgetCos, boolean removeDAEntry) {
        if (removeDAEntry) {
            widgetCos.removeItem(COSName.DA);
        }
        widgetCos.removeItem(COSName.FT);
        widgetCos.removeItem(COSName.T);
        widgetCos.removeItem(COSName.V);
    }

    private void acroFormLegacyMode(PDFCloneUtility cloner, PDAcroForm destAcroForm, PDAcroForm srcAcroForm) throws IOException {
        COSArray destFields;
        List<PDField> srcFields = srcAcroForm.getFields();
        if (srcFields != null && !srcFields.isEmpty()) {
            int prefixLength = "dummyFieldName".length();
            for (PDField destField : destAcroForm.getFieldTree()) {
                String fieldName = destField.getPartialName();
                if (fieldName.startsWith("dummyFieldName")) {
                    String suffix = fieldName.substring(prefixLength);
                    if (suffix.matches("\\d+")) {
                        this.nextFieldNum = Math.max(this.nextFieldNum, Integer.parseInt(suffix) + 1);
                    }
                }
            }
            COSBase base = destAcroForm.getCOSObject().getItem(COSName.FIELDS);
            if (base instanceof COSArray) {
                destFields = (COSArray) base;
            } else {
                destFields = new COSArray();
            }
            for (PDField srcField : srcAcroForm.getFields()) {
                COSDictionary dstField = (COSDictionary) cloner.cloneForNewDocument(srcField.getCOSObject());
                if (destAcroForm.getField(srcField.getFullyQualifiedName()) != null) {
                    COSName cOSName = COSName.T;
                    StringBuilder sbAppend = new StringBuilder().append("dummyFieldName");
                    int i = this.nextFieldNum;
                    this.nextFieldNum = i + 1;
                    dstField.setString(cOSName, sbAppend.append(i).toString());
                }
                destFields.add((COSBase) dstField);
            }
            destAcroForm.getCOSObject().setItem(COSName.FIELDS, (COSBase) destFields);
        }
    }

    public boolean isIgnoreAcroFormErrors() {
        return this.ignoreAcroFormErrors;
    }

    public void setIgnoreAcroFormErrors(boolean ignoreAcroFormErrorsValue) {
        this.ignoreAcroFormErrors = ignoreAcroFormErrorsValue;
    }

    private void updatePageReferences(PDFCloneUtility cloner, Map<Integer, COSObjectable> numberTreeAsMap, Map<COSDictionary, COSDictionary> objMapping) throws IOException {
        for (COSObjectable obj : numberTreeAsMap.values()) {
            if (obj != null) {
                PDParentTreeValue val = (PDParentTreeValue) obj;
                COSBase base = val.getCOSObject();
                if (base instanceof COSArray) {
                    updatePageReferences(cloner, (COSArray) base, objMapping);
                } else {
                    updatePageReferences(cloner, (COSDictionary) base, objMapping);
                }
            }
        }
    }

    private void updatePageReferences(PDFCloneUtility cloner, COSDictionary parentTreeEntry, Map<COSDictionary, COSDictionary> objMapping) throws IOException {
        COSDictionary pageDict = parentTreeEntry.getCOSDictionary(COSName.PG);
        if (objMapping.containsKey(pageDict)) {
            parentTreeEntry.setItem(COSName.PG, (COSBase) objMapping.get(pageDict));
        }
        COSBase obj = parentTreeEntry.getDictionaryObject(COSName.OBJ);
        if (obj instanceof COSDictionary) {
            COSDictionary objDict = (COSDictionary) obj;
            if (objMapping.containsKey(objDict)) {
                parentTreeEntry.setItem(COSName.OBJ, (COSBase) objMapping.get(objDict));
            } else {
                COSBase item = parentTreeEntry.getItem(COSName.OBJ);
                if (item instanceof COSObject) {
                    LOG.debug("clone potential orphan object in structure tree: " + item + ", Type: " + objDict.getNameAsString(COSName.TYPE) + ", Subtype: " + objDict.getNameAsString(COSName.SUBTYPE) + ", T: " + objDict.getNameAsString(COSName.T));
                } else {
                    LOG.debug("clone potential orphan object in structure tree, Type: " + objDict.getNameAsString(COSName.TYPE) + ", Subtype: " + objDict.getNameAsString(COSName.SUBTYPE) + ", T: " + objDict.getNameAsString(COSName.T));
                }
                parentTreeEntry.setItem(COSName.OBJ, cloner.cloneForNewDocument(obj));
            }
        }
        COSBase kSubEntry = parentTreeEntry.getDictionaryObject(COSName.K);
        if (kSubEntry instanceof COSArray) {
            updatePageReferences(cloner, (COSArray) kSubEntry, objMapping);
        } else if (kSubEntry instanceof COSDictionary) {
            updatePageReferences(cloner, (COSDictionary) kSubEntry, objMapping);
        }
    }

    private void updatePageReferences(PDFCloneUtility cloner, COSArray parentTreeEntry, Map<COSDictionary, COSDictionary> objMapping) throws IOException {
        for (int i = 0; i < parentTreeEntry.size(); i++) {
            COSBase subEntry = parentTreeEntry.getObject(i);
            if (subEntry instanceof COSArray) {
                updatePageReferences(cloner, (COSArray) subEntry, objMapping);
            } else if (subEntry instanceof COSDictionary) {
                updatePageReferences(cloner, (COSDictionary) subEntry, objMapping);
            }
        }
    }

    private void updateStructParentEntries(PDPage page, int structParentOffset) throws IOException {
        int structParents = page.getStructParents();
        if (structParents >= 0) {
            page.setStructParents(structParents + structParentOffset);
        }
        List<PDAnnotation> annots = page.getAnnotations();
        List<PDAnnotation> newannots = new ArrayList<>(annots.size());
        for (PDAnnotation annot : annots) {
            int structParent = annot.getStructParent();
            if (structParent >= 0) {
                annot.setStructParent(structParent + structParentOffset);
            }
            newannots.add(annot);
        }
        page.setAnnotations(newannots);
    }

    private boolean isDynamicXfa(PDAcroForm acroForm) {
        return acroForm != null && acroForm.xfaIsDynamic();
    }

    private void mergeInto(COSDictionary src, COSDictionary dst, Set<COSName> exclude) {
        for (Map.Entry<COSName, COSBase> entry : src.entrySet()) {
            if (!exclude.contains(entry.getKey()) && !dst.containsKey(entry.getKey())) {
                dst.setItem(entry.getKey(), entry.getValue());
            }
        }
    }
}
