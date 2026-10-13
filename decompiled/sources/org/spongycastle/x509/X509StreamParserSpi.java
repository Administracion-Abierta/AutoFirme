package org.spongycastle.x509;

import java.io.InputStream;
import java.util.Collection;
import org.spongycastle.x509.util.StreamParsingException;

/* JADX INFO: loaded from: autofirma.jar:org/spongycastle/x509/X509StreamParserSpi.class */
public abstract class X509StreamParserSpi {
    public abstract void engineInit(InputStream inputStream);

    public abstract Object engineRead() throws StreamParsingException;

    public abstract Collection engineReadAll() throws StreamParsingException;
}
