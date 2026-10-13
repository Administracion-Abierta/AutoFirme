package com.sun.jna.platform.mac;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.unix.LibCAPI;
import com.sun.jna.platform.win32.Ddeml;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/SystemB.class */
public interface SystemB extends LibCAPI, Library {
    public static final int HOST_LOAD_INFO = 1;
    public static final int HOST_VM_INFO = 2;
    public static final int HOST_CPU_LOAD_INFO = 3;
    public static final int HOST_VM_INFO64 = 4;
    public static final int CPU_STATE_MAX = 4;
    public static final int CPU_STATE_USER = 0;
    public static final int CPU_STATE_SYSTEM = 1;
    public static final int CPU_STATE_IDLE = 2;
    public static final int CPU_STATE_NICE = 3;
    public static final int PROCESSOR_BASIC_INFO = 1;
    public static final int PROCESSOR_CPU_LOAD_INFO = 2;
    public static final int MAXCOMLEN = 16;
    public static final int MAXPATHLEN = 1024;
    public static final int PROC_PIDPATHINFO_MAXSIZE = 4096;
    public static final int PROC_ALL_PIDS = 1;
    public static final int PROC_PIDTASKALLINFO = 2;
    public static final int PROC_PIDTBSDINFO = 3;
    public static final int PROC_PIDTASKINFO = 4;
    public static final int PROC_PIDVNODEPATHINFO = 9;
    public static final int MFSTYPENAMELEN = 16;
    public static final int MNAMELEN = 1024;
    public static final int MNT_WAIT = 1;
    public static final int MNT_NOWAIT = 16;
    public static final int MNT_DWAIT = 256;
    public static final int RUSAGE_INFO_V2 = 2;
    public static final SystemB INSTANCE = (SystemB) Native.load(Ddeml.SZDDESYS_TOPIC, SystemB.class);
    public static final int UINT64_SIZE = Native.getNativeSize(Long.TYPE);
    public static final int INT_SIZE = Native.getNativeSize(Integer.TYPE);

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/SystemB$Group.class */
    @Structure.FieldOrder({"gr_name", "gr_passwd", "gr_gid", "gr_mem"})
    public static class Group extends Structure {
        public String gr_name;
        public String gr_passwd;
        public int gr_gid;
        public PointerByReference gr_mem;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/SystemB$HostCpuLoadInfo.class */
    @Structure.FieldOrder({"cpu_ticks"})
    public static class HostCpuLoadInfo extends Structure {
        public int[] cpu_ticks = new int[4];
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/SystemB$HostLoadInfo.class */
    @Structure.FieldOrder({"avenrun", "mach_factor"})
    public static class HostLoadInfo extends Structure {
        public int[] avenrun = new int[3];
        public int[] mach_factor = new int[3];
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/SystemB$IFdata.class */
    @Structure.FieldOrder({"ifi_type", "ifi_typelen", "ifi_physical", "ifi_addrlen", "ifi_hdrlen", "ifi_recvquota", "ifi_xmitquota", "ifi_unused1", "ifi_mtu", "ifi_metric", "ifi_baudrate", "ifi_ipackets", "ifi_ierrors", "ifi_opackets", "ifi_oerrors", "ifi_collisions", "ifi_ibytes", "ifi_obytes", "ifi_imcasts", "ifi_omcasts", "ifi_iqdrops", "ifi_noproto", "ifi_recvtiming", "ifi_xmittiming", "ifi_lastchange", "ifi_unused2", "ifi_hwassist", "ifi_reserved1", "ifi_reserved2"})
    public static class IFdata extends Structure {
        public byte ifi_type;
        public byte ifi_typelen;
        public byte ifi_physical;
        public byte ifi_addrlen;
        public byte ifi_hdrlen;
        public byte ifi_recvquota;
        public byte ifi_xmitquota;
        public byte ifi_unused1;
        public int ifi_mtu;
        public int ifi_metric;
        public int ifi_baudrate;
        public int ifi_ipackets;
        public int ifi_ierrors;
        public int ifi_opackets;
        public int ifi_oerrors;
        public int ifi_collisions;
        public int ifi_ibytes;
        public int ifi_obytes;
        public int ifi_imcasts;
        public int ifi_omcasts;
        public int ifi_iqdrops;
        public int ifi_noproto;
        public int ifi_recvtiming;
        public int ifi_xmittiming;
        public Timeval ifi_lastchange;
        public int ifi_unused2;
        public int ifi_hwassist;
        public int ifi_reserved1;
        public int ifi_reserved2;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/SystemB$IFdata64.class */
    @Structure.FieldOrder({"ifi_type", "ifi_typelen", "ifi_physical", "ifi_addrlen", "ifi_hdrlen", "ifi_recvquota", "ifi_xmitquota", "ifi_unused1", "ifi_mtu", "ifi_metric", "ifi_baudrate", "ifi_ipackets", "ifi_ierrors", "ifi_opackets", "ifi_oerrors", "ifi_collisions", "ifi_ibytes", "ifi_obytes", "ifi_imcasts", "ifi_omcasts", "ifi_iqdrops", "ifi_noproto", "ifi_recvtiming", "ifi_xmittiming", "ifi_lastchange"})
    public static class IFdata64 extends Structure {
        public byte ifi_type;
        public byte ifi_typelen;
        public byte ifi_physical;
        public byte ifi_addrlen;
        public byte ifi_hdrlen;
        public byte ifi_recvquota;
        public byte ifi_xmitquota;
        public byte ifi_unused1;
        public int ifi_mtu;
        public int ifi_metric;
        public long ifi_baudrate;
        public long ifi_ipackets;
        public long ifi_ierrors;
        public long ifi_opackets;
        public long ifi_oerrors;
        public long ifi_collisions;
        public long ifi_ibytes;
        public long ifi_obytes;
        public long ifi_imcasts;
        public long ifi_omcasts;
        public long ifi_iqdrops;
        public long ifi_noproto;
        public int ifi_recvtiming;
        public int ifi_xmittiming;
        public Timeval ifi_lastchange;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/SystemB$Passwd.class */
    @Structure.FieldOrder({"pw_name", "pw_passwd", "pw_uid", "pw_gid", "pw_change", "pw_class", "pw_gecos", "pw_dir", "pw_shell", "pw_expire", "pw_fields"})
    public static class Passwd extends Structure {
        public String pw_name;
        public String pw_passwd;
        public int pw_uid;
        public int pw_gid;
        public NativeLong pw_change;
        public String pw_class;
        public String pw_gecos;
        public String pw_dir;
        public String pw_shell;
        public NativeLong pw_expire;
        public int pw_fields;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/SystemB$ProcBsdInfo.class */
    @Structure.FieldOrder({"pbi_flags", "pbi_status", "pbi_xstatus", "pbi_pid", "pbi_ppid", "pbi_uid", "pbi_gid", "pbi_ruid", "pbi_rgid", "pbi_svuid", "pbi_svgid", "rfu_1", "pbi_comm", "pbi_name", "pbi_nfiles", "pbi_pgid", "pbi_pjobc", "e_tdev", "e_tpgid", "pbi_nice", "pbi_start_tvsec", "pbi_start_tvusec"})
    public static class ProcBsdInfo extends Structure {
        public int pbi_flags;
        public int pbi_status;
        public int pbi_xstatus;
        public int pbi_pid;
        public int pbi_ppid;
        public int pbi_uid;
        public int pbi_gid;
        public int pbi_ruid;
        public int pbi_rgid;
        public int pbi_svuid;
        public int pbi_svgid;
        public int rfu_1;
        public byte[] pbi_comm = new byte[16];
        public byte[] pbi_name = new byte[32];
        public int pbi_nfiles;
        public int pbi_pgid;
        public int pbi_pjobc;
        public int e_tdev;
        public int e_tpgid;
        public int pbi_nice;
        public long pbi_start_tvsec;
        public long pbi_start_tvusec;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/SystemB$ProcTaskAllInfo.class */
    @Structure.FieldOrder({"pbsd", "ptinfo"})
    public static class ProcTaskAllInfo extends Structure {
        public ProcBsdInfo pbsd;
        public ProcTaskInfo ptinfo;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/SystemB$ProcTaskInfo.class */
    @Structure.FieldOrder({"pti_virtual_size", "pti_resident_size", "pti_total_user", "pti_total_system", "pti_threads_user", "pti_threads_system", "pti_policy", "pti_faults", "pti_pageins", "pti_cow_faults", "pti_messages_sent", "pti_messages_received", "pti_syscalls_mach", "pti_syscalls_unix", "pti_csw", "pti_threadnum", "pti_numrunning", "pti_priority"})
    public static class ProcTaskInfo extends Structure {
        public long pti_virtual_size;
        public long pti_resident_size;
        public long pti_total_user;
        public long pti_total_system;
        public long pti_threads_user;
        public long pti_threads_system;
        public int pti_policy;
        public int pti_faults;
        public int pti_pageins;
        public int pti_cow_faults;
        public int pti_messages_sent;
        public int pti_messages_received;
        public int pti_syscalls_mach;
        public int pti_syscalls_unix;
        public int pti_csw;
        public int pti_threadnum;
        public int pti_numrunning;
        public int pti_priority;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/SystemB$RUsageInfoV2.class */
    @Structure.FieldOrder({"ri_uuid", "ri_user_time", "ri_system_time", "ri_pkg_idle_wkups", "ri_interrupt_wkups", "ri_pageins", "ri_wired_size", "ri_resident_size", "ri_phys_footprint", "ri_proc_start_abstime", "ri_proc_exit_abstime", "ri_child_user_time", "ri_child_system_time", "ri_child_pkg_idle_wkups", "ri_child_interrupt_wkups", "ri_child_pageins", "ri_child_elapsed_abstime", "ri_diskio_bytesread", "ri_diskio_byteswritten"})
    public static class RUsageInfoV2 extends Structure {
        public byte[] ri_uuid = new byte[16];
        public long ri_user_time;
        public long ri_system_time;
        public long ri_pkg_idle_wkups;
        public long ri_interrupt_wkups;
        public long ri_pageins;
        public long ri_wired_size;
        public long ri_resident_size;
        public long ri_phys_footprint;
        public long ri_proc_start_abstime;
        public long ri_proc_exit_abstime;
        public long ri_child_user_time;
        public long ri_child_system_time;
        public long ri_child_pkg_idle_wkups;
        public long ri_child_interrupt_wkups;
        public long ri_child_pageins;
        public long ri_child_elapsed_abstime;
        public long ri_diskio_bytesread;
        public long ri_diskio_byteswritten;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/SystemB$Statfs.class */
    @Structure.FieldOrder({"f_bsize", "f_iosize", "f_blocks", "f_bfree", "f_bavail", "f_files", "f_ffree", "f_fsid", "f_owner", "f_type", "f_flags", "f_fssubtype", "f_fstypename", "f_mntonname", "f_mntfromname", "f_reserved"})
    public static class Statfs extends Structure {
        public int f_bsize;
        public int f_iosize;
        public long f_blocks;
        public long f_bfree;
        public long f_bavail;
        public long f_files;
        public long f_ffree;
        public int f_owner;
        public int f_type;
        public int f_flags;
        public int f_fssubtype;
        public int[] f_fsid = new int[2];
        public byte[] f_fstypename = new byte[16];
        public byte[] f_mntonname = new byte[1024];
        public byte[] f_mntfromname = new byte[1024];
        public int[] f_reserved = new int[8];
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/SystemB$Timeval.class */
    @Structure.FieldOrder({"tv_sec", "tv_usec"})
    public static class Timeval extends Structure {
        public NativeLong tv_sec;
        public int tv_usec;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/SystemB$Timezone.class */
    @Structure.FieldOrder({"tz_minuteswest", "tz_dsttime"})
    public static class Timezone extends Structure {
        public int tz_minuteswest;
        public int tz_dsttime;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/SystemB$VMMeter.class */
    @Structure.FieldOrder({"v_swtch", "v_trap", "v_syscall", "v_intr", "v_soft", "v_faults", "v_lookups", "v_hits", "v_vm_faults", "v_cow_faults", "v_swpin", "v_swpout", "v_pswpin", "v_pswpout", "v_pageins", "v_pageouts", "v_pgpgin", "v_pgpgout", "v_intrans", "v_reactivated", "v_rev", "v_scan", "v_dfree", "v_pfree", "v_zfod", "v_nzfod", "v_page_size", "v_kernel_pages", "v_free_target", "v_free_min", "v_free_count", "v_wire_count", "v_active_count", "v_inactive_target", "v_inactive_count"})
    public static class VMMeter extends Structure {
        public int v_swtch;
        public int v_trap;
        public int v_syscall;
        public int v_intr;
        public int v_soft;
        public int v_faults;
        public int v_lookups;
        public int v_hits;
        public int v_vm_faults;
        public int v_cow_faults;
        public int v_swpin;
        public int v_swpout;
        public int v_pswpin;
        public int v_pswpout;
        public int v_pageins;
        public int v_pageouts;
        public int v_pgpgin;
        public int v_pgpgout;
        public int v_intrans;
        public int v_reactivated;
        public int v_rev;
        public int v_scan;
        public int v_dfree;
        public int v_pfree;
        public int v_zfod;
        public int v_nzfod;
        public int v_page_size;
        public int v_kernel_pages;
        public int v_free_target;
        public int v_free_min;
        public int v_free_count;
        public int v_wire_count;
        public int v_active_count;
        public int v_inactive_target;
        public int v_inactive_count;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/SystemB$VMStatistics.class */
    @Structure.FieldOrder({"free_count", "active_count", "inactive_count", "wire_count", "zero_fill_count", "reactivations", "pageins", "pageouts", "faults", "cow_faults", "lookups", "hits", "purgeable_count", "purges", "speculative_count"})
    public static class VMStatistics extends Structure {
        public int free_count;
        public int active_count;
        public int inactive_count;
        public int wire_count;
        public int zero_fill_count;
        public int reactivations;
        public int pageins;
        public int pageouts;
        public int faults;
        public int cow_faults;
        public int lookups;
        public int hits;
        public int purgeable_count;
        public int purges;
        public int speculative_count;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/SystemB$VMStatistics64.class */
    @Structure.FieldOrder({"free_count", "active_count", "inactive_count", "wire_count", "zero_fill_count", "reactivations", "pageins", "pageouts", "faults", "cow_faults", "lookups", "hits", "purges", "purgeable_count", "speculative_count", "decompressions", "compressions", "swapins", "swapouts", "compressor_page_count", "throttled_count", "external_page_count", "internal_page_count", "total_uncompressed_pages_in_compressor"})
    public static class VMStatistics64 extends Structure {
        public int free_count;
        public int active_count;
        public int inactive_count;
        public int wire_count;
        public long zero_fill_count;
        public long reactivations;
        public long pageins;
        public long pageouts;
        public long faults;
        public long cow_faults;
        public long lookups;
        public long hits;
        public long purges;
        public int purgeable_count;
        public int speculative_count;
        public long decompressions;
        public long compressions;
        public long swapins;
        public long swapouts;
        public int compressor_page_count;
        public int throttled_count;
        public int external_page_count;
        public int internal_page_count;
        public long total_uncompressed_pages_in_compressor;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/SystemB$VnodeInfoPath.class */
    @Structure.FieldOrder({"vip_vi", "vip_path"})
    public static class VnodeInfoPath extends Structure {
        public byte[] vip_vi = new byte[152];
        public byte[] vip_path = new byte[1024];
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/SystemB$VnodePathInfo.class */
    @Structure.FieldOrder({"pvi_cdir", "pvi_rdir"})
    public static class VnodePathInfo extends Structure {
        public VnodeInfoPath pvi_cdir;
        public VnodeInfoPath pvi_rdir;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/SystemB$XswUsage.class */
    @Structure.FieldOrder({"xsu_total", "xsu_avail", "xsu_used", "xsu_pagesize", "xsu_encrypted"})
    public static class XswUsage extends Structure {
        public long xsu_total;
        public long xsu_avail;
        public long xsu_used;
        public int xsu_pagesize;
        public boolean xsu_encrypted;
    }

