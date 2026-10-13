package org.apache.pdfbox.util.filetypedetector;

import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/util/filetypedetector/ByteTrie.class */
class ByteTrie<T> {
    private final ByteTrieNode<T> root = new ByteTrieNode<>();
    private int maxDepth;

    ByteTrie() {
    }

    /* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/util/filetypedetector/ByteTrie$ByteTrieNode.class */
    static class ByteTrieNode<T> {
        private final Map<Byte, ByteTrieNode<T>> children = new HashMap();
        private T value = null;

        ByteTrieNode() {
        }

        public void setValue(T value) {
            if (this.value != null) {
                throw new IllegalStateException("Value already set for this trie node");
            }
            this.value = value;
        }

        public T getValue() {
            return this.value;
        }
    }

    public T find(byte[] bytes) {
        ByteTrieNode<T> node = this.root;
        T val = node.getValue();
        for (byte b : bytes) {
            ByteTrieNode<T> child = (ByteTrieNode) ((ByteTrieNode) node).children.get(Byte.valueOf(b));
            if (child == null) {
                break;
            }
            node = child;
            if (node.getValue() != null) {
                val = node.getValue();
            }
        }
        return val;
    }

    public void addPath(T value, byte[]... parts) {
        int depth = 0;
        ByteTrieNode<T> node = this.root;
        for (byte[] part : parts) {
            for (byte b : part) {
                ByteTrieNode<T> child = (ByteTrieNode) ((ByteTrieNode) node).children.get(Byte.valueOf(b));
                if (child == null) {
                    child = new ByteTrieNode<>();
                    ((ByteTrieNode) node).children.put(Byte.valueOf(b), child);
                }
                node = child;
                depth++;
            }
        }
        node.setValue(value);
        this.maxDepth = Math.max(this.maxDepth, depth);
    }

    public void setDefaultValue(T defaultValue) {
        this.root.setValue(defaultValue);
    }

    public int getMaxDepth() {
        return this.maxDepth;
    }
}
