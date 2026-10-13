package es.gob.afirma.keystores.mozilla.bintutil;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:es/gob/afirma/keystores/mozilla/bintutil/ElfMachineType.class */
public enum ElfMachineType {
    UNSPECIFIED("No especificado"),
    BELLMAC_32("AT&T WE 32100"),
    SPARC("SPARC"),
    X86("x86"),
    M68K("Motorola 68000"),
    M88K("Motorola 88000"),
    IMCU("Intel MCU"),
    I860("Intel 80860"),
    MIPS("MIPS"),
    IBM_370("IBM System/370"),
    MIPS_R3K_LE("MIPS RS3000 Little-endian"),
    RESERVED("Reservado para uso futuro"),
    HP_PARISC("Hewlett-Packard PA-RISC"),
    I960("Intel 80960"),
    POWERPC("PowerPC"),
    POWERPC64("PowerPC 64"),
    IBM_390("IBM System/390"),
    ARM("ARM"),
    SUPERH("Hitachi SuperH"),
    IA64("IA-64"),
    AMD64("AMD64"),
    TMS320C6000("TMS320C6000 Family"),
    ARM64("ARM 64-bits (ARMv8/Aarch64)"),
    RISCV("RISC-V"),
    WDC_65C816("WDC 65C816"),
    UNKNOWN("Desconocido");

    private final String description;

    ElfMachineType(String str) {
        this.description = str;
    }

    @Override // java.lang.Enum
    public String toString() {
        return this.description;
    }
}
