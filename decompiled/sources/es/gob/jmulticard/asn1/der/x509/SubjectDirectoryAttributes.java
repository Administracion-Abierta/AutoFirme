package es.gob.jmulticard.asn1.der.x509;

import es.gob.jmulticard.asn1.Asn1Exception;
import es.gob.jmulticard.asn1.OptionalDecoderObjectElement;
import es.gob.jmulticard.asn1.TlvException;
import es.gob.jmulticard.asn1.der.GeneralizedTime;
import es.gob.jmulticard.asn1.der.OctectString;
import es.gob.jmulticard.asn1.der.Sequence;
import java.util.Date;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/der/x509/SubjectDirectoryAttributes.class */
public final class SubjectDirectoryAttributes extends Sequence {
    public SubjectDirectoryAttributes() {
        super(new OptionalDecoderObjectElement(Attribute.class, false));
    }

    @Override // es.gob.jmulticard.asn1.DecoderObject
    public void setDerValue(byte[] byteValue) throws TlvException, Asn1Exception {
        OctectString os = new OctectString();
        os.setDerValue(byteValue);
        super.setDerValue(os.getOctectStringByteValue());
    }

    public String toString() {
        return getElementAt(0).toString();
    }

    public Date getSubjectBirthDate() throws Asn1Exception {
        return ((GeneralizedTime) ((Attribute) getElementAt(0)).getAttributeValues().getElementAt(0)).getDateValue();
    }
}
