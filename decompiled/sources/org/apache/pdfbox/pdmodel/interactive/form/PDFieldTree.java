package org.apache.pdfbox.pdmodel.interactive.form;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSDictionary;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/form/PDFieldTree.class */
public class PDFieldTree implements Iterable<PDField> {
    private static final Log LOG = LogFactory.getLog(PDFieldTree.class);
    private final PDAcroForm acroForm;

    public PDFieldTree(PDAcroForm acroForm) {
        if (acroForm == null) {
            throw new IllegalArgumentException("root cannot be null");
        }
        this.acroForm = acroForm;
    }

    @Override // java.lang.Iterable
    public Iterator<PDField> iterator() {
        return new FieldIterator(this.acroForm);
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/pdmodel/interactive/form/PDFieldTree$FieldIterator.class */
    private static final class FieldIterator implements Iterator<PDField> {
        private final Queue<PDField> queue;
        private final Set<COSDictionary> set;

        private FieldIterator(PDAcroForm form) {
            this.queue = new ArrayDeque();
            this.set = Collections.newSetFromMap(new IdentityHashMap());
            List<PDField> fields = form.getFields();
            for (PDField field : fields) {
                enqueueKids(field);
            }
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return !this.queue.isEmpty();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // java.util.Iterator
        public PDField next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return this.queue.poll();
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException();
        }

        private void enqueueKids(PDField node) {
            this.queue.add(node);
            this.set.add(node.getCOSObject());
            if (node instanceof PDNonTerminalField) {
                List<PDField> kids = ((PDNonTerminalField) node).getChildren();
                for (PDField kid : kids) {
                    if (this.set.contains(kid.getCOSObject())) {
                        PDFieldTree.LOG.error("Child of field '" + node.getFullyQualifiedName() + "' already exists elsewhere, ignored to avoid recursion");
                    } else {
                        enqueueKids(kid);
                    }
                }
            }
        }
    }
}
