package com.sun.jna.platform.unix;

import com.aowagie.text.ElementTags;
import com.sun.jna.Callback;
import com.sun.jna.FromNativeContext;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.ptr.ByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.NativeLongByReference;
import com.sun.jna.ptr.PointerByReference;
import es.gob.afirma.signers.cades.CAdESExtraParams;
import es.gob.afirma.signers.xades.XAdESExtraParams;
import org.apache.pdfbox.contentstream.operator.OperatorName;
import org.mozilla.javascript.ES6Iterator;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11.class */
public interface X11 extends Library {
    public static final int XK_0 = 48;
    public static final int XK_9 = 57;
    public static final int XK_A = 65;
    public static final int XK_Z = 90;
    public static final int XK_a = 97;
    public static final int XK_z = 122;
    public static final int XK_Shift_L = 65505;
    public static final int XK_Shift_R = 65506;
    public static final int XK_Control_L = 65507;
    public static final int XK_Control_R = 65508;
    public static final int XK_CapsLock = 65509;
    public static final int XK_ShiftLock = 65510;
    public static final int XK_Meta_L = 65511;
    public static final int XK_Meta_R = 65512;
    public static final int XK_Alt_L = 65513;
    public static final int XK_Alt_R = 65514;
    public static final int VisualNoMask = 0;
    public static final int VisualIDMask = 1;
    public static final int VisualScreenMask = 2;
    public static final int VisualDepthMask = 4;
    public static final int VisualClassMask = 8;
    public static final int VisualRedMaskMask = 16;
    public static final int VisualGreenMaskMask = 32;
    public static final int VisualBlueMaskMask = 64;
    public static final int VisualColormapSizeMask = 128;
    public static final int VisualBitsPerRGBMask = 256;
    public static final int VisualAllMask = 511;
    public static final int None = 0;
    public static final int ParentRelative = 1;
    public static final int CopyFromParent = 0;
    public static final int PointerWindow = 0;
    public static final int InputFocus = 1;
    public static final int PointerRoot = 1;
    public static final int AnyPropertyType = 0;
    public static final int AnyKey = 0;
    public static final int AnyButton = 0;
    public static final int AllTemporary = 0;
    public static final int CurrentTime = 0;
    public static final int NoSymbol = 0;
    public static final int NoEventMask = 0;
    public static final int KeyPressMask = 1;
    public static final int KeyReleaseMask = 2;
    public static final int ButtonPressMask = 4;
    public static final int ButtonReleaseMask = 8;
    public static final int EnterWindowMask = 16;
    public static final int LeaveWindowMask = 32;
    public static final int PointerMotionMask = 64;
    public static final int PointerMotionHintMask = 128;
    public static final int Button1MotionMask = 256;
    public static final int Button2MotionMask = 512;
    public static final int Button3MotionMask = 1024;
    public static final int Button4MotionMask = 2048;
    public static final int Button5MotionMask = 4096;
    public static final int ButtonMotionMask = 8192;
    public static final int KeymapStateMask = 16384;
    public static final int ExposureMask = 32768;
    public static final int VisibilityChangeMask = 65536;
    public static final int StructureNotifyMask = 131072;
    public static final int ResizeRedirectMask = 262144;
    public static final int SubstructureNotifyMask = 524288;
    public static final int SubstructureRedirectMask = 1048576;
    public static final int FocusChangeMask = 2097152;
    public static final int PropertyChangeMask = 4194304;
    public static final int ColormapChangeMask = 8388608;
    public static final int OwnerGrabButtonMask = 16777216;
    public static final int KeyPress = 2;
    public static final int KeyRelease = 3;
    public static final int ButtonPress = 4;
    public static final int ButtonRelease = 5;
    public static final int MotionNotify = 6;
    public static final int EnterNotify = 7;
    public static final int LeaveNotify = 8;
    public static final int FocusIn = 9;
    public static final int FocusOut = 10;
    public static final int KeymapNotify = 11;
    public static final int Expose = 12;
    public static final int GraphicsExpose = 13;
    public static final int NoExpose = 14;
    public static final int VisibilityNotify = 15;
    public static final int CreateNotify = 16;
    public static final int DestroyNotify = 17;
    public static final int UnmapNotify = 18;
    public static final int MapNotify = 19;
    public static final int MapRequest = 20;
    public static final int ReparentNotify = 21;
    public static final int ConfigureNotify = 22;
    public static final int ConfigureRequest = 23;
    public static final int GravityNotify = 24;
    public static final int ResizeRequest = 25;
    public static final int CirculateNotify = 26;
    public static final int CirculateRequest = 27;
    public static final int PropertyNotify = 28;
    public static final int SelectionClear = 29;
    public static final int SelectionRequest = 30;
    public static final int SelectionNotify = 31;
    public static final int ColormapNotify = 32;
    public static final int ClientMessage = 33;
    public static final int MappingNotify = 34;
    public static final int GenericEvent = 35;
    public static final int LASTEvent = 36;
    public static final int ShiftMask = 1;
    public static final int LockMask = 2;
    public static final int ControlMask = 4;
    public static final int Mod1Mask = 8;
    public static final int Mod2Mask = 16;
    public static final int Mod3Mask = 32;
    public static final int Mod4Mask = 64;
    public static final int Mod5Mask = 128;
    public static final int ShiftMapIndex = 0;
    public static final int LockMapIndex = 1;
    public static final int ControlMapIndex = 2;
    public static final int Mod1MapIndex = 3;
    public static final int Mod2MapIndex = 4;
    public static final int Mod3MapIndex = 5;
    public static final int Mod4MapIndex = 6;
    public static final int Mod5MapIndex = 7;
    public static final int Button1Mask = 256;
    public static final int Button2Mask = 512;
    public static final int Button3Mask = 1024;
    public static final int Button4Mask = 2048;
    public static final int Button5Mask = 4096;
    public static final int AnyModifier = 32768;
    public static final int Button1 = 1;
    public static final int Button2 = 2;
    public static final int Button3 = 3;
    public static final int Button4 = 4;
    public static final int Button5 = 5;
    public static final int NotifyNormal = 0;
    public static final int NotifyGrab = 1;
    public static final int NotifyUngrab = 2;
    public static final int NotifyWhileGrabbed = 3;
    public static final int NotifyHint = 1;
    public static final int NotifyAncestor = 0;
    public static final int NotifyVirtual = 1;
    public static final int NotifyInferior = 2;
    public static final int NotifyNonlinear = 3;
    public static final int NotifyNonlinearVirtual = 4;
    public static final int NotifyPointer = 5;
    public static final int NotifyPointerRoot = 6;
    public static final int NotifyDetailNone = 7;
    public static final int VisibilityUnobscured = 0;
    public static final int VisibilityPartiallyObscured = 1;
    public static final int VisibilityFullyObscured = 2;
    public static final int PlaceOnTop = 0;
    public static final int PlaceOnBottom = 1;
    public static final int FamilyInternet = 0;
    public static final int FamilyDECnet = 1;
    public static final int FamilyChaos = 2;
    public static final int FamilyInternet6 = 6;
    public static final int FamilyServerInterpreted = 5;
    public static final int PropertyNewValue = 0;
    public static final int PropertyDelete = 1;
    public static final int ColormapUninstalled = 0;
    public static final int ColormapInstalled = 1;
    public static final int GrabModeSync = 0;
    public static final int GrabModeAsync = 1;
    public static final int GrabSuccess = 0;
    public static final int AlreadyGrabbed = 1;
    public static final int GrabInvalidTime = 2;
    public static final int GrabNotViewable = 3;
    public static final int GrabFrozen = 4;
    public static final int AsyncPointer = 0;
    public static final int SyncPointer = 1;
    public static final int ReplayPointer = 2;
    public static final int AsyncKeyboard = 3;
    public static final int SyncKeyboard = 4;
    public static final int ReplayKeyboard = 5;
    public static final int AsyncBoth = 6;
    public static final int SyncBoth = 7;
    public static final int RevertToNone = 0;
    public static final int RevertToPointerRoot = 1;
    public static final int RevertToParent = 2;
    public static final int Success = 0;
    public static final int BadRequest = 1;
    public static final int BadValue = 2;
    public static final int BadWindow = 3;
    public static final int BadPixmap = 4;
    public static final int BadAtom = 5;
    public static final int BadCursor = 6;
    public static final int BadFont = 7;
    public static final int BadMatch = 8;
    public static final int BadDrawable = 9;
    public static final int BadAccess = 10;
    public static final int BadAlloc = 11;
    public static final int BadColor = 12;
    public static final int BadGC = 13;
    public static final int BadIDChoice = 14;
    public static final int BadName = 15;
    public static final int BadLength = 16;
    public static final int BadImplementation = 17;
    public static final int FirstExtensionError = 128;
    public static final int LastExtensionError = 255;
    public static final int InputOutput = 1;
    public static final int InputOnly = 2;
    public static final int CWBackPixmap = 1;
    public static final int CWBackPixel = 2;
    public static final int CWBorderPixmap = 4;
    public static final int CWBorderPixel = 8;
    public static final int CWBitGravity = 16;
    public static final int CWWinGravity = 32;
    public static final int CWBackingStore = 64;
    public static final int CWBackingPlanes = 128;
    public static final int CWBackingPixel = 256;
    public static final int CWOverrideRedirect = 512;
    public static final int CWSaveUnder = 1024;
    public static final int CWEventMask = 2048;
    public static final int CWDontPropagate = 4096;
    public static final int CWColormap = 8192;
    public static final int CWCursor = 16384;
    public static final int CWX = 1;
    public static final int CWY = 2;
    public static final int CWWidth = 4;
    public static final int CWHeight = 8;
    public static final int CWBorderWidth = 16;
    public static final int CWSibling = 32;
    public static final int CWStackMode = 64;
    public static final int ForgetGravity = 0;
    public static final int NorthWestGravity = 1;
    public static final int NorthGravity = 2;
    public static final int NorthEastGravity = 3;
    public static final int WestGravity = 4;
    public static final int CenterGravity = 5;
    public static final int EastGravity = 6;
    public static final int SouthWestGravity = 7;
    public static final int SouthGravity = 8;
    public static final int SouthEastGravity = 9;
    public static final int StaticGravity = 10;
    public static final int UnmapGravity = 0;
    public static final int NotUseful = 0;
    public static final int WhenMapped = 1;
    public static final int Always = 2;
    public static final int IsUnmapped = 0;
    public static final int IsUnviewable = 1;
    public static final int IsViewable = 2;
    public static final int SetModeInsert = 0;
    public static final int SetModeDelete = 1;
    public static final int DestroyAll = 0;
    public static final int RetainPermanent = 1;
    public static final int RetainTemporary = 2;
    public static final int Above = 0;
    public static final int Below = 1;
    public static final int TopIf = 2;
    public static final int BottomIf = 3;
    public static final int Opposite = 4;
    public static final int RaiseLowest = 0;
    public static final int LowerHighest = 1;
    public static final int PropModeReplace = 0;
    public static final int PropModePrepend = 1;
    public static final int PropModeAppend = 2;
    public static final int GXclear = 0;
    public static final int GXand = 1;
    public static final int GXandReverse = 2;
    public static final int GXcopy = 3;
    public static final int GXandInverted = 4;
    public static final int GXnoop = 5;
    public static final int GXxor = 6;
    public static final int GXor = 7;
    public static final int GXnor = 8;
    public static final int GXequiv = 9;
    public static final int GXinvert = 10;
    public static final int GXorReverse = 11;
    public static final int GXcopyInverted = 12;
    public static final int GXorInverted = 13;
    public static final int GXnand = 14;
    public static final int GXset = 15;
    public static final int LineSolid = 0;
    public static final int LineOnOffDash = 1;
    public static final int LineDoubleDash = 2;
    public static final int CapNotLast = 0;
    public static final int CapButt = 1;
    public static final int CapRound = 2;
    public static final int CapProjecting = 3;
    public static final int JoinMiter = 0;
    public static final int JoinRound = 1;
    public static final int JoinBevel = 2;
    public static final int FillSolid = 0;
    public static final int FillTiled = 1;
    public static final int FillStippled = 2;
    public static final int FillOpaqueStippled = 3;
    public static final int EvenOddRule = 0;
    public static final int WindingRule = 1;
    public static final int ClipByChildren = 0;
    public static final int IncludeInferiors = 1;
    public static final int Unsorted = 0;
    public static final int YSorted = 1;
    public static final int YXSorted = 2;
    public static final int YXBanded = 3;
    public static final int CoordModeOrigin = 0;
    public static final int CoordModePrevious = 1;
    public static final int Complex = 0;
    public static final int Nonconvex = 1;
    public static final int Convex = 2;
    public static final int ArcChord = 0;
    public static final int ArcPieSlice = 1;
    public static final int GCFunction = 1;
    public static final int GCPlaneMask = 2;
    public static final int GCForeground = 4;
    public static final int GCBackground = 8;
    public static final int GCLineWidth = 16;
    public static final int GCLineStyle = 32;
    public static final int GCCapStyle = 64;
    public static final int GCJoinStyle = 128;
    public static final int GCFillStyle = 256;
    public static final int GCFillRule = 512;
    public static final int GCTile = 1024;
    public static final int GCStipple = 2048;
    public static final int GCTileStipXOrigin = 4096;
    public static final int GCTileStipYOrigin = 8192;
    public static final int GCFont = 16384;
    public static final int GCSubwindowMode = 32768;
    public static final int GCGraphicsExposures = 65536;
    public static final int GCClipXOrigin = 131072;
    public static final int GCClipYOrigin = 262144;
    public static final int GCClipMask = 524288;
    public static final int GCDashOffset = 1048576;
    public static final int GCDashList = 2097152;
    public static final int GCArcMode = 4194304;
    public static final int GCLastBit = 22;
    public static final int FontLeftToRight = 0;
    public static final int FontRightToLeft = 1;
    public static final int FontChange = 255;
    public static final int XYBitmap = 0;
    public static final int XYPixmap = 1;
    public static final int ZPixmap = 2;
    public static final int AllocNone = 0;
    public static final int AllocAll = 1;
    public static final int DoRed = 1;
    public static final int DoGreen = 2;
    public static final int DoBlue = 4;
    public static final int CursorShape = 0;
    public static final int TileShape = 1;
    public static final int StippleShape = 2;
    public static final int AutoRepeatModeOff = 0;
    public static final int AutoRepeatModeOn = 1;
    public static final int AutoRepeatModeDefault = 2;
    public static final int LedModeOff = 0;
    public static final int LedModeOn = 1;
    public static final int KBKeyClickPercent = 1;
    public static final int KBBellPercent = 2;
    public static final int KBBellPitch = 4;
    public static final int KBBellDuration = 8;
    public static final int KBLed = 16;
    public static final int KBLedMode = 32;
    public static final int KBKey = 64;
    public static final int KBAutoRepeatMode = 128;
    public static final int MappingSuccess = 0;
    public static final int MappingBusy = 1;
    public static final int MappingFailed = 2;
    public static final int MappingModifier = 0;
    public static final int MappingKeyboard = 1;
    public static final int MappingPointer = 2;
    public static final int DontPreferBlanking = 0;
    public static final int PreferBlanking = 1;
    public static final int DefaultBlanking = 2;
    public static final int DisableScreenSaver = 0;
    public static final int DisableScreenInterval = 0;
    public static final int DontAllowExposures = 0;
    public static final int AllowExposures = 1;
    public static final int DefaultExposures = 2;
    public static final int ScreenSaverReset = 0;
    public static final int ScreenSaverActive = 1;
    public static final int HostInsert = 0;
    public static final int HostDelete = 1;
    public static final int EnableAccess = 1;
    public static final int DisableAccess = 0;
    public static final int StaticGray = 0;
    public static final int GrayScale = 1;
    public static final int StaticColor = 2;
    public static final int PseudoColor = 3;
    public static final int TrueColor = 4;
    public static final int DirectColor = 5;
    public static final int LSBFirst = 0;
    public static final int MSBFirst = 1;
    public static final X11 INSTANCE = (X11) Native.load("X11", X11.class);
    public static final Atom XA_PRIMARY = new Atom(1);
    public static final Atom XA_SECONDARY = new Atom(2);
    public static final Atom XA_ARC = new Atom(3);
    public static final Atom XA_ATOM = new Atom(4);
    public static final Atom XA_BITMAP = new Atom(5);
    public static final Atom XA_CARDINAL = new Atom(6);
    public static final Atom XA_COLORMAP = new Atom(7);
    public static final Atom XA_CURSOR = new Atom(8);
    public static final Atom XA_CUT_BUFFER0 = new Atom(9);
    public static final Atom XA_CUT_BUFFER1 = new Atom(10);
    public static final Atom XA_CUT_BUFFER2 = new Atom(11);
    public static final Atom XA_CUT_BUFFER3 = new Atom(12);
    public static final Atom XA_CUT_BUFFER4 = new Atom(13);
    public static final Atom XA_CUT_BUFFER5 = new Atom(14);
    public static final Atom XA_CUT_BUFFER6 = new Atom(15);
    public static final Atom XA_CUT_BUFFER7 = new Atom(16);
    public static final Atom XA_DRAWABLE = new Atom(17);
    public static final Atom XA_FONT = new Atom(18);
    public static final Atom XA_INTEGER = new Atom(19);
    public static final Atom XA_PIXMAP = new Atom(20);
    public static final Atom XA_POINT = new Atom(21);
    public static final Atom XA_RECTANGLE = new Atom(22);
    public static final Atom XA_RESOURCE_MANAGER = new Atom(23);
    public static final Atom XA_RGB_COLOR_MAP = new Atom(24);
    public static final Atom XA_RGB_BEST_MAP = new Atom(25);
    public static final Atom XA_RGB_BLUE_MAP = new Atom(26);
    public static final Atom XA_RGB_DEFAULT_MAP = new Atom(27);
    public static final Atom XA_RGB_GRAY_MAP = new Atom(28);
    public static final Atom XA_RGB_GREEN_MAP = new Atom(29);
    public static final Atom XA_RGB_RED_MAP = new Atom(30);
    public static final Atom XA_STRING = new Atom(31);
    public static final Atom XA_VISUALID = new Atom(32);
    public static final Atom XA_WINDOW = new Atom(33);
    public static final Atom XA_WM_COMMAND = new Atom(34);
    public static final Atom XA_WM_HINTS = new Atom(35);
    public static final Atom XA_WM_CLIENT_MACHINE = new Atom(36);
    public static final Atom XA_WM_ICON_NAME = new Atom(37);
    public static final Atom XA_WM_ICON_SIZE = new Atom(38);
    public static final Atom XA_WM_NAME = new Atom(39);
    public static final Atom XA_WM_NORMAL_HINTS = new Atom(40);
    public static final Atom XA_WM_SIZE_HINTS = new Atom(41);
    public static final Atom XA_WM_ZOOM_HINTS = new Atom(42);
    public static final Atom XA_MIN_SPACE = new Atom(43);
    public static final Atom XA_NORM_SPACE = new Atom(44);
    public static final Atom XA_MAX_SPACE = new Atom(45);
    public static final Atom XA_END_SPACE = new Atom(46);
    public static final Atom XA_SUPERSCRIPT_X = new Atom(47);
    public static final Atom XA_SUPERSCRIPT_Y = new Atom(48);
    public static final Atom XA_SUBSCRIPT_X = new Atom(49);
    public static final Atom XA_SUBSCRIPT_Y = new Atom(50);
    public static final Atom XA_UNDERLINE_POSITION = new Atom(51);
    public static final Atom XA_UNDERLINE_THICKNESS = new Atom(52);
    public static final Atom XA_STRIKEOUT_ASCENT = new Atom(53);
    public static final Atom XA_STRIKEOUT_DESCENT = new Atom(54);
    public static final Atom XA_ITALIC_ANGLE = new Atom(55);
    public static final Atom XA_X_HEIGHT = new Atom(56);
    public static final Atom XA_QUAD_WIDTH = new Atom(57);
    public static final Atom XA_WEIGHT = new Atom(58);
    public static final Atom XA_POINT_SIZE = new Atom(59);
    public static final Atom XA_RESOLUTION = new Atom(60);
    public static final Atom XA_COPYRIGHT = new Atom(61);
    public static final Atom XA_NOTICE = new Atom(62);
    public static final Atom XA_FONT_NAME = new Atom(63);
    public static final Atom XA_FAMILY_NAME = new Atom(64);
    public static final Atom XA_FULL_NAME = new Atom(65);
    public static final Atom XA_CAP_HEIGHT = new Atom(66);
    public static final Atom XA_WM_CLASS = new Atom(67);
    public static final Atom XA_WM_TRANSIENT_FOR = new Atom(68);
    public static final Atom XA_LAST_PREDEFINED = XA_WM_TRANSIENT_FOR;

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$Display.class */
    public static class Display extends PointerType {
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$GC.class */
    public static class GC extends PointerType {
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$Screen.class */
    public static class Screen extends PointerType {
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XAnyEvent.class */
    @Structure.FieldOrder({"type", "serial", "send_event", "display", "window"})
    public static class XAnyEvent extends Structure {
        public int type;
        public NativeLong serial;
        public int send_event;
        public Display display;
        public Window window;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XButtonEvent.class */
    @Structure.FieldOrder({"type", "serial", "send_event", "display", "window", "root", "subwindow", "time", "x", OperatorName.CURVE_TO_REPLICATE_FINAL_POINT, "x_root", "y_root", "state", "button", "same_screen"})
    public static class XButtonEvent extends Structure {
        public int type;
        public NativeLong serial;
        public int send_event;
        public Display display;
        public Window window;
        public Window root;
        public Window subwindow;
        public NativeLong time;
        public int x;
        public int y;
        public int x_root;
        public int y_root;
        public int state;
        public int button;
        public int same_screen;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XButtonPressedEvent.class */
    public static class XButtonPressedEvent extends XButtonEvent {
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XButtonReleasedEvent.class */
    public static class XButtonReleasedEvent extends XButtonEvent {
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XCirculateEvent.class */
    @Structure.FieldOrder({"type", "serial", "send_event", "display", "event", "window", "place"})
    public static class XCirculateEvent extends Structure {
        public int type;
        public NativeLong serial;
        public int send_event;
        public Display display;
        public Window event;
        public Window window;
        public int place;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XCirculateRequestEvent.class */
    @Structure.FieldOrder({"type", "serial", "send_event", "display", "parent", "window", "place"})
    public static class XCirculateRequestEvent extends Structure {
        public int type;
        public NativeLong serial;
        public int send_event;
        public Display display;
        public Window parent;
        public Window window;
        public int place;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XClientMessageEvent.class */
    @Structure.FieldOrder({"type", "serial", "send_event", "display", "window", "message_type", XAdESExtraParams.FORMAT, "data"})
    public static class XClientMessageEvent extends Structure {
        public int type;
        public NativeLong serial;
        public int send_event;
        public Display display;
        public Window window;
        public Atom message_type;
        public int format;
        public Data data;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XClientMessageEvent$Data.class */
        public static class Data extends Union {
            public byte[] b = new byte[20];
            public short[] s = new short[10];
            public NativeLong[] l = new NativeLong[5];
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XColormapEvent.class */
    @Structure.FieldOrder({"type", "serial", "send_event", "display", "window", "colormap", "c_new", "state"})
    public static class XColormapEvent extends Structure {
        public int type;
        public NativeLong serial;
        public int send_event;
        public Display display;
        public Window window;
        public Colormap colormap;
        public int c_new;
        public int state;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XConfigureEvent.class */
    @Structure.FieldOrder({"type", "serial", "send_event", "display", "event", "window", "x", OperatorName.CURVE_TO_REPLICATE_FINAL_POINT, ElementTags.WIDTH, "height", "border_width", "above", "override_redirect"})
    public static class XConfigureEvent extends Structure {
        public int type;
        public NativeLong serial;
        public int send_event;
        public Display display;
        public Window event;
        public Window window;
        public int x;
        public int y;
        public int width;
        public int height;
        public int border_width;
        public Window above;
        public int override_redirect;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XConfigureRequestEvent.class */
    @Structure.FieldOrder({"type", "serial", "send_event", "display", "parent", "window", "x", OperatorName.CURVE_TO_REPLICATE_FINAL_POINT, ElementTags.WIDTH, "height", "border_width", "above", "detail", "value_mask"})
    public static class XConfigureRequestEvent extends Structure {
        public int type;
        public NativeLong serial;
        public int send_event;
        public Display display;
        public Window parent;
        public Window window;
        public int x;
        public int y;
        public int width;
        public int height;
        public int border_width;
        public Window above;
        public int detail;
        public NativeLong value_mask;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XCreateWindowEvent.class */
    @Structure.FieldOrder({"type", "serial", "send_event", "display", "parent", "window", "x", OperatorName.CURVE_TO_REPLICATE_FINAL_POINT, ElementTags.WIDTH, "height", "border_width", "override_redirect"})
    public static class XCreateWindowEvent extends Structure {
        public int type;
        public NativeLong serial;
        public int send_event;
        public Display display;
        public Window parent;
        public Window window;
        public int x;
        public int y;
        public int width;
        public int height;
        public int border_width;
        public int override_redirect;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XCrossingEvent.class */
    @Structure.FieldOrder({"type", "serial", "send_event", "display", "window", "root", "subwindow", "time", "x", OperatorName.CURVE_TO_REPLICATE_FINAL_POINT, "x_root", "y_root", CAdESExtraParams.MODE, "detail", "same_screen", "focus", "state"})
    public static class XCrossingEvent extends Structure {
        public int type;
        public NativeLong serial;
        public int send_event;
        public Display display;
        public Window window;
        public Window root;
        public Window subwindow;
        public NativeLong time;
        public int x;
        public int y;
        public int x_root;
        public int y_root;
        public int mode;
        public int detail;
        public int same_screen;
        public int focus;
        public int state;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XDestroyWindowEvent.class */
    @Structure.FieldOrder({"type", "serial", "send_event", "display", "event", "window"})
    public static class XDestroyWindowEvent extends Structure {
        public int type;
        public NativeLong serial;
        public int send_event;
        public Display display;
        public Window event;
        public Window window;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XDeviceByReference.class */
    @Structure.FieldOrder({"device_id", "num_classes", "classes"})
    public static class XDeviceByReference extends Structure implements Structure.ByReference {
        public XID device_id;
        public int num_classes;
        public XInputClassInfoByReference classes;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XEnterWindowEvent.class */
    public static class XEnterWindowEvent extends XCrossingEvent {
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XErrorEvent.class */
    @Structure.FieldOrder({"type", "display", "serial", "error_code", "request_code", "minor_code", "resourceid"})
    public static class XErrorEvent extends Structure {
        public int type;
        public Display display;
        public NativeLong serial;
        public byte error_code;
        public byte request_code;
        public byte minor_code;
        public XID resourceid;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XErrorHandler.class */
    public interface XErrorHandler extends Callback {
        int apply(Display display, XErrorEvent xErrorEvent);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XEvent.class */
    public static class XEvent extends Union {
        public int type;
        public XAnyEvent xany;
        public XKeyEvent xkey;
        public XButtonEvent xbutton;
        public XMotionEvent xmotion;
        public XCrossingEvent xcrossing;
        public XFocusChangeEvent xfocus;
        public XExposeEvent xexpose;
        public XGraphicsExposeEvent xgraphicsexpose;
        public XNoExposeEvent xnoexpose;
        public XVisibilityEvent xvisibility;
        public XCreateWindowEvent xcreatewindow;
        public XDestroyWindowEvent xdestroywindow;
        public XUnmapEvent xunmap;
        public XMapEvent xmap;
        public XMapRequestEvent xmaprequest;
        public XReparentEvent xreparent;
        public XConfigureEvent xconfigure;
        public XGravityEvent xgravity;
        public XResizeRequestEvent xresizerequest;
        public XConfigureRequestEvent xconfigurerequest;
        public XCirculateEvent xcirculate;
        public XCirculateRequestEvent xcirculaterequest;
        public XPropertyEvent xproperty;
        public XSelectionClearEvent xselectionclear;
        public XSelectionRequestEvent xselectionrequest;
        public XSelectionEvent xselection;
        public XColormapEvent xcolormap;
        public XClientMessageEvent xclient;
        public XMappingEvent xmapping;
        public XErrorEvent xerror;
        public XKeymapEvent xkeymap;
        public XGenericEvent xgeneric;
        public XGenericEventCookie xcookie;
        public NativeLong[] pad = new NativeLong[24];
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XExposeEvent.class */
    @Structure.FieldOrder({"type", "serial", "send_event", "display", "window", "x", OperatorName.CURVE_TO_REPLICATE_FINAL_POINT, ElementTags.WIDTH, "height", "count"})
    public static class XExposeEvent extends Structure {
        public int type;
        public NativeLong serial;
        public int send_event;
        public Display display;
        public Window window;
        public int x;
        public int y;
        public int width;
        public int height;
        public int count;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XFocusChangeEvent.class */
    @Structure.FieldOrder({"type", "serial", "send_event", "display", "window", CAdESExtraParams.MODE, "detail"})
    public static class XFocusChangeEvent extends Structure {
        public int type;
        public NativeLong serial;
        public int send_event;
        public Display display;
        public Window window;
        public int mode;
        public int detail;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XFocusInEvent.class */
    public static class XFocusInEvent extends XFocusChangeEvent {
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XFocusOutEvent.class */
    public static class XFocusOutEvent extends XFocusChangeEvent {
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XGCValues.class */
    @Structure.FieldOrder({"function", "plane_mask", "foreground", "background", "line_width", "line_style", "cap_style", "join_style", "fill_style", "fill_rule", "arc_mode", "tile", "stipple", "ts_x_origin", "ts_y_origin", ElementTags.FONT, "subwindow_mode", "graphics_exposures", "clip_x_origin", "clip_y_origin", "clip_mask", "dash_offset", "dashes"})
    public static class XGCValues extends Structure {
        public int function;
        public NativeLong plane_mask;
        public NativeLong foreground;
        public NativeLong background;
        public int line_width;
        public int line_style;
        public int cap_style;
        public int join_style;
        public int fill_style;
        public int fill_rule;
        public int arc_mode;
        public Pixmap tile;
        public Pixmap stipple;
        public int ts_x_origin;
        public int ts_y_origin;
        public Font font;
        public int subwindow_mode;
        public boolean graphics_exposures;
        public int clip_x_origin;
        public int clip_y_origin;
        public Pixmap clip_mask;
        public int dash_offset;
        public byte dashes;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XGenericEvent.class */
    @Structure.FieldOrder({"type", "serial", "send_event", "display", "extension", "evtype"})
    public static class XGenericEvent extends Structure {
        public int type;
        public NativeLong serial;
        public int send_event;
        public Display display;
        public int extension;
        public int evtype;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XGenericEventCookie.class */
    @Structure.FieldOrder({"type", "serial", "send_event", "display", "extension", "evtype", "cookie", "data"})
    public static class XGenericEventCookie extends Structure {
        public int type;
        public NativeLong serial;
        public int send_event;
        public Display display;
        public int extension;
        public int evtype;
        public int cookie;
        public Pointer data;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XGraphicsExposeEvent.class */
    @Structure.FieldOrder({"type", "serial", "send_event", "display", "drawable", "x", OperatorName.CURVE_TO_REPLICATE_FINAL_POINT, ElementTags.WIDTH, "height", "count", "major_code", "minor_code"})
    public static class XGraphicsExposeEvent extends Structure {
        public int type;
        public NativeLong serial;
        public int send_event;
        public Display display;
        public Drawable drawable;
        public int x;
        public int y;
        public int width;
        public int height;
        public int count;
        public int major_code;
        public int minor_code;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XGravityEvent.class */
    @Structure.FieldOrder({"type", "serial", "send_event", "display", "event", "window", "x", OperatorName.CURVE_TO_REPLICATE_FINAL_POINT})
    public static class XGravityEvent extends Structure {
        public int type;
        public NativeLong serial;
        public int send_event;
        public Display display;
        public Window event;
        public Window window;
        public int x;
        public int y;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XImage.class */
    public static class XImage extends PointerType {
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XInputClassInfoByReference.class */
    @Structure.FieldOrder({"input_class", "event_type_base"})
    public static class XInputClassInfoByReference extends Structure implements Structure.ByReference {
        public byte input_class;
        public byte event_type_base;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XKeyEvent.class */
    @Structure.FieldOrder({"type", "serial", "send_event", "display", "window", "root", "subwindow", "time", "x", OperatorName.CURVE_TO_REPLICATE_FINAL_POINT, "x_root", "y_root", "state", "keycode", "same_screen"})
    public static class XKeyEvent extends Structure {
        public int type;
        public NativeLong serial;
        public int send_event;
        public Display display;
        public Window window;
        public Window root;
        public Window subwindow;
        public NativeLong time;
        public int x;
        public int y;
        public int x_root;
        public int y_root;
        public int state;
        public int keycode;
        public int same_screen;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XKeymapEvent.class */
    @Structure.FieldOrder({"type", "serial", "send_event", "display", "window", "key_vector"})
    public static class XKeymapEvent extends Structure {
        public int type;
        public NativeLong serial;
        public int send_event;
        public Display display;
        public Window window;
        public byte[] key_vector = new byte[32];
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XLeaveWindowEvent.class */
    public static class XLeaveWindowEvent extends XCrossingEvent {
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XMapEvent.class */
    @Structure.FieldOrder({"type", "serial", "send_event", "display", "event", "window", "override_redirect"})
    public static class XMapEvent extends Structure {
        public int type;
        public NativeLong serial;
        public int send_event;
        public Display display;
        public Window event;
        public Window window;
        public int override_redirect;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XMapRequestEvent.class */
    @Structure.FieldOrder({"type", "serial", "send_event", "display", "parent", "window"})
    public static class XMapRequestEvent extends Structure {
        public int type;
        public NativeLong serial;
        public int send_event;
        public Display display;
        public Window parent;
        public Window window;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XMappingEvent.class */
    @Structure.FieldOrder({"type", "serial", "send_event", "display", "window", "request", "first_keycode", "count"})
    public static class XMappingEvent extends Structure {
        public int type;
        public NativeLong serial;
        public int send_event;
        public Display display;
        public Window window;
        public int request;
        public int first_keycode;
        public int count;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XModifierKeymapRef.class */
    @Structure.FieldOrder({"max_keypermod", "modifiermap"})
    public static class XModifierKeymapRef extends Structure implements Structure.ByReference {
        public int max_keypermod;
        public Pointer modifiermap;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XMotionEvent.class */
    @Structure.FieldOrder({"type", "serial", "send_event", "display", "window", "root", "subwindow", "time", "x", OperatorName.CURVE_TO_REPLICATE_FINAL_POINT, "x_root", "y_root", "state", "is_hint", "same_screen"})
    public static class XMotionEvent extends Structure {
        public int type;
        public NativeLong serial;
        public int send_event;
        public Display display;
        public Window window;
        public Window root;
        public Window subwindow;
        public NativeLong time;
        public int x;
        public int y;
        public int x_root;
        public int y_root;
        public int state;
        public byte is_hint;
        public int same_screen;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XNoExposeEvent.class */
    @Structure.FieldOrder({"type", "serial", "send_event", "display", "drawable", "major_code", "minor_code"})
    public static class XNoExposeEvent extends Structure {
        public int type;
        public NativeLong serial;
        public int send_event;
        public Display display;
        public Drawable drawable;
        public int major_code;
        public int minor_code;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XPointerMovedEvent.class */
    public static class XPointerMovedEvent extends XMotionEvent {
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XPropertyEvent.class */
    @Structure.FieldOrder({"type", "serial", "send_event", "display", "window", "atom", "time", "state"})
    public static class XPropertyEvent extends Structure {
        public int type;
        public NativeLong serial;
        public int send_event;
        public Display display;
        public Window window;
        public Atom atom;
        public NativeLong time;
        public int state;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XReparentEvent.class */
    @Structure.FieldOrder({"type", "serial", "send_event", "display", "event", "window", "parent", "x", OperatorName.CURVE_TO_REPLICATE_FINAL_POINT, "override_redirect"})
    public static class XReparentEvent extends Structure {
        public int type;
        public NativeLong serial;
        public int send_event;
        public Display display;
        public Window event;
        public Window window;
        public Window parent;
        public int x;
        public int y;
        public int override_redirect;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XResizeRequestEvent.class */
    @Structure.FieldOrder({"type", "serial", "send_event", "display", "window", ElementTags.WIDTH, "height"})
    public static class XResizeRequestEvent extends Structure {
        public int type;
        public NativeLong serial;
        public int send_event;
        public Display display;
        public Window window;
        public int width;
        public int height;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XSelectionClearEvent.class */
    @Structure.FieldOrder({"type", "serial", "send_event", "display", "window", "selection", "time"})
    public static class XSelectionClearEvent extends Structure {
        public int type;
        public NativeLong serial;
        public int send_event;
        public Display display;
        public Window window;
        public Atom selection;
        public NativeLong time;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XSelectionEvent.class */
    @Structure.FieldOrder({"type", "serial", "send_event", "display", "requestor", "selection", "target", "property", "time"})
    public static class XSelectionEvent extends Structure {
        public int type;
        public NativeLong serial;
        public int send_event;
        public Display display;
        public Window requestor;
        public Atom selection;
        public Atom target;
        public Atom property;
        public NativeLong time;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XSelectionRequestEvent.class */
    @Structure.FieldOrder({"type", "serial", "send_event", "display", "owner", "requestor", "selection", "target", "property", "time"})
    public static class XSelectionRequestEvent extends Structure {
        public int type;
        public NativeLong serial;
        public int send_event;
        public Display display;
        public Window owner;
        public Window requestor;
        public Atom selection;
        public Atom target;
        public Atom property;
        public NativeLong time;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XSetWindowAttributes.class */
    @Structure.FieldOrder({"background_pixmap", "background_pixel", "border_pixmap", "border_pixel", "bit_gravity", "win_gravity", "backing_store", "backing_planes", "backing_pixel", "save_under", "event_mask", "do_not_propagate_mask", "override_redirect", "colormap", "cursor"})
    public static class XSetWindowAttributes extends Structure {
        public Pixmap background_pixmap;
        public NativeLong background_pixel;
        public Pixmap border_pixmap;
        public NativeLong border_pixel;
        public int bit_gravity;
        public int win_gravity;
        public int backing_store;
        public NativeLong backing_planes;
        public NativeLong backing_pixel;
        public boolean save_under;
        public NativeLong event_mask;
        public NativeLong do_not_propagate_mask;
        public boolean override_redirect;
        public Colormap colormap;
        public Cursor cursor;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XSizeHints.class */
    @Structure.FieldOrder({"flags", "x", OperatorName.CURVE_TO_REPLICATE_FINAL_POINT, ElementTags.WIDTH, "height", "min_width", "min_height", "max_width", "max_height", "width_inc", "height_inc", "min_aspect", "max_aspect", "base_width", "base_height", "win_gravity"})
    public static class XSizeHints extends Structure {
        public NativeLong flags;
        public int x;
        public int y;
        public int width;
        public int height;
        public int min_width;
        public int min_height;
        public int max_width;
        public int max_height;
        public int width_inc;
        public int height_inc;
        public Aspect min_aspect;
        public Aspect max_aspect;
        public int base_width;
        public int base_height;
        public int win_gravity;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XSizeHints$Aspect.class */
        @Structure.FieldOrder({"x", OperatorName.CURVE_TO_REPLICATE_FINAL_POINT})
        public static class Aspect extends Structure {
            public int x;
            public int y;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XTest.class */
    public interface XTest extends Library {
        public static final XTest INSTANCE = (XTest) Native.load("Xtst", XTest.class);

