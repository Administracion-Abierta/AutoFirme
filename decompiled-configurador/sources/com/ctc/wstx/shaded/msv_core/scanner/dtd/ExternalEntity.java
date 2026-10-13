package com.ctc.wstx.shaded.msv_core.scanner.dtd;

import java.io.IOException;
import java.net.URL;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/scanner/dtd/ExternalEntity.class */
class ExternalEntity extends EntityDecl {
    String systemId;
    String publicId;
    String notation;

    public ExternalEntity(InputEntity in) {
    }

    public InputSource getInputSource(EntityResolver r) throws SAXException, IOException {
        InputSource retval = r.resolveEntity(this.publicId, this.systemId);
        if (retval == null) {
            retval = Resolver.createInputSource(new URL(this.systemId), false);
        }
        return retval;
    }
}
