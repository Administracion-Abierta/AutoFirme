package com.ctc.wstx.api;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/api/ValidatorConfig.class */
public final class ValidatorConfig extends CommonConfig {
    static final ValidatorConfig sInstance = new ValidatorConfig(null);

    @Override // com.ctc.wstx.api.CommonConfig
    public /* bridge */ /* synthetic */ boolean returnNullForDefaultNamespace() {
        return super.returnNullForDefaultNamespace();
    }

    @Override // com.ctc.wstx.api.CommonConfig
    public /* bridge */ /* synthetic */ boolean doesSupportXmlId() {
        return super.doesSupportXmlId();
    }

    @Override // com.ctc.wstx.api.CommonConfig
    public /* bridge */ /* synthetic */ boolean doesSupportXml11() {
        return super.doesSupportXml11();
    }

    @Override // com.ctc.wstx.api.CommonConfig
    public /* bridge */ /* synthetic */ boolean setProperty(String str, Object obj) {
        return super.setProperty(str, obj);
    }

    @Override // com.ctc.wstx.api.CommonConfig
    public /* bridge */ /* synthetic */ boolean isPropertySupported(String str) {
        return super.isPropertySupported(str);
    }

    @Override // com.ctc.wstx.api.CommonConfig
    public /* bridge */ /* synthetic */ Object getProperty(String str) {
        return super.getProperty(str);
    }

    private ValidatorConfig(ValidatorConfig base) {
        super(base);
    }

    public static ValidatorConfig createDefaults() {
        return sInstance;
    }

    @Override // com.ctc.wstx.api.CommonConfig
    protected int findPropertyId(String propName) {
        return -1;
    }

    @Override // com.ctc.wstx.api.CommonConfig
    protected Object getProperty(int id) {
        return null;
    }

    @Override // com.ctc.wstx.api.CommonConfig
    protected boolean setProperty(String propName, int id, Object value) {
        return false;
    }
}
