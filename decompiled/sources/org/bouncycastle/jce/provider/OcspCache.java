package org.bouncycastle.jce.provider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Extension;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.ocsp.BasicOCSPResponse;
import org.bouncycastle.asn1.ocsp.CertID;
import org.bouncycastle.asn1.ocsp.OCSPObjectIdentifiers;
import org.bouncycastle.asn1.ocsp.OCSPRequest;
import org.bouncycastle.asn1.ocsp.OCSPResponse;
import org.bouncycastle.asn1.ocsp.Request;
import org.bouncycastle.asn1.ocsp.ResponseBytes;
import org.bouncycastle.asn1.ocsp.ResponseData;
import org.bouncycastle.asn1.ocsp.SingleResponse;
import org.bouncycastle.asn1.ocsp.TBSRequest;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.jcajce.PKIXCertRevocationCheckerParameters;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.io.Streams;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jce/provider/OcspCache.class */
class OcspCache {
    private static final int DEFAULT_TIMEOUT = 15000;
    private static final int DEFAULT_MAX_RESPONSE_SIZE = 32768;
    private static Map<URI, WeakReference<Map<CertID, OCSPResponse>>> cache = Collections.synchronizedMap(new WeakHashMap());

    OcspCache() {
    }

    static OCSPResponse getOcspResponse(CertID certID, PKIXCertRevocationCheckerParameters pKIXCertRevocationCheckerParameters, URI uri, X509Certificate x509Certificate, List<Extension> list, JcaJceHelper jcaJceHelper) throws CertPathValidatorException {
        OCSPResponse oCSPResponse;
        WeakReference<Map<CertID, OCSPResponse>> weakReference = cache.get(uri);
        Map<CertID, OCSPResponse> map = weakReference != null ? weakReference.get() : null;
        if (map != null && (oCSPResponse = map.get(certID)) != null) {
            if (isCertIDFoundAndCurrent(BasicOCSPResponse.getInstance(ASN1OctetString.getInstance(oCSPResponse.getResponseBytes().getResponse()).getOctets()), pKIXCertRevocationCheckerParameters.getValidDate(), certID)) {
                return oCSPResponse;
            }
            map.remove(certID);
        }
        try {
            URL url = uri.toURL();
            ASN1EncodableVector aSN1EncodableVector = new ASN1EncodableVector();
            aSN1EncodableVector.add(new Request(certID, null));
            ASN1EncodableVector aSN1EncodableVector2 = new ASN1EncodableVector();
            byte[] bArrClone = null;
            for (int i = 0; i != list.size(); i++) {
                Extension extension = list.get(i);
                ASN1ObjectIdentifier aSN1ObjectIdentifier = new ASN1ObjectIdentifier(extension.getId());
                DEROctetString dEROctetString = new DEROctetString(extension.getValue());
                if (OCSPObjectIdentifiers.id_pkix_ocsp_nonce.equals((ASN1Primitive) aSN1ObjectIdentifier)) {
                    bArrClone = Arrays.clone(dEROctetString.getOctets());
                }
                aSN1EncodableVector2.add(new org.bouncycastle.asn1.x509.Extension(aSN1ObjectIdentifier, extension.isCritical(), dEROctetString));
            }
            try {
                byte[] encoded = new OCSPRequest(aSN1EncodableVector2.size() != 0 ? new TBSRequest((GeneralName) null, new DERSequence(aSN1EncodableVector), Extensions.getInstance(new DERSequence(aSN1EncodableVector2))) : new TBSRequest((GeneralName) null, new DERSequence(aSN1EncodableVector), (Extensions) null), null).getEncoded();
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(15000);
                httpURLConnection.setReadTimeout(15000);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-type", "application/ocsp-request");
                httpURLConnection.setRequestProperty("Content-length", String.valueOf(encoded.length));
                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(encoded);
                outputStream.flush();
                InputStream inputStream = httpURLConnection.getInputStream();
                int contentLength = httpURLConnection.getContentLength();
                if (contentLength < 0) {
                    contentLength = 32768;
                }
                OCSPResponse oCSPResponse2 = OCSPResponse.getInstance(Streams.readAllLimited(inputStream, contentLength));
                if (0 != oCSPResponse2.getResponseStatus().getIntValue()) {
                    throw new CertPathValidatorException("OCSP responder failed: " + oCSPResponse2.getResponseStatus().getValue(), null, pKIXCertRevocationCheckerParameters.getCertPath(), pKIXCertRevocationCheckerParameters.getIndex());
                }
                boolean z = false;
                ResponseBytes responseBytes = ResponseBytes.getInstance(oCSPResponse2.getResponseBytes());
                if (responseBytes.getResponseType().equals((ASN1Primitive) OCSPObjectIdentifiers.id_pkix_ocsp_basic)) {
                    BasicOCSPResponse basicOCSPResponse = BasicOCSPResponse.getInstance(responseBytes.getResponse().getOctets());
                    z = ProvOcspRevocationChecker.validatedOcspResponse(basicOCSPResponse, pKIXCertRevocationCheckerParameters, bArrClone, x509Certificate, jcaJceHelper) && isCertIDFoundAndCurrent(basicOCSPResponse, pKIXCertRevocationCheckerParameters.getValidDate(), certID);
                }
                if (!z) {
                    throw new CertPathValidatorException("OCSP response failed to validate", null, pKIXCertRevocationCheckerParameters.getCertPath(), pKIXCertRevocationCheckerParameters.getIndex());
                }
                WeakReference<Map<CertID, OCSPResponse>> weakReference2 = cache.get(uri);
                if (weakReference2 != null) {
                    map = weakReference2.get();
                }
                if (map != null) {
                    map.put(certID, oCSPResponse2);
                } else {
                    HashMap map2 = new HashMap();
                    map2.put(certID, oCSPResponse2);
                    cache.put(uri, new WeakReference<>(map2));
                }
                return oCSPResponse2;
            } catch (IOException e) {
                throw new CertPathValidatorException("configuration error: " + e.getMessage(), e, pKIXCertRevocationCheckerParameters.getCertPath(), pKIXCertRevocationCheckerParameters.getIndex());
            }
        } catch (MalformedURLException e2) {
            throw new CertPathValidatorException("configuration error: " + e2.getMessage(), e2, pKIXCertRevocationCheckerParameters.getCertPath(), pKIXCertRevocationCheckerParameters.getIndex());
        }
    }

    private static boolean isCertIDFoundAndCurrent(BasicOCSPResponse basicOCSPResponse, Date date, CertID certID) {
        ASN1Sequence responses = ResponseData.getInstance(basicOCSPResponse.getTbsResponseData()).getResponses();
        for (int i = 0; i != responses.size(); i++) {
            SingleResponse singleResponse = SingleResponse.getInstance(responses.getObjectAt(i));
            if (certID.equals(singleResponse.getCertID())) {
                ASN1GeneralizedTime nextUpdate = singleResponse.getNextUpdate();
                if (nextUpdate == null) {
                    return true;
                }
                try {
                    return !date.after(nextUpdate.getDate());
                } catch (ParseException e) {
                    return false;
                }
            }
        }
        return false;
    }
}
