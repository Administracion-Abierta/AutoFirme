package org.spongycastle.x509;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.util.Collection;
import org.spongycastle.x509.util.StreamParser;
import org.spongycastle.x509.util.StreamParsingException;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/x509/X509StreamParser.class */
public class X509StreamParser implements StreamParser {
    private Provider _provider;
    private X509StreamParserSpi _spi;

    public static X509StreamParser getInstance(String type) throws NoSuchParserException {
        try {
            X509Util.Implementation impl = X509Util.getImplementation("X509StreamParser", type);
            return createParser(impl);
        } catch (NoSuchAlgorithmException e) {
            throw new NoSuchParserException(e.getMessage());
        }
    }

    public static X509StreamParser getInstance(String type, String provider) throws NoSuchParserException, NoSuchProviderException {
        return getInstance(type, X509Util.getProvider(provider));
    }

    public static X509StreamParser getInstance(String type, Provider provider) throws NoSuchParserException {
        try {
            X509Util.Implementation impl = X509Util.getImplementation("X509StreamParser", type, provider);
            return createParser(impl);
        } catch (NoSuchAlgorithmException e) {
            throw new NoSuchParserException(e.getMessage());
        }
    }

    private static X509StreamParser createParser(X509Util.Implementation impl) {
        X509StreamParserSpi spi = (X509StreamParserSpi) impl.getEngine();
        return new X509StreamParser(impl.getProvider(), spi);
    }

    private X509StreamParser(Provider provider, X509StreamParserSpi spi) {
        this._provider = provider;
        this._spi = spi;
    }

    public Provider getProvider() {
        return this._provider;
    }

    public void init(InputStream stream) {
        this._spi.engineInit(stream);
    }

    public void init(byte[] data) {
        this._spi.engineInit(new ByteArrayInputStream(data));
    }

    @Override // org.spongycastle.x509.util.StreamParser
    public Object read() throws StreamParsingException {
        return this._spi.engineRead();
    }

    @Override // org.spongycastle.x509.util.StreamParser
    public Collection readAll() throws StreamParsingException {
        return this._spi.engineReadAll();
    }
}
