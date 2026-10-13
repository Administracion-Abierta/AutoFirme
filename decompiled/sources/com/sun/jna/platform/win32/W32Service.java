package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import java.io.Closeable;
import java.util.List;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/W32Service.class */
public class W32Service implements Closeable {
    Winsvc.SC_HANDLE _handle;

    public W32Service(Winsvc.SC_HANDLE handle) {
        this._handle = null;
        this._handle = handle;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public void close() {
        if (this._handle != null) {
            if (!Advapi32.INSTANCE.CloseServiceHandle(this._handle)) {
                throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
            }
            this._handle = null;
        }
    }

    private void addShutdownPrivilegeToProcess() {
        WinNT.HANDLEByReference hToken = new WinNT.HANDLEByReference();
        WinNT.LUID luid = new WinNT.LUID();
        Advapi32.INSTANCE.OpenProcessToken(Kernel32.INSTANCE.GetCurrentProcess(), 32, hToken);
        Advapi32.INSTANCE.LookupPrivilegeValue("", WinNT.SE_SHUTDOWN_NAME, luid);
        WinNT.TOKEN_PRIVILEGES tp = new WinNT.TOKEN_PRIVILEGES(1);
        tp.Privileges[0] = new WinNT.LUID_AND_ATTRIBUTES(luid, new WinDef.DWORD(2L));
        Advapi32.INSTANCE.AdjustTokenPrivileges(hToken.getValue(), false, tp, tp.size(), null, new IntByReference());
    }

    public void setFailureActions(List<Winsvc.SC_ACTION> actions, int resetPeriod, String rebootMsg, String command) {
        Winsvc.SERVICE_FAILURE_ACTIONS.ByReference actionStruct = new Winsvc.SERVICE_FAILURE_ACTIONS.ByReference();
        actionStruct.dwResetPeriod = resetPeriod;
        actionStruct.lpRebootMsg = rebootMsg;
        actionStruct.lpCommand = command;
        actionStruct.cActions = actions.size();
        actionStruct.lpsaActions = new Winsvc.SC_ACTION.ByReference();
        Winsvc.SC_ACTION[] actionArray = (Winsvc.SC_ACTION[]) actionStruct.lpsaActions.toArray(actions.size());
        boolean hasShutdownPrivilege = false;
        int i = 0;
        for (Winsvc.SC_ACTION action : actions) {
            if (!hasShutdownPrivilege && action.type == 2) {
                addShutdownPrivilegeToProcess();
                hasShutdownPrivilege = true;
            }
            actionArray[i].type = action.type;
            actionArray[i].delay = action.delay;
            i++;
        }
        if (!Advapi32.INSTANCE.ChangeServiceConfig2(this._handle, 2, actionStruct)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
    }

    private Pointer queryServiceConfig2(int type) {
        IntByReference bufferSize = new IntByReference();
        Advapi32.INSTANCE.QueryServiceConfig2(this._handle, type, Pointer.NULL, 0, bufferSize);
        Pointer buffer = new Memory(bufferSize.getValue());
        if (!Advapi32.INSTANCE.QueryServiceConfig2(this._handle, type, buffer, bufferSize.getValue(), new IntByReference())) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        return buffer;
    }

    public Winsvc.SERVICE_FAILURE_ACTIONS getFailureActions() {
        Pointer buffer = queryServiceConfig2(2);
        Winsvc.SERVICE_FAILURE_ACTIONS result = new Winsvc.SERVICE_FAILURE_ACTIONS(buffer);
        return result;
    }

    public void setFailureActionsFlag(boolean flagValue) {
        Winsvc.SERVICE_FAILURE_ACTIONS_FLAG flag = new Winsvc.SERVICE_FAILURE_ACTIONS_FLAG();
        flag.fFailureActionsOnNonCrashFailures = flagValue ? 1 : 0;
        if (!Advapi32.INSTANCE.ChangeServiceConfig2(this._handle, 4, flag)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
    }

    public boolean getFailureActionsFlag() {
        Pointer buffer = queryServiceConfig2(4);
        Winsvc.SERVICE_FAILURE_ACTIONS_FLAG result = new Winsvc.SERVICE_FAILURE_ACTIONS_FLAG(buffer);
        return result.fFailureActionsOnNonCrashFailures != 0;
    }

    public Winsvc.SERVICE_STATUS_PROCESS queryStatus() {
        IntByReference size = new IntByReference();
        Advapi32.INSTANCE.QueryServiceStatusEx(this._handle, 0, null, 0, size);
        Winsvc.SERVICE_STATUS_PROCESS status = new Winsvc.SERVICE_STATUS_PROCESS(size.getValue());
        if (!Advapi32.INSTANCE.QueryServiceStatusEx(this._handle, 0, status, status.size(), size)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        return status;
    }

    public void startService() {
        waitForNonPendingState();
        if (queryStatus().dwCurrentState == 4) {
            return;
        }
        if (!Advapi32.INSTANCE.StartService(this._handle, 0, null)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        waitForNonPendingState();
        if (queryStatus().dwCurrentState != 4) {
            throw new RuntimeException("Unable to start the service");
        }
    }

    public void stopService() {
        stopService(30000L);
    }

    public void stopService(long timeout) {
        long startTime = System.currentTimeMillis();
        waitForNonPendingState();
        if (queryStatus().dwCurrentState == 1) {
            return;
        }
        Winsvc.SERVICE_STATUS status = new Winsvc.SERVICE_STATUS();
        if (!Advapi32.INSTANCE.ControlService(this._handle, 1, status)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        while (status.dwCurrentState != 1) {
            long msRemainingBeforeTimeout = timeout - (System.currentTimeMillis() - startTime);
            if (msRemainingBeforeTimeout < 0) {
                throw new RuntimeException(String.format("Service stop exceeded timeout time of %d ms", Long.valueOf(timeout)));
            }
            long dwWaitTime = Math.min(sanitizeWaitTime(status.dwWaitHint), msRemainingBeforeTimeout);
            try {
                Thread.sleep(dwWaitTime);
                if (!Advapi32.INSTANCE.QueryServiceStatus(this._handle, status)) {
                    throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void continueService() {
        waitForNonPendingState();
        if (queryStatus().dwCurrentState == 4) {
            return;
        }
        if (!Advapi32.INSTANCE.ControlService(this._handle, 3, new Winsvc.SERVICE_STATUS())) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        waitForNonPendingState();
        if (queryStatus().dwCurrentState != 4) {
            throw new RuntimeException("Unable to continue the service");
        }
    }

    public void pauseService() {
        waitForNonPendingState();
        if (queryStatus().dwCurrentState == 7) {
            return;
        }
        if (!Advapi32.INSTANCE.ControlService(this._handle, 2, new Winsvc.SERVICE_STATUS())) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        waitForNonPendingState();
        if (queryStatus().dwCurrentState != 7) {
            throw new RuntimeException("Unable to pause the service");
        }
    }

    int sanitizeWaitTime(int dwWaitHint) {
        int dwWaitTime = dwWaitHint / 10;
        if (dwWaitTime < 1000) {
            dwWaitTime = 1000;
        } else if (dwWaitTime > 10000) {
            dwWaitTime = 10000;
        }
        return dwWaitTime;
    }

    public void waitForNonPendingState() {
        Winsvc.SERVICE_STATUS_PROCESS status = queryStatus();
        int previousCheckPoint = status.dwCheckPoint;
        int checkpointStartTickCount = Kernel32.INSTANCE.GetTickCount();
        while (isPendingState(status.dwCurrentState)) {
            if (status.dwCheckPoint != previousCheckPoint) {
                previousCheckPoint = status.dwCheckPoint;
                checkpointStartTickCount = Kernel32.INSTANCE.GetTickCount();
            }
            if (Kernel32.INSTANCE.GetTickCount() - checkpointStartTickCount > status.dwWaitHint) {
                throw new RuntimeException("Timeout waiting for service to change to a non-pending state.");
            }
            int dwWaitTime = sanitizeWaitTime(status.dwWaitHint);
            try {
                Thread.sleep(dwWaitTime);
                status = queryStatus();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private boolean isPendingState(int state) {
        switch (state) {
            case 2:
            case 3:
            case 5:
            case 6:
                return true;
            case 4:
            default:
                return false;
        }
    }

    public Winsvc.SC_HANDLE getHandle() {
        return this._handle;
    }

    public Winsvc.ENUM_SERVICE_STATUS[] enumDependentServices(int dwServiceState) {
        IntByReference pcbBytesNeeded = new IntByReference(0);
        IntByReference lpServicesReturned = new IntByReference(0);
        Advapi32.INSTANCE.EnumDependentServices(this._handle, dwServiceState, Pointer.NULL, 0, pcbBytesNeeded, lpServicesReturned);
        int lastError = Kernel32.INSTANCE.GetLastError();
        if (lastError != 234) {
            throw new Win32Exception(lastError);
        }
        Memory buffer = new Memory(pcbBytesNeeded.getValue());
        boolean result = Advapi32.INSTANCE.EnumDependentServices(this._handle, dwServiceState, buffer, (int) buffer.size(), pcbBytesNeeded, lpServicesReturned);
        if (!result) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
        }
        if (lpServicesReturned.getValue() == 0) {
            return new Winsvc.ENUM_SERVICE_STATUS[0];
        }
        Winsvc.ENUM_SERVICE_STATUS status = (Winsvc.ENUM_SERVICE_STATUS) Structure.newInstance(Winsvc.ENUM_SERVICE_STATUS.class, buffer);
        status.read();
        return (Winsvc.ENUM_SERVICE_STATUS[]) status.toArray(lpServicesReturned.getValue());
    }
}
