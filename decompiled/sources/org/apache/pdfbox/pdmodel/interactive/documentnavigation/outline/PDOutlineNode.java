package org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline;

import java.util.Iterator;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.PDDictionaryWrapper;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/documentnavigation/outline/PDOutlineNode.class */
public abstract class PDOutlineNode extends PDDictionaryWrapper {
    public PDOutlineNode() {
    }

    public PDOutlineNode(COSDictionary dict) {
        super(dict);
    }

    PDOutlineNode getParent() {
        COSBase base = getCOSObject().getDictionaryObject(COSName.PARENT);
        if (base instanceof COSDictionary) {
            COSDictionary parent = (COSDictionary) base;
            if (COSName.OUTLINES.equals(parent.getCOSName(COSName.TYPE))) {
                return new PDDocumentOutline(parent);
            }
            return new PDOutlineItem(parent);
        }
        return null;
    }

    void setParent(PDOutlineNode parent) {
        getCOSObject().setItem(COSName.PARENT, parent);
    }

    public void addLast(PDOutlineItem newChild) {
        requireSingleNode(newChild);
        append(newChild);
        updateParentOpenCountForAddedChild(newChild);
    }

    public void addFirst(PDOutlineItem newChild) {
        requireSingleNode(newChild);
        prepend(newChild);
        updateParentOpenCountForAddedChild(newChild);
    }

    void requireSingleNode(PDOutlineItem node) {
        if (node.getNextSibling() != null || node.getPreviousSibling() != null) {
            throw new IllegalArgumentException("A single node with no siblings is required");
        }
    }

    private void append(PDOutlineItem newChild) {
        newChild.setParent(this);
        if (!hasChildren()) {
            setFirstChild(newChild);
        } else {
            PDOutlineItem previousLastChild = getLastChild();
            previousLastChild.setNextSibling(newChild);
            newChild.setPreviousSibling(previousLastChild);
        }
        setLastChild(newChild);
    }

    private void prepend(PDOutlineItem newChild) {
        newChild.setParent(this);
        if (!hasChildren()) {
            setLastChild(newChild);
        } else {
            PDOutlineItem previousFirstChild = getFirstChild();
            newChild.setNextSibling(previousFirstChild);
            previousFirstChild.setPreviousSibling(newChild);
        }
        setFirstChild(newChild);
    }

    void updateParentOpenCountForAddedChild(PDOutlineItem newChild) {
        int delta = 1;
        if (newChild.isNodeOpen()) {
            delta = 1 + newChild.getOpenCount();
        }
        newChild.updateParentOpenCount(delta);
    }

    public boolean hasChildren() {
        return getCOSObject().getCOSDictionary(COSName.FIRST) != null;
    }

    PDOutlineItem getOutlineItem(COSName name) {
        COSBase base = getCOSObject().getDictionaryObject(name);
        if (base instanceof COSDictionary) {
            return new PDOutlineItem((COSDictionary) base);
        }
        return null;
    }

    public PDOutlineItem getFirstChild() {
        return getOutlineItem(COSName.FIRST);
    }

    void setFirstChild(PDOutlineNode outlineNode) {
        getCOSObject().setItem(COSName.FIRST, outlineNode);
    }

    public PDOutlineItem getLastChild() {
        return getOutlineItem(COSName.LAST);
    }

    void setLastChild(PDOutlineNode outlineNode) {
        getCOSObject().setItem(COSName.LAST, outlineNode);
    }

    public int getOpenCount() {
        return getCOSObject().getInt(COSName.COUNT, 0);
    }

    void setOpenCount(int openCount) {
        getCOSObject().setInt(COSName.COUNT, openCount);
    }

    public void openNode() {
        if (!isNodeOpen()) {
            switchNodeCount();
        }
    }

    public void closeNode() {
        if (isNodeOpen()) {
            switchNodeCount();
        }
    }

    private void switchNodeCount() {
        int openCount = getOpenCount();
        setOpenCount(-openCount);
        updateParentOpenCount(-openCount);
    }

    public boolean isNodeOpen() {
        return getOpenCount() > 0;
    }

    void updateParentOpenCount(int delta) {
        PDOutlineNode parent = getParent();
        if (parent != null) {
            if (parent.isNodeOpen()) {
                parent.setOpenCount(parent.getOpenCount() + delta);
                parent.updateParentOpenCount(delta);
            } else {
                parent.setOpenCount(parent.getOpenCount() - delta);
            }
        }
    }

    public Iterable<PDOutlineItem> children() {
        return new Iterable<PDOutlineItem>() { // from class: org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineNode.1
            @Override // java.lang.Iterable
            public Iterator<PDOutlineItem> iterator() {
                return new PDOutlineItemIterator(PDOutlineNode.this.getFirstChild());
            }
        };
    }
}
