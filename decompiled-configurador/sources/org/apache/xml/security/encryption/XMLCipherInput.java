package org.apache.xml.security.encryption;

import java.io.IOException;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.signature.XMLSignatureInput;
import org.apache.xml.security.transforms.TransformationException;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.utils.resolver.ResourceResolver;
import org.apache.xml.security.utils.resolver.ResourceResolverContext;
import org.apache.xml.security.utils.resolver.ResourceResolverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/apache/xml/security/encryption/XMLCipherInput.class */
public class XMLCipherInput {
    private static final Logger LOG = LoggerFactory.getLogger((Class<?>) XMLCipherInput.class);
    private CipherData cipherData;
    private boolean secureValidation;

    public XMLCipherInput(CipherData data) throws XMLEncryptionException {
        this.secureValidation = true;
        this.cipherData = data;
        if (this.cipherData == null) {
            throw new XMLEncryptionException("CipherData is null");
        }
    }

    public XMLCipherInput(EncryptedType input) throws XMLEncryptionException {
        this(input == null ? null : input.getCipherData());
    }

    public void setSecureValidation(boolean secureValidation) {
        this.secureValidation = secureValidation;
    }

    public byte[] getBytes() throws XMLEncryptionException {
        return getDecryptBytes();
    }

    private byte[] getDecryptBytes() throws XMLEncryptionException {
        if (this.cipherData.getDataType() == 2) {
            LOG.debug("Found a reference type CipherData");
            CipherReference cr = this.cipherData.getCipherReference();
            Attr uriAttr = cr.getURIAsAttr();
            try {
                ResourceResolverContext resolverContext = new ResourceResolverContext(uriAttr, null, this.secureValidation);
                if (resolverContext.isURISafeToResolve()) {
                    XMLSignatureInput input = ResourceResolver.resolve(resolverContext);
                    if (input != null) {
                        LOG.debug("Managed to resolve URI \"{}\"", cr.getURI());
                        Transforms transforms = cr.getTransforms();
                        if (transforms != null) {
                            LOG.debug("Have transforms in cipher reference");
                            try {
                                org.apache.xml.security.transforms.Transforms dsTransforms = transforms.getDSTransforms();
                                dsTransforms.setSecureValidation(this.secureValidation);
                                input = dsTransforms.performTransforms(input);
                            } catch (TransformationException ex) {
                                throw new XMLEncryptionException(ex);
                            }
                        }
                        try {
                            return input.getBytes();
                        } catch (IOException | CanonicalizationException ex2) {
                            throw new XMLEncryptionException(ex2);
                        }
                    }
                    LOG.debug("Failed to resolve URI \"{}\"", cr.getURI());
                    throw new XMLEncryptionException();
                }
                String uriToResolve = uriAttr != null ? uriAttr.getValue() : null;
                Object[] exArgs = new Object[2];
                exArgs[0] = uriToResolve != null ? uriToResolve : "null";
                exArgs[1] = null;
                throw new ResourceResolverException("utils.resolver.noClass", exArgs, uriToResolve, (String) null);
            } catch (ResourceResolverException ex3) {
                throw new XMLEncryptionException(ex3);
            }
        }
        if (this.cipherData.getDataType() == 1) {
            String base64EncodedEncryptedOctets = this.cipherData.getCipherValue().getValue();
            LOG.debug("Encrypted octets:\n{}", base64EncodedEncryptedOctets);
            return XMLUtils.decode(base64EncodedEncryptedOctets);
        }
        throw new XMLEncryptionException("CipherData.getDataType() returned unexpected value");
    }
}
