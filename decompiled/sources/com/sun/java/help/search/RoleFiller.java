package com.sun.java.help.search;

import java.io.PrintStream;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/RoleFiller.class */
class RoleFiller {
    private ConceptData _conceptData;
    private byte _fixedRole;
    private short _filled;
    private int _begin;
    private int _end;
    private int _limit;
    private RoleFiller _next;
    private RoleFiller[] _fillers;
    static int Threshold = 300;
    public static final RoleFiller STOP = new RoleFiller();

    private RoleFiller() {
    }

    public RoleFiller(int i, ConceptData conceptData, int i2, int i3, int i4) {
        this._conceptData = conceptData;
        this._fixedRole = (byte) i2;
        this._filled = (short) (1 << this._fixedRole);
        this._begin = i3;
        this._end = this._begin + conceptData.getConceptLength();
        this._limit = i4;
        this._next = null;
        this._fillers = new RoleFiller[i];
        this._fillers[i2] = this;
    }

    public void print(PrintStream printStream) {
        printStream.println(new StringBuffer().append(this._begin).append(", ").append(this._end).toString());
    }

    void makeQueryHit(Query query, int i, int i2, double d) {
        if (query.goodEnough(d)) {
            int[] conceptArrayOfNewHit = query.getConceptArrayOfNewHit(d, new Location(i2, this._begin, this._end));
            for (int i3 = 0; i3 < i; i3++) {
                conceptArrayOfNewHit[i3] = (this._filled & (1 << i3)) != 0 ? this._fillers[i3].getConcept() : 0;
            }
        }
    }

    boolean isHit() {
        return this._filled > (1 << this._fixedRole);
    }

    double getScore() {
        return this._conceptData.getScore();
    }

    int getConcept() {
        return this._conceptData.getConcept();
    }

    RoleFiller next() {
        return this._next;
    }

    void use(RoleFiller[] roleFillerArr, int i) {
        if (roleFillerArr[i] != null) {
            RoleFiller roleFiller = roleFillerArr[i];
            roleFillerArr[i] = this;
            this._next = roleFiller;
            while (roleFiller._limit >= this._begin) {
                if (this._fixedRole != roleFiller._fixedRole) {
                    if ((roleFiller._filled & (1 << this._fixedRole)) == 0) {
                        RoleFiller roleFiller2 = roleFiller;
                        roleFiller2._filled = (short) (roleFiller2._filled | (1 << this._fixedRole));
                        roleFiller._fillers[this._fixedRole] = this;
                        roleFiller._end = this._end;
                    } else {
                        roleFiller.considerReplacementWith(this);
                    }
                }
                if (roleFiller._next != null) {
                    roleFiller = roleFiller._next;
                } else {
                    return;
                }
            }
            return;
        }
        roleFillerArr[i] = this;
    }

    private void considerReplacementWith(RoleFiller roleFiller) {
        byte b = roleFiller._fixedRole;
        if (roleFiller.getScore() > this._fillers[b].getScore()) {
            this._fillers[b] = roleFiller;
        }
    }

    private double penalty(Query query, int i) {
        int conceptLength = (this._end - this._begin) + 1;
        double dLookupPenalty = query.lookupPenalty(this._filled);
        for (int i2 = 0; i2 < i; i2++) {
            if ((this._filled & (1 << i2)) != 0) {
                dLookupPenalty += this._fillers[i2]._conceptData.getPenalty();
                conceptLength -= this._fillers[i2]._conceptData.getConceptLength() + 1;
                if ((this._filled >> (i2 + 1)) != 0) {
                    for (int i3 = i2 + 1; i3 < i; i3++) {
                        if ((this._filled & (1 << i3)) != 0 && this._fillers[i3]._begin < this._begin) {
                            dLookupPenalty += query.getOutOufOrderPenalty();
                        }
                    }
                }
            }
        }
        return dLookupPenalty + (((double) conceptLength) * query.getGapPenalty());
    }

    public void scoreList(Query query, int i) {
        int nColumns = query.getNColumns();
        RoleFiller roleFiller = this;
        double dPenalty = roleFiller.penalty(query, nColumns);
        RoleFiller roleFiller2 = roleFiller._next;
        while (true) {
            RoleFiller roleFiller3 = roleFiller2;
            if (roleFiller3 != null) {
                if (roleFiller3._end < roleFiller._begin) {
                    roleFiller.makeQueryHit(query, nColumns, i, dPenalty);
                    roleFiller = roleFiller3;
                    dPenalty = roleFiller.penalty(query, nColumns);
                } else {
                    double dPenalty2 = roleFiller3.penalty(query, nColumns);
                    if (dPenalty2 <= dPenalty) {
                        dPenalty = dPenalty2;
                        roleFiller = roleFiller3;
                    }
                }
                roleFiller2 = roleFiller3._next;
            } else {
                roleFiller.makeQueryHit(query, nColumns, i, dPenalty);
                return;
            }
        }
    }
}
