package org.bouncycastle.est;

import com.aowagie.text.pdf.Barcode128;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Pattern;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERPrintableString;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.asn1.est.CsrAttrs;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cmc.CMCException;
import org.bouncycastle.cmc.SimplePKIResponse;
import org.bouncycastle.mime.BasicMimeParser;
import org.bouncycastle.mime.ConstantMimeContext;
import org.bouncycastle.mime.Headers;
import org.bouncycastle.mime.MimeContext;
import org.bouncycastle.mime.MimeParserContext;
import org.bouncycastle.mime.MimeParserListener;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.encoders.Base64;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/est/ESTService.class */
public class ESTService {
    protected static final String CACERTS = "/cacerts";
    protected static final String SIMPLE_ENROLL = "/simpleenroll";
    protected static final String SIMPLE_REENROLL = "/simplereenroll";
    protected static final String FULLCMC = "/fullcmc";
    protected static final String SERVERGEN = "/serverkeygen";
    protected static final String CSRATTRS = "/csrattrs";
    protected static final Set<String> illegalParts = new HashSet();
    private final String server;
    private final ESTClientProvider clientProvider;
    private static final Pattern pathInValid;

    ESTService(String str, String str2, ESTClientProvider eSTClientProvider) {
        String strVerifyServer = verifyServer(str);
        if (str2 != null) {
            this.server = "https://" + strVerifyServer + "/.well-known/est/" + verifyLabel(str2);
        } else {
            this.server = "https://" + strVerifyServer + "/.well-known/est";
        }
        this.clientProvider = eSTClientProvider;
    }

    public static X509CertificateHolder[] storeToArray(Store<X509CertificateHolder> store) {
        return storeToArray(store, null);
    }

    public static X509CertificateHolder[] storeToArray(Store<X509CertificateHolder> store, Selector<X509CertificateHolder> selector) {
        Collection<X509CertificateHolder> matches = store.getMatches(selector);
        return (X509CertificateHolder[]) matches.toArray(new X509CertificateHolder[matches.size()]);
    }

    public CACertsResponse getCACerts() throws ESTException {
        ESTResponse eSTResponse = null;
        Exception exc = null;
        try {
            try {
                URL url = new URL(this.server + CACERTS);
                ESTClient eSTClientMakeClient = this.clientProvider.makeClient();
                ESTRequest eSTRequestBuild = new ESTRequestBuilder("GET", url).withClient(eSTClientMakeClient).build();
                ESTResponse eSTResponseDoRequest = eSTClientMakeClient.doRequest(eSTRequestBuild);
                Store<X509CertificateHolder> certificates = null;
                Store<X509CRLHolder> cRLs = null;
                if (eSTResponseDoRequest.getStatusCode() == 200) {
                    String firstValue = eSTResponseDoRequest.getHeaders().getFirstValue("Content-Type");
                    if (firstValue == null || !firstValue.startsWith("application/pkcs7-mime")) {
                        throw new ESTException("Response : " + url.toString() + "Expecting application/pkcs7-mime " + (firstValue != null ? " got " + firstValue : " but was not present."), null, eSTResponseDoRequest.getStatusCode(), eSTResponseDoRequest.getInputStream());
                    }
                    try {
                        SimplePKIResponse simplePKIResponse = new SimplePKIResponse(ContentInfo.getInstance(getASN1InputStream(eSTResponseDoRequest.getInputStream(), eSTResponseDoRequest.getContentLength()).readObject()));
                        certificates = simplePKIResponse.getCertificates();
                        cRLs = simplePKIResponse.getCRLs();
                    } catch (Throwable th) {
                        throw new ESTException("Decoding CACerts: " + url.toString() + " " + th.getMessage(), th, eSTResponseDoRequest.getStatusCode(), eSTResponseDoRequest.getInputStream());
                    }
                } else if (eSTResponseDoRequest.getStatusCode() != 204) {
                    throw new ESTException("Get CACerts: " + url.toString(), null, eSTResponseDoRequest.getStatusCode(), eSTResponseDoRequest.getInputStream());
                }
                CACertsResponse cACertsResponse = new CACertsResponse(certificates, cRLs, eSTRequestBuild, eSTResponseDoRequest.getSource(), this.clientProvider.isTrusted());
                if (eSTResponseDoRequest != null) {
                    try {
                        eSTResponseDoRequest.close();
                    } catch (Exception e) {
                        exc = e;
                    }
                }
                if (exc == null) {
                    return cACertsResponse;
                }
                if (exc instanceof ESTException) {
                    throw ((ESTException) exc);
                }
                throw new ESTException("Get CACerts: " + url.toString(), exc, eSTResponseDoRequest.getStatusCode(), null);
            } catch (Throwable th2) {
                if (th2 instanceof ESTException) {
                    throw ((ESTException) th2);
                }
                throw new ESTException(th2.getMessage(), th2);
            }
        } catch (Throwable th3) {
            if (0 != 0) {
                try {
                    eSTResponse.close();
                } catch (Exception e2) {
                }
            }
            throw th3;
        }
    }

