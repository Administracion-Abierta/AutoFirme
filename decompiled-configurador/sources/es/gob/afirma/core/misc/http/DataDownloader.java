package es.gob.afirma.core.misc.http;

import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.Base64;
import es.gob.afirma.core.misc.LoggerUtil;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import org.slf4j.Marker;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/core/misc/http/DataDownloader.class */
public final class DataDownloader {
    private static final int GUNZIP_BUFFER_SIZE = 1024;
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");

    private DataDownloader() {
    }

    /* JADX WARN: Not initialized variable reg: 10, insn: 0x0209: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r10 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) A[TRY_LEAVE], block:B:98:0x0209 */
    /* JADX WARN: Not initialized variable reg: 11, insn: 0x020e: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r11 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:100:0x020e */
    public static byte[] downloadData(String str, boolean z, boolean z2) throws IOException {
        InputStream inputStream;
        Throwable th;
        if (str == null) {
            throw new IllegalArgumentException("La fuente de datos no puede ser nula");
        }
        if (z && Base64.isBase64(str.getBytes())) {
            LOGGER.info("Se ha indicado que los datos de entrada estan comrpimidos con GZIP");
            return gunzipBytes(Base64.decode(str.replace("_", "/").replace("-", Marker.ANY_NON_NULL_MARKER)));
        }
        String strTrim = str.trim();
        if (strTrim.startsWith("http://") || strTrim.startsWith("https://")) {
            SSLConfig sSLConfig = null;
            if (z2) {
                sSLConfig = new SSLConfig();
                sSLConfig.setHostnameVerifier(SslSecurityManager.DUMMY_HOSTNAME_VERIFIER);
                try {
                    SSLContext sSLContext = SSLContext.getInstance("SSL");
                    sSLContext.init(null, SslSecurityManager.DUMMY_TRUST_MANAGER, new SecureRandom());
                    sSLConfig.setSSLSocketFactory(sSLContext.getSocketFactory());
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "No se ha podido configurar la conexion para que ignore los problemas de confianza SSL", (Throwable) e);
                }
            }
            SSLErrorProcessor sSLErrorProcessor = new SSLErrorProcessor();
            try {
                return UrlHttpManagerFactory.getInstalledManager().readUrl(strTrim, UrlHttpMethod.GET, sSLErrorProcessor, sSLConfig);
            } catch (SSLHandshakeException e2) {
                if (sSLErrorProcessor.isCancelled()) {
                    LOGGER.info("El usuario no permite la importacion del certificado SSL de confianza para el acceso a " + LoggerUtil.getTrimStr(strTrim));
                }
                throw e2;
            }
        }
        if (strTrim.startsWith("ftp://")) {
            InputStream inputStreamOpenStream = new URL(strTrim).openStream();
            Throwable th2 = null;
            try {
                try {
                    byte[] dataFromInputStream = AOUtil.getDataFromInputStream(inputStreamOpenStream);
                    if (inputStreamOpenStream != null) {
                        if (0 != 0) {
                            try {
                                inputStreamOpenStream.close();
                            } catch (Throwable th3) {
                                th2.addSuppressed(th3);
                            }
                        } else {
                            inputStreamOpenStream.close();
                        }
                    }
                    return dataFromInputStream;
                } catch (Throwable th4) {
                    th2 = th4;
                    throw th4;
                }
            } catch (Throwable th5) {
                if (inputStreamOpenStream != null) {
                    if (th2 != null) {
                        try {
                            inputStreamOpenStream.close();
                        } catch (Throwable th6) {
                            th2.addSuppressed(th6);
                        }
                    } else {
                        inputStreamOpenStream.close();
                    }
                }
                throw th5;
            }
        }
        if (!strTrim.startsWith("file:/")) {
            if (Base64.isBase64(strTrim.getBytes())) {
                LOGGER.info("El contenido a obtener es Base64");
                try {
                    return Base64.decode(strTrim.replace("_", "/").replace("-", Marker.ANY_NON_NULL_MARKER));
                } catch (Exception e3) {
                    LOGGER.warning("Los datos introducidos no se pueden tratar como Base64: " + e3);
                }
            }
            return strTrim.getBytes();
        }
        try {
            try {
                InputStream inputStreamLoadFile = AOUtil.loadFile(new URI(strTrim));
                Throwable th7 = null;
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStreamLoadFile);
                Throwable th8 = null;
                try {
                    try {
                        byte[] dataFromInputStream2 = AOUtil.getDataFromInputStream(bufferedInputStream);
                        if (bufferedInputStream != null) {
                            if (0 != 0) {
                                try {
                                    bufferedInputStream.close();
                                } catch (Throwable th9) {
                                    th8.addSuppressed(th9);
                                }
                            } else {
                                bufferedInputStream.close();
                            }
                        }
                        if (inputStreamLoadFile != null) {
                            if (0 != 0) {
                                try {
                                    inputStreamLoadFile.close();
                                } catch (Throwable th10) {
                                    th7.addSuppressed(th10);
                                }
                            } else {
                                inputStreamLoadFile.close();
                            }
                        }
                        return dataFromInputStream2;
                    } catch (Throwable th11) {
                        th8 = th11;
                        throw th11;
                    }
                } catch (Throwable th12) {
                    if (bufferedInputStream != null) {
                        if (th8 != null) {
                            try {
                                bufferedInputStream.close();
                            } catch (Throwable th13) {
                                th8.addSuppressed(th13);
                            }
                        } else {
                            bufferedInputStream.close();
                        }
                    }
                    throw th12;
                }
            } catch (Throwable th14) {
                if (inputStream != null) {
                    if (th != null) {
                        try {
                            inputStream.close();
                        } catch (Throwable th15) {
                            th.addSuppressed(th15);
                        }
                    } else {
                        inputStream.close();
                    }
                }
                throw th14;
            }
        } catch (URISyntaxException e4) {
            throw new IOException("Error leyendo el fichero (" + strTrim + "): " + e4, e4);
        }
    }

    public static byte[] downloadData(String str, boolean z) throws IOException {
        return downloadData(str, z, false);
    }

    public static byte[] downloadData(String str) throws IOException {
        return downloadData(str, false, false);
    }

    /* JADX WARN: Code duplicated, block: B:41:0x00be  */
    /* JADX WARN: Code duplicated, block: B:46:0x00d7 A[Catch: Throwable -> 0x00df, all -> 0x00e8, Throwable -> 0x010d, all -> 0x0112, TryCatch #4 {Throwable -> 0x010d, blocks: (B:3:0x000b, B:4:0x0016, B:5:0x0023, B:6:0x002a, B:8:0x0038, B:9:0x0044, B:13:0x0054, B:16:0x0068, B:15:0x005e, B:21:0x0076, B:24:0x0089, B:23:0x007f, B:37:0x00b6, B:43:0x00c3, B:46:0x00d7, B:45:0x00cd, B:48:0x00de, B:51:0x00e7, B:57:0x00f3, B:60:0x0106, B:59:0x00fc, B:62:0x010c), top: B:83:0x000b, outer: #3 }] */
    /* JADX WARN: Code duplicated, block: B:77:0x00c3 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Not initialized variable reg: 8, insn: 0x00ea: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r8 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) A[TRY_LEAVE], block:B:53:0x00ea */
    /* JADX WARN: Not initialized variable reg: 9, insn: 0x00ee: MOVE (r0 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]) = (r9 I:??[int, float, boolean, short, byte, char, OBJECT, ARRAY]), block:B:55:0x00ee */
    private static byte[] gunzipBytes(byte[] bArr) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream;
        Throwable th;
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bArr);
        Throwable th2 = null;
        try {
            try {
                try {
                    ByteArrayOutputStream byteArrayOutputStream2 = new ByteArrayOutputStream();
                    Throwable th3 = null;
                    GZIPInputStream gZIPInputStream = new GZIPInputStream(byteArrayInputStream);
                    Throwable th4 = null;
                    try {
                        try {
                            byte[] bArr2 = new byte[1024];
                            while (true) {
                                int i = gZIPInputStream.read(bArr2);
                                if (i == -1) {
                                    break;
                                }
                                byteArrayOutputStream2.write(bArr2, 0, i);
                                if (gZIPInputStream != null) {
                                    if (th4 != null) {
                                        try {
                                            gZIPInputStream.close();
                                        } catch (Throwable th5) {
                                            th4.addSuppressed(th5);
                                        }
                                    } else {
                                        gZIPInputStream.close();
                                    }
                                }
                                throw th;
                            }
                            byte[] byteArray = byteArrayOutputStream2.toByteArray();
                            if (gZIPInputStream != null) {
                                if (0 != 0) {
                                    try {
                                        gZIPInputStream.close();
                                    } catch (Throwable th6) {
                                        th4.addSuppressed(th6);
                                    }
                                } else {
                                    gZIPInputStream.close();
                                }
                            }
                            if (byteArrayOutputStream2 != null) {
                                if (0 != 0) {
                                    try {
                                        byteArrayOutputStream2.close();
                                    } catch (Throwable th7) {
                                        th3.addSuppressed(th7);
                                    }
                                } else {
                                    byteArrayOutputStream2.close();
                                }
                            }
                            if (byteArrayInputStream != null) {
                                if (0 != 0) {
                                    try {
                                        byteArrayInputStream.close();
                                    } catch (Throwable th8) {
                                        th2.addSuppressed(th8);
                                    }
                                } else {
                                    byteArrayInputStream.close();
                                }
                            }
                            return byteArray;
                        } catch (Throwable th9) {
                            th4 = th9;
                            throw th9;
                        }
                    } catch (Throwable th10) {
                        if (gZIPInputStream != null) {
                            if (th4 != null) {
                                gZIPInputStream.close();
                            } else {
                                gZIPInputStream.close();
                            }
                        }
                        throw th10;
                    }
                } catch (Throwable th11) {
                    throw th11;
                }
            } catch (Throwable th12) {
                if (byteArrayInputStream != null) {
                    if (0 != 0) {
                        try {
                            byteArrayInputStream.close();
                        } catch (Throwable th13) {
                            th2.addSuppressed(th13);
                        }
                    } else {
                        byteArrayInputStream.close();
                    }
                }
                throw th12;
            }
        } catch (Throwable th14) {
            if (byteArrayOutputStream != null) {
                if (th != null) {
                    try {
                        byteArrayOutputStream.close();
                    } catch (Throwable th15) {
                        th.addSuppressed(th15);
                    }
                } else {
                    byteArrayOutputStream.close();
                }
            }
            throw th14;
        }
    }
}
