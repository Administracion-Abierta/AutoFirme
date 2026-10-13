package javax.help;

/* JADX INFO: loaded from: autofirma.jar:javax/help/SearchHit.class */
public class SearchHit {
    private double confidence;
    private int begin;
    private int end;

    public SearchHit(double d, int i, int i2) {
        this.confidence = d;
        this.begin = i;
        this.end = i2;
    }

    public double getConfidence() {
        return this.confidence;
    }

    public int getBegin() {
        return this.begin;
    }

    public int getEnd() {
        return this.end;
    }
}
