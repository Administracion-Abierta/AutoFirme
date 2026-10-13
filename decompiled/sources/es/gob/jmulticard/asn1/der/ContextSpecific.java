package es.gob.jmulticard.asn1.der;

import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.asn1.Asn1Exception;
import es.gob.jmulticard.asn1.DecoderObject;
import es.gob.jmulticard.asn1.Tlv;
import es.gob.jmulticard.asn1.TlvException;
import java.lang.reflect.InvocationTargetException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/der/ContextSpecific.class */
public abstract class ContextSpecific extends DecoderObject {
    private DecoderObject object = null;
    private final Class<? extends DecoderObject> elementType;

    protected final DecoderObject getObject() {
        if (this.object == null) {
            throw new IllegalStateException();
        }
        return this.object;
    }

    @Override // es.gob.jmulticard.asn1.DecoderObject
    protected void decodeValue() throws TlvException, Asn1Exception {
        try {
            DecoderObject tmpDo = this.elementType.getConstructor(new Class[0]).newInstance(new Object[0]);
            Tlv tlv = new Tlv(getBytes());
            tmpDo.setDerValue(tlv.getValue());
            this.object = tmpDo;
        } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
            throw new Asn1Exception("No se ha podido instanciar un " + this.elementType.getName() + " en el contexto especifico", e);
        }
    }

    protected ContextSpecific(Class<? extends DecoderObject> type) {
        if (type == null) {
            throw new IllegalArgumentException("El tipo contenido dentro de ContextSpecific no puede ser nulo");
        }
        this.elementType = type;
    }

    @Override // es.gob.jmulticard.asn1.DecoderObject
    protected byte getDefaultTag() {
        throw new UnsupportedOperationException("No hay tipo por defecto");
    }

    @Override // es.gob.jmulticard.asn1.DecoderObject
    public void checkTag(byte tag) throws Asn1Exception {
        if ((tag & 192) != 128) {
            throw new Asn1Exception("La etiqueta " + HexUtils.hexify(new byte[]{tag}, false) + " no es valida para un objeto especifico del contexto");
        }
    }
}
