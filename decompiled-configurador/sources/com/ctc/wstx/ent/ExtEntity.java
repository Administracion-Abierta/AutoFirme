package com.ctc.wstx.ent;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.io.WstxInputSource;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import javax.xml.stream.Location;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/ent/ExtEntity.class */
public abstract class ExtEntity extends EntityDecl {
    final String mPublicId;
    final String mSystemId;

    @Override // com.ctc.wstx.ent.EntityDecl, com.ctc.wstx.evt.WEntityDeclaration
    public abstract String getNotationName();

    @Override // com.ctc.wstx.ent.EntityDecl, com.ctc.wstx.evt.WEntityDeclaration
    public abstract void writeEnc(Writer writer) throws IOException;

    @Override // com.ctc.wstx.ent.EntityDecl
    public abstract boolean isParsed();

    @Override // com.ctc.wstx.ent.EntityDecl
    public abstract WstxInputSource expand(WstxInputSource wstxInputSource, XMLResolver xMLResolver, ReaderConfig readerConfig, int i) throws XMLStreamException, IOException;

    public ExtEntity(Location loc, String name, URL ctxt, String pubId, String sysId) {
        super(loc, name, ctxt);
        this.mPublicId = pubId;
        this.mSystemId = sysId;
    }

    @Override // com.ctc.wstx.ent.EntityDecl, com.ctc.wstx.evt.WEntityDeclaration
    public String getPublicId() {
        return this.mPublicId;
    }

    @Override // com.ctc.wstx.ent.EntityDecl, com.ctc.wstx.evt.WEntityDeclaration
    public String getReplacementText() {
        return null;
    }

    @Override // com.ctc.wstx.ent.EntityDecl
    public int getReplacementText(Writer w) {
        return 0;
    }

    @Override // com.ctc.wstx.ent.EntityDecl, com.ctc.wstx.evt.WEntityDeclaration
    public String getSystemId() {
        return this.mSystemId;
    }

    @Override // com.ctc.wstx.ent.EntityDecl
    public char[] getReplacementChars() {
        return null;
    }

    @Override // com.ctc.wstx.ent.EntityDecl
    public boolean isExternal() {
        return true;
    }
}
