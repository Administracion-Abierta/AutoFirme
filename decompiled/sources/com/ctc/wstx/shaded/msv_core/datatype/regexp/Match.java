package com.ctc.wstx.shaded.msv_core.datatype.regexp;

import java.text.CharacterIterator;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/datatype/regexp/Match.class */
final class Match implements Cloneable {
    int[] beginpos = null;
    int[] endpos = null;
    int nofgroups = 0;
    CharacterIterator ciSource = null;
    String strSource = null;
    char[] charSource = null;

    public synchronized Object clone() {
        Match ma = new Match();
        if (this.nofgroups > 0) {
            ma.setNumberOfGroups(this.nofgroups);
            if (this.ciSource != null) {
                ma.setSource(this.ciSource);
            }
            if (this.strSource != null) {
                ma.setSource(this.strSource);
            }
            for (int i = 0; i < this.nofgroups; i++) {
                ma.setBeginning(i, getBeginning(i));
                ma.setEnd(i, getEnd(i));
            }
        }
        return ma;
    }

    protected void setNumberOfGroups(int n) {
        int oldn = this.nofgroups;
        this.nofgroups = n;
        if (oldn <= 0 || oldn < n || n * 2 < oldn) {
            this.beginpos = new int[n];
            this.endpos = new int[n];
        }
        for (int i = 0; i < n; i++) {
            this.beginpos[i] = -1;
            this.endpos[i] = -1;
        }
    }

    protected void setSource(CharacterIterator ci) {
        this.ciSource = ci;
        this.strSource = null;
        this.charSource = null;
    }

    protected void setSource(String str) {
        this.ciSource = null;
        this.strSource = str;
        this.charSource = null;
    }

    protected void setSource(char[] chars) {
        this.ciSource = null;
        this.strSource = null;
        this.charSource = chars;
    }

    protected void setBeginning(int index, int v) {
        this.beginpos[index] = v;
    }

    protected void setEnd(int index, int v) {
        this.endpos[index] = v;
    }

    public int getNumberOfGroups() {
        if (this.nofgroups <= 0) {
            throw new IllegalStateException("A result is not set.");
        }
        return this.nofgroups;
    }

    public int getBeginning(int index) {
        if (this.beginpos == null) {
            throw new IllegalStateException("A result is not set.");
        }
        if (index < 0 || this.nofgroups <= index) {
            throw new IllegalArgumentException("The parameter must be less than " + this.nofgroups + ": " + index);
        }
        return this.beginpos[index];
    }

    public int getEnd(int index) {
        if (this.endpos == null) {
            throw new IllegalStateException("A result is not set.");
        }
        if (index < 0 || this.nofgroups <= index) {
            throw new IllegalArgumentException("The parameter must be less than " + this.nofgroups + ": " + index);
        }
        return this.endpos[index];
    }

    public String getCapturedText(int index) {
        String ret;
        if (this.beginpos == null) {
            throw new IllegalStateException("match() has never been called.");
        }
        if (index < 0 || this.nofgroups <= index) {
            throw new IllegalArgumentException("The parameter must be less than " + this.nofgroups + ": " + index);
        }
        int begin = this.beginpos[index];
        int end = this.endpos[index];
        if (begin < 0 || end < 0) {
            return null;
        }
        if (this.ciSource != null) {
            ret = REUtil.substring(this.ciSource, begin, end);
        } else if (this.strSource != null) {
            ret = this.strSource.substring(begin, end);
        } else {
            ret = new String(this.charSource, begin, end - begin);
        }
        return ret;
    }
}
