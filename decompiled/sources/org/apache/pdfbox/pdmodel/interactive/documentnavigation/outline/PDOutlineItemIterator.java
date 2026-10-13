package org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline;

import java.util.Iterator;
import java.util.NoSuchElementException;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/documentnavigation/outline/PDOutlineItemIterator.class */
class PDOutlineItemIterator implements Iterator<PDOutlineItem> {
    private PDOutlineItem currentItem;
    private final PDOutlineItem startingItem;

    PDOutlineItemIterator(PDOutlineItem startingItem) {
        this.startingItem = startingItem;
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        if (this.startingItem == null) {
            return false;
        }
        if (this.currentItem == null) {
            return true;
        }
        PDOutlineItem sibling = this.currentItem.getNextSibling();
        return (sibling == null || this.startingItem.equals(sibling)) ? false : true;
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // java.util.Iterator
    public PDOutlineItem next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        if (this.currentItem == null) {
            this.currentItem = this.startingItem;
        } else {
            this.currentItem = this.currentItem.getNextSibling();
        }
        return this.currentItem;
    }

    @Override // java.util.Iterator
    public void remove() {
        throw new UnsupportedOperationException();
    }
}
