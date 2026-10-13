package org.bouncycastle.jcajce.provider.keystore.util;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.jcajce.BCLoadStoreParameter;
import org.bouncycastle.jcajce.provider.util.DigestFactory;
import org.bouncycastle.jcajce.util.JcaJceHelper;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;
import org.bouncycastle.util.io.Streams;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/keystore/util/JKSKeyStoreSpi.class */
public class JKSKeyStoreSpi extends KeyStoreSpi {
    private static final String NOT_IMPLEMENTED_MESSAGE = "BC JKS store is read-only and only supports certificate entries";
    private final Hashtable<String, BCJKSTrustedCertEntry> certificateEntries = new Hashtable<>();
    private final JcaJceHelper helper;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/keystore/util/JKSKeyStoreSpi$BCJKSTrustedCertEntry.class */
    private static final class BCJKSTrustedCertEntry {
        final Date date;
        final Certificate cert;

        public BCJKSTrustedCertEntry(Date date, Certificate certificate) {
            this.date = date;
            this.cert = certificate;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/jcajce/provider/keystore/util/JKSKeyStoreSpi$ErasableByteStream.class */
    private static final class ErasableByteStream extends ByteArrayInputStream {
        public ErasableByteStream(byte[] bArr, int i, int i2) {
            super(bArr, i, i2);
        }

        public void erase() {
            Arrays.fill(this.buf, (byte) 0);
        }
    }

    public JKSKeyStoreSpi(JcaJceHelper jcaJceHelper) {
        this.helper = jcaJceHelper;
    }

    @Override // java.security.KeyStoreSpi
    public boolean engineProbe(InputStream inputStream) throws IOException {
        DataInputStream dataInputStream = inputStream instanceof DataInputStream ? (DataInputStream) inputStream : new DataInputStream(inputStream);
        int i = dataInputStream.readInt();
        int i2 = dataInputStream.readInt();
        return i == -17957139 && (i2 == 1 || i2 == 2);
    }

    @Override // java.security.KeyStoreSpi
    public Key engineGetKey(String str, char[] cArr) throws NoSuchAlgorithmException, UnrecoverableKeyException {
        return null;
    }

    @Override // java.security.KeyStoreSpi
    public Certificate[] engineGetCertificateChain(String str) {
        return null;
    }

    @Override // java.security.KeyStoreSpi
    public Certificate engineGetCertificate(String str) {
        synchronized (this.certificateEntries) {
            BCJKSTrustedCertEntry bCJKSTrustedCertEntry = this.certificateEntries.get(str);
            if (bCJKSTrustedCertEntry == null) {
                return null;
            }
            return bCJKSTrustedCertEntry.cert;
        }
    }

    @Override // java.security.KeyStoreSpi
    public Date engineGetCreationDate(String str) {
        synchronized (this.certificateEntries) {
            BCJKSTrustedCertEntry bCJKSTrustedCertEntry = this.certificateEntries.get(str);
            if (bCJKSTrustedCertEntry == null) {
                return null;
            }
            return bCJKSTrustedCertEntry.date;
        }
    }

    @Override // java.security.KeyStoreSpi
    public void engineSetKeyEntry(String str, Key key, char[] cArr, Certificate[] certificateArr) throws KeyStoreException {
        throw new KeyStoreException(NOT_IMPLEMENTED_MESSAGE);
    }

    @Override // java.security.KeyStoreSpi
    public void engineSetKeyEntry(String str, byte[] bArr, Certificate[] certificateArr) throws KeyStoreException {
        throw new KeyStoreException(NOT_IMPLEMENTED_MESSAGE);
    }

    @Override // java.security.KeyStoreSpi
    public void engineSetCertificateEntry(String str, Certificate certificate) throws KeyStoreException {
        throw new KeyStoreException(NOT_IMPLEMENTED_MESSAGE);
    }

    @Override // java.security.KeyStoreSpi
    public void engineDeleteEntry(String str) throws KeyStoreException {
        throw new KeyStoreException(NOT_IMPLEMENTED_MESSAGE);
    }

    @Override // java.security.KeyStoreSpi
    public Enumeration<String> engineAliases() {
        Enumeration<String> enumerationKeys;
        synchronized (this.certificateEntries) {
            enumerationKeys = this.certificateEntries.keys();
        }
        return enumerationKeys;
    }

    @Override // java.security.KeyStoreSpi
    public boolean engineContainsAlias(String str) {
        boolean zContainsKey;
        if (str == null) {
            throw new NullPointerException("alias value is null");
        }
        synchronized (this.certificateEntries) {
            zContainsKey = this.certificateEntries.containsKey(str);
        }
        return zContainsKey;
    }

    @Override // java.security.KeyStoreSpi
    public int engineSize() {
        return this.certificateEntries.size();
    }

    @Override // java.security.KeyStoreSpi
    public boolean engineIsKeyEntry(String str) {
        return false;
    }

    @Override // java.security.KeyStoreSpi
    public boolean engineIsCertificateEntry(String str) {
        boolean zContainsKey;
        synchronized (this.certificateEntries) {
            zContainsKey = this.certificateEntries.containsKey(str);
        }
        return zContainsKey;
    }

    @Override // java.security.KeyStoreSpi
    public String engineGetCertificateAlias(Certificate certificate) {
        synchronized (this.certificateEntries) {
            for (Map.Entry<String, BCJKSTrustedCertEntry> entry : this.certificateEntries.entrySet()) {
                if (entry.getValue().cert.equals(certificate)) {
                    return entry.getKey();
                }
            }
            return null;
        }
    }

    @Override // java.security.KeyStoreSpi
    public void engineStore(OutputStream outputStream, char[] cArr) throws NoSuchAlgorithmException, IOException, CertificateException {
        throw new IOException(NOT_IMPLEMENTED_MESSAGE);
    }

    @Override // java.security.KeyStoreSpi
    public void engineLoad(KeyStore.LoadStoreParameter loadStoreParameter) throws NoSuchAlgorithmException, IOException, CertificateException {
        if (loadStoreParameter == null) {
            engineLoad(null, null);
        } else {
            if (!(loadStoreParameter instanceof BCLoadStoreParameter)) {
                throw new IllegalArgumentException("no support for 'param' of type " + loadStoreParameter.getClass().getName());
            }
            engineLoad(((BCLoadStoreParameter) loadStoreParameter).getInputStream(), ParameterUtil.extractPassword(loadStoreParameter));
        }
    }

    /* JADX WARN: Code duplicated, block: B:18:0x0086 A[Catch: all -> 0x018f, all -> 0x019e, TryCatch #2 {all -> 0x018f, blocks: (B:7:0x0014, B:10:0x003b, B:11:0x0054, B:15:0x0075, B:18:0x0086, B:19:0x008f, B:20:0x00a8, B:21:0x00b1, B:22:0x00b2, B:24:0x00cd, B:26:0x00de, B:27:0x00ed, B:28:0x00ff, B:29:0x0122, B:31:0x0133, B:32:0x013c, B:34:0x013f, B:39:0x014f, B:42:0x0171, B:37:0x0149, B:38:0x014e, B:40:0x0167, B:41:0x0170, B:12:0x005f, B:13:0x006b, B:14:0x0074, B:43:0x0177, B:45:0x017e, B:46:0x0187), top: B:64:0x0014, outer: #1 }] */
    /* JADX WARN: Code duplicated, block: B:22:0x00b2 A[Catch: all -> 0x018f, all -> 0x019e, TryCatch #2 {all -> 0x018f, blocks: (B:7:0x0014, B:10:0x003b, B:11:0x0054, B:15:0x0075, B:18:0x0086, B:19:0x008f, B:20:0x00a8, B:21:0x00b1, B:22:0x00b2, B:24:0x00cd, B:26:0x00de, B:27:0x00ed, B:28:0x00ff, B:29:0x0122, B:31:0x0133, B:32:0x013c, B:34:0x013f, B:39:0x014f, B:42:0x0171, B:37:0x0149, B:38:0x014e, B:40:0x0167, B:41:0x0170, B:12:0x005f, B:13:0x006b, B:14:0x0074, B:43:0x0177, B:45:0x017e, B:46:0x0187), top: B:64:0x0014, outer: #1 }] */
    /* JADX WARN: Code duplicated, block: B:24:0x00cd A[Catch: all -> 0x018f, all -> 0x019e, TryCatch #2 {all -> 0x018f, blocks: (B:7:0x0014, B:10:0x003b, B:11:0x0054, B:15:0x0075, B:18:0x0086, B:19:0x008f, B:20:0x00a8, B:21:0x00b1, B:22:0x00b2, B:24:0x00cd, B:26:0x00de, B:27:0x00ed, B:28:0x00ff, B:29:0x0122, B:31:0x0133, B:32:0x013c, B:34:0x013f, B:39:0x014f, B:42:0x0171, B:37:0x0149, B:38:0x014e, B:40:0x0167, B:41:0x0170, B:12:0x005f, B:13:0x006b, B:14:0x0074, B:43:0x0177, B:45:0x017e, B:46:0x0187), top: B:64:0x0014, outer: #1 }] */
    /* JADX WARN: Code duplicated, block: B:26:0x00de A[Catch: all -> 0x018f, all -> 0x019e, TryCatch #2 {all -> 0x018f, blocks: (B:7:0x0014, B:10:0x003b, B:11:0x0054, B:15:0x0075, B:18:0x0086, B:19:0x008f, B:20:0x00a8, B:21:0x00b1, B:22:0x00b2, B:24:0x00cd, B:26:0x00de, B:27:0x00ed, B:28:0x00ff, B:29:0x0122, B:31:0x0133, B:32:0x013c, B:34:0x013f, B:39:0x014f, B:42:0x0171, B:37:0x0149, B:38:0x014e, B:40:0x0167, B:41:0x0170, B:12:0x005f, B:13:0x006b, B:14:0x0074, B:43:0x0177, B:45:0x017e, B:46:0x0187), top: B:64:0x0014, outer: #1 }] */
    /* JADX WARN: Code duplicated, block: B:27:0x00ed A[Catch: all -> 0x018f, all -> 0x019e, TryCatch #2 {all -> 0x018f, blocks: (B:7:0x0014, B:10:0x003b, B:11:0x0054, B:15:0x0075, B:18:0x0086, B:19:0x008f, B:20:0x00a8, B:21:0x00b1, B:22:0x00b2, B:24:0x00cd, B:26:0x00de, B:27:0x00ed, B:28:0x00ff, B:29:0x0122, B:31:0x0133, B:32:0x013c, B:34:0x013f, B:39:0x014f, B:42:0x0171, B:37:0x0149, B:38:0x014e, B:40:0x0167, B:41:0x0170, B:12:0x005f, B:13:0x006b, B:14:0x0074, B:43:0x0177, B:45:0x017e, B:46:0x0187), top: B:64:0x0014, outer: #1 }] */
    /* JADX WARN: Code duplicated, block: B:33:0x013d  */
    /* JADX WARN: Code duplicated, block: B:66:0x00a8 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:67:0x0167 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:68:0x0133 A[SYNTHETIC] */
    /*  JADX ERROR: JadxOverflowException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxOverflowException: Regions count limit reached at block B:22:0x00b2
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
        	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
        	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
        */
    @Override // java.security.KeyStoreSpi
    public void engineLoad(java.io.InputStream r8, char[] r9) throws java.security.NoSuchAlgorithmException, java.io.IOException, java.security.cert.CertificateException {
        /*
            Method dump skipped, instruction units count: 423
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.bouncycastle.jcajce.provider.keystore.util.JKSKeyStoreSpi.engineLoad(java.io.InputStream, char[]):void");
    }

    private CertificateFactory createCertFactory(String str) throws CertificateException {
        if (this.helper == null) {
            return CertificateFactory.getInstance(str);
        }
        try {
            return this.helper.createCertificateFactory(str);
        } catch (NoSuchProviderException e) {
            throw new CertificateException(e.toString());
        }
    }

    private void addPassword(Digest digest, char[] cArr) throws IOException {
        for (int i = 0; i < cArr.length; i++) {
            digest.update((byte) (cArr[i] >> '\b'));
            digest.update((byte) cArr[i]);
        }
        digest.update(Strings.toByteArray("Mighty Aphrodite"), 0, 16);
    }

    private ErasableByteStream validateStream(InputStream inputStream, char[] cArr) throws IOException {
        Digest digest = DigestFactory.getDigest("SHA-1");
        byte[] all = Streams.readAll(inputStream);
        if (cArr == null) {
            return new ErasableByteStream(all, 0, all.length - digest.getDigestSize());
        }
        addPassword(digest, cArr);
        digest.update(all, 0, all.length - digest.getDigestSize());
        byte[] bArr = new byte[digest.getDigestSize()];
        digest.doFinal(bArr, 0);
        byte[] bArr2 = new byte[bArr.length];
        System.arraycopy(all, all.length - bArr.length, bArr2, 0, bArr.length);
        if (Arrays.constantTimeAreEqual(bArr, bArr2)) {
            return new ErasableByteStream(all, 0, all.length - bArr.length);
        }
        Arrays.fill(all, (byte) 0);
        throw new IOException("password incorrect or store tampered with");
    }
}
