package org.codehaus.stax2.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

/* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/io/Stax2BlockSource.class */
public abstract class Stax2BlockSource extends Stax2Source {
    @Override // org.codehaus.stax2.io.Stax2Source
    public abstract Reader constructReader() throws IOException;

    @Override // org.codehaus.stax2.io.Stax2Source
    public abstract InputStream constructInputStream() throws IOException;

    protected Stax2BlockSource() {
    }

    @Override // org.codehaus.stax2.io.Stax2Source
    public URL getReference() {
        return null;
    }
}
