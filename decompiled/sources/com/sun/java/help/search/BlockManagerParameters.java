package com.sun.java.help.search;

import java.net.URL;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/BlockManagerParameters.class */
class BlockManagerParameters extends DBPartParameters {
    private URL url;
    private int blockSize;
    protected int root;
    private boolean debug;

    public BlockManagerParameters(Schema schema, String str) throws Exception {
        super(schema, str);
        this.debug = false;
        this.url = schema.getURL(str);
        debug(this.url.toString());
    }

    public boolean readState() {
        if (parametersKnown()) {
            this.blockSize = integerParameter("bs");
            this.root = integerParameter("rt");
            return true;
        }
        return false;
    }

    @Override // com.sun.java.help.search.DBPartParameters
    public void updateSchema(String str) {
        super.updateSchema(new StringBuffer().append("bs=").append(this.blockSize).append(" rt=").append(this.root).append(" fl=-1 ").append(str).toString());
    }

    public BlockManagerParameters(URL url, int i, int i2) {
        this.debug = false;
        this.url = url;
        this.blockSize = i;
        this.root = i2;
    }

    @Override // com.sun.java.help.search.DBPartParameters
    public URL getURL() {
        return this.url;
    }

    public int getBlockSize() {
        return this.blockSize;
    }

    public void setBlockSize(int i) {
        this.blockSize = i;
    }

    public int getRootPosition() {
        return this.root;
    }

    public void setRoot(int i) {
        this.root = i;
    }

    private void debug(String str) {
        if (this.debug) {
            System.err.println(new StringBuffer().append("Block Manager Parameters: ").append(str).toString());
        }
    }
}
