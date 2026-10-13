package org.apache.fontbox.afm;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/afm/Ligature.class */
public class Ligature {
    private String successor;
    private String ligature;

    public String getLigature() {
        return this.ligature;
    }

    public void setLigature(String lig) {
        this.ligature = lig;
    }

    public String getSuccessor() {
        return this.successor;
    }

    public void setSuccessor(String successorValue) {
        this.successor = successorValue;
    }
}
