package org.spongycastle.util;

import java.util.Collection;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/util/StreamParser.class */
public interface StreamParser {
    Object read() throws StreamParsingException;

    Collection readAll() throws StreamParsingException;
}
