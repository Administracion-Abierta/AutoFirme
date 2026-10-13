package com.sun.jna.platform.win32;

import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinGDI.class */
public interface WinGDI {
    public static final int RDH_RECTANGLES = 1;
    public static final WinNT.HANDLE HGDI_ERROR = new WinNT.HANDLE(Pointer.createConstant(-1));
    public static final int RGN_AND = 1;
    public static final int RGN_OR = 2;
    public static final int RGN_XOR = 3;
    public static final int RGN_DIFF = 4;
    public static final int RGN_COPY = 5;
    public static final int ERROR = 0;
    public static final int NULLREGION = 1;
    public static final int SIMPLEREGION = 2;
    public static final int COMPLEXREGION = 3;
    public static final int ALTERNATE = 1;
    public static final int WINDING = 2;
    public static final int BI_RGB = 0;
    public static final int BI_RLE8 = 1;
    public static final int BI_RLE4 = 2;
    public static final int BI_BITFIELDS = 3;
    public static final int BI_JPEG = 4;
    public static final int BI_PNG = 5;
    public static final int PFD_TYPE_RGBA = 0;
    public static final int PFD_TYPE_COLORINDEX = 1;
    public static final int PFD_MAIN_PLANE = 0;
    public static final int PFD_OVERLAY_PLANE = 1;
    public static final int PFD_UNDERLAY_PLANE = -1;
    public static final int PFD_DOUBLEBUFFER = 1;
    public static final int PFD_STEREO = 2;
    public static final int PFD_DRAW_TO_WINDOW = 4;
    public static final int PFD_DRAW_TO_BITMAP = 8;
    public static final int PFD_SUPPORT_GDI = 16;
    public static final int PFD_SUPPORT_OPENGL = 32;
    public static final int PFD_GENERIC_FORMAT = 64;
    public static final int PFD_NEED_PALETTE = 128;
    public static final int PFD_NEED_SYSTEM_PALETTE = 256;
    public static final int PFD_SWAP_EXCHANGE = 512;
    public static final int PFD_SWAP_COPY = 1024;
    public static final int PFD_SWAP_LAYER_BUFFERS = 2048;
    public static final int PFD_GENERIC_ACCELERATED = 4096;
    public static final int PFD_SUPPORT_DIRECTDRAW = 8192;
    public static final int DIB_RGB_COLORS = 0;
    public static final int DIB_PAL_COLORS = 1;

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinGDI$BITMAP.class */
    @Structure.FieldOrder({"bmType", "bmWidth", "bmHeight", "bmWidthBytes", "bmPlanes", "bmBitsPixel", "bmBits"})
    public static class BITMAP extends Structure {
        public NativeLong bmType;
        public NativeLong bmWidth;
        public NativeLong bmHeight;
        public NativeLong bmWidthBytes;
        public short bmPlanes;
        public short bmBitsPixel;
        public Pointer bmBits;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinGDI$BITMAPINFOHEADER.class */
    @Structure.FieldOrder({"biSize", "biWidth", "biHeight", "biPlanes", "biBitCount", "biCompression", "biSizeImage", "biXPelsPerMeter", "biYPelsPerMeter", "biClrUsed", "biClrImportant"})
    public static class BITMAPINFOHEADER extends Structure {
        public int biSize = size();
        public int biWidth;
        public int biHeight;
        public short biPlanes;
        public short biBitCount;
        public int biCompression;
        public int biSizeImage;
        public int biXPelsPerMeter;
        public int biYPelsPerMeter;
        public int biClrUsed;
        public int biClrImportant;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinGDI$DIBSECTION.class */
    @Structure.FieldOrder({"dsBm", "dsBmih", "dsBitfields", "dshSection", "dsOffset"})
    public static class DIBSECTION extends Structure {
        public BITMAP dsBm;
        public BITMAPINFOHEADER dsBmih;
        public int[] dsBitfields = new int[3];
        public WinNT.HANDLE dshSection;
        public int dsOffset;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinGDI$ICONINFO.class */
    @Structure.FieldOrder({"fIcon", "xHotspot", "yHotspot", "hbmMask", "hbmColor"})
    public static class ICONINFO extends Structure {
        public boolean fIcon;
        public int xHotspot;
        public int yHotspot;
        public WinDef.HBITMAP hbmMask;
        public WinDef.HBITMAP hbmColor;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinGDI$RGBQUAD.class */
    @Structure.FieldOrder({"rgbBlue", "rgbGreen", "rgbRed", "rgbReserved"})
    public static class RGBQUAD extends Structure {
        public byte rgbBlue;
        public byte rgbGreen;
        public byte rgbRed;
        public byte rgbReserved = 0;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinGDI$RGNDATAHEADER.class */
    @Structure.FieldOrder({"dwSize", "iType", "nCount", "nRgnSize", "rcBound"})
    public static class RGNDATAHEADER extends Structure {
        public int dwSize = size();
        public int iType = 1;
        public int nCount;
        public int nRgnSize;
        public WinDef.RECT rcBound;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinGDI$RGNDATA.class */
    @Structure.FieldOrder({"rdh", "Buffer"})
    public static class RGNDATA extends Structure {
        public RGNDATAHEADER rdh;
        public byte[] Buffer;

