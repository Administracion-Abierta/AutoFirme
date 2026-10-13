package com.ctc.wstx.ent;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.io.DefaultInputResolver;
import com.ctc.wstx.io.WstxInputSource;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import javax.xml.stream.Location;
import javax.xml.stream.XMLResolver;
import javax.xml.stream.XMLStreamException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/ent/ParsedExtEntity.class */
public class ParsedExtEntity extends ExtEntity {
    public ParsedExtEntity(Location loc, String name, URL ctxt, String pubId, String sysId) {
        super(loc, name, ctxt, pubId, sysId);
    }

    @Override // com.ctc.wstx.ent.ExtEntity, com.ctc.wstx.ent.EntityDecl, com.ctc.wstx.evt.WEntityDeclaration
    public String getNotationName() {
        return null;
    }

    @Override // com.ctc.wstx.ent.ExtEntity, com.ctc.wstx.ent.EntityDecl, com.ctc.wstx.evt.WEntityDeclaration
    public void writeEnc(Writer w) throws IOException {
        w.write("<!ENTITY ");
        w.write(this.mName);
        String pubId = getPublicId();
        if (pubId != null) {
            w.write("PUBLIC \"");
            w.write(pubId);
            w.write("\" ");
        } else {
            w.write("SYSTEM ");
        }
        w.write(34);
        w.write(getSystemId());
        w.write("\">");
    }

    @Override // com.ctc.wstx.ent.ExtEntity, com.ctc.wstx.ent.EntityDecl
    public boolean isParsed() {
        return true;
    }

    @Override // com.ctc.wstx.ent.ExtEntity, com.ctc.wstx.ent.EntityDecl
    public WstxInputSource expand(WstxInputSource parent, XMLResolver res, ReaderConfig cfg, int xmlVersion) throws XMLStreamException, IOException {
        if (xmlVersion == 0) {
            xmlVersion = 256;
        }
        return DefaultInputResolver.resolveEntity(parent, this.mContext, this.mName, getPublicId(), getSystemId(), res, cfg, xmlVersion);
    }
}
