package org.bouncycastle.tsp.ers;

import java.util.Date;
import org.bouncycastle.util.Selector;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/tsp/ers/ERSEvidenceRecordSelector.class */
public class ERSEvidenceRecordSelector implements Selector<ERSEvidenceRecord> {
    private final ERSData data;
    private final Date date;

    public ERSEvidenceRecordSelector(ERSData eRSData) {
        this(eRSData, new Date());
    }

    public ERSEvidenceRecordSelector(ERSData eRSData, Date date) {
        this.data = eRSData;
        this.date = new Date(date.getTime());
    }

    public ERSData getData() {
        return this.data;
    }

    @Override // org.bouncycastle.util.Selector
    public boolean match(ERSEvidenceRecord eRSEvidenceRecord) {
        try {
            if (!eRSEvidenceRecord.isContaining(this.data, this.date)) {
                return false;
            }
            try {
                eRSEvidenceRecord.validatePresent(this.data, this.date);
                return true;
            } catch (Exception e) {
                return false;
            }
        } catch (Exception e2) {
            return false;
        }
    }

    @Override // org.bouncycastle.util.Selector
    public Object clone() {
        return this;
    }
}