        boolean XTestQueryExtension(Display display, IntByReference intByReference, IntByReference intByReference2, IntByReference intByReference3, IntByReference intByReference4);

        boolean XTestCompareCursorWithWindow(Display display, Window window, Cursor cursor);

        boolean XTestCompareCurrentCursorWithWindow(Display display, Window window);

        int XTestFakeKeyEvent(Display display, int i, boolean z, NativeLong nativeLong);

        int XTestFakeButtonEvent(Display display, int i, boolean z, NativeLong nativeLong);

        int XTestFakeMotionEvent(Display display, int i, int i2, int i3, NativeLong nativeLong);

        int XTestFakeRelativeMotionEvent(Display display, int i, int i2, NativeLong nativeLong);

        int XTestFakeDeviceKeyEvent(Display display, XDeviceByReference xDeviceByReference, int i, boolean z, IntByReference intByReference, int i2, NativeLong nativeLong);

        int XTestFakeDeviceButtonEvent(Display display, XDeviceByReference xDeviceByReference, int i, boolean z, IntByReference intByReference, int i2, NativeLong nativeLong);

        int XTestFakeProximityEvent(Display display, XDeviceByReference xDeviceByReference, boolean z, IntByReference intByReference, int i, NativeLong nativeLong);

        int XTestFakeDeviceMotionEvent(Display display, XDeviceByReference xDeviceByReference, boolean z, int i, IntByReference intByReference, int i2, NativeLong nativeLong);

