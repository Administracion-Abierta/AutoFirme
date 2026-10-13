package com.sun.java.help.search;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;

/* JADX INFO: loaded from: autofirma.jar:com/sun/java/help/search/BtreeDictParameters.class */
class BtreeDictParameters extends BlockManagerParameters {
    private int id1;
    private String dirName;
    private static boolean debugFlag = false;

    public BtreeDictParameters(URL url, int i, int i2, int i3) {
        super(url, i, i2);
        this.id1 = i3;
    }

    public BtreeDictParameters(Schema schema, String str) throws Exception {
        super(schema, str);
    }

    @Override // com.sun.java.help.search.BlockManagerParameters
    public boolean readState() {
        if (super.readState()) {
            setFreeID(integerParameter("id1"));
            return true;
        }
        return false;
    }

    public void writeState() {
    }

    public int getFreeID() {
        return this.id1;
    }

    public final void setFreeID(int i) {
        this.id1 = i;
    }

    private void setDirName(String str) {
        this.dirName = str;
    }

    public static BtreeDictParameters create(URL url) {
        try {
            new URL(url, "TMAP");
            BtreeDictParameters btreeDictParameters = null;
            btreeDictParameters.setDirName(url.getFile());
            return null;
        } catch (MalformedURLException e) {
            System.out.println(new StringBuffer().append("Couldn't create ").append(url).append(File.separator).append("TMAP").toString());
            return null;
        }
    }

    public static BtreeDictParameters read(String str, URL url) throws Exception {
        URL url2;
        String line;
        URL url3 = null;
        if (url == null) {
            if (new File(str).exists()) {
                if (File.separatorChar != '/') {
                    str = str.replace(File.separatorChar, '/');
                }
                if (str.lastIndexOf(File.separatorChar) != str.length() - 1) {
                    str = str.concat(File.separator);
                }
                debug(new StringBuffer().append("file:").append(str).toString());
                url3 = new URL("file", "", str);
            } else {
                url3 = new URL(str);
            }
        }
        if (url != null) {
            url2 = new URL(url, new StringBuffer().append(str).append("/SCHEMA").toString());
        } else {
            url2 = new URL(url3, "SCHEMA");
        }
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url2.openConnection().getInputStream()));
        do {
            line = bufferedReader.readLine();
        } while (!line.startsWith("TMAP"));
        bufferedReader.close();
        StringTokenizer stringTokenizer = new StringTokenizer(line, " =");
        stringTokenizer.nextToken();
        while (stringTokenizer.hasMoreTokens()) {
            String strNextToken = stringTokenizer.nextToken();
            if (strNextToken.equals("bs")) {
                Integer.parseInt(stringTokenizer.nextToken());
            } else if (strNextToken.equals("rt")) {
                Integer.parseInt(stringTokenizer.nextToken());
            } else if (strNextToken.equals("id1")) {
                Integer.parseInt(stringTokenizer.nextToken());
            }
        }
        if (url != null) {
            new URL(url, new StringBuffer().append(str).append("/TMAP").toString());
        } else {
            new URL(url3, "TMAP");
        }
        BtreeDictParameters btreeDictParameters = null;
        if (url == null) {
            btreeDictParameters.setDirName(Utilities.URLDecoder(url3.getFile()));
        }
        return null;
    }

    public void updateSchema() {
        super.updateSchema(new StringBuffer().append("id1=").append(this.id1).append(" id2=1").toString());
    }

    public void write() throws IOException {
        FileWriter fileWriter = new FileWriter(new StringBuffer().append(this.dirName).append("/SCHEMA").toString());
        fileWriter.write("JavaSearch 1.0\n");
        fileWriter.write(new StringBuffer().append("TMAP bs=2048 rt=").append(this.root).append(" fl=-1 id1=").append(this.id1).append(" id2=1\n").toString());
        fileWriter.close();
    }

    private static void debug(String str) {
        if (debugFlag) {
            System.out.println(new StringBuffer().append("BtreeDictParamters: ").append(str).toString());
        }
    }
}
