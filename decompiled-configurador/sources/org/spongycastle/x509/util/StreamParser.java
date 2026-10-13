package org.spongycastle.x509.util;

import java.util.Collection;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/x509/util/StreamParser.class */
public interface StreamParser {
    Object read() throws StreamParsingException;

    Collection readAll() throws StreamParsingException;
}
