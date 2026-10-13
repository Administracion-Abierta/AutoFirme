package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.win32.W32APITypeMapper;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Ntifs.class */
public interface Ntifs extends WinDef, BaseTSD {
    public static final int MAXIMUM_REPARSE_DATA_BUFFER_SIZE = 16384;
    public static final int REPARSE_BUFFER_HEADER_SIZE = 8;
    public static final int SYMLINK_FLAG_RELATIVE = 1;

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Ntifs$SymbolicLinkReparseBuffer.class */
    @Structure.FieldOrder({"SubstituteNameOffset", "SubstituteNameLength", "PrintNameOffset", "PrintNameLength", "Flags", "PathBuffer"})
    public static class SymbolicLinkReparseBuffer extends Structure {
        public short SubstituteNameOffset;
        public short SubstituteNameLength;
        public short PrintNameOffset;
        public short PrintNameLength;
        public int Flags;
        public char[] PathBuffer;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Ntifs$SymbolicLinkReparseBuffer$ByReference.class */
        public static class ByReference extends SymbolicLinkReparseBuffer implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        public static int sizeOf() {
            return Native.getNativeSize(MountPointReparseBuffer.class, null);
        }

        public SymbolicLinkReparseBuffer() {
            super(W32APITypeMapper.UNICODE);
            this.SubstituteNameOffset = (short) 0;
            this.SubstituteNameLength = (short) 0;
            this.PrintNameOffset = (short) 0;
            this.PrintNameLength = (short) 0;
            this.Flags = 0;
            this.PathBuffer = new char[8192];
        }

        public SymbolicLinkReparseBuffer(Pointer memory) {
            super(memory, 0, W32APITypeMapper.UNICODE);
            this.SubstituteNameOffset = (short) 0;
            this.SubstituteNameLength = (short) 0;
            this.PrintNameOffset = (short) 0;
            this.PrintNameLength = (short) 0;
            this.Flags = 0;
            this.PathBuffer = new char[8192];
            read();
        }

        public SymbolicLinkReparseBuffer(String substituteName, String printName, int Flags) {
            this.SubstituteNameOffset = (short) 0;
            this.SubstituteNameLength = (short) 0;
            this.PrintNameOffset = (short) 0;
            this.PrintNameLength = (short) 0;
            this.Flags = 0;
            this.PathBuffer = new char[8192];
            String bothNames = substituteName + printName;
            this.PathBuffer = bothNames.toCharArray();
            this.SubstituteNameOffset = (short) 0;
            this.SubstituteNameLength = (short) (substituteName.length() * 2);
            this.PrintNameOffset = (short) (substituteName.length() * 2);
            this.PrintNameLength = (short) (printName.length() * 2);
            this.Flags = Flags;
            write();
        }

        public SymbolicLinkReparseBuffer(short SubstituteNameOffset, short SubstituteNameLength, short PrintNameOffset, short PrintNameLength, int Flags, String PathBuffer) {
            this.SubstituteNameOffset = (short) 0;
            this.SubstituteNameLength = (short) 0;
            this.PrintNameOffset = (short) 0;
            this.PrintNameLength = (short) 0;
            this.Flags = 0;
            this.PathBuffer = new char[8192];
            this.SubstituteNameOffset = SubstituteNameOffset;
            this.SubstituteNameLength = SubstituteNameLength;
            this.PrintNameOffset = PrintNameOffset;
            this.PrintNameLength = PrintNameLength;
            this.Flags = Flags;
            this.PathBuffer = PathBuffer.toCharArray();
            write();
        }

        public String getPrintName() {
            return String.copyValueOf(this.PathBuffer, this.PrintNameOffset / 2, this.PrintNameLength / 2);
        }

