package org.apache.pdfbox.pdmodel;

import com.aowagie.text.pdf.ColumnText;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.contentstream.PDContentStream;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.interactive.action.PDPageAdditionalActions;
import org.apache.pdfbox.pdmodel.interactive.annotation.AnnotationFilter;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.measurement.PDViewportDictionary;
import org.apache.pdfbox.pdmodel.interactive.pagenavigation.PDThreadBead;
import org.apache.pdfbox.pdmodel.interactive.pagenavigation.PDTransition;
import org.apache.pdfbox.util.Matrix;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/PDPage.class */
public class PDPage implements COSObjectable, PDContentStream {
    private static final Log LOG = LogFactory.getLog(PDPage.class);
    private final COSDictionary page;
    private PDResources pageResources;
    private ResourceCache resourceCache;
    private PDRectangle mediaBox;

    public PDPage() {
        this(PDRectangle.LETTER);
    }

    public PDPage(PDRectangle mediaBox) {
        this.page = new COSDictionary();
        this.page.setItem(COSName.TYPE, (COSBase) COSName.PAGE);
        this.page.setItem(COSName.MEDIA_BOX, mediaBox);
    }

    public PDPage(COSDictionary pageDictionary) {
        this.page = pageDictionary;
    }

    PDPage(COSDictionary pageDictionary, ResourceCache resourceCache) {
        this.page = pageDictionary;
        this.resourceCache = resourceCache;
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.page;
    }

    public Iterator<PDStream> getContentStreams() {
        List<PDStream> streams = new ArrayList<>();
        COSBase base = this.page.getDictionaryObject(COSName.CONTENTS);
        if (base instanceof COSStream) {
            streams.add(new PDStream((COSStream) base));
        } else if ((base instanceof COSArray) && ((COSArray) base).size() > 0) {
            COSArray array = (COSArray) base;
            for (int i = 0; i < array.size(); i++) {
                COSStream stream = (COSStream) array.getObject(i);
                streams.add(new PDStream(stream));
            }
        }
        return streams.iterator();
    }

    @Override // org.apache.pdfbox.contentstream.PDContentStream
    public InputStream getContents() throws IOException {
        COSBase base = this.page.getDictionaryObject(COSName.CONTENTS);
        if (base instanceof COSStream) {
            return ((COSStream) base).createInputStream();
        }
        if ((base instanceof COSArray) && ((COSArray) base).size() > 0) {
            COSArray streams = (COSArray) base;
            byte[] delimiter = {10};
            List<InputStream> inputStreams = new ArrayList<>();
            for (int i = 0; i < streams.size(); i++) {
                COSBase strm = streams.getObject(i);
                if (strm instanceof COSStream) {
                    COSStream stream = (COSStream) strm;
                    inputStreams.add(stream.createInputStream());
                    inputStreams.add(new ByteArrayInputStream(delimiter));
                }
            }
            return new SequenceInputStream(Collections.enumeration(inputStreams));
        }
        return new ByteArrayInputStream(new byte[0]);
    }

    public boolean hasContents() {
        COSBase contents = this.page.getDictionaryObject(COSName.CONTENTS);
        if (contents instanceof COSStream) {
            return ((COSStream) contents).size() > 0;
        }
        return (contents instanceof COSArray) && ((COSArray) contents).size() > 0;
    }

    @Override // org.apache.pdfbox.contentstream.PDContentStream
    public PDResources getResources() {
        if (this.pageResources == null) {
            COSBase base = PDPageTree.getInheritableAttribute(this.page, COSName.RESOURCES);
            if (base instanceof COSDictionary) {
                this.pageResources = new PDResources((COSDictionary) base, this.resourceCache);
            }
        }
        return this.pageResources;
    }

    public void setResources(PDResources resources) {
        this.pageResources = resources;
        if (resources != null) {
            this.page.setItem(COSName.RESOURCES, resources);
        } else {
            this.page.removeItem(COSName.RESOURCES);
        }
    }

    public int getStructParents() {
        return this.page.getInt(COSName.STRUCT_PARENTS);
    }

    public void setStructParents(int structParents) {
        this.page.setInt(COSName.STRUCT_PARENTS, structParents);
    }

    @Override // org.apache.pdfbox.contentstream.PDContentStream
    public PDRectangle getBBox() {
        return getCropBox();
    }

    @Override // org.apache.pdfbox.contentstream.PDContentStream
    public Matrix getMatrix() {
        return new Matrix();
    }

    public PDRectangle getMediaBox() {
        if (this.mediaBox == null) {
            COSBase base = PDPageTree.getInheritableAttribute(this.page, COSName.MEDIA_BOX);
            if (base instanceof COSArray) {
                this.mediaBox = new PDRectangle((COSArray) base);
            }
        }
        if (this.mediaBox == null) {
            LOG.debug("Can't find MediaBox, will use U.S. Letter");
            this.mediaBox = PDRectangle.LETTER;
        }
        return this.mediaBox;
    }

