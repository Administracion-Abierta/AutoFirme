package com.ctc.wstx.shaded.msv.org_isorelax.dispatcher;

import java.util.Iterator;

/* JADX INFO: loaded from: autofirmaConfigurador.jar:com/ctc/wstx/shaded/msv/org_isorelax/dispatcher/SchemaProvider.class */
public interface SchemaProvider {
    IslandVerifier createTopLevelVerifier();

    IslandSchema getSchemaByNamespace(String str);

    Iterator iterateNamespace();

    IslandSchema[] getSchemata();
}
