package es.gob.jmulticard.asn1.der;

import es.gob.jmulticard.asn1.Asn1Exception;
import es.gob.jmulticard.asn1.DecoderObject;
import es.gob.jmulticard.asn1.OptionalDecoderObjectElement;
import es.gob.jmulticard.asn1.Tlv;
import es.gob.jmulticard.asn1.TlvException;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/der/Sequence.class */
public abstract class Sequence extends DecoderObject {
    private static final byte TAG_SEQUENCE = 48;
    private final List<DecoderObject> elements = new ArrayList();
    private final OptionalDecoderObjectElement[] elementsTypes;

    protected final int getElementCount() {
        return this.elements.size();
    }

    protected Sequence(OptionalDecoderObjectElement... types) {
        if (types == null) {
            throw new IllegalArgumentException();
        }
        this.elementsTypes = new OptionalDecoderObjectElement[types.length];
        System.arraycopy(types, 0, this.elementsTypes, 0, types.length);
    }

    @Override // es.gob.jmulticard.asn1.DecoderObject
    protected final void decodeValue() throws TlvException, Asn1Exception {
        Tlv mainTlv = new Tlv(getBytes());
        checkTag(mainTlv.getTag());
        int offset = 0;
        byte[] rawValue = mainTlv.getValue();
        for (int i = 0; i < this.elementsTypes.length; i++) {
            byte[] remainingBytes = new byte[rawValue.length - offset];
            System.arraycopy(rawValue, offset, remainingBytes, 0, remainingBytes.length);
            try {
                Tlv tlv = new Tlv(remainingBytes);
                DecoderObject tmpDo = this.elementsTypes[i].getElementType().getConstructor(new Class[0]).newInstance(new Object[0]);
                tmpDo.checkTag(tlv.getTag());
                tmpDo.setDerValue(tlv.getBytes());
                offset += tlv.getBytes().length;
                this.elements.add(tmpDo);
            } catch (Exception e) {
                if (!this.elementsTypes[i].isOptional()) {
                    throw new Asn1Exception("Error en el elemento " + i + " (" + this.elementsTypes[i].getElementType().getName() + ") de la secuencia ASN.1", e);
                }
            }
        }
    }

    @Override // es.gob.jmulticard.asn1.DecoderObject
    protected byte getDefaultTag() {
        return (byte) 48;
    }

    public final DecoderObject getElementAt(int index) {
        return this.elements.get(index);
    }
}
