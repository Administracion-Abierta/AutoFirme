package org.apache.pdfbox.multipdf;

import com.twelvemonkeys.imageio.metadata.tiff.TIFF;
import java.awt.geom.AffineTransform;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/multipdf/Overlay.class */
public class Overlay implements Closeable {
    private LayoutPage defaultOverlayPage;
    private LayoutPage firstPageOverlayPage;
    private LayoutPage lastPageOverlayPage;
    private LayoutPage oddPageOverlayPage;
    private LayoutPage evenPageOverlayPage;
    private final Set<PDDocument> openDocuments = new HashSet();
    private Map<Integer, LayoutPage> specificPageOverlayPage = new HashMap();
    private Position position = Position.BACKGROUND;
    private String inputFileName = null;
    private PDDocument inputPDFDocument = null;
    private String defaultOverlayFilename = null;
    private PDDocument defaultOverlay = null;
    private String firstPageOverlayFilename = null;
    private PDDocument firstPageOverlay = null;
    private String lastPageOverlayFilename = null;
    private PDDocument lastPageOverlay = null;
    private String allPagesOverlayFilename = null;
    private PDDocument allPagesOverlay = null;
    private String oddPageOverlayFilename = null;
    private PDDocument oddPageOverlay = null;
    private String evenPageOverlayFilename = null;
    private PDDocument evenPageOverlay = null;
    private int numberOfOverlayPages = 0;
    private boolean useAllOverlayPages = false;

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/multipdf/Overlay$Position.class */
    public enum Position {
        FOREGROUND,
        BACKGROUND
    }

    public PDDocument overlay(Map<Integer, String> specificPageOverlayFile) throws IOException {
        Map<String, PDDocument> loadedDocuments = new HashMap<>();
        Map<PDDocument, LayoutPage> layouts = new HashMap<>();
        loadPDFs();
        for (Map.Entry<Integer, String> e : specificPageOverlayFile.entrySet()) {
            PDDocument doc = loadedDocuments.get(e.getValue());
            if (doc == null) {
                doc = loadPDF(e.getValue());
                loadedDocuments.put(e.getValue(), doc);
                layouts.put(doc, getLayoutPage(doc));
                this.openDocuments.add(doc);
            }
            this.specificPageOverlayPage.put(e.getKey(), layouts.get(doc));
        }
        processPages(this.inputPDFDocument);
        return this.inputPDFDocument;
    }

