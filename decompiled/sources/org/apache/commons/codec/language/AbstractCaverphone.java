package org.apache.commons.codec.language;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringEncoder;

/* JADX INFO: loaded from: autofirma.jar:org/apache/commons/codec/language/AbstractCaverphone.class */
public abstract class AbstractCaverphone implements StringEncoder {
    @Override // org.apache.commons.codec.Encoder
    public Object encode(Object source) throws EncoderException {
        if (!(source instanceof String)) {
            throw new EncoderException("Parameter supplied to Caverphone encode is not of type java.lang.String");
        }
        return encode((String) source);
    }

    public boolean isEncodeEqual(String str1, String str2) throws EncoderException {
        return encode(str1).equals(encode(str2));
    }
}
