package es.gob.afirma.standalone.plugins.manager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.CodeSigner;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/standalone/plugins/manager/JarVerifier.class */
public class JarVerifier {
    private static final long THRESHOLD_FILE_SIZE = 100000000;

    private JarVerifier() {
    }

    public static List<X509Certificate[]> verify(File file) throws JarNoSignedException, IOException, SecurityException {
        if (file.length() >= THRESHOLD_FILE_SIZE) {
            throw new IOException("El archivo tiene un tamano superior al permitido.");
        }
        HashSet hashSet = new HashSet();
        ArrayList arrayList = new ArrayList();
        JarFile jarFile = new JarFile(file, true, 1);
        Throwable th = null;
        try {
            if (jarFile.getManifest() == null) {
                throw new SecurityException("No se ha encontrado el manifest en el archivo");
            }
            Enumeration<JarEntry> enumerationEntries = jarFile.entries();
            while (enumerationEntries.hasMoreElements()) {
                JarEntry jarEntryNextElement = enumerationEntries.nextElement();
                if (!jarEntryNextElement.isDirectory() && !jarEntryNextElement.getName().contains("META-INF")) {
                    byte[] bArr = new byte[8096];
                    InputStream inputStream = jarFile.getInputStream(jarEntryNextElement);
                    Throwable th2 = null;
                    do {
                        try {
                            try {
                            } catch (Throwable th3) {
                                th2 = th3;
                                throw th3;
                            }
                        } catch (Throwable th4) {
                            if (inputStream != null) {
                                if (th2 != null) {
                                    try {
                                        inputStream.close();
                                    } catch (Throwable th5) {
                                        th2.addSuppressed(th5);
                                    }
                                } else {
                                    inputStream.close();
                                }
                            }
                            throw th4;
                        }
                    } while (inputStream.read(bArr, 0, bArr.length) != -1);
                    if (inputStream != null) {
                        if (0 != 0) {
                            try {
                                inputStream.close();
                            } catch (Throwable th6) {
                                th2.addSuppressed(th6);
                            }
                        } else {
                            inputStream.close();
                        }
                    }
                    CodeSigner[] codeSigners = jarEntryNextElement.getCodeSigners();
                    if (codeSigners == null) {
                        throw new JarNoSignedException("Se han encontrado entradas sin firmar: " + jarEntryNextElement.getName());
                    }
                    if (hashSet.isEmpty()) {
                        for (CodeSigner codeSigner : codeSigners) {
                            hashSet.add(codeSigner);
                            arrayList.add(codeSigner.getSignerCertPath().getCertificates().toArray(new X509Certificate[0]));
                        }
                    } else {
                        if (codeSigners.length != hashSet.size()) {
                            throw new JarNoSignedException("Se han encontrado entradas no firmadas por todos los firmantes: " + jarEntryNextElement.getName());
                        }
                        for (CodeSigner codeSigner2 : codeSigners) {
                            if (!hashSet.contains(codeSigner2)) {
                                throw new JarNoSignedException("Se han encontrado entradas firmadas por firmantes distintos al resto: " + jarEntryNextElement.getName());
                            }
                        }
                    }
                }
            }
            if (jarFile != null) {
                if (0 != 0) {
                    try {
                        jarFile.close();
                    } catch (Throwable th7) {
                        th.addSuppressed(th7);
                    }
                } else {
                    jarFile.close();
                }
            }
            return arrayList;
        } catch (Throwable th8) {
            if (jarFile != null) {
                if (0 != 0) {
                    try {
                        jarFile.close();
                    } catch (Throwable th9) {
                        th.addSuppressed(th9);
                    }
                } else {
                    jarFile.close();
                }
            }
            throw th8;
        }
    }
}
