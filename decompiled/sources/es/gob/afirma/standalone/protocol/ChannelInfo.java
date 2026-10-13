package es.gob.afirma.standalone.protocol;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/protocol/ChannelInfo.class */
class ChannelInfo {
    private final String idSession;
    private int[] ports;

    public ChannelInfo(String str, int[] iArr) {
        this.idSession = str;
        this.ports = iArr;
    }

    public String getIdSession() {
        return this.idSession;
    }

    public int[] getPorts() {
        return this.ports;
    }

    public void setPorts(int[] iArr) {
        this.ports = iArr;
    }
}
