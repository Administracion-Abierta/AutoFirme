package org.codehaus.stax2.io;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/codehaus/stax2/io/EscapingWriterFactory.class */
public interface EscapingWriterFactory {
    Writer createEscapingWriterFor(Writer writer, String str) throws UnsupportedEncodingException;

    Writer createEscapingWriterFor(OutputStream outputStream, String str) throws UnsupportedEncodingException;
}
