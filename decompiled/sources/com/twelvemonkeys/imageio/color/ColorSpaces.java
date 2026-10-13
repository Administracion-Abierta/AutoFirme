package com.twelvemonkeys.imageio.color;

import com.twelvemonkeys.lang.Validate;
import com.twelvemonkeys.util.LRUHashMap;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Map;
import org.ini4j.Registry;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/color/ColorSpaces.class */
public final class ColorSpaces {
    public static final int CS_ADOBE_RGB_1998 = 5000;
    public static final int CS_GENERIC_CMYK = 5001;
    static final boolean DEBUG = "true".equalsIgnoreCase(System.getProperty("com.twelvemonkeys.imageio.color.debug"));
    private static WeakReference<ICC_Profile> adobeRGB1998 = new WeakReference<>(null);
    private static WeakReference<ICC_Profile> genericCMYK = new WeakReference<>(null);
    private static final Map<Key, ICC_ColorSpace> cache = new LRUHashMap(16);

    static {
        ProfileDeferralActivator.activateProfiles();
    }

    private ColorSpaces() {
    }

    public static ICC_ColorSpace createColorSpace(ICC_Profile iCC_Profile) {
        Validate.notNull(iCC_Profile, "profile");
        ColorProfiles.fixProfile(iCC_Profile);
        byte[] profileHeaderWithProfileId = ColorProfiles.getProfileHeaderWithProfileId(iCC_Profile);
        ICC_ColorSpace internalCS = getInternalCS(iCC_Profile.getColorSpaceType(), profileHeaderWithProfileId);
        if (internalCS != null) {
            return internalCS;
        }
        return getCachedOrCreateCS(iCC_Profile, profileHeaderWithProfileId);
    }

    static ICC_ColorSpace getInternalCS(int i, byte[] bArr) {
        if (i == 5 && Arrays.equals(bArr, ColorProfiles.sRGB.header)) {
            return ColorSpace.getInstance(1000);
        }
        if (i == 6 && Arrays.equals(bArr, ColorProfiles.GRAY.header)) {
            return ColorSpace.getInstance(1003);
        }
        if (i == 13 && Arrays.equals(bArr, ColorProfiles.PYCC.header)) {
            return ColorSpace.getInstance(1002);
        }
        if (i == 5 && Arrays.equals(bArr, ColorProfiles.LINEAR_RGB.header)) {
            return ColorSpace.getInstance(1004);
        }
        if (i == 0 && Arrays.equals(bArr, ColorProfiles.CIEXYZ.header)) {
            return ColorSpace.getInstance(1001);
        }
        return null;
    }

    private static ICC_ColorSpace getCachedOrCreateCS(ICC_Profile iCC_Profile, byte[] bArr) {
        ICC_ColorSpace iCC_ColorSpace;
        Key key = new Key(bArr);
        synchronized (cache) {
            ICC_ColorSpace cachedCS = getCachedCS(key);
            if (cachedCS == null) {
                cachedCS = new ICC_ColorSpace(iCC_Profile);
                validateColorSpace(cachedCS);
                cache.put(key, cachedCS);
                if (ColorProfiles.validationAltersProfileHeader()) {
                    cache.put(new Key(ColorProfiles.getProfileHeaderWithProfileId(cachedCS.getProfile())), cachedCS);
                }
            }
            iCC_ColorSpace = cachedCS;
        }
        return iCC_ColorSpace;
    }

    private static ICC_ColorSpace getCachedCS(Key key) {
        ICC_ColorSpace iCC_ColorSpace;
        synchronized (cache) {
            iCC_ColorSpace = cache.get(key);
        }
        return iCC_ColorSpace;
    }

    static ICC_ColorSpace getCachedCS(byte[] bArr) {
        return getCachedCS(new Key(bArr));
    }

    static void validateColorSpace(ICC_ColorSpace iCC_ColorSpace) {
        iCC_ColorSpace.fromRGB(new float[]{0.999f, 0.5f, 0.001f});
        iCC_ColorSpace.getProfile().getData();
    }

    @Deprecated
    public static boolean isCS_sRGB(ICC_Profile iCC_Profile) {
        return ColorProfiles.isCS_sRGB(iCC_Profile);
    }

    @Deprecated
    public static boolean isCS_GRAY(ICC_Profile iCC_Profile) {
        return ColorProfiles.isCS_GRAY(iCC_Profile);
    }

    @Deprecated
    public static ICC_Profile validateProfile(ICC_Profile iCC_Profile) {
        return ColorProfiles.validateProfile(iCC_Profile);
    }

    public static ColorSpace getColorSpace(int i) {
        ICC_Profile profileFromPath;
        switch (i) {
            case CS_ADOBE_RGB_1998 /* 5000 */:
                synchronized (ColorSpaces.class) {
                    profileFromPath = adobeRGB1998.get();
                    if (profileFromPath == null) {
                        profileFromPath = ColorProfiles.readProfileFromPath(ColorProfiles.Profiles.getPath("ADOBE_RGB_1998"));
                        if (profileFromPath == null) {
                            profileFromPath = ColorProfiles.readProfileFromClasspathResource("/profiles/ClayRGB1998.icc");
                            if (profileFromPath == null) {
                                throw new IllegalStateException("Could not read AdobeRGB1998 profile");
                            }
                        }
                        if (profileFromPath.getColorSpaceType() != 5) {
                            throw new IllegalStateException("Configured AdobeRGB1998 profile is not TYPE_RGB");
                        }
                        adobeRGB1998 = new WeakReference<>(profileFromPath);
                    }
                }
                return createColorSpace(profileFromPath);
            case 5001:
                synchronized (ColorSpaces.class) {
                    ICC_Profile profileFromPath2 = genericCMYK.get();
                    if (profileFromPath2 == null) {
                        profileFromPath2 = ColorProfiles.readProfileFromPath(ColorProfiles.Profiles.getPath("GENERIC_CMYK"));
                        if (profileFromPath2 == null) {
                            if (DEBUG) {
                                System.out.println("Using fallback profile");
                            }
                            return CMYKColorSpace.getInstance();
                        }
                        if (profileFromPath2.getColorSpaceType() != 9) {
                            throw new IllegalStateException("Configured Generic CMYK profile is not TYPE_CMYK");
                        }
                        genericCMYK = new WeakReference<>(profileFromPath2);
                    }
                    return createColorSpace(profileFromPath2);
                }
            default:
                return ColorSpace.getInstance(i);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/color/ColorSpaces$Key.class */
    private static final class Key {
        private final byte[] data;

        Key(byte[] bArr) {
            this.data = bArr;
        }

        public boolean equals(Object obj) {
            return (obj instanceof Key) && Arrays.equals(this.data, ((Key) obj).data);
        }

        public int hashCode() {
            return Arrays.hashCode(this.data);
        }

        public String toString() {
            return getClass().getSimpleName() + Registry.Key.DEFAULT_NAME + Integer.toHexString(hashCode());
        }
    }
}
