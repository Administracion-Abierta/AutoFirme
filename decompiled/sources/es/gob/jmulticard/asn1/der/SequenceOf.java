package es.gob.jmulticard.asn1.der;

import es.gob.jmulticard.asn1.Asn1Exception;
import es.gob.jmulticard.asn1.DecoderObject;
import es.gob.jmulticard.asn1.Tlv;
import es.gob.jmulticard.asn1.TlvException;
import java.util.LinkedList;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/der/SequenceOf.class */
public abstract class SequenceOf extends DecoderObject {
    private static final byte TAG_SEQUENCE = 48;
    private final Class<? extends DecoderObject> elementsType;
    private LinkedList<DecoderObject> sequenceObjects = null;

    @Override // es.gob.jmulticard.asn1.DecoderObject
    protected final void decodeValue() throws TlvException, Asn1Exception {
        Tlv tlv = new Tlv(getBytes());
        checkTag(tlv.getTag());
        int offset = 0;
        byte[] valueBytes = tlv.getValue();
        this.sequenceObjects = new LinkedList<>();
        while (offset < valueBytes.length) {
            byte[] remainingBytes = new byte[valueBytes.length - offset];
            System.arraycopy(valueBytes, offset, remainingBytes, 0, remainingBytes.length);
            Tlv tlv2 = new Tlv(remainingBytes);
            try {
                DecoderObject tmpDo = this.elementsType.getConstructor(new Class[0]).newInstance(new Object[0]);
                offset += tlv2.getBytes().length;
                tmpDo.checkTag(tlv2.getTag());
                tmpDo.setDerValue(tlv2.getBytes());
                this.sequenceObjects.add(tmpDo);
            } catch (Exception e) {
                throw new Asn1Exception("No se ha podido instanciar un " + this.elementsType.getName() + " en la secuencia", e);
            }
        }
    }

    protected SequenceOf(Class<? extends DecoderObject> type) {
        if (type == null) {
            throw new IllegalArgumentException();
        }
        this.elementsType = type;
    }

    @Override // es.gob.jmulticard.asn1.DecoderObject
    protected final byte getDefaultTag() {
        return (byte) 48;
    }

    protected final DecoderObject getElementAt(int index) {
        return this.sequenceObjects.get(index);
    }

    protected final int getElementCount() {
        return this.sequenceObjects.size();
    }
}
