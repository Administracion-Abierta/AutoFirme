package nu.xom.xslt;

import nu.xom.Nodes;
import org.xml.sax.InputSource;

/* JADX INFO: loaded from: autofirma.jar:nu/xom/xslt/XOMInputSource.class */
class XOMInputSource extends InputSource {
    private final Nodes nodes;

    XOMInputSource(Nodes nodes) {
        this.nodes = nodes;
    }

    Nodes getNodes() {
        return this.nodes;
    }
}