    int gettimeofday(Timeval timeval, Timezone timezone);

    int mach_host_self();

    int mach_task_self();

    int mach_port_deallocate(int i, int i2);

    int host_page_size(int i, LongByReference longByReference);

    int host_statistics(int i, int i2, Structure structure, IntByReference intByReference);

    int host_statistics64(int i, int i2, Structure structure, IntByReference intByReference);

    int sysctl(int[] iArr, int i, Pointer pointer, LibCAPI.size_t.ByReference byReference, Pointer pointer2, LibCAPI.size_t size_tVar);

    @Deprecated
    int sysctl(int[] iArr, int i, Pointer pointer, IntByReference intByReference, Pointer pointer2, int i2);

    int sysctlbyname(String str, Pointer pointer, LibCAPI.size_t.ByReference byReference, Pointer pointer2, LibCAPI.size_t size_tVar);

    @Deprecated
    int sysctlbyname(String str, Pointer pointer, IntByReference intByReference, Pointer pointer2, int i);

    int sysctlnametomib(String str, Pointer pointer, LibCAPI.size_t.ByReference byReference);

    @Deprecated
    int sysctlnametomib(String str, Pointer pointer, IntByReference intByReference);

    int host_processor_info(int i, int i2, IntByReference intByReference, PointerByReference pointerByReference, IntByReference intByReference2);

