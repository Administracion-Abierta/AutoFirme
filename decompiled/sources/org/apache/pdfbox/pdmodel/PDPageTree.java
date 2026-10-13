package org.apache.pdfbox.pdmodel;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/PDPageTree.class */
public class PDPageTree implements COSObjectable, Iterable<PDPage> {
    private static final Log LOG = LogFactory.getLog(PDPageTree.class);
    private final COSDictionary root;
    private final PDDocument document;

    public PDPageTree() {
        this.root = new COSDictionary();
        this.root.setItem(COSName.TYPE, (COSBase) COSName.PAGES);
        this.root.setItem(COSName.KIDS, (COSBase) new COSArray());
        this.root.setItem(COSName.COUNT, (COSBase) COSInteger.ZERO);
        this.document = null;
    }

    public PDPageTree(COSDictionary root) {
        this(root, null);
    }

    PDPageTree(COSDictionary root, PDDocument document) {
        if (root == null) {
            throw new IllegalArgumentException("page tree root cannot be null");
        }
        if (COSName.PAGE.equals(root.getCOSName(COSName.TYPE))) {
            COSArray kids = new COSArray();
            kids.add((COSBase) root);
            this.root = new COSDictionary();
            this.root.setItem(COSName.KIDS, (COSBase) kids);
            this.root.setInt(COSName.COUNT, 1);
        } else {
            this.root = root;
        }
        this.document = document;
    }

    public static COSBase getInheritableAttribute(COSDictionary node, COSName key) {
        COSBase value = node.getDictionaryObject(key);
        if (value != null) {
            return value;
        }
        COSBase base = node.getDictionaryObject(COSName.PARENT, COSName.P);
        if (base instanceof COSDictionary) {
            COSDictionary parent = (COSDictionary) base;
            if (COSName.PAGES.equals(parent.getDictionaryObject(COSName.TYPE))) {
                return getInheritableAttribute(parent, key);
            }
            return null;
        }
        return null;
    }

