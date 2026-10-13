package org.codehaus.stax2.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

/* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/io/Stax2ReferentialSource.class */
public abstract class Stax2ReferentialSource extends Stax2Source {
    @Override // org.codehaus.stax2.io.Stax2Source
    public abstract URL getReference();

    @Override // org.codehaus.stax2.io.Stax2Source
    public abstract Reader constructReader() throws IOException;

    @Override // org.codehaus.stax2.io.Stax2Source
    public abstract InputStream constructInputStream() throws IOException;

    protected Stax2ReferentialSource() {
    }

    @Override // org.codehaus.stax2.io.Stax2Source, javax.xml.transform.Source
    public String getSystemId() {
        String sysId = super.getSystemId();
        if (sysId == null) {
            sysId = getReference().toExternalForm();
        }
        return sysId;
    }
}
