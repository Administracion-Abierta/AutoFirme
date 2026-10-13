package es.gob.jmulticard.card.icao;

import es.gob.jmulticard.apdu.iso7816four.pace.MseSetPaceAlgorithmApduCommand;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/icao/WirelessInitializerCan.class */
public final class WirelessInitializerCan implements WirelessInitializer {
    private final String can;

    public WirelessInitializerCan(String cardAccessNumber) {
        if (cardAccessNumber == null || cardAccessNumber.isEmpty()) {
            throw new IllegalArgumentException("El valor del CAN no puede ser nulo ni vacio");
        }
        this.can = cardAccessNumber;
    }

    @Override // es.gob.jmulticard.card.icao.WirelessInitializer
    public byte[] getBytes() {
        return this.can.getBytes();
    }

    public String toString() {
        return this.can;
    }

    @Override // es.gob.jmulticard.card.icao.WirelessInitializer
    public MseSetPaceAlgorithmApduCommand.PacePasswordType getPasswordType() {
        return MseSetPaceAlgorithmApduCommand.PacePasswordType.CAN;
    }
}
