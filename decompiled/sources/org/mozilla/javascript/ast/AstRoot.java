package org.mozilla.javascript.ast;

import java.util.SortedSet;
import java.util.TreeSet;
import org.mozilla.javascript.Node;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ast/AstRoot.class */
public class AstRoot extends ScriptNode {
    private SortedSet<Comment> comments;

    public AstRoot() {
        this.type = 137;
    }

    public AstRoot(int pos) {
        super(pos);
        this.type = 137;
    }

    public SortedSet<Comment> getComments() {
        return this.comments;
    }

    public void setComments(SortedSet<Comment> comments) {
        if (comments == null) {
            this.comments = null;
            return;
        }
        if (this.comments != null) {
            this.comments.clear();
        }
        for (Comment c : comments) {
            addComment(c);
        }
    }

    public void addComment(Comment comment) {
        assertNotNull(comment);
        if (this.comments == null) {
            this.comments = new TreeSet(new AstNode.PositionComparator());
        }
        this.comments.add(comment);
        comment.setParent(this);
    }

    public void visitComments(NodeVisitor visitor) {
        if (this.comments != null) {
            for (Comment c : this.comments) {
                visitor.visit(c);
            }
        }
    }

    public void visitAll(NodeVisitor visitor) {
        visit(visitor);
        visitComments(visitor);
    }

    @Override // org.mozilla.javascript.ast.Scope, org.mozilla.javascript.ast.Jump, org.mozilla.javascript.ast.AstNode
    public String toSource(int depth) {
        StringBuilder sb = new StringBuilder();
        for (Node node : this) {
            sb.append(((AstNode) node).toSource(depth));
            if (node.getType() == 162) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public String debugPrint() {
        AstNode.DebugPrintVisitor dpv = new AstNode.DebugPrintVisitor(new StringBuilder(1000));
        visitAll(dpv);
        return dpv.toString();
    }

    public void checkParentLinks() {
        visit(new NodeVisitor() { // from class: org.mozilla.javascript.ast.AstRoot.1
            @Override // org.mozilla.javascript.ast.NodeVisitor
            public boolean visit(AstNode node) {
                int type = node.getType();
                if (type != 137 && node.getParent() == null) {
                    throw new IllegalStateException("No parent for node: " + node + "\n" + node.toSource(0));
                }
                return true;
            }
        });
    }
}