    private ASN1InputStream getASN1InputStream(InputStream inputStream, Long l) {
        if (l != null && l.intValue() == l.longValue()) {
            return new ASN1InputStream(inputStream, l.intValue());
        }
        return new ASN1InputStream(inputStream);
    }

    public EnrollmentResponse simpleEnroll(EnrollmentResponse enrollmentResponse) throws Exception {
        if (!this.clientProvider.isTrusted()) {
            throw new IllegalStateException("No trust anchors.");
        }
        ESTResponse eSTResponseDoRequest = null;
        try {
            try {
                ESTClient eSTClientMakeClient = this.clientProvider.makeClient();
                eSTResponseDoRequest = eSTClientMakeClient.doRequest(new ESTRequestBuilder(enrollmentResponse.getRequestToRetry()).withClient(eSTClientMakeClient).build());
                EnrollmentResponse enrollmentResponseHandleEnrollResponse = handleEnrollResponse(eSTResponseDoRequest);
                if (eSTResponseDoRequest != null) {
                    eSTResponseDoRequest.close();
                }
                return enrollmentResponseHandleEnrollResponse;
            } catch (Throwable th) {
                if (th instanceof ESTException) {
                    throw ((ESTException) th);
                }
                throw new ESTException(th.getMessage(), th);
            }
        } catch (Throwable th2) {
            if (eSTResponseDoRequest != null) {
                eSTResponseDoRequest.close();
            }
            throw th2;
        }
    }

    protected EnrollmentResponse enroll(boolean z, PKCS10CertificationRequest pKCS10CertificationRequest, ESTAuth eSTAuth, boolean z2) throws IOException {
        String str;
        if (!this.clientProvider.isTrusted()) {
            throw new IllegalStateException("No trust anchors.");
        }
        ESTResponse eSTResponseDoRequest = null;
        try {
            try {
                byte[] bytes = annotateRequest(pKCS10CertificationRequest.getEncoded()).getBytes();
                StringBuilder sbAppend = new StringBuilder().append(this.server);
                if (z2) {
                    str = SERVERGEN;
                } else {
                    str = z ? SIMPLE_REENROLL : SIMPLE_ENROLL;
                }
                URL url = new URL(sbAppend.append(str).toString());
                ESTClient eSTClientMakeClient = this.clientProvider.makeClient();
                ESTRequestBuilder eSTRequestBuilderWithClient = new ESTRequestBuilder("POST", url).withData(bytes).withClient(eSTClientMakeClient);
                eSTRequestBuilderWithClient.addHeader("Content-Type", "application/pkcs10");
                eSTRequestBuilderWithClient.addHeader("Content-Length", "" + bytes.length);
                eSTRequestBuilderWithClient.addHeader("Content-Transfer-Encoding", "base64");
                if (eSTAuth != null) {
                    eSTAuth.applyAuth(eSTRequestBuilderWithClient);
                }
                eSTResponseDoRequest = eSTClientMakeClient.doRequest(eSTRequestBuilderWithClient.build());
                EnrollmentResponse enrollmentResponseHandleEnrollResponse = handleEnrollResponse(eSTResponseDoRequest);
                if (eSTResponseDoRequest != null) {
                    eSTResponseDoRequest.close();
                }
                return enrollmentResponseHandleEnrollResponse;
            } catch (Throwable th) {
                if (th instanceof ESTException) {
                    throw ((ESTException) th);
                }
                throw new ESTException(th.getMessage(), th);
            }
        } catch (Throwable th2) {
            if (eSTResponseDoRequest != null) {
                eSTResponseDoRequest.close();
            }
            throw th2;
        }
    }

