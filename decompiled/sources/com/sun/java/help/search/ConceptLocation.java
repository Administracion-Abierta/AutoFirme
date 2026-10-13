package com.sun.java.help.search;

import com.sun.jna.platform.win32.COM.tlb.imp.TlbBase;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/ConceptLocation.class */
class ConceptLocation {
    private int _concept;
    private int _begin;
    private int _end;
    private static ConceptLocationSorter _cComp;
    private static ConceptLocationSorter _pComp;

    /* JADX INFO: renamed from: com.sun.java.help.search.ConceptLocation$1, reason: invalid class name */
    /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/ConceptLocation$1.class */
    static class AnonymousClass1 {
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/ConceptLocation$ConceptLocationSorter.class */
    private abstract class ConceptLocationSorter {
        private final ConceptLocation this$0;

        public abstract boolean smallerThan(ConceptLocation conceptLocation, ConceptLocation conceptLocation2);

        private ConceptLocationSorter(ConceptLocation conceptLocation) {
            this.this$0 = conceptLocation;
        }

        ConceptLocationSorter(ConceptLocation conceptLocation, AnonymousClass1 anonymousClass1) {
            this(conceptLocation);
        }

        private int partition(ConceptLocation[] conceptLocationArr, int i, int i2) {
            ConceptLocation conceptLocation = conceptLocationArr[i];
            int i3 = i - 1;
            int i4 = i2 + 1;
            while (true) {
                i4--;
                if (!smallerThan(conceptLocation, conceptLocationArr[i4])) {
                    do {
                        i3++;
                    } while (smallerThan(conceptLocationArr[i3], conceptLocation));
                    if (i3 < i4) {
                        ConceptLocation conceptLocation2 = conceptLocationArr[i3];
                        conceptLocationArr[i3] = conceptLocationArr[i4];
                        conceptLocationArr[i4] = conceptLocation2;
                    } else {
                        return i4;
                    }
                }
            }
        }

        public void quicksort(ConceptLocation[] conceptLocationArr, int i, int i2) {
            if (i < i2) {
                int iPartition = partition(conceptLocationArr, i, i2);
                quicksort(conceptLocationArr, i, iPartition);
                quicksort(conceptLocationArr, iPartition + 1, i2);
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/ConceptLocation$ConceptSorter.class */
    private class ConceptSorter extends ConceptLocationSorter {
        private final ConceptLocation this$0;

        private ConceptSorter(ConceptLocation conceptLocation) {
            super(conceptLocation, null);
            this.this$0 = conceptLocation;
        }

        ConceptSorter(ConceptLocation conceptLocation, AnonymousClass1 anonymousClass1) {
            this(conceptLocation);
        }

        @Override // com.sun.java.help.search.ConceptLocation.ConceptLocationSorter
        public boolean smallerThan(ConceptLocation conceptLocation, ConceptLocation conceptLocation2) {
            return conceptLocation._concept < conceptLocation2._concept;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/ConceptLocation$PositionSorter.class */
    private class PositionSorter extends ConceptLocationSorter {
        private final ConceptLocation this$0;

        private PositionSorter(ConceptLocation conceptLocation) {
            super(conceptLocation, null);
            this.this$0 = conceptLocation;
        }

        PositionSorter(ConceptLocation conceptLocation, AnonymousClass1 anonymousClass1) {
            this(conceptLocation);
        }

        @Override // com.sun.java.help.search.ConceptLocation.ConceptLocationSorter
        public boolean smallerThan(ConceptLocation conceptLocation, ConceptLocation conceptLocation2) {
            return conceptLocation._begin < conceptLocation2._begin || (conceptLocation._begin == conceptLocation2._begin && conceptLocation._end < conceptLocation2._end);
        }
    }

    private ConceptLocation() {
        _cComp = new ConceptSorter(this, null);
        _pComp = new PositionSorter(this, null);
    }

    static {
        new ConceptLocation();
    }

    public ConceptLocation(int i, int i2, int i3) {
        this._concept = i;
        this._begin = i2;
        this._end = i3;
    }

    public boolean equals(ConceptLocation conceptLocation) {
        return this._concept == conceptLocation._concept && this._begin == conceptLocation._begin && this._end == conceptLocation._end;
    }

    public void setConcept(int i) {
        this._concept = i;
    }

    public int getConcept() {
        return this._concept;
    }

    public int getBegin() {
        return this._begin;
    }

    public int getEnd() {
        return this._end;
    }

    public int getLength() {
        return this._end - this._begin;
    }

    public static void sortByConcept(ConceptLocation[] conceptLocationArr, int i, int i2) {
        _cComp.quicksort(conceptLocationArr, i, i2 - 1);
    }

    public static void sortByPosition(ConceptLocation[] conceptLocationArr, int i, int i2) {
        _pComp.quicksort(conceptLocationArr, i, i2 - 1);
    }

    public void print() {
        System.out.println(new StringBuffer().append(this._concept).append(TlbBase.TAB).append(this._begin).append(TlbBase.TAB).append(this._end).toString());
    }

    public static void main(String[] strArr) {
        ConceptLocation[] conceptLocationArr = new ConceptLocation[30];
        for (int i = 0; i < 30; i++) {
            int iRandom = (int) (Math.random() * 1000.0d);
            int iRandom2 = (int) (Math.random() * 1000.0d);
            conceptLocationArr[i] = new ConceptLocation(iRandom, iRandom2, iRandom2 + ((int) (Math.random() * 10.0d)));
        }
        for (int i2 = 0; i2 < 30; i2++) {
            conceptLocationArr[i2].print();
        }
        sortByConcept(conceptLocationArr, 0, 30);
        System.out.println("----------------------------------");
        for (int i3 = 0; i3 < 30; i3++) {
            conceptLocationArr[i3].print();
        }
        sortByPosition(conceptLocationArr, 0, 30);
        System.out.println("----------------------------------");
        for (int i4 = 0; i4 < 30; i4++) {
            conceptLocationArr[i4].print();
        }
    }
}