        int XTestGrabControl(Display display, boolean z);

        void XTestSetVisualIDOfVisual(Visual visual, VisualID visualID);

        int XTestDiscard(Display display);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XTextProperty.class */
    @Structure.FieldOrder({ES6Iterator.VALUE_PROPERTY, "encoding", XAdESExtraParams.FORMAT, "nitems"})
    public static class XTextProperty extends Structure {
        public String value;
        public Atom encoding;
        public int format;
        public NativeLong nitems;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XUnmapEvent.class */
    @Structure.FieldOrder({"type", "serial", "send_event", "display", "event", "window", "from_configure"})
    public static class XUnmapEvent extends Structure {
        public int type;
        public NativeLong serial;
        public int send_event;
        public Display display;
        public Window event;
        public Window window;
        public int from_configure;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XVisibilityEvent.class */
    @Structure.FieldOrder({"type", "serial", "send_event", "display", "window", "state"})
    public static class XVisibilityEvent extends Structure {
        public int type;
        public NativeLong serial;
        public int send_event;
        public Display display;
        public Window window;
        public int state;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XVisualInfo.class */
    @Structure.FieldOrder({"visual", "visualid", "screen", "depth", "c_class", "red_mask", "green_mask", "blue_mask", "colormap_size", "bits_per_rgb"})
    public static class XVisualInfo extends Structure {
        public Visual visual;
        public VisualID visualid;
        public int screen;
        public int depth;
        public int c_class;
        public NativeLong red_mask;
        public NativeLong green_mask;
        public NativeLong blue_mask;
        public int colormap_size;
        public int bits_per_rgb;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XWMHints.class */
    @Structure.FieldOrder({"flags", "input", "initial_state", "icon_pixmap", "icon_window", "icon_x", "icon_y", "icon_mask", "window_group"})
    public static class XWMHints extends Structure {
        public NativeLong flags;
        public boolean input;
        public int initial_state;
        public Pixmap icon_pixmap;
        public Window icon_window;
        public int icon_x;
        public int icon_y;
        public Pixmap icon_mask;
        public XID window_group;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XWindowAttributes.class */
    @Structure.FieldOrder({"x", OperatorName.CURVE_TO_REPLICATE_FINAL_POINT, ElementTags.WIDTH, "height", "border_width", "depth", "visual", "root", "c_class", "bit_gravity", "win_gravity", "backing_store", "backing_planes", "backing_pixel", "save_under", "colormap", "map_installed", "map_state", "all_event_masks", "your_event_mask", "do_not_propagate_mask", "override_redirect", "screen"})
    public static class XWindowAttributes extends Structure {
        public int x;
        public int y;
        public int width;
        public int height;
        public int border_width;
        public int depth;
        public Visual visual;
        public Window root;
        public int c_class;
        public int bit_gravity;
        public int win_gravity;
        public int backing_store;
        public NativeLong backing_planes;
        public NativeLong backing_pixel;
        public boolean save_under;
        public Colormap colormap;
        public boolean map_installed;
        public int map_state;
        public NativeLong all_event_masks;
        public NativeLong your_event_mask;
        public NativeLong do_not_propagate_mask;
        public boolean override_redirect;
        public Screen screen;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$Xevie.class */
    public interface Xevie extends Library {
        public static final Xevie INSTANCE = (Xevie) Native.load("Xevie", Xevie.class);
        public static final int XEVIE_UNMODIFIED = 0;
        public static final int XEVIE_MODIFIED = 1;

