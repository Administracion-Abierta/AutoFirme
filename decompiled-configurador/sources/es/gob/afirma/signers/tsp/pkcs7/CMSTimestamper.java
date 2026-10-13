package es.gob.afirma.signers.tsp.pkcs7;

import es.gob.afirma.core.AOException;
import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.Base64;
import es.gob.afirma.core.misc.MimeHelper;
import es.gob.afirma.core.signers.AOSignConstants;
import es.gob.afirma.signers.pkcs7.AOAlgorithmID;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.net.URI;
import java.net.URLConnection;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.logging.Logger;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.asn1.ASN1ObjectIdentifier;
import org.spongycastle.asn1.ASN1Primitive;
import org.spongycastle.asn1.DERSet;
import org.spongycastle.asn1.cmp.PKIFailureInfo;
import org.spongycastle.asn1.cms.Attribute;
import org.spongycastle.asn1.cms.AttributeTable;
import org.spongycastle.asn1.x509.X509ObjectIdentifiers;
import org.spongycastle.cms.CMSSignedData;
import org.spongycastle.cms.SignerInformation;
import org.spongycastle.cms.SignerInformationStore;
import org.spongycastle.tsp.TimeStampRequest;
import org.spongycastle.tsp.TimeStampRequestGenerator;
import org.spongycastle.tsp.TimeStampResponse;
import org.spongycastle.tsp.TimeStampToken;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/signers/tsp/pkcs7/CMSTimestamper.class */
public final class CMSTimestamper {
    private static final String SIGNATURE_TIMESTAMP_TOKEN_OID = "1.2.840.113549.1.9.16.2.14";
    private static final int SOCKET_TIMEOUT = 500000;
    private static final String STORE_TYPE_PKCS12 = "PKCS12";
    static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private final TimeStampRequestGenerator tsqGenerator;
    private final URI tsaURL;
    private final String tsaUsername;
    private final String tsaPassword;
    private byte[] sslKeyStoreFile;
    private String sslKeyStorePassword;
    private String sslKeyStoreType;
    private byte[] sslTrustStoreFile;
    private String sslTrustStorePassword;
    private String sslTrustStoreType;
    private boolean verifyHostname;

    public CMSTimestamper(boolean z, String str, URI uri, String str2, String str3, TsaRequestExtension[] tsaRequestExtensionArr, byte[] bArr, String str4) {
        this(z, str, uri, str2, str3, tsaRequestExtensionArr, bArr, str4, STORE_TYPE_PKCS12, null, null, null, false);
    }

    public CMSTimestamper(boolean z, String str, URI uri, String str2, String str3, TsaRequestExtension[] tsaRequestExtensionArr, byte[] bArr, String str4, String str5, byte[] bArr2, String str6, String str7, boolean z2) {
        this.sslKeyStoreFile = null;
        this.sslKeyStorePassword = null;
        this.sslKeyStoreType = null;
        this.sslTrustStoreFile = null;
        this.sslTrustStorePassword = null;
        this.sslTrustStoreType = null;
        this.verifyHostname = true;
        this.tsqGenerator = new TimeStampRequestGenerator();
        if (tsaRequestExtensionArr != null) {
            for (TsaRequestExtension tsaRequestExtension : tsaRequestExtensionArr) {
                this.tsqGenerator.addExtension(new ASN1ObjectIdentifier(tsaRequestExtension.getOid()), tsaRequestExtension.isCritical(), tsaRequestExtension.getValue());
                LOGGER.info("Anadida extension a la solicitud de sello de tiempo: " + tsaRequestExtension);
            }
        }
        this.tsqGenerator.setCertReq(z);
        this.tsqGenerator.setReqPolicy(new ASN1ObjectIdentifier(str));
        this.tsaURL = uri;
        this.tsaPassword = str3;
        this.tsaUsername = str2;
        this.sslKeyStoreFile = bArr != null ? (byte[]) bArr.clone() : null;
        this.sslKeyStorePassword = str4;
        this.sslKeyStoreType = str5;
        this.sslTrustStoreFile = bArr2 != null ? (byte[]) bArr2.clone() : null;
        this.sslTrustStorePassword = str6;
        this.sslTrustStoreType = str7;
        this.verifyHostname = z2;
    }

    public CMSTimestamper(TsaParams tsaParams) {
        this(tsaParams.doTsaRequireCert(), tsaParams.getTsaPolicy(), tsaParams.getTsaUrl(), tsaParams.getTsaUsr(), tsaParams.getTsaPwd(), tsaParams.getExtensions(), tsaParams.getSslKeyStore(), tsaParams.getSslKeyStorePassword(), tsaParams.getSslKeyStoreType(), tsaParams.getSslTrustStore(), tsaParams.getSslTrustStorePassword(), tsaParams.getSslTrustStoreType(), tsaParams.isVerifyHostname());
    }

