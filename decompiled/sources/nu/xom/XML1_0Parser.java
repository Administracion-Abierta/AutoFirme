package nu.xom;

import org.apache.xerces.parsers.DTDConfiguration;
import org.apache.xerces.parsers.SAXParser;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/XML1_0Parser.class */
class XML1_0Parser extends SAXParser {
    XML1_0Parser() {
        super(new DTDConfiguration());
    }
}