        public RGNDATA() {
            this(1);
        }

        public RGNDATA(int bufferSize) {
            this.Buffer = new byte[bufferSize];
            allocateMemory();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinGDI$BITMAPINFO.class */
    @Structure.FieldOrder({"bmiHeader", "bmiColors"})
    public static class BITMAPINFO extends Structure {
        public BITMAPINFOHEADER bmiHeader;
        public RGBQUAD[] bmiColors;

        public BITMAPINFO() {
            this(1);
        }

        public BITMAPINFO(int size) {
            this.bmiHeader = new BITMAPINFOHEADER();
            this.bmiColors = new RGBQUAD[1];
            this.bmiColors = new RGBQUAD[size];
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinGDI$PIXELFORMATDESCRIPTOR.class */
    @Structure.FieldOrder({"nSize", "nVersion", "dwFlags", "iPixelType", "cColorBits", "cRedBits", "cRedShift", "cGreenBits", "cGreenShift", "cBlueBits", "cBlueShift", "cAlphaBits", "cAlphaShift", "cAccumBits", "cAccumRedBits", "cAccumGreenBits", "cAccumBlueBits", "cAccumAlphaBits", "cDepthBits", "cStencilBits", "cAuxBuffers", "iLayerType", "bReserved", "dwLayerMask", "dwVisibleMask", "dwDamageMask"})
    public static class PIXELFORMATDESCRIPTOR extends Structure {
        public short nSize;
        public short nVersion;
        public int dwFlags;
        public byte iPixelType;
        public byte cColorBits;
        public byte cRedBits;
        public byte cRedShift;
        public byte cGreenBits;
        public byte cGreenShift;
        public byte cBlueBits;
        public byte cBlueShift;
        public byte cAlphaBits;
        public byte cAlphaShift;
        public byte cAccumBits;
        public byte cAccumRedBits;
        public byte cAccumGreenBits;
        public byte cAccumBlueBits;
        public byte cAccumAlphaBits;
        public byte cDepthBits;
        public byte cStencilBits;
        public byte cAuxBuffers;
        public byte iLayerType;
        public byte bReserved;
        public int dwLayerMask;
        public int dwVisibleMask;
        public int dwDamageMask;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/WinGDI$PIXELFORMATDESCRIPTOR$ByReference.class */
        public static class ByReference extends PIXELFORMATDESCRIPTOR implements Structure.ByReference {
        }

        public PIXELFORMATDESCRIPTOR() {
            this.nSize = (short) size();
        }

        public PIXELFORMATDESCRIPTOR(Pointer memory) {
            super(memory);
            read();
        }
    }
}