    public void setMediaBox(PDRectangle mediaBox) {
        this.mediaBox = mediaBox;
        if (mediaBox == null) {
            this.page.removeItem(COSName.MEDIA_BOX);
        } else {
            this.page.setItem(COSName.MEDIA_BOX, mediaBox);
        }
    }

    public PDRectangle getCropBox() {
        COSBase base = PDPageTree.getInheritableAttribute(this.page, COSName.CROP_BOX);
        if (base instanceof COSArray) {
            return clipToMediaBox(new PDRectangle((COSArray) base));
        }
        return getMediaBox();
    }

    public void setCropBox(PDRectangle cropBox) {
        if (cropBox == null) {
            this.page.removeItem(COSName.CROP_BOX);
        } else {
            this.page.setItem(COSName.CROP_BOX, (COSBase) cropBox.getCOSArray());
        }
    }

    public PDRectangle getBleedBox() {
        COSBase base = this.page.getDictionaryObject(COSName.BLEED_BOX);
        if (base instanceof COSArray) {
            return clipToMediaBox(new PDRectangle((COSArray) base));
        }
        return getCropBox();
    }

    public void setBleedBox(PDRectangle bleedBox) {
        if (bleedBox == null) {
            this.page.removeItem(COSName.BLEED_BOX);
        } else {
            this.page.setItem(COSName.BLEED_BOX, bleedBox);
        }
    }

    public PDRectangle getTrimBox() {
        COSBase base = this.page.getDictionaryObject(COSName.TRIM_BOX);
        if (base instanceof COSArray) {
            return clipToMediaBox(new PDRectangle((COSArray) base));
        }
        return getCropBox();
    }

    public void setTrimBox(PDRectangle trimBox) {
        if (trimBox == null) {
            this.page.removeItem(COSName.TRIM_BOX);
        } else {
            this.page.setItem(COSName.TRIM_BOX, trimBox);
        }
    }

    public PDRectangle getArtBox() {
        COSBase base = this.page.getDictionaryObject(COSName.ART_BOX);
        if (base instanceof COSArray) {
            return clipToMediaBox(new PDRectangle((COSArray) base));
        }
        return getCropBox();
    }

    public void setArtBox(PDRectangle artBox) {
        if (artBox == null) {
            this.page.removeItem(COSName.ART_BOX);
        } else {
            this.page.setItem(COSName.ART_BOX, artBox);
        }
    }

    private PDRectangle clipToMediaBox(PDRectangle box) {
        PDRectangle mediaBox = getMediaBox();
        PDRectangle result = new PDRectangle();
        result.setLowerLeftX(Math.max(mediaBox.getLowerLeftX(), box.getLowerLeftX()));
        result.setLowerLeftY(Math.max(mediaBox.getLowerLeftY(), box.getLowerLeftY()));
        result.setUpperRightX(Math.min(mediaBox.getUpperRightX(), box.getUpperRightX()));
        result.setUpperRightY(Math.min(mediaBox.getUpperRightY(), box.getUpperRightY()));
        return result;
    }

    public int getRotation() {
        COSBase obj = PDPageTree.getInheritableAttribute(this.page, COSName.ROTATE);
        if (obj instanceof COSNumber) {
            int rotationAngle = ((COSNumber) obj).intValue();
            if (rotationAngle % 90 == 0) {
                return ((rotationAngle % 360) + 360) % 360;
            }
            return 0;
        }
        return 0;
    }

    public void setRotation(int rotation) {
        this.page.setInt(COSName.ROTATE, rotation);
    }

    public void setContents(PDStream contents) {
        this.page.setItem(COSName.CONTENTS, contents);
    }

    public void setContents(List<PDStream> contents) {
        COSArray array = new COSArray();
        for (PDStream stream : contents) {
            array.add(stream);
        }
        this.page.setItem(COSName.CONTENTS, (COSBase) array);
    }

    public List<PDThreadBead> getThreadBeads() {
        COSArray beads = (COSArray) this.page.getDictionaryObject(COSName.B);
        if (beads == null) {
            beads = new COSArray();
        }
        List<PDThreadBead> pdObjects = new ArrayList<>(beads.size());
        for (int i = 0; i < beads.size(); i++) {
            COSBase base = beads.getObject(i);
            PDThreadBead bead = null;
            if (base instanceof COSDictionary) {
                bead = new PDThreadBead((COSDictionary) base);
            }
            pdObjects.add(bead);
        }
        return new COSArrayList(pdObjects, beads);
    }

