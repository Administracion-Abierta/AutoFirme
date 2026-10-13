package es.gob.jmulticard.jse.provider.ceres;

import es.gob.jmulticard.HexUtils;
import es.gob.jmulticard.card.PrivateKeyReference;
import es.gob.jmulticard.card.fnmt.ceres.Ceres;
import es.gob.jmulticard.card.fnmt.ceres.CeresPrivateKeyReference;
import es.gob.jmulticard.connection.ApduConnection;
import es.gob.jmulticard.crypto.BcCryptoHelper;
import es.gob.jmulticard.jse.provider.CachePasswordCallback;
import es.gob.jmulticard.jse.provider.CardPasswordCallback;
import es.gob.jmulticard.jse.provider.JMultiCardProviderMessages;
import java.io.IOException;
import java.io.InputStream;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;
import javax.security.auth.callback.PasswordCallback;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/jse/provider/ceres/CeresKeyStoreImpl.class */
public final class CeresKeyStoreImpl extends KeyStoreSpi {
    private static final Logger LOGGER = Logger.getLogger(CeresKeyStoreImpl.class.getName());
    private List<String> userCertAliases = null;
    private Ceres cryptoCard = null;

    private void loadAliases() {
        String[] aliases = this.cryptoCard.getAliases();
        this.userCertAliases = new ArrayList(aliases.length);
        Collections.addAll(this.userCertAliases, aliases);
    }

    @Override // java.security.KeyStoreSpi
    public Enumeration<String> engineAliases() {
        return Collections.enumeration(this.userCertAliases);
    }

    @Override // java.security.KeyStoreSpi
    public boolean engineContainsAlias(String alias) {
        return this.userCertAliases.contains(alias);
    }

    @Override // java.security.KeyStoreSpi
    public Certificate engineGetCertificate(String alias) {
        if (!engineContainsAlias(alias)) {
            return null;
        }
        return this.cryptoCard.getCertificate(alias);
    }

    @Override // java.security.KeyStoreSpi
    public String engineGetCertificateAlias(Certificate otherCert) {
        if (!(otherCert instanceof X509Certificate)) {
            return null;
        }
        for (String alias : this.userCertAliases) {
            X509Certificate myCert = (X509Certificate) engineGetCertificate(alias);
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
    public Certificate[] engineGetCertificateChain(String alias) {
        if (!engineContainsAlias(alias)) {
            return null;
        }
        return new X509Certificate[]{(X509Certificate) engineGetCertificate(alias)};
    }

    @Override // java.security.KeyStoreSpi
    public Key engineGetKey(String alias, char[] password) {
        if (!engineContainsAlias(alias)) {
            return null;
        }
        if (password != null) {
            this.cryptoCard.setPasswordCallback(new CachePasswordCallback(password));
        }
        PrivateKeyReference pkRef = this.cryptoCard.getPrivateKey(alias);
        if (!(pkRef instanceof CeresPrivateKeyReference)) {
            throw new ProviderException("La clave obtenida de la tarjeta no es del tipo esperado, se ha obtenido: " + pkRef.getClass().getName());
        }
        return new CeresPrivateKey((CeresPrivateKeyReference) pkRef, this.cryptoCard, ((RSAPublicKey) engineGetCertificate(alias).getPublicKey()).getModulus());
    }

    @Override // java.security.KeyStoreSpi
    public KeyStore.Entry engineGetEntry(String alias, KeyStore.ProtectionParameter protParam) {
        if (protParam instanceof KeyStore.PasswordProtection) {
            PasswordCallback pwc = new CachePasswordCallback(((KeyStore.PasswordProtection) protParam).getPassword());
            this.cryptoCard.setPasswordCallback(pwc);
        }
        if (!engineContainsAlias(alias)) {
            return null;
        }
        PrivateKey key = (PrivateKey) engineGetKey(alias, null);
        return new KeyStore.PrivateKeyEntry(key, engineGetCertificateChain(alias));
    }

    @Override // java.security.KeyStoreSpi
    public boolean engineIsCertificateEntry(String alias) {
        return false;
    }

    @Override // java.security.KeyStoreSpi
    public boolean engineIsKeyEntry(String alias) {
        return this.userCertAliases.contains(alias);
    }

    private static ApduConnection getApduConnection() {
        return CeresProvider.getDefaultApduConnection();
    }

    @Override // java.security.KeyStoreSpi
    public void engineLoad(KeyStore.LoadStoreParameter param) throws IOException {
        if (param != null) {
            KeyStore.ProtectionParameter pp = param.getProtectionParameter();
            if (pp instanceof KeyStore.CallbackHandlerProtection) {
                if (((KeyStore.CallbackHandlerProtection) pp).getCallbackHandler() == null) {
                    throw new IllegalArgumentException("El CallbackHandler no puede ser nulo");
                }
                this.cryptoCard = new Ceres(CeresProvider.getDefaultApduConnection(), new BcCryptoHelper());
                this.cryptoCard.setCallbackHandler(((KeyStore.CallbackHandlerProtection) pp).getCallbackHandler());
            } else if (pp instanceof KeyStore.PasswordProtection) {
                PasswordCallback pwc = new CardPasswordCallback((KeyStore.PasswordProtection) pp, JMultiCardProviderMessages.getString("Ceres430KeyStoreImpl.0"));
                this.cryptoCard = new Ceres(CeresProvider.getDefaultApduConnection(), new BcCryptoHelper());
                this.cryptoCard.setPasswordCallback(pwc);
            } else {
                LOGGER.warning("Se ha proporcionado un LoadStoreParameter de tipo no soportado, se ignorara: " + (pp != null ? pp.getClass().getName() : "NULO"));
            }
        } else {
            this.cryptoCard = new Ceres(CeresProvider.getDefaultApduConnection(), new BcCryptoHelper());
        }
        this.userCertAliases = Arrays.asList(this.cryptoCard.getAliases());
    }

    @Override // java.security.KeyStoreSpi
    public void engineLoad(InputStream stream, char[] password) throws IOException {
        this.cryptoCard = new Ceres(getApduConnection(), new BcCryptoHelper());
        loadAliases();
    }

    @Override // java.security.KeyStoreSpi
    public int engineSize() {
        return this.userCertAliases.size();
    }

    @Override // java.security.KeyStoreSpi
    public boolean engineEntryInstanceOf(String alias, Class<? extends KeyStore.Entry> entryClass) {
        if (!engineContainsAlias(alias)) {
            return false;
        }
        return entryClass.equals(KeyStore.PrivateKeyEntry.class);
    }

    @Override // java.security.KeyStoreSpi
    public void engineDeleteEntry(String alias) {
        throw new UnsupportedOperationException();
    }

    @Override // java.security.KeyStoreSpi
    public void engineStore(OutputStream os, char[] pass) {
        throw new UnsupportedOperationException();
    }

    @Override // java.security.KeyStoreSpi
    public void engineSetCertificateEntry(String alias, Certificate cert) {
        throw new UnsupportedOperationException();
    }

    @Override // java.security.KeyStoreSpi
    public void engineSetKeyEntry(String alias, byte[] key, Certificate[] chain) {
        throw new UnsupportedOperationException();
    }

    @Override // java.security.KeyStoreSpi
    public void engineSetKeyEntry(String alias, Key key, char[] pass, Certificate[] chain) {
        throw new UnsupportedOperationException();
    }

    @Override // java.security.KeyStoreSpi
    public Date engineGetCreationDate(String alias) {
        LOGGER.warning("No se soporta la obtencion de fecha de creacion, se devuelve la fecha actual");
        return new Date();
    }
}
