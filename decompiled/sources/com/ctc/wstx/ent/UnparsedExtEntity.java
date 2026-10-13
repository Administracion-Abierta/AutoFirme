package com.ctc.wstx.ent;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.io.WstxInputSource;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import javax.xml.stream.Location;
import javax.xml.stream.XMLResolver;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/ent/UnparsedExtEntity.class */
public class UnparsedExtEntity extends ExtEntity {
    final String mNotationId;

    public UnparsedExtEntity(Location loc, String name, URL ctxt, String pubId, String sysId, String notationId) {
        super(loc, name, ctxt, pubId, sysId);
        this.mNotationId = notationId;
    }

    @Override // com.ctc.wstx.ent.ExtEntity, com.ctc.wstx.ent.EntityDecl, com.ctc.wstx.evt.WEntityDeclaration
    public String getNotationName() {
        return this.mNotationId;
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
        w.write("\" NDATA ");
        w.write(this.mNotationId);
        w.write(62);
    }

    @Override // com.ctc.wstx.ent.ExtEntity, com.ctc.wstx.ent.EntityDecl
    public boolean isParsed() {
        return false;
    }

    @Override // com.ctc.wstx.ent.ExtEntity, com.ctc.wstx.ent.EntityDecl
    public WstxInputSource expand(WstxInputSource parent, XMLResolver res, ReaderConfig cfg, int xmlVersion) {
        throw new IllegalStateException("Internal error: createInputSource() called for unparsed (external) entity.");
    }
}
