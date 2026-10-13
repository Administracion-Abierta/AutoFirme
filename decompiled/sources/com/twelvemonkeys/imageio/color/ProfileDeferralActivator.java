package com.twelvemonkeys.imageio.color;

import com.twelvemonkeys.imageio.util.IIOUtil;
import java.io.File;
import java.util.Locale;
import javax.imageio.spi.ImageInputStreamSpi;
import javax.imageio.spi.ServiceRegistry;
import javax.imageio.stream.ImageInputStream;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/color/ProfileDeferralActivator.class */
final class ProfileDeferralActivator {
    ProfileDeferralActivator() {
    }

    static {
        activateProfilesInternal();
    }

    private static void activateProfilesInternal() {
        try {
            Class.forName("java.awt.image.ColorConvertOp");
        } catch (Throwable th) {
            System.err.println("ProfileDeferralMgr.activateProfiles() failed. ICC Color Profiles may not work properly, see stack trace below.");
            System.err.println("For more information, see https://bugs.openjdk.java.net/browse/JDK-6986863");
            System.err.println("Please upgrade to Java 17 or later where this bug is fixed, or ask your JRE provider to backport the fix.");
            System.err.println();
            System.err.println("If you can't update to Java 17, a possible workaround is to add");
            System.err.println("\tClass.forName(\"java.awt.image.ColorConvertOp\");");
            System.err.println("*early* in your application startup code, to force profile activation before profiles are accessed.");
            System.err.println();
            th.printStackTrace();
        }
    }

    static void activateProfiles() {
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/color/ProfileDeferralActivator$Spi.class */
    public static final class Spi extends ImageInputStreamSpi {
        public void onRegistration(ServiceRegistry serviceRegistry, Class<?> cls) {
            ProfileDeferralActivator.activateProfiles();
            IIOUtil.deregisterProvider(serviceRegistry, this, cls);
        }

        public String getDescription(Locale locale) {
            return getClass().getName();
        }

        public ImageInputStream createInputStreamInstance(Object obj, boolean z, File file) {
            throw new UnsupportedOperationException();
        }
    }
}