        public String getSubstituteName() {
            return String.copyValueOf(this.PathBuffer, this.SubstituteNameOffset / 2, this.SubstituteNameLength / 2);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Ntifs$MountPointReparseBuffer.class */
    @Structure.FieldOrder({"SubstituteNameOffset", "SubstituteNameLength", "PrintNameOffset", "PrintNameLength", "PathBuffer"})
    public static class MountPointReparseBuffer extends Structure {
        public short SubstituteNameOffset;
        public short SubstituteNameLength;
        public short PrintNameOffset;
        public short PrintNameLength;
        public char[] PathBuffer;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Ntifs$MountPointReparseBuffer$ByReference.class */
        public static class ByReference extends MountPointReparseBuffer implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        public static int sizeOf() {
            return Native.getNativeSize(MountPointReparseBuffer.class, null);
        }

        public MountPointReparseBuffer() {
            super(W32APITypeMapper.UNICODE);
            this.SubstituteNameOffset = (short) 0;
            this.SubstituteNameLength = (short) 0;
            this.PrintNameOffset = (short) 0;
            this.PrintNameLength = (short) 0;
            this.PathBuffer = new char[8192];
        }

        public MountPointReparseBuffer(Pointer memory) {
            super(memory, 0, W32APITypeMapper.UNICODE);
            this.SubstituteNameOffset = (short) 0;
            this.SubstituteNameLength = (short) 0;
            this.PrintNameOffset = (short) 0;
            this.PrintNameLength = (short) 0;
            this.PathBuffer = new char[8192];
            read();
        }

        public MountPointReparseBuffer(String substituteName, String printName) {
            this.SubstituteNameOffset = (short) 0;
            this.SubstituteNameLength = (short) 0;
            this.PrintNameOffset = (short) 0;
            this.PrintNameLength = (short) 0;
            this.PathBuffer = new char[8192];
            String bothNames = substituteName + printName;
            this.PathBuffer = bothNames.toCharArray();
            this.SubstituteNameOffset = (short) 0;
            this.SubstituteNameLength = (short) substituteName.length();
            this.PrintNameOffset = (short) (substituteName.length() * 2);
            this.PrintNameLength = (short) (printName.length() * 2);
            write();
        }

        public MountPointReparseBuffer(short SubstituteNameOffset, short SubstituteNameLength, short PrintNameOffset, short PrintNameLength, String PathBuffer) {
            this.SubstituteNameOffset = (short) 0;
            this.SubstituteNameLength = (short) 0;
            this.PrintNameOffset = (short) 0;
            this.PrintNameLength = (short) 0;
            this.PathBuffer = new char[8192];
            this.SubstituteNameOffset = SubstituteNameOffset;
            this.SubstituteNameLength = SubstituteNameLength;
            this.PrintNameOffset = PrintNameOffset;
            this.PrintNameLength = PrintNameLength;
            this.PathBuffer = PathBuffer.toCharArray();
            write();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Ntifs$GenericReparseBuffer.class */
    @Structure.FieldOrder({"DataBuffer"})
    public static class GenericReparseBuffer extends Structure {
        public byte[] DataBuffer;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Ntifs$GenericReparseBuffer$ByReference.class */
        public static class ByReference extends GenericReparseBuffer implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        public static int sizeOf() {
            return Native.getNativeSize(GenericReparseBuffer.class, null);
        }

        public GenericReparseBuffer() {
            this.DataBuffer = new byte[16384];
        }

        public GenericReparseBuffer(Pointer memory) {
            super(memory);
            this.DataBuffer = new byte[16384];
            read();
        }

        public GenericReparseBuffer(String DataBuffer) {
            this.DataBuffer = new byte[16384];
            this.DataBuffer = DataBuffer.getBytes();
            write();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Ntifs$REPARSE_DATA_BUFFER.class */
    @Structure.FieldOrder({"ReparseTag", "ReparseDataLength", "Reserved", "u"})
    public static class REPARSE_DATA_BUFFER extends Structure {
        public int ReparseTag;
        public short ReparseDataLength;
        public short Reserved;
        public REPARSE_UNION u;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Ntifs$REPARSE_DATA_BUFFER$ByReference.class */
        public static class ByReference extends REPARSE_DATA_BUFFER implements Structure.ByReference {
            public ByReference() {
            }

            public ByReference(Pointer memory) {
                super(memory);
            }
        }

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Ntifs$REPARSE_DATA_BUFFER$REPARSE_UNION.class */
        public static class REPARSE_UNION extends Union {
            public SymbolicLinkReparseBuffer symLinkReparseBuffer;
            public MountPointReparseBuffer mountPointReparseBuffer;
            public GenericReparseBuffer genericReparseBuffer;

            /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Ntifs$REPARSE_DATA_BUFFER$REPARSE_UNION$ByReference.class */
            public static class ByReference extends REPARSE_UNION implements Structure.ByReference {
            }

            public REPARSE_UNION() {
            }

            public REPARSE_UNION(Pointer memory) {
                super(memory);
            }
        }

        public static int sizeOf() {
            return Native.getNativeSize(REPARSE_DATA_BUFFER.class, null);
        }

        public int getSize() {
            return 8 + this.ReparseDataLength;
        }

        public REPARSE_DATA_BUFFER() {
            this.ReparseTag = 0;
            this.ReparseDataLength = (short) 0;
            this.Reserved = (short) 0;
        }

        public REPARSE_DATA_BUFFER(int ReparseTag, short Reserved) {
            this.ReparseTag = 0;
            this.ReparseDataLength = (short) 0;
            this.Reserved = (short) 0;
            this.ReparseTag = ReparseTag;
            this.Reserved = Reserved;
            this.ReparseDataLength = (short) 0;
            write();
        }

        public REPARSE_DATA_BUFFER(int ReparseTag, short Reserved, SymbolicLinkReparseBuffer symLinkReparseBuffer) {
            this.ReparseTag = 0;
            this.ReparseDataLength = (short) 0;
            this.Reserved = (short) 0;
            this.ReparseTag = ReparseTag;
            this.Reserved = Reserved;
            this.ReparseDataLength = (short) symLinkReparseBuffer.size();
            this.u.setType(SymbolicLinkReparseBuffer.class);
            this.u.symLinkReparseBuffer = symLinkReparseBuffer;
            write();
        }

        public REPARSE_DATA_BUFFER(Pointer memory) {
            super(memory);
            this.ReparseTag = 0;
            this.ReparseDataLength = (short) 0;
            this.Reserved = (short) 0;
            read();
        }

        @Override // com.sun.jna.Structure
        public void read() {
            super.read();
            switch (this.ReparseTag) {
                case WinNT.IO_REPARSE_TAG_MOUNT_POINT /* -1610612733 */:
                    this.u.setType(MountPointReparseBuffer.class);
                    break;
                case WinNT.IO_REPARSE_TAG_SYMLINK /* -1610612724 */:
                    this.u.setType(SymbolicLinkReparseBuffer.class);
                    break;
                default:
                    this.u.setType(GenericReparseBuffer.class);
                    break;
            }
            this.u.read();
        }
    }
}
