package com.ctc.wstx.shaded.msv_core.util;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/util/LightStack.class */
public final class LightStack {
    private Object[] buf = new Object[8];
    private int len = 0;

    public void push(Object o) {
        try {
            this.buf[this.len] = o;
            this.len++;
        } catch (ArrayIndexOutOfBoundsException e) {
            Object[] nbuf = new Object[this.buf.length * 2];
            System.arraycopy(this.buf, 0, nbuf, 0, this.buf.length);
            this.buf = nbuf;
            Object[] objArr = this.buf;
            int i = this.len;
            this.len = i + 1;
            objArr[i] = o;
        }
    }

    public Object pop() {
        Object[] objArr = this.buf;
        int i = this.len - 1;
        this.len = i;
        return objArr[i];
    }

    public Object top() {
        return this.buf[this.len - 1];
    }

    public int size() {
        return this.len;
    }

    public boolean contains(Object o) {
        for (int i = 0; i < this.len; i++) {
            if (this.buf[i] == o) {
                return true;
            }
        }
        return false;
    }
}