    Passwd getpwuid(int i);

    Group getgrgid(int i);

    int proc_listpids(int i, int i2, int[] iArr, int i3);

    int proc_pidinfo(int i, int i2, long j, Structure structure, int i3);

    int proc_pidpath(int i, Pointer pointer, int i2);

    int proc_pid_rusage(int i, int i2, RUsageInfoV2 rUsageInfoV2);

    int getfsstat64(Statfs[] statfsArr, int i, int i2);

    int getpid();

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/SystemB$IFmsgHdr.class */
    @Structure.FieldOrder({"ifm_msglen", "ifm_version", "ifm_type", "ifm_addrs", "ifm_flags", "ifm_index", "ifm_data"})
    public static class IFmsgHdr extends Structure {
        public short ifm_msglen;
        public byte ifm_version;
        public byte ifm_type;
        public int ifm_addrs;
        public int ifm_flags;
        public short ifm_index;
        public IFdata ifm_data;

        public IFmsgHdr() {
        }

        public IFmsgHdr(Pointer p) {
            super(p);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/mac/SystemB$IFmsgHdr2.class */
    @Structure.FieldOrder({"ifm_msglen", "ifm_version", "ifm_type", "ifm_addrs", "ifm_flags", "ifm_index", "ifm_snd_len", "ifm_snd_maxlen", "ifm_snd_drops", "ifm_timer", "ifm_data"})
    public static class IFmsgHdr2 extends Structure {
        public short ifm_msglen;
        public byte ifm_version;
        public byte ifm_type;
        public int ifm_addrs;
        public int ifm_flags;
        public short ifm_index;
        public int ifm_snd_len;
        public int ifm_snd_maxlen;
        public int ifm_snd_drops;
        public int ifm_timer;
        public IFdata64 ifm_data;

        public IFmsgHdr2(Pointer p) {
            super(p);
        }
    }
}
