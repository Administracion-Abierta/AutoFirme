package nu.xom;

import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/Nodes.class */
public final class Nodes {
    private final List nodes;

    public Nodes() {
        this.nodes = new ArrayList();
    }

    public Nodes(Node node) {
        if (node == null) {
            throw new NullPointerException("Nodes content must be non-null");
        }
        this.nodes = new ArrayList(1);
        this.nodes.add(node);
    }

    Nodes(List list) {
        this.nodes = list;
    }

    public int size() {
        return this.nodes.size();
    }

    public Node get(int i) {
        return (Node) this.nodes.get(i);
    }

    public Node remove(int i) {
        return (Node) this.nodes.remove(i);
    }

    public void insert(Node node, int i) {
        if (node == null) {
            throw new NullPointerException("Nodes content must be non-null");
        }
        this.nodes.add(i, node);
    }

    public void append(Node node) {
        if (node == null) {
            throw new NullPointerException("Nodes content must be non-null");
        }
        this.nodes.add(node);
    }

    public boolean contains(Node node) {
        return this.nodes.contains(node);
    }
}
