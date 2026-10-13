package es.gob.jmulticard.jse.provider;

import es.gob.jmulticard.CryptoHelper;
import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.card.PrivateKeyReference;
import es.gob.jmulticard.card.dnie.Dnie;
import es.gob.jmulticard.card.dnie.DniePrivateKeyReference;
import es.gob.jmulticard.crypto.BcCryptoHelper;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreSpi;
import java.security.PrivateKey;
import java.security.ProviderException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.PasswordCallback;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/jse/provider/AbstractJMultiCardKeyStore.class */
public abstract class AbstractJMultiCardKeyStore extends KeyStoreSpi {
    protected static final Logger LOGGER = Logger.getLogger(AbstractJMultiCardKeyStore.class.getName());
    protected static final CryptoHelper CRYPTO_HELPER = new BcCryptoHelper();
    protected Dnie cryptoCard = null;
    protected List<String> aliases = null;

    @Override // java.security.KeyStoreSpi
    public final Enumeration<String> engineAliases() {
        return Collections.enumeration(this.aliases);
    }

    @Override // java.security.KeyStoreSpi
    public final boolean engineContainsAlias(String alias) {
        return this.aliases.contains(alias);
    }

    @Override // java.security.KeyStoreSpi
    public final Certificate engineGetCertificate(String alias) {
        return this.cryptoCard.getCertificate(alias);
    }

    @Override // java.security.KeyStoreSpi
    public final String engineGetCertificateAlias(Certificate otherCert) {
        if (!(otherCert instanceof X509Certificate)) {
            return null;
        }
        for (String alias : this.aliases) {
            Certificate myCert = engineGetCertificate(alias);
            if (myCert != null) {
                try {
                    if (HexUtils.arrayEquals(otherCert.getEncoded(), myCert.getEncoded())) {
                        return alias;
                    }
                } catch (CertificateEncodingException e) {
                    LOGGER.warning("No se han podido comparar certificados: " + e);
                    return null;
                }
            }
        }
        return null;
    }

    @Override // java.security.KeyStoreSpi
    public final Key engineGetKey(String alias, char[] password) {
        if (!engineContainsAlias(alias)) {
            return null;
        }
        if (password != null) {
            PasswordCallback pwc = new CachePasswordCallback(password);
            this.cryptoCard.setPasswordCallback(pwc);
        }
        PrivateKeyReference pkRef = this.cryptoCard.getPrivateKey(alias);
        if (!(pkRef instanceof DniePrivateKeyReference)) {
            throw new ProviderException("La clave obtenida de la tarjeta no es del tipo esperado, se ha obtenido: " + (pkRef != null ? pkRef.getClass().getName() : "null"));
        }
        return new DniePrivateKey((DniePrivateKeyReference) pkRef, ((RSAPublicKey) engineGetCertificate(alias).getPublicKey()).getModulus());
    }

    @Override // java.security.KeyStoreSpi
    public final KeyStore.Entry engineGetEntry(String alias, KeyStore.ProtectionParameter protParam) {
        if (protParam instanceof KeyStore.CallbackHandlerProtection) {
            CallbackHandler chp = ((KeyStore.CallbackHandlerProtection) protParam).getCallbackHandler();
            if (chp != null) {
                this.cryptoCard.setCallbackHandler(chp);
            }
        } else if (protParam instanceof KeyStore.PasswordProtection) {
            PasswordCallback pwc = new CachePasswordCallback(((KeyStore.PasswordProtection) protParam).getPassword());
            this.cryptoCard.setPasswordCallback(pwc);
        } else {
            LOGGER.warning("Se ha proporcionado un ProtectionParameter de tipo no soportado, se ignorara: " + (protParam != null ? protParam.getClass().getName() : "NULO"));
        }
        if (!engineContainsAlias(alias)) {
            return null;
        }
        PrivateKey key = (PrivateKey) engineGetKey(alias, null);
        return new KeyStore.PrivateKeyEntry(key, engineGetCertificateChain(alias));
    }

    @Override // java.security.KeyStoreSpi
    public final int engineSize() {
        return this.aliases.size();
    }

    @Override // java.security.KeyStoreSpi
    public final boolean engineEntryInstanceOf(String alias, Class<? extends KeyStore.Entry> entryClass) {
        if (!engineContainsAlias(alias)) {
            return false;
        }
        return entryClass.equals(KeyStore.PrivateKeyEntry.class);
    }

    @Override // java.security.KeyStoreSpi
    public final boolean engineIsCertificateEntry(String alias) {
        return false;
    }

    @Override // java.security.KeyStoreSpi
    public final boolean engineIsKeyEntry(String alias) {
        return this.aliases.contains(alias);
    }

    @Override // java.security.KeyStoreSpi
    public final void engineStore(OutputStream os, char[] pass) {
        throw new UnsupportedOperationException();
    }

    @Override // java.security.KeyStoreSpi
    public final void engineSetCertificateEntry(String alias, Certificate cert) {
        throw new UnsupportedOperationException();
    }

    @Override // java.security.KeyStoreSpi
    public final void engineSetKeyEntry(String alias, byte[] key, Certificate[] chain) {
        throw new UnsupportedOperationException();
    }

    @Override // java.security.KeyStoreSpi
    public final void engineSetKeyEntry(String alias, Key key, char[] pass, Certificate[] chain) {
        throw new UnsupportedOperationException();
    }

    @Override // java.security.KeyStoreSpi
    public final void engineDeleteEntry(String alias) {
        throw new UnsupportedOperationException();
    }

    @Override // java.security.KeyStoreSpi
    public final Date engineGetCreationDate(String alias) {
        LOGGER.warning("No se soporta la obtencion de fecha de creacion, se devuelve la fecha actual");
        return new Date();
    }
}