    public byte[] addTimestamp(byte[] bArr, String str, Calendar calendar) throws NoSuchAlgorithmException, IOException, AOException {
        String digestAlgorithmName = AOSignConstants.getDigestAlgorithmName(str);
        try {
            CMSSignedData cMSSignedData = new CMSSignedData(bArr);
            SignerInformationStore signerInfos = cMSSignedData.getSignerInfos();
            ArrayList arrayList = new ArrayList();
            for (SignerInformation signerInformation : signerInfos.getSigners()) {
                ASN1InputStream aSN1InputStream = new ASN1InputStream(new ByteArrayInputStream(getTimeStampToken(MessageDigest.getInstance(digestAlgorithmName).digest(signerInformation.getSignature()), digestAlgorithmName, calendar)));
                Throwable th = null;
                try {
                    try {
                        ASN1Primitive object = aSN1InputStream.readObject();
                        if (aSN1InputStream != null) {
                            if (0 != 0) {
                                try {
                                    aSN1InputStream.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            } else {
                                aSN1InputStream.close();
                            }
                        }
                        Attribute attribute = new Attribute(new ASN1ObjectIdentifier(SIGNATURE_TIMESTAMP_TOKEN_OID), new DERSet(object));
                        Hashtable hashtable = new Hashtable();
                        hashtable.put(new ASN1ObjectIdentifier(SIGNATURE_TIMESTAMP_TOKEN_OID), attribute);
                        arrayList.add(SignerInformation.replaceUnsignedAttributes(signerInformation, new AttributeTable(hashtable)));
                    } catch (Throwable th3) {
                        th = th3;
                        throw th3;
                    }
                } catch (Throwable th4) {
                    if (aSN1InputStream != null) {
                        if (th != null) {
                            try {
                                aSN1InputStream.close();
                            } catch (Throwable th5) {
                                th.addSuppressed(th5);
                            }
                        } else {
                            aSN1InputStream.close();
                        }
                    }
                    throw th4;
                }
            }
            return CMSSignedData.replaceSigners(cMSSignedData, new SignerInformationStore(arrayList)).getEncoded();
        } catch (Exception e) {
            throw new IllegalArgumentException("Los datos de entrada no son un SignedData de CMS: " + e);
        }
    }

    private byte[] getTSAResponse(byte[] bArr) throws IOException {
        if (this.tsaURL.getScheme().equals("socket")) {
            return getTSAResponseSocket(bArr);
        }
        if (this.tsaURL.getScheme().equals("http")) {
            return getTSAResponseHttp(bArr);
        }
        if (this.tsaURL.getScheme().equals("https")) {
            return getTSAResponseHttps(bArr);
        }
        throw new UnsupportedOperationException("Protocolo de conexion con TSA no soportado: " + this.tsaURL.getScheme());
    }

    private byte[] getTSAResponseSocket(byte[] bArr) throws IOException {
        Socket socket = new Socket(this.tsaURL.getHost(), this.tsaURL.getPort());
        Throwable th = null;
        try {
            try {
                socket.setSoTimeout(SOCKET_TIMEOUT);
                byte[] tSAResponseExternalSocket = getTSAResponseExternalSocket(bArr, socket);
                if (socket != null) {
                    if (0 != 0) {
                        try {
                            socket.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        socket.close();
                    }
                }
                return tSAResponseExternalSocket;
            } catch (Throwable th3) {
                th = th3;
                throw th3;
            }
        } catch (Throwable th4) {
            if (socket != null) {
                if (th != null) {
                    try {
                        socket.close();
                    } catch (Throwable th5) {
                        th.addSuppressed(th5);
                    }
                } else {
                    socket.close();
                }
            }
            throw th4;
        }
    }

    private static byte[] getTSAResponseExternalSocket(byte[] bArr, Socket socket) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        Throwable th = null;
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
            Throwable th2 = null;
            try {
                dataOutputStream.writeInt(bArr.length + 1);
                dataOutputStream.writeByte(0);
                dataOutputStream.write(bArr);
                dataOutputStream.flush();
                outputStream.flush();
                if (dataOutputStream != null) {
                    if (0 != 0) {
                        try {
                            dataOutputStream.close();
                        } catch (Throwable th3) {
                            th2.addSuppressed(th3);
                        }
                    } else {
                        dataOutputStream.close();
                    }
                }
                if (outputStream != null) {
                    if (0 != 0) {
                        try {
                            outputStream.close();
                        } catch (Throwable th4) {
                            th.addSuppressed(th4);
                        }
                    } else {
                        outputStream.close();
                    }
                }
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                int i = dataInputStream.readInt();
                byte b = dataInputStream.readByte();
                byte[] bArr2 = new byte[i - 1];
                dataInputStream.readFully(bArr2);
                if (b != 5 && b != 6) {
                    throw new IOException("Obtenida resuesta incorrecta del servidor TSA: " + new String(bArr2));
                }
                socket.close();
                return bArr2;
            } catch (Throwable th5) {
                if (dataOutputStream != null) {
                    if (0 != 0) {
                        try {
                            dataOutputStream.close();
                        } catch (Throwable th6) {
                            th2.addSuppressed(th6);
                        }
                    } else {
                        dataOutputStream.close();
                    }
                }
                throw th5;
            }
        } catch (Throwable th7) {
            if (outputStream != null) {
                if (0 != 0) {
                    try {
                        outputStream.close();
                    } catch (Throwable th8) {
                        th.addSuppressed(th8);
                    }
                } else {
                    outputStream.close();
                }
            }
            throw th7;
        }
    }