    public EnrollmentResponse simpleEnroll(boolean z, PKCS10CertificationRequest pKCS10CertificationRequest, ESTAuth eSTAuth) throws IOException {
        return enroll(z, pKCS10CertificationRequest, eSTAuth, false);
    }

    public EnrollmentResponse simpleEnrollWithServersideCreation(PKCS10CertificationRequest pKCS10CertificationRequest, ESTAuth eSTAuth) throws IOException {
        return enroll(false, pKCS10CertificationRequest, eSTAuth, true);
    }

    public EnrollmentResponse enrollPop(boolean z, final PKCS10CertificationRequestBuilder pKCS10CertificationRequestBuilder, final ContentSigner contentSigner, ESTAuth eSTAuth, boolean z2) throws IOException {
        if (!this.clientProvider.isTrusted()) {
            throw new IllegalStateException("No trust anchors.");
        }
        ESTResponse eSTResponseDoRequest = null;
        try {
            try {
                URL url = new URL(this.server + (z ? SIMPLE_REENROLL : SIMPLE_ENROLL));
                ESTClient eSTClientMakeClient = this.clientProvider.makeClient();
                ESTRequestBuilder eSTRequestBuilderWithConnectionListener = new ESTRequestBuilder("POST", url).withClient(eSTClientMakeClient).withConnectionListener(new ESTSourceConnectionListener() { // from class: org.bouncycastle.est.ESTService.1
                    @Override // org.bouncycastle.est.ESTSourceConnectionListener
                    public ESTRequest onConnection(Source source, ESTRequest eSTRequest) throws IOException {
                        if (!(source instanceof TLSUniqueProvider) || !((TLSUniqueProvider) source).isTLSUniqueAvailable()) {
                            throw new IOException("Source does not supply TLS unique.");
                        }
                        PKCS10CertificationRequestBuilder pKCS10CertificationRequestBuilder2 = new PKCS10CertificationRequestBuilder(pKCS10CertificationRequestBuilder);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        pKCS10CertificationRequestBuilder2.setAttribute(PKCSObjectIdentifiers.pkcs_9_at_challengePassword, new DERPrintableString(Base64.toBase64String(((TLSUniqueProvider) source).getTLSUnique())));
                        byteArrayOutputStream.write(ESTService.this.annotateRequest(pKCS10CertificationRequestBuilder2.build(contentSigner).getEncoded()).getBytes());
                        byteArrayOutputStream.flush();
                        ESTRequestBuilder eSTRequestBuilderWithData = new ESTRequestBuilder(eSTRequest).withData(byteArrayOutputStream.toByteArray());
                        eSTRequestBuilderWithData.setHeader("Content-Type", "application/pkcs10");
                        eSTRequestBuilderWithData.setHeader("Content-Transfer-Encoding", "base64");
                        eSTRequestBuilderWithData.setHeader("Content-Length", Long.toString(byteArrayOutputStream.size()));
                        return eSTRequestBuilderWithData.build();
                    }
                });
                if (eSTAuth != null) {
                    eSTAuth.applyAuth(eSTRequestBuilderWithConnectionListener);
                }
                eSTResponseDoRequest = eSTClientMakeClient.doRequest(eSTRequestBuilderWithConnectionListener.build());
                EnrollmentResponse enrollmentResponseHandleEnrollResponse = handleEnrollResponse(eSTResponseDoRequest);
                if (eSTResponseDoRequest != null) {
                    eSTResponseDoRequest.close();
                }
                return enrollmentResponseHandleEnrollResponse;
            } catch (Throwable th) {
                if (th instanceof ESTException) {
                    throw ((ESTException) th);
                }
                throw new ESTException(th.getMessage(), th);
            }
        } catch (Throwable th2) {
            if (eSTResponseDoRequest != null) {
                eSTResponseDoRequest.close();
            }
            throw th2;
        }
    }

    public EnrollmentResponse simpleEnrollPoP(boolean z, PKCS10CertificationRequestBuilder pKCS10CertificationRequestBuilder, ContentSigner contentSigner, ESTAuth eSTAuth) throws IOException {
        return enrollPop(z, pKCS10CertificationRequestBuilder, contentSigner, eSTAuth, false);
    }

    public EnrollmentResponse simpleEnrollPopWithServersideCreation(PKCS10CertificationRequestBuilder pKCS10CertificationRequestBuilder, ContentSigner contentSigner, ESTAuth eSTAuth) throws IOException {
        return enrollPop(false, pKCS10CertificationRequestBuilder, contentSigner, eSTAuth, true);
    }

