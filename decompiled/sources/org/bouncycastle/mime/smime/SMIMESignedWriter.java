package org.bouncycastle.mime.smime;

import es.gob.afirma.standalone.CommandLineParameters;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSAlgorithm;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSSignedDataStreamGenerator;
import org.bouncycastle.cms.SignerInfoGenerator;
import org.bouncycastle.mime.Headers;
import org.bouncycastle.mime.MimeWriter;
import org.bouncycastle.mime.encoding.Base64OutputStream;
import org.bouncycastle.util.Store;
import org.bouncycastle.util.Strings;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/mime/smime/SMIMESignedWriter.class */
public class SMIMESignedWriter extends MimeWriter {
    public static final Map RFC3851_MICALGS;
    public static final Map RFC5751_MICALGS;
    public static final Map STANDARD_MICALGS;
    private final CMSSignedDataStreamGenerator sigGen;
    private final String boundary;
    private final OutputStream mimeOut;
    private final String contentTransferEncoding;

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/mime/smime/SMIMESignedWriter$Builder.class */
    public static class Builder {
        private static final String[] detHeaders = {"Content-Type"};
        private static final String[] detValues = {"multipart/signed; protocol=\"application/pkcs7-signature\""};
        private static final String[] encHeaders = {"Content-Type", "Content-Disposition", "Content-Transfer-Encoding", "Content-Description"};
        private static final String[] encValues = {"application/pkcs7-mime; name=\"smime.p7m\"; smime-type=enveloped-data", "attachment; filename=\"smime.p7m\"", "base64", "S/MIME Signed Message"};
        private final CMSSignedDataStreamGenerator sigGen;
        private final Map<String, String> extraHeaders;
        private final boolean encapsulated;
        private final Map micAlgs;
        String contentTransferEncoding;

        public Builder() {
            this(false);
        }

        public Builder(boolean z) {
            this.sigGen = new CMSSignedDataStreamGenerator();
            this.extraHeaders = new LinkedHashMap();
            this.micAlgs = SMIMESignedWriter.STANDARD_MICALGS;
            this.contentTransferEncoding = "base64";
            this.encapsulated = z;
        }

        public Builder withHeader(String str, String str2) {
            this.extraHeaders.put(str, str2);
            return this;
        }

        public Builder addCertificate(X509CertificateHolder x509CertificateHolder) throws CMSException {
            this.sigGen.addCertificate(x509CertificateHolder);
            return this;
        }

        public Builder addCertificates(Store store) throws CMSException {
            this.sigGen.addCertificates(store);
            return this;
        }

        public Builder addSignerInfoGenerator(SignerInfoGenerator signerInfoGenerator) {
            this.sigGen.addSignerInfoGenerator(signerInfoGenerator);
            return this;
        }

        public SMIMESignedWriter build(OutputStream outputStream) {
            String strGenerateBoundary;
            LinkedHashMap linkedHashMap = new LinkedHashMap();
            if (this.encapsulated) {
                strGenerateBoundary = null;
                for (int i = 0; i != encHeaders.length; i++) {
                    linkedHashMap.put(encHeaders[i], encValues[i]);
                }
            } else {
                strGenerateBoundary = generateBoundary();
                StringBuffer stringBuffer = new StringBuffer(detValues[0]);
                addHashHeader(stringBuffer, this.sigGen.getDigestAlgorithms());
                addBoundary(stringBuffer, strGenerateBoundary);
                linkedHashMap.put(detHeaders[0], stringBuffer.toString());
                for (int i2 = 1; i2 < detHeaders.length; i2++) {
                    linkedHashMap.put(detHeaders[i2], detValues[i2]);
                }
            }
            for (Map.Entry<String, String> entry : this.extraHeaders.entrySet()) {
                linkedHashMap.put(entry.getKey(), entry.getValue());
            }
            return new SMIMESignedWriter(this, linkedHashMap, strGenerateBoundary, SMimeUtils.autoBuffer(outputStream));
        }

        private void addHashHeader(StringBuffer stringBuffer, List list) {
            int i = 0;
            Iterator it = list.iterator();
            TreeSet<String> treeSet = new TreeSet();
            while (it.hasNext()) {
                String str = (String) this.micAlgs.get(((AlgorithmIdentifier) it.next()).getAlgorithm());
                if (str == null) {
                    treeSet.add("unknown");
                } else {
                    treeSet.add(str);
                }
            }
            for (String str2 : treeSet) {
                if (i != 0) {
                    stringBuffer.append(',');
                } else if (treeSet.size() != 1) {
                    stringBuffer.append("; micalg=\"");
                } else {
                    stringBuffer.append("; micalg=");
                }
                stringBuffer.append(str2);
                i++;
            }
            if (i == 0 || treeSet.size() == 1) {
                return;
            }
            stringBuffer.append('\"');
        }

        private void addBoundary(StringBuffer stringBuffer, String str) {
            stringBuffer.append(";\r\n\tboundary=\"");
            stringBuffer.append(str);
            stringBuffer.append(OperatorName.SHOW_TEXT_LINE_AND_SPACE);
        }

