package org.ini4j.spi;

import org.ini4j.Config;
import org.ini4j.Ini;
import org.ini4j.Profile;

/* JADX INFO: loaded from: autofirma.jar:org/ini4j/spi/IniBuilder.class */
public class IniBuilder extends AbstractProfileBuilder implements IniHandler {
    private Ini _ini;

    @Override // org.ini4j.spi.AbstractProfileBuilder, org.ini4j.spi.IniHandler
    public /* bridge */ /* synthetic */ void startSection(String x0) {
        super.startSection(x0);
    }

    @Override // org.ini4j.spi.AbstractProfileBuilder, org.ini4j.spi.IniHandler
    public /* bridge */ /* synthetic */ void startIni() {
        super.startIni();
    }

    @Override // org.ini4j.spi.AbstractProfileBuilder, org.ini4j.spi.IniHandler, org.ini4j.spi.HandlerBase
    public /* bridge */ /* synthetic */ void handleOption(String x0, String x1) {
        super.handleOption(x0, x1);
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

    public static IniBuilder newInstance(Ini ini) {
        IniBuilder instance = newInstance();
        instance.setIni(ini);
        return instance;
    }

    public void setIni(Ini value) {
        this._ini = value;
    }

    @Override // org.ini4j.spi.AbstractProfileBuilder
    Config getConfig() {
        return this._ini.getConfig();
    }

    @Override // org.ini4j.spi.AbstractProfileBuilder
    Profile getProfile() {
        return this._ini;
    }

    private static IniBuilder newInstance() {
        return (IniBuilder) ServiceFinder.findService(IniBuilder.class);
    }
}
