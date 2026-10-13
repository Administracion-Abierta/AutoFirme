package com.ctc.wstx.shaded.msv_core.relaxns.reader.trex;

import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchema;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchemaReader;
import com.ctc.wstx.shaded.msv_core.grammar.trex.TREXGrammar;
import com.ctc.wstx.shaded.msv_core.reader.trex.classic.TREXGrammarReader;
import com.ctc.wstx.shaded.msv_core.relaxns.grammar.trex.TREXIslandSchema;
import org.xml.sax.helpers.XMLFilterImpl;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv_core/relaxns/reader/trex/TREXIslandSchemaReader.class */
public class TREXIslandSchemaReader extends XMLFilterImpl implements IslandSchemaReader {
    private final TREXGrammarReader baseReader;

    public TREXIslandSchemaReader(TREXGrammarReader baseReader) {
        this.baseReader = baseReader;
        setContentHandler(baseReader);
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchemaReader
    public final IslandSchema getSchema() {
        TREXGrammar g = this.baseReader.getResult();
        if (g == null) {
            return null;
        }
        return new TREXIslandSchema(g);
    }
}
