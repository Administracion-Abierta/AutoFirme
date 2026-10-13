package es.uji.crypto.xades.jxades.security.xml.XAdES;

import es.uji.crypto.xades.jxades.util.SystemUtils;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Date;
import javax.security.auth.x500.X500Principal;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/uji/crypto/xades/jxades/security/xml/XAdES/CRLIdentifier.class */
public class CRLIdentifier extends XAdESStructure {
    private URI crlURI;
    private X500Principal issuer;
    private Date issueTime;
    private BigInteger crlNumber;

    public CRLIdentifier(Node node, String xadesPrefix, String xadesNamespace, String xmlSignaturePrefix) {
        super(node, xadesPrefix, xadesNamespace, xmlSignaturePrefix);
    }

    public X500Principal getIssuer() {
        String value;
        if (this.issuer == null && (value = getChildElementTextContent("Issuer")) != null) {
            this.issuer = new X500Principal(value);
        }
        return this.issuer;
    }

    public Date getIssueTime() throws ParseException {
        String value;
        if (this.issueTime == null && (value = getChildElementTextContent("IssueTime")) != null) {
            this.issueTime = SystemUtils.parseDate(value);
        }
        return this.issueTime;
    }

    public BigInteger getCRLNumber() {
        String value;
        if (this.crlNumber == null && (value = getChildElementTextContent("Number")) != null) {
            this.crlNumber = new BigInteger(value);
        }
        return this.crlNumber;
    }

    public URI getCrlURI() throws URISyntaxException {
        String value;
        if (this.crlURI == null && (value = getAttribute("URI")) != null) {
            this.crlURI = new URI(value);
        }
        return this.crlURI;
    }
}