    private byte[] getTSAResponseHttp(byte[] bArr) throws IOException {
        return getTSAResponseHttp(bArr, prepareConnection(false));
    }

    private byte[] getTSAResponseHttps(byte[] bArr) throws IOException {
        return getTSAResponseHttp(bArr, prepareConnection(true));
    }

    private static byte[] getTSAResponseHttp(byte[] bArr, URLConnection uRLConnection) throws IOException {
        OutputStream outputStream = uRLConnection.getOutputStream();
        Throwable th = null;
        try {
            try {
                outputStream.write(bArr);
                outputStream.flush();
                if (outputStream != null) {
                    if (0 != 0) {
                        try {
                            outputStream.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        outputStream.close();
                    }
                }
                InputStream inputStream = uRLConnection.getInputStream();
                Throwable th3 = null;
                try {
                    try {
                        byte[] dataFromInputStream = AOUtil.getDataFromInputStream(inputStream);
                        if (inputStream != null) {
                            if (0 != 0) {
                                try {
                                    inputStream.close();
                                } catch (Throwable th4) {
                                    th3.addSuppressed(th4);
                                }
                            } else {
                                inputStream.close();
                            }
                        }
                        String contentEncoding = uRLConnection.getContentEncoding();
                        if (contentEncoding != null && contentEncoding.equalsIgnoreCase("base64")) {
                            return Base64.decode(new String(dataFromInputStream));
                        }
                        return dataFromInputStream;
                    } catch (Throwable th5) {
                        th3 = th5;
                        throw th5;
                    }
                } catch (Throwable th6) {
                    if (inputStream != null) {
                        if (th3 != null) {
                            try {
                                inputStream.close();
                            } catch (Throwable th7) {
                                th3.addSuppressed(th7);
                            }
                        } else {
                            inputStream.close();
                        }
                    }
                    throw th6;
                }
            } catch (Throwable th8) {
                th = th8;
                throw th8;
            }
        } catch (Throwable th9) {
            if (outputStream != null) {
                if (th != null) {
                    try {
                        outputStream.close();
                    } catch (Throwable th10) {
                        th.addSuppressed(th10);
                    }
                } else {
                    outputStream.close();
                }
            }
            throw th9;
        }
    }

    private URLConnection prepareConnection(boolean z) throws IOException {
        URLConnection uRLConnectionOpenConnection = this.tsaURL.toURL().openConnection();
        uRLConnectionOpenConnection.setDoInput(true);
        uRLConnectionOpenConnection.setDoOutput(true);
        uRLConnectionOpenConnection.setUseCaches(false);
        uRLConnectionOpenConnection.setRequestProperty("Content-Type", "application/timestamp-query");
        uRLConnectionOpenConnection.setRequestProperty("Content-Transfer-Encoding", MimeHelper.DEFAULT_CONTENT_DESCRIPTION);
        if (z) {
            configureHttpsConnection(uRLConnectionOpenConnection);
        }
        if (this.tsaUsername != null && !this.tsaUsername.isEmpty()) {
            uRLConnectionOpenConnection.setRequestProperty("Authorization", "Basic " + new String(Base64.encode((this.tsaUsername + ":" + this.tsaPassword).getBytes())));
        }
        return uRLConnectionOpenConnection;
    }

    private void configureHttpsConnection(URLConnection uRLConnection) throws IOException {
        TrustManager[] trustManagers;
        if (uRLConnection == null) {
            throw new IllegalArgumentException("La conexion no puede ser nula");
        }
        if (!this.verifyHostname) {
            LOGGER.warning("No se comprobaran los nombres de host en la conexion SSL del sello de tiempo");
            if (uRLConnection instanceof HttpsURLConnection) {
                ((HttpsURLConnection) uRLConnection).setHostnameVerifier(new HostnameVerifier() { // from class: es.gob.afirma.signers.tsp.pkcs7.CMSTimestamper.1
                    @Override // javax.net.ssl.HostnameVerifier
                    public boolean verify(String str, SSLSession sSLSession) {
                        return true;
                    }
                });
            } else {
                LOGGER.warning("No se ha podido deshabilitar la comprobacion de nombre de host, tipo desconocido de conexion: " + uRLConnection.getClass().getName());
            }
        }
        try {
            SSLContext sSLContext = SSLContext.getInstance("TLS");
            KeyManager[] keyManagers = null;
            if (this.sslKeyStoreFile != null && this.sslKeyStorePassword != null) {
                try {
                    KeyStore keyStore = KeyStore.getInstance(this.sslKeyStoreType == null ? "JKS" : this.sslKeyStoreType);
                    keyStore.load(new ByteArrayInputStream(this.sslKeyStoreFile), this.sslKeyStorePassword.toCharArray());
                    KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                    keyManagerFactory.init(keyStore, this.sslKeyStorePassword.toCharArray());
                    keyManagers = keyManagerFactory.getKeyManagers();
                } catch (Exception e) {
                    throw new IOException("Error obteniendo el almacen de certificados cliente para el SSL: " + e, e);
                }
            }
            if (this.sslTrustStoreFile != null && this.sslTrustStorePassword != null) {
                try {
                    KeyStore keyStore2 = KeyStore.getInstance(this.sslTrustStoreType == null ? "JKS" : this.sslTrustStoreType);
                    keyStore2.load(new ByteArrayInputStream(this.sslTrustStoreFile), this.sslTrustStorePassword.toCharArray());
                    TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                    trustManagerFactory.init(keyStore2);
                    trustManagers = trustManagerFactory.getTrustManagers();
                } catch (Exception e2) {
                    throw new IOException("Error obteniendo el almacen de confianza con los certificados de CA con los que se configuro la SSL: " + e2, e2);
                }
            } else {
                trustManagers = new TrustManager[]{new X509TrustManager() { // from class: es.gob.afirma.signers.tsp.pkcs7.CMSTimestamper.2
                    @Override // javax.net.ssl.X509TrustManager
                    public void checkClientTrusted(X509Certificate[] x509CertificateArr, String str) {
                    }

                    @Override // javax.net.ssl.X509TrustManager
                    public void checkServerTrusted(X509Certificate[] x509CertificateArr, String str) {
                    }

                    @Override // javax.net.ssl.X509TrustManager
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }};
            }
            try {
                sSLContext.init(keyManagers, trustManagers, null);
                try {
                    uRLConnection.getClass().getMethod("setSSLSocketFactory", SSLSocketFactory.class).invoke(uRLConnection, sSLContext.getSocketFactory());
                } catch (Exception e3) {
                    LOGGER.severe("Error en la configuracion del acceso a la URL sobre SSL");
                    throw new IOException("Error en la configuracion del acceso a la URL sobre SSL", e3);
                }
            } catch (Exception e4) {
                throw new IOException("Error creando el gestor de seguridad SSL: " + e4, e4);
            }
        } catch (Exception e5) {
            throw new IOException("No se ha podido obtener el contexto de seguridad SSL: " + e5, e5);
        }
    }

    public byte[] getTimeStampToken(byte[] bArr, String str, Calendar calendar) throws IOException, AOException {
        TimeStampRequest timeStampRequestGenerate = this.tsqGenerator.generate(new ASN1ObjectIdentifier(str != null ? AOAlgorithmID.getOID(str) : X509ObjectIdentifiers.id_SHA1.getId()), bArr, BigInteger.valueOf(calendar != null ? calendar.getTimeInMillis() : System.currentTimeMillis()));
        byte[] tSAResponse = getTSAResponse(timeStampRequestGenerate.getEncoded());
        try {
            TimeStampResponse timeStampResponse = new TimeStampResponse(tSAResponse);
            try {
                timeStampResponse.validate(timeStampRequestGenerate);
                PKIFailureInfo failInfo = timeStampResponse.getFailInfo();
                int iIntValue = failInfo == null ? 0 : failInfo.intValue();
                if (iIntValue != 0) {
                    throw new AOException("Respuesta invalida de la TSA ('" + this.tsaURL + "') con el codigo " + iIntValue);
                }
                TimeStampToken timeStampToken = timeStampResponse.getTimeStampToken();
                if (timeStampToken == null) {
                    throw new AOException("La respuesta de la TSA ('" + this.tsaURL + "') no es un sello de tiempo valido: " + new String(tSAResponse));
                }
                return timeStampToken.getEncoded();
            } catch (Exception e) {
                throw new AOException("Error validando la respuesta de la TSA: " + e, e);
            }
        } catch (Exception e2) {
            LOGGER.severe("Respuesta de la TSA: " + new String(tSAResponse));
            throw new AOException("Error obteniendo la respuesta de la TSA: " + e2, e2);
        }
    }
}
