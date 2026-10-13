package com.sun.jna.platform.win32;

import com.aowagie.text.ElementTags;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.ptr.IntByReference;
import org.apache.xml.security.utils.Constants;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Wincon.class */
public interface Wincon {
    public static final int ATTACH_PARENT_PROCESS = -1;
    public static final int CTRL_C_EVENT = 0;
    public static final int CTRL_BREAK_EVENT = 1;
    public static final int STD_INPUT_HANDLE = -10;
    public static final int STD_OUTPUT_HANDLE = -11;
    public static final int STD_ERROR_HANDLE = -12;
    public static final int CONSOLE_FULLSCREEN = 1;
    public static final int CONSOLE_FULLSCREEN_HARDWARE = 2;
    public static final int ENABLE_PROCESSED_INPUT = 1;
    public static final int ENABLE_LINE_INPUT = 2;
    public static final int ENABLE_ECHO_INPUT = 4;
    public static final int ENABLE_WINDOW_INPUT = 8;
    public static final int ENABLE_MOUSE_INPUT = 16;
    public static final int ENABLE_INSERT_MODE = 32;
    public static final int ENABLE_QUICK_EDIT_MODE = 64;
    public static final int ENABLE_EXTENDED_FLAGS = 128;
    public static final int ENABLE_VIRTUAL_TERMINAL_PROCESSING = 4;
    public static final int DISABLE_NEWLINE_AUTO_RETURN = 8;
    public static final int ENABLE_VIRTUAL_TERMINAL_INPUT = 512;
    public static final int ENABLE_PROCESSED_OUTPUT = 1;
    public static final int ENABLE_WRAP_AT_EOL_OUTPUT = 2;
    public static final int MAX_CONSOLE_TITLE_LENGTH = 65536;

    boolean AllocConsole();

    boolean FreeConsole();

    boolean AttachConsole(int i);

    boolean FlushConsoleInputBuffer(WinNT.HANDLE handle);

    boolean GenerateConsoleCtrlEvent(int i, int i2);

    int GetConsoleCP();

    boolean SetConsoleCP(int i);

    int GetConsoleOutputCP();

    boolean SetConsoleOutputCP(int i);

    WinDef.HWND GetConsoleWindow();

    boolean GetNumberOfConsoleInputEvents(WinNT.HANDLE handle, IntByReference intByReference);

    boolean GetNumberOfConsoleMouseButtons(IntByReference intByReference);

    WinNT.HANDLE GetStdHandle(int i);

    boolean SetStdHandle(int i, WinNT.HANDLE handle);

    boolean GetConsoleDisplayMode(IntByReference intByReference);

    boolean GetConsoleMode(WinNT.HANDLE handle, IntByReference intByReference);

    boolean SetConsoleMode(WinNT.HANDLE handle, int i);

    int GetConsoleTitle(char[] cArr, int i);

    int GetConsoleOriginalTitle(char[] cArr, int i);

    boolean SetConsoleTitle(String str);

    boolean GetConsoleScreenBufferInfo(WinNT.HANDLE handle, CONSOLE_SCREEN_BUFFER_INFO console_screen_buffer_info);

    boolean ReadConsoleInput(WinNT.HANDLE handle, INPUT_RECORD[] input_recordArr, int i, IntByReference intByReference);

    boolean WriteConsole(WinNT.HANDLE handle, String str, int i, IntByReference intByReference, WinDef.LPVOID lpvoid);

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Wincon$COORD.class */
    @Structure.FieldOrder({"X", Constants._TAG_Y})
    public static class COORD extends Structure {
        public short X;
        public short Y;

