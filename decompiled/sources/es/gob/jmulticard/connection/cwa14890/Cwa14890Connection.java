package es.gob.jmulticard.connection.cwa14890;

import es.gob.jmulticard.connection.ApduConnection;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/connection/cwa14890/Cwa14890Connection.class */
public interface Cwa14890Connection extends ApduConnection {
    byte[] getKenc();

    byte[] getKmac();

    byte[] getSsc();
}
