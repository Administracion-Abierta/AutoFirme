package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.StringArray;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.win32.W32APITypeMapper;
import org.apache.pdfbox.pdmodel.documentinterchange.taggedpdf.PDLayoutAttributeObject;

/* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Winevt.class */
public interface Winevt {
    public static final int EVT_VARIANT_TYPE_ARRAY = 128;
    public static final int EVT_VARIANT_TYPE_MASK = 127;
    public static final int EVT_READ_ACCESS = 1;
    public static final int EVT_WRITE_ACCESS = 2;
    public static final int EVT_ALL_ACCESS = 7;
    public static final int EVT_CLEAR_ACCESS = 4;

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Winevt$EVT_CHANNEL_CLOCK_TYPE.class */
    public interface EVT_CHANNEL_CLOCK_TYPE {
        public static final int EvtChannelClockTypeSystemTime = 0;
        public static final int EvtChannelClockTypeQPC = 1;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Winevt$EVT_CHANNEL_CONFIG_PROPERTY_ID.class */
    public interface EVT_CHANNEL_CONFIG_PROPERTY_ID {
        public static final int EvtChannelConfigEnabled = 0;
        public static final int EvtChannelConfigIsolation = 1;
        public static final int EvtChannelConfigType = 2;
        public static final int EvtChannelConfigOwningPublisher = 3;
        public static final int EvtChannelConfigClassicEventlog = 4;
        public static final int EvtChannelConfigAccess = 5;
        public static final int EvtChannelLoggingConfigRetention = 6;
        public static final int EvtChannelLoggingConfigAutoBackup = 7;
        public static final int EvtChannelLoggingConfigMaxSize = 8;
        public static final int EvtChannelLoggingConfigLogFilePath = 9;
        public static final int EvtChannelPublishingConfigLevel = 10;
        public static final int EvtChannelPublishingConfigKeywords = 11;
        public static final int EvtChannelPublishingConfigControlGuid = 12;
        public static final int EvtChannelPublishingConfigBufferSize = 13;
        public static final int EvtChannelPublishingConfigMinBuffers = 14;
        public static final int EvtChannelPublishingConfigMaxBuffers = 15;
        public static final int EvtChannelPublishingConfigLatency = 16;
        public static final int EvtChannelPublishingConfigClockType = 17;
        public static final int EvtChannelPublishingConfigSidType = 18;
        public static final int EvtChannelPublisherList = 19;
        public static final int EvtChannelPublishingConfigFileMax = 20;
        public static final int EvtChannelConfigPropertyIdEND = 21;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Winevt$EVT_CHANNEL_ISOLATION_TYPE.class */
    public interface EVT_CHANNEL_ISOLATION_TYPE {
        public static final int EvtChannelIsolationTypeApplication = 0;
        public static final int EvtChannelIsolationTypeSystem = 1;
        public static final int EvtChannelIsolationTypeCustom = 2;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Winevt$EVT_CHANNEL_REFERENCE_FLAGS.class */
    public interface EVT_CHANNEL_REFERENCE_FLAGS {
        public static final int EvtChannelReferenceImported = 1;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Winevt$EVT_CHANNEL_SID_TYPE.class */
    public interface EVT_CHANNEL_SID_TYPE {
        public static final int EvtChannelSidTypeNone = 0;
        public static final int EvtChannelSidTypePublishing = 1;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Winevt$EVT_CHANNEL_TYPE.class */
    public interface EVT_CHANNEL_TYPE {
        public static final int EvtChannelTypeAdmin = 0;
        public static final int EvtChannelTypeOperational = 1;
        public static final int EvtChannelTypeAnalytic = 2;
        public static final int EvtChannelTypeDebug = 3;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Winevt$EVT_EVENT_METADATA_PROPERTY_ID.class */
    public interface EVT_EVENT_METADATA_PROPERTY_ID {
        public static final int EventMetadataEventID = 0;
        public static final int EventMetadataEventVersion = 1;
        public static final int EventMetadataEventChannel = 2;
        public static final int EventMetadataEventLevel = 3;
        public static final int EventMetadataEventOpcode = 4;
        public static final int EventMetadataEventTask = 5;
        public static final int EventMetadataEventKeyword = 6;
        public static final int EventMetadataEventMessageID = 7;
        public static final int EventMetadataEventTemplate = 8;
        public static final int EvtEventMetadataPropertyIdEND = 9;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Winevt$EVT_EVENT_PROPERTY_ID.class */
    public interface EVT_EVENT_PROPERTY_ID {
        public static final int EvtEventQueryIDs = 0;
        public static final int EvtEventPath = 1;
        public static final int EvtEventPropertyIdEND = 2;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Winevt$EVT_EXPORTLOG_FLAGS.class */
    public interface EVT_EXPORTLOG_FLAGS {
        public static final int EvtExportLogChannelPath = 1;
        public static final int EvtExportLogFilePath = 2;
        public static final int EvtExportLogTolerateQueryErrors = 4096;
        public static final int EvtExportLogOverwrite = 8192;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Winevt$EVT_FORMAT_MESSAGE_FLAGS.class */
    public interface EVT_FORMAT_MESSAGE_FLAGS {
        public static final int EvtFormatMessageEvent = 1;
        public static final int EvtFormatMessageLevel = 2;
        public static final int EvtFormatMessageTask = 3;
        public static final int EvtFormatMessageOpcode = 4;
        public static final int EvtFormatMessageKeyword = 5;
        public static final int EvtFormatMessageChannel = 6;
        public static final int EvtFormatMessageProvider = 7;
        public static final int EvtFormatMessageId = 8;
        public static final int EvtFormatMessageXml = 9;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Winevt$EVT_LOGIN_CLASS.class */
    public interface EVT_LOGIN_CLASS {
        public static final int EvtRpcLogin = 1;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Winevt$EVT_LOG_PROPERTY_ID.class */
    public interface EVT_LOG_PROPERTY_ID {
        public static final int EvtLogCreationTime = 0;
        public static final int EvtLogLastAccessTime = 1;
        public static final int EvtLogLastWriteTime = 2;
        public static final int EvtLogFileSize = 3;
        public static final int EvtLogAttributes = 4;
        public static final int EvtLogNumberOfLogRecords = 5;
        public static final int EvtLogOldestRecordNumber = 6;
        public static final int EvtLogFull = 7;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Winevt$EVT_OPEN_LOG_FLAGS.class */
    public interface EVT_OPEN_LOG_FLAGS {
        public static final int EvtOpenChannelPath = 1;
        public static final int EvtOpenFilePath = 2;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Winevt$EVT_PUBLISHER_METADATA_PROPERTY_ID.class */
    public interface EVT_PUBLISHER_METADATA_PROPERTY_ID {
        public static final int EvtPublisherMetadataPublisherGuid = 0;
        public static final int EvtPublisherMetadataResourceFilePath = 1;
        public static final int EvtPublisherMetadataParameterFilePath = 2;
        public static final int EvtPublisherMetadataMessageFilePath = 3;
        public static final int EvtPublisherMetadataHelpLink = 4;
        public static final int EvtPublisherMetadataPublisherMessageID = 5;
        public static final int EvtPublisherMetadataChannelReferences = 6;
        public static final int EvtPublisherMetadataChannelReferencePath = 7;
        public static final int EvtPublisherMetadataChannelReferenceIndex = 8;
        public static final int EvtPublisherMetadataChannelReferenceID = 9;
        public static final int EvtPublisherMetadataChannelReferenceFlags = 10;
        public static final int EvtPublisherMetadataChannelReferenceMessageID = 11;
        public static final int EvtPublisherMetadataLevels = 12;
        public static final int EvtPublisherMetadataLevelName = 13;
        public static final int EvtPublisherMetadataLevelValue = 14;
        public static final int EvtPublisherMetadataLevelMessageID = 15;
        public static final int EvtPublisherMetadataTasks = 16;
        public static final int EvtPublisherMetadataTaskName = 17;
        public static final int EvtPublisherMetadataTaskEventGuid = 18;
        public static final int EvtPublisherMetadataTaskValue = 19;
        public static final int EvtPublisherMetadataTaskMessageID = 20;
        public static final int EvtPublisherMetadataOpcodes = 21;
        public static final int EvtPublisherMetadataOpcodeName = 22;
        public static final int EvtPublisherMetadataOpcodeValue = 23;
        public static final int EvtPublisherMetadataOpcodeMessageID = 24;
        public static final int EvtPublisherMetadataKeywords = 25;
        public static final int EvtPublisherMetadataKeywordName = 26;
        public static final int EvtPublisherMetadataKeywordValue = 27;
        public static final int EvtPublisherMetadataKeywordMessageID = 28;
        public static final int EvtPublisherMetadataPropertyIdEND = 29;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Winevt$EVT_QUERY_FLAGS.class */
    public interface EVT_QUERY_FLAGS {
        public static final int EvtQueryChannelPath = 1;
        public static final int EvtQueryFilePath = 2;
        public static final int EvtQueryForwardDirection = 256;
        public static final int EvtQueryReverseDirection = 512;
        public static final int EvtQueryTolerateQueryErrors = 4096;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Winevt$EVT_QUERY_PROPERTY_ID.class */
    public interface EVT_QUERY_PROPERTY_ID {
        public static final int EvtQueryNames = 0;
        public static final int EvtQueryStatuses = 1;
        public static final int EvtQueryPropertyIdEND = 2;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Winevt$EVT_RENDER_CONTEXT_FLAGS.class */
    public interface EVT_RENDER_CONTEXT_FLAGS {
        public static final int EvtRenderContextValues = 0;
        public static final int EvtRenderContextSystem = 1;
        public static final int EvtRenderContextUser = 2;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Winevt$EVT_RENDER_FLAGS.class */
    public interface EVT_RENDER_FLAGS {
        public static final int EvtRenderEventValues = 0;
        public static final int EvtRenderEventXml = 1;
        public static final int EvtRenderBookmark = 2;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Winevt$EVT_RPC_LOGIN_FLAGS.class */
    public interface EVT_RPC_LOGIN_FLAGS {
        public static final int EvtRpcLoginAuthDefault = 0;
        public static final int EvtRpcLoginAuthNegotiate = 1;
        public static final int EvtRpcLoginAuthKerberos = 2;
        public static final int EvtRpcLoginAuthNTLM = 3;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Winevt$EVT_SEEK_FLAGS.class */
    public interface EVT_SEEK_FLAGS {
        public static final int EvtSeekRelativeToFirst = 1;
        public static final int EvtSeekRelativeToLast = 2;
        public static final int EvtSeekRelativeToCurrent = 3;
        public static final int EvtSeekRelativeToBookmark = 4;
        public static final int EvtSeekOriginMask = 7;
        public static final int EvtSeekStrict = 65536;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Winevt$EVT_SUBSCRIBE_FLAGS.class */
    public interface EVT_SUBSCRIBE_FLAGS {
        public static final int EvtSubscribeToFutureEvents = 1;
        public static final int EvtSubscribeStartAtOldestRecord = 2;
        public static final int EvtSubscribeStartAfterBookmark = 3;
        public static final int EvtSubscribeOriginMask = 3;
        public static final int EvtSubscribeTolerateQueryErrors = 4096;
        public static final int EvtSubscribeStrict = 65536;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Winevt$EVT_SUBSCRIBE_NOTIFY_ACTION.class */
    public interface EVT_SUBSCRIBE_NOTIFY_ACTION {
        public static final int EvtSubscribeActionError = 0;
        public static final int EvtSubscribeActionDeliver = 1;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Winevt$EVT_SYSTEM_PROPERTY_ID.class */
    public interface EVT_SYSTEM_PROPERTY_ID {
        public static final int EvtSystemProviderName = 0;
        public static final int EvtSystemProviderGuid = 1;
        public static final int EvtSystemEventID = 2;
        public static final int EvtSystemQualifiers = 3;
        public static final int EvtSystemLevel = 4;
        public static final int EvtSystemTask = 5;
        public static final int EvtSystemOpcode = 6;
        public static final int EvtSystemKeywords = 7;
        public static final int EvtSystemTimeCreated = 8;
        public static final int EvtSystemEventRecordId = 9;
        public static final int EvtSystemActivityID = 10;
        public static final int EvtSystemRelatedActivityID = 11;
        public static final int EvtSystemProcessID = 12;
        public static final int EvtSystemThreadID = 13;
        public static final int EvtSystemChannel = 14;
        public static final int EvtSystemComputer = 15;
        public static final int EvtSystemUserID = 16;
        public static final int EvtSystemVersion = 17;
        public static final int EvtSystemPropertyIdEND = 18;
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Winevt$EVT_VARIANT_TYPE.class */
    public enum EVT_VARIANT_TYPE {
        EvtVarTypeNull(""),
        EvtVarTypeString("String"),
        EvtVarTypeAnsiString("AnsiString"),
        EvtVarTypeSByte("SByte"),
        EvtVarTypeByte("Byte"),
        EvtVarTypeInt16("Int16"),
        EvtVarTypeUInt16("UInt16"),
        EvtVarTypeInt32("Int32"),
        EvtVarTypeUInt32("UInt32"),
        EvtVarTypeInt64("Int64"),
        EvtVarTypeUInt64("UInt64"),
        EvtVarTypeSingle("Single"),
        EvtVarTypeDouble(PDLayoutAttributeObject.BORDER_STYLE_DOUBLE),
        EvtVarTypeBoolean("Boolean"),
        EvtVarTypeBinary("Binary"),
        EvtVarTypeGuid("Guid"),
        EvtVarTypeSizeT("SizeT"),
        EvtVarTypeFileTime("FileTime"),
        EvtVarTypeSysTime("SysTime"),
        EvtVarTypeSid("Sid"),
        EvtVarTypeHexInt32("Int32"),
        EvtVarTypeHexInt64("Int64"),
        EvtVarTypeEvtHandle("EvtHandle"),
        EvtVarTypeEvtXml("Xml");

