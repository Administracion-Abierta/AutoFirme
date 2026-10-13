package org.mozilla.javascript.ast;

import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ast/LabeledStatement.class */
public class LabeledStatement extends AstNode {
    private List<Label> labels;
    private AstNode statement;

    public LabeledStatement() {
        this.labels = new ArrayList();
        this.type = 134;
    }

    public LabeledStatement(int pos) {
        super(pos);
        this.labels = new ArrayList();
        this.type = 134;
    }

    public LabeledStatement(int pos, int len) {
        super(pos, len);
        this.labels = new ArrayList();
        this.type = 134;
    }

    public List<Label> getLabels() {
        return this.labels;
    }

    public void setLabels(List<Label> labels) {
        assertNotNull(labels);
        if (this.labels != null) {
            this.labels.clear();
        }
        for (Label l : labels) {
            addLabel(l);
        }
    }

    public void addLabel(Label label) {
        assertNotNull(label);
        this.labels.add(label);
        label.setParent(this);
    }

    public AstNode getStatement() {
        return this.statement;
    }

    public Label getLabelByName(String name) {
        for (Label label : this.labels) {
            if (name.equals(label.getName())) {
                return label;
            }
        }
        return null;
    }

    public void setStatement(AstNode statement) {
        assertNotNull(statement);
        this.statement = statement;
        statement.setParent(this);
    }

    public Label getFirstLabel() {
        return this.labels.get(0);
    }

    @Override // org.mozilla.javascript.ast.AstNode, org.mozilla.javascript.Node
    public boolean hasSideEffects() {
        return true;
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public String toSource(int depth) {
        StringBuilder sb = new StringBuilder();
        for (Label label : this.labels) {
            sb.append(label.toSource(depth));
        }
        sb.append(this.statement.toSource(depth + 1));
        return sb.toString();
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public void visit(NodeVisitor v) {
        if (v.visit(this)) {
            for (AstNode label : this.labels) {
                label.visit(v);
            }
            this.statement.visit(v);
        }
    }
}
