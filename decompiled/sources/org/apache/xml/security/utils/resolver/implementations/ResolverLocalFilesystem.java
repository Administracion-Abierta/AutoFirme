package org.apache.xml.security.utils.resolver.implementations;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.resolver.ResourceResolverContext;
import org.apache.xml.security.utils.resolver.ResourceResolverException;
import org.apache.xml.security.utils.resolver.ResourceResolverSpi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/utils/resolver/implementations/ResolverLocalFilesystem.class */
public class ResolverLocalFilesystem extends ResourceResolverSpi {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) ResolverLocalFilesystem.class);

    @Override // org.apache.xml.security.utils.resolver.ResourceResolverSpi
    public XMLSignatureInput engineResolveURI(ResourceResolverContext context) throws ResourceResolverException {
        try {
            URI uriNew = getNewURI(context.uriToResolve, context.baseUri);
            InputStream inputStream = Files.newInputStream(Paths.get(uriNew), new OpenOption[0]);
            XMLSignatureInput result = new XMLSignatureInput(inputStream);
            result.setSecureValidation(context.secureValidation);
            result.setSourceURI(uriNew.toString());
            return result;
        } catch (Exception e) {
            throw new ResourceResolverException(e, context.uriToResolve, context.baseUri, "generic.EmptyMessage");
        }
    }

    @Override // org.apache.xml.security.utils.resolver.ResourceResolverSpi
    public boolean engineCanResolveURI(ResourceResolverContext context) {
        if (context.uriToResolve == null || context.uriToResolve.isEmpty() || context.uriToResolve.charAt(0) == '#' || context.uriToResolve.startsWith("http:")) {
            return false;
        }
        try {
            LOG.debug("I was asked whether I can resolve {}", context.uriToResolve);
            if (context.uriToResolve.startsWith("file:") || context.baseUri.startsWith("file:")) {
                LOG.debug("I state that I can resolve {}", context.uriToResolve);
                return true;
            }
        } catch (Exception e) {
            LOG.debug(e.getMessage(), (Throwable) e);
        }
        LOG.debug("But I can't");
        return false;
    }

    private static URI getNewURI(String uri, String baseURI) throws URISyntaxException {
        URI newUri;
        if (baseURI == null || baseURI.length() == 0) {
            newUri = new URI(uri);
        } else {
            newUri = new URI(baseURI).resolve(uri);
        }
        if (newUri.getFragment() != null) {
            return new URI(newUri.getScheme(), newUri.getSchemeSpecificPart(), null);
        }
        return newUri;
    }
}
