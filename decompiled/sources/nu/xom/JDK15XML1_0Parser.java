package nu.xom;

import com.sun.org.apache.xerces.internal.parsers.DTDConfiguration;
import com.sun.org.apache.xerces.internal.parsers.SAXParser;
import com.sun.org.apache.xerces.internal.util.SecurityManager;
import org.xml.sax.SAXException;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/JDK15XML1_0Parser.class */
class JDK15XML1_0Parser extends SAXParser {
    JDK15XML1_0Parser() throws SAXException {
        super(new DTDConfiguration());
        setProperty("http://apache.org/xml/properties/security-manager", new SecurityManager());
    }
}
