package es.gob.jmulticard.card.icao;

import es.gob.jmulticard.apdu.iso7816four.pace.MseSetPaceAlgorithmApduCommand;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/icao/WirelessInitializer.class */
public interface WirelessInitializer {
    byte[] getBytes();

    MseSetPaceAlgorithmApduCommand.PacePasswordType getPasswordType();
}
