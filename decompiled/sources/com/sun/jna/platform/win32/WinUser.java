package com.sun.jna.platform.win32;

import com.aowagie.text.html.Markup;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import com.sun.jna.Callback;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APITypeMapper;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinUser.class */
public interface WinUser extends WinDef {
    public static final int FLASHW_STOP = 0;
    public static final int FLASHW_CAPTION = 1;
    public static final int FLASHW_TRAY = 2;
    public static final int FLASHW_ALL = 3;
    public static final int FLASHW_TIMER = 4;
    public static final int FLASHW_TIMERNOFG = 12;
    public static final int IMAGE_BITMAP = 0;
    public static final int IMAGE_ICON = 1;
    public static final int IMAGE_CURSOR = 2;
    public static final int IMAGE_ENHMETAFILE = 3;
    public static final int LR_DEFAULTCOLOR = 0;
    public static final int LR_MONOCHROME = 1;
    public static final int LR_COLOR = 2;
    public static final int LR_COPYRETURNORG = 4;
    public static final int LR_COPYDELETEORG = 8;
    public static final int LR_LOADFROMFILE = 16;
    public static final int LR_LOADTRANSPARENT = 32;
    public static final int LR_DEFAULTSIZE = 64;
    public static final int LR_VGACOLOR = 128;
    public static final int LR_LOADMAP3DCOLORS = 4096;
    public static final int LR_CREATEDIBSECTION = 8192;
    public static final int LR_COPYFROMRESOURCE = 16384;
    public static final int LR_SHARED = 32768;
    public static final int GWL_EXSTYLE = -20;
    public static final int GWL_STYLE = -16;
    public static final int GWL_WNDPROC = -4;
    public static final int GWL_HINSTANCE = -6;
    public static final int GWL_ID = -12;
    public static final int GWL_USERDATA = -21;
    public static final int GWL_HWNDPARENT = -8;
    public static final int DWL_MSGRESULT = 0;
    public static final int WS_BORDER = 8388608;
    public static final int WS_CAPTION = 12582912;
    public static final int WS_CHILD = 1073741824;
    public static final int WS_CHILDWINDOW = 1073741824;
    public static final int WS_CLIPCHILDREN = 33554432;
    public static final int WS_CLIPSIBLINGS = 67108864;
    public static final int WS_DISABLED = 134217728;
    public static final int WS_DLGFRAME = 4194304;
    public static final int WS_GROUP = 131072;
    public static final int WS_HSCROLL = 1048576;
    public static final int WS_ICONIC = 536870912;
    public static final int WS_MAXIMIZE = 16777216;
    public static final int WS_MAXIMIZEBOX = 65536;
    public static final int WS_MINIMIZE = 536870912;
    public static final int WS_MINIMIZEBOX = 131072;
    public static final int WS_OVERLAPPED = 0;
    public static final int WS_POPUP = Integer.MIN_VALUE;
    public static final int WS_SYSMENU = 524288;
    public static final int WS_THICKFRAME = 262144;
    public static final int WS_POPUPWINDOW = -2138570752;
    public static final int WS_OVERLAPPEDWINDOW = 13565952;
    public static final int WS_SIZEBOX = 262144;
    public static final int WS_TABSTOP = 65536;
    public static final int WS_TILED = 0;
    public static final int WS_TILEDWINDOW = 13565952;
    public static final int WS_VISIBLE = 268435456;
    public static final int WS_VSCROLL = 2097152;
    public static final int WS_EX_COMPOSITED = 536870912;
    public static final int WS_EX_LAYERED = 524288;
    public static final int WS_EX_TRANSPARENT = 32;
    public static final int LWA_COLORKEY = 1;
    public static final int LWA_ALPHA = 2;
    public static final int ULW_COLORKEY = 1;
    public static final int ULW_ALPHA = 2;
    public static final int ULW_OPAQUE = 4;
    public static final int AC_SRC_OVER = 0;
    public static final int AC_SRC_ALPHA = 1;
    public static final int AC_SRC_NO_PREMULT_ALPHA = 1;
    public static final int AC_SRC_NO_ALPHA = 2;
    public static final int VK_SHIFT = 16;
    public static final int VK_LSHIFT = 160;
    public static final int VK_RSHIFT = 161;
    public static final int VK_CONTROL = 17;
    public static final int VK_LCONTROL = 162;
    public static final int VK_RCONTROL = 163;
    public static final int VK_MENU = 18;
    public static final int VK_LMENU = 164;
    public static final int VK_RMENU = 165;
    public static final int MOD_ALT = 1;
    public static final int MOD_CONTROL = 2;
    public static final int MOD_NOREPEAT = 16384;
    public static final int MOD_SHIFT = 4;
    public static final int MOD_WIN = 8;
    public static final int WH_KEYBOARD = 2;
    public static final int WH_CALLWNDPROC = 4;
    public static final int WH_MOUSE = 7;
    public static final int WH_KEYBOARD_LL = 13;
    public static final int WH_MOUSE_LL = 14;
    public static final int WM_PAINT = 15;
    public static final int WM_CLOSE = 16;
    public static final int WM_QUIT = 18;
    public static final int WM_SHOWWINDOW = 24;
    public static final int WM_DRAWITEM = 43;
    public static final int WM_KEYDOWN = 256;
    public static final int WM_CHAR = 258;
    public static final int WM_SYSCOMMAND = 274;
    public static final int WM_MDIMAXIMIZE = 549;
    public static final int WM_HOTKEY = 786;
    public static final int WM_USER = 1024;
    public static final int WM_COPYDATA = 74;
    public static final int WM_KEYUP = 257;
    public static final int WM_SYSKEYDOWN = 260;
    public static final int WM_SYSKEYUP = 261;
    public static final int WM_SESSION_CHANGE = 689;
    public static final int WM_CREATE = 1;
    public static final int WM_SIZE = 5;
    public static final int WM_DESTROY = 2;
    public static final int WM_DEVICECHANGE = 537;
    public static final int WM_GETICON = 127;
    public static final int ICON_BIG = 1;
    public static final int ICON_SMALL = 0;
    public static final int ICON_SMALL2 = 2;
    public static final int SM_CXSCREEN = 0;
    public static final int SM_CYSCREEN = 1;
    public static final int SM_CXVSCROLL = 2;
    public static final int SM_CYHSCROLL = 3;
    public static final int SM_CYCAPTION = 4;
    public static final int SM_CXBORDER = 5;
    public static final int SM_CYBORDER = 6;
    public static final int SM_CXDLGFRAME = 7;
    public static final int SM_CYDLGFRAME = 8;
    public static final int SM_CYVTHUMB = 9;
    public static final int SM_CXHTHUMB = 10;
    public static final int SM_CXICON = 11;
    public static final int SM_CYICON = 12;
    public static final int SM_CXCURSOR = 13;
    public static final int SM_CYCURSOR = 14;
    public static final int SM_CYMENU = 15;
    public static final int SM_CXFULLSCREEN = 16;
    public static final int SM_CYFULLSCREEN = 17;
    public static final int SM_CYKANJIWINDOW = 18;
    public static final int SM_MOUSEPRESENT = 19;
    public static final int SM_CYVSCROLL = 20;
    public static final int SM_CXHSCROLL = 21;
    public static final int SM_DEBUG = 22;
    public static final int SM_SWAPBUTTON = 23;
    public static final int SM_RESERVED1 = 24;
    public static final int SM_RESERVED2 = 25;
    public static final int SM_RESERVED3 = 26;
    public static final int SM_RESERVED4 = 27;
    public static final int SM_CXMIN = 28;
    public static final int SM_CYMIN = 29;
    public static final int SM_CXSIZE = 30;
    public static final int SM_CYSIZE = 31;
    public static final int SM_CXFRAME = 32;
    public static final int SM_CYFRAME = 33;
    public static final int SM_CXMINTRACK = 34;
    public static final int SM_CYMINTRACK = 35;
    public static final int SM_CXDOUBLECLK = 36;
    public static final int SM_CYDOUBLECLK = 37;
    public static final int SM_CXICONSPACING = 38;
    public static final int SM_CYICONSPACING = 39;
    public static final int SM_MENUDROPALIGNMENT = 40;
    public static final int SM_PENWINDOWS = 41;
    public static final int SM_DBCSENABLED = 42;
    public static final int SM_CMOUSEBUTTONS = 43;
    public static final int SM_CXFIXEDFRAME = 7;
    public static final int SM_CYFIXEDFRAME = 8;
    public static final int SM_CXSIZEFRAME = 32;
    public static final int SM_CYSIZEFRAME = 33;
    public static final int SM_SECURE = 44;
    public static final int SM_CXEDGE = 45;
    public static final int SM_CYEDGE = 46;
    public static final int SM_CXMINSPACING = 47;
    public static final int SM_CYMINSPACING = 48;
    public static final int SM_CXSMICON = 49;
    public static final int SM_CYSMICON = 50;
    public static final int SM_CYSMCAPTION = 51;
    public static final int SM_CXSMSIZE = 52;
    public static final int SM_CYSMSIZE = 53;
    public static final int SM_CXMENUSIZE = 54;
    public static final int SM_CYMENUSIZE = 55;
    public static final int SM_ARRANGE = 56;
    public static final int SM_CXMINIMIZED = 57;
    public static final int SM_CYMINIMIZED = 58;
    public static final int SM_CXMAXTRACK = 59;
    public static final int SM_CYMAXTRACK = 60;
    public static final int SM_CXMAXIMIZED = 61;
    public static final int SM_CYMAXIMIZED = 62;
    public static final int SM_NETWORK = 63;
    public static final int SM_CLEANBOOT = 67;
    public static final int SM_CXDRAG = 68;
    public static final int SM_CYDRAG = 69;
    public static final int SM_SHOWSOUNDS = 70;
    public static final int SM_CXMENUCHECK = 71;
    public static final int SM_CYMENUCHECK = 72;
    public static final int SM_SLOWMACHINE = 73;
    public static final int SM_MIDEASTENABLED = 74;
    public static final int SM_MOUSEWHEELPRESENT = 75;
    public static final int SM_XVIRTUALSCREEN = 76;
    public static final int SM_YVIRTUALSCREEN = 77;
    public static final int SM_CXVIRTUALSCREEN = 78;
    public static final int SM_CYVIRTUALSCREEN = 79;
    public static final int SM_CMONITORS = 80;
    public static final int SM_SAMEDISPLAYFORMAT = 81;
    public static final int SM_IMMENABLED = 82;
    public static final int SM_CXFOCUSBORDER = 83;
    public static final int SM_CYFOCUSBORDER = 84;
    public static final int SM_TABLETPC = 86;
    public static final int SM_MEDIACENTER = 87;
    public static final int SM_STARTER = 88;
    public static final int SM_SERVERR2 = 89;
    public static final int SM_MOUSEHORIZONTALWHEELPRESENT = 91;
    public static final int SM_CXPADDEDBORDER = 92;
    public static final int SM_REMOTESESSION = 4096;
    public static final int SM_SHUTTINGDOWN = 8192;
    public static final int SM_REMOTECONTROL = 8193;
    public static final int SM_CARETBLINKINGENABLED = 8194;
    public static final int SW_HIDE = 0;
    public static final int SW_SHOWNORMAL = 1;
    public static final int SW_NORMAL = 1;
    public static final int SW_SHOWMINIMIZED = 2;
    public static final int SW_SHOWMAXIMIZED = 3;
    public static final int SW_MAXIMIZE = 3;
    public static final int SW_SHOWNOACTIVATE = 4;
    public static final int SW_SHOW = 5;
    public static final int SW_MINIMIZE = 6;
    public static final int SW_SHOWMINNOACTIVE = 7;
    public static final int SW_SHOWNA = 8;
    public static final int SW_RESTORE = 9;
    public static final int SW_SHOWDEFAULT = 10;
    public static final int SW_FORCEMINIMIZE = 11;
    public static final int SW_MAX = 11;
    public static final int RDW_INVALIDATE = 1;
    public static final int RDW_INTERNALPAINT = 2;
    public static final int RDW_ERASE = 4;
    public static final int RDW_VALIDATE = 8;
    public static final int RDW_NOINTERNALPAINT = 16;
    public static final int RDW_NOERASE = 32;
    public static final int RDW_NOCHILDREN = 64;
    public static final int RDW_ALLCHILDREN = 128;
    public static final int RDW_UPDATENOW = 256;
    public static final int RDW_ERASENOW = 512;
    public static final int RDW_FRAME = 1024;
    public static final int RDW_NOFRAME = 2048;
    public static final int GW_HWNDFIRST = 0;
    public static final int GW_HWNDLAST = 1;
    public static final int GW_HWNDNEXT = 2;
    public static final int GW_HWNDPREV = 3;
    public static final int GW_OWNER = 4;
    public static final int GW_CHILD = 5;
    public static final int GW_ENABLEDPOPUP = 6;
    public static final int SWP_ASYNCWINDOWPOS = 16384;
    public static final int SWP_DEFERERASE = 8192;
    public static final int SWP_DRAWFRAME = 32;
    public static final int SWP_FRAMECHANGED = 32;
    public static final int SWP_HIDEWINDOW = 128;
    public static final int SWP_NOACTIVATE = 16;
    public static final int SWP_NOCOPYBITS = 256;
    public static final int SWP_NOMOVE = 2;
    public static final int SWP_NOOWNERZORDER = 512;
    public static final int SWP_NOREDRAW = 8;
    public static final int SWP_NOREPOSITION = 512;
    public static final int SWP_NOSENDCHANGING = 1024;
    public static final int SWP_NOSIZE = 1;
    public static final int SWP_NOZORDER = 4;
    public static final int SWP_SHOWWINDOW = 64;
    public static final int SC_MINIMIZE = 61472;
    public static final int SC_MAXIMIZE = 61488;
    public static final int BS_PUSHBUTTON = 0;
    public static final int BS_DEFPUSHBUTTON = 1;
    public static final int BS_CHECKBOX = 2;
    public static final int BS_AUTOCHECKBOX = 3;
    public static final int BS_RADIOBUTTON = 4;
    public static final int BS_3STATE = 5;
    public static final int BS_AUTO3STATE = 6;
    public static final int BS_GROUPBOX = 7;
    public static final int BS_USERBUTTON = 8;
    public static final int BS_AUTORADIOBUTTON = 9;
    public static final int BS_PUSHBOX = 10;
    public static final int BS_OWNERDRAW = 11;
    public static final int BS_TYPEMASK = 15;
    public static final int BS_LEFTTEXT = 32;
    public static final int MONITOR_DEFAULTTONULL = 0;
    public static final int MONITOR_DEFAULTTOPRIMARY = 1;
    public static final int MONITOR_DEFAULTTONEAREST = 2;
    public static final int MONITORINFOF_PRIMARY = 1;
    public static final int CCHDEVICENAME = 32;
    public static final int EWX_HYBRID_SHUTDOWN = 4194304;
    public static final int EWX_LOGOFF = 0;
    public static final int EWX_POWEROFF = 8;
    public static final int EWX_REBOOT = 2;
    public static final int EWX_RESTARTAPPS = 64;
    public static final int EWX_SHUTDOWN = 1;
    public static final int EWX_FORCE = 4;
    public static final int EWX_FORCEIFHUNG = 16;
    public static final int GA_PARENT = 1;
    public static final int GA_ROOT = 2;
    public static final int GA_ROOTOWNER = 3;
    public static final int GCW_ATOM = -32;
    public static final int GCL_HICON = -14;
    public static final int GCL_HICONSM = -34;
    public static final int GCL_CBCLSEXTRA = -20;
    public static final int GCL_CBWNDEXTRA = -18;
    public static final int GCLP_HBRBACKGROUND = -10;
    public static final int GCLP_HCURSOR = -12;
    public static final int GCLP_HICON = -14;
    public static final int GCLP_HICONSM = -34;
    public static final int GCLP_HMODULE = -16;
    public static final int GCLP_MENUNAME = -8;
    public static final int GCL_STYLE = -26;
    public static final int GCLP_WNDPROC = -24;
    public static final int SMTO_ABORTIFHUNG = 2;
    public static final int SMTO_BLOCK = 1;
    public static final int SMTO_NORMAL = 0;
    public static final int SMTO_NOTIMEOUTIFNOTHUNG = 8;
    public static final int SMTO_ERRORONEXIT = 32;
    public static final int IDC_APPSTARTING = 32650;
    public static final int IDC_ARROW = 32512;
    public static final int IDC_CROSS = 32515;
    public static final int IDC_HAND = 32649;
    public static final int IDC_HELP = 32651;
    public static final int IDC_IBEAM = 32513;
    public static final int IDC_NO = 32648;
    public static final int IDC_SIZEALL = 32646;
    public static final int IDC_SIZENESW = 32643;
    public static final int IDC_SIZENS = 32645;
    public static final int IDC_SIZENWSE = 32642;
    public static final int IDC_SIZEWE = 32644;
    public static final int IDC_UPARROW = 32516;
    public static final int IDC_WAIT = 32514;
    public static final int IDI_APPLICATION = 32512;
    public static final int IDI_ASTERISK = 32516;
    public static final int IDI_EXCLAMATION = 32515;
    public static final int IDI_HAND = 32513;
    public static final int IDI_QUESTION = 32514;
    public static final int IDI_WINLOGO = 32517;
    public static final int RIM_TYPEMOUSE = 0;
    public static final int RIM_TYPEKEYBOARD = 1;
    public static final int RIM_TYPEHID = 2;
    public static final int CF_BITMAT = 2;
    public static final int CF_DIB = 8;
    public static final int CF_DIBV5 = 17;
    public static final int CF_DIF = 5;
    public static final int CF_DSPBITMAP = 130;
    public static final int CF_DSPENHMETAFILE = 142;
    public static final int CF_DSPMETAFILEPICT = 131;
    public static final int CF_DSPTEXT = 129;
    public static final int CF_ENHMETAFILE = 14;
    public static final int CF_GDIOBJFIRST = 768;
    public static final int CF_GDIOBJLAST = 1023;
    public static final int CF_HDROP = 15;
    public static final int CF_LOCALE = 16;
    public static final int CF_METAFILEPICT = 3;
    public static final int CF_OEMTEXT = 7;
    public static final int CF_OWNERDISPLAY = 128;
    public static final int CF_PALETTE = 9;
    public static final int CF_PENDATA = 10;
    public static final int CF_PRIVATEFIRST = 512;
    public static final int CF_PRIVATELAST = 767;
    public static final int CF_RIFF = 11;
    public static final int CF_SYLK = 4;
    public static final int CF_TEXT = 1;
    public static final int CF_TIFF = 6;
    public static final int CF_UNICODETEXT = 13;
    public static final int CF_WAVE = 12;
    public static final int MAPVK_VK_TO_VSC = 0;
    public static final int MAPVK_VSC_TO_VK = 1;
    public static final int MAPVK_VK_TO_CHAR = 2;
    public static final int MAPVK_VSC_TO_VK_EX = 3;
    public static final int MAPVK_VK_TO_VSC_EX = 4;
    public static final int KL_NAMELENGTH = 9;
    public static final int MODIFIER_SHIFT_MASK = 1;
    public static final int MODIFIER_CTRL_MASK = 2;
    public static final int MODIFIER_ALT_MASK = 4;
    public static final int MODIFIER_HANKAKU_MASK = 8;
    public static final int MODIFIER_RESERVED1_MASK = 16;
    public static final int MODIFIER_RESERVED2_MASK = 32;
    public static final WinDef.HWND HWND_BROADCAST = new WinDef.HWND(Pointer.createConstant(65535));
    public static final WinDef.HWND HWND_MESSAGE = new WinDef.HWND(Pointer.createConstant(-3));
    public static final int DWL_DLGPROC = Native.POINTER_SIZE;
    public static final int DWL_USER = 2 * Native.POINTER_SIZE;

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinUser$BLENDFUNCTION.class */
    @Structure.FieldOrder({"BlendOp", "BlendFlags", "SourceConstantAlpha", "AlphaFormat"})
    public static class BLENDFUNCTION extends Structure {
        public byte BlendOp = 0;
        public byte BlendFlags = 0;
        public byte SourceConstantAlpha;
        public byte AlphaFormat;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinUser$FLASHWINFO.class */
    @Structure.FieldOrder({"cbSize", "hWnd", "dwFlags", "uCount", "dwTimeout"})
    public static class FLASHWINFO extends Structure {
        public int cbSize = size();
        public WinNT.HANDLE hWnd;
        public int dwFlags;
        public int uCount;
        public int dwTimeout;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinUser$GUITHREADINFO.class */
    @Structure.FieldOrder({"cbSize", "flags", "hwndActive", "hwndFocus", "hwndCapture", "hwndMenuOwner", "hwndMoveSize", "hwndCaret", "rcCaret"})
    public static class GUITHREADINFO extends Structure {
        public int cbSize = size();
        public int flags;
        public WinDef.HWND hwndActive;
        public WinDef.HWND hwndFocus;
        public WinDef.HWND hwndCapture;
        public WinDef.HWND hwndMenuOwner;
        public WinDef.HWND hwndMoveSize;
        public WinDef.HWND hwndCaret;
        public WinDef.RECT rcCaret;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinUser$HHOOK.class */
    public static class HHOOK extends WinNT.HANDLE {
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinUser$HOOKPROC.class */
    public interface HOOKPROC extends StdCallLibrary.StdCallCallback {
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinUser$KBDLLHOOKSTRUCT.class */
    @Structure.FieldOrder({"vkCode", "scanCode", "flags", "time", "dwExtraInfo"})
    public static class KBDLLHOOKSTRUCT extends Structure {
        public int vkCode;
        public int scanCode;
        public int flags;
        public int time;
        public BaseTSD.ULONG_PTR dwExtraInfo;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinUser$LASTINPUTINFO.class */
    @Structure.FieldOrder({"cbSize", "dwTime"})
    public static class LASTINPUTINFO extends Structure {
        public int cbSize = size();
        public int dwTime;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinUser$LowLevelKeyboardProc.class */
    public interface LowLevelKeyboardProc extends HOOKPROC {
        WinDef.LRESULT callback(int i, WinDef.WPARAM wparam, KBDLLHOOKSTRUCT kbdllhookstruct);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinUser$LowLevelMouseProc.class */
    public interface LowLevelMouseProc extends HOOKPROC {
        WinDef.LRESULT callback(int i, WinDef.WPARAM wparam, MSLLHOOKSTRUCT msllhookstruct);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinUser$MONITORENUMPROC.class */
    public interface MONITORENUMPROC extends StdCallLibrary.StdCallCallback {
        int apply(HMONITOR hmonitor, WinDef.HDC hdc, WinDef.RECT rect, WinDef.LPARAM lparam);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinUser$MONITORINFO.class */
    @Structure.FieldOrder({"cbSize", "rcMonitor", "rcWork", "dwFlags"})
    public static class MONITORINFO extends Structure {
        public int cbSize = size();
        public WinDef.RECT rcMonitor;
        public WinDef.RECT rcWork;
        public int dwFlags;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinUser$MONITORINFOEX.class */
    @Structure.FieldOrder({"cbSize", "rcMonitor", "rcWork", "dwFlags", "szDevice"})
    public static class MONITORINFOEX extends Structure {
        public WinDef.RECT rcMonitor;
        public WinDef.RECT rcWork;
        public int dwFlags;
        public char[] szDevice = new char[32];
        public int cbSize = size();
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinUser$MSG.class */
    @Structure.FieldOrder({"hWnd", "message", "wParam", "lParam", "time", "pt"})
    public static class MSG extends Structure {
        public WinDef.HWND hWnd;
        public int message;
        public WinDef.WPARAM wParam;
        public WinDef.LPARAM lParam;
        public int time;
        public WinDef.POINT pt;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinUser$MSLLHOOKSTRUCT.class */
    @Structure.FieldOrder({"pt", "mouseData", "flags", "time", "dwExtraInfo"})
    public static class MSLLHOOKSTRUCT extends Structure {
        public WinDef.POINT pt;
        public int mouseData;
        public int flags;
        public int time;
        public BaseTSD.ULONG_PTR dwExtraInfo;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinUser$WINDOWINFO.class */
    @Structure.FieldOrder({"cbSize", "rcWindow", "rcClient", "dwStyle", "dwExStyle", "dwWindowStatus", "cxWindowBorders", "cyWindowBorders", "atomWindowType", "wCreatorVersion"})
    public static class WINDOWINFO extends Structure {
        public int cbSize = size();
        public WinDef.RECT rcWindow;
        public WinDef.RECT rcClient;
        public int dwStyle;
        public int dwExStyle;
        public int dwWindowStatus;
        public int cxWindowBorders;
        public int cyWindowBorders;
        public short atomWindowType;
        public short wCreatorVersion;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinUser$WINDOWPLACEMENT.class */
    @Structure.FieldOrder({XSDatatype.FACET_LENGTH, "flags", "showCmd", "ptMinPosition", "ptMaxPosition", "rcNormalPosition"})
    public static class WINDOWPLACEMENT extends Structure {
        public static final int WPF_SETMINPOSITION = 1;
        public static final int WPF_RESTORETOMAXIMIZED = 2;
        public static final int WPF_ASYNCWINDOWPLACEMENT = 4;
        public int length = size();
        public int flags;
        public int showCmd;
        public WinDef.POINT ptMinPosition;
        public WinDef.POINT ptMaxPosition;
        public WinDef.RECT rcNormalPosition;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinUser$WNDENUMPROC.class */
    public interface WNDENUMPROC extends StdCallLibrary.StdCallCallback {
        boolean callback(WinDef.HWND hwnd, Pointer pointer);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinUser$WinEventProc.class */
    public interface WinEventProc extends Callback {
        void callback(WinNT.HANDLE handle, WinDef.DWORD dword, WinDef.HWND hwnd, WinDef.LONG r4, WinDef.LONG r5, WinDef.DWORD dword2, WinDef.DWORD dword3);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinUser$WindowProc.class */
    public interface WindowProc extends StdCallLibrary.StdCallCallback {
        WinDef.LRESULT callback(WinDef.HWND hwnd, int i, WinDef.WPARAM wparam, WinDef.LPARAM lparam);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinUser$HDEVNOTIFY.class */
    public static class HDEVNOTIFY extends WinDef.PVOID {
        public HDEVNOTIFY() {
        }

