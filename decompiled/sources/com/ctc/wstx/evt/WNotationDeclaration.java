package com.ctc.wstx.evt;

import java.net.URL;
import javax.xml.stream.Location;
import org.codehaus.stax2.ri.evt.NotationDeclarationEventImpl;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/evt/WNotationDeclaration.class */
public class WNotationDeclaration extends NotationDeclarationEventImpl {
    final URL _baseURL;

    public WNotationDeclaration(Location loc, String name, String pubId, String sysId, URL baseURL) {
        super(loc, name, pubId, sysId);
        this._baseURL = baseURL;
    }

    @Override // org.codehaus.stax2.ri.evt.NotationDeclarationEventImpl, org.codehaus.stax2.evt.NotationDeclaration2
    public String getBaseURI() {
        if (this._baseURL == null) {
            return super.getBaseURI();
        }
        return this._baseURL.toExternalForm();
    }
}
