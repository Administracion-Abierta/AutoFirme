package org.spongycastle.est;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.asn1.ASN1Sequence;
import org.spongycastle.asn1.DERPrintableString;
import org.spongycastle.asn1.cms.ContentInfo;
import org.spongycastle.asn1.est.CsrAttrs;
import org.spongycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.spongycastle.asn1.x509.DisplayText;
import org.spongycastle.cert.X509CRLHolder;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.cmc.CMCException;
import org.spongycastle.cmc.SimplePKIResponse;
import org.spongycastle.operator.ContentSigner;
import org.spongycastle.pkcs.PKCS10CertificationRequest;
import org.spongycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.spongycastle.util.Selector;
import org.spongycastle.util.Store;
import org.spongycastle.util.encoders.Base64;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/est/ESTService.class */
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
    private static final Pattern pathInvalid;

    static {
        illegalParts.add(CACERTS.substring(1));
        illegalParts.add(SIMPLE_ENROLL.substring(1));
        illegalParts.add(SIMPLE_REENROLL.substring(1));
        illegalParts.add(FULLCMC.substring(1));
        illegalParts.add(SERVERGEN.substring(1));
        illegalParts.add(CSRATTRS.substring(1));
        pathInvalid = Pattern.compile("^[0-9a-zA-Z_\\-.~!$&'()*+,;=]+");
    }

    ESTService(String serverAuthority, String label, ESTClientProvider clientProvider) {
        String serverAuthority2 = verifyServer(serverAuthority);
        if (label != null) {
            this.server = "https://" + serverAuthority2 + "/.well-known/est/" + verifyLabel(label);
        } else {
            this.server = "https://" + serverAuthority2 + "/.well-known/est";
        }
        this.clientProvider = clientProvider;
    }

    public static X509CertificateHolder[] storeToArray(Store<X509CertificateHolder> store) {
        return storeToArray(store, null);
    }

    public static X509CertificateHolder[] storeToArray(Store<X509CertificateHolder> store, Selector<X509CertificateHolder> selector) {
        Collection<X509CertificateHolder> c = store.getMatches(selector);
        return (X509CertificateHolder[]) c.toArray(new X509CertificateHolder[c.size()]);
    }

    public CACertsResponse getCACerts() throws Exception {
        ESTResponse resp = null;
        Exception finalThrowable = null;
        try {
            try {
                URL url = new URL(this.server + CACERTS);
                ESTClient client = this.clientProvider.makeClient();
                ESTRequest req = new ESTRequestBuilder("GET", url).withClient(client).build();
                ESTResponse resp2 = client.doRequest(req);
                Store<X509CertificateHolder> caCerts = null;
                Store<X509CRLHolder> crlHolderStore = null;
                if (resp2.getStatusCode() == 200) {
                    if (!"application/pkcs7-mime".equals(resp2.getHeaders().getFirstValue("Content-Type"))) {
                        String j = resp2.getHeaders().getFirstValue("Content-Type") != null ? " got " + resp2.getHeaders().getFirstValue("Content-Type") : " but was not present.";
                        throw new ESTException("Response : " + url.toString() + "Expecting application/pkcs7-mime " + j, null, resp2.getStatusCode(), resp2.getInputStream());
                    }
                    try {
                        if (resp2.getContentLength() != null && resp2.getContentLength().longValue() > 0) {
                            ASN1InputStream ain = new ASN1InputStream(resp2.getInputStream());
                            SimplePKIResponse spkr = new SimplePKIResponse(ContentInfo.getInstance((ASN1Sequence) ain.readObject()));
                            caCerts = spkr.getCertificates();
                            crlHolderStore = spkr.getCRLs();
                        }
                    } catch (Throwable ex) {
                        throw new ESTException("Decoding CACerts: " + url.toString() + " " + ex.getMessage(), ex, resp2.getStatusCode(), resp2.getInputStream());
                    }
                } else if (resp2.getStatusCode() != 204) {
                    throw new ESTException("Get CACerts: " + url.toString(), null, resp2.getStatusCode(), resp2.getInputStream());
                }
                CACertsResponse caCertsResponse = new CACertsResponse(caCerts, crlHolderStore, req, resp2.getSource(), this.clientProvider.isTrusted());
                if (resp2 != null) {
                    try {
                        resp2.close();
                    } catch (Exception t) {
                        finalThrowable = t;
                    }
                }
                if (finalThrowable == null) {
                    return caCertsResponse;
                }
                if (finalThrowable instanceof ESTException) {
                    throw finalThrowable;
                }
                throw new ESTException("Get CACerts: " + url.toString(), finalThrowable, resp2.getStatusCode(), null);
            } catch (Throwable t2) {
                if (t2 instanceof ESTException) {
                    throw ((ESTException) t2);
                }
                throw new ESTException(t2.getMessage(), t2);
            }
        } catch (Throwable th) {
            if (0 != 0) {
                try {
                    resp.close();
                } catch (Exception e) {
                }
            }
            throw th;
        }
    }

    public EnrollmentResponse simpleEnroll(EnrollmentResponse priorResponse) throws Exception {
        if (!this.clientProvider.isTrusted()) {
            throw new IllegalStateException("No trust anchors.");
        }
        ESTResponse resp = null;
        try {
            try {
                ESTClient client = this.clientProvider.makeClient();
                resp = client.doRequest(new ESTRequestBuilder(priorResponse.getRequestToRetry()).withClient(client).build());
                EnrollmentResponse enrollmentResponseHandleEnrollResponse = handleEnrollResponse(resp);
                if (resp != null) {
                    resp.close();
                }
                return enrollmentResponseHandleEnrollResponse;
            } catch (Throwable t) {
                if (t instanceof ESTException) {
                    throw ((ESTException) t);
                }
                throw new ESTException(t.getMessage(), t);
            }
        } catch (Throwable th) {
            if (resp != null) {
                resp.close();
            }
            throw th;
        }
    }

    public EnrollmentResponse simpleEnroll(boolean reenroll, PKCS10CertificationRequest certificationRequest, ESTAuth auth) throws IOException {
        if (!this.clientProvider.isTrusted()) {
            throw new IllegalStateException("No trust anchors.");
        }
        ESTResponse resp = null;
        try {
            try {
                byte[] data = annotateRequest(certificationRequest.getEncoded()).getBytes();
                URL url = new URL(this.server + (reenroll ? SIMPLE_REENROLL : SIMPLE_ENROLL));
                ESTClient client = this.clientProvider.makeClient();
                ESTRequestBuilder req = new ESTRequestBuilder("POST", url).withData(data).withClient(client);
                req.addHeader("Content-Type", "application/pkcs10");
                req.addHeader("Content-Length", "" + data.length);
                req.addHeader("Content-Transfer-Encoding", "base64");
                if (auth != null) {
                    auth.applyAuth(req);
                }
                resp = client.doRequest(req.build());
                EnrollmentResponse enrollmentResponseHandleEnrollResponse = handleEnrollResponse(resp);
                if (resp != null) {
                    resp.close();
                }
                return enrollmentResponseHandleEnrollResponse;
            } catch (Throwable t) {
                if (t instanceof ESTException) {
                    throw ((ESTException) t);
                }
                throw new ESTException(t.getMessage(), t);
            }
        } catch (Throwable th) {
            if (resp != null) {
                resp.close();
            }
            throw th;
        }
    }

    public EnrollmentResponse simpleEnrollPoP(boolean reEnroll, final PKCS10CertificationRequestBuilder builder, final ContentSigner contentSigner, ESTAuth auth) throws IOException {
        if (!this.clientProvider.isTrusted()) {
            throw new IllegalStateException("No trust anchors.");
        }
        ESTResponse resp = null;
        try {
            try {
                URL url = new URL(this.server + (reEnroll ? SIMPLE_REENROLL : SIMPLE_ENROLL));
                ESTClient client = this.clientProvider.makeClient();
                ESTRequestBuilder reqBldr = new ESTRequestBuilder("POST", url).withClient(client).withConnectionListener(new ESTSourceConnectionListener() { // from class: org.spongycastle.est.ESTService.1
                    @Override // org.spongycastle.est.ESTSourceConnectionListener
                    public ESTRequest onConnection(Source source, ESTRequest request) throws IOException {
                        if ((source instanceof TLSUniqueProvider) && ((TLSUniqueProvider) source).isTLSUniqueAvailable()) {
                            PKCS10CertificationRequestBuilder localBuilder = new PKCS10CertificationRequestBuilder(builder);
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            byte[] tlsUnique = ((TLSUniqueProvider) source).getTLSUnique();
                            localBuilder.setAttribute(PKCSObjectIdentifiers.pkcs_9_at_challengePassword, new DERPrintableString(Base64.toBase64String(tlsUnique)));
                            bos.write(ESTService.this.annotateRequest(localBuilder.build(contentSigner).getEncoded()).getBytes());
                            bos.flush();
                            ESTRequestBuilder reqBuilder = new ESTRequestBuilder(request).withData(bos.toByteArray());
                            reqBuilder.setHeader("Content-Type", "application/pkcs10");
                            reqBuilder.setHeader("Content-Transfer-Encoding", "base64");
                            reqBuilder.setHeader("Content-Length", Long.toString(bos.size()));
                            return reqBuilder.build();
                        }
                        throw new IOException("Source does not supply TLS unique.");
                    }
                });
                if (auth != null) {
                    auth.applyAuth(reqBldr);
                }
                resp = client.doRequest(reqBldr.build());
                EnrollmentResponse enrollmentResponseHandleEnrollResponse = handleEnrollResponse(resp);
                if (resp != null) {
                    resp.close();
                }
                return enrollmentResponseHandleEnrollResponse;
            } catch (Throwable t) {
                if (t instanceof ESTException) {
                    throw ((ESTException) t);
                }
                throw new ESTException(t.getMessage(), t);
            }
        } catch (Throwable th) {
            if (resp != null) {
                resp.close();
            }
            throw th;
        }
    }

    protected EnrollmentResponse handleEnrollResponse(ESTResponse resp) throws IOException {
        long notBefore;
        ESTRequest req = resp.getOriginalRequest();
        if (resp.getStatusCode() == 202) {
            String rt = resp.getHeader("Retry-After");
            if (rt == null) {
                throw new ESTException("Got Status 202 but not Retry-After header from: " + req.getURL().toString());
            }
            try {
                notBefore = System.currentTimeMillis() + (Long.parseLong(rt) * 1000);
            } catch (NumberFormatException e) {
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
                    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                    notBefore = dateFormat.parse(rt).getTime();
                } catch (Exception ex) {
                    throw new ESTException("Unable to parse Retry-After header:" + req.getURL().toString() + " " + ex.getMessage(), null, resp.getStatusCode(), resp.getInputStream());
                }
            }
            return new EnrollmentResponse(null, notBefore, req, resp.getSource());
        }
        if (resp.getStatusCode() == 200) {
            ASN1InputStream ain = new ASN1InputStream(resp.getInputStream());
            try {
                SimplePKIResponse spkr = new SimplePKIResponse(ContentInfo.getInstance(ain.readObject()));
                Store<X509CertificateHolder> enrolled = spkr.getCertificates();
                return new EnrollmentResponse(enrolled, -1L, null, resp.getSource());
            } catch (CMCException e2) {
                throw new ESTException(e2.getMessage(), e2.getCause());
            }
        }
        throw new ESTException("Simple Enroll: " + req.getURL().toString(), null, resp.getStatusCode(), resp.getInputStream());
    }

    public CSRRequestResponse getCSRAttributes() throws ESTException {
        if (!this.clientProvider.isTrusted()) {
            throw new IllegalStateException("No trust anchors.");
        }
        ESTResponse resp = null;
        CSRAttributesResponse response = null;
        Exception finalThrowable = null;
        try {
            try {
                URL url = new URL(this.server + CSRATTRS);
                ESTClient client = this.clientProvider.makeClient();
                ESTRequest req = new ESTRequestBuilder("GET", url).withClient(client).build();
                ESTResponse resp2 = client.doRequest(req);
                switch (resp2.getStatusCode()) {
                    case DisplayText.DISPLAY_TEXT_MAXIMUM_SIZE /* 200 */:
                        try {
                            if (resp2.getContentLength() != null && resp2.getContentLength().longValue() > 0) {
                                ASN1InputStream ain = new ASN1InputStream(resp2.getInputStream());
                                ASN1Sequence seq = (ASN1Sequence) ain.readObject();
                                response = new CSRAttributesResponse(CsrAttrs.getInstance(seq));
                            }
                        } catch (Throwable ex) {
                            throw new ESTException("Decoding CACerts: " + url.toString() + " " + ex.getMessage(), ex, resp2.getStatusCode(), resp2.getInputStream());
                        }
                        break;
                    case 204:
                        response = null;
                        break;
                    case 404:
                        response = null;
                        break;
                    default:
                        throw new ESTException("CSR Attribute request: " + req.getURL().toString(), null, resp2.getStatusCode(), resp2.getInputStream());
                }
                if (resp2 != null) {
                    try {
                        resp2.close();
                    } catch (Exception ex2) {
                        finalThrowable = ex2;
                    }
                }
                if (finalThrowable == null) {
                    return new CSRRequestResponse(response, resp2.getSource());
                }
                if (finalThrowable instanceof ESTException) {
                    throw ((ESTException) finalThrowable);
                }
                throw new ESTException(finalThrowable.getMessage(), finalThrowable, resp2.getStatusCode(), null);
            } catch (Throwable t) {
                if (t instanceof ESTException) {
                    throw ((ESTException) t);
                }
                throw new ESTException(t.getMessage(), t);
            }
        } catch (Throwable th) {
            if (0 != 0) {
                try {
                    resp.close();
                } catch (Exception e) {
                }
            }
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String annotateRequest(byte[] data) {
        int i = 0;
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        do {
            if (i + 48 < data.length) {
                pw.print(Base64.toBase64String(data, i, 48));
                i += 48;
            } else {
                pw.print(Base64.toBase64String(data, i, data.length - i));
                i = data.length;
            }
            pw.print('\n');
        } while (i < data.length);
        pw.flush();
        return sw.toString();
    }

    private String verifyLabel(String label) {
        while (label.endsWith("/") && label.length() > 0) {
            label = label.substring(0, label.length() - 1);
        }
        while (label.startsWith("/") && label.length() > 0) {
            label = label.substring(1);
        }
        if (label.length() == 0) {
            throw new IllegalArgumentException("Label set but after trimming '/' is not zero length string.");
        }
        if (!pathInvalid.matcher(label).matches()) {
            throw new IllegalArgumentException("Server path " + label + " contains invalid characters");
        }
        if (illegalParts.contains(label)) {
            throw new IllegalArgumentException("Label " + label + " is a reserved path segment.");
        }
        return label;
    }

    private String verifyServer(String server) {
        while (server.endsWith("/") && server.length() > 0) {
            try {
                server = server.substring(0, server.length() - 1);
            } catch (Exception ex) {
                if (ex instanceof IllegalArgumentException) {
                    throw ((IllegalArgumentException) ex);
                }
                throw new IllegalArgumentException("Scheme and host is invalid: " + ex.getMessage(), ex);
            }
        }
        if (server.contains("://")) {
            throw new IllegalArgumentException("Server contains scheme, must only be <dnsname/ipaddress>:port, https:// will be added arbitrarily.");
        }
        URL u = new URL("https://" + server);
        if (u.getPath().length() == 0 || u.getPath().equals("/")) {
            return server;
        }
        throw new IllegalArgumentException("Server contains path, must only be <dnsname/ipaddress>:port, a path of '/.well-known/est/<label>' will be added arbitrarily.");
    }
}
