package es.gob.jmulticard.asn1.icao;

import es.gob.jmulticard.asn1.Asn1Exception;
import es.gob.jmulticard.asn1.DecoderObject;
import es.gob.jmulticard.asn1.TlvException;
import java.io.IOException;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/asn1/icao/SubjectFacePhoto.class */
public final class SubjectFacePhoto extends DecoderObject {
    private static final byte TAG = 117;

    @Override // es.gob.jmulticard.asn1.DecoderObject
    protected void decodeValue() throws TlvException, Asn1Exception {
        checkTag(getBytes()[0]);
    }

    @Override // es.gob.jmulticard.asn1.DecoderObject
    protected byte getDefaultTag() {
        return (byte) 117;
    }

    public byte[] getSubjectPhotoAsJpeg2k() throws IOException {
        return IcaoUtils.extractJpeg2kImage(getBytes());
    }
}
