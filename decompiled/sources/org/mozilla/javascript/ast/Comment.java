package org.mozilla.javascript.ast;

import org.mozilla.javascript.Token;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ast/Comment.class */
public class Comment extends AstNode {
    private String value;
    private Token.CommentType commentType;

    public Comment(int pos, int len, Token.CommentType type, String value) {
        super(pos, len);
        this.type = 162;
        this.commentType = type;
        this.value = value;
    }

    public Token.CommentType getCommentType() {
        return this.commentType;
    }

    public void setCommentType(Token.CommentType type) {
        this.commentType = type;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String commentString) {
        this.value = commentString;
        setLength(this.value.length());
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public String toSource(int depth) {
        StringBuilder sb = new StringBuilder(getLength() + 10);
        sb.append(makeIndent(depth));
        sb.append(this.value);
        if (Token.CommentType.BLOCK_COMMENT == getCommentType()) {
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override // org.mozilla.javascript.ast.AstNode
    public void visit(NodeVisitor v) {
        v.visit(this);
    }
}
