package es.gob.jmulticard.card.icao;

import es.gob.jmulticard.CryptoHelper;
import es.gob.jmulticard.apdu.iso7816four.pace.MseSetPaceAlgorithmApduCommand;
import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/card/icao/WirelessInitializerMrz.class */
public final class WirelessInitializerMrz implements WirelessInitializer {
    private final byte[] k;

    private WirelessInitializerMrz(byte[] mrz) {
        if (mrz == null) {
            throw new IllegalArgumentException("La MRZ no puede ser nula");
        }
        this.k = mrz;
    }

    public String toString() {
        return new String(this.k);
    }

    @Override // es.gob.jmulticard.card.icao.WirelessInitializer
    public byte[] getBytes() {
        return this.k;
    }

    @Override // es.gob.jmulticard.card.icao.WirelessInitializer
    public MseSetPaceAlgorithmApduCommand.PacePasswordType getPasswordType() {
        return MseSetPaceAlgorithmApduCommand.PacePasswordType.MRZ;
    }

    public static WirelessInitializerMrz deriveMrz(String mrz, CryptoHelper cryptoHelper) throws MalformedMrzException {
        if (mrz == null || mrz.isEmpty()) {
            throw new IllegalArgumentException("El valor no puede ser nulo ni vacio");
        }
        try {
            return new WirelessInitializerMrz(new Mrz(mrz).getMrzPswd(cryptoHelper));
        } catch (IOException | IllegalArgumentException ex) {
            throw new MalformedMrzException("La MRZ no tiene formato valido", ex);
        }
    }
}
