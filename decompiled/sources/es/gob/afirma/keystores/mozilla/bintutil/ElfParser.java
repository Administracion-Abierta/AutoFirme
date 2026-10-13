package es.gob.afirma.keystores.mozilla.bintutil;

import es.gob.afirma.core.misc.AOUtil;
import es.gob.afirma.core.misc.Platform;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;

/* JADX INFO: loaded from: autofirma.jar:es/gob/afirma/keystores/mozilla/bintutil/ElfParser.class */
public final class ElfParser {
    private ElfParser() {
    }

    public static ElfMachineType getMachineType(File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("El fichero no puede ser nulo");
        }
        if (!file.isFile()) {
            throw new IllegalArgumentException("El fichero no existe: " + file.getAbsolutePath());
        }
        FileInputStream fileInputStream = new FileInputStream(file);
        Throwable th = null;
        try {
            ElfMachineType machineType = getMachineType(AOUtil.getDataFromInputStream(fileInputStream));
            if (fileInputStream != null) {
                if (0 != 0) {
                    try {
                        fileInputStream.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                } else {
                    fileInputStream.close();
                }
            }
            return machineType;
        } catch (Throwable th3) {
            if (fileInputStream != null) {
                if (0 != 0) {
                    try {
                        fileInputStream.close();
                    } catch (Throwable th4) {
                        th.addSuppressed(th4);
                    }
                } else {
                    fileInputStream.close();
                }
            }
            throw th3;
        }
    }

    public static ElfMachineType getMachineType(String str) throws IOException {
        if (str == null) {
            throw new IllegalArgumentException("El nombre de fichero no puede ser nulo");
        }
        return getMachineType(new File(str));
    }

    public static ElfMachineType getMachineType(byte[] bArr) {
        if (bArr == null || bArr.length < 19) {
            throw new IllegalArgumentException("Fichero ELF nulo o invalido");
        }
        switch (bArr[18]) {
            case -116:
                return ElfMachineType.TMS320C6000;
            case -73:
                return ElfMachineType.ARM64;
            case -13:
                return ElfMachineType.RISCV;
            case 0:
                return ElfMachineType.UNSPECIFIED;
            case 1:
                return ElfMachineType.BELLMAC_32;
            case 2:
                return ElfMachineType.SPARC;
            case 3:
                return ElfMachineType.X86;
            case 4:
                return ElfMachineType.M68K;
            case 5:
                return ElfMachineType.M88K;
            case 6:
                return ElfMachineType.IMCU;
            case 7:
                return ElfMachineType.I860;
            case 8:
                return ElfMachineType.MIPS;
            case 9:
                return ElfMachineType.IBM_370;
            case 10:
                return ElfMachineType.MIPS_R3K_LE;
            case 11:
            case 12:
            case 13:
            case 15:
                return ElfMachineType.RESERVED;
            case 14:
                return ElfMachineType.HP_PARISC;
            case 19:
                return ElfMachineType.I960;
            case 20:
                return ElfMachineType.POWERPC;
            case 21:
                return ElfMachineType.POWERPC64;
            case 22:
                return ElfMachineType.IBM_390;
            case 40:
                return ElfMachineType.ARM;
            case 42:
                return ElfMachineType.SUPERH;
            case 50:
                return ElfMachineType.IA64;
            case 62:
                return ElfMachineType.AMD64;
            default:
                return ElfMachineType.UNKNOWN;
        }
    }

    public static boolean archMatches(File file) {
        if (file == null) {
            return false;
        }
        try {
            ElfMachineType machineType = getMachineType(file);
            return ("64".equals(Platform.getJavaArch()) && Platform.MACHINE.AMD64.equals(Platform.getMachineType()) && ElfMachineType.AMD64.equals(machineType)) || ("32".equals(Platform.getJavaArch()) && ((Platform.MACHINE.X86.equals(Platform.getMachineType()) || Platform.MACHINE.AMD64.equals(Platform.getMachineType())) && ElfMachineType.X86.equals(machineType)));
        } catch (IOException e) {
            Logger.getLogger("es.gob.afirma").warning("No se puede comprobar la arquitectura del fichero '" + file.getAbsolutePath() + "': " + e);
            return false;
        }
    }
}
