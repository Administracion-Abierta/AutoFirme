package com.sun.jna.platform;

import com.sun.jna.Platform;
import com.sun.jna.platform.unix.X11;
import com.sun.jna.platform.win32.User32;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/KeyboardUtils.class */
public class KeyboardUtils {
    static final NativeKeyboardUtils INSTANCE;

    /* JADX INFO: Thrown type has an unknown type hierarchy: java.awt.HeadlessException */
    static {
        if (GraphicsEnvironment.isHeadless()) {
            throw new HeadlessException("KeyboardUtils requires a keyboard");
        }
        if (Platform.isWindows()) {
            INSTANCE = new W32KeyboardUtils();
        } else {
            if (Platform.isMac()) {
                INSTANCE = new MacKeyboardUtils();
                throw new UnsupportedOperationException("No support (yet) for " + System.getProperty("os.name"));
            }
            INSTANCE = new X11KeyboardUtils();
        }
    }

    public static boolean isPressed(int keycode, int location) {
        return INSTANCE.isPressed(keycode, location);
    }

    public static boolean isPressed(int keycode) {
        return INSTANCE.isPressed(keycode);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/KeyboardUtils$NativeKeyboardUtils.class */
    private static abstract class NativeKeyboardUtils {
        public abstract boolean isPressed(int i, int i2);

        private NativeKeyboardUtils() {
        }

        public boolean isPressed(int keycode) {
            return isPressed(keycode, 0);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/KeyboardUtils$W32KeyboardUtils.class */
    private static class W32KeyboardUtils extends NativeKeyboardUtils {
        private W32KeyboardUtils() {
            super();
        }

        private int toNative(int code, int loc) {
            if ((code >= 65 && code <= 90) || (code >= 48 && code <= 57)) {
                return code;
            }
            if (code == 16) {
                if ((loc & 3) != 0) {
                    return 161;
                }
                if ((loc & 2) != 0) {
                    return 160;
                }
                return 16;
            }
            if (code == 17) {
                if ((loc & 3) != 0) {
                    return 163;
                }
                if ((loc & 2) != 0) {
                    return 162;
                }
                return 17;
            }
            if (code == 18) {
                if ((loc & 3) != 0) {
                    return 165;
                }
                if ((loc & 2) != 0) {
                    return 164;
                }
                return 18;
            }
            return 0;
        }

        @Override // com.sun.jna.platform.KeyboardUtils.NativeKeyboardUtils
        public boolean isPressed(int keycode, int location) {
            User32 lib = User32.INSTANCE;
            return (lib.GetAsyncKeyState(toNative(keycode, location)) & Short.MIN_VALUE) != 0;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/KeyboardUtils$MacKeyboardUtils.class */
    private static class MacKeyboardUtils extends NativeKeyboardUtils {
        private MacKeyboardUtils() {
            super();
        }

        @Override // com.sun.jna.platform.KeyboardUtils.NativeKeyboardUtils
        public boolean isPressed(int keycode, int location) {
            return false;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/KeyboardUtils$X11KeyboardUtils.class */
    private static class X11KeyboardUtils extends NativeKeyboardUtils {
        private X11KeyboardUtils() {
            super();
        }

        private int toKeySym(int code, int location) {
            if (code >= 65 && code <= 90) {
                return 97 + (code - 65);
            }
            if (code >= 48 && code <= 57) {
                return 48 + (code - 48);
            }
            if (code == 16) {
                if ((location & 3) != 0) {
                    return 65506;
                }
                return 65505;
            }
            if (code == 17) {
                if ((location & 3) != 0) {
                    return 65508;
                }
                return 65507;
            }
            if (code == 18) {
                if ((location & 3) != 0) {
                    return 65514;
                }
                return 65513;
            }
            if (code == 157) {
                if ((location & 3) != 0) {
                    return 65512;
                }
                return 65511;
            }
            return 0;
        }

        @Override // com.sun.jna.platform.KeyboardUtils.NativeKeyboardUtils
        public boolean isPressed(int keycode, int location) {
            X11 lib = X11.INSTANCE;
            X11.Display dpy = lib.XOpenDisplay(null);
            if (dpy == null) {
                throw new Error("Can't open X Display");
            }
            try {
                byte[] keys = new byte[32];
                lib.XQueryKeymap(dpy, keys);
                int keysym = toKeySym(keycode, location);
                for (int code = 5; code < 256; code++) {
                    int idx = code / 8;
                    int shift = code % 8;
                    if ((keys[idx] & (1 << shift)) != 0) {
                        int sym = lib.XKeycodeToKeysym(dpy, (byte) code, 0).intValue();
                        if (sym == keysym) {
                            return true;
                        }
                    }
                }
                return false;
            } finally {
                lib.XCloseDisplay(dpy);
            }
        }
    }
}
