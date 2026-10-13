package es.gob.afirma.core.misc.http;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import javax.net.ssl.X509TrustManager;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/misc/http/MultiX509TrustManager.class */
public class MultiX509TrustManager implements X509TrustManager {
    private final X509TrustManager[] tms;

    public MultiX509TrustManager(X509TrustManager[] x509TrustManagerArr) {
        this.tms = x509TrustManagerArr;
    }

    @Override // javax.net.ssl.X509TrustManager
    public void checkClientTrusted(X509Certificate[] x509CertificateArr, String str) throws CertificateException {
        boolean z = false;
        CertificateException certificateException = null;
        for (int i = 0; i < this.tms.length && !z; i++) {
            try {
                this.tms[i].checkClientTrusted(x509CertificateArr, str);
                z = true;
            } catch (CertificateException e) {
                certificateException = e;
            }
        }
        if (!z && certificateException != null) {
            throw certificateException;
        }
    }

    @Override // javax.net.ssl.X509TrustManager
    public void checkServerTrusted(X509Certificate[] x509CertificateArr, String str) throws CertificateException {
        boolean z = false;
        CertificateException certificateException = null;
        for (int i = 0; i < this.tms.length && !z; i++) {
            try {
                this.tms[i].checkServerTrusted(x509CertificateArr, str);
                z = true;
            } catch (CertificateException e) {
                certificateException = e;
            }
        }
        if (!z && certificateException != null) {
            throw certificateException;
        }
    }

    @Override // javax.net.ssl.X509TrustManager
    public X509Certificate[] getAcceptedIssuers() {
        ArrayList arrayList = new ArrayList();
        for (X509TrustManager x509TrustManager : this.tms) {
            arrayList.addAll(Arrays.asList(x509TrustManager.getAcceptedIssuers()));
        }
        return (X509Certificate[]) arrayList.toArray(new X509Certificate[0]);
    }
}