        boolean XevieQueryVersion(Display display, IntByReference intByReference, IntByReference intByReference2);

        int XevieStart(Display display);

        int XevieEnd(Display display);

        int XevieSendEvent(Display display, XEvent xEvent, int i);

        int XevieSelectInput(Display display, NativeLong nativeLong);
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$Xext.class */
    public interface Xext extends Library {
        public static final Xext INSTANCE = (Xext) Native.load("Xext", Xext.class);
        public static final int ShapeBounding = 0;
        public static final int ShapeClip = 1;
        public static final int ShapeInput = 2;
        public static final int ShapeSet = 0;
        public static final int ShapeUnion = 1;
        public static final int ShapeIntersect = 2;
        public static final int ShapeSubtract = 3;
        public static final int ShapeInvert = 4;

        void XShapeCombineMask(Display display, Window window, int i, int i2, int i3, Pixmap pixmap, int i4);
    }

    boolean XQueryExtension(Display display, String str, IntByReference intByReference, IntByReference intByReference2, IntByReference intByReference3);

    Display XOpenDisplay(String str);

    int XGetErrorText(Display display, int i, byte[] bArr, int i2);

    int XDefaultScreen(Display display);

    Screen DefaultScreenOfDisplay(Display display);

    Visual XDefaultVisual(Display display, int i);

