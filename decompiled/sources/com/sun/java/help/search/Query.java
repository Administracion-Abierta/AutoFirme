package com.sun.java.help.search;

import java.util.Vector;
import javax.help.search.SearchQuery;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/Query.class */
class Query {
    private double _currentStandard;
    private final int _nColumns;
    private double[] _missingPenalty;
    private double[] _upperboundTemplate;
    private double[] _penalties;
    private final SearchEnvironment _env;
    private int _hitCounter = 0;
    private boolean _vote = false;
    private HitStore _store;

    public Query(SearchEnvironment searchEnvironment, int i, double[] dArr) {
        this._env = searchEnvironment;
        this._nColumns = i;
        this._missingPenalty = new double[i];
        this._upperboundTemplate = new double[i];
        this._penalties = dArr;
        this._currentStandard = (((double) (i - 1)) * 10.0d) + 9.9999d;
        this._store = new HitStore(this._currentStandard);
        for (int i2 = 0; i2 < this._nColumns; i2++) {
            this._missingPenalty[i2] = dArr != null ? dArr[i2] : 10.0d;
        }
        makePenaltiesTable();
    }

    public void makeEvent(int i, SearchQuery searchQuery) {
        Vector vector = new Vector(i);
        if (i > 0) {
            QueryHit queryHitFirstBestQueryHit = this._store.firstBestQueryHit();
            int i2 = i;
            while (queryHitFirstBestQueryHit != null) {
                try {
                    vector.addElement(this._env.makeItem(queryHitFirstBestQueryHit));
                } catch (Exception e) {
                    System.err.println(new StringBuffer().append(e).append("hit not translated").toString());
                }
                i2--;
                queryHitFirstBestQueryHit = i2 > 0 ? this._store.nextBestQueryHit() : null;
            }
        }
        searchQuery.itemsFound(true, vector);
    }

    public double lookupPenalty(int i) {
        return this._penalties[i];
    }

    public double getOutOufOrderPenalty() {
        return 0.25d;
    }

    public double getGapPenalty() {
        return 0.005d;
    }

    public int getNColumns() {
        return this._nColumns;
    }

    public boolean goodEnough(double d) {
        return d <= this._currentStandard;
    }

    public int[] getConceptArrayOfNewHit(double d, Location location) {
        QueryHit queryHit = new QueryHit(location, d, this._nColumns);
        this._store.addQueryHit(queryHit);
        this._hitCounter++;
        return queryHit.getArray();
    }

    public void resetForNextDocument() {
        this._currentStandard = this._store.getCurrentStandard();
        for (int i = 0; i < this._nColumns; i++) {
            this._upperboundTemplate[i] = this._missingPenalty[i];
        }
        this._vote = false;
    }

    public boolean vote() {
        double d = 0.0d;
        for (int i = 0; i < this._nColumns; i++) {
            d += this._upperboundTemplate[i];
        }
        boolean z = d <= this._currentStandard;
        this._vote = z;
        return z;
    }

    public void updateEstimate(int i, double d) {
        if (d < this._upperboundTemplate[i]) {
            this._upperboundTemplate[i] = d;
        }
    }

    public void printHits(int i) {
        if (i > 0) {
            QueryHit queryHitFirstBestQueryHit = this._store.firstBestQueryHit();
            int i2 = i;
            while (queryHitFirstBestQueryHit != null) {
                try {
                    System.out.println(this._env.hitToString(queryHitFirstBestQueryHit));
                } catch (Exception e) {
                    System.err.println(new StringBuffer().append(e).append("hit not translated").toString());
                }
                i2--;
                queryHitFirstBestQueryHit = i2 > 0 ? this._store.nextBestQueryHit() : null;
            }
        }
    }

    private void makePenaltiesTable() {
        int i = 1 << this._nColumns;
        this._penalties = new double[i];
        for (int i2 = 0; i2 < i; i2++) {
            this._penalties[i2] = computePenalty(i2);
        }
    }

    private double computePenalty(int i) {
        double d = 0.0d;
        for (int i2 = 0; i2 < this._nColumns; i2++) {
            if ((i & (1 << i2)) == 0) {
                d += this._missingPenalty[i2];
            }
        }
        return d;
    }
}
