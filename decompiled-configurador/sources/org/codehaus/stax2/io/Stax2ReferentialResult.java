package org.codehaus.stax2.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/codehaus/stax2/io/Stax2ReferentialResult.class */
public abstract class Stax2ReferentialResult extends Stax2Result {
    @Override // org.codehaus.stax2.io.Stax2Result
    public abstract Writer constructWriter() throws IOException;

    @Override // org.codehaus.stax2.io.Stax2Result
    public abstract OutputStream constructOutputStream() throws IOException;

    protected Stax2ReferentialResult() {
    }
}