    public void setThreadBeads(List<PDThreadBead> beads) {
        this.page.setItem(COSName.B, (COSBase) COSArrayList.converterToCOSArray(beads));
    }

    public PDMetadata getMetadata() {
        PDMetadata retval = null;
        COSBase base = this.page.getDictionaryObject(COSName.METADATA);
        if (base instanceof COSStream) {
            retval = new PDMetadata((COSStream) base);
        }
        return retval;
    }

    public void setMetadata(PDMetadata meta) {
        this.page.setItem(COSName.METADATA, meta);
    }

    public PDPageAdditionalActions getActions() {
        COSDictionary addAct;
        COSBase base = this.page.getDictionaryObject(COSName.AA);
        if (base instanceof COSDictionary) {
            addAct = (COSDictionary) base;
        } else {
            addAct = new COSDictionary();
            this.page.setItem(COSName.AA, (COSBase) addAct);
        }
        return new PDPageAdditionalActions(addAct);
    }

    public void setActions(PDPageAdditionalActions actions) {
        this.page.setItem(COSName.AA, actions);
    }

    public PDTransition getTransition() {
        COSBase base = this.page.getDictionaryObject(COSName.TRANS);
        if (base instanceof COSDictionary) {
            return new PDTransition((COSDictionary) base);
        }
        return null;
    }

    public void setTransition(PDTransition transition) {
        this.page.setItem(COSName.TRANS, transition);
    }

    public void setTransition(PDTransition transition, float duration) {
        this.page.setItem(COSName.TRANS, transition);
        this.page.setItem(COSName.DUR, (COSBase) new COSFloat(duration));
    }

    public List<PDAnnotation> getAnnotations() throws IOException {
        return getAnnotations(new AnnotationFilter() { // from class: org.apache.pdfbox.pdmodel.PDPage.1
            @Override // org.apache.pdfbox.pdmodel.interactive.annotation.AnnotationFilter
            public boolean accept(PDAnnotation annotation) {
                return true;
            }
        });
    }

    public List<PDAnnotation> getAnnotations(AnnotationFilter annotationFilter) throws IOException {
        COSBase base = this.page.getDictionaryObject(COSName.ANNOTS);
        if (base instanceof COSArray) {
            COSArray annots = (COSArray) base;
            List<PDAnnotation> actuals = new ArrayList<>();
            for (int i = 0; i < annots.size(); i++) {
                COSBase item = annots.getObject(i);
                if (item != null) {
                    PDAnnotation createdAnnotation = PDAnnotation.createAnnotation(item);
                    if (annotationFilter.accept(createdAnnotation)) {
                        actuals.add(createdAnnotation);
                    }
                }
            }
            return new COSArrayList(actuals, annots);
        }
        return new COSArrayList(this.page, COSName.ANNOTS);
    }

    public void setAnnotations(List<PDAnnotation> annotations) {
        this.page.setItem(COSName.ANNOTS, (COSBase) COSArrayList.converterToCOSArray(annotations));
    }

    public boolean equals(Object other) {
        return (other instanceof PDPage) && ((PDPage) other).getCOSObject() == getCOSObject();
    }

    public int hashCode() {
        return this.page.hashCode();
    }

    public ResourceCache getResourceCache() {
        return this.resourceCache;
    }

    public List<PDViewportDictionary> getViewports() {
        COSBase base = this.page.getDictionaryObject(COSName.VP);
        if (!(base instanceof COSArray)) {
            return null;
        }
        COSArray array = (COSArray) base;
        List<PDViewportDictionary> viewports = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            COSBase base2 = array.getObject(i);
            if (base2 instanceof COSDictionary) {
                viewports.add(new PDViewportDictionary((COSDictionary) base2));
            } else {
                LOG.warn("Array element " + base2 + " is skipped, must be a (viewport) dictionary");
            }
        }
        return viewports;
    }

    public void setViewports(List<PDViewportDictionary> viewports) {
        if (viewports == null) {
            this.page.removeItem(COSName.VP);
            return;
        }
        COSArray array = new COSArray();
        for (PDViewportDictionary viewport : viewports) {
            array.add(viewport);
        }
        this.page.setItem(COSName.VP, (COSBase) array);
    }

    public float getUserUnit() {
        float userUnit = this.page.getFloat(COSName.USER_UNIT, 1.0f);
        if (userUnit > ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            return userUnit;
        }
        return 1.0f;
    }

    public void setUserUnit(float userUnit) {
        if (userUnit <= ColumnText.GLOBAL_SPACE_CHAR_RATIO) {
            throw new IllegalArgumentException("User unit must be positive");
        }
        this.page.setFloat(COSName.USER_UNIT, userUnit);
    }
}
