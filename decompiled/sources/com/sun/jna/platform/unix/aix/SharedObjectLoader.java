package com.sun.jna.platform.unix.aix;

import com.sun.jna.Library;
import com.sun.jna.Native;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/aix/SharedObjectLoader.class */
final class SharedObjectLoader {
    private SharedObjectLoader() {
    }

    static Perfstat getPerfstatInstance() {
        Map<String, Object> options = getOptions();
        try {
            return (Perfstat) Native.load("/usr/lib/libperfstat.a(shr_64.o)", Perfstat.class, options);
        } catch (UnsatisfiedLinkError e) {
            return (Perfstat) Native.load("/usr/lib/libperfstat.a(shr.o)", Perfstat.class, options);
        }
    }

    private static Map<String, Object> getOptions() {
        Map<String, Object> options = new HashMap<>();
        options.put(Library.OPTION_OPEN_FLAGS, Integer.valueOf(262144 | 65536 | 4));
        return Collections.unmodifiableMap(options);
    }
}
