package es.gob.jmulticard.asn1.der;

import es.gob.jmulticard.asn1.Asn1Exception;
import es.gob.jmulticard.asn1.DecoderObject;
import es.gob.jmulticard.asn1.OptionalDecoderObjectElement;
import es.gob.jmulticard.asn1.Tlv;
import es.gob.jmulticard.asn1.TlvException;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/der/Record.class */
public abstract class Record extends DecoderObject {
    private final OptionalDecoderObjectElement[] elementsTypes;
    private final List<DecoderObject> elements = new ArrayList();

    protected Record(OptionalDecoderObjectElement... types) {
        if (types == null || types.length == 0) {
            throw new IllegalArgumentException("Los tipos de los elementos del registro no pueden ser nulos ni vacios");
        }
        this.elementsTypes = new OptionalDecoderObjectElement[types.length];
        System.arraycopy(types, 0, this.elementsTypes, 0, types.length);
    }

    protected final int getElementCount() {
        return this.elements.size();
    }

    protected final DecoderObject getElementAt(int pos) {
        if (pos < 0 || pos >= this.elements.size()) {
            throw new IndexOutOfBoundsException("No existe un elemento en este registro en el indice " + Integer.toString(pos));
        }
        return this.elements.get(pos);
    }

    @Override // es.gob.jmulticard.asn1.DecoderObject
    protected final void decodeValue() throws TlvException, Asn1Exception {
        if (getBytes().length == 0) {
            throw new Asn1Exception("El valor del objeto ASN.1 esta vacio");
        }
        int offset = 0;
        for (int i = 0; i < this.elementsTypes.length; i++) {
            try {
                byte[] remainingBytes = new byte[getBytes().length - offset];
                System.arraycopy(getBytes(), offset, remainingBytes, 0, remainingBytes.length);
                Tlv tlv = new Tlv(remainingBytes);
                try {
                    DecoderObject tmpDo = this.elementsTypes[i].getElementType().getConstructor(new Class[0]).newInstance(new Object[0]);
                    tmpDo.checkTag(tlv.getTag());
                    offset += tlv.getBytes().length;
                    tmpDo.setDerValue(tlv.getBytes());
                    this.elements.add(tmpDo);
                } catch (Exception e) {
                    throw new Asn1Exception("No se ha podido instanciar un " + this.elementsTypes[i].getElementType().getName() + " en la posicion " + Integer.toString(i) + " del registro", e);
                }
            } catch (Exception e2) {
                if (!this.elementsTypes[i].isOptional()) {
                    throw new Asn1Exception("Error en el elemento " + i + " del registro ASN.1", e2);
                }
            }
        }
    }

    @Override // es.gob.jmulticard.asn1.DecoderObject
    protected byte getDefaultTag() {
        throw new UnsupportedOperationException("No hay tipo por defecto");
    }
}
