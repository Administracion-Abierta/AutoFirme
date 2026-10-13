package org.bouncycastle.util;

import java.util.Collection;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/util/StreamParser.class */
public interface StreamParser {
    Object read() throws StreamParsingException;

    Collection readAll() throws StreamParsingException;
}
