package es.gob.afirma.core.signers.asic;

import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.LoggerUtil;
import es.gob.afirma.core.misc.MimeHelper;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/core/signers/asic/ASiCUtil.class */
public final class ASiCUtil {
    private static final String ASIC_S_MIMETYPE = "application/vnd.etsi.asic-s+zip";
    private static final String DEFAULT_DATAOBJECT_EXTENSION = "bin";
    public static final String ENTRY_NAME_BINARY_SIGNATURE = "META-INF/signature.p7s";
    public static final String ENTRY_NAME_XML_SIGNATURE = "META-INF/signatures.xml";
    private static final String ENTRY_NAME_MIMETYPE = "mimetype";
    private static final Logger LOGGER = Logger.getLogger("es.gob.afirma");
    private static final int THRESHOLD_FILE_SIZE = 1000000000;

    private ASiCUtil() {
    }

    public static String getASiCSDefaultDataFilename(byte[] bArr) {
        String extension;
        try {
            extension = new MimeHelper(bArr).getExtension();
        } catch (IOException e) {
            LOGGER.warning("No se pudieron cargar los datos para su analisis: " + e);
            extension = null;
        }
        return "dataobject." + ((extension == null || extension.isEmpty()) ? "bin" : extension);
    }

    public static byte[] createSContainer(byte[] bArr, byte[] bArr2, String str, String str2) throws IOException {
        String aSiCSDefaultDataFilename;
        if (bArr == null || bArr.length < 1) {
            throw new IllegalArgumentException("La firma no puede ser nula ni vacia");
        }
        if (bArr2 == null || bArr2.length < 1) {
            throw new IllegalArgumentException("Los datos no pueden ser nulos ni vacios");
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);
        Throwable th = null;
        try {
            try {
                zipOutputStream.setComment("mimetype=application/vnd.etsi.asic-s+zip");
                zipOutputStream.putNextEntry(new ZipEntry(ENTRY_NAME_MIMETYPE));
                zipOutputStream.write(ASIC_S_MIMETYPE.getBytes());
                if (str2 != null) {
                    aSiCSDefaultDataFilename = str2;
                } else {
                    aSiCSDefaultDataFilename = getASiCSDefaultDataFilename(bArr2);
                }
                zipOutputStream.putNextEntry(new ZipEntry(aSiCSDefaultDataFilename));
                zipOutputStream.write(bArr2);
                zipOutputStream.putNextEntry(new ZipEntry(str != null ? str : ENTRY_NAME_BINARY_SIGNATURE));
                zipOutputStream.write(bArr);
                if (zipOutputStream != null) {
                    if (0 != 0) {
                        try {
                            zipOutputStream.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        zipOutputStream.close();
                    }
                }
                return byteArrayOutputStream.toByteArray();
            } catch (Throwable th3) {
                th = th3;
                throw th3;
            }
        } catch (Throwable th4) {
            if (zipOutputStream != null) {
                if (th != null) {
                    try {
                        zipOutputStream.close();
                    } catch (Throwable th5) {
                        th.addSuppressed(th5);
                    }
                } else {
                    zipOutputStream.close();
                }
            }
            throw th4;
        }
    }

    public static byte[] getASiCSBinarySignature(byte[] bArr) throws IOException {
        return getASiCSSignature(bArr, ENTRY_NAME_BINARY_SIGNATURE);
    }

    public static byte[] getASiCSXMLSignature(byte[] bArr) throws IOException {
        return getASiCSSignature(bArr, ENTRY_NAME_XML_SIGNATURE);
    }

    /* JADX WARN: Code duplicated, block: B:45:0x00b1  */
    /* JADX WARN: Code duplicated, block: B:50:0x00c7  */
    /* JADX WARN: Code duplicated, block: B:55:0x00b5 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    private static byte[] getASiCSSignature(byte[] bArr, String str) throws IOException {
        ZipEntry nextEntry;
        if (bArr == null || bArr.length < 1) {
            throw new IllegalArgumentException("La firma ASiC proporcionada no puede ser nula ni vacia");
        }
        if (bArr.length >= THRESHOLD_FILE_SIZE) {
            throw new IOException("El archivo tiene un tamano superior al permitido.");
        }
        if (str == null) {
            throw new IllegalArgumentException("La firma entrada de firma del ASiC no puede ser nula");
        }
        ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(bArr));
        Throwable th = null;
        do {
            try {
                try {
                    nextEntry = zipInputStream.getNextEntry();
                    if (nextEntry == null) {
                        if (zipInputStream != null) {
                            if (0 != 0) {
                                try {
                                    zipInputStream.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            } else {
                                zipInputStream.close();
                            }
                        }
                        throw new IOException("Los datos proporcionados no son una firma ASiC-S conteniendo la entrada " + LoggerUtil.getTrimStr(str));
                    }
                } catch (Throwable th3) {
                    th = th3;
                    throw th3;
                }
            } catch (Throwable th4) {
                if (zipInputStream != null) {
                    if (th != null) {
                        try {
                            zipInputStream.close();
                        } catch (Throwable th5) {
                            th.addSuppressed(th5);
                        }
                    } else {
                        zipInputStream.close();
                    }
                }
                throw th4;
            }
            if (zipInputStream != null) {
                if (th != null) {
                    zipInputStream.close();
                } else {
                    zipInputStream.close();
                }
            }
            throw th4;
        } while (!str.equals(nextEntry.getName()));
        byte[] dataFromInputStream = AOUtil.getDataFromInputStream(zipInputStream);
        if (zipInputStream != null) {
            if (0 != 0) {
                try {
                    zipInputStream.close();
                } catch (Throwable th6) {
                    th.addSuppressed(th6);
                }
            } else {
                zipInputStream.close();
            }
        }
        return dataFromInputStream;
    }

    /* JADX WARN: Code duplicated, block: B:45:0x00b5  */
    /* JADX WARN: Code duplicated, block: B:50:0x00cb  */
    /* JADX WARN: Code duplicated, block: B:59:0x00b9 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    public static byte[] getASiCSData(byte[] bArr) throws IOException {
        if (bArr == null || bArr.length < 1) {
            throw new IllegalArgumentException("La firma ASiC proporcionada no puede ser nula ni vacia");
        }
        if (bArr.length >= THRESHOLD_FILE_SIZE) {
            throw new IOException("El archivo tiene un tamano superior al permitido.");
        }
        ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(bArr));
        Throwable th = null;
        while (true) {
            try {
                try {
                    ZipEntry nextEntry = zipInputStream.getNextEntry();
                    if (nextEntry != null) {
                        if (!ENTRY_NAME_BINARY_SIGNATURE.equals(nextEntry.getName()) && !ENTRY_NAME_XML_SIGNATURE.equals(nextEntry.getName()) && !ENTRY_NAME_MIMETYPE.equals(nextEntry.getName())) {
                            byte[] dataFromInputStream = AOUtil.getDataFromInputStream(zipInputStream);
                            if (zipInputStream != null) {
                                if (0 != 0) {
                                    try {
                                        zipInputStream.close();
                                    } catch (Throwable th2) {
                                        th.addSuppressed(th2);
                                    }
                                } else {
                                    zipInputStream.close();
                                }
                            }
                            return dataFromInputStream;
                        }
                    } else {
                        if (zipInputStream != null) {
                            if (0 != 0) {
                                try {
                                    zipInputStream.close();
                                } catch (Throwable th3) {
                                    th.addSuppressed(th3);
                                }
                            } else {
                                zipInputStream.close();
                            }
                        }
                        throw new IOException("Los datos proporcionados no son una firma ASiC-S");
                    }
                } catch (Throwable th4) {
                    th = th4;
                    throw th4;
                }
            } catch (Throwable th5) {
                if (zipInputStream != null) {
                    if (th != null) {
                        try {
                            zipInputStream.close();
                        } catch (Throwable th6) {
                            th.addSuppressed(th6);
                        }
                    } else {
                        zipInputStream.close();
                    }
                }
                throw th5;
            }
            if (zipInputStream != null) {
                if (th != null) {
                    zipInputStream.close();
                } else {
                    zipInputStream.close();
                }
            }
            throw th5;
        }
    }

    public static String getASiCSDataFilename(byte[] bArr) throws IOException {
        if (bArr == null || bArr.length < 1) {
            throw new IllegalArgumentException("La firma ASiC proporcionada no puede ser nula ni vacia");
        }
        if (bArr.length >= THRESHOLD_FILE_SIZE) {
            throw new IOException("El archivo tiene un tamano superior al permitido.");
        }
        ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(bArr));
        while (true) {
            ZipEntry nextEntry = zipInputStream.getNextEntry();
            if (nextEntry == null) {
                throw new IOException("Los datos proporcionados no son una firma ASiC-S");
            }
            if (nextEntry.getSize() >= 1000000000) {
                throw new IOException("El archivo tiene un tamano superior al permitido.");
            }
            String name = nextEntry.getName();
            if (!ENTRY_NAME_BINARY_SIGNATURE.equals(name) && !ENTRY_NAME_XML_SIGNATURE.equals(name) && !ENTRY_NAME_MIMETYPE.equals(name)) {
                return name;
            }
        }
    }
}
