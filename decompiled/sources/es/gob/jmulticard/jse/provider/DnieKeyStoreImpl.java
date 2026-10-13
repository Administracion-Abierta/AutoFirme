package es.gob.jmulticard.jse.provider;

import es.gob.jmulticard.CryptoHelper;
import es.gob.jmulticard.card.dnie.DnieFactory;
import es.gob.jmulticard.connection.ApduConnection;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.security.auth.callback.PasswordCallback;

/* JADX INFO: loaded from: autofirma.jar:es/gob/jmulticard/jse/provider/DnieKeyStoreImpl.class */
public class DnieKeyStoreImpl extends AbstractJMultiCardKeyStore {
    private static final String INTERMEDIATE_CA_CERT_ALIAS = "CertCAIntermediaDGP";

    @Override // java.security.KeyStoreSpi
    public Certificate[] engineGetCertificateChain(String alias) {
        if (!engineContainsAlias(alias)) {
            return null;
        }
        List<X509Certificate> certs = new ArrayList<>();
        certs.add((X509Certificate) engineGetCertificate(alias));
        X509Certificate intermediateCaCert = this.cryptoCard.getCertificate(INTERMEDIATE_CA_CERT_ALIAS);
        X509Certificate sha2DnieRoot = null;
        if (intermediateCaCert != null) {
            certs.add(intermediateCaCert);
            try {
                InputStream is = DnieKeyStoreImpl.class.getResourceAsStream("/ACRAIZ-SHA2-2.crt");
                Throwable th = null;
                try {
                    try {
                        sha2DnieRoot = CryptoHelper.generateCertificate(is);
                        if (is != null) {
                            if (0 != 0) {
                                try {
                                    is.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            } else {
                                is.close();
                            }
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        throw th3;
                    }
                } catch (Throwable th4) {
                    if (is != null) {
                        if (th != null) {
                            try {
                                is.close();
                            } catch (Throwable th5) {
                                th.addSuppressed(th5);
                            }
                        } else {
                            is.close();
                        }
                    }
                    throw th4;
                }
            } catch (Exception e) {
                sha2DnieRoot = null;
                LOGGER.warning("No se ha podido cargar el certificado de la CA raiz 2: " + e);
            }
            if (sha2DnieRoot != null) {
                try {
                    intermediateCaCert.verify(sha2DnieRoot.getPublicKey());
                } catch (Exception e2) {
                    LOGGER.warning("La CA raiz no es la V2, se intentara con la version anterior: " + e2);
                    try {
                        InputStream is2 = DnieKeyStoreImpl.class.getResourceAsStream("/ACRAIZ-SHA2.crt");
                        Throwable th6 = null;
                        try {
                            try {
                                sha2DnieRoot = CryptoHelper.generateCertificate(is2);
                                if (is2 != null) {
                                    if (0 != 0) {
                                        try {
                                            is2.close();
                                        } catch (Throwable th7) {
                                            th6.addSuppressed(th7);
                                        }
                                    } else {
                                        is2.close();
                                    }
                                }
                                if (sha2DnieRoot != null) {
                                    try {
                                        intermediateCaCert.verify(sha2DnieRoot.getPublicKey());
                                    } catch (Exception ex2) {
                                        sha2DnieRoot = null;
                                        LOGGER.info("La CA raiz de DNIe precargada no es la emisora de este DNIe: " + ex2);
                                    }
                                }
                            } catch (Throwable th8) {
                                th6 = th8;
                                throw th8;
                            }
                        } catch (Throwable th9) {
                            if (is2 != null) {
                                if (th6 != null) {
                                    try {
                                        is2.close();
                                    } catch (Throwable th10) {
                                        th6.addSuppressed(th10);
                                    }
                                } else {
                                    is2.close();
                                }
                            }
                            throw th9;
                        }
                    } catch (Exception ex) {
                        sha2DnieRoot = null;
                        LOGGER.warning("No se ha podido cargar el certificado de la CA raiz: " + ex);
                    }
                }
            }
        }
        if (sha2DnieRoot != null) {
            certs.add(sha2DnieRoot);
        }
        return (Certificate[]) certs.toArray(new X509Certificate[0]);
    }

    @Override // java.security.KeyStoreSpi
    public void engineLoad(KeyStore.LoadStoreParameter param) throws IOException {
        if (param != null) {
            KeyStore.ProtectionParameter pp = param.getProtectionParameter();
            if (pp instanceof KeyStore.CallbackHandlerProtection) {
                if (((KeyStore.CallbackHandlerProtection) pp).getCallbackHandler() == null) {
                    throw new IllegalArgumentException("El CallbackHandler no puede ser nulo");
                }
                this.cryptoCard = DnieFactory.getDnie(DnieProvider.getDefaultApduConnection(), null, CRYPTO_HELPER, ((KeyStore.CallbackHandlerProtection) pp).getCallbackHandler(), isAnotherCardsAllowed());
            } else if (pp instanceof KeyStore.PasswordProtection) {
                PasswordCallback pwc = new CardPasswordCallback((KeyStore.PasswordProtection) pp, JMultiCardProviderMessages.getString("DnieKeyStoreImpl.0"));
                this.cryptoCard = DnieFactory.getDnie(DnieProvider.getDefaultApduConnection(), pwc, CRYPTO_HELPER, null, isAnotherCardsAllowed());
            } else {
                LOGGER.warning("Se ha proporcionado un LoadStoreParameter de tipo no soportado, se ignorara: " + (pp != null ? pp.getClass().getName() : "NULO"));
            }
        } else {
            this.cryptoCard = DnieFactory.getDnie(DnieProvider.getDefaultApduConnection(), null, CRYPTO_HELPER, null, isAnotherCardsAllowed());
        }
        this.aliases = Arrays.asList(this.cryptoCard.getAliases());
    }

    @Override // java.security.KeyStoreSpi
    public void engineLoad(InputStream stream, char[] password) throws IOException {
        ApduConnection defaultApduConnection;
        try {
            if (DnieProvider.getDefaultApduConnection() == null) {
                defaultApduConnection = (ApduConnection) Class.forName(ProviderUtil.DEFAULT_PROVIDER_CLASSNAME).getConstructor(new Class[0]).newInstance(new Object[0]);
            } else {
                defaultApduConnection = DnieProvider.getDefaultApduConnection();
            }
            ApduConnection conn = defaultApduConnection;
            this.cryptoCard = DnieFactory.getDnie(conn, password != null ? new CachePasswordCallback(password) : null, CRYPTO_HELPER, null, isAnotherCardsAllowed());
            this.aliases = Arrays.asList(this.cryptoCard.getAliases());
        } catch (Exception e) {
            throw new IllegalStateException("No hay una conexion de APDU por defecto", e);
        }
    }

    protected boolean isAnotherCardsAllowed() {
        return false;
    }
}
