package es.gob.jmulticard.apdu.iso7816four;

import es.gob.jmulticard.asn1.Tlv;
import java.io.ByteArrayOutputStream;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/apdu/iso7816four/MseSetComputationApduCommand.class */
public final class MseSetComputationApduCommand extends MseSetApduCommand {
    public MseSetComputationApduCommand(byte cla, byte[] privateKeyReference, byte[] algorithmReference) {
        super(cla, (byte) 65, (byte) -74, createDst(privateKeyReference, algorithmReference));
    }

    private static byte[] createDst(byte[] privateKeyReference, byte[] algorithmReference) {
        if (privateKeyReference == null) {
            throw new IllegalArgumentException("La referencia a la clave privada no puede ser nula");
        }
        Tlv prkRefTlv = new Tlv((byte) -124, privateKeyReference);
        Tlv algRefTlv = null;
        if (algorithmReference != null) {
            algRefTlv = new Tlv((byte) -128, algorithmReference);
        }
        ByteArrayOutputStream dstData = new ByteArrayOutputStream();
        try {
            dstData.write(prkRefTlv.getBytes());
            if (algRefTlv != null) {
                dstData.write(algRefTlv.getBytes());
            }
            return dstData.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Error creando el cuerpo del DST", e);
        }
    }
}
