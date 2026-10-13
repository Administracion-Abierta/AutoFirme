package com.sun.jna.platform.mac;

import com.sun.jna.Library;
import com.sun.jna.Native;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/DiskArbitration.class */
public interface DiskArbitration extends Library {
    public static final DiskArbitration INSTANCE = (DiskArbitration) Native.load("DiskArbitration", DiskArbitration.class);

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/DiskArbitration$DADiskRef.class */
    public static class DADiskRef extends CoreFoundation.CFTypeRef {
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/DiskArbitration$DASessionRef.class */
    public static class DASessionRef extends CoreFoundation.CFTypeRef {
    }

    DASessionRef DASessionCreate(CoreFoundation.CFAllocatorRef cFAllocatorRef);

    DADiskRef DADiskCreateFromBSDName(CoreFoundation.CFAllocatorRef cFAllocatorRef, DASessionRef dASessionRef, String str);

    DADiskRef DADiskCreateFromIOMedia(CoreFoundation.CFAllocatorRef cFAllocatorRef, DASessionRef dASessionRef, IOKit.IOObject iOObject);

    CoreFoundation.CFDictionaryRef DADiskCopyDescription(DADiskRef dADiskRef);

    String DADiskGetBSDName(DADiskRef dADiskRef);
}
