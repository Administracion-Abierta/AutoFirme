package com.sun.jna.platform.win32;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import java.util.ArrayList;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Netapi32Util.class */
public abstract class Netapi32Util {

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Netapi32Util$DomainController.class */
    public static class DomainController {
        public String name;
        public String address;
        public int addressType;
        public Guid.GUID domainGuid;
        public String domainName;
        public String dnsForestName;
        public int flags;
        public String clientSiteName;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Netapi32Util$Group.class */
    public static class Group {
        public String name;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Netapi32Util$LocalGroup.class */
    public static class LocalGroup extends Group {
        public String comment;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Netapi32Util$User.class */
    public static class User {
        public String name;
        public String comment;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Netapi32Util$UserInfo.class */
    public static class UserInfo extends User {
        public String fullName;
        public String sidString;
        public WinNT.PSID sid;
        public int flags;
    }

    public static String getDCName() {
        return getDCName(null, null);
    }

    public static String getDCName(String serverName, String domainName) {
        PointerByReference bufptr = new PointerByReference();
        try {
            int rc = Netapi32.INSTANCE.NetGetDCName(domainName, serverName, bufptr);
            if (0 != rc) {
                throw new Win32Exception(rc);
            }
            String wideString = bufptr.getValue().getWideString(0L);
            if (0 == Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue())) {
                return wideString;
            }
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        } catch (Throwable th) {
            if (0 != Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue())) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }
            throw th;
        }
    }

    public static int getJoinStatus() {
        return getJoinStatus(null);
    }

    public static int getJoinStatus(String computerName) {
        int rc;
        int rc2;
        PointerByReference lpNameBuffer = new PointerByReference();
        IntByReference bufferType = new IntByReference();
        try {
            int rc3 = Netapi32.INSTANCE.NetGetJoinInformation(computerName, lpNameBuffer, bufferType);
            if (0 != rc3) {
                throw new Win32Exception(rc3);
            }
            int value = bufferType.getValue();
            if (lpNameBuffer.getPointer() == null || 0 == (rc2 = Netapi32.INSTANCE.NetApiBufferFree(lpNameBuffer.getValue()))) {
                return value;
            }
            throw new Win32Exception(rc2);
        } catch (Throwable th) {
            if (lpNameBuffer.getPointer() != null && 0 != (rc = Netapi32.INSTANCE.NetApiBufferFree(lpNameBuffer.getValue()))) {
                throw new Win32Exception(rc);
            }
            throw th;
        }
    }

    public static String getDomainName(String computerName) {
        int rc;
        int rc2;
        PointerByReference lpNameBuffer = new PointerByReference();
        IntByReference bufferType = new IntByReference();
        try {
            int rc3 = Netapi32.INSTANCE.NetGetJoinInformation(computerName, lpNameBuffer, bufferType);
            if (0 != rc3) {
                throw new Win32Exception(rc3);
            }
            String wideString = lpNameBuffer.getValue().getWideString(0L);
            if (lpNameBuffer.getPointer() == null || 0 == (rc2 = Netapi32.INSTANCE.NetApiBufferFree(lpNameBuffer.getValue()))) {
                return wideString;
            }
            throw new Win32Exception(rc2);
        } catch (Throwable th) {
            if (lpNameBuffer.getPointer() != null && 0 != (rc = Netapi32.INSTANCE.NetApiBufferFree(lpNameBuffer.getValue()))) {
                throw new Win32Exception(rc);
            }
            throw th;
        }
    }

    public static LocalGroup[] getLocalGroups() {
        return getLocalGroups(null);
    }