        public HDEVNOTIFY(Pointer p) {
            super(p);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinUser$COPYDATASTRUCT.class */
    @Structure.FieldOrder({"dwData", "cbData", "lpData"})
    public static class COPYDATASTRUCT extends Structure {
        public BaseTSD.ULONG_PTR dwData;
        public int cbData;
        public Pointer lpData;

        public COPYDATASTRUCT() {
        }

        public COPYDATASTRUCT(Pointer p) {
            super(p);
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinUser$SIZE.class */
    @Structure.FieldOrder({"cx", "cy"})
    public static class SIZE extends Structure {
        public int cx;
        public int cy;

        public SIZE() {
        }

        public SIZE(int w, int h) {
            this.cx = w;
            this.cy = h;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinUser$CWPSTRUCT.class */
    @Structure.FieldOrder({"lParam", "wParam", "message", "hwnd"})
    public static class CWPSTRUCT extends Structure {
        public WinDef.LPARAM lParam;
        public WinDef.WPARAM wParam;
        public int message;
        public WinDef.HWND hwnd;

        public CWPSTRUCT() {
        }

        public CWPSTRUCT(Pointer p) {
            super(p);
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinUser$HARDWAREINPUT.class */
    @Structure.FieldOrder({"uMsg", "wParamL", "wParamH"})
    public static class HARDWAREINPUT extends Structure {
        public WinDef.DWORD uMsg;
        public WinDef.WORD wParamL;
        public WinDef.WORD wParamH;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinUser$HARDWAREINPUT$ByReference.class */
        public static class ByReference extends HARDWAREINPUT implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        public HARDWAREINPUT() {
        }

        public HARDWAREINPUT(Pointer memory) {
            super(memory);
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinUser$INPUT.class */
    @Structure.FieldOrder({"type", "input"})
    public static class INPUT extends Structure {
        public static final int INPUT_MOUSE = 0;
        public static final int INPUT_KEYBOARD = 1;
        public static final int INPUT_HARDWARE = 2;
        public WinDef.DWORD type;
        public INPUT_UNION input;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinUser$INPUT$ByReference.class */
        public static class ByReference extends INPUT implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        public INPUT() {
            this.input = new INPUT_UNION();
        }

        public INPUT(Pointer memory) {
            super(memory);
            this.input = new INPUT_UNION();
            read();
        }

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinUser$INPUT$INPUT_UNION.class */
        public static class INPUT_UNION extends Union {
            public MOUSEINPUT mi;
            public KEYBDINPUT ki;
            public HARDWAREINPUT hi;

            public INPUT_UNION() {
            }

            public INPUT_UNION(Pointer memory) {
                super(memory);
                read();
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinUser$KEYBDINPUT.class */
    @Structure.FieldOrder({"wVk", "wScan", "dwFlags", "time", "dwExtraInfo"})
    public static class KEYBDINPUT extends Structure {
        public static final int KEYEVENTF_EXTENDEDKEY = 1;
        public static final int KEYEVENTF_KEYUP = 2;
        public static final int KEYEVENTF_UNICODE = 4;
        public static final int KEYEVENTF_SCANCODE = 8;
        public WinDef.WORD wVk;
        public WinDef.WORD wScan;
        public WinDef.DWORD dwFlags;
        public WinDef.DWORD time;
        public BaseTSD.ULONG_PTR dwExtraInfo;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinUser$KEYBDINPUT$ByReference.class */
        public static class ByReference extends KEYBDINPUT implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        public KEYBDINPUT() {
        }

        public KEYBDINPUT(Pointer memory) {
            super(memory);
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinUser$MOUSEINPUT.class */
    @Structure.FieldOrder({"dx", "dy", "mouseData", "dwFlags", "time", "dwExtraInfo"})
    public static class MOUSEINPUT extends Structure {
        public WinDef.LONG dx;
        public WinDef.LONG dy;
        public WinDef.DWORD mouseData;
        public WinDef.DWORD dwFlags;
        public WinDef.DWORD time;
        public BaseTSD.ULONG_PTR dwExtraInfo;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinUser$MOUSEINPUT$ByReference.class */
        public static class ByReference extends MOUSEINPUT implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        public MOUSEINPUT() {
        }

        public MOUSEINPUT(Pointer memory) {
            super(memory);
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinUser$WNDCLASSEX.class */
    @Structure.FieldOrder({"cbSize", Markup.HTML_ATTR_STYLE, "lpfnWndProc", "cbClsExtra", "cbWndExtra", "hInstance", "hIcon", "hCursor", "hbrBackground", "lpszMenuName", "lpszClassName", "hIconSm"})
    public static class WNDCLASSEX extends Structure {
        public int cbSize;
        public int style;
        public Callback lpfnWndProc;
        public int cbClsExtra;
        public int cbWndExtra;
        public WinDef.HINSTANCE hInstance;
        public WinDef.HICON hIcon;
        public WinDef.HCURSOR hCursor;
        public WinDef.HBRUSH hbrBackground;
        public String lpszMenuName;
        public String lpszClassName;
        public WinDef.HICON hIconSm;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinUser$WNDCLASSEX$ByReference.class */
        public static class ByReference extends WNDCLASSEX implements Structure.ByReference {
        }

        public WNDCLASSEX() {
            super(W32APITypeMapper.DEFAULT);
            this.cbSize = size();
        }

        public WNDCLASSEX(Pointer memory) {
            super(memory, 0, W32APITypeMapper.DEFAULT);
            this.cbSize = size();
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinUser$HMONITOR.class */
    public static class HMONITOR extends WinNT.HANDLE {
        public HMONITOR() {
        }

        public HMONITOR(Pointer p) {
            super(p);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinUser$RAWINPUTDEVICELIST.class */
    @Structure.FieldOrder({"hDevice", "dwType"})
    public static class RAWINPUTDEVICELIST extends Structure {
        public WinNT.HANDLE hDevice;
        public int dwType;

        public RAWINPUTDEVICELIST() {
        }

        public RAWINPUTDEVICELIST(Pointer p) {
            super(p);
        }

        public int sizeof() {
            return calculateSize(false);
        }

        @Override // com.sun.jna.Structure
        public String toString() {
            return "hDevice=" + this.hDevice + ", dwType=" + this.dwType;
        }
    }
}