    @Override // java.lang.Iterable
    public Iterator<PDPage> iterator() {
        return new PageIterator(this.root);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public List<COSDictionary> getKids(COSDictionary node) {
        List<COSDictionary> result = new ArrayList<>();
        COSArray kids = node.getCOSArray(COSName.KIDS);
        if (kids == null) {
            return result;
        }
        int size = kids.size();
        for (int i = 0; i < size; i++) {
            COSBase base = kids.getObject(i);
            if (base instanceof COSDictionary) {
                result.add((COSDictionary) base);
            } else {
                LOG.warn("COSDictionary expected, but got " + (base == null ? "null" : base.getClass().getSimpleName()));
            }
        }
        return result;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/PDPageTree$PageIterator.class */
    private final class PageIterator implements Iterator<PDPage> {
        private final Queue<COSDictionary> queue;
        private Set<COSDictionary> set;

        private PageIterator(COSDictionary node) {
            this.queue = new ArrayDeque();
            this.set = new HashSet();
            enqueueKids(node);
            this.set = null;
        }

        private void enqueueKids(COSDictionary node) {
            if (PDPageTree.this.isPageTreeNode(node)) {
                List<COSDictionary> kids = PDPageTree.this.getKids(node);
                for (COSDictionary kid : kids) {
                    if (this.set.contains(kid)) {
                        PDPageTree.LOG.error("This page tree node has already been visited");
                    } else {
                        if (kid.containsKey(COSName.KIDS)) {
                            this.set.add(kid);
                        }
                        enqueueKids(kid);
                    }
                }
                return;
            }
            this.queue.add(node);
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return !this.queue.isEmpty();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public PDPage next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            COSDictionary next = this.queue.poll();
            PDPageTree.sanitizeType(next);
            ResourceCache resourceCache = PDPageTree.this.document != null ? PDPageTree.this.document.getResourceCache() : null;
            return new PDPage(next, resourceCache);
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public PDPage get(int index) {
        COSDictionary dict = get(index + 1, this.root, 0);
        sanitizeType(dict);
        ResourceCache resourceCache = this.document != null ? this.document.getResourceCache() : null;
        return new PDPage(dict, resourceCache);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void sanitizeType(COSDictionary dictionary) {
        COSName type = dictionary.getCOSName(COSName.TYPE);
        if (type == null) {
            dictionary.setItem(COSName.TYPE, (COSBase) COSName.PAGE);
        } else if (!COSName.PAGE.equals(type)) {
            throw new IllegalStateException("Expected 'Page' but found " + type);
        }
    }

    private COSDictionary get(int pageNum, COSDictionary node, int encountered) {
        if (pageNum < 1) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + pageNum);
        }
        if (isPageTreeNode(node)) {
            int count = node.getInt(COSName.COUNT, 0);
            if (pageNum <= encountered + count) {
                for (COSDictionary kid : getKids(node)) {
                    if (isPageTreeNode(kid)) {
                        int kidCount = kid.getInt(COSName.COUNT, 0);
                        if (pageNum <= encountered + kidCount) {
                            return get(pageNum, kid, encountered);
                        }
                        encountered += kidCount;
                    } else {
                        encountered++;
                        if (pageNum == encountered) {
                            return get(pageNum, kid, encountered);
                        }
                    }
                }
                throw new IllegalStateException("1-based index not found: " + pageNum);
            }
            throw new IndexOutOfBoundsException("1-based index out of bounds: " + pageNum);
        }
        if (encountered == pageNum) {
            return node;
        }
        throw new IllegalStateException("1-based index not found: " + pageNum);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isPageTreeNode(COSDictionary node) {
        return node != null && (node.getCOSName(COSName.TYPE) == COSName.PAGES || node.containsKey(COSName.KIDS));
    }

    public int indexOf(PDPage page) {
        SearchContext context = new SearchContext(page);
        if (!findPage(context, this.root)) {
            return -1;
        }
        return context.index;
    }

    private boolean findPage(SearchContext context, COSDictionary node) {
        for (COSDictionary kid : getKids(node)) {
            if (context.found) {
                break;
            }
            if (isPageTreeNode(kid)) {
                findPage(context, kid);
            } else {
                context.visitPage(kid);
            }
        }
        return context.found;
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/PDPageTree$SearchContext.class */
    private static final class SearchContext {
        private final COSDictionary searched;
        private int index;
        private boolean found;

        private SearchContext(PDPage page) {
            this.index = -1;
            this.searched = page.getCOSObject();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void visitPage(COSDictionary current) {
            this.index++;
            this.found = this.searched == current;
        }
    }

    public int getCount() {
        return this.root.getInt(COSName.COUNT, 0);
    }

    @Override // org.apache.pdfbox.pdmodel.common.COSObjectable
    public COSDictionary getCOSObject() {
        return this.root;
    }

    public void remove(int index) {
        COSDictionary node = get(index + 1, this.root, 0);
        remove(node);
    }

    public void remove(PDPage page) {
        remove(page.getCOSObject());
    }

    private void remove(COSDictionary node) {
        COSDictionary parent = (COSDictionary) node.getDictionaryObject(COSName.PARENT, COSName.P);
        COSArray kids = (COSArray) parent.getDictionaryObject(COSName.KIDS);
        if (kids.removeObject(node)) {
            do {
                node = (COSDictionary) node.getDictionaryObject(COSName.PARENT, COSName.P);
                if (node != null) {
                    node.setInt(COSName.COUNT, node.getInt(COSName.COUNT) - 1);
                }
            } while (node != null);
        }
    }

    public void add(PDPage page) {
        COSDictionary node = page.getCOSObject();
        node.setItem(COSName.PARENT, (COSBase) this.root);
        COSArray kids = (COSArray) this.root.getDictionaryObject(COSName.KIDS);
        kids.add((COSBase) node);
        do {
            node = (COSDictionary) node.getDictionaryObject(COSName.PARENT, COSName.P);
            if (node != null) {
                node.setInt(COSName.COUNT, node.getInt(COSName.COUNT) + 1);
            }
        } while (node != null);
    }

    public void insertBefore(PDPage newPage, PDPage nextPage) {
        COSDictionary nextPageDict = nextPage.getCOSObject();
        COSDictionary parentDict = (COSDictionary) nextPageDict.getDictionaryObject(COSName.PARENT);
        COSArray kids = (COSArray) parentDict.getDictionaryObject(COSName.KIDS);
        boolean found = false;
        for (int i = 0; i < kids.size(); i++) {
            COSDictionary pageDict = (COSDictionary) kids.getObject(i);
            if (pageDict.equals(nextPage.getCOSObject())) {
                kids.add(i, newPage.getCOSObject());
                newPage.getCOSObject().setItem(COSName.PARENT, (COSBase) parentDict);
                found = true;
                break;
            }
        }
        if (!found) {
            throw new IllegalArgumentException("attempted to insert before orphan page");
        }
        increaseParents(parentDict);
    }

    public void insertAfter(PDPage newPage, PDPage prevPage) {
        COSDictionary prevPageDict = prevPage.getCOSObject();
        COSDictionary parentDict = (COSDictionary) prevPageDict.getDictionaryObject(COSName.PARENT);
        COSArray kids = (COSArray) parentDict.getDictionaryObject(COSName.KIDS);
        boolean found = false;
        for (int i = 0; i < kids.size(); i++) {
            COSDictionary pageDict = (COSDictionary) kids.getObject(i);
            if (pageDict.equals(prevPage.getCOSObject())) {
                kids.add(i + 1, newPage.getCOSObject());
                newPage.getCOSObject().setItem(COSName.PARENT, (COSBase) parentDict);
                found = true;
                break;
            }
        }
        if (!found) {
            throw new IllegalArgumentException("attempted to insert before orphan page");
        }
        increaseParents(parentDict);
    }

    private void increaseParents(COSDictionary parentDict) {
        do {
            int cnt = parentDict.getInt(COSName.COUNT);
            parentDict.setInt(COSName.COUNT, cnt + 1);
            parentDict = (COSDictionary) parentDict.getDictionaryObject(COSName.PARENT);
        } while (parentDict != null);
    }
}
