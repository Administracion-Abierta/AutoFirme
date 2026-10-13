package com.ctc.wstx.ent;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.evt.WEntityDeclaration;
import com.ctc.wstx.io.WstxInputSource;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import javax.xml.stream.Location;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/ent/EntityDecl.class */
public abstract class EntityDecl extends WEntityDeclaration {
    final String mName;
    final URL mContext;
    protected boolean mDeclaredExternally;

    @Override // com.ctc.wstx.evt.WEntityDeclaration
    public abstract String getNotationName();

    @Override // com.ctc.wstx.evt.WEntityDeclaration
    public abstract String getPublicId();

    @Override // com.ctc.wstx.evt.WEntityDeclaration
    public abstract String getReplacementText();

    public abstract int getReplacementText(Writer writer) throws IOException;

    @Override // com.ctc.wstx.evt.WEntityDeclaration
    public abstract String getSystemId();

    @Override // com.ctc.wstx.evt.WEntityDeclaration
    public abstract void writeEnc(Writer writer) throws IOException;

    public abstract char[] getReplacementChars();

    public abstract boolean isExternal();

    public abstract boolean isParsed();

    public abstract WstxInputSource expand(WstxInputSource wstxInputSource, XMLResolver xMLResolver, ReaderConfig readerConfig, int i) throws XMLStreamException, IOException;

    public EntityDecl(Location loc, String name, URL ctxt) {
        super(loc);
        this.mDeclaredExternally = false;
        this.mName = name;
        this.mContext = ctxt;
    }

    public void markAsExternallyDeclared() {
        this.mDeclaredExternally = true;
    }

    @Override // com.ctc.wstx.evt.WEntityDeclaration
    public final String getBaseURI() {
        return this.mContext.toExternalForm();
    }

    @Override // com.ctc.wstx.evt.WEntityDeclaration
    public final String getName() {
        return this.mName;
    }

    public boolean wasDeclaredExternally() {
        return this.mDeclaredExternally;
    }

    public final int getReplacementTextLength() {
        String str = getReplacementText();
        if (str == null) {
            return 0;
        }
        return str.length();
    }
}