    Colormap XDefaultColormap(Display display, int i);

    int XDisplayWidth(Display display, int i);

    int XDisplayHeight(Display display, int i);

    Window XDefaultRootWindow(Display display);

    Window XRootWindow(Display display, int i);

    int XAllocNamedColor(Display display, int i, String str, Pointer pointer, Pointer pointer2);

    XSizeHints XAllocSizeHints();

    void XSetWMProperties(Display display, Window window, String str, String str2, String[] strArr, int i, XSizeHints xSizeHints, Pointer pointer, Pointer pointer2);

    int XSetWMProtocols(Display display, Window window, Atom[] atomArr, int i);

    int XGetWMProtocols(Display display, Window window, PointerByReference pointerByReference, IntByReference intByReference);

    int XFree(Pointer pointer);

    Window XCreateSimpleWindow(Display display, Window window, int i, int i2, int i3, int i4, int i5, int i6, int i7);

    Pixmap XCreateBitmapFromData(Display display, Window window, Pointer pointer, int i, int i2);

    int XMapWindow(Display display, Window window);

    int XMapRaised(Display display, Window window);

    int XMapSubwindows(Display display, Window window);

    int XFlush(Display display);

    int XSync(Display display, boolean z);

    int XEventsQueued(Display display, int i);

    int XPending(Display display);

