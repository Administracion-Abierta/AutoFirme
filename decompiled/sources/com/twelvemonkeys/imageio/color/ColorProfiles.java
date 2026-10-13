package com.twelvemonkeys.imageio.color;

import com.twelvemonkeys.io.FileUtil;
import com.twelvemonkeys.lang.Platform;
import com.twelvemonkeys.lang.SystemUtil;
import com.twelvemonkeys.lang.Validate;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Properties;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;

/* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/color/ColorProfiles.class */
public final class ColorProfiles {
    private static final ICCProfileSanitizer profileCleaner = ICCProfileSanitizer.Factory.get();
    static final int ICC_PROFILE_MAGIC = 1633907568;
    static final int ICC_PROFILE_HEADER_SIZE = 128;

    static {
        ProfileDeferralActivator.activateProfiles();
    }

    private ColorProfiles() {
    }

    static byte[] getProfileHeaderWithProfileId(ICC_Profile iCC_Profile) {
        return getProfileHeaderWithProfileId(iCC_Profile.getData());
    }

    static byte[] getProfileHeaderWithProfileId(byte[] bArr) {
        byte[] bArrCopyOf = Arrays.copyOf(bArr, 128);
        Arrays.fill(bArrCopyOf, 4, 8, (byte) 0);
        Arrays.fill(bArrCopyOf, 40, 44, (byte) 0);
        Arrays.fill(bArrCopyOf, 64, 68, (byte) 0);
        Arrays.fill(bArrCopyOf, 80, 84, (byte) 0);
        Arrays.fill(bArrCopyOf, 84, 100, (byte) 0);
        byte[] bArrComputeMD5 = computeMD5(bArrCopyOf, bArr);
        System.arraycopy(bArrComputeMD5, 0, bArrCopyOf, 84, bArrComputeMD5.length);
        return bArrCopyOf;
    }

