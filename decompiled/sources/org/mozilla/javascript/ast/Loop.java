package org.mozilla.javascript.ast;

/* JADX INFO: loaded from: autofirma.jar:org/mozilla/javascript/ast/Loop.class */
public abstract class Loop extends Scope {
    protected AstNode body;
    protected int lp;
    protected int rp;

    public Loop() {
        this.lp = -1;
        this.rp = -1;
    }

    public Loop(int pos) {
        super(pos);
        this.lp = -1;
        this.rp = -1;
    }

    public Loop(int pos, int len) {
        super(pos, len);
        this.lp = -1;
        this.rp = -1;
    }

    public AstNode getBody() {
        return this.body;
    }

    public void setBody(AstNode body) {
        this.body = body;
        int end = body.getPosition() + body.getLength();
        setLength(end - getPosition());
        body.setParent(this);
    }

    public int getLp() {
        return this.lp;
    }

    public void setLp(int lp) {
        this.lp = lp;
    }

    public int getRp() {
        return this.rp;
    }

    public void setRp(int rp) {
        this.rp = rp;
    }

    public void setParens(int lp, int rp) {
        this.lp = lp;
        this.rp = rp;
    }
}
