package org.apache.xml.security.keys.keyresolver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import javax.crypto.SecretKey;
import org.apache.xml.security.keys.storage.StorageResolver;
import org.apache.xml.security.parser.XMLParserException;
import org.apache.xml.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/* JADX INFO: loaded from: autofirma.jar:org/apache/xml/security/keys/keyresolver/KeyResolverSpi.class */
public abstract class KeyResolverSpi {
    protected abstract boolean engineCanResolve(Element element, String str, StorageResolver storageResolver);

    protected abstract PublicKey engineResolvePublicKey(Element element, String str, StorageResolver storageResolver, boolean z) throws KeyResolverException;

    protected abstract X509Certificate engineResolveX509Certificate(Element element, String str, StorageResolver storageResolver, boolean z) throws KeyResolverException;

    protected abstract SecretKey engineResolveSecretKey(Element element, String str, StorageResolver storageResolver, boolean z) throws KeyResolverException;

    protected abstract PrivateKey engineResolvePrivateKey(Element element, String str, StorageResolver storageResolver, boolean z) throws KeyResolverException;

    public PublicKey engineLookupAndResolvePublicKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        if (!engineCanResolve(element, baseURI, storage)) {
            return null;
        }
        return engineResolvePublicKey(element, baseURI, storage, secureValidation);
    }

    public X509Certificate engineLookupResolveX509Certificate(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        if (!engineCanResolve(element, baseURI, storage)) {
            return null;
        }
        return engineResolveX509Certificate(element, baseURI, storage, secureValidation);
    }

    public SecretKey engineLookupAndResolveSecretKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        if (!engineCanResolve(element, baseURI, storage)) {
            return null;
        }
        return engineResolveSecretKey(element, baseURI, storage, secureValidation);
    }

    public PrivateKey engineLookupAndResolvePrivateKey(Element element, String baseURI, StorageResolver storage, boolean secureValidation) throws KeyResolverException {
        if (!engineCanResolve(element, baseURI, storage)) {
            return null;
        }
        return engineResolvePrivateKey(element, baseURI, storage, secureValidation);
    }

    protected static Element getDocFromBytes(byte[] bytes, boolean secureValidation) throws KeyResolverException {
        try {
            InputStream is = new ByteArrayInputStream(bytes);
            Throwable th = null;
            try {
                Document doc = XMLUtils.read(is, secureValidation);
                Element documentElement = doc.getDocumentElement();
                if (0 != 0) {
                    try {
                        is.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                } else {
                    is.close();
                }
                return documentElement;
            } catch (Throwable th3) {
                if (0 != 0) {
                    try {
                        is.close();
                    } catch (Throwable th4) {
                        th.addSuppressed(th4);
                    }
                } else {
                    is.close();
                }
                throw th3;
            }
        } catch (IOException ex) {
            throw new KeyResolverException(ex);
        } catch (XMLParserException ex2) {
            throw new KeyResolverException(ex2);
        }
    }
}
