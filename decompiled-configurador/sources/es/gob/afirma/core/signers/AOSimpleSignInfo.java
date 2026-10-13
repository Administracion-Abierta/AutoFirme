package es.gob.afirma.core.signers;

import es.gob.afirma.core.misc.AOUtil;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.util.Date;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/signers/AOSimpleSignInfo.class */
public final class AOSimpleSignInfo {
    private X509Certificate[] certs;
    private Date signingTime;
    private String signAlgorithm = null;
    private String signFormat = null;
    private Date[] timestampingTime = null;
    private byte[] pkcs1 = null;

    public AOSimpleSignInfo(X509Certificate[] x509CertificateArr, Date date) {
        this.certs = null;
        this.signingTime = null;
        if (x509CertificateArr == null || x509CertificateArr.length == 0 || x509CertificateArr[0] == null) {
            throw new IllegalArgumentException("No se ha introducido la cadena de certificacion");
        }
        this.certs = (X509Certificate[]) x509CertificateArr.clone();
        this.signingTime = date;
    }

    public String getSignAlgorithm() {
        return this.signAlgorithm;
    }

    public void setSignAlgorithm(String str) {
        this.signAlgorithm = str;
    }

    public String getSignFormat() {
        return this.signFormat;
    }

    public void setSignFormat(String str) {
        this.signFormat = str;
    }

    public Date[] getTimestampingTime() {
        if (this.timestampingTime == null) {
            return null;
        }
        return (Date[]) this.timestampingTime.clone();
    }

    public void setTimestampingTime(Date[] dateArr) {
        this.timestampingTime = dateArr == null ? null : (Date[]) dateArr.clone();
    }

    public X509Certificate[] getCerts() {
        if (this.certs == null) {
            return null;
        }
        return (X509Certificate[]) this.certs.clone();
    }

    public Date getSigningTime() {
        return this.signingTime;
    }

    public boolean isTimeStamped() {
        return (this.timestampingTime == null || this.timestampingTime.length <= 0 || this.timestampingTime[0] == null) ? false : true;
    }

    public byte[] getPkcs1() {
        if (this.pkcs1 == null) {
            return null;
        }
        return (byte[]) this.pkcs1.clone();
    }

    public void setPkcs1(byte[] bArr) {
        this.pkcs1 = bArr == null ? null : (byte[]) bArr.clone();
    }

    public String toString() {
        String cn = AOUtil.getCN(this.certs[0]);
        if (this.timestampingTime != null && this.timestampingTime.length > 0 && this.timestampingTime[0] != null) {
            cn = cn + " (TimeStamp: " + DateFormat.getDateTimeInstance(2, 3).format(this.timestampingTime[0]) + ")";
        } else if (this.signingTime != null) {
            cn = cn + " (" + DateFormat.getDateTimeInstance(2, 3).format(this.signingTime) + ")";
        }
        return cn;
    }
}
