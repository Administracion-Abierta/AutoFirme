package com.sun.jna.platform.win32;

import com.sun.jna.DefaultTypeMapper;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.EnumConverter;
import com.sun.jna.win32.StdCallLibrary;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Dxva2.class */
public interface Dxva2 extends StdCallLibrary, PhysicalMonitorEnumerationAPI, HighLevelMonitorConfigurationAPI, LowLevelMonitorConfigurationAPI {
    public static final Map<String, Object> DXVA_OPTIONS = Collections.unmodifiableMap(new HashMap<String, Object>() { // from class: com.sun.jna.platform.win32.Dxva2.1
        private static final long serialVersionUID = -1987971664975780480L;

        {
            put(Library.OPTION_TYPE_MAPPER, new DefaultTypeMapper() { // from class: com.sun.jna.platform.win32.Dxva2.1.1
                {
                    addTypeConverter(HighLevelMonitorConfigurationAPI.MC_POSITION_TYPE.class, new EnumConverter(HighLevelMonitorConfigurationAPI.MC_POSITION_TYPE.class));
                    addTypeConverter(HighLevelMonitorConfigurationAPI.MC_SIZE_TYPE.class, new EnumConverter(HighLevelMonitorConfigurationAPI.MC_SIZE_TYPE.class));
                    addTypeConverter(HighLevelMonitorConfigurationAPI.MC_GAIN_TYPE.class, new EnumConverter(HighLevelMonitorConfigurationAPI.MC_GAIN_TYPE.class));
                    addTypeConverter(HighLevelMonitorConfigurationAPI.MC_DRIVE_TYPE.class, new EnumConverter(HighLevelMonitorConfigurationAPI.MC_DRIVE_TYPE.class));
                }
            });
        }
    });
    public static final Dxva2 INSTANCE = (Dxva2) Native.load("Dxva2", Dxva2.class, DXVA_OPTIONS);

    WinDef.BOOL GetMonitorCapabilities(WinNT.HANDLE handle, WinDef.DWORDByReference dWORDByReference, WinDef.DWORDByReference dWORDByReference2);

    WinDef.BOOL SaveCurrentMonitorSettings(WinNT.HANDLE handle);

    WinDef.BOOL GetMonitorTechnologyType(WinNT.HANDLE handle, HighLevelMonitorConfigurationAPI.MC_DISPLAY_TECHNOLOGY_TYPE.ByReference byReference);

    WinDef.BOOL GetMonitorBrightness(WinNT.HANDLE handle, WinDef.DWORDByReference dWORDByReference, WinDef.DWORDByReference dWORDByReference2, WinDef.DWORDByReference dWORDByReference3);

    WinDef.BOOL GetMonitorContrast(WinNT.HANDLE handle, WinDef.DWORDByReference dWORDByReference, WinDef.DWORDByReference dWORDByReference2, WinDef.DWORDByReference dWORDByReference3);

    WinDef.BOOL GetMonitorColorTemperature(WinNT.HANDLE handle, HighLevelMonitorConfigurationAPI.MC_COLOR_TEMPERATURE.ByReference byReference);

    WinDef.BOOL GetMonitorRedGreenOrBlueDrive(WinNT.HANDLE handle, HighLevelMonitorConfigurationAPI.MC_DRIVE_TYPE mc_drive_type, WinDef.DWORDByReference dWORDByReference, WinDef.DWORDByReference dWORDByReference2, WinDef.DWORDByReference dWORDByReference3);

    WinDef.BOOL GetMonitorRedGreenOrBlueGain(WinNT.HANDLE handle, HighLevelMonitorConfigurationAPI.MC_GAIN_TYPE mc_gain_type, WinDef.DWORDByReference dWORDByReference, WinDef.DWORDByReference dWORDByReference2, WinDef.DWORDByReference dWORDByReference3);

    WinDef.BOOL SetMonitorBrightness(WinNT.HANDLE handle, int i);

