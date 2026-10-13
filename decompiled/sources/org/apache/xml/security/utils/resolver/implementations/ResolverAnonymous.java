package org.apache.xml.security.utils.resolver.implementations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.utils.resolver.ResourceResolverContext;
import org.apache.xml.security.utils.resolver.ResourceResolverException;
import org.apache.xml.security.utils.resolver.ResourceResolverSpi;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/utils/resolver/implementations/ResolverAnonymous.class */
public class ResolverAnonymous extends ResourceResolverSpi {
    private final Path resourcePath;

    public ResolverAnonymous(String filename) throws IOException {
        this(Paths.get(filename, new String[0]));
    }

    public ResolverAnonymous(Path resourcePath) {
        this.resourcePath = resourcePath;
    }

    @Override // org.apache.xml.security.utils.resolver.ResourceResolverSpi
    public XMLSignatureInput engineResolveURI(ResourceResolverContext context) throws ResourceResolverException {
        try {
            XMLSignatureInput input = new XMLSignatureInput(Files.newInputStream(this.resourcePath, new OpenOption[0]));
            input.setSecureValidation(context.secureValidation);
            return input;
        } catch (IOException e) {
            throw new ResourceResolverException(e, context.uriToResolve, context.baseUri, "generic.EmptyMessage");
        }
    }

    @Override // org.apache.xml.security.utils.resolver.ResourceResolverSpi
    public boolean engineCanResolveURI(ResourceResolverContext context) {
        return context.uriToResolve == null;
    }
}
