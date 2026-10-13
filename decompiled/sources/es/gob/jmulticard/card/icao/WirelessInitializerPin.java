package es.gob.jmulticard.card.icao;

import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.apdu.iso7816four.pace.MseSetPaceAlgorithmApduCommand;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/icao/WirelessInitializerPin.class */
public final class WirelessInitializerPin implements WirelessInitializer {
    private final char[] pin;

    public WirelessInitializerPin(char[] personalIdentificationNumber) {
        if (personalIdentificationNumber == null || personalIdentificationNumber.length < 1) {
            throw new IllegalArgumentException("El valor del PIN no puede ser nulo ni vacio");
        }
        this.pin = personalIdentificationNumber;
    }

    @Override // es.gob.jmulticard.card.icao.WirelessInitializer
    public byte[] getBytes() {
        return HexUtils.charArrayToByteArray(this.pin);
    }

    public String toString() {
        return new String(this.pin);
    }

    @Override // es.gob.jmulticard.card.icao.WirelessInitializer
    public MseSetPaceAlgorithmApduCommand.PacePasswordType getPasswordType() {
        return MseSetPaceAlgorithmApduCommand.PacePasswordType.PIN;
    }
}