    public PDDocument overlayDocuments(Map<Integer, PDDocument> specificPageOverlayDocuments) throws IOException {
        loadPDFs();
        for (Map.Entry<Integer, PDDocument> e : specificPageOverlayDocuments.entrySet()) {
            PDDocument doc = e.getValue();
            if (doc != null) {
                this.specificPageOverlayPage.put(e.getKey(), getLayoutPage(doc));
            }
        }
        processPages(this.inputPDFDocument);
        return this.inputPDFDocument;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.defaultOverlay != null) {
            this.defaultOverlay.close();
        }
        if (this.firstPageOverlay != null) {
            this.firstPageOverlay.close();
        }
        if (this.lastPageOverlay != null) {
            this.lastPageOverlay.close();
        }
        if (this.allPagesOverlay != null) {
            this.allPagesOverlay.close();
        }
        if (this.oddPageOverlay != null) {
            this.oddPageOverlay.close();
        }
        if (this.evenPageOverlay != null) {
            this.evenPageOverlay.close();
        }
        for (PDDocument doc : this.openDocuments) {
            doc.close();
        }
        this.openDocuments.clear();
        this.specificPageOverlayPage.clear();
    }

    private void loadPDFs() throws IOException {
        if (this.inputFileName != null) {
            this.inputPDFDocument = loadPDF(this.inputFileName);
        }
        if (this.defaultOverlayFilename != null) {
            this.defaultOverlay = loadPDF(this.defaultOverlayFilename);
        }
        if (this.defaultOverlay != null) {
            this.defaultOverlayPage = getLayoutPage(this.defaultOverlay);
        }
        if (this.firstPageOverlayFilename != null) {
            this.firstPageOverlay = loadPDF(this.firstPageOverlayFilename);
        }
        if (this.firstPageOverlay != null) {
            this.firstPageOverlayPage = getLayoutPage(this.firstPageOverlay);
        }
        if (this.lastPageOverlayFilename != null) {
            this.lastPageOverlay = loadPDF(this.lastPageOverlayFilename);
        }
        if (this.lastPageOverlay != null) {
            this.lastPageOverlayPage = getLayoutPage(this.lastPageOverlay);
        }
        if (this.oddPageOverlayFilename != null) {
            this.oddPageOverlay = loadPDF(this.oddPageOverlayFilename);
        }
        if (this.oddPageOverlay != null) {
            this.oddPageOverlayPage = getLayoutPage(this.oddPageOverlay);
        }
        if (this.evenPageOverlayFilename != null) {
            this.evenPageOverlay = loadPDF(this.evenPageOverlayFilename);
        }
        if (this.evenPageOverlay != null) {
            this.evenPageOverlayPage = getLayoutPage(this.evenPageOverlay);
        }
        if (this.allPagesOverlayFilename != null) {
            this.allPagesOverlay = loadPDF(this.allPagesOverlayFilename);
        }
        if (this.allPagesOverlay != null) {
            this.specificPageOverlayPage = getLayoutPages(this.allPagesOverlay);
            this.useAllOverlayPages = true;
            this.numberOfOverlayPages = this.specificPageOverlayPage.size();
        }
    }

    private PDDocument loadPDF(String pdfName) throws IOException {
        return PDDocument.load(new File(pdfName));
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/multipdf/Overlay$LayoutPage.class */
    private static final class LayoutPage {
        private final PDRectangle overlayMediaBox;
        private final COSStream overlayContentStream;
        private final COSDictionary overlayResources;
        private final short overlayRotation;

        private LayoutPage(PDRectangle mediaBox, COSStream contentStream, COSDictionary resources, short rotation) {
            this.overlayMediaBox = mediaBox;
            this.overlayContentStream = contentStream;
            this.overlayResources = resources;
            this.overlayRotation = rotation;
        }
    }

    private LayoutPage getLayoutPage(PDDocument doc) throws IOException {
        return createLayoutPage(doc.getPage(0));
    }

    private LayoutPage createLayoutPage(PDPage page) throws IOException {
        COSBase contents = page.getCOSObject().getDictionaryObject(COSName.CONTENTS);
        PDResources resources = page.getResources();
        if (resources == null) {
            resources = new PDResources();
        }
        return new LayoutPage(page.getMediaBox(), createCombinedContentStream(contents), resources.getCOSObject(), (short) page.getRotation());
    }

    private Map<Integer, LayoutPage> getLayoutPages(PDDocument doc) throws IOException {
        int i = 0;
        Map<Integer, LayoutPage> layoutPages = new HashMap<>();
        for (PDPage page : doc.getPages()) {
            layoutPages.put(Integer.valueOf(i), createLayoutPage(page));
            i++;
        }
        return layoutPages;
    }

    private COSStream createCombinedContentStream(COSBase contents) throws IOException {
        List<COSStream> contentStreams = createContentStreamList(contents);
        COSStream concatStream = this.inputPDFDocument.getDocument().createCOSStream();
        OutputStream out = concatStream.createOutputStream(COSName.FLATE_DECODE);
        for (COSStream contentStream : contentStreams) {
            InputStream in = contentStream.createInputStream();
            IOUtils.copy(in, out);
            out.flush();
            in.close();
        }
        out.close();
        return concatStream;
    }

    private List<COSStream> createContentStreamList(COSBase contents) throws IOException {
        List<COSStream> contentStreams = new ArrayList<>();
        if (contents == null) {
            return contentStreams;
        }
        if (contents instanceof COSStream) {
            contentStreams.add((COSStream) contents);
        } else if (contents instanceof COSArray) {
            for (COSBase item : (COSArray) contents) {
                contentStreams.addAll(createContentStreamList(item));
            }
        } else if (contents instanceof COSObject) {
            contentStreams.addAll(createContentStreamList(((COSObject) contents).getObject()));
        } else {
            throw new IOException("Unknown content type: " + contents.getClass().getName());
        }
        return contentStreams;
    }

    private void processPages(PDDocument document) throws IOException {
        int pageCounter = 0;
        PDPageTree pageTree = document.getPages();
        int numberOfPages = pageTree.getCount();
        for (PDPage page : pageTree) {
            pageCounter++;
            LayoutPage layoutPage = getLayoutPage(pageCounter, numberOfPages);
            if (layoutPage != null) {
                COSDictionary pageDictionary = page.getCOSObject();
                COSBase originalContent = pageDictionary.getDictionaryObject(COSName.CONTENTS);
                COSArray newContentArray = new COSArray();
                switch (this.position) {
                    case FOREGROUND:
                        newContentArray.add((COSBase) createStream("q\n"));
                        addOriginalContent(originalContent, newContentArray);
                        newContentArray.add((COSBase) createStream("Q\n"));
                        overlayPage(page, layoutPage, newContentArray);
                        break;
                    case BACKGROUND:
                        overlayPage(page, layoutPage, newContentArray);
                        addOriginalContent(originalContent, newContentArray);
                        break;
                    default:
                        throw new IOException("Unknown type of position:" + this.position);
                }
                pageDictionary.setItem(COSName.CONTENTS, (COSBase) newContentArray);
            }
        }
    }

    private void addOriginalContent(COSBase contents, COSArray contentArray) throws IOException {
        if (contents == null) {
            return;
        }
        if (contents instanceof COSStream) {
            contentArray.add(contents);
        } else {
            if (contents instanceof COSArray) {
                contentArray.addAll((COSArray) contents);
                return;
            }
            throw new IOException("Unknown content type: " + contents.getClass().getName());
        }
    }

    private void overlayPage(PDPage page, LayoutPage layoutPage, COSArray array) throws IOException {
        PDResources resources = page.getResources();
        if (resources == null) {
            PDResources resources2 = new PDResources();
            page.setResources(resources2);
        }
        COSName xObjectId = createOverlayXObject(page, layoutPage);
        array.add((COSBase) createOverlayStream(page, layoutPage, xObjectId));
    }

    private LayoutPage getLayoutPage(int pageNumber, int numberOfPages) {
        LayoutPage layoutPage = null;
        if (!this.useAllOverlayPages && this.specificPageOverlayPage.containsKey(Integer.valueOf(pageNumber))) {
            layoutPage = this.specificPageOverlayPage.get(Integer.valueOf(pageNumber));
        } else if (pageNumber == 1 && this.firstPageOverlayPage != null) {
            layoutPage = this.firstPageOverlayPage;
        } else if (pageNumber == numberOfPages && this.lastPageOverlayPage != null) {
            layoutPage = this.lastPageOverlayPage;
        } else if (pageNumber % 2 == 1 && this.oddPageOverlayPage != null) {
            layoutPage = this.oddPageOverlayPage;
        } else if (pageNumber % 2 == 0 && this.evenPageOverlayPage != null) {
            layoutPage = this.evenPageOverlayPage;
        } else if (this.defaultOverlayPage != null) {
            layoutPage = this.defaultOverlayPage;
        } else if (this.useAllOverlayPages) {
            int usePageNum = (pageNumber - 1) % this.numberOfOverlayPages;
            layoutPage = this.specificPageOverlayPage.get(Integer.valueOf(usePageNum));
        }
        return layoutPage;
    }

    private COSName createOverlayXObject(PDPage page, LayoutPage layoutPage) {
        PDFormXObject xobjForm = new PDFormXObject(layoutPage.overlayContentStream);
        xobjForm.setResources(new PDResources(layoutPage.overlayResources));
        xobjForm.setFormType(1);
        xobjForm.setBBox(layoutPage.overlayMediaBox.createRetranslatedRectangle());
        AffineTransform at = new AffineTransform();
        switch (layoutPage.overlayRotation) {
            case 90:
                at.translate(0.0d, layoutPage.overlayMediaBox.getWidth());
                at.rotate(Math.toRadians(-90.0d));
                break;
            case 180:
                at.translate(layoutPage.overlayMediaBox.getWidth(), layoutPage.overlayMediaBox.getHeight());
                at.rotate(Math.toRadians(-180.0d));
                break;
            case TIFF.TAG_IMAGE_DESCRIPTION /* 270 */:
                at.translate(layoutPage.overlayMediaBox.getHeight(), 0.0d);
                at.rotate(Math.toRadians(-270.0d));
                break;
        }
        xobjForm.setMatrix(at);
        PDResources resources = page.getResources();
        return resources.add(xobjForm, "OL");
    }

    private COSStream createOverlayStream(PDPage page, LayoutPage layoutPage, COSName xObjectId) throws IOException {
        StringBuilder overlayStream = new StringBuilder();
        overlayStream.append("q\nq\n");
        PDRectangle overlayMediaBox = new PDRectangle(layoutPage.overlayMediaBox.getCOSArray());
        if (layoutPage.overlayRotation == 90 || layoutPage.overlayRotation == 270) {
            overlayMediaBox.setLowerLeftX(layoutPage.overlayMediaBox.getLowerLeftY());
            overlayMediaBox.setLowerLeftY(layoutPage.overlayMediaBox.getLowerLeftX());
            overlayMediaBox.setUpperRightX(layoutPage.overlayMediaBox.getUpperRightY());
            overlayMediaBox.setUpperRightY(layoutPage.overlayMediaBox.getUpperRightX());
        }
        AffineTransform at = calculateAffineTransform(page, overlayMediaBox);
        double[] flatmatrix = new double[6];
        at.getMatrix(flatmatrix);
        for (double v : flatmatrix) {
            overlayStream.append(float2String((float) v));
            overlayStream.append(" ");
        }
        overlayStream.append(" cm\n");
        overlayStream.append(" /");
        overlayStream.append(xObjectId.getName());
        overlayStream.append(" Do Q\nQ\n");
        return createStream(overlayStream.toString());
    }

    protected AffineTransform calculateAffineTransform(PDPage page, PDRectangle overlayMediaBox) {
        AffineTransform at = new AffineTransform();
        PDRectangle pageMediaBox = page.getMediaBox();
        float hShift = (pageMediaBox.getWidth() - overlayMediaBox.getWidth()) / 2.0f;
        float vShift = (pageMediaBox.getHeight() - overlayMediaBox.getHeight()) / 2.0f;
        at.translate(hShift, vShift);
        return at;
    }

    private String float2String(float floatValue) {
        BigDecimal value = new BigDecimal(String.valueOf(floatValue));
        String stringValue = value.toPlainString();
        if (stringValue.indexOf(46) > -1 && !stringValue.endsWith(".0")) {
            while (stringValue.endsWith("0") && !stringValue.endsWith(".0")) {
                stringValue = stringValue.substring(0, stringValue.length() - 1);
            }
        }
        return stringValue;
    }

    private COSStream createStream(String content) throws IOException {
        COSStream stream = this.inputPDFDocument.getDocument().createCOSStream();
        OutputStream out = stream.createOutputStream(content.length() > 20 ? COSName.FLATE_DECODE : null);
        out.write(content.getBytes("ISO-8859-1"));
        out.close();
        return stream;
    }

    public void setOverlayPosition(Position overlayPosition) {
        this.position = overlayPosition;
    }

    public void setInputFile(String inputFile) {
        this.inputFileName = inputFile;
    }

    public void setInputPDF(PDDocument inputPDF) {
        this.inputPDFDocument = inputPDF;
    }

    public String getInputFile() {
        return this.inputFileName;
    }

    public void setDefaultOverlayFile(String defaultOverlayFile) {
        this.defaultOverlayFilename = defaultOverlayFile;
    }

    public void setDefaultOverlayPDF(PDDocument defaultOverlayPDF) {
        this.defaultOverlay = defaultOverlayPDF;
    }

    public String getDefaultOverlayFile() {
        return this.defaultOverlayFilename;
    }

    public void setFirstPageOverlayFile(String firstPageOverlayFile) {
        this.firstPageOverlayFilename = firstPageOverlayFile;
    }

    public void setFirstPageOverlayPDF(PDDocument firstPageOverlayPDF) {
        this.firstPageOverlay = firstPageOverlayPDF;
    }

    public void setLastPageOverlayFile(String lastPageOverlayFile) {
        this.lastPageOverlayFilename = lastPageOverlayFile;
    }

    public void setLastPageOverlayPDF(PDDocument lastPageOverlayPDF) {
        this.lastPageOverlay = lastPageOverlayPDF;
    }

    public void setAllPagesOverlayFile(String allPagesOverlayFile) {
        this.allPagesOverlayFilename = allPagesOverlayFile;
    }

    public void setAllPagesOverlayPDF(PDDocument allPagesOverlayPDF) {
        this.allPagesOverlay = allPagesOverlayPDF;
    }

    public void setOddPageOverlayFile(String oddPageOverlayFile) {
        this.oddPageOverlayFilename = oddPageOverlayFile;
    }

    public void setOddPageOverlayPDF(PDDocument oddPageOverlayPDF) {
        this.oddPageOverlay = oddPageOverlayPDF;
    }

    public void setEvenPageOverlayFile(String evenPageOverlayFile) {
        this.evenPageOverlayFilename = evenPageOverlayFile;
    }

    public void setEvenPageOverlayPDF(PDDocument evenPageOverlayPDF) {
        this.evenPageOverlay = evenPageOverlayPDF;
    }
}
