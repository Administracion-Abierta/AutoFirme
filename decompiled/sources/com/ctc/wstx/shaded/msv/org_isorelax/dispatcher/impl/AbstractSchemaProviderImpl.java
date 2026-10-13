package com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.impl;

import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.IslandSchema;
import com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.SchemaProvider;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/* JADX INFO: loaded from: autofirma.jar:com/ctc/wstx/shaded/msv/org_isorelax/dispatcher/impl/AbstractSchemaProviderImpl.class */
public abstract class AbstractSchemaProviderImpl implements SchemaProvider {
    protected final Map schemata = new HashMap();

    public void addSchema(String s, IslandSchema islandschema) {
        if (this.schemata.containsKey(s)) {
            throw new IllegalArgumentException();
        }
        this.schemata.put(s, islandschema);
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.SchemaProvider
    public IslandSchema getSchemaByNamespace(String s) {
        return (IslandSchema) this.schemata.get(s);
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.SchemaProvider
    public Iterator iterateNamespace() {
        return this.schemata.keySet().iterator();
    }

    @Override // com.ctc.wstx.shaded.msv.org_isorelax.dispatcher.SchemaProvider
    public IslandSchema[] getSchemata() {
        IslandSchema[] aislandschema = new IslandSchema[this.schemata.size()];
        this.schemata.values().toArray(aislandschema);
        return aislandschema;
    }
}
