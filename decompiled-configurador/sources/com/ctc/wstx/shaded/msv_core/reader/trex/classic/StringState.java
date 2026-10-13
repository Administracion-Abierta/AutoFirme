package com.ctc.wstx.shaded.msv_core.reader.trex.classic;

import com.ctc.wstx.shaded.msv_core.datatype.xsd.StringType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.TokenType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.WhiteSpaceProcessor;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.ctc.wstx.shaded.msv_core.grammar.Expression;
import com.ctc.wstx.shaded.msv_core.reader.ExpressionWithoutChildState;
import com.ctc.wstx.shaded.msv_core.util.StringPair;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv_core/reader/trex/classic/StringState.class */
public class StringState extends ExpressionWithoutChildState {
    protected final StringBuffer text = new StringBuffer();

    @Override // com.ctc.wstx.shaded.msv_core.reader.State, org.xml.sax.ContentHandler
    public void characters(char[] buf, int from, int len) {
        this.text.append(buf, from, len);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.State, org.xml.sax.ContentHandler
    public void ignorableWhitespace(char[] buf, int from, int len) {
        this.text.append(buf, from, len);
    }

    @Override // com.ctc.wstx.shaded.msv_core.reader.ExpressionState
    protected Expression makeExpression() {
        if ("preserve".equals(this.startTag.getAttribute(XSDatatype.FACET_WHITESPACE))) {
            return this.reader.pool.createValue(StringType.theInstance, new StringPair("", "string"), this.text.toString());
        }
        return this.reader.pool.createValue(TokenType.theInstance, new StringPair("", "token"), WhiteSpaceProcessor.collapse(this.text.toString()));
    }
}
