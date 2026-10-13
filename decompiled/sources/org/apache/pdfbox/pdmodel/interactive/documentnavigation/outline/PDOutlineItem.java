package org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline;

import com.aowagie.text.pdf.ColumnText;
import java.awt.Color;
import java.io.IOException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureElement;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionFactory;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionGoTo;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDNamedDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageXYZDestination;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/documentnavigation/outline/PDOutlineItem.class */
public final class PDOutlineItem extends PDOutlineNode {
    private static final int ITALIC_FLAG = 1;
    private static final int BOLD_FLAG = 2;

    public PDOutlineItem() {
    }

    public PDOutlineItem(COSDictionary dic) {
        super(dic);
    }

    public void insertSiblingAfter(PDOutlineItem newSibling) {
        requireSingleNode(newSibling);
        PDOutlineNode parent = getParent();
        newSibling.setParent(parent);
        PDOutlineItem next = getNextSibling();
        setNextSibling(newSibling);
        newSibling.setPreviousSibling(this);
        if (next != null) {
            newSibling.setNextSibling(next);
            next.setPreviousSibling(newSibling);
        } else if (parent != null) {
            getParent().setLastChild(newSibling);
        }
        updateParentOpenCountForAddedChild(newSibling);
    }

    public void insertSiblingBefore(PDOutlineItem newSibling) {
        requireSingleNode(newSibling);
        PDOutlineNode parent = getParent();
        newSibling.setParent(parent);
        PDOutlineItem previous = getPreviousSibling();
        setPreviousSibling(newSibling);
        newSibling.setNextSibling(this);
        if (previous != null) {
            previous.setNextSibling(newSibling);
            newSibling.setPreviousSibling(previous);
        } else if (parent != null) {
            getParent().setFirstChild(newSibling);
        }
        updateParentOpenCountForAddedChild(newSibling);
    }

    public PDOutlineItem getPreviousSibling() {
        return getOutlineItem(COSName.PREV);
    }

    void setPreviousSibling(PDOutlineNode outlineNode) {
        getCOSObject().setItem(COSName.PREV, outlineNode);
    }

    public PDOutlineItem getNextSibling() {
        return getOutlineItem(COSName.NEXT);
    }

    void setNextSibling(PDOutlineNode outlineNode) {
        getCOSObject().setItem(COSName.NEXT, outlineNode);
    }

    public String getTitle() {
        return getCOSObject().getString(COSName.TITLE);
    }

    public void setTitle(String title) {
        getCOSObject().setString(COSName.TITLE, title);
    }

    public PDDestination getDestination() throws IOException {
        return PDDestination.create(getCOSObject().getDictionaryObject(COSName.DEST));
    }

    public void setDestination(PDDestination dest) {
        getCOSObject().setItem(COSName.DEST, dest);
    }

    public void setDestination(PDPage page) {
        PDPageXYZDestination dest = null;
        if (page != null) {
            dest = new PDPageXYZDestination();
            dest.setPage(page);
        }
        setDestination(dest);
    }

    public PDPage findDestinationPage(PDDocument doc) throws IOException {
        PDPageDestination pageDestination;
        int pageNumber;
        PDDestination dest = getDestination();
        if (dest == null) {
            PDAction outlineAction = getAction();
            if (outlineAction instanceof PDActionGoTo) {
                dest = ((PDActionGoTo) outlineAction).getDestination();
            }
        }
        if (dest == null) {
            return null;
        }
        if (dest instanceof PDNamedDestination) {
            pageDestination = doc.getDocumentCatalog().findNamedDestinationPage((PDNamedDestination) dest);
            if (pageDestination == null) {
                return null;
            }
        } else if (dest instanceof PDPageDestination) {
            pageDestination = (PDPageDestination) dest;
        } else {
            throw new IOException("Error: Unknown destination type " + dest);
        }
        PDPage page = pageDestination.getPage();
        if (page == null && (pageNumber = pageDestination.getPageNumber()) != -1) {
            page = doc.getPage(pageNumber);
        }
        return page;
    }

    public PDAction getAction() {
        return PDActionFactory.createAction((COSDictionary) getCOSObject().getDictionaryObject(COSName.A));
    }

    public void setAction(PDAction action) {
        getCOSObject().setItem(COSName.A, action);
    }

    public PDStructureElement getStructureElement() {
        PDStructureElement se = null;
        COSDictionary dic = (COSDictionary) getCOSObject().getDictionaryObject(COSName.SE);
        if (dic != null) {
            se = new PDStructureElement(dic);
        }
        return se;
    }

    public void setStructureElement(PDStructureElement structureElement) {
        getCOSObject().setItem(COSName.SE, structureElement);
    }

    public PDColor getTextColor() {
        COSArray csValues = (COSArray) getCOSObject().getDictionaryObject(COSName.C);
        if (csValues == null) {
            csValues = new COSArray();
            csValues.growToSize(3, new COSFloat(ColumnText.GLOBAL_SPACE_CHAR_RATIO));
            getCOSObject().setItem(COSName.C, (COSBase) csValues);
        }
        return new PDColor(csValues, PDDeviceRGB.INSTANCE);
    }

    public void setTextColor(PDColor textColor) {
        getCOSObject().setItem(COSName.C, (COSBase) textColor.toCOSArray());
    }

    public void setTextColor(Color textColor) {
        COSArray array = new COSArray();
        array.add((COSBase) new COSFloat(textColor.getRed() / 255.0f));
        array.add((COSBase) new COSFloat(textColor.getGreen() / 255.0f));
        array.add((COSBase) new COSFloat(textColor.getBlue() / 255.0f));
        getCOSObject().setItem(COSName.C, (COSBase) array);
    }

    public boolean isItalic() {
        return getCOSObject().getFlag(COSName.F, 1);
    }

    public void setItalic(boolean italic) {
        getCOSObject().setFlag(COSName.F, 1, italic);
    }

    public boolean isBold() {
        return getCOSObject().getFlag(COSName.F, 2);
    }

    public void setBold(boolean bold) {
        getCOSObject().setFlag(COSName.F, 2, bold);
    }
}
