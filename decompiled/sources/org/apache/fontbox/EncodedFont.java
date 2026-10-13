package org.apache.fontbox;

import java.io.IOException;
import org.apache.fontbox.encoding.Encoding;

/* JADX INFO: loaded from: autofirma.jar:org/apache/fontbox/EncodedFont.class */
public interface EncodedFont {
    Encoding getEncoding() throws IOException;
}