        @Override // com.sun.jna.Structure
        public String toString() {
            return String.format("COORD(%s,%s)", Short.valueOf(this.X), Short.valueOf(this.Y));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Wincon$SMALL_RECT.class */
    @Structure.FieldOrder({ElementTags.ALIGN_LEFT, "Top", ElementTags.ALIGN_RIGHT, "Bottom"})
    public static class SMALL_RECT extends Structure {
        public short Left;
        public short Top;
        public short Right;
        public short Bottom;

        @Override // com.sun.jna.Structure
        public String toString() {
            return String.format("SMALL_RECT(%s,%s)(%s,%s)", Short.valueOf(this.Left), Short.valueOf(this.Top), Short.valueOf(this.Right), Short.valueOf(this.Bottom));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Wincon$CONSOLE_SCREEN_BUFFER_INFO.class */
    @Structure.FieldOrder({"dwSize", "dwCursorPosition", "wAttributes", "srWindow", "dwMaximumWindowSize"})
    public static class CONSOLE_SCREEN_BUFFER_INFO extends Structure {
        public COORD dwSize;
        public COORD dwCursorPosition;
        public short wAttributes;
        public SMALL_RECT srWindow;
        public COORD dwMaximumWindowSize;

        @Override // com.sun.jna.Structure
        public String toString() {
            return String.format("CONSOLE_SCREEN_BUFFER_INFO(%s,%s,%s,%s,%s)", this.dwSize, this.dwCursorPosition, Short.valueOf(this.wAttributes), this.srWindow, this.dwMaximumWindowSize);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Wincon$INPUT_RECORD.class */
    @Structure.FieldOrder({"EventType", "Event"})
    public static class INPUT_RECORD extends Structure {
        public static final short KEY_EVENT = 1;
        public static final short MOUSE_EVENT = 2;
        public static final short WINDOW_BUFFER_SIZE_EVENT = 4;
        public short EventType;
        public Event Event;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Wincon$INPUT_RECORD$Event.class */
        public static class Event extends Union {
            public KEY_EVENT_RECORD KeyEvent;
            public MOUSE_EVENT_RECORD MouseEvent;
            public WINDOW_BUFFER_SIZE_RECORD WindowBufferSizeEvent;
        }

        @Override // com.sun.jna.Structure
        public void read() {
            super.read();
            switch (this.EventType) {
                case 1:
                    this.Event.setType("KeyEvent");
                    break;
                case 2:
                    this.Event.setType("MouseEvent");
                    break;
                case 4:
                    this.Event.setType("WindowBufferSizeEvent");
                    break;
            }
            this.Event.read();
        }

        @Override // com.sun.jna.Structure
        public String toString() {
            return String.format("INPUT_RECORD(%s)", Short.valueOf(this.EventType));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Wincon$KEY_EVENT_RECORD.class */
    @Structure.FieldOrder({"bKeyDown", "wRepeatCount", "wVirtualKeyCode", "wVirtualScanCode", "uChar", "dwControlKeyState"})
    public static class KEY_EVENT_RECORD extends Structure {
        public boolean bKeyDown;
        public short wRepeatCount;
        public short wVirtualKeyCode;
        public short wVirtualScanCode;
        public char uChar;
        public int dwControlKeyState;

        @Override // com.sun.jna.Structure
        public String toString() {
            return String.format("KEY_EVENT_RECORD(%s,%s,%s,%s,%s,%s)", Boolean.valueOf(this.bKeyDown), Short.valueOf(this.wRepeatCount), Short.valueOf(this.wVirtualKeyCode), Short.valueOf(this.wVirtualKeyCode), Short.valueOf(this.wVirtualScanCode), Character.valueOf(this.uChar), Integer.valueOf(this.dwControlKeyState));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Wincon$MOUSE_EVENT_RECORD.class */
    @Structure.FieldOrder({"dwMousePosition", "dwButtonState", "dwControlKeyState", "dwEventFlags"})
    public static class MOUSE_EVENT_RECORD extends Structure {
        public COORD dwMousePosition;
        public int dwButtonState;
        public int dwControlKeyState;
        public int dwEventFlags;

        @Override // com.sun.jna.Structure
        public String toString() {
            return String.format("MOUSE_EVENT_RECORD(%s,%s,%s,%s)", this.dwMousePosition, Integer.valueOf(this.dwButtonState), Integer.valueOf(this.dwControlKeyState), Integer.valueOf(this.dwEventFlags));
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Wincon$WINDOW_BUFFER_SIZE_RECORD.class */
    @Structure.FieldOrder({"dwSize"})
    public static class WINDOW_BUFFER_SIZE_RECORD extends Structure {
        public COORD dwSize;

        @Override // com.sun.jna.Structure
        public String toString() {
            return String.format("WINDOW_BUFFER_SIZE_RECORD(%s)", this.dwSize);
        }
    }
}
