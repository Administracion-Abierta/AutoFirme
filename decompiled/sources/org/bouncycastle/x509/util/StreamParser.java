package org.bouncycastle.x509.util;

import java.util.Collection;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/x509/util/StreamParser.class */
public interface StreamParser {
    Object read() throws StreamParsingException;

    Collection readAll() throws StreamParsingException;
}
