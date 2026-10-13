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

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/utils/resolver/implementations/ResolverFragment.class */
public class ResolverFragment extends ResourceResolverSpi {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) ResolverFragment.class);

    @Override // org.apache.xml.security.utils.resolver.ResourceResolverSpi
    public XMLSignatureInput engineResolveURI(ResourceResolverContext context) throws ResourceResolverException {
        Node selectedElem;
        Document doc = context.attr.getOwnerElement().getOwnerDocument();
        if (context.uriToResolve.isEmpty()) {
            LOG.debug("ResolverFragment with empty URI (means complete document)");
            selectedElem = doc;
        } else {
            String id = context.uriToResolve.substring(1);
            selectedElem = doc.getElementById(id);
            if (selectedElem == null) {
                Object[] exArgs = {id};
                throw new ResourceResolverException("signature.Verification.MissingID", exArgs, context.uriToResolve, context.baseUri);
            }
            if (context.secureValidation) {
                Element start = context.attr.getOwnerDocument().getDocumentElement();
                if (!XMLUtils.protectAgainstWrappingAttack(start, id)) {
                    Object[] exArgs2 = {id};
                    throw new ResourceResolverException("signature.Verification.MultipleIDs", exArgs2, context.uriToResolve, context.baseUri);
                }
            }
            LOG.debug("Try to catch an Element with ID {} and Element was {}", id, selectedElem);
        }
        XMLSignatureInput result = new XMLSignatureInput(selectedElem);
        result.setSecureValidation(context.secureValidation);
        result.setExcludeComments(true);
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
        if (context.uriToResolve == null) {
            LOG.debug("Quick fail for null uri");
            return false;
        }
        if (context.uriToResolve.isEmpty() || (context.uriToResolve.charAt(0) == '#' && !context.uriToResolve.startsWith("#xpointer("))) {
            LOG.debug("State I can resolve reference: \"{}\"", context.uriToResolve);
            return true;
        }
        LOG.debug("Do not seem to be able to resolve reference: \"{}\"", context.uriToResolve);
        return false;
    }
}