        private String generateBoundary() {
            return "==" + new BigInteger(180, new SecureRandom()).setBit(179).toString(16) + "=";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/mime/smime/SMIMESignedWriter$ContentOutputStream.class */
    private class ContentOutputStream extends OutputStream {
        private final OutputStream main;
        private final OutputStream backing;
        private final ByteArrayOutputStream sigStream;
        private final OutputStream sigBase;

        ContentOutputStream(OutputStream outputStream, OutputStream outputStream2, ByteArrayOutputStream byteArrayOutputStream, OutputStream outputStream3) {
            this.main = outputStream;
            this.backing = outputStream2;
            this.sigStream = byteArrayOutputStream;
            this.sigBase = outputStream3;
        }

        @Override // java.io.OutputStream
        public void write(byte[] bArr) throws IOException {
            this.main.write(bArr);
        }

        @Override // java.io.OutputStream
        public void write(byte[] bArr, int i, int i2) throws IOException {
            this.main.write(bArr, i, i2);
        }

        @Override // java.io.OutputStream
        public void write(int i) throws IOException {
            this.main.write(i);
        }

        @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            if (SMIMESignedWriter.this.boundary != null) {
                this.main.close();
                this.backing.write(Strings.toByteArray("\r\n--"));
                this.backing.write(Strings.toByteArray(SMIMESignedWriter.this.boundary));
                this.backing.write(Strings.toByteArray("\r\n"));
                this.backing.write(Strings.toByteArray("Content-Type: application/pkcs7-signature; name=\"smime.p7s\"\r\n"));
                this.backing.write(Strings.toByteArray("Content-Transfer-Encoding: base64\r\n"));
                this.backing.write(Strings.toByteArray("Content-Disposition: attachment; filename=\"smime.p7s\"\r\n"));
                this.backing.write(Strings.toByteArray("\r\n"));
                if (this.sigBase != null) {
                    this.sigBase.close();
                }
                this.backing.write(this.sigStream.toByteArray());
                this.backing.write(Strings.toByteArray("\r\n--"));
                this.backing.write(Strings.toByteArray(SMIMESignedWriter.this.boundary));
                this.backing.write(Strings.toByteArray("--\r\n"));
            }
            if (this.backing != null) {
                this.backing.close();
            }
        }
    }

    private SMIMESignedWriter(Builder builder, Map<String, String> map, String str, OutputStream outputStream) {
        super(new Headers(mapToLines(map), builder.contentTransferEncoding));
        this.sigGen = builder.sigGen;
        this.contentTransferEncoding = builder.contentTransferEncoding;
        this.boundary = str;
        this.mimeOut = outputStream;
    }

    @Override // org.bouncycastle.mime.MimeWriter
    public OutputStream getContentStream() throws IOException {
        this.headers.dumpHeaders(this.mimeOut);
        this.mimeOut.write(Strings.toByteArray("\r\n"));
        if (this.boundary == null) {
            return null;
        }
        this.mimeOut.write(Strings.toByteArray("This is an S/MIME signed message\r\n"));
        this.mimeOut.write(Strings.toByteArray("\r\n--"));
        this.mimeOut.write(Strings.toByteArray(this.boundary));
        this.mimeOut.write(Strings.toByteArray("\r\n"));
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Base64OutputStream base64OutputStream = new Base64OutputStream(byteArrayOutputStream);
        return new ContentOutputStream(this.sigGen.open((OutputStream) base64OutputStream, false, SMimeUtils.createUnclosable(this.mimeOut)), this.mimeOut, byteArrayOutputStream, base64OutputStream);
    }

    static {
        HashMap map = new HashMap();
        map.put(CMSAlgorithm.MD5, "md5");
        map.put(CMSAlgorithm.SHA1, "sha-1");
        map.put(CMSAlgorithm.SHA224, "sha-224");
        map.put(CMSAlgorithm.SHA256, "sha-256");
        map.put(CMSAlgorithm.SHA384, "sha-384");
        map.put(CMSAlgorithm.SHA512, "sha-512");
        map.put(CMSAlgorithm.GOST3411, "gostr3411-94");
        map.put(CMSAlgorithm.GOST3411_2012_256, "gostr3411-2012-256");
        map.put(CMSAlgorithm.GOST3411_2012_512, "gostr3411-2012-512");
        RFC5751_MICALGS = Collections.unmodifiableMap(map);
        HashMap map2 = new HashMap();
        map2.put(CMSAlgorithm.MD5, "md5");
        map2.put(CMSAlgorithm.SHA1, CommandLineParameters.ALGO_SHA1);
        map2.put(CMSAlgorithm.SHA224, "sha224");
        map2.put(CMSAlgorithm.SHA256, CommandLineParameters.ALGO_SHA256);
        map2.put(CMSAlgorithm.SHA384, CommandLineParameters.ALGO_SHA384);
        map2.put(CMSAlgorithm.SHA512, CommandLineParameters.ALGO_SHA512);
        map2.put(CMSAlgorithm.GOST3411, "gostr3411-94");
        map2.put(CMSAlgorithm.GOST3411_2012_256, "gostr3411-2012-256");
        map2.put(CMSAlgorithm.GOST3411_2012_512, "gostr3411-2012-512");
        RFC3851_MICALGS = Collections.unmodifiableMap(map2);
        STANDARD_MICALGS = RFC5751_MICALGS;
    }
}
