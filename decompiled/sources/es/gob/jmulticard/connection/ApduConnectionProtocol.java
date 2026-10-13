package es.gob.jmulticard.connection;

import es.gob.jmulticard.JmcLogger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/connection/ApduConnectionProtocol.class */
public enum ApduConnectionProtocol {
    T0,
    T1,
    TCL,
    ANY;

    @Override // java.lang.Enum
    public String toString() {
        switch (this) {
            case T0:
                return "T=0";
            case T1:
                return "T=1";
            case TCL:
                return "T=CL";
            default:
                return "*";
        }
    }

    public static ApduConnectionProtocol getApduConnectionProtocol(String name) {
        switch (name) {
            case "T=1":
                return T1;
            case "T=0":
                return T0;
            case "T=CL":
                return TCL;
            default:
                JmcLogger.warning("Protocolo desconocido, se devolvera '*': " + name);
                return ANY;
        }
    }
}