    int XUnmapWindow(Display display, Window window);

    int XDestroyWindow(Display display, Window window);

    int XCloseDisplay(Display display);

    int XClearWindow(Display display, Window window);

    int XClearArea(Display display, Window window, int i, int i2, int i3, int i4, int i5);

    Pixmap XCreatePixmap(Display display, Drawable drawable, int i, int i2, int i3);

    int XFreePixmap(Display display, Pixmap pixmap);

    GC XCreateGC(Display display, Drawable drawable, NativeLong nativeLong, XGCValues xGCValues);

    int XSetFillRule(Display display, GC gc, int i);

    int XFreeGC(Display display, GC gc);

    int XDrawPoint(Display display, Drawable drawable, GC gc, int i, int i2);

    int XDrawPoints(Display display, Drawable drawable, GC gc, XPoint[] xPointArr, int i, int i2);

    int XFillRectangle(Display display, Drawable drawable, GC gc, int i, int i2, int i3, int i4);

    int XFillRectangles(Display display, Drawable drawable, GC gc, XRectangle[] xRectangleArr, int i);

    int XSetForeground(Display display, GC gc, NativeLong nativeLong);

    int XSetBackground(Display display, GC gc, NativeLong nativeLong);

    int XFillArc(Display display, Drawable drawable, GC gc, int i, int i2, int i3, int i4, int i5, int i6);

    int XFillPolygon(Display display, Drawable drawable, GC gc, XPoint[] xPointArr, int i, int i2, int i3);

    int XQueryTree(Display display, Window window, WindowByReference windowByReference, WindowByReference windowByReference2, PointerByReference pointerByReference, IntByReference intByReference);

    boolean XQueryPointer(Display display, Window window, WindowByReference windowByReference, WindowByReference windowByReference2, IntByReference intByReference, IntByReference intByReference2, IntByReference intByReference3, IntByReference intByReference4, IntByReference intByReference5);

    int XGetWindowAttributes(Display display, Window window, XWindowAttributes xWindowAttributes);

    int XChangeWindowAttributes(Display display, Window window, NativeLong nativeLong, XSetWindowAttributes xSetWindowAttributes);

    int XGetGeometry(Display display, Drawable drawable, WindowByReference windowByReference, IntByReference intByReference, IntByReference intByReference2, IntByReference intByReference3, IntByReference intByReference4, IntByReference intByReference5, IntByReference intByReference6);

    boolean XTranslateCoordinates(Display display, Window window, Window window2, int i, int i2, IntByReference intByReference, IntByReference intByReference2, WindowByReference windowByReference);

    int XSelectInput(Display display, Window window, NativeLong nativeLong);

    int XSendEvent(Display display, Window window, int i, NativeLong nativeLong, XEvent xEvent);

    int XNextEvent(Display display, XEvent xEvent);

    int XPeekEvent(Display display, XEvent xEvent);

    int XWindowEvent(Display display, Window window, NativeLong nativeLong, XEvent xEvent);

    boolean XCheckWindowEvent(Display display, Window window, NativeLong nativeLong, XEvent xEvent);

    int XMaskEvent(Display display, NativeLong nativeLong, XEvent xEvent);

    boolean XCheckMaskEvent(Display display, NativeLong nativeLong, XEvent xEvent);

    boolean XCheckTypedEvent(Display display, int i, XEvent xEvent);

    boolean XCheckTypedWindowEvent(Display display, Window window, int i, XEvent xEvent);

    boolean XGetEventData(Display display, XGenericEventCookie xGenericEventCookie);

    void XFreeEventData(Display display, XGenericEventCookie xGenericEventCookie);

    XWMHints XGetWMHints(Display display, Window window);

    int XGetWMName(Display display, Window window, XTextProperty xTextProperty);

    XVisualInfo XGetVisualInfo(Display display, NativeLong nativeLong, XVisualInfo xVisualInfo, IntByReference intByReference);

    Colormap XCreateColormap(Display display, Window window, Visual visual, int i);

    int XGetWindowProperty(Display display, Window window, Atom atom, NativeLong nativeLong, NativeLong nativeLong2, boolean z, Atom atom2, AtomByReference atomByReference, IntByReference intByReference, NativeLongByReference nativeLongByReference, NativeLongByReference nativeLongByReference2, PointerByReference pointerByReference);

    int XChangeProperty(Display display, Window window, Atom atom, Atom atom2, int i, int i2, Pointer pointer, int i3);

    int XDeleteProperty(Display display, Window window, Atom atom);

    Atom XInternAtom(Display display, String str, boolean z);

    String XGetAtomName(Display display, Atom atom);

    int XCopyArea(Display display, Drawable drawable, Drawable drawable2, GC gc, int i, int i2, int i3, int i4, int i5, int i6);

    XImage XCreateImage(Display display, Visual visual, int i, int i2, int i3, Pointer pointer, int i4, int i5, int i6, int i7);

    int XPutImage(Display display, Drawable drawable, GC gc, XImage xImage, int i, int i2, int i3, int i4, int i5, int i6);

    int XDestroyImage(XImage xImage);

    XErrorHandler XSetErrorHandler(XErrorHandler xErrorHandler);

