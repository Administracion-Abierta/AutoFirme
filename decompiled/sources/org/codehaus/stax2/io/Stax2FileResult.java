package org.codehaus.stax2.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/* JADX INFO: loaded from: autofirma.jar:org/codehaus/stax2/io/Stax2FileResult.class */
public class Stax2FileResult extends Stax2ReferentialResult {
    final File mFile;

    public Stax2FileResult(File f) {
        this.mFile = f;
    }

    @Override // org.codehaus.stax2.io.Stax2ReferentialResult, org.codehaus.stax2.io.Stax2Result
    public Writer constructWriter() throws IOException {
        String enc = getEncoding();
        if (enc != null && enc.length() > 0) {
            return new OutputStreamWriter(constructOutputStream(), enc);
        }
        return new FileWriter(this.mFile);
    }

    @Override // org.codehaus.stax2.io.Stax2ReferentialResult, org.codehaus.stax2.io.Stax2Result
    public OutputStream constructOutputStream() throws IOException {
        return new FileOutputStream(this.mFile);
    }

    public File getFile() {
        return this.mFile;
    }
}