        private final String field;

        EVT_VARIANT_TYPE(String field) {
            this.field = field;
        }

        public String getField() {
            return this.field.isEmpty() ? "" : this.field + "Val";
        }

        public String getArrField() {
            return this.field.isEmpty() ? "" : this.field + "Arr";
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Winevt$EVT_VARIANT.class */
    @Structure.FieldOrder({"field1", "Count", "Type"})
    public static class EVT_VARIANT extends Structure {
        public field1_union field1;
        public int Count;
        public int Type;
        private Object holder;
        static final /* synthetic */ boolean $assertionsDisabled;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Winevt$EVT_VARIANT$field1_union.class */
        public static class field1_union extends Union {
            public byte byteValue;
            public short shortValue;
            public int intValue;
            public long longValue;
            public float floatValue;
            public double doubleVal;
            public Pointer pointerValue;
        }

        static {
            $assertionsDisabled = !Winevt.class.desiredAssertionStatus();
        }

        public EVT_VARIANT() {
            super(W32APITypeMapper.DEFAULT);
        }

        public EVT_VARIANT(Pointer peer) {
            super(peer, 0, W32APITypeMapper.DEFAULT);
        }

        public void use(Pointer m) {
            useMemory(m, 0);
        }

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Winevt$EVT_VARIANT$ByReference.class */
        public static class ByReference extends EVT_VARIANT implements Structure.ByReference {
            public ByReference(Pointer p) {
                super(p);
            }

            public ByReference() {
            }
        }

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Winevt$EVT_VARIANT$ByValue.class */
        public static class ByValue extends EVT_VARIANT implements Structure.ByValue {
            public ByValue(Pointer p) {
                super(p);
            }

            public ByValue() {
            }
        }

        private int getBaseType() {
            return this.Type & 127;
        }

        public boolean isArray() {
            return (this.Type & 128) == 128;
        }

        public EVT_VARIANT_TYPE getVariantType() {
            return EVT_VARIANT_TYPE.values()[getBaseType()];
        }

        public void setValue(EVT_VARIANT_TYPE type, Object value) {
            allocateMemory();
            if (type == null) {
                throw new IllegalArgumentException("setValue must not be called with type set to NULL");
            }
            this.holder = null;
            if (value == null || type == EVT_VARIANT_TYPE.EvtVarTypeNull) {
                this.Type = EVT_VARIANT_TYPE.EvtVarTypeNull.ordinal();
                this.Count = 0;
                this.field1.writeField("pointerValue", Pointer.NULL);
            } else {
                switch (type) {
                    case EvtVarTypeAnsiString:
                        if (value.getClass().isArray() && value.getClass().getComponentType() == String.class) {
                            this.Type = type.ordinal() | 128;
                            StringArray sa = new StringArray((String[]) value, false);
                            this.holder = sa;
                            this.Count = ((String[]) value).length;
                            this.field1.writeField("pointerValue", sa);
                        } else if (value.getClass() == String.class) {
                            this.Type = type.ordinal();
                            Memory mem = new Memory(((String) value).length() + 1);
                            mem.setString(0L, (String) value);
                            this.holder = mem;
                            this.Count = 0;
                            this.field1.writeField("pointerValue", mem);
                        } else {
                            throw new IllegalArgumentException(type.name() + " must be set from String/String[]");
                        }
                        break;
                    case EvtVarTypeBoolean:
                        if (value.getClass().isArray() && value.getClass().getComponentType() == WinDef.BOOL.class) {
                            this.Type = type.ordinal() | 128;
                            Memory mem2 = new Memory(((WinDef.BOOL[]) value).length * 4);
                            for (int i = 0; i < ((WinDef.BOOL[]) value).length; i++) {
                                mem2.setInt(i * 4, ((WinDef.BOOL[]) value)[i].intValue());
                            }
                            this.holder = mem2;
                            this.Count = 0;
                            this.field1.writeField("pointerValue", mem2);
                        } else if (value.getClass() == WinDef.BOOL.class) {
                            this.Type = type.ordinal();
                            this.Count = 0;
                            this.field1.writeField("intValue", Integer.valueOf(((WinDef.BOOL) value).intValue()));
                        } else {
                            throw new IllegalArgumentException(type.name() + " must be set from BOOL/BOOL[]");
                        }
                        break;
                    case EvtVarTypeString:
                    case EvtVarTypeEvtXml:
                        if (value.getClass().isArray() && value.getClass().getComponentType() == String.class) {
                            this.Type = type.ordinal() | 128;
                            StringArray sa2 = new StringArray((String[]) value, true);
                            this.holder = sa2;
                            this.Count = ((String[]) value).length;
                            this.field1.writeField("pointerValue", sa2);
                        } else if (value.getClass() == String.class) {
                            this.Type = type.ordinal();
                            Memory mem3 = new Memory((((String) value).length() + 1) * 2);
                            mem3.setWideString(0L, (String) value);
                            this.holder = mem3;
                            this.Count = 0;
                            this.field1.writeField("pointerValue", mem3);
                        } else {
                            throw new IllegalArgumentException(type.name() + " must be set from String/String[]");
                        }
                        break;
                    case EvtVarTypeSByte:
                    case EvtVarTypeByte:
                        if (value.getClass().isArray() && value.getClass().getComponentType() == Byte.TYPE) {
                            this.Type = type.ordinal() | 128;
                            Memory mem4 = new Memory(((byte[]) value).length * 1);
                            mem4.write(0L, (byte[]) value, 0, ((byte[]) value).length);
                            this.holder = mem4;
                            this.Count = 0;
                            this.field1.writeField("pointerValue", mem4);
                        } else if (value.getClass() == Byte.TYPE) {
                            this.Type = type.ordinal();
                            this.Count = 0;
                            this.field1.writeField("byteValue", value);
                        } else {
                            throw new IllegalArgumentException(type.name() + " must be set from byte/byte[]");
                        }
                        break;
                    case EvtVarTypeInt16:
                    case EvtVarTypeUInt16:
                        if (value.getClass().isArray() && value.getClass().getComponentType() == Short.TYPE) {
                            this.Type = type.ordinal() | 128;
                            Memory mem5 = new Memory(((short[]) value).length * 2);
                            mem5.write(0L, (short[]) value, 0, ((short[]) value).length);
                            this.holder = mem5;
                            this.Count = 0;
                            this.field1.writeField("pointerValue", mem5);
                        } else if (value.getClass() == Short.TYPE) {
                            this.Type = type.ordinal();
                            this.Count = 0;
                            this.field1.writeField("shortValue", value);
                        } else {
                            throw new IllegalArgumentException(type.name() + " must be set from short/short[]");
                        }
                        break;
                    case EvtVarTypeHexInt32:
                    case EvtVarTypeInt32:
                    case EvtVarTypeUInt32:
                        if (value.getClass().isArray() && value.getClass().getComponentType() == Integer.TYPE) {
                            this.Type = type.ordinal() | 128;
                            Memory mem6 = new Memory(((int[]) value).length * 4);
                            mem6.write(0L, (int[]) value, 0, ((int[]) value).length);
                            this.holder = mem6;
                            this.Count = 0;
                            this.field1.writeField("pointerValue", mem6);
                        } else if (value.getClass() == Integer.TYPE) {
                            this.Type = type.ordinal();
                            this.Count = 0;
                            this.field1.writeField("intValue", value);
                        } else {
                            throw new IllegalArgumentException(type.name() + " must be set from int/int[]");
                        }
                        break;
                    case EvtVarTypeHexInt64:
                    case EvtVarTypeInt64:
                    case EvtVarTypeUInt64:
                        if (value.getClass().isArray() && value.getClass().getComponentType() == Long.TYPE) {
                            this.Type = type.ordinal() | 128;
                            Memory mem7 = new Memory(((long[]) value).length * 4);
                            mem7.write(0L, (long[]) value, 0, ((long[]) value).length);
                            this.holder = mem7;
                            this.Count = 0;
                            this.field1.writeField("pointerValue", mem7);
                        } else if (value.getClass() == Long.TYPE) {
                            this.Type = type.ordinal();
                            this.Count = 0;
                            this.field1.writeField("longValue", value);
                        } else {
                            throw new IllegalArgumentException(type.name() + " must be set from long/long[]");
                        }
                        break;
                    case EvtVarTypeSingle:
                        if (value.getClass().isArray() && value.getClass().getComponentType() == Float.TYPE) {
                            this.Type = type.ordinal() | 128;
                            Memory mem8 = new Memory(((float[]) value).length * 4);
                            mem8.write(0L, (float[]) value, 0, ((float[]) value).length);
                            this.holder = mem8;
                            this.Count = 0;
                            this.field1.writeField("pointerValue", mem8);
                        } else if (value.getClass() == Float.TYPE) {
                            this.Type = type.ordinal();
                            this.Count = 0;
                            this.field1.writeField("floatValue", value);
                        } else {
                            throw new IllegalArgumentException(type.name() + " must be set from float/float[]");
                        }
                        break;
                    case EvtVarTypeDouble:
                        if (value.getClass().isArray() && value.getClass().getComponentType() == Double.TYPE) {
                            this.Type = type.ordinal() | 128;
                            Memory mem9 = new Memory(((double[]) value).length * 4);
                            mem9.write(0L, (double[]) value, 0, ((double[]) value).length);
                            this.holder = mem9;
                            this.Count = 0;
                            this.field1.writeField("pointerValue", mem9);
                        } else if (value.getClass() == Double.TYPE) {
                            this.Type = type.ordinal();
                            this.Count = 0;
                            this.field1.writeField("doubleVal", value);
                        } else {
                            throw new IllegalArgumentException(type.name() + " must be set from double/double[]");
                        }
                        break;
                    case EvtVarTypeBinary:
                        if (value.getClass().isArray() && value.getClass().getComponentType() == Byte.TYPE) {
                            this.Type = type.ordinal();
                            Memory mem10 = new Memory(((byte[]) value).length * 1);
                            mem10.write(0L, (byte[]) value, 0, ((byte[]) value).length);
                            this.holder = mem10;
                            this.Count = 0;
                            this.field1.writeField("pointerValue", mem10);
                        } else {
                            throw new IllegalArgumentException(type.name() + " must be set from byte[]");
                        }
                        break;
                    case EvtVarTypeFileTime:
                    case EvtVarTypeEvtHandle:
                    case EvtVarTypeSysTime:
                    case EvtVarTypeGuid:
                    case EvtVarTypeSid:
                    case EvtVarTypeSizeT:
                    default:
                        throw new IllegalStateException(String.format("NOT IMPLEMENTED: getValue(%s) (Array: %b, Count: %d)", type, Boolean.valueOf(isArray()), Integer.valueOf(this.Count)));
                }
            }
            write();
        }

        public Object getValue() {
            EVT_VARIANT_TYPE type = getVariantType();
            switch (type) {
                case EvtVarTypeAnsiString:
                    return isArray() ? this.field1.getPointer().getPointer(0L).getStringArray(0L, this.Count) : this.field1.getPointer().getPointer(0L).getString(0L);
                case EvtVarTypeBoolean:
                    if (isArray()) {
                        int[] rawValue = this.field1.getPointer().getPointer(0L).getIntArray(0L, this.Count);
                        WinDef.BOOL[] result = new WinDef.BOOL[rawValue.length];
                        for (int i = 0; i < result.length; i++) {
                            result[i] = new WinDef.BOOL(rawValue[i]);
                        }
                        return result;
                    }
                    return new WinDef.BOOL(this.field1.getPointer().getInt(0L));
                case EvtVarTypeString:
                case EvtVarTypeEvtXml:
                    return isArray() ? this.field1.getPointer().getPointer(0L).getWideStringArray(0L, this.Count) : this.field1.getPointer().getPointer(0L).getWideString(0L);
                case EvtVarTypeSByte:
                case EvtVarTypeByte:
                    return isArray() ? this.field1.getPointer().getPointer(0L).getByteArray(0L, this.Count) : Byte.valueOf(this.field1.getPointer().getByte(0L));
                case EvtVarTypeInt16:
                case EvtVarTypeUInt16:
                    return isArray() ? this.field1.getPointer().getPointer(0L).getShortArray(0L, this.Count) : Short.valueOf(this.field1.getPointer().getShort(0L));
                case EvtVarTypeHexInt32:
                case EvtVarTypeInt32:
                case EvtVarTypeUInt32:
                    return isArray() ? this.field1.getPointer().getPointer(0L).getIntArray(0L, this.Count) : Integer.valueOf(this.field1.getPointer().getInt(0L));
                case EvtVarTypeHexInt64:
                case EvtVarTypeInt64:
                case EvtVarTypeUInt64:
                    return isArray() ? this.field1.getPointer().getPointer(0L).getLongArray(0L, this.Count) : Long.valueOf(this.field1.getPointer().getLong(0L));
                case EvtVarTypeSingle:
                    return isArray() ? this.field1.getPointer().getPointer(0L).getFloatArray(0L, this.Count) : Float.valueOf(this.field1.getPointer().getFloat(0L));
                case EvtVarTypeDouble:
                    return isArray() ? this.field1.getPointer().getPointer(0L).getDoubleArray(0L, this.Count) : Double.valueOf(this.field1.getPointer().getDouble(0L));
                case EvtVarTypeBinary:
                    if ($assertionsDisabled || !isArray()) {
                        return this.field1.getPointer().getPointer(0L).getByteArray(0L, this.Count);
                    }
                    throw new AssertionError();
                case EvtVarTypeFileTime:
                    if (isArray()) {
                        WinBase.FILETIME resultFirst = (WinBase.FILETIME) Structure.newInstance(WinBase.FILETIME.class, this.field1.getPointer().getPointer(0L));
                        resultFirst.read();
                        return resultFirst.toArray(this.Count);
                    }
                    WinBase.FILETIME result2 = new WinBase.FILETIME(this.field1.getPointer());
                    result2.read();
                    return result2;
                case EvtVarTypeEvtHandle:
                    if (isArray()) {
                        Pointer[] rawValue2 = this.field1.getPointer().getPointer(0L).getPointerArray(0L, this.Count);
                        WinNT.HANDLE[] result3 = new WinNT.HANDLE[rawValue2.length];
                        for (int i2 = 0; i2 < result3.length; i2++) {
                            result3[i2] = new WinNT.HANDLE(rawValue2[i2]);
                        }
                        return result3;
                    }
                    return new WinNT.HANDLE(this.field1.getPointer().getPointer(0L));
                case EvtVarTypeSysTime:
                    if (isArray()) {
                        WinBase.SYSTEMTIME resultFirst2 = (WinBase.SYSTEMTIME) Structure.newInstance(WinBase.SYSTEMTIME.class, this.field1.getPointer().getPointer(0L));
                        resultFirst2.read();
                        return resultFirst2.toArray(this.Count);
                    }
                    WinBase.SYSTEMTIME result4 = (WinBase.SYSTEMTIME) Structure.newInstance(WinBase.SYSTEMTIME.class, this.field1.getPointer().getPointer(0L));
                    result4.read();
                    return result4;
                case EvtVarTypeGuid:
                    if (isArray()) {
                        Guid.GUID resultFirst3 = (Guid.GUID) Structure.newInstance(Guid.GUID.class, this.field1.getPointer().getPointer(0L));
                        resultFirst3.read();
                        return resultFirst3.toArray(this.Count);
                    }
                    Guid.GUID result5 = (Guid.GUID) Structure.newInstance(Guid.GUID.class, this.field1.getPointer().getPointer(0L));
                    result5.read();
                    return result5;
                case EvtVarTypeSid:
                    if (isArray()) {
                        WinNT.PSID resultFirst4 = (WinNT.PSID) Structure.newInstance(WinNT.PSID.class, this.field1.getPointer().getPointer(0L));
                        resultFirst4.read();
                        return resultFirst4.toArray(this.Count);
                    }
                    WinNT.PSID result6 = (WinNT.PSID) Structure.newInstance(WinNT.PSID.class, this.field1.getPointer().getPointer(0L));
                    result6.read();
                    return result6;
                case EvtVarTypeSizeT:
                    if (isArray()) {
                        long[] rawValue3 = this.field1.getPointer().getPointer(0L).getLongArray(0L, this.Count);
                        BaseTSD.SIZE_T[] result7 = new BaseTSD.SIZE_T[rawValue3.length];
                        for (int i3 = 0; i3 < result7.length; i3++) {
                            result7[i3] = new BaseTSD.SIZE_T(rawValue3[i3]);
                        }
                        return result7;
                    }
                    return new BaseTSD.SIZE_T(this.field1.getPointer().getLong(0L));
                case EvtVarTypeNull:
                    return null;
                default:
                    throw new IllegalStateException(String.format("NOT IMPLEMENTED: getValue(%s) (Array: %b, Count: %d)", type, Boolean.valueOf(isArray()), Integer.valueOf(this.Count)));
            }
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Winevt$EVT_RPC_LOGIN.class */
    @Structure.FieldOrder({"Server", "User", "Domain", "Password", "Flags"})
    public static class EVT_RPC_LOGIN extends Structure {
        public String Server;
        public String User;
        public String Domain;
        public String Password;
        public int Flags;

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Winevt$EVT_RPC_LOGIN$ByReference.class */
        public static class ByReference extends EVT_RPC_LOGIN implements Structure.ByReference {
        }

        /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Winevt$EVT_RPC_LOGIN$ByValue.class */
        public static class ByValue extends EVT_RPC_LOGIN implements Structure.ByValue {
        }

        public EVT_RPC_LOGIN() {
            super(W32APITypeMapper.UNICODE);
        }

        public EVT_RPC_LOGIN(String Server, String User, String Domain, String Password, int Flags) {
            super(W32APITypeMapper.UNICODE);
            this.Server = Server;
            this.User = User;
            this.Domain = Domain;
            this.Password = Password;
            this.Flags = Flags;
        }

        public EVT_RPC_LOGIN(Pointer peer) {
            super(peer, 0, W32APITypeMapper.UNICODE);
        }
    }

    /* JADX INFO: loaded from: autofirma.jar:com/sun/jna/platform/win32/Winevt$EVT_HANDLE.class */
    public static class EVT_HANDLE extends WinNT.HANDLE {
        public EVT_HANDLE() {
        }

        public EVT_HANDLE(Pointer p) {
            super(p);
        }
    }
}
