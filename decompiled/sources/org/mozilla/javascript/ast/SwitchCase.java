package org.mozilla.javascript.ast;

import java.util.ArrayList;
import java.util.List;
import org.mozilla.javascript.Token;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ast/SwitchCase.class */
public class SwitchCase extends AstNode {
    private AstNode expression;
    private List<AstNode> statements;

    public SwitchCase() {
        this.type = 116;
    }

    public SwitchCase(int pos) {
        super(pos);
        this.type = 116;
    }

    public SwitchCase(int pos, int len) {
        super(pos, len);
        this.type = 116;
    }

    public AstNode getExpression() {
        return this.expression;
    }

    public void setExpression(AstNode expression) {
        this.expression = expression;
        if (expression != null) {
            expression.setParent(this);
        }
    }

    public boolean isDefault() {
        return this.expression == null;
    }

    public List<AstNode> getStatements() {
        return this.statements;
    }

    public void setStatements(List<AstNode> statements) {
        if (this.statements != null) {
            this.statements.clear();
        }
        for (AstNode s : statements) {
            addStatement(s);
        }
    }

    public void addStatement(AstNode statement) {
        assertNotNull(statement);
        if (this.statements == null) {
            this.statements = new ArrayList();
        }
        int end = statement.getPosition() + statement.getLength();
        setLength(end - getPosition());
        this.statements.add(statement);
        statement.setParent(this);
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public String toSource(int depth) {
        StringBuilder sb = new StringBuilder();
        sb.append(makeIndent(depth));
        if (this.expression == null) {
            sb.append("default:\n");
        } else {
            sb.append("case ");
            sb.append(this.expression.toSource(0));
            sb.append(":");
            if (getInlineComment() != null) {
                sb.append(getInlineComment().toSource(depth + 1));
            }
            sb.append("\n");
        }
        if (this.statements != null) {
            for (AstNode s : this.statements) {
                sb.append(s.toSource(depth + 1));
                if (s.getType() == 162 && ((Comment) s).getCommentType() == Token.CommentType.LINE) {
                    sb.append("\n");
                }
            }
        }
        return sb.toString();
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public void visit(NodeVisitor v) {
        if (v.visit(this)) {
            if (this.expression != null) {
                this.expression.visit(v);
            }
            if (this.statements != null) {
                for (AstNode s : this.statements) {
                    s.visit(v);
                }
            }
        }
    }
}
