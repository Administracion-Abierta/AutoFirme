package org.apache.pdfbox.io;

import java.io.File;

/* JADX INFO: loaded from: autofirma.jar:org/apache/pdfbox/io/MemoryUsageSetting.class */
public final class MemoryUsageSetting {
    private final boolean useMainMemory;
    private final boolean useTempFile;
    private final long maxMainMemoryBytes;
    private final long maxStorageBytes;
    private File tempDir;

    private MemoryUsageSetting(boolean useMainMemory, boolean useTempFile, long maxMainMemoryBytes, long maxStorageBytes) {
        boolean locUseMainMemory = !useTempFile || useMainMemory;
        long locMaxMainMemoryBytes = useMainMemory ? maxMainMemoryBytes : -1L;
        long locMaxStorageBytes = maxStorageBytes > 0 ? maxStorageBytes : -1L;
        locMaxMainMemoryBytes = locMaxMainMemoryBytes < -1 ? -1L : locMaxMainMemoryBytes;
        if (locUseMainMemory && locMaxMainMemoryBytes == 0) {
            if (useTempFile) {
                locUseMainMemory = false;
            } else {
                locMaxMainMemoryBytes = locMaxStorageBytes;
            }
        }
        if (locUseMainMemory && locMaxStorageBytes > -1 && (locMaxMainMemoryBytes == -1 || locMaxMainMemoryBytes > locMaxStorageBytes)) {
            locMaxStorageBytes = locMaxMainMemoryBytes;
        }
        this.useMainMemory = locUseMainMemory;
        this.useTempFile = useTempFile;
        this.maxMainMemoryBytes = locMaxMainMemoryBytes;
        this.maxStorageBytes = locMaxStorageBytes;
    }

    public static MemoryUsageSetting setupMainMemoryOnly() {
        return setupMainMemoryOnly(-1L);
    }

    public static MemoryUsageSetting setupMainMemoryOnly(long maxMainMemoryBytes) {
        return new MemoryUsageSetting(true, false, maxMainMemoryBytes, maxMainMemoryBytes);
    }

    public static MemoryUsageSetting setupTempFileOnly() {
        return setupTempFileOnly(-1L);
    }

    public static MemoryUsageSetting setupTempFileOnly(long maxStorageBytes) {
        return new MemoryUsageSetting(false, true, 0L, maxStorageBytes);
    }

    public static MemoryUsageSetting setupMixed(long maxMainMemoryBytes) {
        return setupMixed(maxMainMemoryBytes, -1L);
    }

    public static MemoryUsageSetting setupMixed(long maxMainMemoryBytes, long maxStorageBytes) {
        return new MemoryUsageSetting(true, true, maxMainMemoryBytes, maxStorageBytes);
    }

    public MemoryUsageSetting getPartitionedCopy(int parallelUseCount) {
        long newMaxMainMemoryBytes = this.maxMainMemoryBytes <= 0 ? this.maxMainMemoryBytes : this.maxMainMemoryBytes / ((long) parallelUseCount);
        long newMaxStorageBytes = this.maxStorageBytes <= 0 ? this.maxStorageBytes : this.maxStorageBytes / ((long) parallelUseCount);
        MemoryUsageSetting copy = new MemoryUsageSetting(this.useMainMemory, this.useTempFile, newMaxMainMemoryBytes, newMaxStorageBytes);
        copy.tempDir = this.tempDir;
        return copy;
    }

    public MemoryUsageSetting setTempDir(File tempDir) {
        this.tempDir = tempDir;
        return this;
    }

    public boolean useMainMemory() {
        return this.useMainMemory;
    }

    public boolean useTempFile() {
        return this.useTempFile;
    }

    public boolean isMainMemoryRestricted() {
        return this.maxMainMemoryBytes >= 0;
    }

    public boolean isStorageRestricted() {
        return this.maxStorageBytes > 0;
    }

    public long getMaxMainMemoryBytes() {
        return this.maxMainMemoryBytes;
    }

    public long getMaxStorageBytes() {
        return this.maxStorageBytes;
    }

    public File getTempDir() {
        return this.tempDir;
    }

    public String toString() {
        if (!this.useMainMemory) {
            return isStorageRestricted() ? "Scratch file only with max. of " + this.maxStorageBytes + " bytes" : "Scratch file only with no size restriction";
        }
        if (this.useTempFile) {
            return "Mixed mode with max. of " + this.maxMainMemoryBytes + " main memory bytes" + (isStorageRestricted() ? " and max. of " + this.maxStorageBytes + " storage bytes" : " and unrestricted scratch file size");
        }
        return isMainMemoryRestricted() ? "Main memory only with max. of " + this.maxMainMemoryBytes + " bytes" : "Main memory only with no size restriction";
    }
}
