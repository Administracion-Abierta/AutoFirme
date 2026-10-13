package com.sun.jna.platform.win32;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.win32.W32APITypeMapper;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/LMAccess.class */
public interface LMAccess {
    public static final int FILTER_TEMP_DUPLICATE_ACCOUNT = 1;
    public static final int FILTER_NORMAL_ACCOUNT = 2;
    public static final int FILTER_INTERDOMAIN_TRUST_ACCOUNT = 8;
    public static final int FILTER_WORKSTATION_TRUST_ACCOUNT = 16;
    public static final int FILTER_SERVER_TRUST_ACCOUNT = 32;
    public static final int USER_PRIV_MASK = 3;
    public static final int USER_PRIV_GUEST = 0;
    public static final int USER_PRIV_USER = 1;
    public static final int USER_PRIV_ADMIN = 2;
    public static final int ACCESS_NONE = 0;
    public static final int ACCESS_READ = 1;
    public static final int ACCESS_WRITE = 2;
    public static final int ACCESS_CREATE = 4;
    public static final int ACCESS_EXEC = 8;
    public static final int ACCESS_DELETE = 16;
    public static final int ACCESS_ATRIB = 32;
    public static final int ACCESS_PERM = 64;
    public static final int ACCESS_ALL = 127;
    public static final int ACCESS_GROUP = 32768;

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/LMAccess$LOCALGROUP_INFO_0.class */
    @Structure.FieldOrder({"lgrui0_name"})
    public static class LOCALGROUP_INFO_0 extends Structure {
        public String lgrui0_name;

        public LOCALGROUP_INFO_0() {
            super(W32APITypeMapper.UNICODE);
        }

        public LOCALGROUP_INFO_0(Pointer memory) {
            super(memory, 0, W32APITypeMapper.UNICODE);
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/LMAccess$LOCALGROUP_INFO_1.class */
    @Structure.FieldOrder({"lgrui1_name", "lgrui1_comment"})
    public static class LOCALGROUP_INFO_1 extends Structure {
        public String lgrui1_name;
        public String lgrui1_comment;

        public LOCALGROUP_INFO_1() {
            super(W32APITypeMapper.UNICODE);
        }

        public LOCALGROUP_INFO_1(Pointer memory) {
            super(memory, 0, W32APITypeMapper.UNICODE);
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/LMAccess$USER_INFO_0.class */
    @Structure.FieldOrder({"usri0_name"})
    public static class USER_INFO_0 extends Structure {
        public String usri0_name;

        public USER_INFO_0() {
            super(W32APITypeMapper.UNICODE);
        }

        public USER_INFO_0(Pointer memory) {
            super(memory, 0, W32APITypeMapper.UNICODE);
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/LMAccess$USER_INFO_1.class */
    @Structure.FieldOrder({"usri1_name", "usri1_password", "usri1_password_age", "usri1_priv", "usri1_home_dir", "usri1_comment", "usri1_flags", "usri1_script_path"})
    public static class USER_INFO_1 extends Structure {
        public String usri1_name;
        public String usri1_password;
        public int usri1_password_age;
        public int usri1_priv;
        public String usri1_home_dir;
        public String usri1_comment;
        public int usri1_flags;
        public String usri1_script_path;

        public USER_INFO_1() {
            super(W32APITypeMapper.UNICODE);
        }

        public USER_INFO_1(Pointer memory) {
            super(memory, 0, W32APITypeMapper.UNICODE);
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/LMAccess$USER_INFO_23.class */
    @Structure.FieldOrder({"usri23_name", "usri23_full_name", "usri23_comment", "usri23_flags", "usri23_user_sid"})
    public static class USER_INFO_23 extends Structure {
        public String usri23_name;
        public String usri23_full_name;
        public String usri23_comment;
        public int usri23_flags;
        public WinNT.PSID.ByReference usri23_user_sid;

        public USER_INFO_23() {
            super(W32APITypeMapper.UNICODE);
        }

        public USER_INFO_23(Pointer memory) {
            super(memory, 0, W32APITypeMapper.UNICODE);
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/LMAccess$GROUP_USERS_INFO_0.class */
    @Structure.FieldOrder({"grui0_name"})
    public static class GROUP_USERS_INFO_0 extends Structure {
        public String grui0_name;

        public GROUP_USERS_INFO_0() {
            super(W32APITypeMapper.UNICODE);
        }

        public GROUP_USERS_INFO_0(Pointer memory) {
            super(memory, 0, W32APITypeMapper.UNICODE);
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/LMAccess$LOCALGROUP_USERS_INFO_0.class */
    @Structure.FieldOrder({"lgrui0_name"})
    public static class LOCALGROUP_USERS_INFO_0 extends Structure {
        public String lgrui0_name;

        public LOCALGROUP_USERS_INFO_0() {
            super(W32APITypeMapper.UNICODE);
        }

        public LOCALGROUP_USERS_INFO_0(Pointer memory) {
            super(memory, 0, W32APITypeMapper.UNICODE);
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/LMAccess$GROUP_INFO_0.class */
    @Structure.FieldOrder({"grpi0_name"})
    public static class GROUP_INFO_0 extends Structure {
        public String grpi0_name;

        public GROUP_INFO_0() {
            super(W32APITypeMapper.UNICODE);
        }

        public GROUP_INFO_0(Pointer memory) {
            super(memory, 0, W32APITypeMapper.UNICODE);
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/LMAccess$GROUP_INFO_1.class */
    @Structure.FieldOrder({"grpi1_name", "grpi1_comment"})
    public static class GROUP_INFO_1 extends Structure {
        public String grpi1_name;
        public String grpi1_comment;

        public GROUP_INFO_1() {
            super(W32APITypeMapper.UNICODE);
        }

        public GROUP_INFO_1(Pointer memory) {
            super(memory, 0, W32APITypeMapper.UNICODE);
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/LMAccess$GROUP_INFO_2.class */
    @Structure.FieldOrder({"grpi2_name", "grpi2_comment", "grpi2_group_id", "grpi2_attributes"})
    public static class GROUP_INFO_2 extends Structure {
        public String grpi2_name;
        public String grpi2_comment;
        public int grpi2_group_id;
        public int grpi2_attributes;

        public GROUP_INFO_2() {
            super(W32APITypeMapper.UNICODE);
        }

        public GROUP_INFO_2(Pointer memory) {
            super(memory, 0, W32APITypeMapper.UNICODE);
            read();
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/LMAccess$GROUP_INFO_3.class */
    @Structure.FieldOrder({"grpi3_name", "grpi3_comment", "grpi3_group_sid", "grpi3_attributes"})
    public static class GROUP_INFO_3 extends Structure {
        public String grpi3_name;
        public String grpi3_comment;
        public WinNT.PSID.ByReference grpi3_group_sid;
        public int grpi3_attributes;

        public GROUP_INFO_3() {
            super(W32APITypeMapper.UNICODE);
        }

        public GROUP_INFO_3(Pointer memory) {
            super(memory, 0, W32APITypeMapper.UNICODE);
            read();
        }
    }
}
