package org.spongycastle.est;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;
import org.spongycastle.asn1.DERNull;
import org.spongycastle.asn1.nist.NISTObjectIdentifiers;
import org.spongycastle.asn1.x509.AlgorithmIdentifier;
import org.spongycastle.cms.CMSAttributeTableGenerator;
import org.spongycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.spongycastle.operator.DigestAlgorithmIdentifierFinder;
import org.spongycastle.operator.DigestCalculator;
import org.spongycastle.operator.DigestCalculatorProvider;
import org.spongycastle.operator.OperatorCreationException;
import org.spongycastle.util.Strings;
import org.spongycastle.util.encoders.Base64;
import org.spongycastle.util.encoders.Hex;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:org/spongycastle/est/HttpAuth.class */
public class HttpAuth implements ESTAuth {
    private static final DigestAlgorithmIdentifierFinder digestAlgorithmIdentifierFinder = new DefaultDigestAlgorithmIdentifierFinder();
    private final String realm;
    private final String username;
    private final char[] password;
    private final SecureRandom nonceGenerator;
    private final DigestCalculatorProvider digestCalculatorProvider;
    private static final Set<String> validParts;

    static {
        HashSet<String> s = new HashSet<>();
        s.add("realm");
        s.add("nonce");
        s.add("opaque");
        s.add("algorithm");
        s.add("qop");
        validParts = Collections.unmodifiableSet(s);
    }

    public HttpAuth(String username, char[] password) {
        this(null, username, password, null, null);
    }

    public HttpAuth(String realm, String username, char[] password) {
        this(realm, username, password, null, null);
    }

    public HttpAuth(String username, char[] password, SecureRandom nonceGenerator, DigestCalculatorProvider digestCalculatorProvider) {
        this(null, username, password, nonceGenerator, digestCalculatorProvider);
    }

    public HttpAuth(String realm, String username, char[] password, SecureRandom nonceGenerator, DigestCalculatorProvider digestCalculatorProvider) {
        this.realm = realm;
        this.username = username;
        this.password = password;
        this.nonceGenerator = nonceGenerator;
        this.digestCalculatorProvider = digestCalculatorProvider;
    }

