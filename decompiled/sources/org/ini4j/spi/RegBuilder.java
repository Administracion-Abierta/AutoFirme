package org.ini4j.spi;

import org.ini4j.Config;
import org.ini4j.Profile;
import org.ini4j.Reg;
import org.ini4j.Registry;

/* JADX INFO: loaded from: autofirma.jar:org/ini4j/spi/RegBuilder.class */
public class RegBuilder extends AbstractProfileBuilder {
    private Reg _reg;

    @Override // org.ini4j.spi.AbstractProfileBuilder, org.ini4j.spi.IniHandler
    public /* bridge */ /* synthetic */ void startSection(String x0) {
        super.startSection(x0);
    }

    @Override // org.ini4j.spi.AbstractProfileBuilder, org.ini4j.spi.IniHandler
    public /* bridge */ /* synthetic */ void startIni() {
        super.startIni();
    }

    @Override // org.ini4j.spi.AbstractProfileBuilder, org.ini4j.spi.IniHandler, org.ini4j.spi.HandlerBase
    public /* bridge */ /* synthetic */ void handleComment(String x0) {
        super.handleComment(x0);
    }

    @Override // org.ini4j.spi.AbstractProfileBuilder, org.ini4j.spi.IniHandler
    public /* bridge */ /* synthetic */ void endSection() {
        super.endSection();
    }

    @Override // org.ini4j.spi.AbstractProfileBuilder, org.ini4j.spi.IniHandler
    public /* bridge */ /* synthetic */ void endIni() {
        super.endIni();
    }

    public static RegBuilder newInstance(Reg reg) {
        RegBuilder instance = newInstance();
        instance.setReg(reg);
        return instance;
    }

    public void setReg(Reg value) {
        this._reg = value;
    }

    @Override // org.ini4j.spi.AbstractProfileBuilder, org.ini4j.spi.IniHandler, org.ini4j.spi.HandlerBase
    public void handleOption(String rawName, String rawValue) {
        String name = rawName.charAt(0) == '\"' ? RegEscapeTool.getInstance().unquote(rawName) : rawName;
        TypeValuesPair tv = RegEscapeTool.getInstance().decode(rawValue);
        if (tv.getType() != Registry.Type.REG_SZ) {
            ((Registry.Key) getCurrentSection()).putType(name, tv.getType());
        }
        String[] arr$ = tv.getValues();
        for (String value : arr$) {
            super.handleOption(name, value);
        }
    }

    @Override // org.ini4j.spi.AbstractProfileBuilder
    Config getConfig() {
        return this._reg.getConfig();
    }

    @Override // org.ini4j.spi.AbstractProfileBuilder
    Profile getProfile() {
        return this._reg;
    }

    private static RegBuilder newInstance() {
        return (RegBuilder) ServiceFinder.findService(RegBuilder.class);
    }
}
