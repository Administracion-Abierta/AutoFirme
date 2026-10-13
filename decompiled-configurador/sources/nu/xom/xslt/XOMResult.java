package nu.xom.xslt;

import javax.xml.transform.sax.SAXResult;
import nu.xom.NodeFactory;
import nu.xom.Nodes;
import org.xml.sax.ext.LexicalHandler;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:nu/xom/xslt/XOMResult.class */
class XOMResult extends SAXResult {
    XOMResult(NodeFactory nodeFactory) {
        super(new XSLTHandler(nodeFactory));
        setLexicalHandler((LexicalHandler) getHandler());
    }

    public Nodes getResult() {
        return ((XSLTHandler) getHandler()).getResult();
    }
}
