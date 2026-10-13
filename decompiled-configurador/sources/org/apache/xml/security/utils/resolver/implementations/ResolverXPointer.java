package org.apache.xml.security.utils.resolver.implementations;

import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.utils.resolver.ResourceResolverContext;
import org.apache.xml.security.utils.resolver.ResourceResolverException;
import org.apache.xml.security.utils.resolver.ResourceResolverSpi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/utils/resolver/implementations/ResolverXPointer.class */
public class ResolverXPointer extends ResourceResolverSpi {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) ResolverXPointer.class);
    private static final String XP = "#xpointer(id(";
    private static final int XP_LENGTH = XP.length();

    @Override // org.apache.xml.security.utils.resolver.ResourceResolverSpi
    public XMLSignatureInput engineResolveURI(ResourceResolverContext context) throws ResourceResolverException {
        Node resultNode = null;
        Document doc = context.attr.getOwnerElement().getOwnerDocument();
        if (isXPointerSlash(context.uriToResolve)) {
            resultNode = doc;
        } else if (isXPointerId(context.uriToResolve)) {
            String id = getXPointerId(context.uriToResolve);
            resultNode = doc.getElementById(id);
            if (context.secureValidation) {
                Element start = context.attr.getOwnerDocument().getDocumentElement();
                if (!XMLUtils.protectAgainstWrappingAttack(start, id)) {
                    Object[] exArgs = {id};
                    throw new ResourceResolverException("signature.Verification.MultipleIDs", exArgs, context.uriToResolve, context.baseUri);
                }
            }
            if (resultNode == null) {
                Object[] exArgs2 = {id};
                throw new ResourceResolverException("signature.Verification.MissingID", exArgs2, context.uriToResolve, context.baseUri);
            }
        }
        XMLSignatureInput result = new XMLSignatureInput(resultNode);
        result.setSecureValidation(context.secureValidation);
        result.setMIMEType("text/xml");
        if (context.baseUri != null && context.baseUri.length() > 0) {
            result.setSourceURI(context.baseUri.concat(context.uriToResolve));
        } else {
            result.setSourceURI(context.uriToResolve);
        }
        return result;
    }

    @Override // org.apache.xml.security.utils.resolver.ResourceResolverSpi
    public boolean engineCanResolveURI(ResourceResolverContext context) {
        return isXPointerSlash(context.uriToResolve) || isXPointerId(context.uriToResolve);
    }

    private static boolean isXPointerSlash(String uri) {
        return "#xpointer(/)".equals(uri);
    }

    private static boolean isXPointerId(String uri) {
        if (uri != null && uri.startsWith(XP) && uri.endsWith("))")) {
            String idPlusDelim = uri.substring(XP_LENGTH, uri.length() - 2);
            int idLen = idPlusDelim.length() - 1;
            if ((idPlusDelim.charAt(0) == '\"' && idPlusDelim.charAt(idLen) == '\"') || (idPlusDelim.charAt(0) == '\'' && idPlusDelim.charAt(idLen) == '\'')) {
                LOG.debug("Id = {}", idPlusDelim.substring(1, idLen));
                return true;
            }
            return false;
        }
        return false;
    }

    private static String getXPointerId(String uri) {
        if (uri.startsWith(XP) && uri.endsWith("))")) {
            String idPlusDelim = uri.substring(XP_LENGTH, uri.length() - 2);
            int idLen = idPlusDelim.length() - 1;
            if ((idPlusDelim.charAt(0) == '\"' && idPlusDelim.charAt(idLen) == '\"') || (idPlusDelim.charAt(0) == '\'' && idPlusDelim.charAt(idLen) == '\'')) {
                return idPlusDelim.substring(1, idLen);
            }
            return null;
        }
        return null;
    }
}