    @Override // org.spongycastle.est.ESTAuth
    public void applyAuth(ESTRequestBuilder reqBldr) {
        reqBldr.withHijacker(new ESTHijacker() { // from class: org.spongycastle.est.HttpAuth.1
            @Override // org.spongycastle.est.ESTHijacker
            public ESTResponse hijack(ESTRequest req, Source sock) throws IOException {
                ESTResponse res;
                ESTResponse res2 = new ESTResponse(req, sock);
                if (res2.getStatusCode() == 401) {
                    String authHeader = res2.getHeader("WWW-Authenticate");
                    if (authHeader == null) {
                        throw new ESTException("Status of 401 but no WWW-Authenticate header");
                    }
                    String authHeader2 = Strings.toLowerCase(authHeader);
                    if (authHeader2.startsWith(CMSAttributeTableGenerator.DIGEST)) {
                        res = HttpAuth.this.doDigestFunction(res2);
                    } else if (authHeader2.startsWith("basic")) {
                        res2.close();
                        Map<String, String> s = HttpUtil.splitCSL("Basic", res2.getHeader("WWW-Authenticate"));
                        if (HttpAuth.this.realm != null && !HttpAuth.this.realm.equals(s.get("realm"))) {
                            throw new ESTException("Supplied realm '" + HttpAuth.this.realm + "' does not match server realm '" + s.get("realm") + "'", null, 401, null);
                        }
                        ESTRequestBuilder answer = new ESTRequestBuilder(req).withHijacker(null);
                        if (HttpAuth.this.realm != null && HttpAuth.this.realm.length() > 0) {
                            answer.setHeader("WWW-Authenticate", "Basic realm=\"" + HttpAuth.this.realm + "\"");
                        }
                        if (!HttpAuth.this.username.contains(":")) {
                            String userPass = HttpAuth.this.username + ":" + new String(HttpAuth.this.password);
                            answer.setHeader("Authorization", "Basic " + Base64.toBase64String(userPass.getBytes()));
                            res = req.getClient().doRequest(answer.build());
                        } else {
                            throw new IllegalArgumentException("User must not contain a ':'");
                        }
                    } else {
                        throw new ESTException("Unknown auth mode: " + authHeader2);
                    }
                    return res;
                }
                return res2;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ESTResponse doDigestFunction(ESTResponse res) throws IOException {
        res.close();
        ESTRequest req = res.getOriginalRequest();
        try {
            Map<String, String> parts = HttpUtil.splitCSL("Digest", res.getHeader("WWW-Authenticate"));
            try {
                String uri = req.getURL().toURI().getPath();
                for (Object k : parts.keySet()) {
                    if (!validParts.contains(k)) {
                        throw new ESTException("Unrecognised entry in WWW-Authenticate header: '" + k + "'");
                    }
                }
                String method = req.getMethod();
                String realm = parts.get("realm");
                String nonce = parts.get("nonce");
                String opaque = parts.get("opaque");
                String algorithm = parts.get("algorithm");
                String qop = parts.get("qop");
                List<String> qopMods = new ArrayList<>();
                if (this.realm != null && !this.realm.equals(realm)) {
                    throw new ESTException("Supplied realm '" + this.realm + "' does not match server realm '" + realm + "'", null, 401, null);
                }
                if (algorithm == null) {
                    algorithm = MessageDigestAlgorithms.MD5;
                }
                if (algorithm.length() == 0) {
                    throw new ESTException("WWW-Authenticate no algorithm defined.");
                }
                String algorithm2 = Strings.toUpperCase(algorithm);
                if (qop != null) {
                    if (qop.length() == 0) {
                        throw new ESTException("QoP value is empty.");
                    }
                    String[] s = Strings.toLowerCase(qop).split(",");
                    for (int j = 0; j != s.length; j++) {
                        if (!s[j].equals("auth") && !s[j].equals("auth-int")) {
                            throw new ESTException("QoP value unknown: '" + j + "'");
                        }
                        String jt = s[j].trim();
                        if (!qopMods.contains(jt)) {
                            qopMods.add(jt);
                        }
                    }
                    AlgorithmIdentifier digestAlg = lookupDigest(algorithm2);
                    if (digestAlg == null || digestAlg.getAlgorithm() == null) {
                        throw new IOException("auth digest algorithm unknown: " + algorithm2);
                    }
                    DigestCalculator dCalc = getDigestCalculator(algorithm2, digestAlg);
                    OutputStream dOut = dCalc.getOutputStream();
                    String crnonce = makeNonce(10);
                    update(dOut, this.username);
                    update(dOut, ":");
                    update(dOut, realm);
                    update(dOut, ":");
                    update(dOut, this.password);
                    dOut.close();
                    byte[] ha1 = dCalc.getDigest();
                    if (algorithm2.endsWith("-SESS")) {
                        DigestCalculator sessCalc = getDigestCalculator(algorithm2, digestAlg);
                        OutputStream sessOut = sessCalc.getOutputStream();
                        String cs = Hex.toHexString(ha1);
                        update(sessOut, cs);
                        update(sessOut, ":");
                        update(sessOut, nonce);
                        update(sessOut, ":");
                        update(sessOut, crnonce);
                        sessOut.close();
                        ha1 = sessCalc.getDigest();
                    }
                    String hashHa1 = Hex.toHexString(ha1);
                    DigestCalculator authCalc = getDigestCalculator(algorithm2, digestAlg);
                    OutputStream authOut = authCalc.getOutputStream();
                    if (qopMods.get(0).equals("auth-int")) {
                        DigestCalculator reqCalc = getDigestCalculator(algorithm2, digestAlg);
                        OutputStream reqOut = reqCalc.getOutputStream();
                        req.writeData(reqOut);
                        reqOut.close();
                        byte[] b = reqCalc.getDigest();
                        update(authOut, method);
                        update(authOut, ":");
                        update(authOut, uri);
                        update(authOut, ":");
                        update(authOut, Hex.toHexString(b));
                    } else if (qopMods.get(0).equals("auth")) {
                        update(authOut, method);
                        update(authOut, ":");
                        update(authOut, uri);
                    }
                    authOut.close();
                    String hashHa2 = Hex.toHexString(authCalc.getDigest());
                    DigestCalculator responseCalc = getDigestCalculator(algorithm2, digestAlg);
                    OutputStream responseOut = responseCalc.getOutputStream();
                    if (qopMods.contains("missing")) {
                        update(responseOut, hashHa1);
                        update(responseOut, ":");
                        update(responseOut, nonce);
                        update(responseOut, ":");
                        update(responseOut, hashHa2);
                    } else {
                        update(responseOut, hashHa1);
                        update(responseOut, ":");
                        update(responseOut, nonce);
                        update(responseOut, ":");
                        update(responseOut, "00000001");
                        update(responseOut, ":");
                        update(responseOut, crnonce);
                        update(responseOut, ":");
                        if (qopMods.get(0).equals("auth-int")) {
                            update(responseOut, "auth-int");
                        } else {
                            update(responseOut, "auth");
                        }
                        update(responseOut, ":");
                        update(responseOut, hashHa2);
                    }
                    responseOut.close();
                    String digest = Hex.toHexString(responseCalc.getDigest());
                    Map<String, String> hdr = new HashMap<>();
                    hdr.put("username", this.username);
                    hdr.put("realm", realm);
                    hdr.put("nonce", nonce);
                    hdr.put("uri", uri);
                    hdr.put("response", digest);
                    if (qopMods.get(0).equals("auth-int")) {
                        hdr.put("qop", "auth-int");
                        hdr.put("nc", "00000001");
                        hdr.put("cnonce", crnonce);
                    } else if (qopMods.get(0).equals("auth")) {
                        hdr.put("qop", "auth");
                        hdr.put("nc", "00000001");
                        hdr.put("cnonce", crnonce);
                    }
                    hdr.put("algorithm", algorithm2);
                    if (opaque == null || opaque.length() == 0) {
                        hdr.put("opaque", makeNonce(20));
                    }
                    ESTRequestBuilder answer = new ESTRequestBuilder(req).withHijacker(null);
                    answer.setHeader("Authorization", HttpUtil.mergeCSL("Digest", hdr));
                    return req.getClient().doRequest(answer.build());
                }
                throw new ESTException("Qop is not defined in WWW-Authenticate header.");
            } catch (Exception e) {
                throw new IOException("unable to process URL in request: " + e.getMessage());
            }
        } catch (Throwable t) {
            throw new ESTException("Parsing WWW-Authentication header: " + t.getMessage(), t, res.getStatusCode(), new ByteArrayInputStream(res.getHeader("WWW-Authenticate").getBytes()));
        }
    }

    private DigestCalculator getDigestCalculator(String algorithm, AlgorithmIdentifier digestAlg) throws IOException {
        try {
            DigestCalculator dCalc = this.digestCalculatorProvider.get(digestAlg);
            return dCalc;
        } catch (OperatorCreationException e) {
            throw new IOException("cannot create digest calculator for " + algorithm + ": " + e.getMessage());
        }
    }

    private AlgorithmIdentifier lookupDigest(String algorithm) {
        if (algorithm.endsWith("-SESS")) {
            algorithm = algorithm.substring(0, algorithm.length() - "-SESS".length());
        }
        if (algorithm.equals("SHA-512-256")) {
            return new AlgorithmIdentifier(NISTObjectIdentifiers.id_sha512_256, DERNull.INSTANCE);
        }
        return digestAlgorithmIdentifierFinder.find(algorithm);
    }

    private void update(OutputStream dOut, char[] value) throws IOException {
        dOut.write(Strings.toUTF8ByteArray(value));
    }

    private void update(OutputStream dOut, String value) throws IOException {
        dOut.write(Strings.toUTF8ByteArray(value));
    }

    private String makeNonce(int len) {
        byte[] b = new byte[len];
        this.nonceGenerator.nextBytes(b);
        return Hex.toHexString(b);
    }
}