    private static byte[] computeMD5(byte[] bArr, byte[] bArr2) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(MessageDigestAlgorithms.MD5);
            messageDigest.update(bArr, 0, 128);
            messageDigest.update(bArr2, 128, bArr2.length - 128);
            return messageDigest.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Missing MD5 MessageDigest");
        }
    }

    public static boolean isCS_sRGB(ICC_Profile iCC_Profile) {
        Validate.notNull(iCC_Profile, "profile");
        return iCC_Profile.getColorSpaceType() == 5 && Arrays.equals(getProfileHeaderWithProfileId(iCC_Profile), sRGB.header);
    }

    public static boolean isCS_GRAY(ICC_Profile iCC_Profile) {
        Validate.notNull(iCC_Profile, "profile");
        return iCC_Profile.getColorSpaceType() == 6 && Arrays.equals(getProfileHeaderWithProfileId(iCC_Profile), GRAY.header);
    }

    static boolean isOffendingColorProfile(ICC_Profile iCC_Profile) {
        Validate.notNull(iCC_Profile, "profile");
        byte[] data = iCC_Profile.getData(1751474532);
        return (data[64] == 0 && data[65] == 0 && data[66] == 0 && data[67] <= 3) ? false : true;
    }

    public static ICC_Profile validateProfile(ICC_Profile iCC_Profile) {
        profileCleaner.fixProfile(iCC_Profile);
        ColorSpaces.validateColorSpace(new ICC_ColorSpace(iCC_Profile));
        return iCC_Profile;
    }

    public static ICC_Profile readProfileRaw(InputStream inputStream) throws IOException {
        Validate.notNull(inputStream, "input");
        return ICC_Profile.getInstance(inputStream);
    }

    public static ICC_Profile readProfile(InputStream inputStream) throws IOException {
        Validate.notNull(inputStream, "input");
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        byte[] bArr = new byte[128];
        try {
            dataInputStream.readFully(bArr);
            int iValidateHeaderAndGetSize = validateHeaderAndGetSize(bArr);
            byte[] bArrCopyOf = Arrays.copyOf(bArr, iValidateHeaderAndGetSize);
            dataInputStream.readFully(bArrCopyOf, bArr.length, iValidateHeaderAndGetSize - bArr.length);
            return createProfile(bArrCopyOf);
        } catch (EOFException e) {
            throw new IllegalArgumentException("Truncated ICC Profile data", e);
        }
    }

    public static ICC_Profile createProfileRaw(byte[] bArr) {
        return ICC_Profile.getInstance(limit(bArr, validateHeaderAndGetSize(bArr)));
    }

    public static ICC_Profile createProfile(byte[] bArr) {
        int iValidateAndGetSize = validateAndGetSize(bArr);
        byte[] profileHeaderWithProfileId = getProfileHeaderWithProfileId(bArr);
        ICC_Profile internalProfile = getInternalProfile(profileHeaderWithProfileId);
        if (internalProfile != null) {
            return internalProfile;
        }
        ICC_ColorSpace cachedCS = ColorSpaces.getCachedCS(profileHeaderWithProfileId);
        if (cachedCS != null) {
            return cachedCS.getProfile();
        }
        return ColorSpaces.createColorSpace(ICC_Profile.getInstance(limit(bArr, iValidateAndGetSize))).getProfile();
    }

    private static byte[] limit(byte[] bArr, int i) {
        return bArr.length == i ? bArr : Arrays.copyOf(bArr, i);
    }

    private static int validateAndGetSize(byte[] bArr) {
        int iValidateHeaderAndGetSize = validateHeaderAndGetSize(bArr);
        if (iValidateHeaderAndGetSize < 0 || iValidateHeaderAndGetSize > bArr.length) {
            throw new IllegalArgumentException("Truncated ICC profile data, length < " + iValidateHeaderAndGetSize + ": " + bArr.length);
        }
        return iValidateHeaderAndGetSize;
    }

    private static int validateHeaderAndGetSize(byte[] bArr) {
        Validate.notNull(bArr, "input");
        if (bArr.length < 128) {
            throw new IllegalArgumentException("Truncated ICC profile data, length < 128: " + bArr.length);
        }
        int iIntBigEndian = intBigEndian(bArr, 0);
        if (intBigEndian(bArr, 36) != ICC_PROFILE_MAGIC) {
            throw new IllegalArgumentException("Not an ICC profile, missing file signature");
        }
        return iIntBigEndian;
    }

    private static ICC_Profile getInternalProfile(byte[] bArr) {
        int csType = getCsType(bArr);
        if (csType == 5 && Arrays.equals(bArr, sRGB.header)) {
            return ICC_Profile.getInstance(1000);
        }
        if (csType == 6 && Arrays.equals(bArr, GRAY.header)) {
            return ICC_Profile.getInstance(1003);
        }
        if (csType == 13 && Arrays.equals(bArr, PYCC.header)) {
            return ICC_Profile.getInstance(1002);
        }
        if (csType == 5 && Arrays.equals(bArr, LINEAR_RGB.header)) {
            return ICC_Profile.getInstance(1004);
        }
        if (csType == 0 && Arrays.equals(bArr, CIEXYZ.header)) {
            return ICC_Profile.getInstance(1001);
        }
        return null;
    }

    private static int intBigEndian(byte[] bArr, int i) {
        return ((bArr[i] & 255) << 24) | ((bArr[i + 1] & 255) << 16) | ((bArr[i + 2] & 255) << 8) | (bArr[i + 3] & 255);
    }

    private static int getCsType(byte[] bArr) {
        int iIntBigEndian = intBigEndian(bArr, 16);
        switch (iIntBigEndian) {
            case 843271250:
                return 12;
            case 860048466:
                return 13;
            case 876825682:
                return 14;
            case 893602898:
                return 15;
            case 910380114:
                return 16;
            case 927157330:
                return 17;
            case 943934546:
                return 18;
            case 960711762:
                return 19;
            case 1094929490:
                return 20;
            case 1111706706:
                return 21;
            case 1128483922:
                return 22;
            case 1129142560:
                return 11;
            case 1129142603:
                return 9;
            case 1145261138:
                return 23;
            case 1162038354:
                return 24;
            case 1178815570:
                return 25;
            case 1196573017:
                return 6;
            case 1212961568:
                return 8;
            case 1213421088:
                return 7;
            case 1281450528:
                return 1;
            case 1282766368:
                return 2;
            case 1380401696:
                return 5;
            case 1482250784:
                return 0;
            case 1497588338:
                return 3;
            case 1501067552:
                return 4;
            default:
                throw new IllegalArgumentException("Invalid ICC color space signature: " + iIntBigEndian);
        }
    }

    static ICC_Profile readProfileFromClasspathResource(String str) {
        InputStream resourceAsStream = ColorSpaces.class.getResourceAsStream(str);
        if (resourceAsStream != null) {
            if (ColorSpaces.DEBUG) {
                System.out.println("Loading profile from classpath resource: " + str);
            }
            try {
                return ICC_Profile.getInstance(resourceAsStream);
            } catch (IOException e) {
                if (ColorSpaces.DEBUG) {
                    e.printStackTrace();
                }
                return null;
            } finally {
                FileUtil.close(resourceAsStream);
            }
        }
        return null;
    }

    static ICC_Profile readProfileFromPath(String str) {
        if (str != null) {
            if (ColorSpaces.DEBUG) {
                System.out.println("Loading profile from: " + str);
            }
            try {
                return ICC_Profile.getInstance(str);
            } catch (IOException | SecurityException e) {
                if (ColorSpaces.DEBUG) {
                    e.printStackTrace();
                    return null;
                }
                return null;
            }
        }
        return null;
    }

    static void fixProfile(ICC_Profile iCC_Profile) {
        profileCleaner.fixProfile(iCC_Profile);
    }

    static boolean validationAltersProfileHeader() {
        return profileCleaner.validationAltersProfileHeader();
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/color/ColorProfiles$sRGB.class */
    static class sRGB {
        static final byte[] header = ColorProfiles.getProfileHeaderWithProfileId(ICC_Profile.getInstance(1000));

        sRGB() {
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/color/ColorProfiles$CIEXYZ.class */
    static class CIEXYZ {
        static final byte[] header = ColorProfiles.getProfileHeaderWithProfileId(ICC_Profile.getInstance(1001));

        CIEXYZ() {
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/color/ColorProfiles$PYCC.class */
    static class PYCC {
        static final byte[] header = ColorProfiles.getProfileHeaderWithProfileId(ICC_Profile.getInstance(1002));

        PYCC() {
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/color/ColorProfiles$GRAY.class */
    static class GRAY {
        static final byte[] header = ColorProfiles.getProfileHeaderWithProfileId(ICC_Profile.getInstance(1003));

        GRAY() {
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/color/ColorProfiles$LINEAR_RGB.class */
    static class LINEAR_RGB {
        static final byte[] header = ColorProfiles.getProfileHeaderWithProfileId(ICC_Profile.getInstance(1004));

        LINEAR_RGB() {
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/twelvemonkeys/imageio/color/ColorProfiles$Profiles.class */
    static class Profiles {
        private static final Properties PROFILES = loadProfiles();

        Profiles() {
        }

        private static Properties loadProfiles() {
            Properties propertiesLoadProperties;
            try {
                propertiesLoadProperties = SystemUtil.loadProperties(ColorSpaces.class, "com/twelvemonkeys/imageio/color/icc_profiles_" + Platform.os().id());
            } catch (IOException | SecurityException e) {
                System.err.printf("Warning: Could not load system default ICC profile locations from %s, will use bundled fallback profiles.\n", e.getMessage());
                if (ColorSpaces.DEBUG) {
                    e.printStackTrace();
                }
                propertiesLoadProperties = null;
            }
            Properties properties = new Properties(propertiesLoadProperties);
            try {
                properties.putAll(SystemUtil.loadProperties(ColorSpaces.class, "com/twelvemonkeys/imageio/color/icc_profiles"));
            } catch (IOException | SecurityException e2) {
            }
            if (ColorSpaces.DEBUG) {
                System.out.println("User ICC profiles: " + properties);
                System.out.println("System ICC profiles : " + propertiesLoadProperties);
            }
            return properties;
        }

        static String getPath(String str) {
            return PROFILES.getProperty(str);
        }
    }
}
