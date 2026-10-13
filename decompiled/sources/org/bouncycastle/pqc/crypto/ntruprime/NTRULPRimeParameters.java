package org.bouncycastle.pqc.crypto.ntruprime;

import com.github.markusbernhardt.proxy.search.wpad.dhcp.DHCPOptions;
import com.sun.jna.platform.win32.LMErr;
import com.sun.jna.platform.win32.WinError;
import com.twelvemonkeys.imageio.metadata.psd.PSD;
import com.twelvemonkeys.imageio.metadata.tiff.TIFF;
import org.bouncycastle.pqc.crypto.KEMParameters;

/* JADX INFO: loaded from: autofirma.jar:org/bouncycastle/pqc/crypto/ntruprime/NTRULPRimeParameters.class */
public class NTRULPRimeParameters implements KEMParameters {
    public static final NTRULPRimeParameters ntrulpr653 = new NTRULPRimeParameters("ntrulpr653", WinError.ERROR_SYSTEM_HIVE_TOO_LARGE, 4621, DHCPOptions.OPTION_PROXY_AUTODISCOVERY, TIFF.TAG_FREE_BYTE_COUNTS, 2175, 113, 2031, 290, 865, 897, WinError.ERROR_FLOPPY_BAD_REGISTERS, 32);
    public static final NTRULPRimeParameters ntrulpr761 = new NTRULPRimeParameters("ntrulpr761", WinError.ERROR_VOLSNAP_HIBERNATE_READY, 4591, 250, TIFF.TAG_GROUP3OPTIONS, LMErr.NERR_JobNoRoom, 114, 2007, TIFF.TAG_Y_POSITION, 1007, PSD.RES_ICC_PROFILE, WinError.ERROR_SERVICE_NOTIFY_CLIENT_LAGGING, 32);
    public static final NTRULPRimeParameters ntrulpr857 = new NTRULPRimeParameters("ntrulpr857", 857, 5167, 281, 329, LMErr.NERR_BadRecipient, 101, 2265, TIFF.TAG_TILE_OFFSETS, WinError.ERROR_SINGLE_INSTANCE_APP, 1184, WinError.ERROR_SYMLINK_CLASS_DISABLED, 32);
    public static final NTRULPRimeParameters ntrulpr953 = new NTRULPRimeParameters("ntrulpr953", 953, 6343, 345, 404, 2997, 82, 2798, 400, WinError.ERROR_NO_SUCH_USER, WinError.ERROR_BAD_TOKEN_TYPE, WinError.ERROR_INSTALL_SERVICE_SAFEBOOT, 32);
    public static final NTRULPRimeParameters ntrulpr1013 = new NTRULPRimeParameters("ntrulpr1013", 1013, 7177, 392, 450, 3367, 73, 3143, 449, WinError.ERROR_WINDOW_NOT_COMBOBOX, WinError.ERROR_COMMITMENT_LIMIT, WinError.RPC_X_SS_CHAR_TRANS_OPEN_FAIL, 32);
    public static final NTRULPRimeParameters ntrulpr1277 = new NTRULPRimeParameters("ntrulpr1277", WinError.ERROR_ACCESS_DISABLED_WEBBLADE, 7879, 429, 502, 3724, 66, 3469, 496, WinError.ERROR_RESOURCE_LANG_NOT_FOUND, 1847, LMErr.NERR_UserLogon, 32);
    private final String name;
    private final int p;
    private final int q;
    private final int w;
    private final int delta;
    private final int tau0;
    private final int tau1;
    private final int tau2;
    private final int tau3;
    private final int roundedPolynomialBytes;
    private final int publicKeyBytes;
    private final int privateKeyBytes;
    private final int sharedKeyBytes;

    private NTRULPRimeParameters(String str, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12) {
        this.name = str;
        this.p = i;
        this.q = i2;
        this.w = i3;
        this.delta = i4;
        this.tau0 = i5;
        this.tau1 = i6;
        this.tau2 = i7;
        this.tau3 = i8;
        this.roundedPolynomialBytes = i9;
        this.publicKeyBytes = i10;
        this.privateKeyBytes = i11;
        this.sharedKeyBytes = i12;
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

    public int getDelta() {
        return this.delta;
    }

    public int getTau0() {
        return this.tau0;
    }

    public int getTau1() {
        return this.tau1;
    }

    public int getTau2() {
        return this.tau2;
    }

    public int getTau3() {
        return this.tau3;
    }

    public int getPublicKeyBytes() {
        return this.publicKeyBytes;
    }

    public int getPrivateKeyBytes() {
        return this.privateKeyBytes;
    }

    public int getRoundedPolynomialBytes() {
        return this.roundedPolynomialBytes;
    }

    public int getSessionKeySize() {
        return this.sharedKeyBytes * 8;
    }
}
