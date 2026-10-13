package es.gob.afirma.keystores.mozilla.bintutil;

import java.util.Hashtable;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/keystores/mozilla/bintutil/PeMachineType.class */
public enum PeMachineType {
    UNKNOWN("Desconocida"),
    MATSUSHITA_AM33("Matsushita AM33"),
    X64("x64"),
    ARM_LITTLE_ENDIAN("ARM little endian"),
    EFI_BYTECODE("EFI bytecode"),
    INTEL_386("Intel 386 o posteriores y compatibles"),
    INTEL_ITANIUM("Familia de procesadores Intel Itanium"),
    MITSUBISHI_M32R_LITTLE_ENDIAN("Mitsubishi M32R little endian"),
    MIPS16("MIPS 16"),
    MIPS_FPU("MIPS con FPU");

    private final String description;
    private static final Hashtable<String, PeMachineType> MACHINE_TYPES = new Hashtable<>(10);

    static {
        MACHINE_TYPES.put("00-00", UNKNOWN);
        MACHINE_TYPES.put("01-D3", MATSUSHITA_AM33);
        MACHINE_TYPES.put("86-64", X64);
        MACHINE_TYPES.put("01-C0", ARM_LITTLE_ENDIAN);
        MACHINE_TYPES.put("0E-BC", EFI_BYTECODE);
        MACHINE_TYPES.put("01-4C", INTEL_386);
        MACHINE_TYPES.put("02-00", INTEL_ITANIUM);
        MACHINE_TYPES.put("90-41", MITSUBISHI_M32R_LITTLE_ENDIAN);
        MACHINE_TYPES.put("02-66", MIPS16);
        MACHINE_TYPES.put("03-66", MIPS_FPU);
    }

    PeMachineType(String str) {
        this.description = str;
    }

    @Override // java.lang.Enum
    public String toString() {
        return this.description;
    }

    static PeMachineType getPeMachineType(String str) {
        return MACHINE_TYPES.get(str);
    }
}
