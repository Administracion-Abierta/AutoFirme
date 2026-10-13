package org.bouncycastle.pqc.crypto.ntruprime;

import com.sun.jna.platform.linux.Fcntl;
import com.sun.jna.platform.win32.WinError;
import com.twelvemonkeys.imageio.metadata.tiff.TIFF;
import org.bouncycastle.pqc.crypto.KEMParameters;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/ntruprime/SNTRUPrimeParameters.class */
public class SNTRUPrimeParameters implements KEMParameters {
    public static final SNTRUPrimeParameters sntrup653 = new SNTRUPrimeParameters("sntrup653", WinError.ERROR_SYSTEM_HIVE_TOO_LARGE, 4621, 288, WinError.ERROR_EA_ACCESS_DENIED, 865, WinError.ERROR_EA_ACCESS_DENIED, 1518, 32);
    public static final SNTRUPrimeParameters sntrup761 = new SNTRUPrimeParameters("sntrup761", WinError.ERROR_VOLSNAP_HIBERNATE_READY, 4591, TIFF.TAG_X_POSITION, WinError.ERROR_NO_MORE_USER_HANDLES, 1007, WinError.ERROR_NO_MORE_USER_HANDLES, WinError.RPC_S_INVALID_NAF_ID, 32);
    public static final SNTRUPrimeParameters sntrup857 = new SNTRUPrimeParameters("sntrup857", 857, 5167, TIFF.TAG_TILE_WIDTH, WinError.ERROR_LAST_ADMIN, WinError.ERROR_SINGLE_INSTANCE_APP, WinError.ERROR_LAST_ADMIN, 1999, 32);
    public static final SNTRUPrimeParameters sntrup953 = new SNTRUPrimeParameters("sntrup953", 953, 6343, 396, 1505, WinError.ERROR_NO_SUCH_USER, 1505, 2254, 32);
    public static final SNTRUPrimeParameters sntrup1013 = new SNTRUPrimeParameters("sntrup1013", 1013, 7177, Fcntl.S_IRWXU, WinError.ERROR_INSTALL_LANGUAGE_UNSUPPORTED, WinError.ERROR_WINDOW_NOT_COMBOBOX, WinError.ERROR_INSTALL_LANGUAGE_UNSUPPORTED, 2417, 32);
    public static final SNTRUPrimeParameters sntrup1277 = new SNTRUPrimeParameters("sntrup1277", WinError.ERROR_ACCESS_DISABLED_WEBBLADE, 7879, 492, 2067, WinError.ERROR_RESOURCE_LANG_NOT_FOUND, 2067, 3059, 32);
    private final String name;
    private final int p;
    private final int q;
    private final int w;
    private final int rqPolynomialBytes;
    private final int roundedPolynomialBytes;
    private final int publicKeyBytes;
    private final int privateKeyBytes;
    private final int sharedKeyBytes;

    private SNTRUPrimeParameters(String str, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        this.name = str;
        this.p = i;
        this.q = i2;
        this.w = i3;
        this.rqPolynomialBytes = i4;
        this.roundedPolynomialBytes = i5;
        this.publicKeyBytes = i6;
        this.privateKeyBytes = i7;
        this.sharedKeyBytes = i8;
    }

    public String getName() {
        return this.name;
    }

    public int getP() {
        return this.p;
    }

    public int getQ() {
        return this.q;
    }

    public int getW() {
        return this.w;
    }

    public int getPublicKeyBytes() {
        return this.publicKeyBytes;
    }

    public int getPrivateKeyBytes() {
        return this.privateKeyBytes;
    }

    public int getRqPolynomialBytes() {
        return this.rqPolynomialBytes;
    }

    public int getRoundedPolynomialBytes() {
        return this.roundedPolynomialBytes;
    }

    public int getSessionKeySize() {
        return this.sharedKeyBytes * 8;
    }
}