    protected EnrollmentResponse handleEnrollResponse(ESTResponse eSTResponse) throws IOException {
        long time;
        ESTRequest originalRequest = eSTResponse.getOriginalRequest();
        if (eSTResponse.getStatusCode() == 202) {
            String header = eSTResponse.getHeader("Retry-After");
            if (header == null) {
                throw new ESTException("Got Status 202 but not Retry-After header from: " + originalRequest.getURL().toString());
            }
            try {
                time = System.currentTimeMillis() + (Long.parseLong(header) * 1000);
            } catch (NumberFormatException e) {
                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
                    simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                    time = simpleDateFormat.parse(header).getTime();
                } catch (Exception e2) {
                    throw new ESTException("Unable to parse Retry-After header:" + originalRequest.getURL().toString() + " " + e2.getMessage(), null, eSTResponse.getStatusCode(), eSTResponse.getInputStream());
                }
            }
            return new EnrollmentResponse(null, time, originalRequest, eSTResponse.getSource());
        }
        if (eSTResponse.getStatusCode() != 200 || !eSTResponse.getHeaderOrEmpty("content-type").contains("multipart/mixed")) {
            if (eSTResponse.getStatusCode() != 200) {
                throw new ESTException("Simple Enroll: " + originalRequest.getURL().toString(), null, eSTResponse.getStatusCode(), eSTResponse.getInputStream());
            }
            try {
                return new EnrollmentResponse(new SimplePKIResponse(ContentInfo.getInstance(new ASN1InputStream(eSTResponse.getInputStream()).readObject())).getCertificates(), -1L, null, eSTResponse.getSource());
            } catch (CMCException e3) {
                throw new ESTException(e3.getMessage(), e3.getCause());
            }
        }
        final Object[] objArr = new Object[2];
        new BasicMimeParser(new Headers(eSTResponse.getHeaderOrEmpty("content-type"), "base64"), eSTResponse.getInputStream()).parse(new MimeParserListener() { // from class: org.bouncycastle.est.ESTService.2
            @Override // org.bouncycastle.mime.MimeParserListener
            public MimeContext createContext(MimeParserContext mimeParserContext, Headers headers) {
                return ConstantMimeContext.Instance;
            }

            @Override // org.bouncycastle.mime.MimeParserListener
            public void object(MimeParserContext mimeParserContext, Headers headers, InputStream inputStream) throws IOException {
                if (headers.getContentType().contains("application/pkcs8")) {
                    ASN1InputStream aSN1InputStream = new ASN1InputStream(inputStream);
                    objArr[0] = PrivateKeyInfo.getInstance(aSN1InputStream.readObject());
                    if (aSN1InputStream.readObject() != null) {
                        throw new ESTException("Unexpected ASN1 object after private key info");
                    }
                    return;
                }
                if (headers.getContentType().contains("application/pkcs7-mime")) {
                    ASN1InputStream aSN1InputStream2 = new ASN1InputStream(inputStream);
                    try {
                        objArr[1] = new SimplePKIResponse(ContentInfo.getInstance(aSN1InputStream2.readObject()));
                        if (aSN1InputStream2.readObject() != null) {
                            throw new ESTException("Unexpected ASN1 object after reading certificates");
                        }
                    } catch (CMCException e4) {
                        throw new IOException(e4.getMessage());
                    }
                }
            }
        });
        if (objArr[0] == null || objArr[1] == null) {
            throw new ESTException("received neither private key info and certificates");
        }
        return new EnrollmentResponse(((SimplePKIResponse) objArr[1]).getCertificates(), -1L, null, eSTResponse.getSource(), PrivateKeyInfo.getInstance(objArr[0]));
    }

    public CSRRequestResponse getCSRAttributes() throws ESTException {
        CSRAttributesResponse cSRAttributesResponse;
        if (!this.clientProvider.isTrusted()) {
            throw new IllegalStateException("No trust anchors.");
        }
        ESTResponse eSTResponse = null;
        Exception exc = null;
        try {
            try {
                URL url = new URL(this.server + CSRATTRS);
                ESTClient eSTClientMakeClient = this.clientProvider.makeClient();
                ESTRequest eSTRequestBuild = new ESTRequestBuilder("GET", url).withClient(eSTClientMakeClient).build();
                ESTResponse eSTResponseDoRequest = eSTClientMakeClient.doRequest(eSTRequestBuild);
                switch (eSTResponseDoRequest.getStatusCode()) {
                    case 200:
                        try {
                            cSRAttributesResponse = new CSRAttributesResponse(CsrAttrs.getInstance(ASN1Sequence.getInstance(getASN1InputStream(eSTResponseDoRequest.getInputStream(), eSTResponseDoRequest.getContentLength()).readObject())));
                        } catch (Throwable th) {
                            throw new ESTException("Decoding CACerts: " + url.toString() + " " + th.getMessage(), th, eSTResponseDoRequest.getStatusCode(), eSTResponseDoRequest.getInputStream());
                        }
                        break;
                    case Barcode128.STARTB /* 204 */:
                        cSRAttributesResponse = null;
                        break;
                    case 404:
                        cSRAttributesResponse = null;
                        break;
                    default:
                        throw new ESTException("CSR Attribute request: " + eSTRequestBuild.getURL().toString(), null, eSTResponseDoRequest.getStatusCode(), eSTResponseDoRequest.getInputStream());
                }
                if (eSTResponseDoRequest != null) {
                    try {
                        eSTResponseDoRequest.close();
                    } catch (Exception e) {
                        exc = e;
                    }
                }
                if (exc == null) {
                    return new CSRRequestResponse(cSRAttributesResponse, eSTResponseDoRequest.getSource());
                }
                if (exc instanceof ESTException) {
                    throw ((ESTException) exc);
                }
                throw new ESTException(exc.getMessage(), exc, eSTResponseDoRequest.getStatusCode(), null);
            } catch (Throwable th2) {
                if (0 != 0) {
                    try {
                        eSTResponse.close();
                    } catch (Exception e2) {
                    }
                }
                throw th2;
            }
        } catch (Throwable th3) {
            if (th3 instanceof ESTException) {
                throw ((ESTException) th3);
            }
            throw new ESTException(th3.getMessage(), th3);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String annotateRequest(byte[] bArr) {
        int length = 0;
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        do {
            if (length + 48 < bArr.length) {
                printWriter.print(Base64.toBase64String(bArr, length, 48));
                length += 48;
            } else {
                printWriter.print(Base64.toBase64String(bArr, length, bArr.length - length));
                length = bArr.length;
            }
            printWriter.print('\n');
        } while (length < bArr.length);
        printWriter.flush();
        return stringWriter.toString();
    }

    private String verifyLabel(String str) {
        while (str.endsWith("/") && str.length() > 0) {
            str = str.substring(0, str.length() - 1);
        }
        while (str.startsWith("/") && str.length() > 0) {
            str = str.substring(1);
        }
        if (str.length() == 0) {
            throw new IllegalArgumentException("Label set but after trimming '/' is not zero length string.");
        }
        if (!pathInValid.matcher(str).matches()) {
            throw new IllegalArgumentException("Server path " + str + " contains invalid characters");
        }
        if (illegalParts.contains(str)) {
            throw new IllegalArgumentException("Label " + str + " is a reserved path segment.");
        }
        return str;
    }

    private String verifyServer(String str) {
        while (str.endsWith("/") && str.length() > 0) {
            try {
                str = str.substring(0, str.length() - 1);
            } catch (Exception e) {
                if (e instanceof IllegalArgumentException) {
                    throw ((IllegalArgumentException) e);
                }
                throw new IllegalArgumentException("Scheme and host is invalid: " + e.getMessage(), e);
            }
        }
        if (str.contains("://")) {
            throw new IllegalArgumentException("Server contains scheme, must only be <dnsname/ipaddress>:port, https:// will be added arbitrarily.");
        }
        URL url = new URL("https://" + str);
        if (url.getPath().length() == 0 || url.getPath().equals("/")) {
            return str;
        }
        throw new IllegalArgumentException("Server contains path, must only be <dnsname/ipaddress>:port, a path of '/.well-known/est/<label>' will be added arbitrarily.");
    }

    static {
        illegalParts.add(CACERTS.substring(1));
        illegalParts.add(SIMPLE_ENROLL.substring(1));
        illegalParts.add(SIMPLE_REENROLL.substring(1));
        illegalParts.add(FULLCMC.substring(1));
        illegalParts.add(SERVERGEN.substring(1));
        illegalParts.add(CSRATTRS.substring(1));
        pathInValid = Pattern.compile("^[0-9a-zA-Z_\\-.~!$&'()*+,;:=]+");
    }
}
