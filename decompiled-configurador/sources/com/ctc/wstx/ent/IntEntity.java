package com.ctc.wstx.ent;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.io.InputSourceFactory;
import com.ctc.wstx.io.TextEscaper;
import com.ctc.wstx.io.WstxInputLocation;
import com.ctc.wstx.io.WstxInputSource;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import javax.xml.stream.Location;
import javax.xml.stream.XMLResolver;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/ent/IntEntity.class */
public class IntEntity extends EntityDecl {
    protected final Location mContentLocation;
    final char[] mRepl;
    String mReplText;

    public IntEntity(Location loc, String name, URL ctxt, char[] repl, Location defLoc) {
        super(loc, name, ctxt);
        this.mReplText = null;
        this.mRepl = repl;
        this.mContentLocation = defLoc;
    }

    public static IntEntity create(String id, String repl) {
        return create(id, repl.toCharArray());
    }

    public static IntEntity create(String id, char[] val) {
        WstxInputLocation loc = WstxInputLocation.getEmptyLocation();
        return new IntEntity(loc, id, null, val, loc);
    }

    @Override // com.ctc.wstx.ent.EntityDecl, com.ctc.wstx.evt.WEntityDeclaration
    public String getNotationName() {
        return null;
    }

    @Override // com.ctc.wstx.ent.EntityDecl, com.ctc.wstx.evt.WEntityDeclaration
    public String getPublicId() {
        return null;
    }

    @Override // com.ctc.wstx.ent.EntityDecl, com.ctc.wstx.evt.WEntityDeclaration
    public String getReplacementText() {
        String repl = this.mReplText;
        if (repl == null) {
            String repl2 = this.mRepl.length == 0 ? "" : new String(this.mRepl);
            this.mReplText = repl2;
        }
        return this.mReplText;
    }

    @Override // com.ctc.wstx.ent.EntityDecl
    public int getReplacementText(Writer w) throws IOException {
        w.write(this.mRepl);
        return this.mRepl.length;
    }

    @Override // com.ctc.wstx.ent.EntityDecl, com.ctc.wstx.evt.WEntityDeclaration
    public String getSystemId() {
        return null;
    }

    @Override // com.ctc.wstx.ent.EntityDecl, com.ctc.wstx.evt.WEntityDeclaration
    public void writeEnc(Writer w) throws IOException {
        w.write("<!ENTITY ");
        w.write(this.mName);
        w.write(" \"");
        TextEscaper.outputDTDText(w, this.mRepl, 0, this.mRepl.length);
        w.write("\">");
    }

    @Override // com.ctc.wstx.ent.EntityDecl
    public char[] getReplacementChars() {
        return this.mRepl;
    }

    @Override // com.ctc.wstx.ent.EntityDecl
    public boolean isExternal() {
        return false;
    }

    @Override // com.ctc.wstx.ent.EntityDecl
    public boolean isParsed() {
        return true;
    }

    @Override // com.ctc.wstx.ent.EntityDecl
    public WstxInputSource expand(WstxInputSource parent, XMLResolver res, ReaderConfig cfg, int xmlVersion) {
        return InputSourceFactory.constructCharArraySource(parent, this.mName, this.mRepl, 0, this.mRepl.length, this.mContentLocation, null);
    }
}
