package com.sun.java.help.search;

import java.net.URL;
import java.util.Hashtable;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/DBPartParameters.class */
public class DBPartParameters {
    protected Schema _schema;
    private String _partName;
    private Hashtable _parameters;

    protected DBPartParameters() {
    }

    public DBPartParameters(Schema schema, String str) {
        this._schema = schema;
        this._partName = str;
        this._parameters = schema.parameters(str);
    }

    protected boolean parametersKnown() {
        return this._parameters != null;
    }

    protected void updateSchema(String str) {
        this._schema.update(this._partName, str);
    }

    public int integerParameter(String str) {
        return Integer.parseInt((String) this._parameters.get(str));
    }

    public URL getURL() throws Exception {
        return this._schema.getURL(this._partName);
    }
}