    String XKeysymToString(KeySym keySym);

    KeySym XStringToKeysym(String str);

    byte XKeysymToKeycode(Display display, KeySym keySym);

    KeySym XKeycodeToKeysym(Display display, byte b, int i);

    int XGrabKey(Display display, int i, int i2, Window window, int i3, int i4, int i5);

    int XUngrabKey(Display display, int i, int i2, Window window);

    int XGrabKeyboard(Display display, Window window, int i, int i2, int i3, NativeLong nativeLong);

    int XUngrabKeyboard(Display display, NativeLong nativeLong);

    int XFetchName(Display display, Window window, PointerByReference pointerByReference);

    int XChangeKeyboardMapping(Display display, int i, int i2, KeySym[] keySymArr, int i3);

    KeySym XGetKeyboardMapping(Display display, byte b, int i, IntByReference intByReference);

    int XDisplayKeycodes(Display display, IntByReference intByReference, IntByReference intByReference2);

    int XSetModifierMapping(Display display, XModifierKeymapRef xModifierKeymapRef);

    XModifierKeymapRef XGetModifierMapping(Display display);

    XModifierKeymapRef XNewModifiermap(int i);

    XModifierKeymapRef XInsertModifiermapEntry(XModifierKeymapRef xModifierKeymapRef, byte b, int i);

    XModifierKeymapRef XDeleteModifiermapEntry(XModifierKeymapRef xModifierKeymapRef, byte b, int i);

    int XFreeModifiermap(XModifierKeymapRef xModifierKeymapRef);

    int XChangeKeyboardControl(Display display, NativeLong nativeLong, XKeyboardControlRef xKeyboardControlRef);

    int XGetKeyboardControl(Display display, XKeyboardStateRef xKeyboardStateRef);

    int XAutoRepeatOn(Display display);

    int XAutoRepeatOff(Display display);

    int XBell(Display display, int i);

    int XQueryKeymap(Display display, byte[] bArr);

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$VisualID.class */
    public static class VisualID extends NativeLong {
        private static final long serialVersionUID = 1;
        public static final VisualID None = null;

        public VisualID() {
            this(0L);
        }

        public VisualID(long value) {
            super(value, true);
        }

        protected boolean isNone(Object o) {
            return o == null || ((o instanceof Number) && ((Number) o).longValue() == 0);
        }