    WinDef.BOOL SetMonitorContrast(WinNT.HANDLE handle, int i);

    WinDef.BOOL SetMonitorColorTemperature(WinNT.HANDLE handle, HighLevelMonitorConfigurationAPI.MC_COLOR_TEMPERATURE mc_color_temperature);

    WinDef.BOOL SetMonitorRedGreenOrBlueDrive(WinNT.HANDLE handle, HighLevelMonitorConfigurationAPI.MC_DRIVE_TYPE mc_drive_type, int i);

    WinDef.BOOL SetMonitorRedGreenOrBlueGain(WinNT.HANDLE handle, HighLevelMonitorConfigurationAPI.MC_GAIN_TYPE mc_gain_type, int i);

    WinDef.BOOL DegaussMonitor(WinNT.HANDLE handle);

    WinDef.BOOL GetMonitorDisplayAreaSize(WinNT.HANDLE handle, HighLevelMonitorConfigurationAPI.MC_SIZE_TYPE mc_size_type, WinDef.DWORDByReference dWORDByReference, WinDef.DWORDByReference dWORDByReference2, WinDef.DWORDByReference dWORDByReference3);

    WinDef.BOOL GetMonitorDisplayAreaPosition(WinNT.HANDLE handle, HighLevelMonitorConfigurationAPI.MC_POSITION_TYPE mc_position_type, WinDef.DWORDByReference dWORDByReference, WinDef.DWORDByReference dWORDByReference2, WinDef.DWORDByReference dWORDByReference3);

    WinDef.BOOL SetMonitorDisplayAreaSize(WinNT.HANDLE handle, HighLevelMonitorConfigurationAPI.MC_SIZE_TYPE mc_size_type, int i);

    WinDef.BOOL SetMonitorDisplayAreaPosition(WinNT.HANDLE handle, HighLevelMonitorConfigurationAPI.MC_POSITION_TYPE mc_position_type, int i);

    WinDef.BOOL RestoreMonitorFactoryColorDefaults(WinNT.HANDLE handle);

    WinDef.BOOL RestoreMonitorFactoryDefaults(WinNT.HANDLE handle);

    WinDef.BOOL GetVCPFeatureAndVCPFeatureReply(WinNT.HANDLE handle, WinDef.BYTE r2, LowLevelMonitorConfigurationAPI.MC_VCP_CODE_TYPE.ByReference byReference, WinDef.DWORDByReference dWORDByReference, WinDef.DWORDByReference dWORDByReference2);

    WinDef.BOOL SetVCPFeature(WinNT.HANDLE handle, WinDef.BYTE r2, WinDef.DWORD dword);

    WinDef.BOOL SaveCurrentSettings(WinNT.HANDLE handle);

    WinDef.BOOL GetCapabilitiesStringLength(WinNT.HANDLE handle, WinDef.DWORDByReference dWORDByReference);

    WinDef.BOOL CapabilitiesRequestAndCapabilitiesReply(WinNT.HANDLE handle, WTypes.LPSTR lpstr, WinDef.DWORD dword);

    WinDef.BOOL GetTimingReport(WinNT.HANDLE handle, LowLevelMonitorConfigurationAPI.MC_TIMING_REPORT mc_timing_report);

    WinDef.BOOL GetNumberOfPhysicalMonitorsFromHMONITOR(WinUser.HMONITOR hmonitor, WinDef.DWORDByReference dWORDByReference);

    WinDef.BOOL GetPhysicalMonitorsFromHMONITOR(WinUser.HMONITOR hmonitor, int i, PhysicalMonitorEnumerationAPI.PHYSICAL_MONITOR[] physical_monitorArr);

    WinDef.BOOL DestroyPhysicalMonitor(WinNT.HANDLE handle);

    WinDef.BOOL DestroyPhysicalMonitors(int i, PhysicalMonitorEnumerationAPI.PHYSICAL_MONITOR[] physical_monitorArr);
}