    public static LocalGroup[] getLocalGroups(String serverName) {
        int rc;
        int rc2;
        PointerByReference bufptr = new PointerByReference();
        IntByReference entriesRead = new IntByReference();
        IntByReference totalEntries = new IntByReference();
        try {
            int rc3 = Netapi32.INSTANCE.NetLocalGroupEnum(serverName, 1, bufptr, -1, entriesRead, totalEntries, null);
            if (0 != rc3 || bufptr.getValue() == Pointer.NULL) {
                throw new Win32Exception(rc3);
            }
            ArrayList<LocalGroup> result = new ArrayList<>();
            if (entriesRead.getValue() > 0) {
                LMAccess.LOCALGROUP_INFO_1 group = new LMAccess.LOCALGROUP_INFO_1(bufptr.getValue());
                LMAccess.LOCALGROUP_INFO_1[] groups = (LMAccess.LOCALGROUP_INFO_1[]) group.toArray(entriesRead.getValue());
                for (LMAccess.LOCALGROUP_INFO_1 lgpi : groups) {
                    LocalGroup lgp = new LocalGroup();
                    lgp.name = lgpi.lgrui1_name;
                    lgp.comment = lgpi.lgrui1_comment;
                    result.add(lgp);
                }
            }
            LocalGroup[] localGroupArr = (LocalGroup[]) result.toArray(new LocalGroup[0]);
            if (bufptr.getValue() == Pointer.NULL || 0 == (rc2 = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue()))) {
                return localGroupArr;
            }
            throw new Win32Exception(rc2);
        } catch (Throwable th) {
            if (bufptr.getValue() != Pointer.NULL && 0 != (rc = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue()))) {
                throw new Win32Exception(rc);
            }
            throw th;
        }
    }

    public static Group[] getGlobalGroups() {
        return getGlobalGroups(null);
    }

    public static Group[] getGlobalGroups(String serverName) {
        int rc;
        int rc2;
        PointerByReference bufptr = new PointerByReference();
        IntByReference entriesRead = new IntByReference();
        IntByReference totalEntries = new IntByReference();
        try {
            int rc3 = Netapi32.INSTANCE.NetGroupEnum(serverName, 1, bufptr, -1, entriesRead, totalEntries, null);
            if (0 != rc3 || bufptr.getValue() == Pointer.NULL) {
                throw new Win32Exception(rc3);
            }
            ArrayList<LocalGroup> result = new ArrayList<>();
            if (entriesRead.getValue() > 0) {
                LMAccess.GROUP_INFO_1 group = new LMAccess.GROUP_INFO_1(bufptr.getValue());
                LMAccess.GROUP_INFO_1[] groups = (LMAccess.GROUP_INFO_1[]) group.toArray(entriesRead.getValue());
                for (LMAccess.GROUP_INFO_1 lgpi : groups) {
                    LocalGroup lgp = new LocalGroup();
                    lgp.name = lgpi.grpi1_name;
                    lgp.comment = lgpi.grpi1_comment;
                    result.add(lgp);
                }
            }
            Group[] groupArr = (Group[]) result.toArray(new LocalGroup[0]);
            if (bufptr.getValue() == Pointer.NULL || 0 == (rc2 = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue()))) {
                return groupArr;
            }
            throw new Win32Exception(rc2);
        } catch (Throwable th) {
            if (bufptr.getValue() != Pointer.NULL && 0 != (rc = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue()))) {
                throw new Win32Exception(rc);
            }
            throw th;
        }
    }

    public static User[] getUsers() {
        return getUsers(null);
    }

    public static User[] getUsers(String serverName) {
        int rc;
        int rc2;
        PointerByReference bufptr = new PointerByReference();
        IntByReference entriesRead = new IntByReference();
        IntByReference totalEntries = new IntByReference();
        try {
            int rc3 = Netapi32.INSTANCE.NetUserEnum(serverName, 1, 0, bufptr, -1, entriesRead, totalEntries, null);
            if (0 != rc3 || bufptr.getValue() == Pointer.NULL) {
                throw new Win32Exception(rc3);
            }
            ArrayList<User> result = new ArrayList<>();
            if (entriesRead.getValue() > 0) {
                LMAccess.USER_INFO_1 user = new LMAccess.USER_INFO_1(bufptr.getValue());
                LMAccess.USER_INFO_1[] users = (LMAccess.USER_INFO_1[]) user.toArray(entriesRead.getValue());
                for (LMAccess.USER_INFO_1 lu : users) {
                    User auser = new User();
                    if (lu.usri1_name != null) {
                        auser.name = lu.usri1_name;
                    }
                    result.add(auser);
                }
            }
            User[] userArr = (User[]) result.toArray(new User[0]);
            if (bufptr.getValue() == Pointer.NULL || 0 == (rc2 = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue()))) {
                return userArr;
            }
            throw new Win32Exception(rc2);
        } catch (Throwable th) {
            if (bufptr.getValue() != Pointer.NULL && 0 != (rc = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue()))) {
                throw new Win32Exception(rc);
            }
            throw th;
        }
    }

    public static Group[] getCurrentUserLocalGroups() {
        return getUserLocalGroups(Secur32Util.getUserNameEx(2));
    }

    public static Group[] getUserLocalGroups(String userName) {
        return getUserLocalGroups(userName, null);
    }

    public static Group[] getUserLocalGroups(String userName, String serverName) {
        int rc;
        int rc2;
        PointerByReference bufptr = new PointerByReference();
        IntByReference entriesread = new IntByReference();
        IntByReference totalentries = new IntByReference();
        try {
            int rc3 = Netapi32.INSTANCE.NetUserGetLocalGroups(serverName, userName, 0, 0, bufptr, -1, entriesread, totalentries);
            if (rc3 != 0) {
                throw new Win32Exception(rc3);
            }
            ArrayList<Group> result = new ArrayList<>();
            if (entriesread.getValue() > 0) {
                LMAccess.LOCALGROUP_USERS_INFO_0 lgroup = new LMAccess.LOCALGROUP_USERS_INFO_0(bufptr.getValue());
                LMAccess.LOCALGROUP_USERS_INFO_0[] lgroups = (LMAccess.LOCALGROUP_USERS_INFO_0[]) lgroup.toArray(entriesread.getValue());
                for (LMAccess.LOCALGROUP_USERS_INFO_0 lgpi : lgroups) {
                    LocalGroup lgp = new LocalGroup();
                    if (lgpi.lgrui0_name != null) {
                        lgp.name = lgpi.lgrui0_name;
                    }
                    result.add(lgp);
                }
            }
            Group[] groupArr = (Group[]) result.toArray(new Group[0]);
            if (bufptr.getValue() == Pointer.NULL || 0 == (rc2 = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue()))) {
                return groupArr;
            }
            throw new Win32Exception(rc2);
        } catch (Throwable th) {
            if (bufptr.getValue() != Pointer.NULL && 0 != (rc = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue()))) {
                throw new Win32Exception(rc);
            }
            throw th;
        }
    }

    public static Group[] getUserGroups(String userName) {
        return getUserGroups(userName, null);
    }

    public static Group[] getUserGroups(String userName, String serverName) {
        int rc;
        int rc2;
        PointerByReference bufptr = new PointerByReference();
        IntByReference entriesread = new IntByReference();
        IntByReference totalentries = new IntByReference();
        try {
            int rc3 = Netapi32.INSTANCE.NetUserGetGroups(serverName, userName, 0, bufptr, -1, entriesread, totalentries);
            if (rc3 != 0) {
                throw new Win32Exception(rc3);
            }
            ArrayList<Group> result = new ArrayList<>();
            if (entriesread.getValue() > 0) {
                LMAccess.GROUP_USERS_INFO_0 lgroup = new LMAccess.GROUP_USERS_INFO_0(bufptr.getValue());
                LMAccess.GROUP_USERS_INFO_0[] lgroups = (LMAccess.GROUP_USERS_INFO_0[]) lgroup.toArray(entriesread.getValue());
                for (LMAccess.GROUP_USERS_INFO_0 lgpi : lgroups) {
                    Group lgp = new Group();
                    if (lgpi.grui0_name != null) {
                        lgp.name = lgpi.grui0_name;
                    }
                    result.add(lgp);
                }
            }
            Group[] groupArr = (Group[]) result.toArray(new Group[0]);
            if (bufptr.getValue() == Pointer.NULL || 0 == (rc2 = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue()))) {
                return groupArr;
            }
            throw new Win32Exception(rc2);
        } catch (Throwable th) {
            if (bufptr.getValue() != Pointer.NULL && 0 != (rc = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue()))) {
                throw new Win32Exception(rc);
            }
            throw th;
        }
    }

    public static DomainController getDC() {
        DsGetDC.PDOMAIN_CONTROLLER_INFO pdci = new DsGetDC.PDOMAIN_CONTROLLER_INFO();
        int rc = Netapi32.INSTANCE.DsGetDcName(null, null, null, null, 0, pdci);
        if (0 != rc) {
            throw new Win32Exception(rc);
        }
        DomainController dc = new DomainController();
        dc.address = pdci.dci.DomainControllerAddress;
        dc.addressType = pdci.dci.DomainControllerAddressType;
        dc.clientSiteName = pdci.dci.ClientSiteName;
        dc.dnsForestName = pdci.dci.DnsForestName;
        dc.domainGuid = pdci.dci.DomainGuid;
        dc.domainName = pdci.dci.DomainName;
        dc.flags = pdci.dci.Flags;
        dc.name = pdci.dci.DomainControllerName;
        int rc2 = Netapi32.INSTANCE.NetApiBufferFree(pdci.dci.getPointer());
        if (0 != rc2) {
            throw new Win32Exception(rc2);
        }
        return dc;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Netapi32Util$DomainTrust.class */
    public static class DomainTrust {
        public String NetbiosDomainName;
        public String DnsDomainName;
        public WinNT.PSID DomainSid;
        public String DomainSidString;
        public Guid.GUID DomainGuid;
        public String DomainGuidString;
        private int flags;

        public boolean isInForest() {
            return (this.flags & 1) != 0;
        }

        public boolean isOutbound() {
            return (this.flags & 2) != 0;
        }

        public boolean isRoot() {
            return (this.flags & 4) != 0;
        }

        public boolean isPrimary() {
            return (this.flags & 8) != 0;
        }

        public boolean isNativeMode() {
            return (this.flags & 16) != 0;
        }

        public boolean isInbound() {
            return (this.flags & 32) != 0;
        }
    }

    public static DomainTrust[] getDomainTrusts() {
        return getDomainTrusts(null);
    }

    public static DomainTrust[] getDomainTrusts(String serverName) {
        IntByReference domainTrustCount = new IntByReference();
        PointerByReference domainsPointerRef = new PointerByReference();
        int rc = Netapi32.INSTANCE.DsEnumerateDomainTrusts(serverName, 63, domainsPointerRef, domainTrustCount);
        if (0 != rc) {
            throw new Win32Exception(rc);
        }
        try {
            ArrayList<DomainTrust> trusts = new ArrayList<>(domainTrustCount.getValue());
            if (domainTrustCount.getValue() > 0) {
                DsGetDC.DS_DOMAIN_TRUSTS domainTrustRefs = new DsGetDC.DS_DOMAIN_TRUSTS(domainsPointerRef.getValue());
                DsGetDC.DS_DOMAIN_TRUSTS[] domainTrusts = (DsGetDC.DS_DOMAIN_TRUSTS[]) domainTrustRefs.toArray(new DsGetDC.DS_DOMAIN_TRUSTS[domainTrustCount.getValue()]);
                for (DsGetDC.DS_DOMAIN_TRUSTS domainTrust : domainTrusts) {
                    DomainTrust t = new DomainTrust();
                    if (domainTrust.DnsDomainName != null) {
                        t.DnsDomainName = domainTrust.DnsDomainName;
                    }
                    if (domainTrust.NetbiosDomainName != null) {
                        t.NetbiosDomainName = domainTrust.NetbiosDomainName;
                    }
                    t.DomainSid = domainTrust.DomainSid;
                    if (domainTrust.DomainSid != null) {
                        t.DomainSidString = Advapi32Util.convertSidToStringSid(domainTrust.DomainSid);
                    }
                    t.DomainGuid = domainTrust.DomainGuid;
                    if (domainTrust.DomainGuid != null) {
                        t.DomainGuidString = Ole32Util.getStringFromGUID(domainTrust.DomainGuid);
                    }
                    t.flags = domainTrust.Flags;
                    trusts.add(t);
                }
            }
            DomainTrust[] domainTrustArr = (DomainTrust[]) trusts.toArray(new DomainTrust[0]);
            int rc2 = Netapi32.INSTANCE.NetApiBufferFree(domainsPointerRef.getValue());
            if (0 == rc2) {
                return domainTrustArr;
            }
            throw new Win32Exception(rc2);
        } catch (Throwable th) {
            int rc3 = Netapi32.INSTANCE.NetApiBufferFree(domainsPointerRef.getValue());
            if (0 != rc3) {
                throw new Win32Exception(rc3);
            }
            throw th;
        }
    }

    public static UserInfo getUserInfo(String accountName) {
        return getUserInfo(accountName, getDCName());
    }

    public static UserInfo getUserInfo(String accountName, String domainName) {
        PointerByReference bufptr = new PointerByReference();
        try {
            int rc = Netapi32.INSTANCE.NetUserGetInfo(domainName, accountName, 23, bufptr);
            if (rc == 0) {
                LMAccess.USER_INFO_23 info_23 = new LMAccess.USER_INFO_23(bufptr.getValue());
                UserInfo userInfo = new UserInfo();
                userInfo.comment = info_23.usri23_comment;
                userInfo.flags = info_23.usri23_flags;
                userInfo.fullName = info_23.usri23_full_name;
                userInfo.name = info_23.usri23_name;
                if (info_23.usri23_user_sid != null) {
                    userInfo.sidString = Advapi32Util.convertSidToStringSid(info_23.usri23_user_sid);
                }
                userInfo.sid = info_23.usri23_user_sid;
                if (bufptr.getValue() != Pointer.NULL) {
                    Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue());
                }
                return userInfo;
            }
            throw new Win32Exception(rc);
        } catch (Throwable th) {
            if (bufptr.getValue() != Pointer.NULL) {
                Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue());
            }
            throw th;
        }
    }
}