        @Override // com.sun.jna.IntegerType, com.sun.jna.NativeMapped
        public Object fromNative(Object nativeValue, FromNativeContext context) {
            if (isNone(nativeValue)) {
                return None;
            }
            return new VisualID(((Number) nativeValue).longValue());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XID.class */
    public static class XID extends NativeLong {
        private static final long serialVersionUID = 1;
        public static final XID None = null;

        public XID() {
            this(0L);
        }

        public XID(long id) {
            super(id, true);
        }

        protected boolean isNone(Object o) {
            return o == null || ((o instanceof Number) && ((Number) o).longValue() == 0);
        }

        @Override // com.sun.jna.IntegerType, com.sun.jna.NativeMapped
        public Object fromNative(Object nativeValue, FromNativeContext context) {
            if (isNone(nativeValue)) {
                return None;
            }
            return new XID(((Number) nativeValue).longValue());
        }

        @Override // com.sun.jna.IntegerType
        public String toString() {
            return "0x" + Long.toHexString(longValue());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$Atom.class */
    public static class Atom extends XID {
        private static final long serialVersionUID = 1;
        public static final Atom None = null;

        public Atom() {
        }

        public Atom(long id) {
            super(id);
        }

        @Override // com.sun.jna.platform.unix.X11.XID, com.sun.jna.IntegerType, com.sun.jna.NativeMapped
        public Object fromNative(Object nativeValue, FromNativeContext context) {
            long value = ((Number) nativeValue).longValue();
            if (value <= 2147483647L) {
                switch ((int) value) {
                    case 0:
                        return None;
                    case 1:
                        return X11.XA_PRIMARY;
                    case 2:
                        return X11.XA_SECONDARY;
                    case 3:
                        return X11.XA_ARC;
                    case 4:
                        return X11.XA_ATOM;
                    case 5:
                        return X11.XA_BITMAP;
                    case 6:
                        return X11.XA_CARDINAL;
                    case 7:
                        return X11.XA_COLORMAP;
                    case 8:
                        return X11.XA_CURSOR;
                    case 9:
                        return X11.XA_CUT_BUFFER0;
                    case 10:
                        return X11.XA_CUT_BUFFER1;
                    case 11:
                        return X11.XA_CUT_BUFFER2;
                    case 12:
                        return X11.XA_CUT_BUFFER3;
                    case 13:
                        return X11.XA_CUT_BUFFER4;
                    case 14:
                        return X11.XA_CUT_BUFFER5;
                    case 15:
                        return X11.XA_CUT_BUFFER6;
                    case 16:
                        return X11.XA_CUT_BUFFER7;
                    case 17:
                        return X11.XA_DRAWABLE;
                    case 18:
                        return X11.XA_FONT;
                    case 19:
                        return X11.XA_INTEGER;
                    case 20:
                        return X11.XA_PIXMAP;
                    case 21:
                        return X11.XA_POINT;
                    case 22:
                        return X11.XA_RECTANGLE;
                    case 23:
                        return X11.XA_RESOURCE_MANAGER;
                    case 24:
                        return X11.XA_RGB_COLOR_MAP;
                    case 25:
                        return X11.XA_RGB_BEST_MAP;
                    case 26:
                        return X11.XA_RGB_BLUE_MAP;
                    case 27:
                        return X11.XA_RGB_DEFAULT_MAP;
                    case 28:
                        return X11.XA_RGB_GRAY_MAP;
                    case 29:
                        return X11.XA_RGB_GREEN_MAP;
                    case 30:
                        return X11.XA_RGB_RED_MAP;
                    case 31:
                        return X11.XA_STRING;
                    case 32:
                        return X11.XA_VISUALID;
                    case 33:
                        return X11.XA_WINDOW;
                    case 34:
                        return X11.XA_WM_COMMAND;
                    case 35:
                        return X11.XA_WM_HINTS;
                    case 36:
                        return X11.XA_WM_CLIENT_MACHINE;
                    case 37:
                        return X11.XA_WM_ICON_NAME;
                    case 38:
                        return X11.XA_WM_ICON_SIZE;
                    case 39:
                        return X11.XA_WM_NAME;
                    case 40:
                        return X11.XA_WM_NORMAL_HINTS;
                    case 41:
                        return X11.XA_WM_SIZE_HINTS;
                    case 42:
                        return X11.XA_WM_ZOOM_HINTS;
                    case 43:
                        return X11.XA_MIN_SPACE;
                    case 44:
                        return X11.XA_NORM_SPACE;
                    case 45:
                        return X11.XA_MAX_SPACE;
                    case 46:
                        return X11.XA_END_SPACE;
                    case 47:
                        return X11.XA_SUPERSCRIPT_X;
                    case 48:
                        return X11.XA_SUPERSCRIPT_Y;
                    case 49:
                        return X11.XA_SUBSCRIPT_X;
                    case 50:
                        return X11.XA_SUBSCRIPT_Y;
                    case 51:
                        return X11.XA_UNDERLINE_POSITION;
                    case 52:
                        return X11.XA_UNDERLINE_THICKNESS;
                    case 53:
                        return X11.XA_STRIKEOUT_ASCENT;
                    case 54:
                        return X11.XA_STRIKEOUT_DESCENT;
                    case 55:
                        return X11.XA_ITALIC_ANGLE;
                    case 56:
                        return X11.XA_X_HEIGHT;
                    case 57:
                        return X11.XA_QUAD_WIDTH;
                    case 58:
                        return X11.XA_WEIGHT;
                    case 59:
                        return X11.XA_POINT_SIZE;
                    case 60:
                        return X11.XA_RESOLUTION;
                    case 61:
                        return X11.XA_COPYRIGHT;
                    case 62:
                        return X11.XA_NOTICE;
                    case 63:
                        return X11.XA_FONT_NAME;
                    case 64:
                        return X11.XA_FAMILY_NAME;
                    case 65:
                        return X11.XA_FULL_NAME;
                    case 66:
                        return X11.XA_CAP_HEIGHT;
                    case 67:
                        return X11.XA_WM_CLASS;
                    case 68:
                        return X11.XA_WM_TRANSIENT_FOR;
                }
            }
            return new Atom(value);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$AtomByReference.class */
    public static class AtomByReference extends ByReference {
        public AtomByReference() {
            super(XID.SIZE);
        }

        public Atom getValue() {
            NativeLong value = getPointer().getNativeLong(0L);
            return (Atom) new Atom().fromNative(value, null);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$Colormap.class */
    public static class Colormap extends XID {
        private static final long serialVersionUID = 1;
        public static final Colormap None = null;

        public Colormap() {
        }

        public Colormap(long id) {
            super(id);
        }

        @Override // com.sun.jna.platform.unix.X11.XID, com.sun.jna.IntegerType, com.sun.jna.NativeMapped
        public Object fromNative(Object nativeValue, FromNativeContext context) {
            if (isNone(nativeValue)) {
                return None;
            }
            return new Colormap(((Number) nativeValue).longValue());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$Font.class */
    public static class Font extends XID {
        private static final long serialVersionUID = 1;
        public static final Font None = null;

        public Font() {
        }

        public Font(long id) {
            super(id);
        }

        @Override // com.sun.jna.platform.unix.X11.XID, com.sun.jna.IntegerType, com.sun.jna.NativeMapped
        public Object fromNative(Object nativeValue, FromNativeContext context) {
            if (isNone(nativeValue)) {
                return None;
            }
            return new Font(((Number) nativeValue).longValue());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$Cursor.class */
    public static class Cursor extends XID {
        private static final long serialVersionUID = 1;
        public static final Cursor None = null;

        public Cursor() {
        }

        public Cursor(long id) {
            super(id);
        }

        @Override // com.sun.jna.platform.unix.X11.XID, com.sun.jna.IntegerType, com.sun.jna.NativeMapped
        public Object fromNative(Object nativeValue, FromNativeContext context) {
            if (isNone(nativeValue)) {
                return None;
            }
            return new Cursor(((Number) nativeValue).longValue());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$KeySym.class */
    public static class KeySym extends XID {
        private static final long serialVersionUID = 1;
        public static final KeySym None = null;

        public KeySym() {
        }

        public KeySym(long id) {
            super(id);
        }

        @Override // com.sun.jna.platform.unix.X11.XID, com.sun.jna.IntegerType, com.sun.jna.NativeMapped
        public Object fromNative(Object nativeValue, FromNativeContext context) {
            if (isNone(nativeValue)) {
                return None;
            }
            return new KeySym(((Number) nativeValue).longValue());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$Drawable.class */
    public static class Drawable extends XID {
        private static final long serialVersionUID = 1;
        public static final Drawable None = null;

        public Drawable() {
        }

        public Drawable(long id) {
            super(id);
        }

        @Override // com.sun.jna.platform.unix.X11.XID, com.sun.jna.IntegerType, com.sun.jna.NativeMapped
        public Object fromNative(Object nativeValue, FromNativeContext context) {
            if (isNone(nativeValue)) {
                return None;
            }
            return new Drawable(((Number) nativeValue).longValue());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$Window.class */
    public static class Window extends Drawable {
        private static final long serialVersionUID = 1;
        public static final Window None = null;

        public Window() {
        }

        public Window(long id) {
            super(id);
        }

        @Override // com.sun.jna.platform.unix.X11.Drawable, com.sun.jna.platform.unix.X11.XID, com.sun.jna.IntegerType, com.sun.jna.NativeMapped
        public Object fromNative(Object nativeValue, FromNativeContext context) {
            if (isNone(nativeValue)) {
                return None;
            }
            return new Window(((Number) nativeValue).longValue());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$WindowByReference.class */
    public static class WindowByReference extends ByReference {
        public WindowByReference() {
            super(XID.SIZE);
        }

        public Window getValue() {
            NativeLong value = getPointer().getNativeLong(0L);
            return value.longValue() == 0 ? Window.None : new Window(value.longValue());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$Pixmap.class */
    public static class Pixmap extends Drawable {
        private static final long serialVersionUID = 1;
        public static final Pixmap None = null;

        public Pixmap() {
        }

        public Pixmap(long id) {
            super(id);
        }

        @Override // com.sun.jna.platform.unix.X11.Drawable, com.sun.jna.platform.unix.X11.XID, com.sun.jna.IntegerType, com.sun.jna.NativeMapped
        public Object fromNative(Object nativeValue, FromNativeContext context) {
            if (isNone(nativeValue)) {
                return None;
            }
            return new Pixmap(((Number) nativeValue).longValue());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$Visual.class */
    public static class Visual extends PointerType {
        public VisualID getVisualID() {
            if (getPointer() != null) {
                return new VisualID(getPointer().getNativeLong(Native.POINTER_SIZE).longValue());
            }
            throw new IllegalStateException("Attempting to retrieve VisualID from a null Visual");
        }

        @Override // com.sun.jna.PointerType
        public String toString() {
            return "Visual: VisualID=0x" + Long.toHexString(getVisualID().longValue());
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$Xrender.class */
    public interface Xrender extends Library {
        public static final Xrender INSTANCE = (Xrender) Native.load("Xrender", Xrender.class);
        public static final int PictTypeIndexed = 0;
        public static final int PictTypeDirect = 1;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$Xrender$XRenderDirectFormat.class */
        @Structure.FieldOrder({ElementTags.RED, "redMask", ElementTags.GREEN, "greenMask", ElementTags.BLUE, "blueMask", "alpha", "alphaMask"})
        public static class XRenderDirectFormat extends Structure {
            public short red;
            public short redMask;
            public short green;
            public short greenMask;
            public short blue;
            public short blueMask;
            public short alpha;
            public short alphaMask;
        }

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$Xrender$XRenderPictFormat.class */
        @Structure.FieldOrder({ElementTags.ID, "type", "depth", "direct", "colormap"})
        public static class XRenderPictFormat extends Structure {
            public PictFormat id;
            public int type;
            public int depth;
            public XRenderDirectFormat direct;
            public Colormap colormap;
        }

        XRenderPictFormat XRenderFindVisualFormat(Display display, Visual visual);

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$Xrender$PictFormat.class */
        public static class PictFormat extends XID {
            private static final long serialVersionUID = 1;
            public static final PictFormat None = null;

            public PictFormat(long value) {
                super(value);
            }

            public PictFormat() {
                this(0L);
            }

            @Override // com.sun.jna.platform.unix.X11.XID, com.sun.jna.IntegerType, com.sun.jna.NativeMapped
            public Object fromNative(Object nativeValue, FromNativeContext context) {
                if (isNone(nativeValue)) {
                    return None;
                }
                return new PictFormat(((Number) nativeValue).longValue());
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XPoint.class */
    @Structure.FieldOrder({"x", OperatorName.CURVE_TO_REPLICATE_FINAL_POINT})
    public static class XPoint extends Structure {
        public short x;
        public short y;

        public XPoint() {
            this((short) 0, (short) 0);
        }

        public XPoint(short x, short y) {
            this.x = x;
            this.y = y;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XRectangle.class */
    @Structure.FieldOrder({"x", OperatorName.CURVE_TO_REPLICATE_FINAL_POINT, ElementTags.WIDTH, "height"})
    public static class XRectangle extends Structure {
        public short x;
        public short y;
        public short width;
        public short height;

        public XRectangle() {
            this((short) 0, (short) 0, (short) 0, (short) 0);
        }

        public XRectangle(short x, short y, short width, short height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XKeyboardControlRef.class */
    @Structure.FieldOrder({"key_click_percent", "bell_percent", "bell_pitch", "bell_duration", "led", "led_mode", "key", "auto_repeat_mode"})
    public static class XKeyboardControlRef extends Structure implements Structure.ByReference {
        public int key_click_percent;
        public int bell_percent;
        public int bell_pitch;
        public int bell_duration;
        public int led;
        public int led_mode;
        public int key;
        public int auto_repeat_mode;

        @Override // com.sun.jna.Structure
        public String toString() {
            return "XKeyboardControlByReference{key_click_percent=" + this.key_click_percent + ", bell_percent=" + this.bell_percent + ", bell_pitch=" + this.bell_pitch + ", bell_duration=" + this.bell_duration + ", led=" + this.led + ", led_mode=" + this.led_mode + ", key=" + this.key + ", auto_repeat_mode=" + this.auto_repeat_mode + '}';
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/unix/X11$XKeyboardStateRef.class */
    @Structure.FieldOrder({"key_click_percent", "bell_percent", "bell_pitch", "bell_duration", "led_mask", "global_auto_repeat", "auto_repeats"})
    public static class XKeyboardStateRef extends Structure implements Structure.ByReference {
        public int key_click_percent;
        public int bell_percent;
        public int bell_pitch;
        public int bell_duration;
        public NativeLong led_mask;
        public int global_auto_repeat;
        public byte[] auto_repeats = new byte[32];

        @Override // com.sun.jna.Structure
        public String toString() {
            return "XKeyboardStateByReference{key_click_percent=" + this.key_click_percent + ", bell_percent=" + this.bell_percent + ", bell_pitch=" + this.bell_pitch + ", bell_duration=" + this.bell_duration + ", led_mask=" + this.led_mask + ", global_auto_repeat=" + this.global_auto_repeat + ", auto_repeats=" + this.auto_repeats + '}';
        }
    }
}
