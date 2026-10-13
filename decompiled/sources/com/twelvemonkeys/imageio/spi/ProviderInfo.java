package com.twelvemonkeys.imageio.spi;

import com.twelvemonkeys.lang.Validate;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/spi/ProviderInfo.class */
public class ProviderInfo {
    private final String title;
    private final String vendorName;
    private final String version;

    public ProviderInfo(Package r5) {
        Validate.notNull(r5, "package");
        String implementationTitle = r5.getImplementationTitle();
        this.title = implementationTitle != null ? implementationTitle : r5.getName();
        String implementationVendor = r5.getImplementationVendor();
        this.vendorName = implementationVendor != null ? implementationVendor : fakeVendor(r5);
        String implementationVersion = r5.getImplementationVersion();
        this.version = implementationVersion != null ? implementationVersion : fakeVersion(r5);
    }

    private static String fakeVendor(Package r3) {
        String name = r3.getName();
        return name.startsWith("com.twelvemonkeys") ? "TwelveMonkeys" : name;
    }

    private String fakeVersion(Package r4) {
        return r4.getName().startsWith("com.twelvemonkeys") ? "DEV" : "Unspecified";
    }

    final String getImplementationTitle() {
        return this.title;
    }

    public final String getVendorName() {
        return this.vendorName;
    }

    public final String getVersion() {
        return this.version;
    }

    public String toString() {
        return this.title + ", " + this.version + " by " + this.vendorName;
    }
}
